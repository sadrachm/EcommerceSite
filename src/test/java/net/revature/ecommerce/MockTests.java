package net.revature.ecommerce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.revature.ecommerce.controller.EcommerceController;
import net.revature.ecommerce.exceptions.InvalidInputException;
import net.revature.ecommerce.model.EcommerceProduct;
import net.revature.ecommerce.model.EcommerceUser;
import net.revature.ecommerce.service.EcommerceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = EcommerceController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class MockTests {
    @Autowired
    MockMvc mvc;
    @MockBean
    EcommerceService service;
    @Autowired
    ObjectMapper objectMapper;
    private EcommerceUser user;
    private EcommerceProduct product;
    @BeforeEach
    public void init() {
        user = new EcommerceUser((long)1,"Bob", "password", new ArrayList<>());
        product = new EcommerceProduct((long)1, "Brocolli", "15.00", "");
    }

    @Test
    public void CreateUser_ReturnUser() throws Exception {
        given(service.postUser(user)).willReturn(user);
        given(service.postUser(user)).willReturn(user);
    }
    @Test
    public void CreateProduct_ReturnProduct() throws Exception {
        given(service.postProduct(product)).willReturn(product);
    }
    @Test
    public  void AddToCart() throws InvalidInputException {
        given(service.addToCart((long)1, (long) 1)).willThrow(InvalidInputException.class);
    }
    @Test
    public void removeFromCart() throws InvalidInputException {
        service.postUser(user);
        service.postProduct(product);
        System.out.println(service.getUser((long)1));
        service.addToCart(user.getId(), product.getId());
        service.addToCart(user.getId(), product.getId());
        service.addToCart(user.getId(), product.getId());
//        service.removeSingleFromCart(user.getId(), product.getId());
        System.out.println(service.removeFromCart(user.getId(), product.getId()));

    }


}
