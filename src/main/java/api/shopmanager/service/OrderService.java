package api.shopmanager.service;

import api.shopmanager.entity.Order;
import api.shopmanager.repository.OrderRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void save(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findOrdersByProductId(Long id){
        /*
        * Получение списка заказов, где содержится нужный продукт
        * В первом списке содержатся их id, а во втором, используя цикл мы находим каждый из заказов
        */

        List<Long> orders = orderRepository.findAllByProductsIs(id);
        List<Order> ordersList = new ArrayList<>();

        for(Long orderId: orders){
            Order order = orderRepository.findById(orderId).orElse(null);
        }

        return ordersList;
    }
}
