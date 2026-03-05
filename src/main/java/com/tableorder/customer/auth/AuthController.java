package com.tableorder.customer.auth;

import com.tableorder.customer.auth.dto.LoginRequest;
import com.tableorder.customer.auth.dto.RefreshRequest;
import com.tableorder.customer.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/table/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[AUTH-API] 로그인 요청 | storeId={}, tableNumber={}", request.storeId(), request.tableNumber());
        TokenResponse response = authService.login(request);
        log.info("[AUTH-API] 로그인 성공 | storeId={}, tableId={}, sessionId={}",
                response.storeId(), response.tableId(), response.sessionId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        log.info("[AUTH-API] 토큰 갱신 요청");
        TokenResponse response = authService.refresh(request.refreshToken());
        log.info("[AUTH-API] 토큰 갱신 성공 | tableId={}, sessionId={}", response.tableId(), response.sessionId());
        return ResponseEntity.ok(response);
    }
}
