package net.revature.ecommerce.dao;

import net.revature.ecommerce.model.EcommerceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<EcommerceProduct, Long> {
    EcommerceProduct findByProduct(String product);
}
