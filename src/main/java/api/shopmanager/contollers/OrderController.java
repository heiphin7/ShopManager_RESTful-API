package api.shopmanager.contollers;

import api.shopmanager.dto.OrderRequest;
import api.shopmanager.entity.Order;
import api.shopmanager.mapper.OrderMapper;
import api.shopmanager.service.OrderService;
import api.shopmanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @PostMapping("/save/order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest order) {
        try {
            if (order == null || order.getProducts() == null || order.getProducts().isEmpty()) {
                return new ResponseEntity<>("Список продуктов должен быть заполнен!", HttpStatus.BAD_REQUEST);
            }

            Order orderToSave = orderMapper.orderRequestMapper(order);
            orderService.save(orderToSave);

            return ResponseEntity.ok("Ваш заказ успешно сохранён!");

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Произошла ошибка при сохранении заказа: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

