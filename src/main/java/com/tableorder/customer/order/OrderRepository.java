package com.tableorder.customer.order;

import com.tableorder.customer.common.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items " +
           "WHERE o.tableId = :tableId AND o.sessionId = :sessionId AND o.isDeleted = false " +
           "ORDER BY o.createdAt ASC")
    List<Order> findByTableIdAndSessionId(Long tableId, String sessionId);
}
