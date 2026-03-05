package com.tableorder.customer.order;

import com.tableorder.customer.common.exception.ApiException;
import com.tableorder.customer.order.dto.CreateOrderRequest;
import com.tableorder.customer.order.dto.OrderDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
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

        log.info("[ORDER-API] 주문 생성 요청 | storeId={}, tableId={}, items={}개",
                storeId, tableId, request.items().size());

        OrderDto result = orderService.createOrder(storeId, tableId, sessionId, request);

        log.info("[ORDER-API] 주문 생성 완료 | orderId={}, totalAmount={}, items={}개",
                result.id(), result.totalAmount(), result.items().size());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/tables/{tableId}/orders")
    public ResponseEntity<List<OrderDto>> getOrders(
            HttpServletRequest req,
            @PathVariable Long tableId) {
        Long tokenTableId = (Long) req.getAttribute("tableId");
        String sessionId = (String) req.getAttribute("sessionId");

        log.info("[ORDER-API] 주문 내역 조회 요청 | tableId={}", tableId);

        if (!tokenTableId.equals(tableId)) {
            log.warn("[ORDER-API] 접근 권한 없음 | 요청 tableId={}, 토큰 tableId={}", tableId, tokenTableId);
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다");
        }

        List<OrderDto> result = orderService.getOrdersBySession(tableId, sessionId);
        log.info("[ORDER-API] 주문 내역 조회 완료 | tableId={}, 주문={}건", tableId, result.size());
        return ResponseEntity.ok(result);
    }
}
