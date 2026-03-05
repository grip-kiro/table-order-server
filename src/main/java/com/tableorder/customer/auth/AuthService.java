package com.tableorder.customer.auth;

import com.tableorder.customer.auth.dto.LoginRequest;
import com.tableorder.customer.auth.dto.TokenResponse;
import com.tableorder.customer.common.entity.RefreshToken;
import com.tableorder.customer.common.entity.RestaurantTable;
import com.tableorder.customer.common.entity.Store;
import com.tableorder.customer.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

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
        Store store = storeRepo.findById(req.storeId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND", "매장을 찾을 수 없습니다"));

        RestaurantTable table = tableRepo.findByStoreIdAndTableNumber(req.storeId(), req.tableNumber())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "TABLE_NOT_FOUND", "테이블을 찾을 수 없습니다"));

        if (!table.getPin().equals(req.pin())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_PIN", "인증에 실패했습니다");
        }

        // 기기 제한: 기존 토큰 무효화
        refreshTokenRepo.revokeAllByTableId(table.getId());

        // 새 세션 생성
        String sessionId = UUID.randomUUID().toString();
        table.setCurrentSessionId(sessionId);
        table.setStatus("OCCUPIED");
        table.setUpdatedAt(LocalDateTime.now());
        tableRepo.save(table);

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

        return new TokenResponse(accessToken, refreshTokenStr,
                jwtProvider.getAccessTokenExpiry() / 1000,
                store.getId(), table.getId(), table.getTableNumber(), sessionId);
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenStr) {
        RefreshToken rt = refreshTokenRepo.findByToken(refreshTokenStr)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다"));

        if (rt.getIsRevoked()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "TOKEN_REVOKED", "무효화된 토큰입니다");
        }
        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "만료된 토큰입니다");
        }

        RestaurantTable table = tableRepo.findById(rt.getTableId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "세션이 만료되었습니다"));

        if (table.getCurrentSessionId() == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "세션이 만료되었습니다");
        }

        String accessToken = jwtProvider.createAccessToken(
                rt.getStoreId(), table.getId(), table.getTableNumber(),
                table.getCurrentSessionId(), "TABLE");

        return new TokenResponse(accessToken, refreshTokenStr,
                jwtProvider.getAccessTokenExpiry() / 1000,
                rt.getStoreId(), table.getId(), table.getTableNumber(),
                table.getCurrentSessionId());
    }
}
