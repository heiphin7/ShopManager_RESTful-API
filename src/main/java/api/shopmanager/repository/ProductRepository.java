package api.shopmanager.repository;

import api.shopmanager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    Optional<Product> findByName(String name);

    void deleteById(Long id);

    List<Product> findAllByIdIn(List<Long> ids);
}
