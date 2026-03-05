package com.tableorder.customer.menu;

import com.tableorder.customer.menu.dto.CategoryWithMenusDto;
import com.tableorder.customer.menu.dto.MenuDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/stores/{storeId}/menus")
    public ResponseEntity<List<CategoryWithMenusDto>> getMenus(@PathVariable Long storeId) {
        return ResponseEntity.ok(menuService.getMenusByStore(storeId));
    }

    @GetMapping("/menus/{menuId}")
    public ResponseEntity<MenuDto> getMenuDetail(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.getMenuDetail(menuId));
    }
}
