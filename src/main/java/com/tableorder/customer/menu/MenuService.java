package com.tableorder.customer.menu;

import com.tableorder.customer.common.entity.Category;
import com.tableorder.customer.common.entity.Menu;
import com.tableorder.customer.common.exception.ApiException;
import com.tableorder.customer.menu.dto.CategoryWithMenusDto;
import com.tableorder.customer.menu.dto.MenuDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepo;
    private final CategoryRepository categoryRepo;

    public MenuService(MenuRepository menuRepo, CategoryRepository categoryRepo) {
        this.menuRepo = menuRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<CategoryWithMenusDto> getMenusByStore(Long storeId) {
        List<Category> categories = categoryRepo.findByStoreIdOrderByDisplayOrder(storeId);
        List<Menu> menus = menuRepo.findActiveByStoreId(storeId);

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
        Menu menu = menuRepo.findActiveById(menuId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MENU_NOT_FOUND", "메뉴를 찾을 수 없습니다"));
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
