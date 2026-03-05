package com.tableorder.customer.menu;

import com.tableorder.customer.common.entity.Category;
import com.tableorder.customer.common.entity.Menu;
import com.tableorder.customer.common.exception.ApiException;
import com.tableorder.customer.menu.dto.CategoryWithMenusDto;
import com.tableorder.customer.menu.dto.MenuDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final MenuRepository menuRepo;
    private final CategoryRepository categoryRepo;

    public MenuService(MenuRepository menuRepo, CategoryRepository categoryRepo) {
        this.menuRepo = menuRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<CategoryWithMenusDto> getMenusByStore(Long storeId) {
        log.debug("[MENU] 매장 메뉴 조회 | storeId={}", storeId);

        List<Category> categories = categoryRepo.findByStoreIdOrderByDisplayOrder(storeId);
        List<Menu> menus = menuRepo.findActiveByStoreId(storeId);

        log.debug("[MENU] 조회 결과 | storeId={}, 카테고리={}개, 메뉴={}개", storeId, categories.size(), menus.size());

        // 카테고리별 메뉴 그룹핑
        Map<Long, List<MenuDto>> menusByCategory = menus.stream()
                .flatMap(m -> m.getCategories().stream().map(c -> Map.entry(c.getId(), toDto(m))))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        return categories.stream()
                .map(c -> new CategoryWithMenusDto(
                        c.getId(), c.getName(), c.getDisplayOrder(),
                        menusByCategory.getOrDefault(c.getId(), List.of())))
                .toList();
    }

    public MenuDto getMenuDetail(Long menuId) {
        log.debug("[MENU] 메뉴 상세 조회 | menuId={}", menuId);

        Menu menu = menuRepo.findActiveById(menuId)
                .orElseThrow(() -> {
                    log.warn("[MENU] 메뉴 없음 | menuId={}", menuId);
                    return new ApiException(HttpStatus.NOT_FOUND, "MENU_NOT_FOUND", "메뉴를 찾을 수 없습니다");
                });

        log.debug("[MENU] 메뉴 상세 조회 완료 | menuId={}, name={}, price={}", menuId, menu.getName(), menu.getPrice());
        return toDto(menu);
    }

    private MenuDto toDto(Menu m) {
        return new MenuDto(m.getId(), m.getName(), m.getDescription(), m.getPrice(),
                m.getImageUrl(), m.getIsSoldOut(), m.getDisplayOrder(),
                m.getCategories().stream()
                        .map(c -> new MenuDto.CategoryRef(c.getId(), c.getName()))
                        .toList());
    }
}
