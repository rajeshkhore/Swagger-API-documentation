package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.ActionEntity;
import net.bflows.pagafatture.entities.ActionTypeEntity;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.TimelineEntity;
import net.bflows.pagafatture.entities.TimelineEntity.TimelineEntityTypeEnum;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.repositories.ActionRepository;
import net.bflows.pagafatture.repositories.ActionTypeRepository;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.TimelineRepository;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.repositories.WorkflowRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Utils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class ClientApiControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private MerchantRepository merchantRepository;
	
	@Autowired
	private InvoicesRepository invoicesRepository;
	
	@Autowired
	private WorkflowRepository workflowRepository;
	
	@Autowired
	private TimelineRepository timelineRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ActionTypeRepository actionTypeRepository;

	@Autowired
	private ActionRepository actionRepository;
	

	@Test
	@Order(1)
	public void testCreateClient() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		ClientReq request = new ClientReq();
		request.setName("test Client");
		request.setVatNumber("12345789");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setDefaultPaymentDays(13);

		String URI = "/v1/merchants/" + merchant.getId() + "/clients";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())));
	}

	@Test
	@Order(2)
	public void testUpdateClient() throws Exception {
	
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant for update client");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setVatNumber("33211");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		
		ClientReq request = new ClientReq();
		request.setVatNumber("3222111556");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("996673382");
		request.setName("Updated Name");
		String URI = "/v1/merchants/" + merchant.getId() + "/clients/" + clientEntity.getId();
		 mvc
				.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data.name", is(request.getName())));

	}

	@Test
	@Order(3)
	public void testgetClientById() throws Exception {
		
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant for update client");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setVatNumber("33211");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
	
		String URI = "/v1/merchants/" + merchant.getId() + "/clients/" + clientEntity.getId();
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		Map<String, Object> responseMap = mapper.readValue(json.get("data").toString(), Map.class);
		assertNotNull(responseMap.get("client"));
		assertNotNull(responseMap.get("invoiceDetail"));
		
	}

	@Test
	@Order(4)
	public void testgetClientsByMerchantId() throws Exception {
		MerchantEntity merchantEntity = merchantRepository.findById(1l).get();
		String URI = "/v1/merchants/" + merchantEntity.getId() + "/clients";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
	}


	@Test
	@Order(5)
	public void testUpdateClient_Fail() throws Exception {
		MerchantEntity merchantEntity = merchantRepository.findById(1l).get();
		ClientReq request = new ClientReq();
		request.setName("test Client 1");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		
		String URI = "/v1/merchants/" + merchantEntity.getId() + "/clients/" + 0;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	

	@Test
	@Order(6)
	public void testDeleteClient_Fail() throws Exception {
		MerchantEntity merchantEntity = merchantRepository.findById(1l).get();
		String URI = "/v1/merchants/" + merchantEntity.getId() + "/clients/" + 0;
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());
	}



	
	   
	@Test
	@Order(7)
	public void testDeleteClient() throws Exception {
		ClientEntity clientEntity = clientRepository.findById(1l).get();
		String URI = "/v1/merchants/" + clientEntity.getMerchantEntity().getId() + "/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isOk());
	}
		
		  
  
    @Test
	@Order(8)
	public void testCreateClient_FailByMerchantId() throws Exception {
    	MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		ClientReq request = new ClientReq();
		request.setName("test Client");
		request.setVatNumber("12345789");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");

		String URI = "/v1/merchants/" + 0 + "/clients";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
    
	@Test
	@Order(9)
	public void testgetClientById_FailByMerchantId() throws Exception {
		
		String URI = "/v1/merchants/" + 0 + "/clients/" + 1;
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(10)
	public void testgetClientsByMerchantId_FailByMerchantId() throws Exception {
		String URI = "/v1/merchants/" + 0 + "/clients";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(11)
	public void testDeleteClient_FailByMerchantId() throws Exception {
		String URI = "/v1/merchants/" + 0 + "/clients/" + 1;
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());
	}
	
	 @Test
	 @Order(12)
	 public void testCreateMerchantInvoices_FailByMerchantId() throws Exception {

	        List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
	        InvoiceReq invoice=new InvoiceReq();
	        ClientReq clientReq = new ClientReq();
	        
	        MerchantEntity merchantEntity=new MerchantEntity();
	        merchantEntity.setName("Test merchant");
	        merchantEntity=merchantRepository.save(merchantEntity);
	        clientReq.setVatNumber("12345");
	        clientReq.setName("Test Client");
	        invoice.setAmountGross(new BigDecimal(2200));
	        invoice.setAmountNet(new BigDecimal(2500));
	        invoice.setCurrency("INR");
	        invoice.setExternalId("AWS457744");
	        invoice.setExternalRef("AWS33455");
	        invoice.setInvoiceNumber("1001");
	        invoice.setLinkDoc("www.linkdoc.com");
	        invoice.setDueDate(LocalDateTime.now());
	        invoice.setExpectedDate(LocalDateTime.now());
	        invoice.setClient(clientReq);
	        
	        invoices.add(invoice);
	        

	        String URI = "/v1/merchants/" + 0 + "/invoices";
	      
	        
	        mvc.perform(
	                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
	                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
	                status().isNotFound());
	    }
	 
	  @Test
	  @Order(13)
	  public void testGetInvoicesByMerchant_FailByMerchantId() throws Exception {
	        
	        String URI = "/v1/merchants/0/invoices";

	        mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	    }
	  
		@Test
		@Order(14)
		public void testGetWidgets_FailByMerchantId() throws Exception {

			String URI = "/v1/widgets/" + 0;
			mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

		}
		
		@Test
		@Order(15)
		public void testUpdateClient_FailByMerchantId() throws Exception {
			ClientReq request = new ClientReq();
			request.setName("test Client 1");
			request.setEmail("test@gmail.com");
			request.setAddress("Italy");
			
			String URI = "/v1/merchants/" + 0 + "/clients/" + 1;
			mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		}
		
		@Test
		@Order(16)
		public void testgetClientById_FailByClientId() throws Exception {
			
			MerchantEntity merchant = new MerchantEntity();
			merchant.setName("Merchant for update client");
			merchant.setVatNumber("12345678901");
			merchant.setDirectUrl("Http:www.www.it");
			merchant.setAddressCity("Cagliari");
			merchant.setAddressState("Sardinia");
			merchant.setAddressCAP("09100");
			merchant.setAddressStreet("Via PPP");
			merchant = merchantRepository.save(merchant);

			ClientEntity clientEntity = new ClientEntity();
			clientEntity.setVatNumber("32323211");
			clientEntity.setName("Test Client");
			clientEntity.setMerchantEntity(merchant);
			clientEntity.setDeleted(true);
			clientEntity = clientRepository.save(clientEntity);
			
			
			MerchantEntity merchant1 = new MerchantEntity();
			merchant1.setName("Merchant for update client");
			merchant1.setVatNumber("12345678901");
			merchant1.setDirectUrl("Http:www.www.it");
			merchant1.setAddressCity("Cagliari");
			merchant1.setAddressState("Sardinia");
			merchant1.setAddressCAP("09100");
			merchant1.setAddressStreet("Via PPP");
			merchant1 = merchantRepository.save(merchant1);
			

			ClientEntity clientEntity1 = new ClientEntity();
			clientEntity1.setVatNumber("121123445566");
			clientEntity1.setName("Test Client");
			clientEntity1.setMerchantEntity(merchant1);
			clientEntity1.setDeleted(false);
			clientEntity1 = clientRepository.save(clientEntity1);
						
			
			
			
			String URI = "/v1/merchants/" + 1+ "/clients/" + 0;
			mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
			
			URI = "/v1/merchants/" + clientEntity.getMerchantEntity().getId() + "/clients/" + clientEntity.getId();
			mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
			
			URI = "/v1/merchants/" + 1+ "/clients/" + clientEntity1.getId();
			mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
		}
		
		
		  @Test
		  @Order(17)
		  public void testGetInvoiceByIdOrExternalRefWithInvoiceId_FailByInvalidClient() throws Exception {

				MerchantEntity merchant = new MerchantEntity();
				merchant.setName("Merchant2");
				merchant.setVatNumber("12345678901");
				merchant.setDirectUrl("Http:www.www.it");
				merchant.setAddressCity("Cagliari");
				merchant.setAddressState("Sardinia");
				merchant.setAddressCAP("09100");
				merchant.setAddressStreet("Via PPP");
				merchant = merchantRepository.save(merchant);
				
				ClientEntity request = new ClientEntity();
				request.setName("test Client");
				request.setVatNumber("12345789");
				request.setEmail("test@gmail.com");
				request.setAddress("Italy");
				request.setTelephone("6673382");
				request.setDeleted(true);
				request.setMerchantEntity(merchant);
			    request= clientRepository.save(request);
	            Invoices invoice = new Invoices();
	            invoice.setAmountGross(new BigDecimal(755));
	            invoice.setAmountNet(new BigDecimal(755));
	            invoice.setExternalRef("ghtjyif");
	            invoice.setExternalId("AWS457744");
		        invoice.setInvoiceNumber("1001");
		        invoice.setLinkDoc("www.linkdoc.com");
		        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		        invoice.setDueDate(LocalDateTime.now());
		        invoice.setExpectedDate(LocalDateTime.now());
		        invoice.setClientEntity(request);
	            invoice = invoicesRepository.save(invoice);
	            String URI = "/v1/merchants/"+merchant.getId()+"/invoice?invoiceId="+invoice.getId();
		        mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
			   
		  }
		  
		  @Test
		  @Order(18)
		  public void testGetInvoiceByIdOrExternalRefWithExternalRef_FailByInvalidClient() throws Exception {

				MerchantEntity merchant = new MerchantEntity();
				merchant.setName("Merchant2");
				merchant.setVatNumber("12345678901");
				merchant.setDirectUrl("Http:www.www.it");
				merchant.setAddressCity("Cagliari");
				merchant.setAddressState("Sardinia");
				merchant.setAddressCAP("09100");
				merchant.setAddressStreet("Via PPP");
				merchant = merchantRepository.save(merchant);
				
				ClientEntity request = new ClientEntity();
				request.setName("test Client");
				request.setVatNumber("12345789");
				request.setEmail("test@gmail.com");
				request.setAddress("Italy");
				request.setTelephone("6673382");
				request.setDeleted(true);
				request.setMerchantEntity(merchant);
				request= clientRepository.save(request);
	            Invoices invoice = new Invoices();
	            invoice.setAmountGross(new BigDecimal(755));
	            invoice.setAmountNet(new BigDecimal(755));
	            invoice.setExternalRef("eergshv");
	            invoice.setExternalId("AWS457744");
		        invoice.setInvoiceNumber("1001");
		        invoice.setLinkDoc("www.linkdoc.com");
		        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		        invoice.setDueDate(LocalDateTime.now());
		        invoice.setExpectedDate(LocalDateTime.now());
		        invoice.setClientEntity(request);
	            invoice = invoicesRepository.save(invoice);
		         String URI = "/v1/merchants/"+merchant.getId()+"/invoice?externalRef="+invoice.getExternalRef();
		         mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
			   
		  }
		  
			@Test
			@Order(19)
			public void testUpdateClient_FailByClientId() throws Exception {
				ClientReq request = new ClientReq();
				request.setName("test Client 1");
				request.setEmail("test@gmail.com");
				request.setAddress("Italy");
				
				String URI = "/v1/merchants/" + 1 + "/clients/" + 0;
				mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
			}
			
		   
		    
		    
			@Test
			@Order(20)
			public void testCreateClient_FailClientExists() throws Exception {
				MerchantEntity merchant = new MerchantEntity();
				merchant.setName("Merchant2");
				merchant.setVatNumber("12345678901");
				merchant.setDirectUrl("Http:www.www.it");
				merchant.setAddressCity("Cagliari");
				merchant.setAddressState("Sardinia");
				merchant.setAddressCAP("09100");
				merchant.setAddressStreet("Via PPP");
				merchant = merchantRepository.save(merchant);
				
				ClientReq request = new ClientReq();
				request.setName("test Client");
				request.setVatNumber("567y4re");
				request.setEmail("test@gmail.com");
				request.setAddress("Italy");
				request.setTelephone("6673382");

				String URI = "/v1/merchants/" + merchant.getId() + "/clients";
				mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated());

				URI = "/v1/merchants/" + merchant.getId() + "/clients";
				mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest());
			}
			
			@Test
			@Order(21)
			void testgetClientByIdOne() throws Exception {
				MerchantEntity merchant = new MerchantEntity();
				merchant.setName("Merchant2");
				merchant.setVatNumber("12345678901");
				merchant.setDirectUrl("Http:www.www.it");
				merchant.setAddressCity("Cagliari");
				merchant.setAddressState("Sardinia");
				merchant.setAddressCAP("09100");
				merchant.setAddressStreet("Via PPP");
				merchant = merchantRepository.save(merchant);
				
				ClientEntity request = new ClientEntity();
				request.setName("test Client");
				request.setVatNumber("12345789");
				request.setEmail("test@gmail.com");
				request.setAddress("Italy");
				request.setTelephone("6673382");
				request.setDeleted(false);
				request.setMerchantEntity(merchant);
				request= clientRepository.save(request);
	            Invoices invoice = new Invoices();
	            invoice.setAmountGross(new BigDecimal(755));
	            invoice.setAmountNet(new BigDecimal(755));
	            invoice.setExternalRef("ggthyuj");
	            invoice.setExternalId("AWS457744");
		        invoice.setInvoiceNumber("1001");
		        invoice.setLinkDoc("www.linkdoc.com");
		        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		        invoice.setDueDate(LocalDateTime.now());
		        invoice.setExpectedDate(LocalDateTime.now());
		        invoice.setClientEntity(request);
	            invoice = invoicesRepository.save(invoice);
			
				String URI = "/v1/merchants/" + merchant.getId() + "/clients/" + request.getId();
				MvcResult result  = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
				String contentAsString = result.getResponse().getContentAsString();
				JSONObject json = new JSONObject(contentAsString);
				Map<String, Object> responseMap = mapper.readValue(json.get("data").toString(), Map.class);
				assertNotNull(responseMap.get("client"));
				assertNotNull(responseMap.get("invoiceDetail"));
				
			}
			
			  @Test
			  @Order(22)
			  void testGetInvoiceExternalRefWithExternalRef_FailByInvalidClient() throws Exception {

					MerchantEntity merchant = new MerchantEntity();
					merchant.setName("Merchant2");
					merchant.setVatNumber("12345678901");
					merchant.setDirectUrl("Http:www.www.it");
					merchant.setAddressCity("Cagliari");
					merchant.setAddressState("Sardinia");
					merchant.setAddressCAP("09100");
					merchant.setAddressStreet("Via PPP");
					merchant = merchantRepository.save(merchant);
					
					ClientEntity request = new ClientEntity();
					request.setName("test Client for externelRef");
					request.setVatNumber("33446776");
					request.setEmail("test@gmail.com");
					request.setAddress("Italy");
					request.setTelephone("6673382");
					request.setDeleted(true);
					request.setMerchantEntity(merchant);
					request= clientRepository.save(request);
		            Invoices invoice = new Invoices();
		            invoice.setAmountGross(new BigDecimal(755));
		            invoice.setAmountNet(new BigDecimal(755));
		            invoice.setExternalRef("ffghtynb34");
		            invoice.setExternalId("AWS457744");
			        invoice.setInvoiceNumber("1001");
			        invoice.setLinkDoc("www.linkdoc.com");
			        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
			        invoice.setDueDate(LocalDateTime.now());
			        invoice.setExpectedDate(LocalDateTime.now());
			        invoice.setClientEntity(request);
		            invoice = invoicesRepository.save(invoice);
			         String URI = "/v1/invoice?externalRef="+invoice.getExternalRef();
			         mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
				   
			  }
			  
			  
			  
				@Test
				@Order(23)
				void testUpdateClient_FaildForSameVatNumber() throws Exception {
				
					MerchantEntity merchant = new MerchantEntity();
					merchant.setName("Merchant for update client");
					merchant.setVatNumber("12345678901");
					merchant.setDirectUrl("Http:www.www.it");
					merchant.setAddressCity("Cagliari");
					merchant.setAddressState("Sardinia");
					merchant.setAddressCAP("09100");
					merchant.setAddressStreet("Via PPP");
					merchant = merchantRepository.save(merchant);

					ClientEntity clientEntity = new ClientEntity();
					clientEntity.setVatNumber("33211");
					clientEntity.setName("Test Client");
					clientEntity.setMerchantEntity(merchant);
					clientEntity.setDeleted(false);
					clientEntity = clientRepository.save(clientEntity);
					
					

					ClientEntity clientEntity1 = new ClientEntity();
					clientEntity1.setVatNumber("123445566");
					clientEntity1.setName("Test Client");
					clientEntity1.setMerchantEntity(merchant);
					clientEntity1.setDeleted(false);
					clientEntity1 = clientRepository.save(clientEntity1);
					
					ClientReq request = new ClientReq();
					request.setVatNumber("33211");
					request.setEmail("test@gmail.com");
					request.setAddress("Italy");
					request.setTelephone("996673382");
					request.setName("Updated Name");
					String URI = "/v1/merchants/" + merchant.getId() + "/clients/" + clientEntity1.getId();
					 mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
										.contentType(MediaType.APPLICATION_JSON))
								.andExpect(status().isBadRequest());

				}
				
				
				@Test
				@Order(24)
				void testCreateClient_FaildForInvalidRequest() throws Exception {
					MerchantEntity merchant = new MerchantEntity();
					merchant.setName("Merchant2");
					merchant.setVatNumber("12345678901");
					merchant.setDirectUrl("Http:www.www.it");
					merchant.setAddressCity("Cagliari");
					merchant.setAddressState("Sardinia");
					merchant.setAddressCAP("09100");
					merchant.setAddressStreet("Via PPP");
					merchant = merchantRepository.save(merchant);
					
					ClientReq request = new ClientReq();
					String URI = "/v1/merchants/" + merchant.getId() + "/clients";
					mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest());
					
					request.setName("test Client");
					
					mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest());
					request.setVatNumber("334rr5t");
					request.setEmail("test@gmail.com");
					request.setAddress("Italy");
					request.setTelephone("6673382");
					ClientReq request2 = new ClientReq();
					request2.setVatNumber("jfdfkd");
					request2.setEmail("test@gmail.com");
					request2.setAddress("Italy");
					request2.setTelephone("6673382");
					mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request2))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest());

				
				}
				
				
				
				@Test
				@Order(25)
				void testgetClientsByMerchantIdWithEmptyList() throws Exception {
					MerchantEntity merchant = new MerchantEntity();
					merchant.setName("Merchant2");
					merchant.setVatNumber("12345678901");
					merchant.setDirectUrl("Http:www.www.it");
					merchant.setAddressCity("Cagliari");
					merchant.setAddressState("Sardinia");
					merchant.setAddressCAP("09100");
					merchant.setAddressStreet("Via PPP");
					merchant = merchantRepository.save(merchant);
					String URI = "/v1/merchants/" + merchant.getId() + "/clients";
					mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
				}
				
				@Test
				@Order(26)
				public void testCreateClientDefaultPaymentDays() throws Exception {
					MerchantEntity merchant = new MerchantEntity();
					merchant.setName("Merchant2");
					merchant.setVatNumber("12345678901");
					merchant.setDirectUrl("Http:www.www.it");
					merchant.setAddressCity("Cagliari");
					merchant.setAddressState("Sardinia");
					merchant.setAddressCAP("09100");
					merchant.setAddressStreet("Via PPP");
					merchant.setDefaultPaymentDays(15);
					merchant = merchantRepository.save(merchant);
					
					ClientReq request = new ClientReq();
					request.setName("test Client");
					request.setVatNumber("123wew45789");
					request.setAddress("Italy");
					request.setTelephone("6673382");

					String URI = "/v1/merchants/" + merchant.getId() + "/clients";
					mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName()))).
							andExpect(jsonPath("$.data.defaultPaymentDays", is(merchant.getDefaultPaymentDays())));
				}
				
				
				
	@Test
	@Order(26)
	void testCreateClientWithWorkflow() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		WorkflowEntity workflowEntity = new WorkflowEntity();
		workflowEntity.setDefaultWorkflow(false);
		workflowEntity.setEmail("testWorkflow@gmail.com");
		workflowEntity.setName("test workflow");
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMinimumContactDelay(13);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientReq request = new ClientReq();
		request.setName("test Client");
		request.setVatNumber("test1232214rftg");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setWorkflowId(workflowEntity.getId());

		String URI = "/v1/merchants/" + merchant.getId() + "/clients";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.workflowId", is(Integer.valueOf(workflowEntity.getId().toString()))));

		request = new ClientReq();
		request.setName("test Client");
		request.setVatNumber("test1234rftg");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setWorkflowId(0l);

		URI = "/v1/merchants/" + merchant.getId() + "/clients";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		merchant = new MerchantEntity();
		merchant.setName("Merchant3");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("091010");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		request = new ClientReq();
		request.setName("test Client");
		request.setVatNumber("test1234rftg");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setWorkflowId(workflowEntity.getId());
		URI = "/v1/merchants/" + merchant.getId() + "/clients";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(27)
	void testUpdateClientWithWorkflow() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant for update client");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		WorkflowEntity workflowEntity = new WorkflowEntity();
		workflowEntity.setDefaultWorkflow(false);
		workflowEntity.setEmail("testWorkflow@gmail.com");
		workflowEntity.setName("test workflow");
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMinimumContactDelay(13);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setVatNumber("33211");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		assertNull(clientEntity.getWorkflowEntity());

		ClientReq request = new ClientReq();
		request.setVatNumber("3222111556");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("996673382");
		request.setName("Updated Name");
		request.setWorkflowId(workflowEntity.getId());
		String URI = "/v1/merchants/" + merchant.getId() + "/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.workflowId", is(Integer.valueOf(workflowEntity.getId().toString()))));
		
		clientEntity = clientRepository.findById(clientEntity.getId()).get();
		assertNotNull(clientEntity.getWorkflowEntity());
		
		request = new ClientReq();
		request.setVatNumber("3222111556");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("996673382");
		request.setName("Updated Name");
		request.setWorkflowId(0l);
		URI = "/v1/merchants/" + merchant.getId() + "/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		
		
		merchant = new MerchantEntity();
		merchant.setName("Merchant3");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("091010");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		clientEntity = new ClientEntity();
		clientEntity.setVatNumber("331452211");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		
		request = new ClientReq();
		request.setVatNumber("3222111556");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("996673382");
		request.setName("Updated Name");
		request.setWorkflowId(workflowEntity.getId());
		URI = "/v1/merchants/" + merchant.getId() + "/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(28)
	public void testGetClientsByIdWithLastCommunicationDate() throws Exception {
		
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("test1234@test.com");
		user.setFirstName("test");
		user.setLastName("test lastname");
		user.setPassword("test12345678");
		user.setPhone("2424856");

		String URI = "/v1/users";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		UserEntity userEntity = userRepository.findByEmailIgnoreCase(user.getEmail()).get();
		WorkflowEntity workflowEntity = workflowRepository
				.findByMerchantEntityIdAndIsDeleted(userEntity.getMerchant().getId(), false).get(0);
		workflowEntity.setMinimumContactDelay(5);
		workflowEntity = workflowRepository.save(workflowEntity);
		
		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();
		
		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "testMessage");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String newJson = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(newJson);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity); 
		
		
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setVatNumber("33pp1");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(userEntity.getMerchant());
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		
		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("554pr");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice = invoicesRepository.save(invoice);
		
		TimelineEntity timelineEntity = new TimelineEntity();
		timelineEntity.setActionEntity(actionEntity);
		timelineEntity.setSkip(false);
		timelineEntity.setReschedule(false);
		timelineEntity.setClientEntity(clientEntity);
		timelineEntity.setInvoice(invoice);
		timelineEntity.setMessage("test message");
		timelineEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setType(TimelineEntityTypeEnum.ACTION.getValue());
		timelineRepository.save(timelineEntity);
		
		MerchantEntity merchantEntity = userEntity.getMerchant();
		URI = "/v1/merchants/" + merchantEntity.getId() + "/clients/" + clientEntity.getId();
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		Map<String, Object> responseMap = mapper.readValue(json.get("data").toString(), Map.class);
		Map<String,Object> clinetMap = (Map<String, Object>) responseMap.get("client");
		assertNotNull(clinetMap.get("lastCommunicationDate"));
	}
}
