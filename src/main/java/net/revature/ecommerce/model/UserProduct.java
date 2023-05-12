package net.revature.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long productId;
    private String product;
    private String price;
    private Integer quantity;

    public UserProduct(EcommerceProduct product) {
        this.productId = product.getId();
        this.product = product.getProduct();
        this.price = product.getPrice();
    }
}
