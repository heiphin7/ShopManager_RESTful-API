package api.shopmanager.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@Table(name = "Completed_Orders")
public class CompletedOrder {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "price")
    private Double totalPrice;

    @Column(name = "status")
    private String status;

    @OneToMany
    @JoinTable(
            name = "completed_order_products",
            joinColumns = @JoinColumn(name = "completed_order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Collection<Product> products;

    @Column(name = "created_at")
    private Date created_at;
}
