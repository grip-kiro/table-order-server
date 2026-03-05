package com.tableorder.customer.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "table_id")
    private Long tableId;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public Long getTableId() { return tableId; }
    public Long getStoreId() { return storeId; }
    public String getRole() { return role; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public Boolean getIsRevoked() { return isRevoked; }

    public void setToken(String token) { this.token = token; }
    public void setTableId(Long tableId) { this.tableId = tableId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public void setRole(String role) { this.role = role; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setIsRevoked(Boolean isRevoked) { this.isRevoked = isRevoked; }
}
