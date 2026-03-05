package com.tableorder.customer.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer subtotal;

    public Long getId() { return id; }
    public Long getMenuId() { return menuId; }
    public String getMenuName() { return menuName; }
    public Integer getQuantity() { return quantity; }
    public Integer getUnitPrice() { return unitPrice; }
    public Integer getSubtotal() { return subtotal; }

    public void setOrder(Order order) { this.order = order; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }
    public void setSubtotal(Integer subtotal) { this.subtotal = subtotal; }
}
