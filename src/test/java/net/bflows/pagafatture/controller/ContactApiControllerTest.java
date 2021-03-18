package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.ContactsEntity;
import net.bflows.pagafatture.entities.ContactsEntity.ContactsRoleEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.ContactReq;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.ContactRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class ContactApiControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private MerchantRepository merchantRepository;

	@Test
	@Order(1)
	public void testCreateContact() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant for update contact");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setName("test Client");
		clientEntity.setVatNumber("12345");
		clientEntity.setEmail("test@gmail.com");
		clientEntity.setAddress("Italy");
		clientEntity.setDeleted(false);
		clientEntity.setMerchantEntity(merchant);
		clientEntity = clientRepository.save(clientEntity);

		ContactReq contactReq = new ContactReq();
		contactReq.setName("test contact");
		contactReq.setEmail("test@gmail.com");
		contactReq.setPhone("+87945678");
		contactReq.setLastName("Last Name");

		
		String URI = "/v1/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(contactReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(contactReq.getName())));
	}
	
	
	
	@Test
	@Order(2)
	void testUpdateContact() throws Exception {
	MerchantEntity merchant = new MerchantEntity();
	merchant.setName("Merchant for update contact");
	merchant.setVatNumber("12345678901");
	merchant.setDirectUrl("Http:www.www.it");
	merchant.setAddressCity("Cagliari");
	merchant.setAddressState("Sardinia");
	merchant.setAddressCAP("09100");
	merchant.setAddressStreet("Via PPP");
	merchant = merchantRepository.save(merchant);


	ClientEntity clientEntity = new ClientEntity();
	clientEntity.setName("test Client1");
	clientEntity.setVatNumber("445544667");
	clientEntity.setEmail("test@gmail.com");
	clientEntity.setAddress("Italy");
	clientEntity.setMerchantEntity(merchant);
	clientEntity.setDeleted(false);
	clientEntity = clientRepository.save(clientEntity);
	ContactsEntity contactsEntity = new ContactsEntity();
	contactsEntity.setName("test update Contact 1");
	contactsEntity.setPhone("+123456567");
	contactsEntity.setEmail("testContact@gmail.com");
	contactsEntity.setRole(ContactsRoleEnum.ADMIN.getValue());
	contactsEntity.setClientEntity(clientEntity);
	contactsEntity = contactRepository.save(contactsEntity);
	ContactReq request = new ContactReq();

	request.setName("Updated Name");
	request.setRole(ContactsRoleEnum.SALES);
	String URI = "/v1/clients/" + contactsEntity.getClientEntity().getId() + "/contacts/" + contactsEntity.getId();
	mvc
	.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
	.contentType(MediaType.APPLICATION_JSON))
	.andExpect(status().isOk()).andExpect(jsonPath("$.data.name", is(request.getName())));
	request = new ContactReq();
	request.setName("Updated Name");
	URI = "/v1/clients/" + contactsEntity.getClientEntity().getId() + "/contacts/" + contactsEntity.getId();
	mvc
	.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
	.contentType(MediaType.APPLICATION_JSON))
	.andExpect(status().isOk()).andExpect(jsonPath("$.data.name", is(request.getName())));

	}

	@Test
	@Order(3)
	public void testgetContactById() throws Exception {
		ContactsEntity contactsEntity = contactRepository.findById(1l).get();
		String URI = "/v1/clients/" + contactsEntity.getClientEntity().getId() + "/contacts/" + contactsEntity.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
	}

	@Test
	@Order(4)
	public void testgetContactsByClientId() throws Exception {
		ContactsEntity contactsEntity = contactRepository.findById(1l).get();
		String URI = "/v1/clients/" + contactsEntity.getClientEntity().getId() + "/contacts";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
	}



	@Test
	@Order(5)
	public void testUpdateContact_Fail() throws Exception {
		ContactsEntity contactsEntity = contactRepository.findById(1l).get();

		ContactReq request = new ContactReq();
		request.setName("test Contact 1");
		request.setPhone("+123456567");
		request.setEmail("test@gmail.com");
		request.setRole(ContactsRoleEnum.ADMIN);;
		String URI = "/v1/clients/" + contactsEntity.getClientEntity().getId() + "/contacts/" + 0;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	@Order(6)
	public void testDeleteContact_Fail() throws Exception {
		ContactsEntity contactsEntity = contactRepository.findById(1l).get();
		String URI = "/v1/clients/" + contactsEntity.getClientEntity().getId() + "/contacts/" + 0;
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());
	}


	
	@Test
	@Order(7)
	public void testCreateContact_FailByClientId() throws Exception {

		
		ContactReq contactReq = new ContactReq();
		contactReq.setName("test contact");
		contactReq.setEmail("test@gmail.com");
		contactReq.setPhone("+87945678");
		contactReq.setRole(ContactsRoleEnum.SALES);

		
		String URI = "/v1/clients/" + 0;
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(contactReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant for contact");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");

		merchant = merchantRepository.save(merchant);
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setName("test Client1");
		clientEntity.setVatNumber("8855776");
		clientEntity.setEmail("test@gmail.com");
		clientEntity.setAddress("Italy");
		clientEntity.setDeleted(true);
		clientEntity.setMerchantEntity(merchant);
		clientEntity = clientRepository.save(clientEntity);
		
		URI = "/v1/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(contactReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	
	@Test
	@Order(8)
	public void testUpdateContact_FailClientId() throws Exception {
		ContactsEntity contactsEntity = contactRepository.findById(1l).get();

		ContactReq request = new ContactReq();
		request.setName("test Contact 1");
		request.setPhone("+123456567");
		request.setEmail("test@gmail.com");
		request.setRole(ContactsRoleEnum.ADMIN);
		String URI = "/v1/clients/" + 0 + "/contacts/" + contactsEntity.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(9)
	public void testgetContactById_FailByClientId() throws Exception {
		ContactsEntity contactsEntity = contactRepository.findById(1l).get();
		String URI = "/v1/clients/" + 0 + "/contacts/" + contactsEntity.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
	}
	

	@Test
	@Order(10)
	public void testgetContactById_FailById() throws Exception {
	ContactsEntity contactsEntity = contactRepository.findById(1l).get();
	String URI = "/v1/clients/" + contactsEntity.getClientEntity().getId() + "/contacts/" + 0;
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	URI = "/v1/clients/" + 0 + "/contacts/" + contactsEntity.getId();
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());


	ContactsEntity contactsEntity2 = new ContactsEntity();
	contactsEntity2.setClientEntity(contactsEntity.getClientEntity());
	contactsEntity2.setName("test contact");
	contactsEntity2.setPhone("123456");
	contactsEntity2.setEmail("testContact@gmail.com");
	contactsEntity2.setRole(ContactsRoleEnum.PAYER.getValue());
	contactsEntity2.setDeleted(true);
	contactsEntity2=contactRepository.save(contactsEntity2);
	URI = "/v1/clients/" + contactsEntity2.getClientEntity().getId() + "/contacts/" + contactsEntity2.getId();
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	MerchantEntity merchant = new MerchantEntity();
	merchant.setName("Merchant for contact");
	merchant.setVatNumber("12345678901");
	merchant.setDirectUrl("Http:www.www.it");
	merchant.setAddressCity("Cagliari");
	merchant.setAddressState("Sardinia");
	merchant.setAddressCAP("09100");
	merchant.setAddressStreet("Via PPP");

	merchant = merchantRepository.save(merchant);
	ClientEntity clientEntity = new ClientEntity();
	clientEntity.setName("test Client1");
	clientEntity.setVatNumber("8855776");
	clientEntity.setEmail("test@gmail.com");
	clientEntity.setAddress("Italy");
	clientEntity.setDeleted(true);
	clientEntity.setMerchantEntity(merchant);
	clientEntity = clientRepository.save(clientEntity);

	ContactsEntity contactsEntity3 = new ContactsEntity();
	contactsEntity3.setClientEntity(clientEntity);
	contactsEntity3.setName("test contact");
	contactsEntity3.setPhone("123456");
	contactsEntity3.setEmail("testContact@gmail.com");
	contactsEntity3.setRole(ContactsRoleEnum.PAYER.getValue());
	contactsEntity3.setDeleted(false);
	contactsEntity3=contactRepository.save(contactsEntity3);
	URI = "/v1/clients/" + contactsEntity3.getClientEntity().getId() + "/contacts/" + contactsEntity3.getId();
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	URI = "/v1/clients/" + contactsEntity2.getClientEntity().getId() + "/contacts/" + contactsEntity3.getId();
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());


	}
	
	@Test
	@Order(11)
	public void testgetContactsByClientId_Fail() throws Exception {
	String URI = "/v1/clients/" + 0 + "/contacts";
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	MerchantEntity merchant = new MerchantEntity();
	merchant.setName("Merchant for contact");
	merchant.setVatNumber("12345678901");
	merchant.setDirectUrl("Http:www.www.it");
	merchant.setAddressCity("Cagliari");
	merchant.setAddressState("Sardinia");
	merchant.setAddressCAP("09100");
	merchant.setAddressStreet("Via PPP");

	merchant = merchantRepository.save(merchant);
	ClientEntity clientEntity = new ClientEntity();
	clientEntity.setName("test Client1");
	clientEntity.setVatNumber("2233432455");
	clientEntity.setEmail("test@gmail.com");
	clientEntity.setAddress("Italy");
	clientEntity.setDeleted(false);
	clientEntity.setMerchantEntity(merchant);
	clientEntity = clientRepository.save(clientEntity);
	URI = "/v1/clients/" + clientEntity.getId() + "/contacts";
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

	clientEntity.setDeleted(true);
	clientEntity = clientRepository.save(clientEntity);
	URI = "/v1/clients/" + clientEntity.getId() + "/contacts";
	mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	}
	
	@Test
	@Order(12)
	public void testDeleteContact_FailByClientId() throws Exception {
	ContactsEntity contactsEntity1 = contactRepository.findById(1l).get();
	String URI = "/v1/clients/" + 0 + "/contacts/" + contactsEntity1.getId();
	mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());
	MerchantEntity merchant = new MerchantEntity();
	merchant.setName("Merchant for contact");
	merchant.setVatNumber("12345678901");
	merchant.setDirectUrl("Http:www.www.it");
	merchant.setAddressCity("Cagliari");
	merchant.setAddressState("Sardinia");
	merchant.setAddressCAP("09100");
	merchant.setAddressStreet("Via PPP");

	merchant = merchantRepository.save(merchant);
	ClientEntity clientEntity = new ClientEntity();
	clientEntity.setName("test Client1");
	clientEntity.setVatNumber("2233432455");
	clientEntity.setEmail("test@gmail.com");
	clientEntity.setAddress("Italy");
	clientEntity.setDeleted(false);
	clientEntity.setMerchantEntity(merchant);
	clientEntity = clientRepository.save(clientEntity);
	ContactsEntity contactsEntity2 = new ContactsEntity();
	contactsEntity2.setName("test Contact delete");
	contactsEntity2.setPhone("+123456567");
	contactsEntity2.setEmail("test@gmail.com");
	contactsEntity2.setRole(ContactsRoleEnum.ADMIN.getValue());
	contactsEntity2.setClientEntity(clientEntity);
	contactsEntity2= contactRepository.save(contactsEntity2);   
	URI = "/v1/clients/" + contactsEntity2.getClientEntity().getId() + "/contacts/" + contactsEntity1.getId();
	mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());
	clientEntity.setDeleted(true);
	clientEntity = clientRepository.save(clientEntity);
	URI = "/v1/clients/" + contactsEntity2.getClientEntity().getId() + "/contacts/" + contactsEntity2.getId();
	mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());

	}

	@Test
	@Order(13)
	 void testDeleteContact() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant for contact");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");

		merchant = merchantRepository.save(merchant);
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setName("test Client1");
		clientEntity.setVatNumber("22334455");
		clientEntity.setEmail("test@gmail.com");
		clientEntity.setAddress("Italy");
		clientEntity.setDeleted(false);
		clientEntity.setMerchantEntity(merchant);
		clientEntity = clientRepository.save(clientEntity);
		ContactsEntity contactsEntity = new ContactsEntity();
		contactsEntity.setName("test Contact delete");
		contactsEntity.setPhone("+123456567");
		contactsEntity.setEmail("test@gmail.com");
		contactsEntity.setRole(ContactsRoleEnum.ADMIN.getValue());
		contactsEntity.setClientEntity(clientEntity);
		contactsEntity= contactRepository.save(contactsEntity);	
		String URI = "/v1/clients/" +  contactsEntity.getClientEntity().getId() + "/contacts/" + contactsEntity.getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isOk());
	}

	@Test
	@Order(14)
	public void testUpdateContact_FailInvalidClientId() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant contact");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setName("test Client1");
		clientEntity.setVatNumber("12345dcdf");
		clientEntity.setEmail("test@gmail.com");
		clientEntity.setAddress("Italy");
		clientEntity.setDeleted(true);
		clientEntity.setMerchantEntity(merchant);
		clientEntity = clientRepository.save(clientEntity);
		ContactsEntity contactsEntity = contactRepository.findById(1l).get();
		ContactReq request = new ContactReq();
		request.setName("test Contact 1");
		request.setPhone("+123456567");
		request.setEmail("test@gmail.com");
		request.setRole(ContactsRoleEnum.ADMIN);
		String URI = "/v1/clients/" + clientEntity.getId() + "/contacts/" + contactsEntity.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		
		
		ClientEntity clientEntity1 = new ClientEntity();
		clientEntity1.setName("test Client1");
		clientEntity1.setVatNumber("12345dcdf");
		clientEntity1.setEmail("test@gmail.com");
		clientEntity1.setAddress("Italy");
		clientEntity1.setDeleted(false);
		clientEntity1.setMerchantEntity(merchant);
		clientEntity1 = clientRepository.save(clientEntity1);
		URI = "/v1/clients/" + clientEntity1.getId() + "/contacts/" + contactsEntity.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	

	@Test
	@Order(15)
	void testCreateContact_withContactRole() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant contact");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setName("test Client");
		clientEntity.setVatNumber("445378");
		clientEntity.setEmail("test@gmail.com");
		clientEntity.setAddress("Italy");
		clientEntity.setDeleted(false);
		clientEntity.setMerchantEntity(merchant);
		clientEntity = clientRepository.save(clientEntity);
		ContactsEntity contactsEntity = new ContactsEntity();
		contactsEntity.setName("test Contact delete");
		contactsEntity.setPhone("+123456567");
		contactsEntity.setEmail("test@gmail.com");
		contactsEntity.setRole(ContactsRoleEnum.ADMIN.getValue());
		contactsEntity.setClientEntity(clientEntity);
		contactsEntity= contactRepository.save(contactsEntity);	
		ContactReq contactReq = new ContactReq();
		contactReq.setName("test contact");
		contactReq.setEmail("test@gmail.com");
		contactReq.setPhone("+87945678");
		contactReq.setRole(ContactsRoleEnum.ACCOUNTING);

		
		String URI = "/v1/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(contactReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(contactReq.getName())));
	}

	
	
	@Test
	@Order(16)
	void testCreateContact_withInvalidEnum() throws Exception {

			
		ContactReq contactReq = new ContactReq();
		contactReq.setName("test contact");
		contactReq.setEmail("test@gmail.com");
		contactReq.setPhone("+87945678");
		try {
		    contactReq.setRole(ContactsRoleEnum.fromValue("admin"));
		} 
		catch (Exception e) {
		    assertEquals("Unexpected value 'admin'",e.getMessage());
		}   

	}
	
	
	@Test
	@Order(17)
	void testCreateContact_FaildForInvalidRequest() throws Exception {

		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setName("test Client");
		clientEntity.setVatNumber("12345");
		clientEntity.setEmail("test@gmail.com");
		clientEntity.setAddress("Italy");
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);

		ContactReq contactReq = new ContactReq();
		String URI = "/v1/clients/" + clientEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(contactReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		contactReq.setName("test contact");
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(contactReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		contactReq.setEmail("test@gmail.com");
		contactReq.setPhone("+87945678");
		ContactReq contactReq2 = new ContactReq();
		contactReq2.setEmail("test1@gmail.com");
		contactReq2.setPhone("+87945678");
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(contactReq2))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
}
