package net.revature.ecommerce.service;

import net.revature.ecommerce.dao.EcommerceDAOInterface;
import net.revature.ecommerce.dao.ProductRepo;
import net.revature.ecommerce.exceptions.EcommerceExceptionAdvice;
import net.revature.ecommerce.exceptions.InvalidInputException;
import net.revature.ecommerce.exceptions.UserNotFoundException;
import net.revature.ecommerce.model.EcommerceProduct;
import net.revature.ecommerce.model.EcommerceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class EcommerceService {
    @Autowired
    EcommerceDAOInterface userRepo;
    @Autowired
    ProductRepo productRepo;

    // Actions for User
    public List<EcommerceUser> getAllUsers() {
        return userRepo.findAll();
    }
    public EcommerceUser login(EcommerceUser userInfo) throws UserNotFoundException, InvalidInputException{
        EcommerceUser user = userRepo.findByUsername(userInfo.getUsername());
        if (user == null) {
            throw new UserNotFoundException("No Account exists with that username");
        } else if (!user.getPassword().equals(userInfo.getPassword())) {
            throw new InvalidInputException("Incorrect password for given username");
        } else {
            return user;
        }
    }
    public EcommerceUser postUser(EcommerceUser userInfo) throws InvalidInputException {
        if (userInfo.getUsername() == null || userInfo.getPassword() == null)
            throw new InvalidInputException("Must fill out username and password");
        if (userRepo.findByUsername(userInfo.getUsername()) != null) {
            throw new InvalidInputException("Username already taken");
        }
        return userRepo.save(userInfo);
    }
    public EcommerceUser getUser(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    // Actions for Products
    public List<EcommerceProduct> getAllProducts() {
        return productRepo.findAll();
    }
    public EcommerceProduct postProduct(EcommerceProduct product) throws InvalidInputException {
        if (product.getProduct() == null || product.getPrice() == null)
            throw new InvalidInputException("Your product is missing a name or price");
        return productRepo.save(product);
    }
    public EcommerceProduct getProduct(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    // Actions that use both
    public EcommerceUser addToCart(long userId, long productId) throws InvalidInputException {
        try {
            EcommerceUser user = userRepo.findById(userId).orElse(null);
            EcommerceProduct product = productRepo.findById(productId).orElse(null);
            if (user == null || product == null) throw new Exception();
            List<EcommerceProduct> products = user.getProducts();
            products.add(product);
            return userRepo.save(user);
        } catch(Exception e) {
            throw new InvalidInputException("Your product or user does not exist");
        }
    }

}
