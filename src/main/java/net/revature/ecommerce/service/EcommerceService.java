package net.revature.ecommerce.service;

import net.revature.ecommerce.dao.CartRepo;
import net.revature.ecommerce.dao.EcommerceDAOInterface;
import net.revature.ecommerce.dao.ProductRepo;
import net.revature.ecommerce.exceptions.EcommerceExceptionAdvice;
import net.revature.ecommerce.exceptions.InvalidInputException;
import net.revature.ecommerce.exceptions.UserNotFoundException;
import net.revature.ecommerce.model.EcommerceProduct;
import net.revature.ecommerce.model.EcommerceUser;
import net.revature.ecommerce.model.UserProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EcommerceService {
    @Autowired
    EcommerceDAOInterface userRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CartRepo cartRepo;

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
        if (productRepo.findByProduct(product.getProduct()) != null) {
            throw new InvalidInputException("Product name already taken");
        }
        return productRepo.save(product);
    }
    public EcommerceProduct getProduct(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    // Actions that use both
    public EcommerceUser addToCart(long userId, long productId) throws InvalidInputException {
        EcommerceUser user = userRepo.findById(userId).orElse(null);
        EcommerceProduct product = productRepo.findById(productId).orElse(null);
        if (user == null || product == null)
            throw new InvalidInputException("Your product or user does not exist");
        List<UserProduct> products = user.getProducts();
        for (UserProduct prod : products) {
            if (prod.getProduct().equals(product.getProduct())) {
                prod.setQuantity(prod.getQuantity()+1);
                cartRepo.save(prod);
                return userRepo.save(user);
            }
        }
        UserProduct userProduct = new UserProduct(product);
        userProduct.setQuantity(1);
        cartRepo.save(userProduct);
        products.add(userProduct);
        return userRepo.save(user);
    }
    public EcommerceUser removeFromCart(long userId, long productId) throws InvalidInputException{
        EcommerceUser user = userRepo.findById(userId).orElse(null);
        EcommerceProduct product = productRepo.findById(productId).orElse(null);
        if (user==null || product==null) {
            throw new InvalidInputException("Invalid Input or Product");
        }
        ArrayList<Long> ids = new ArrayList<>();
        user.setProducts(user.getProducts().stream().filter(n -> {
            if (n.getProductId()==productId)
                ids.add(n.getId());
            return n.getProductId()!=productId;
        }).collect(Collectors.toList()));
        userRepo.save(user);
        for (long el : ids) {
            cartRepo.deleteById(el);
        }
        return user;
    }
    public EcommerceUser removeSingleFromCart(long userId, long productId) throws InvalidInputException {
        EcommerceUser user = userRepo.findById(userId).orElse(null);
        if (user == null)
            throw new InvalidInputException("Invalid User");
        user.setProducts(user.getProducts().stream().map(n -> {
            if (n.getId() == productId) {
                if (n.getQuantity() > 0)
                    n.setQuantity(n.getQuantity()-1);
            }
            cartRepo.save(n);
            return n;
        }).toList());
        return user;
    }
    public void deleteProduct(long id) throws InvalidInputException {
        EcommerceProduct prod = productRepo.findById(id).orElse(null);
        List<EcommerceUser> users = userRepo.findAll();
        users.forEach(el -> {
            try {
                this.removeFromCart(el.getId(), id);
            } catch (InvalidInputException e) {
            }
        });
        productRepo.deleteById(id);
    }
    public EcommerceUser purchase(long userId) throws UserNotFoundException {
        EcommerceUser user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User not Found");
        }
        List<UserProduct> tempVals = user.getProducts();
        user.setProducts(new ArrayList<>());
        userRepo.save(user);
        tempVals.stream().forEach(n -> {
            cartRepo.deleteById(n.getId());
        });
        return user;
    }
    public List<UserProduct> getUserProducts() {
        return cartRepo.findAll();
    }

}
