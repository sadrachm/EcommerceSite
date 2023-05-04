package net.revature.ecommerce.dao;

import net.revature.ecommerce.model.EcommerceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcommerceDAOInterface extends JpaRepository<EcommerceUser, Long> {
}
