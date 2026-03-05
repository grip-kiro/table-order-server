package com.tableorder.customer.menu.dto;

import java.util.List;

public record CategoryWithMenusDto(
        Long id, String name, Integer displayOrder, List<MenuDto> menus
) {}
