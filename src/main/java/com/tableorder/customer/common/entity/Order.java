package com.tableorder.customer.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "table_id", nullable = false)
    private Long tableId;

    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getStoreId() { return storeId; }
    public Long getTableId() { return tableId; }
    public String getSessionId() { return sessionId; }
    public Integer getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Boolean getIsDeleted() { return isDeleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }

    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void addItem(OrderItem item) { items.add(item); item.setOrder(this); }
}
