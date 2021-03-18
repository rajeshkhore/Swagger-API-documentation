package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.MerchantReq;
import net.bflows.pagafatture.repositories.MerchantRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class MerchantsApiControllerTest {

	Integer id_merchant;

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MerchantRepository merchantRepository;
	
    
	
    @Test
    @Order(1)
    void addMerchant_Success() throws Exception {
       
       MerchantReq merchant = new MerchantReq();
       merchant.setName("Merchant1");
       merchant.setVatNumber("12345678901");
       merchant.setDirectUrl("Http:www.www.it");
       merchant.setAddressCity("Cagliari");
       merchant.setAddressProvince("Sardinia");
       merchant.setAddressCAP("09100");
       merchant .setAddressState("Italy");
       merchant.setAddressStreet("Via PPP");
       merchant.setEmail("test@gmail.com");
       merchant.setPhone("+312457778");
       merchant.setDefaultPaymentDays(60);
       mvc.perform(MockMvcRequestBuilders.post("/v1/merchants").content(new ObjectMapper().writeValueAsString(merchant)).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber());
    }
   
   

    @Test
    @Order(2)
    void updateMerchant_Success() throws Exception {
       
    	MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant1");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
       
       merchant.setName("Updated Name");
       
       mvc.perform(MockMvcRequestBuilders.put("/v1/merchants/"+merchant.getId()).content(new ObjectMapper().writeValueAsString(merchant)).contentType(MediaType.APPLICATION_JSON))
    		   .andExpect(status().isOk()).andExpect(jsonPath("$.data.name", is(merchant.getName())));
       
    }
   
    @Test
    @Order(3)
    void updateMerchant_Fail() throws Exception {
       
       MerchantReq merchant = new MerchantReq();
       merchant.setId(2L);
       merchant.setName("Merchant1 updated");
       merchant.setVatNumber("12345678901");
       merchant.setDirectUrl("Http:www.www.it");
       merchant.setAddressCity("Cagliari");
       merchant.setAddressState("Sardinia");
       merchant.setAddressCAP("09100");
       merchant.setAddressStreet("Via PPP");
       merchant.setEmail("email.email.it");
       
       mvc.perform(MockMvcRequestBuilders.put("/v1/merchants/0").content(new ObjectMapper().writeValueAsString(merchant)).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
       
    }
   
    @Test
    @Order(4)
    void getMerchant_Success() throws Exception {
       
    	MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant1");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
       
       
       mvc.perform(MockMvcRequestBuilders.get("/v1/merchants/"+merchant.getId()).accept(MediaType.APPLICATION_JSON)
       .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QudGVzdCIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNjAzOTgzNzI0fQ.MiJsH782PjBjkYQXGrdr42MIlcu5K61xnxptebtcXnPAPSGVIZwub-v4_uiGH6D_6ZwPTdKNxLUhy7RJtUpAUQ_superKey$2a$10$ZTBwN.ZHXsr/iysflbS6ZecxKhFCg3cEEDGMrjqLk2lw3dZ9wSl4C"))
               .andExpect(status().isOk());
    }
   
    @Test
    @Order(5)
    void getMerchant_Fail() throws Exception {
       mvc.perform(MockMvcRequestBuilders.get("/v1/merchants/0").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
   
    @Test
    @Order(6)
    void deleteMerchant_Success() throws Exception {
       
    	MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant1");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
       
       
       mvc.perform(MockMvcRequestBuilders.delete("/v1/merchants/"+merchant.getId()).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }
    
    @Test
    @Order(7)
    void deleteMerchant_Fail() throws Exception {
       
       mvc.perform(MockMvcRequestBuilders.delete("/v1/merchants/"+0).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
    
    
    @Test
    @Order(8)
    void addMerchant_SuccessWithDefaultDays() throws Exception {
       
       MerchantReq merchant = new MerchantReq();
       merchant.setName("Merchant1");
       merchant.setVatNumber("12345678901");
       merchant.setDirectUrl("Http:www.www.it");
       merchant.setAddressCity("Cagliari");
       merchant.setAddressProvince("Sardinia");
       merchant.setAddressCAP("09100");
       merchant .setAddressState("Italy");
       merchant.setAddressStreet("Via PPP");
       merchant.setEmail("test@gmail.com");
       merchant.setPhone("+312457778");
       mvc.perform(MockMvcRequestBuilders.post("/v1/merchants").content(new ObjectMapper().writeValueAsString(merchant)).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber());
    }
   
    

    @Test
    @Order(9)
    void addMerchant_FaildByVatNumber() throws Exception {
       
       MerchantReq merchant = new MerchantReq();
       merchant.setName("Merchant1");
       merchant.setDirectUrl("Http:www.www.it");
       merchant.setAddressCity("Cagliari");
       merchant.setAddressProvince("Sardinia");
       merchant.setAddressCAP("09100");
       merchant .setAddressState("Italy");
       merchant.setAddressStreet("Via PPP");
       merchant.setEmail("test@gmail.com");
       merchant.setPhone("+312457778");
       merchant.setDefaultPaymentDays(60);
       mvc.perform(MockMvcRequestBuilders.post("/v1/merchants").content(new ObjectMapper().writeValueAsString(merchant)).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
              
    }
    
    
}
