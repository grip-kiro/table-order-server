package com.tableorder.customer.order;

import com.tableorder.customer.auth.TableRepository;
import com.tableorder.customer.common.entity.Menu;
import com.tableorder.customer.common.entity.Order;
import com.tableorder.customer.common.entity.OrderItem;
import com.tableorder.customer.common.entity.RestaurantTable;
import com.tableorder.customer.common.exception.ApiException;
import com.tableorder.customer.menu.MenuRepository;
import com.tableorder.customer.order.dto.CreateOrderRequest;
import com.tableorder.customer.order.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepo;
    private final MenuRepository menuRepo;
    private final TableRepository tableRepo;

    public OrderService(OrderRepository orderRepo, MenuRepository menuRepo, TableRepository tableRepo) {
        this.orderRepo = orderRepo;
        this.menuRepo = menuRepo;
        this.tableRepo = tableRepo;
    }

    @Transactional
    public OrderDto createOrder(Long storeId, Long tableId, String sessionId, CreateOrderRequest req) {
        log.debug("[ORDER] 주문 생성 시작 | storeId={}, tableId={}, sessionId={}", storeId, tableId, sessionId);

        // 세션 유효성 확인
        RestaurantTable table = tableRepo.findById(tableId)
                .orElseThrow(() -> {
                    log.warn("[ORDER] 테이블 없음 | tableId={}", tableId);
                    return new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "세션이 만료되었습니다");
                });
        if (!sessionId.equals(table.getCurrentSessionId())) {
            log.warn("[ORDER] 세션 불일치 | tableId={}, 요청={}, 현재={}", tableId, sessionId, table.getCurrentSessionId());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "세션이 만료되었습니다");
        }

        // 메뉴 검증 + 품절 체크
        List<Long> soldOutIds = new ArrayList<>();
        List<Menu> menus = new ArrayList<>();
        for (var item : req.items()) {
            Menu menu = menuRepo.findActiveById(item.menuId())
                    .orElseThrow(() -> {
                        log.warn("[ORDER] 메뉴 없음 | menuId={}", item.menuId());
                        return new ApiException(HttpStatus.NOT_FOUND, "MENU_NOT_FOUND", "메뉴를 찾을 수 없습니다");
                    });
            if (menu.getIsSoldOut()) {
                log.warn("[ORDER] 품절 메뉴 포함 | menuId={}, name={}", menu.getId(), menu.getName());
                soldOutIds.add(menu.getId());
            }
            menus.add(menu);
        }

        if (!soldOutIds.isEmpty()) {
            log.warn("[ORDER] 주문 거부 - 품절 메뉴 | tableId={}, soldOutIds={}", tableId, soldOutIds);
            throw new ApiException(HttpStatus.BAD_REQUEST, "MENU_SOLD_OUT",
                    "품절된 메뉴가 포함되어 있습니다", Map.of("soldOutMenuIds", soldOutIds));
        }

        // 주문 생성
        Order order = new Order();
        order.setStoreId(storeId);
        order.setTableId(tableId);
        order.setSessionId(sessionId);

        int totalAmount = 0;
        for (int i = 0; i < req.items().size(); i++) {
            var itemReq = req.items().get(i);
            Menu menu = menus.get(i);

            OrderItem oi = new OrderItem();
            oi.setMenuId(menu.getId());
            oi.setMenuName(menu.getName());
            oi.setQuantity(itemReq.quantity());
            oi.setUnitPrice(menu.getPrice());
            oi.setSubtotal(menu.getPrice() * itemReq.quantity());
            order.addItem(oi);
            totalAmount += oi.getSubtotal();

            log.debug("[ORDER] 주문 항목 | menuId={}, name={}, qty={}, unitPrice={}, subtotal={}",
                    menu.getId(), menu.getName(), itemReq.quantity(), menu.getPrice(), oi.getSubtotal());
        }
        order.setTotalAmount(totalAmount);

        order = orderRepo.save(order);
        log.info("[ORDER] 주문 저장 완료 | orderId={}, tableId={}, totalAmount={}", order.getId(), tableId, totalAmount);
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersBySession(Long tableId, String sessionId) {
        log.debug("[ORDER] 세션 주문 조회 | tableId={}, sessionId={}", tableId, sessionId);
        List<OrderDto> orders = orderRepo.findByTableIdAndSessionId(tableId, sessionId)
                .stream().map(this::toDto).toList();
        log.debug("[ORDER] 세션 주문 조회 완료 | tableId={}, 주문={}건", tableId, orders.size());
        return orders;
    }

    private OrderDto toDto(Order o) {
        return new OrderDto(o.getId(), o.getTotalAmount(), o.getStatus(), o.getCreatedAt(),
                o.getItems().stream().map(i -> new OrderDto.OrderItemDto(
                        i.getId(), i.getMenuId(), i.getMenuName(),
                        i.getQuantity(), i.getUnitPrice(), i.getSubtotal()
                )).toList());
    }
}
