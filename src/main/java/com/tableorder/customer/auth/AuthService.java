package com.tableorder.customer.auth;

import com.tableorder.customer.auth.dto.LoginRequest;
import com.tableorder.customer.auth.dto.TokenResponse;
import com.tableorder.customer.common.entity.RefreshToken;
import com.tableorder.customer.common.entity.RestaurantTable;
import com.tableorder.customer.common.entity.Store;
import com.tableorder.customer.common.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final StoreRepository storeRepo;
    private final TableRepository tableRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtProvider jwtProvider;

    public AuthService(StoreRepository storeRepo, TableRepository tableRepo,
                       RefreshTokenRepository refreshTokenRepo, JwtProvider jwtProvider) {
        this.storeRepo = storeRepo;
        this.tableRepo = tableRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        log.debug("[AUTH] 로그인 처리 시작 | storeId={}, tableNumber={}", req.storeId(), req.tableNumber());

        Store store = storeRepo.findById(req.storeId())
                .orElseThrow(() -> {
                    log.warn("[AUTH] 매장 없음 | storeId={}", req.storeId());
                    return new ApiException(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND", "매장을 찾을 수 없습니다");
                });

        RestaurantTable table = tableRepo.findByStoreIdAndTableNumber(req.storeId(), req.tableNumber())
                .orElseThrow(() -> {
                    log.warn("[AUTH] 테이블 없음 | storeId={}, tableNumber={}", req.storeId(), req.tableNumber());
                    return new ApiException(HttpStatus.NOT_FOUND, "TABLE_NOT_FOUND", "테이블을 찾을 수 없습니다");
                });

        if (!table.getPin().equals(req.pin())) {
            log.warn("[AUTH] PIN 불일치 | tableId={}", table.getId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_PIN", "인증에 실패했습니다");
        }

        // 기기 제한: 기존 토큰 무효화
        refreshTokenRepo.revokeAllByTableId(table.getId());
        log.debug("[AUTH] 기존 토큰 무효화 완료 | tableId={}", table.getId());

        // 새 세션 생성
        String sessionId = UUID.randomUUID().toString();
        table.setCurrentSessionId(sessionId);
        table.setStatus("OCCUPIED");
        table.setUpdatedAt(LocalDateTime.now());
        tableRepo.save(table);
        log.debug("[AUTH] 새 세션 생성 | tableId={}, sessionId={}", table.getId(), sessionId);

        // JWT 발급
        String accessToken = jwtProvider.createAccessToken(
                store.getId(), table.getId(), table.getTableNumber(), sessionId, "TABLE");
        String refreshTokenStr = jwtProvider.createRefreshToken();

        // Refresh Token DB 저장
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshTokenStr);
        rt.setTableId(table.getId());
        rt.setStoreId(store.getId());
        rt.setRole("TABLE");
        rt.setExpiresAt(LocalDateTime.now().plusSeconds(jwtProvider.getRefreshTokenExpiry() / 1000));
        refreshTokenRepo.save(rt);
        log.debug("[AUTH] Refresh Token 저장 완료 | tableId={}", table.getId());

        return new TokenResponse(accessToken, refreshTokenStr,
                jwtProvider.getAccessTokenExpiry() / 1000,
                store.getId(), table.getId(), table.getTableNumber(), sessionId);
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenStr) {
        log.debug("[AUTH] 토큰 갱신 처리 시작");

        RefreshToken rt = refreshTokenRepo.findByToken(refreshTokenStr)
                .orElseThrow(() -> {
                    log.warn("[AUTH] Refresh Token 없음");
                    return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다");
                });

        if (rt.getIsRevoked()) {
            log.warn("[AUTH] 무효화된 Refresh Token | tableId={}", rt.getTableId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "TOKEN_REVOKED", "무효화된 토큰입니다");
        }
        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("[AUTH] 만료된 Refresh Token | tableId={}", rt.getTableId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "만료된 토큰입니다");
        }

        RestaurantTable table = tableRepo.findById(rt.getTableId())
                .orElseThrow(() -> {
                    log.warn("[AUTH] 테이블 없음 (토큰 갱신) | tableId={}", rt.getTableId());
                    return new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "세션이 만료되었습니다");
                });

        if (table.getCurrentSessionId() == null) {
            log.warn("[AUTH] 세션 만료 (이용 완료) | tableId={}", table.getId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "세션이 만료되었습니다");
        }

        String accessToken = jwtProvider.createAccessToken(
                rt.getStoreId(), table.getId(), table.getTableNumber(),
                table.getCurrentSessionId(), "TABLE");

        log.debug("[AUTH] Access Token 갱신 완료 | tableId={}, sessionId={}", table.getId(), table.getCurrentSessionId());

        return new TokenResponse(accessToken, refreshTokenStr,
                jwtProvider.getAccessTokenExpiry() / 1000,
                rt.getStoreId(), table.getId(), table.getTableNumber(),
                table.getCurrentSessionId());
    }
}
