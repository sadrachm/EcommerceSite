package net.revature.ecommerce.controller;

import net.revature.ecommerce.exceptions.EcommerceExceptionAdvice;
import net.revature.ecommerce.exceptions.InvalidInputException;
import net.revature.ecommerce.exceptions.UserNotFoundException;
import net.revature.ecommerce.model.EcommerceProduct;
import net.revature.ecommerce.model.EcommerceUser;
import net.revature.ecommerce.service.EcommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class EcommerceController {
    @Autowired
    EcommerceService ecommerceService;

    public EcommerceController(EcommerceService service) {
        this.ecommerceService = service;
    }

    @PostMapping("/user")
    EcommerceUser postUser(@RequestBody EcommerceUser userInfo) throws InvalidInputException {
        return ecommerceService.postUser(userInfo);
    }
    @GetMapping("/user")
    List<EcommerceUser> getAllUsers() {
        return ecommerceService.getAllUsers();
    }
    @PostMapping("/login")
    EcommerceUser login(@RequestBody EcommerceUser userInfo) throws UserNotFoundException, InvalidInputException {
        return ecommerceService.login(userInfo);
    }
    @GetMapping("/user/{userId}")
    EcommerceUser getUser(@PathVariable long userId) {
        return ecommerceService.getUser(userId);
    }

    @PostMapping("/product")
    EcommerceProduct postProduct(@RequestBody EcommerceProduct product) throws InvalidInputException {
        return ecommerceService.postProduct(product);
    }
    @GetMapping("/products")
    List<EcommerceProduct> getAllProducts() {
        return ecommerceService.getAllProducts();
    }
    @PostMapping("/cart/{userId}")
    EcommerceUser addToCart(@PathVariable long userId, @RequestBody EcommerceProduct product ) throws InvalidInputException{
        return ecommerceService.addToCart(userId, product.getId());
    }


}
