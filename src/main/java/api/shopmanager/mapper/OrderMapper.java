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
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductService productService;

    public Order orderRequestMapper(OrderRequest orderRequest) {
        Order order = new Order();

        /*
        * Вручную создаём такие переменные как:
        * price - общяя стоимость, которая подсчитывается когда мы перебираем каждый продукт в списке
        * productList - список продуктов. Мы ее создали так как до этого у нас был список id продуктов
        * и тут мы получаем продукт по id и используя сервис его сохраняем
        */

        double price = 0;
        List<Product> productList = new ArrayList<>();

        try {
            for (Long id : orderRequest.getProducts()) {
                Product product = productService.findById(id)
                        .orElseThrow(() -> new NotFoundException("Продукт не найден"));

                // Добавления текущей стоимости к общей

                price += product.getPrice();
                productList.add(product);
            }

            // Доп.проверка для того, чтобы не было пустых списков у заказов

            if (productList.isEmpty()) {
                throw new IllegalArgumentException("Список продуктов пуст или некорректен!");
            }

        } catch (NotFoundException e) {
            throw new IllegalArgumentException("Продукт из списка продуктов не найден!");
        }
        // Устанавливаем нужные значения для создания объекта Order , включая дату создания

        order.setCreated_at(new Date());
        order.setStatus(orderRequest.getStatus());
        order.setProducts(productList);
        order.setTotalPrice(price);

        return order;
    }
}
