package net.revature.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EcommerceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String product;
    private String price;
    private String imageLink;

    public EcommerceProduct(EcommerceProduct product) {
        this.id = product.getId();
        this.product = product.getProduct();
        this.price = product.getPrice();
    }
}
