package api.shopmanager.contollers;

import api.shopmanager.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {
    public ResponseEntity<?> saveProduct(Product product) {

    }
}
