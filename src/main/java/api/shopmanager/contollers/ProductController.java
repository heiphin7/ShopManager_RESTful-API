package api.shopmanager.contollers;

import api.shopmanager.entity.Product;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {
    public ResponseEntity<?> saveProduct(Product product) {

        // Дополнительный метод для проверки полей на пустоту

        if(checkAllFields(product)){
            return new ResponseEntity<>("Все поля должны быть заполнены!", HttpStatus.BAD_REQUEST);
        }

        // Проверка на цену продукта

        if(product.getPrice() <= 0){
            return new ResponseEntity<>("Цена продукта не может быть отрицательным или равным нулю!", HttpStatus.BAD_REQUEST);
        }

        // Проверка на название продукта, оно должно быть от 4 до 15 символов

        if(product.getName().length() < 4 || product.getName().length() > 15) {
            return new ResponseEntity<>("Название продукта должно быть от 4 до 15 символов!", HttpStatus.BAD_REQUEST);
        }

        if(product.getDescription().length() < 10 ) {
            return new ResponseEntity<>("Опишите ваш продукт более подробнее", HttpStatus.BAD_REQUEST);
        }

        if(product.getDescription().length() > 100) {
            return new ResponseEntity<>("Слишком длинное описание", HttpStatus.BAD_REQUEST);
        }


        return ResponseEntity.ok("Ваш продукт успешно зарегестрирован!");
    }

    /*
    * Проверка полей на "пустоту"
    */
    private boolean checkAllFields(Product product) {
        return
                product.getDescription().isBlank() ||
                         product.getName().isBlank();
    }
}
