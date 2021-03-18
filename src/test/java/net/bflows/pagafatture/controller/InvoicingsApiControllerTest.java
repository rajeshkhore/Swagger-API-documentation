package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.InvoicingConnectionEntity;
import net.bflows.pagafatture.entities.InvoicingEntity;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.model.InvoicingConnectionReq;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.repositories.InvoicingRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvoicingsApiControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private MerchantRepository merchantRepository;
	
	@Autowired
	private InvoicingRepository invoicingRepository;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Test
	@Order(1)
	void testCreateInvocingConnection_Fail_By_Credential() throws Exception {

		InvoicingEntity invoicingEntity = invoicingRepository.findById(1l).get();
		InvoicingConnectionReq invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("d0f99891fa6d8ec7606fd3ed09f2c0862123");
		invoicingConnection.setApiUid("88864");
		invoicingConnection.setTypeId(invoicingEntity.getId());
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		String URI = "/v1/invoicings/" + merchant.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message", is("Invalid credentials.")));
	}

	@Test
	@Order(2)
	void testCreateInvocingConnection_Fail_By_invoicingId() throws Exception {

		InvoicingConnectionReq invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("d0f99891fa6d8ec7606fd3ed09f2c086");
		invoicingConnection.setApiUid("88864");
		invoicingConnection.setTypeId(0l);
		MerchantEntity merchantEntity = merchantRepository.findById(2l).get();

		String URI = "/v1/invoicings/" + merchantEntity.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	   
	@Test
	@Order(3)
	void  testCreateInvocingConnection_Success() throws Exception {
		InvoicingEntity invoicingEntity = invoicingRepository.findById(1l).get();
		InvoicingConnectionReq invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("d0f99891fa6d8ec7606fd3ed09f2c086");
		invoicingConnection.setApiUid("88864");
		invoicingConnection.setTypeId(invoicingEntity.getId());
		MerchantEntity merchantEntity = merchantRepository.findById(2l).get();

		String URI = "/v1/invoicings/" + merchantEntity.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.apiKey", is(invoicingConnection.getApiKey())));
		
		URI = "/v1/invoicings/" + merchantEntity.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
				

	}
		
	
	@Test
	@Order(4)
	void testGetInvoicing() throws Exception {

		String URI = "/v1/invoicings";

		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

	}
	
	@Test
	@Order(5)
	void testGetInvoicingConnectionByMerchantId_Sucess() throws Exception {
		InvoicingConnectionReq invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("d0f99891fa6d8ec7606fd3ed09f2c086");
		invoicingConnection.setApiUid("88864");
		invoicingConnection.setTypeId(1l);
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test InvoicingConnection");
		user.setConfPassword("test12345678");
		user.setEmail("testInvoicingConnection@test.com");
		user.setFirstName("test");
		user.setLastName("test lastname");
		user.setPassword("test12345678");
		user.setPhone("2424856");

		String URI = "/v1/users";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		UserEntity userEntity = userRepository.findByEmailIgnoreCase(user.getEmail()).get();

		
		URI = "/v1/invoicings/" + userEntity.getMerchant().getId() + "/connections";
		ResultActions resultActions = mvc.perform(post(URI).content(new ObjectMapper().writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isCreated());
		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		InvoicingConnectionEntity response = mapper.readValue(json.get("data").toString(), InvoicingConnectionEntity.class);
		
		URI = "/v1/invoicings/" + response.getMerchantEntity().getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
	}
	
	@Test
	@Order(6)
	void testGetInvoicingConnectionByMerchantId_Fail() throws Exception {
		String URI = "/v1/invoicings/" + 0 + "/connections";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("InvoicingConnection Merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		URI = "/v1/invoicings/" + merchant.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(7)
	void testCreateInvocingConnection_Fail_By_MerchantId() throws Exception {

		InvoicingEntity invoicingEntity = invoicingRepository.findById(1l).get();
		InvoicingConnectionReq invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("d0f99891fa6d8ec7606fd3ed09f2c086");
		invoicingConnection.setApiUid("88864");
		invoicingConnection.setTypeId(invoicingEntity.getId());
	

		String URI = "/v1/invoicings/" + 0 + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	

	@Test
	@Order(8)
	void  testCreateInvocingConnection_FailByTypeId() throws Exception {
		InvoicingConnectionReq invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("d0f99891fa6d8ec7606fd3ed09f2c086");
		invoicingConnection.setApiUid("88864");
		MerchantEntity merchantEntity = merchantRepository.findById(2l).get();

		String URI = "/v1/invoicings/" + merchantEntity.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}
	

	@Test
	@Order(9)
	void testGetInvoicingConnection() throws Exception {

		String URI = "/v1/invoicings/connections";

		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
		
		URI = "/v1/invoicings/connections?type=1";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

	}
	
	
/*	@Test
	@Order(10)
	void testCreateInvocingConnection_WithPasspartout() throws Exception {
		InvoicingEntity invoicingEntity = invoicingRepository.findById(2l).get();
		InvoicingConnectionReq invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("137918001");
		invoicingConnection.setApiUid("STU");
		invoicingConnection.setTypeId(invoicingEntity.getId());
		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity.setEmail("testpasspartout@test.test");
		merchantEntity = merchantRepository.save(merchantEntity);

		String URI = "/v1/invoicings/" + merchantEntity.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		invoicingConnection = new InvoicingConnectionReq();
		invoicingConnection.setApiKey("1379185000");
		invoicingConnection.setApiUid("STU");
		invoicingConnection.setTypeId(invoicingEntity.getId());
		URI = "/v1/invoicings/" + merchantEntity.getId() + "/connections";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoicingConnection))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.apiKey", is(invoicingConnection.getApiKey())));

	}*/

}
