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
import org.apache.catalina.filters.CorsFilter;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
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
	void GetAllUser() throws Exception {
		this.createEmployee("Bobby","password");
		mvc.perform(MockMvcRequestBuilders
			.get("/user")
			.accept(MediaType.APPLICATION_JSON))
//			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].username").value("Bobby"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].password").value("password"));
	}
	@Test
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
	void registerProduct() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/product")
				.content(asJsonString(new EcommerceProduct((long)1, "Shirt","12.99", "")))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.product").value("Shirt"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.price").value("12.99"));



	}
	@Test
	void addToCart() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/cart/1")
				.content(asJsonString(new EcommerceProduct((long) 1, "", "","")))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
//				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.products").isNotEmpty());
	}

	@Test
	void contextLoads() {
	}
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
	public void serviceCreateEmployeeAndCheck() {
		this.createEmployee("Bob", "password");
		EcommerceUser user = serviceTest.getUser((long) 2);
		assertNotEquals(user.getUsername(), "Bobby");
		assertEquals(user.getUsername(), "Bob");
	}
	@Test
	public void serviceCreateProductAndCheck() {
		this.createProduct("Soap","12.99");
		EcommerceProduct product = serviceTest.getProduct((long) 1);
		assertEquals(product.getProduct(), "Soap");
		assertEquals(product.getPrice(), "12.99");
	}
	@Test
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
		e.add(asd);
		System.out.println(serviceTest.getUserProducts());
		System.out.println(serviceTest.getAllProducts());
		assertEquals(new EcommerceUser((long)1,"bob", "password", e), serviceTest.removeSingleFromCart(1, 1));
		assertEquals( new EcommerceUser((long)1,"bob", "password", new ArrayList<>()), serviceTest.removeFromCart(1, 1));
		System.out.println(serviceTest.getAllUsers());
		System.out.println(serviceTest.getUserProducts());
		System.out.println(serviceTest.getAllProducts());
	}
	@Test
	public void purchase() throws UserNotFoundException, InvalidInputException {
		EcommerceUser user = new EcommerceUser((long)1,"Boo", "password", new ArrayList<>());
		EcommerceProduct product = new EcommerceProduct((long)1, "Banana", "15.00", "");
		serviceTest.postUser(user);
		serviceTest.postProduct(product);
		serviceTest.addToCart(user.getId(), product.getId());
		serviceTest.addToCart(user.getId(), product.getId());
		assertEquals(new EcommerceUser((long)1,"Boo", "password", new ArrayList<>()), serviceTest.purchase(user.getId()));
	}
//	@Test
//	public void deleteProduct() {
//		serviceTest.deleteProduct(1);
//		assertEquals(serviceTest.getUser((long) 1), null);
//	}
}
