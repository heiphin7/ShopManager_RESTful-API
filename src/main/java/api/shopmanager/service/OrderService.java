package api.shopmanager.service;

import api.shopmanager.entity.Order;
import api.shopmanager.entity.Product;
import api.shopmanager.repository.OrderRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void save(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findOrdersByProductId(Product product) {
        return orderRepository.findByProducts_Id(product.getId());
    }



    public Product getOrderProductsById(Long id, Order order){
        Product product = new Product();

        for (Product productInOrder: order.getProducts()){
            if(productInOrder.getId() == id){
                product = productInOrder;
                break;
            }
        }

        return product;
    }
}
