package com.tableorder.customer.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id, Integer totalAmount, String status,
        LocalDateTime createdAt, List<OrderItemDto> items
) {
    public record OrderItemDto(
            Long id, Long menuId, String menuName,
            Integer quantity, Integer unitPrice, Integer subtotal
    ) {}
}
