package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.ActionEntity;
import net.bflows.pagafatture.entities.ActionTypeEntity;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.ContactsEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.TimelineEntity;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.InvoiceUpdateReq;
import net.bflows.pagafatture.model.TimelineReq;
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
class ActionApiControllerTest {

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
	private ActionTypeRepository actionTypeRepository;

	@Autowired
	private ActionRepository actionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TimelineRepository timelineRepository;
	
	/*@Autowired
	private ActionScheduler actionScheduler;*/

	private List<String> recipients = Arrays.asList("recipients@test.com");
	private List<String> BCC = Arrays.asList("BCC@test.com");
	private List<String> CC = Arrays.asList("CC@gmail.com");
	private String sender = "sender@gmail.com";

	@Test
	@Order(1)
	void testPerformAction_Success() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("cdfj6dcss67");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "testMessage");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222w32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(false);
		timelineReq.setReschedule(false);
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(2)
	void testPerformAction_Faild() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("cdfj6dcss67");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "testMessage");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222w32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		timelineReq = new TimelineReq();
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		client.setDeleted(true);
		client = clientRepository.save(client);
		timelineReq = new TimelineReq();
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setClientId(client.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		client.setDeleted(false);
		client = clientRepository.save(client);
		timelineReq = new TimelineReq();
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setClientId(client.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + 0;
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		timelineReq = new TimelineReq();
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setClientId(0l);
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		timelineReq = new TimelineReq();
		timelineReq.setInvoiceId(0l);
		timelineReq.setClientId(client.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		workflowEntity = new WorkflowEntity();

		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("cd324fj6dcss67");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		timelineReq = new TimelineReq();
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setClientId(client.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

/*	@Test
	@Order(3)
	void testPerformAction_ForAutoMaticEmail() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client");
		client.setVatNumber("eer45");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(2l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("12001");
		invoice.setExternalId("100");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(false);
		timelineReq.setReschedule(false);
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.data.status", is("SUCCESS")));
	}*/

	@Test
	@Order(4)
	void testPerformAction_ForManualEmail() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client");
		client.setVatNumber("ccc332cs7");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(1l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("11w212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(5)
	void testPerformAction_FaildByInvalidEmails() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client");
		client.setVatNumber("eer45");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(1l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("12001");
		invoice.setExternalId("100");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	@Test
	@Order(6)
	void testGetNextActionsByMerchantId_FaildByMerchantId() throws Exception {

		String URI = "/v1/actions/"+ 0 ;
		 mvc.perform(MockMvcRequestBuilders.get(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	

	}
	
	@Test
	@Order(7)
	void testGetNextActionsByMerchantId_Success() throws Exception {

		ActionTypeEntity typeEntity =  actionTypeRepository.findById(4l).get();

		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testNextAction@test.com");
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
		ActionReq req = new ActionReq();
		req.setTriggerDays(5);
		req.setActionTypeId(typeEntity.getId());
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "test callScript");
		req.setActionJson(ACTIONJSON);
		req.setName("test Action");
		
		URI = "/v1/workflows/" + workflowEntity.getId() + "/actions";
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(req))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		
		// With empty client
		
		URI = "/v1/actions/"+ userEntity.getMerchant().getId() ;
		mvc.perform(MockMvcRequestBuilders.get(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
				
		ClientEntity clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("121ed566");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);
				
		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222w32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222w32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(req.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		
	
		
		clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("121ed566");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222w32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(req.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();
		
		ActionEntity actionEntity = new ActionEntity();
		ACTIONJSON = new HashMap<>();
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
		
		actionEntity = new ActionEntity();
		actionEntity.setActionJson(newJson);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(8);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);
		
		TimelineEntity timelineEntity = new TimelineEntity();
		timelineEntity.setActionEntity(actionEntity);
		timelineEntity.setSkip(false);
		timelineEntity.setReschedule(false);
		timelineEntity.setClientEntity(clientEntity1);
		timelineEntity.setInvoice(invoice);
		timelineEntity.setMessage("test message");
		timelineEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineRepository.save(timelineEntity);
		
		clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("646rty4");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1 = clientRepository.save(clientEntity1);

		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("21342");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(10));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		
		
		clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("133566");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("21212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(10));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		timelineEntity = new TimelineEntity();
		timelineEntity.setActionEntity(actionEntity);
		timelineEntity.setSkip(false);
		timelineEntity.setReschedule(false);
		timelineEntity.setClientEntity(clientEntity1);
		timelineEntity.setInvoice(invoice);
		timelineEntity.setMessage("test message");
		timelineEntity.setCreatedDate(LocalDateTime.now().minusDays(25));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineRepository.save(timelineEntity);
		
		
		clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("4435");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("7789");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(10));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("4435");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("7789");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(10));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("7789");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(10));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		
		
		clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("55263");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("77789");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(2));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		invoice = invoicesRepository.save(invoice);
		

		actionEntity = new ActionEntity();
		actionEntity.setActionJson(newJson);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(1);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);
		
		actionEntity = new ActionEntity();
		actionEntity.setActionJson(newJson);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(2);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);
		
		
		clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("66789");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("10055");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(2));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("10055");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(3));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice.setCurrency("EUR");
		
		invoice = invoicesRepository.save(invoice);
		
		typeEntity =  actionTypeRepository.findById(1l).get();
		actionEntity = new ActionEntity();
		actionEntity.setActionJson(newJson);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(8);
		actionEntity.setActionType(typeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);
		
		URI = "/v1/actions/"+ userEntity.getMerchant().getId() ;
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		List<Map<String, Object>> data = mapper.readValue(json.get("data").toString(), List.class);

		
		for (Map<String, Object> action : data) {

			assertNotNull(action.get("name"));
			assertNotNull(action.get("clientId"));
			assertNotNull(action.get("invoiceId"));
			assertNotNull(action.get("nextAction"));
			assertNotNull(action.get("invoiceAmount"));
			assertNotNull(action.get("currency"));
			
		}
	}
	
	@Test
	@Order(8)
	void testGetPerformedActionByClientId_Success() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("cdfj6dcss67");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "testMessage");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String jsonString = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(jsonString);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222wqc32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		URI = "/v1/actions/clients/" + client.getId();
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		List<Map<String, Object>> response = mapper.readValue(json.get("data").toString(), List.class);
		for (Map<String, Object> map : response) {
			assertNotNull(map.get("id"));
			assertNotNull(map.get("skip"));
			assertNotNull(map.get("reschedule"));
			assertNotNull(map.get("status"));
			assertNotNull(map.get("message"));
			assertNotNull(map.get("clientId"));
			assertNotNull(map.get("invoiceId"));
			assertNotNull(map.get("actionId"));
			assertNotNull(map.get("createdDate"));
			assertNotNull(map.get("updatedDate"));
			
		}

	}

	@Test
	@Order(9)
	void testGetPerformedActionByClientId_FaildByClientId() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("cdf4456j6dcss67");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "testMessage");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String jsonString = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(jsonString);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222qw23wqc32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		URI = "/v1/actions/clients/" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

		client.setDeleted(true);
		client = clientRepository.save(client);
		URI = "/v1/actions/clients/" + client.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	
	
	@Test
	@Order(10)
	void testGetPerformedActionByInvoiceId_Success() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345bbnh678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		WorkflowEntity workflowEntity = new WorkflowEntity();
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("eertfg34");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "testMessage");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String jsonString = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(jsonString);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("2wqc32212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		
		
		
		
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		InvoiceUpdateReq updateInovice = new InvoiceUpdateReq();
	
		updateInovice.setInvoiceState(InvoiceStateEnum.SENT);
		
		URI = "/v1/invoices/" + invoice.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateInovice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
				
		
		URI = "/v1/actions/invoices/" + invoice.getId();
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		List<Map<String, Object>> response = mapper.readValue(json.get("data").toString(), List.class);
		for (Map<String, Object> map : response) {
			assertNotNull(map.get("id"));
			assertNotNull(map.get("status"));
			assertNotNull(map.get("clientId"));
			assertNotNull(map.get("invoiceId"));
			assertNotNull(map.get("createdDate"));
			assertNotNull(map.get("updatedDate"));

		}

	}

	@Test
	@Order(11)
	void testGetPerformedActionByInvoiceId_FaildByInvoiceId() throws Exception {

		String URI = "/v1/actions/invoices/" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

	}
	
	/*@Test
	@Order(12)
	void testInvoiceScheduler() throws Exception {
		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(2l).get();

		Map<String, Object> ACTIONJSON=new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("test23Action@test.com");
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

		ClientEntity clientEntity1 = new ClientEntity();
		clientEntity1.setVatNumber("121e6");
		clientEntity1.setName("Test Client");
		clientEntity1.setMerchantEntity(userEntity.getMerchant());
		clientEntity1.setDeleted(false);
		clientEntity1.setWorkflowEntity(workflowEntity);
		clientEntity1 = clientRepository.save(clientEntity1);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("222212");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity1);
		invoice = invoicesRepository.save(invoice);

		ActionEntity actionEntity = new ActionEntity();
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);
		actionEntity.setActionJson(Utils.convertMapIntoJsonString(ACTIONJSON));
		actionScheduler.sendAutometicEmail();
		actionRepository.delete(actionEntity);
		

		actionEntity = new ActionEntity();
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setActionJson(Utils.convertMapIntoJsonString(ACTIONJSON));
		actionEntity = actionRepository.save(actionEntity);
		actionScheduler.sendAutometicEmail();
		
		
		actionEntity = new ActionEntity();
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);
		actionScheduler.sendAutometicEmail();
		actionRepository.delete(actionEntity);
	}*/
	
	
	@Test
	@Order(13)
	void testPerformAction_WithInvalidActionJson() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("112390");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(4l).get();

		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("callScript", "testMessage");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("5543q1");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setReschedule(true);
		timelineReq.setSkip(false);
		
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setReschedule(false);
		timelineReq.setSkip(true);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setReschedule(true);
		timelineReq.setSkip(true);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(true);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(true);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		

		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setReschedule(false);
		timelineReq.setSkip(false);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	
	
	@Test
	@Order(14)
	void testPerformAction_WithInvalidEmailKeys() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("3321p0");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);

		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(1l).get();

		ActionEntity actionEntity = new ActionEntity();
		
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("p001");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(false);
		timelineReq.setReschedule(false);
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		
		ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients@test.com");
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(false);
		timelineReq.setReschedule(false);
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("BCC", "testBCC@test.test");
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		timelineReq.setActionJson(ACTIONJSON);
		
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(false);
		timelineReq.setReschedule(false);
		ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", recipients);
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("CC", "testCC@test.test");
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(false);
		timelineReq.setReschedule(false);
		ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", "testrecipient@test.test");
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setSkip(false);
		timelineReq.setReschedule(false);
		ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("BCC", BCC);
		ACTIONJSON.put("CC", CC);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		timelineReq.setActionJson(ACTIONJSON);
		URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		
	}
	
	
	@Test
	@Order(15)
	void testPerformAction_SuccessWithManualEmail() throws Exception {
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
		workflowEntity.setName("test workflow");
		workflowEntity.setEmail("test@test.com");
		workflowEntity.setDefaultWorkflow(true);
		workflowEntity.setIsDeleted(false);
		workflowEntity.setMerchantEntity(merchant);
		workflowEntity.setMinimumContactDelay(10);
		workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowEntity = workflowRepository.save(workflowEntity);

		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("pop90");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(merchant);
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);
		
		ContactsEntity recipientsContacts = new ContactsEntity();
		recipientsContacts.setEmail("recipients@test.com");
		
		ContactsEntity ccContacts = new ContactsEntity();
		ccContacts.setEmail("CC@gmail.com");
		
		ContactsEntity bccContacts = new ContactsEntity();
		bccContacts.setEmail("BCC@test.com");
		
        List<ContactsEntity> recipientsList = Arrays.asList(recipientsContacts);
        List<ContactsEntity> ccContactsList = Arrays.asList(ccContacts);
        List<ContactsEntity> bccContactsList = Arrays.asList(bccContacts);
		ActionTypeEntity actionTypeEntity = actionTypeRepository.findById(1l).get();
		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON = new HashMap<>();
		ACTIONJSON.put("recipients", recipientsList);
		ACTIONJSON.put("BCC", bccContactsList);
		ACTIONJSON.put("CC", ccContactsList);
		ACTIONJSON.put("subject", "test subject");
		ACTIONJSON.put("message", "test Message body");
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		String json = Utils.convertMapIntoJsonString(ACTIONJSON);
		actionEntity.setActionJson(json);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setTriggerDays(5);
		actionEntity.setActionType(actionTypeEntity);
		actionEntity.setIsDeleted(false);
		actionEntity.setName("test action");
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);

		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("3432p");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now().minusDays(actionEntity.getTriggerDays()));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);

		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setClientId(client.getId());
		timelineReq.setInvoiceId(invoice.getId());
		timelineReq.setMessage("test message");
		timelineReq.setActionJson(ACTIONJSON);
		String URI = "/v1/actions/" + actionEntity.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(timelineReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

	}
	
	
	@Test
	@Order(16)
	void testIllegalTimelineEntityTypeEnum() throws Exception {
		
		WorkflowEntity workflowEntity = workflowRepository.findByIdAndIsDeleted(1l, false);
		
		ClientEntity client = new ClientEntity();
		client.setName("test Client for externelRef");
		client.setVatNumber("pop90");
		client.setEmail("test@gmail.com");
		client.setAddress("Italy");
		client.setTelephone("6682");
		client.setDeleted(false);
		client.setMerchantEntity(workflowEntity.getMerchantEntity());
		client.setWorkflowEntity(workflowEntity);
		client = clientRepository.save(client);
		
		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(755));
		invoice.setAmountNet(new BigDecimal(755));
		invoice.setExternalRef("3432p");
		invoice.setExternalId("AWS457744");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice = invoicesRepository.save(invoice);
		
		TimelineEntity timelineEntity = new TimelineEntity();
		timelineEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setClientEntity(invoice.getClientEntity());
		timelineEntity.setInvoice(invoice);
		timelineEntity.setStatus("SUCCESS");
		timelineEntity.setType("action");
		timelineEntity = timelineRepository.save(timelineEntity);
		String URI = "/v1/actions/clients/" + client.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
	}
	
}
