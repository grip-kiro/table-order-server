package com.tableorder.customer.menu;

import com.tableorder.customer.common.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT DISTINCT m FROM Menu m LEFT JOIN FETCH m.categories c " +
           "WHERE m.storeId = :storeId AND m.isDeleted = false " +
           "ORDER BY m.displayOrder")
    List<Menu> findActiveByStoreId(Long storeId);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.categories WHERE m.id = :id AND m.isDeleted = false")
    Optional<Menu> findActiveById(Long id);
}
