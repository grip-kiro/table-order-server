package com.tableorder.customer.auth;

import com.tableorder.customer.common.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByStoreIdAndTableNumber(Long storeId, Integer tableNumber);
}
