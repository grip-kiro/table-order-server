package com.tableorder.customer.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Column(nullable = false, length = 10)
    private String pin;

    @Column(name = "current_session_id", length = 36)
    private String currentSessionId;

    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public Long getStoreId() { return storeId; }
    public Integer getTableNumber() { return tableNumber; }
    public String getPin() { return pin; }
    public String getCurrentSessionId() { return currentSessionId; }
    public String getStatus() { return status; }

    public void setCurrentSessionId(String sessionId) { this.currentSessionId = sessionId; }
    public void setStatus(String status) { this.status = status; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
