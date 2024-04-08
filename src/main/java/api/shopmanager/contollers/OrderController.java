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

    /*
    * Для создания продукта нам необходим сам объект класса Order
    * а также список продуктов, которые будут храниться в order_products
    * оно будет храниться следующим образом: order_id : product_id
    */
    @PostMapping("/save/order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest order) {
        try{
            if(order == null || order.getProducts().isEmpty()){
                return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
            }

            // Тут все просто, используя маппер преобразуем orderRequest к Order

            Order orderToSave = orderMapper.orderRequestMapper(order);
            orderService.save(orderToSave);

            return ResponseEntity.ok("Ваш заказ успешно сохранён!");
        }catch (NullPointerException e){
            return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая-то ошибка:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
