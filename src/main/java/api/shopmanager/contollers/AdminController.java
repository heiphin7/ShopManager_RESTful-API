package api.shopmanager.contollers;

import api.shopmanager.entity.CompletedOrder;
import api.shopmanager.entity.Order;
import api.shopmanager.mapper.OrderMapper;
import api.shopmanager.repository.CompletedOrderRepository;
import api.shopmanager.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final CompletedOrderRepository completedOrderRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    /*
     Метод для заверешния заказ
     Здесь, перейдя по пути /api/complete/order/id, вы можете завершить заказ
     поместив его в "архив", то есть в таблицу Completed_Orders
    */
    @PutMapping("/complete/order/{id}")
    public ResponseEntity<?> completeOrder(@PathVariable Long id){

        try {
            Order order = orderRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Заказ для завершения не найден!")
            );

            CompletedOrder completedOrder = orderMapper.completedOrder(order);
            completedOrderRepository.save(completedOrder);

            orderRepository.delete(order);

            return ResponseEntity.ok("Заказ успешно помечен как выполненный и помечен в архив");
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Выбранный продукт не найден!", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
