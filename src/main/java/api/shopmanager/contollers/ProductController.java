package api.shopmanager.contollers;

import api.shopmanager.entity.Product;
import api.shopmanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/save/product")
    public ResponseEntity<?> createProduct(@RequestBody Product product) {

        try{
            /*
            * Пользуясь сервисом продуктов, ищем продукт на названию
            * если такого нету, приравняем его null, а этот null будет проверен при следующей проверке
            */

            Product product_copy = productService.findByName(product.getName())
                    .orElse(null);

            if(product_copy != null){
                return new ResponseEntity<>("Такой продукт уже существует!", HttpStatus.BAD_REQUEST);
            }

            // Дополнительный метод для проверки полей на пустоту

            if (checkAllFields(product)) {
                return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
            }

            // Проверка на цену продукта

            if (product.getPrice() <= 0) {
                return new ResponseEntity<>("Цена продукта не может быть отрицательным или равным нулю!", HttpStatus.BAD_REQUEST);
            }

            // Проверка на название продукта, оно должно быть от 4 до 15 символов

            if (product.getName().length() < 4 || product.getName().length() > 15) {
                return new ResponseEntity<>("Название продукта должно быть от 4 до 15 символов!", HttpStatus.BAD_REQUEST);
            }

            if (product.getDescription().length() < 10) {
                return new ResponseEntity<>("Опишите ваш продукт более подробнее", HttpStatus.BAD_REQUEST);
            }

            if (product.getDescription().length() > 100) {
                return new ResponseEntity<>("Слишком длинное описание", HttpStatus.BAD_REQUEST);
            }

            productService.save(product);

            return ResponseEntity.ok("Ваш продукт успешно зарегестрирован!");

        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла ошибка при сохранении продукта: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findAll/products")
    public Object getAllProducts() {
        /*
        * Данный метод возвращает Object так как мы можем вернуть как и List, так и ResponseEntity
        * Даже при такой простой операции у нас может возникнуть ошибка, поэтму
        * делаем примитивную конструкцию try - catch
        */

        try{
            List<Product> products = productService.findAllProduct();

            return products;
        }catch (Exception e){
            return new ResponseEntity<>("Произошла какая-то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update/product/{id}")
    public ResponseEntity<?> updateProductById(@PathVariable Long id, @RequestBody Product updatedProduct) {

        // Используем try - catch для корректной обработки ошибок

        try {
            Product productToUpdate = productService.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Product not found!"));

            // Создаём копию, которую будем сохранять

            // Копирование полей из updatedProduct в productToUpdate
            productToUpdate.setName(updatedProduct.getName());
            productToUpdate.setPrice(updatedProduct.getPrice());
            productToUpdate.setDescription(updatedProduct.getDescription());

            if (checkAllFields(productToUpdate)) {
                return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
            }

            if (productToUpdate.getPrice() <= 0) {
                return new ResponseEntity<>("Цена продукта не может быть отрицательным или равным нулю!", HttpStatus.BAD_REQUEST);
            }

            if (productToUpdate.getName().length() < 4 || productToUpdate.getName().length() > 15) {
                return new ResponseEntity<>("Название продукта должно быть от 4 до 15 символов!", HttpStatus.BAD_REQUEST);
            }

            if (productToUpdate.getDescription().length() < 10) {
                return new ResponseEntity<>("Опишите ваш продукт более подробнее", HttpStatus.BAD_REQUEST);
            }

            if (productToUpdate.getDescription().length() > 100) {
                return new ResponseEntity<>("Слишком длинное описание", HttpStatus.BAD_REQUEST);
            }

            productService.save(productToUpdate);

            return ResponseEntity.ok("Ваш продукт успешно обновлен!");

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("Продукт для изменения не найден!", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/product/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id){

        // Здесь также добавляем try-catch так как выбранного блога может и не быть

        try {

            /*
            * Здесь можно и не находить Product по id, так как при вызове deleteById все это делается за нас
            * и если delete(id) не найден продукт, то выкинет ошибку, которую мы перехватим
            */

            productService.deleteById(id);

            return ResponseEntity.ok("Вы успешно удалили продукт с id = " + id);
        }

        catch (EmptyResultDataAccessException e){
            return new ResponseEntity<>("Выбранный продукт не найден!", HttpStatus.NOT_FOUND);
        }

        catch (Exception e) {
            return new ResponseEntity<>("Произошла какая - то ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/product/{id}")
    public Object getProductById(@PathVariable Long id){

        try{
            Product product = productService.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Выбранный продукт не найден"));

            return product;
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>("Выбранный продукт не найден", HttpStatus.NOT_FOUND);
        }

    }


    /*
    * Проверка полей на "пустоту"
    */

    private boolean checkAllFields(Product product) {
        return
                product.getDescription() == null ||
                        product.getName() == null ||
                product.getDescription().isBlank() ||
                         product.getName().isBlank();
    }
}
