package api.shopmanager.service;

import api.shopmanager.entity.Product;
import api.shopmanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void save(Product product){
        productRepository.save(product);
    }
}
