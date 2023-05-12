package net.revature.ecommerce.dao;

import net.revature.ecommerce.model.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<UserProduct, Long> {
}
