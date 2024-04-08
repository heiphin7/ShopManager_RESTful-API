package api.shopmanager.service;

import api.shopmanager.entity.Product;
import api.shopmanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void deleteById(Long id){
        productRepository.deleteById(id);
    }

    public Optional<Product> findByName(String name){
        return productRepository.findByName(name);
    }

    public void save(Product product){
        productRepository.save(product);
    }

    public List<Product> findAllProduct() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
