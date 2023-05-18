package net.revature.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.revature.ecommerce.controller.EcommerceController;
import net.revature.ecommerce.dao.EcommerceDAOInterface;
import net.revature.ecommerce.dao.ProductRepo;
import net.revature.ecommerce.exceptions.InvalidInputException;
import net.revature.ecommerce.exceptions.UserNotFoundException;
import net.revature.ecommerce.model.EcommerceProduct;
import net.revature.ecommerce.model.EcommerceUser;
import net.revature.ecommerce.model.UserProduct;
import net.revature.ecommerce.service.EcommerceService;
import org.apache.catalina.User;
import org.apache.catalina.filters.CorsFilter;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EcommerceApplicationTests {
	@Autowired
	private MockMvc mvc;
	@InjectMocks
	EcommerceController controller;

	@Autowired
	private EcommerceDAOInterface userRepo;
	@Autowired
	private ProductRepo productRepo;


	@Autowired
	private EcommerceService serviceTest;
	public void init(){
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders
				.standaloneSetup(controller)
				.addFilters(new CorsFilter())
				.build();
	}

	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Test
	@Order(2)
	void GetAllUser() throws Exception {
		this.createEmployee("Bobby","password");
		mvc.perform(MockMvcRequestBuilders
			.get("/user")
			.accept(MediaType.APPLICATION_JSON))
//			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("bob"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].password").value("password"));
	}
	@Test
	@Order(1)
	void registerUser() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/user")
				.content(asJsonString(new EcommerceUser((long) 1,"bob", "password", new ArrayList<>())))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("bob"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password"));
	}
	@Test
	@Order(3)
	void registerProduct() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/product")
				.content(asJsonString(new EcommerceProduct((long)1, "Shirt","12.99", "", "")))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.product").value("Shirt"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.price").value("12.99"));



	}
	@Test
	@Order(4)
	void addToCart() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/cart/1")
				.content(asJsonString(new EcommerceProduct((long) 1, "", "","", "")))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.products").isNotEmpty());
	}

//	@Test
//	void contextLoads() {
//	}
	void createEmployee(String username, String password) {
		EcommerceUser user = new EcommerceUser();
		user.setPassword(password);
		user.setUsername(username);
		userRepo.save(user);
	}
	void createProduct(String name, String price) {
		EcommerceProduct product = new EcommerceProduct();
		product.setProduct(name);
		product.setPrice(price);
		productRepo.save(product);
	}
	@Test
	@Order(5)
	public void serviceCreateEmployeeAndCheck() {
		this.createEmployee("Bob", "password");
		EcommerceUser user = serviceTest.getUser((long) 2);
		assertEquals(user.getUsername(), "Bobby");
	}
	@Test
	@Order(6)
	public void serviceCreateProductAndCheck() {
		this.createProduct("Soap","12.99");
		EcommerceProduct product = serviceTest.getProduct((long) 1);
		assertEquals(product.getProduct(), "Shirt");
		assertEquals(product.getPrice(), "12.99");
	}
	@Test
	@Order(7)
	public void removeFromCart() throws InvalidInputException {
		EcommerceUser user = new EcommerceUser((long)1,"Boss", "password", new ArrayList<>());
		EcommerceProduct product = new EcommerceProduct();
		product.setPrice("12.99");
		product.setProduct("Shirt");
		serviceTest.addToCart(1, 1);
		serviceTest.addToCart(1, 1);
		serviceTest.addToCart(1, 1);
		ArrayList<UserProduct> e = new ArrayList();
		UserProduct asd = new UserProduct(product);
		asd.setId(1);
		asd.setProductId(1);
		asd.setQuantity(3);
		asd.setDescription("");
		asd.setImageLink("");
		e.add(asd);
		assertEquals(new EcommerceUser((long)1,"bob", "password", e), serviceTest.removeSingleFromCart(1, 1));
		assertEquals( new EcommerceUser((long)1,"bob", "password", new ArrayList<>()), serviceTest.removeFromCart(1, 1));
		assertEquals(serviceTest.getUserProducts(), new ArrayList<>());
	}
	@Test
	@Order(8)
	public void purchase() throws UserNotFoundException, InvalidInputException {
		EcommerceUser user = new EcommerceUser((long)1,"Boo", "password", new ArrayList<>());
		EcommerceProduct product = new EcommerceProduct((long)1, "Banana", "15.00", "", "");
		serviceTest.postUser(user);
		serviceTest.postProduct(product);
		serviceTest.addToCart(user.getId(), product.getId());
		serviceTest.addToCart(user.getId(), product.getId());
		assertEquals(new EcommerceUser((long)1,"Boo", "password", new ArrayList<>()), serviceTest.purchase(user.getId()));
	}
	@Test
	@Order(9)
	public void deleteProduct() throws InvalidInputException {
		System.out.println(serviceTest.getUserProducts());
		serviceTest.addToCart(1, 1);
		serviceTest.addToCart(2, 1);
		serviceTest.addToCart(3, 1);
		serviceTest.addToCart(3, 2);
		serviceTest.deleteProduct(1);
		assertEquals(serviceTest.getProduct((long) 1), null);
		ArrayList<UserProduct> e = new ArrayList<>();
		e.add(new UserProduct(6, 2, "Soap", "12.99", null, 1, null));
		assertEquals(serviceTest.getUserProducts(), e);
	}
	@Test
	@Order(10)
	public void whatIsLeft() {
		System.out.println(serviceTest.getAllUsers());
		System.out.println(serviceTest.getUserProducts());
		System.out.println(serviceTest.getAllProducts());
	}
}
