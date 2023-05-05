package net.revature.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.revature.ecommerce.controller.EcommerceController;
import net.revature.ecommerce.dao.EcommerceDAOInterface;
import net.revature.ecommerce.dao.ProductRepo;
import net.revature.ecommerce.model.EcommerceProduct;
import net.revature.ecommerce.model.EcommerceUser;
import net.revature.ecommerce.service.EcommerceService;
import org.apache.catalina.filters.CorsFilter;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class EcommerceApplicationTests {
	@Autowired
	private MockMvc mvc;
	@Mock
	EcommerceService service;
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
	@Test
	void GetAllUser() throws Exception {
		this.createEmployee("Bobby","password");
		mvc.perform(MockMvcRequestBuilders
			.get("/user")
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].username").value("Bobby"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[2].password").value("password"));
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
	void registerUser() throws Exception {
		mvc.perform(MockMvcRequestBuilders
				.post("/user")
				.content(asJsonString(new EcommerceUser((long) 1,"bob", "password", new ArrayList<>())))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("bob"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password"));;

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
	public void createEmployeeAndCheck() {
		this.createEmployee("Bob", "password");
		EcommerceUser user = serviceTest.getUser((long) 2);
		assertNotEquals(user.getUsername(), "Bobby");
		assertEquals(user.getUsername(), "Bob");
	}
	@Test
	public void createProductAndCheck() {
		this.createProduct("Soap","12.99");
		EcommerceProduct product = serviceTest.getProduct((long) 1);
		assertEquals(product.getProduct(), "Soap");
		assertEquals(product.getPrice(), "12.99");
	}
//	@Test
//	public void test_get_all_success() throws Exception {
//		ArrayList temp = new ArrayList();
//		List<EcommerceUser> users = Arrays.asList(
//				new EcommerceUser((long) 1, "Daenerys Targaryen", "asd", temp),
//				new EcommerceUser((long) 2, "John Snow", "asd", temp));
//		when(service.getAllUsers()).thenReturn(users)	@Test
////	public void test_get_all_success() throws Exception {
////		ArrayList temp = new ArrayList();
////		List<EcommerceUser> users = Arrays.asList(
////				new EcommerceUser((long) 1, "Daenerys Targaryen", "asd", temp),
////				new EcommerceUser((long) 2, "John Snow", "asd", temp));
////		when(service.getAllUsers()).thenReturn(users);
////		mvc.perform(get("/users"))
////				.andExpect(status().isOk())
////				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
////				.andExpect(jsonPath("$", hasSize(2)))
////				.andExpect(jsonPath("$[0].id", is(1)))
////				.andExpect(jsonPath("$[0].username", is("Daenerys Targaryen")))
////				.andExpect(jsonPath("$[1].id", is(2)))
////				.andExpect(jsonPath("$[1].username", is("John Snow")));
////		verify(service, times(1)).getAll();
////		verifyNoMoreInteractions(service);
////	};
//		mvc.perform(get("/users"))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//				.andExpect(jsonPath("$", hasSize(2)))
//				.andExpect(jsonPath("$[0].id", is(1)))
//				.andExpect(jsonPath("$[0].username", is("Daenerys Targaryen")))
//				.andExpect(jsonPath("$[1].id", is(2)))
//				.andExpect(jsonPath("$[1].username", is("John Snow")));
//		verify(service, times(1)).getAll();
//		verifyNoMoreInteractions(service);
//	}


}
