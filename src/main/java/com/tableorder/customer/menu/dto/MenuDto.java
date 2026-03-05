package com.tableorder.customer.menu.dto;

import java.util.List;

public record MenuDto(
        Long id, String name, String description, Integer price,
        String imageUrl, Boolean isSoldOut, Integer displayOrder,
        List<CategoryRef> categories
) {
    public record CategoryRef(Long id, String name) {}
}
