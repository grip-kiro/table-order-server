package com.tableorder.customer.menu;

import com.tableorder.customer.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByStoreIdOrderByDisplayOrder(Long storeId);
}
