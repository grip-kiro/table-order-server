package com.tableorder.customer.auth;

import com.tableorder.customer.common.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
