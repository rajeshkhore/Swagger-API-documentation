package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.ActionEntity;
import net.bflows.pagafatture.entities.ActionTypeEntity;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.UpdateActionReq;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.WorkflowReq;
import net.bflows.pagafatture.model.WorkflowUpdateReq;
import net.bflows.pagafatture.repositories.ActionRepository;
import net.bflows.pagafatture.repositories.ActionTypeRepository;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.repositories.WorkflowRepository;
import net.bflows.pagafatture.util.DateTimeUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class WorkflowApiControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MerchantRepository merchantRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WorkflowRepository workflowRepository;
	
	@Autowired
	private ActionTypeRepository actionTypeRepository;
	
	@Autowired
	private ActionRepository actionRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	
	private static final String ACTIONTYPEJSON ="{\n" + 
			"\"recipients\": [\n" + 
			"\"Main contact\",\n" + 
			"\"Accounting\",\n" + 
			"\"Sales\",\n" + 
			"\"Payer\",\"Purchaser\"\n" + 
			"],\n" + 
			"\"callScript\":\"Objectives :- understand potential blocking points - obtain a payment date commitment - send a follow-up mail after the call\"\n" + 
			"\n" + 
			"}";
	
	private static  Map<String, Object> ACTIONJSON=null;
	
	private List<String> recipients = Arrays.asList("recipients@test.com");
	private List<String> BCC = Arrays.asList("BCC@test.com");
	private List<String> CC = Arrays.asList("CC@gmail.com");

	@Test
	@Order(1)
	void testCreateWorkFlow_Success() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow without email");
		request.setMinimumContactDelay(20);
		String URI = "/v1/workflows/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(request.getMinimumContactDelay())))
				.andExpect(jsonPath("$.data.email", is(merchant.getEmail())));

		WorkflowReq request2 = new WorkflowReq();
		request2.setName("test workflow with email");
		request2.setEmail("testworkflow@gmail.com");
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request2))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request2.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(5)))
				.andExpect(jsonPath("$.data.email", is(request2.getEmail()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WorkflowReq response = mapper.readValue(json.get("data").toString(), WorkflowReq.class);
		request2.setId(response.getId());
		request2.setName("new duplicate name");
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request2))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request2.getName())))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(response.getMinimumContactDelay())))
				.andExpect(jsonPath("$.data.email", is(response.getEmail())));
		
		
		request2 = new WorkflowReq();
		request2.setEmail("testworkflow@gmail.com");
		request2.setId(response.getId());
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request2))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(response.getName() + " copy")))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(response.getMinimumContactDelay())))
				.andExpect(jsonPath("$.data.email", is(response.getEmail())));

	}

	@Test
	@Order(2)
	void testCreateWorkFlow_FailByMerchantId() throws Exception {

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setMinimumContactDelay(20);
		String URI = "/v1/workflows/" + 0;
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}
	
	@Test
	@Order(3)
	void testCreateWorkFlow_FailByWorkflowId() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setMinimumContactDelay(20);
		request.setId(0l);
		String URI = "/v1/workflows/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}
	
	@Test
	@Order(4)
	void testCreateWorkFlow_FailByEmptyName() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		
		request.setMinimumContactDelay(20);
		String URI = "/v1/workflows/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		request.setName("");
		URI = "/v1/workflows/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		

	}
	
	
	
	@Test
	@Order(5)
	void testGetWorkflowsByMerchantId_Success() throws Exception {

		
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);
		
		String URI = "/v1/workflows/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

		WorkflowReq request2 = new WorkflowReq();
		request2.setName("test workflow");
		request2.setEmail("testworkflow@gmail.com");
		URI = "/v1/workflows/" + merchant.getId();
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request2))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request2.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(5)))
				.andExpect(jsonPath("$.data.email", is(request2.getEmail()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		mapper.readValue(json.get("data").toString(), WorkflowReq.class);

		URI = "/v1/workflows/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
		result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
		contentAsString = result.getResponse().getContentAsString();
		json = new JSONObject(contentAsString);
		List<Map<String, Object>> workflowResponse = mapper.readValue(json.get("data").toString(), List.class);
		assertTrue(workflowResponse.size() > 0);
		for (Map<String, Object> workflow : workflowResponse) {
               assertNotNull(workflow.get("name"));
               assertNotNull(workflow.get("defaultWorkflow"));
               assertNotNull(workflow.get("minimumContactDelay"));
               assertNotNull(workflow.get("email"));
               assertNotNull(workflow.get("id"));
               assertNotNull(workflow.get("createdDate"));
               assertNotNull(workflow.get("updatedDate"));
		}

		// for default workflow
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test company");
		user.setConfPassword("test12345678");
		user.setEmail("testDefaultWorkflow@gmail.com");
		user.setFirstName("test");
		user.setLastName("test lastname");
		user.setPassword("test12345678");
		user.setPhone("2424856");

		URI = "/v1/users";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		UserEntity userEntity = userRepository.findByEmailIgnoreCase(user.getEmail()).get();

		URI = "/v1/workflows/" + userEntity.getMerchant().getId();
		result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
		contentAsString = result.getResponse().getContentAsString();
		json = new JSONObject(contentAsString);
		workflowResponse = mapper.readValue(json.get("data").toString(), List.class);

		assertTrue(workflowResponse.size() > 0);
		Boolean defualtWorkflowExist = false;
		for (Map<String, Object> workflow : workflowResponse) {

			if (Boolean.TRUE.equals(Boolean.valueOf(workflow.get("defaultWorkflow").toString()))) {
				defualtWorkflowExist = true;
				break;
			}

		}

		assertEquals(true, defualtWorkflowExist);

	}

	@Test
	@Order(6)
	void testGetWorkflowsByMerchantId_FailByMerchantId() throws Exception {

		String URI = "/v1/workflows/" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	
	@Test
	@Order(7)
	void testCreateAction_Success() throws Exception {
		
		
		ActionTypeEntity typeEntity=actionTypeRepository.findById(1l).get();
		
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testAction@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON =new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
	
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);
		
		ActionTypeEntity typeEntity1 = new ActionTypeEntity();
		typeEntity1.setName("MANUAL_EMAIL");
		typeEntity1.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity1.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity1.setJson(ACTIONTYPEJSON);
		typeEntity1=actionTypeRepository.save(typeEntity1);
		
		req = new ActionReq();
		req.setTriggerDays(15);
		req.setActionTypeId(typeEntity1.getId());
		req.setActionJson(ACTIONJSON);
		req.setName("new test Action");
		req.setId(response.getId());
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(response.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(response.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(response.getTriggerDays())));

		req = new ActionReq();
		req.setTriggerDays(15);
		req.setActionTypeId(typeEntity1.getId());
		req.setActionJson(ACTIONJSON);
		req.setId(response.getId());
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(response.getName() + " copy")))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(response.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(response.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(response.getTriggerDays())));

	}
	

	
	@Test
	@Order(8)
	void testUpdateWorkFlow_Success() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test update workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setEmail("testworkflow@gmail.com");
		String URI = "/v1/workflows/" + merchant.getId();
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(5)))
				.andExpect(jsonPath("$.data.email", is(request.getEmail()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WorkflowReq response = mapper.readValue(json.get("data").toString(), WorkflowReq.class);
		WorkflowUpdateReq updateRequest = new WorkflowUpdateReq();
		updateRequest.setEmail("testUpdate@test.com");
		URI = "/v1/workflows/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(response.getName())))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(response.getMinimumContactDelay())))
				.andExpect(jsonPath("$.data.email", is(updateRequest.getEmail())));
		
		updateRequest = new WorkflowUpdateReq();
		updateRequest.setEmail("testUpdate@test.com");
		updateRequest.setMinimumContactDelay(15);
		URI = "/v1/workflows/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(response.getName())))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(updateRequest.getMinimumContactDelay())))
				.andExpect(jsonPath("$.data.email", is(updateRequest.getEmail())));
	}

	@Test
	@Order(9)
	void testUpdateWorkFlow_FailByWorkflowId() throws Exception {
		WorkflowUpdateReq request = new WorkflowUpdateReq();
		request.setName("test workflow");
		String URI = "/v1/workflows/" + 0;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}
	
	@Test
	@Order(10)
	void testDeleteWorkFlow_Success() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test update workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setEmail("testworkflow@gmail.com");
		String URI = "/v1/workflows/" + merchant.getId();
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(5)))
				.andExpect(jsonPath("$.data.email", is(request.getEmail()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WorkflowReq response = mapper.readValue(json.get("data").toString(), WorkflowReq.class);
		URI = "/v1/workflows/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		WorkflowEntity entity=workflowRepository.findById(response.getId()).get();
		assertEquals(true, entity.getIsDeleted());
		mvc.perform(MockMvcRequestBuilders.delete(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@Order(11)
	void testDeleteWorkFlow_FailByWorkflowId() throws Exception {
		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		String URI = "/v1/workflows/" + 0;
		mvc.perform(MockMvcRequestBuilders.delete(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	@Order(12)
	void testCreateAction_FaildByValidations() throws Exception {
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testAction1@test.com");
		user.setFirstName("test");
		user.setLastName("test lastname");
		user.setPassword("test12345678");
		user.setPhone("2424856");

		ActionTypeEntity typeEntity = new ActionTypeEntity();
		typeEntity.setName("MANUAL_EMAIL");
		typeEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setJson(ACTIONTYPEJSON);
		typeEntity=actionTypeRepository.save(typeEntity);
		String URI = "/v1/users";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		UserEntity userEntity = userRepository.findByEmailIgnoreCase(user.getEmail()).get();
		WorkflowEntity workflowEntity = workflowRepository
				.findByMerchantEntityIdAndIsDeleted(userEntity.getMerchant().getId(), false).get(0);

		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		req = new ActionReq();
		req.setName("test action 1");
		req.setTriggerDays(5);
		req.setActionJson(new HashMap<>());
		req.setActionTypeId(typeEntity.getId());
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		
		req = new ActionReq();
		req.setName("");
		req.setTriggerDays(5);
		req.setActionJson(ACTIONJSON);
		req.setActionTypeId(typeEntity.getId());
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		req = new ActionReq();
		req.setName("test action 1");
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());


		req = new ActionReq();
		req.setName("test action");
		req.setTriggerDays(5);
		req.setActionJson(ACTIONJSON);
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());



		req = new ActionReq();
		req.setActionTypeId(typeEntity.getId());
		req.setName("test action");
		req.setActionJson(ACTIONJSON);
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		req = new ActionReq();
		req.setActionTypeId(typeEntity.getId());
		req.setName("test action");
		req.setActionJson(ACTIONJSON);
		req.setTriggerDays(5);
		req.setId(0l);
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		req = new ActionReq();
		req.setActionTypeId(0l);
		req.setName("test action");
		req.setActionJson(ACTIONJSON);
		req.setTriggerDays(10);
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}


	@Test
	@Order(13)
	void testCreateAction_FaildByWorkflowId() throws Exception {

		ActionReq req = new ActionReq();
		req.setName("test action");
		req.setTriggerDays(5);
		req.setActionTypeId(1l);
		req.setActionJson(ACTIONJSON);
		String URI = "/v1/workflows/" + 0 + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(14)
	void testGetActionTypes() throws Exception {
		String URI = "/v1/actionTypes";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		List<Map<String, Object>> actionTypes = mapper.readValue(json.get("data").toString(), List.class);
		assertTrue(actionTypes.size() > 0);
		for (Map<String, Object> actionType : actionTypes) {
			assertNotNull(actionType.get("id"));
			assertNotNull(actionType.get("name"));
			assertNotNull(actionType.get("json"));
			assertNotNull(actionType.get("createdDate"));
			assertNotNull(actionType.get("updatedDate"));
			
		}
	}
	
	

	@Test
	@Order(15)
	void testUpdateAction_Success() throws Exception {

		ActionTypeEntity typeEntity = actionTypeRepository.findById(1l).get();

		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testUpdateAction3@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);

		UpdateActionReq updateActionReq = new UpdateActionReq();
		updateActionReq.setName("update test Action");

		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(updateActionReq.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(response.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(response.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(response.getTriggerDays())));

		ActionTypeEntity validActionType = actionTypeRepository.findById(3l).get();

		updateActionReq = new UpdateActionReq();
		updateActionReq.setName("update test Action name valid actionType");
		updateActionReq.setActionTypeId(validActionType.getId());
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(updateActionReq.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(updateActionReq.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(response.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(response.getTriggerDays())));
		
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("message", "testMessage");
		updateActionReq.setActionJson(ACTIONJSON);
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(updateActionReq.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(updateActionReq.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(updateActionReq.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(response.getTriggerDays())));
		
		validActionType = actionTypeRepository.findById(1l).get();

		updateActionReq = new UpdateActionReq();
		updateActionReq.setName("update test Action name valid actionType");
		updateActionReq.setActionTypeId(validActionType.getId());
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(updateActionReq.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(updateActionReq.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.triggerDays", is(response.getTriggerDays())));
		
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		updateActionReq.setActionJson(ACTIONJSON);
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(updateActionReq.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(updateActionReq.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.triggerDays", is(response.getTriggerDays())));

	}

	@Test
	@Order(16)
	void testUpdateAction_FailByInvalidActionType() throws Exception {
		ActionTypeEntity typeEntity = new ActionTypeEntity();
		typeEntity.setName("AUTOMATIC_EMAIL");
		typeEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setJson(ACTIONTYPEJSON);
		typeEntity=actionTypeRepository.save(typeEntity);

		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testUpdateAction4@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);

		UpdateActionReq updateActionReq = new UpdateActionReq();
		updateActionReq.setName("update test Action with invali actionTypeId");
		updateActionReq.setActionTypeId(0l);
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	@Order(17)
	void testUpdateAction_FailByInvalidActionId() throws Exception {
		ActionTypeEntity typeEntity = actionTypeRepository.findById(1l).get();

		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testUpdateAction11@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);

		UpdateActionReq updateActionReq = new UpdateActionReq();
		updateActionReq.setName("update test Action with invalid action id");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions/" + 0;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	@Order(18)
	void testUpdateAction_FailByInvalidWorkflowId() throws Exception {
		ActionTypeEntity typeEntity = actionTypeRepository.findById(1l).get();

		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testUpdateAction12@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);

		UpdateActionReq updateActionReq = new UpdateActionReq();
		updateActionReq.setName("update test Action with invalid workflow id");
		URI = "/v1/workflows/" + 0 + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateActionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		
		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setMinimumContactDelay(20);
		
		URI = "/v1/workflows/" + userEntity.getMerchant().getId();
		result=mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		contentAsString = result.getResponse().getContentAsString();
		json = new JSONObject(contentAsString);
		WorkflowReq workflowResponse = mapper.readValue(json.get("data").toString(), WorkflowReq.class);
		
		URI = "/v1/workflows/" +  workflowResponse.getId() + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}
	
	
	
	@Test
	@Order(19)
	void testDeleteAction_Success() throws Exception {

		ActionTypeEntity typeEntity = actionTypeRepository.findById(1l).get();

		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testUpdateAction5@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);

		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		ActionEntity entity=actionRepository.findById(response.getId()).get();
		assertEquals(true, entity.getIsDeleted());
		mvc.perform(MockMvcRequestBuilders.delete(URI).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
				
	}
	
	
	@Test
	@Order(20)
	void testDeleteAction_FaildByInvalidActionIdOrWorkflowId() throws Exception {

		ActionTypeEntity typeEntity = actionTypeRepository.findById(1l).get();

		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testUpdateAction6@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);

		URI = "/v1/workflows/" + 0 + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions/" + 0;
		mvc.perform(MockMvcRequestBuilders.delete(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		
		
		
		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setMinimumContactDelay(20);
		URI = "/v1/workflows/" + userEntity.getMerchant().getId();
		result=mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		contentAsString = result.getResponse().getContentAsString();
		json = new JSONObject(contentAsString);
		WorkflowReq workflowResponse = mapper.readValue(json.get("data").toString(), WorkflowReq.class);
		URI = "/v1/workflows/" + workflowResponse.getId() + "/actions/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	
	
	
	
	@Test
	@Order(21)
	void testGetWorkFlowById_Success() throws Exception {

		ActionTypeEntity typeEntity = new ActionTypeEntity();
		typeEntity.setName("MANUAL_EMAIL");
		typeEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setJson(ACTIONTYPEJSON);
		typeEntity = actionTypeRepository.save(typeEntity);

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow without email");
		request.setMinimumContactDelay(20);
		String URI = "/v1/workflows/" + merchant.getId();
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(request.getMinimumContactDelay())))
				.andExpect(jsonPath("$.data.email", is(merchant.getEmail()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WorkflowReq workflowReqResponse = mapper.readValue(json.get("data").toString(), WorkflowReq.class);

		URI = "/v1/workflow/" + workflowReqResponse.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowReqResponse.getId() + "/actions";
		result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		contentAsString = result.getResponse().getContentAsString();
		json = new JSONObject(contentAsString);
		ActionReq response = mapper.readValue(json.get("data").toString(), ActionReq.class);
		URI = "/v1/workflow/" + workflowReqResponse.getId();
		result = mvc
				.perform(MockMvcRequestBuilders.get(URI)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		contentAsString = result.getResponse().getContentAsString();
		json = new JSONObject(contentAsString);
		Map<String, Object> workflowResponse = mapper.readValue(json.get("data").toString(), Map.class);
		assertNotNull(workflowResponse.get("id"));
		assertNotNull(workflowResponse.get("name"));
		assertNotNull(workflowResponse.get("email"));
		assertNotNull(workflowResponse.get("defaultWorkflow"));
		assertNotNull(workflowResponse.get("minimumContactDelay"));
		assertNotNull(workflowResponse.get("createdDate"));
		assertNotNull(workflowResponse.get("updatedDate"));
		assertNotNull(workflowResponse.get("actions"));
		List<Map<String, Object>> actions = (List<Map<String, Object>>) workflowResponse.get("actions");
		for (Map<String, Object> action : actions) {

			assertNotNull(action.get("id"));
			assertNotNull(action.get("name"));
			assertNotNull(action.get("triggerDays"));
			assertNotNull(action.get("actionJson"));
			assertNotNull(action.get("triggerDays"));
			
		}

	}
	
	@Test
	@Order(22)
	void testGetWorkFlowById_FaildByInvalidWorkflowId() throws Exception {

		String URI = "/v1/workflow/" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		

	}
	
	@Test
	@Order(23)
	void testGetActionTypeWithInvalidJson() throws Exception {
		ActionTypeEntity typeEntity = new ActionTypeEntity();
		typeEntity.setName("invalid Type");
		typeEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setJson("test Json");
		typeEntity = actionTypeRepository.save(typeEntity);
		String URI = "/v1/actionTypes";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		List<Map<String, Object>> actionTypes = mapper.readValue(json.get("data").toString(), List.class);
		assertTrue(actionTypes.size() > 0);
		for (Map<String, Object> actionType : actionTypes) {
			if(actionType.get("name") != null && actionType.get("name").toString().equals("invalid Type")) {
				assertNull(actionType.get("json"));
			}
			
		}
	}
	
	
	@Test
	@Order(24)
	void testCreateWorkFlow_FailByInvalidMinmumContactDelay() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test workFlow merchant for invalid contact delay");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow for invalid delay");
		request.setMinimumContactDelay(-20);
		String URI = "/v1/workflows/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		

	}
	
	
	
	@Test
	@Order(25)
	void testUpdateWorkFlow_FaildByInvalidMinimumContactDelay() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test update workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setEmail("testworkflow@gmail.com");
		String URI = "/v1/workflows/" + merchant.getId();
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(5)))
				.andExpect(jsonPath("$.data.email", is(request.getEmail()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WorkflowReq response = mapper.readValue(json.get("data").toString(), WorkflowReq.class);
		WorkflowUpdateReq updateRequest = new WorkflowUpdateReq();
		updateRequest.setEmail("testUpdate@test.com");
		updateRequest.setMinimumContactDelay(-20);
		URI = "/v1/workflows/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
		
		updateRequest = new WorkflowUpdateReq();
		updateRequest.setEmail("testUpdate@test.com");
		updateRequest.setMinimumContactDelay(0);
		URI = "/v1/workflows/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
				
	}
	
	
	@Test
	@Order(26)
	void testCreateAction_FaildByInvalidActionJson() throws Exception {
		
		
		ActionTypeEntity typeEntity = actionTypeRepository.findById(1l).get();
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testInvalidActionJson@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		
		req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("", "testRecipients");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}
	
	
	
	@Test
	@Order(27)
	void testCreateAction_SuccessWithValidActionJson() throws Exception {
		
		
		ActionTypeEntity typeEntity = new ActionTypeEntity();
		typeEntity.setName("Telephone call");
		typeEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		typeEntity.setJson(ACTIONTYPEJSON);
		typeEntity = actionTypeRepository.save(typeEntity);
		
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testActionValidActionJson@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		ACTIONJSON =new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "test callScript");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
	mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(req.getName())))
				.andExpect(jsonPath("$.data.actionTypeId", is(Integer.valueOf(req.getActionTypeId().toString()))))
				.andExpect(jsonPath("$.data.actionJson", is(req.getActionJson())))
				.andExpect(jsonPath("$.data.triggerDays", is(req.getTriggerDays()))).andReturn();

		
	}
	
	
	
	
	@Test
	@Order(28)
	void testApplyWorkFlowToAllClient_Success() throws Exception {
		
		
		
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
		request.setVatNumber("123fdf45789");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setDefaultPaymentDays(13);

		String URI = "/v1/merchants/" + merchant.getId() + "/clients";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())));
		
		
		URI = "/v1/workflows/" + workflowEntity.getId() + "/merchants/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		URI = "/v1/merchants/" + merchant.getId() + "/clients";
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		List<Map<String, Object>>responseMap = mapper.readValue(json.get("data").toString(), List.class);
		for (Map<String, Object> client : responseMap) {
			assertNotNull(client.get("workflowId"));
		}
	}
	
	
	
	
	@Test
	@Order(29)
	void testApplyWorkFlowToAllClient_FaildById() throws Exception {
		
		
		
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
		
		
		
		String URI = "/v1/workflows/" + workflowEntity.getId() + "/merchants/" + 0;
		mvc.perform(MockMvcRequestBuilders.post(URI)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		
		URI = "/v1/workflows/" + 0 + "/merchants/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	
	@Test
	@Order(30)
	void testDeleteWorkFlow_FailAlreadyExists() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test update workFlow merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant.setEmail("testMerchant@gmail.com");
		merchant = merchantRepository.save(merchant);

		WorkflowReq request = new WorkflowReq();
		request.setName("test workflow");
		request.setEmail("testworkflow@gmail.com");
		String URI = "/v1/workflows/" + merchant.getId();
		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.name", is(request.getName())))
				.andExpect(jsonPath("$.data.defaultWorkflow", is(false)))
				.andExpect(jsonPath("$.data.minimumContactDelay", is(5)))
				.andExpect(jsonPath("$.data.email", is(request.getEmail()))).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WorkflowReq response = mapper.readValue(json.get("data").toString(), WorkflowReq.class);
		
		WorkflowEntity workflowEntity = workflowRepository.findByIdAndIsDeleted(response.getId(), false);
		
		ClientEntity clientEntity = new ClientEntity();
		clientEntity.setName("test Client for externelRef");
		clientEntity.setVatNumber("332146776");
		clientEntity.setEmail("test@gmail.com");
		clientEntity.setAddress("Italy");
		clientEntity.setTelephone("6673382");
		clientEntity.setDeleted(false);
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setWorkflowEntity(workflowEntity);
		clientEntity= clientRepository.save(clientEntity);
		URI = "/v1/workflows/" + response.getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI).content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
		
		
	}
	
}
