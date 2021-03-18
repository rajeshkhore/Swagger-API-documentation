package net.bflows.pagafatture.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.RoleEntity;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.RoleRepository;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.util.MailUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@TestMethodOrder(OrderAnnotation.class)
class TestJWTToken {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	MailUtil mailUtil;

	@Test
	@Order(1)
	void testGetWorkFlowById_FailByUnauthorized() throws Exception {

		String URI = "/v1/workflow/" + 1;
		mvc.perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());

		mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QudGVzdCIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNjAzOTgzNzI0fQ.MiJsH782PjBjkYQXGrdr42MIlcu5K61xnxptebtcXnPAPSGVIZwub-v4_uiGH6D_6ZwPTdKNxLUhy7RJtUpAUQ"))
				.andExpect(status().isUnauthorized());
		
		mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer eyJhbGciOiJIUzUxMiJ9.ey"))
				.andExpect(status().isUnauthorized());
		
		URI="/v1/actions/clients/2";
		mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QudGVzdCIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNjAzOTgzNzI0fQ.MiJsH782PjBjkYQXGrdr42MIlcu5K61xnxptebtcXnPAPSGVIZwub-v4_uiGH6D_6ZwPTdKNxLUhy7RJtUpAUQ_superKey$2a$10$ZTBwN.ZHXsr/iysflbS6ZecxKhFCg3cEEDGMrjqLk2lw3dZ9wSl4C"))
				.andExpect(status().isOk());

	}
	
	
	@Test
	@Order(2)
	void testGetMerchantWithValidOrInvalidUser() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant user 1");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("validUser1431@gmail.com");
		userEntity.setUserStatus("ACTIVE");
		userEntity.setLastName("test user");
		userEntity.setFirstName("test user");
		userEntity.setPassword("$2a$10$doUyOcEm8WPuFfpFT5y18.1DvZzF7exbqgy9X0P27cUBK7YWbfzzS");
		RoleEntity role = roleRepository.findById(2l).get();
		userEntity.setRole(role);
		userEntity.setMerchant(merchant);
		userEntity = userRepository.save(userEntity);

		String URI = "/v1/users/" + userEntity.getEmail() + "/sessions";

		MvcResult result = mvc.perform(MockMvcRequestBuilders.post(URI).param("password", "Prova123!"))
				.andExpect(status().isOk()).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		Map<String, Object> responseMap = mapper.readValue(json.get("data").toString(), Map.class);
		String userToken = (String) responseMap.get("id_token");

		URI = "/v1/merchants/" + userEntity.getMerchant().getId();
		mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + userToken)).andExpect(status().isOk());

		merchant = new MerchantEntity();
		merchant.setName("Merchant user 32");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		URI = "/v1/merchants/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + userToken)).andExpect(status().isUnauthorized());

		URI = "/v1/merchants/" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + userToken)).andExpect(status().isNotFound());

		merchantRepository.delete(userEntity.getMerchant());
		merchantRepository.delete(merchant);
		userRepository.delete(userEntity);

	}

}
