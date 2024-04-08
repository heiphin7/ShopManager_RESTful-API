package api.shopmanager.mapper;

import api.shopmanager.dto.OrderRequest;
import api.shopmanager.entity.Order;
import api.shopmanager.entity.Product;
import api.shopmanager.service.OrderService;
import api.shopmanager.service.ProductService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductService productService;

    /*
    * Это у нас маппер для Order-а, то есть для преобразования orderRequest (dto) на
    * объект класса Order.
    */

    public Order orderRequestMapper(OrderRequest orderRequest){

        Order order = new Order();

        // В базе данных у нас у каждого заказа есть список продуктов, а в OrderRequest у нас
        // передаётся список id продуктов

        List<Product> productList = new ArrayList<>();

        // Поэтому используя циклы и сервис продуктов мы должны найти и записать
        // Каждый продукт по их id

        try {
            for(Long id: orderRequest.getProducts()){
                productList.add(productService.findById(id).orElseThrow(() -> new NotFoundException("Продукт не найден")));
            }
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("Продукт из списка продуктов не найден!");
        }
        // Далее если у нас все успешно, просто устанавливаем значения через сеттеры

        order.setPrice(orderRequest.getPrice());
        order.setStatus(orderRequest.getStatus());
        order.setProducts(productList);

        return order;
    }
}
