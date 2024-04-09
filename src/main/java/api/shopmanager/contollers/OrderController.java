package api.shopmanager.contollers;

import api.shopmanager.dto.OrderRequest;
import api.shopmanager.entity.Order;
import api.shopmanager.entity.Product;
import api.shopmanager.mapper.OrderMapper;
import api.shopmanager.repository.OrderRepository;
import api.shopmanager.repository.ProductRepository;
import api.shopmanager.service.OrderService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

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

    // Получение списка всех продуктов
    @GetMapping("findAll/orders")
    public Object getAllOrders() {

        try {
            List<Order> ordersList = orderRepository.findAll();

            return ordersList;
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая то ошибка: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * Получение заказа с указанным id
    *
    * Почему данынй метод возвращает Object?
    * Так как при поиске значения мы можем вернуть как и объект класса Order, Так и ResponseEntity(при ошибке)
    * а они все являются типа данных Object
    */

    @GetMapping("/get/order/{id}")
    public Object getOrderById(@PathVariable Long id){

        // Тут все просто, используем JPA репозиторий чтобы найти order о его id

        try {
            Order order = orderRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Заказ с нужным id не найден"));
            return order;
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Заказ с " + id + " id не найден!", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/order/{id}")
    public ResponseEntity<?> updateOrderById(@PathVariable Long id, @RequestBody OrderRequest updatedOrder){
        try{
            // "Оригинальный", то есть старая версия заказа

            Order order =  orderRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Заказ не найден!"));

            // Тут я создаю новый список так как у updatedOrder идет List<Long>, а для сохранения нам нужен List<Product>

            List<Product> products = new ArrayList<>();

            for(Long product_id: updatedOrder.getProducts()){
                // Если мы не находим продукт из списка обновленного заказа, тогда выбрасываем ошибку
                // которую обрабатываем в первом же блоке catch

                Product product = productRepository.findById(product_id).orElseThrow(
                        () -> new NotFoundException("Продукт из обновленного заказа не найден"));

                products.add(product);
            }

            order.setStatus(updatedOrder.getStatus());
            order.setProducts(products);

            if(order.getProducts() == null){
                return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
            }

            /*
            * Проверка, чтобы пользователь не могу указывать любой статус.
            * Тут нет статус "завершён", так как этот статус будет устанавливать администратор
            */

            if(!order.getStatus().equals("Новый") && !order.getStatus().equals("В процессе")){
                return new ResponseEntity<>("Укажите правильный статус заказа! ('Новый' или 'В Процессе')", HttpStatus.BAD_REQUEST);
            }

            // Обновление totalPrice

            double totalPrice = 0;

            // Просто используя ранее использованный список продуктов считаем обновленную общую стоимость заказа

            for(Product product: products){
                totalPrice += product.getPrice();
            }

            order.setTotalPrice(totalPrice);

            orderService.save(order);
            return ResponseEntity.ok("Заказ успешно обновлен!");

        }catch (NotFoundException e){
            return new ResponseEntity<>("Продукт из обновленного заказа не найден!", HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e){
            return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("Выбранный заказ для изменения не найден!", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/order/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable Long id){
        try {
            Order orderToDelete = orderRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("Заказ не найден"));

            orderRepository.delete(orderToDelete);
            return ResponseEntity.ok("Заказ успешно удалён!");
        }catch (IllegalThreadStateException e){
            return new ResponseEntity<>("Заказ с " + id + " id не найден!", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая-то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

