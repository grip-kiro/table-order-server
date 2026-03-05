package com.tableorder.customer.order;

import com.tableorder.customer.common.exception.ApiException;
import com.tableorder.customer.order.dto.CreateOrderRequest;
import com.tableorder.customer.order.dto.OrderDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(
            HttpServletRequest req,
            @Valid @RequestBody CreateOrderRequest request) {
        Long storeId = (Long) req.getAttribute("storeId");
        Long tableId = (Long) req.getAttribute("tableId");
        String sessionId = (String) req.getAttribute("sessionId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(storeId, tableId, sessionId, request));
    }

    @GetMapping("/tables/{tableId}/orders")
    public ResponseEntity<List<OrderDto>> getOrders(
            HttpServletRequest req,
            @PathVariable Long tableId) {
        Long tokenTableId = (Long) req.getAttribute("tableId");
        String sessionId = (String) req.getAttribute("sessionId");
        if (!tokenTableId.equals(tableId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다");
        }
        return ResponseEntity.ok(orderService.getOrdersBySession(tableId, sessionId));
    }
}
