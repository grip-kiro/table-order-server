package com.tableorder.customer.menu;

import com.tableorder.customer.menu.dto.CategoryWithMenusDto;
import com.tableorder.customer.menu.dto.MenuDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/stores/{storeId}/menus")
    public ResponseEntity<List<CategoryWithMenusDto>> getMenus(@PathVariable Long storeId) {
        log.info("[MENU-API] 메뉴 목록 조회 요청 | storeId={}", storeId);
        List<CategoryWithMenusDto> result = menuService.getMenusByStore(storeId);
        log.info("[MENU-API] 메뉴 목록 조회 완료 | storeId={}, 카테고리={}개", storeId, result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/menus/{menuId}")
    public ResponseEntity<MenuDto> getMenuDetail(@PathVariable Long menuId) {
        log.info("[MENU-API] 메뉴 상세 조회 요청 | menuId={}", menuId);
        MenuDto result = menuService.getMenuDetail(menuId);
        log.info("[MENU-API] 메뉴 상세 조회 완료 | menuId={}, name={}", menuId, result.name());
        return ResponseEntity.ok(result);
    }
}
