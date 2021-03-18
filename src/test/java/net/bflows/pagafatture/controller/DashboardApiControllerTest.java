package net.bflows.pagafatture.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.entities.ActionEntity;
import net.bflows.pagafatture.entities.ActionTypeEntity;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.enums.DashboardEnums;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.ActionWidget;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.widget.InvoicesWithAverageDaysMonthWise;
import net.bflows.pagafatture.model.widget.InvoicesWithAverageDaysWidget;
import net.bflows.pagafatture.model.widget.TopUnpaidInvoiceBean;
import net.bflows.pagafatture.model.widget.TopUnpaidInvoicesWidgets;
import net.bflows.pagafatture.model.widget.TotalDueAndOverDueInvoicesWidget;
import net.bflows.pagafatture.model.widget.TotalDueAndOverDueInvoicesWithRange;
import net.bflows.pagafatture.model.widget.Widget;
import net.bflows.pagafatture.model.widget.WidgetsResponseBean;
import net.bflows.pagafatture.repositories.ActionRepository;
import net.bflows.pagafatture.repositories.ActionTypeRepository;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.TransactionRepository;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.repositories.WorkflowRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Utils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class DashboardApiControllerTest {


	@Autowired
	private MockMvc mvc;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private MerchantRepository merchantRepository;
	
	@Autowired
	private InvoicesRepository invoicesRepository;
	
	@Autowired
	private WorkflowRepository workflowRepository;
	
	@Autowired
	private ActionRepository actionRepository;
	
	@Autowired
	private UserRepository userRepository;
	

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private ActionTypeRepository actionTypeRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Test
	@Order(1)
	void testGetWidgetsByMerchantId() throws Exception {

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant for widget");
		merchantEntity = merchantRepository.save(merchantEntity);

		

		ClientEntity client = new ClientEntity();
		Invoices invoice = new Invoices();
		client.setVatNumber("12345");
		client.setName("Test Client");
		client.setMerchantEntity(merchantEntity);
		client.setDeleted(false);
		client = clientRepository.save(client);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("eerddtf5321d");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().plusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice.setExternalCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);
		
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("APP457744");
		invoice.setExternalRef("eerddtf5321d");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().plusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice.setExternalCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);

		
		Invoices invoice1 = new Invoices();
		invoice1.setAmountGross(new BigDecimal(2200));
		invoice1.setAmountNet(new BigDecimal(2500));
		invoice1.setCurrency("INR");
		invoice1.setExternalId("AWS457744");
		invoice1.setExternalRef("eerddtf532");
		invoice1.setInvoiceNumber("1001");
		invoice1.setLinkDoc("www.linkdoc.com");
		invoice1.setDueDate(LocalDateTime.now().minusDays(5));
		invoice1.setExpectedDate(LocalDateTime.now());
		invoice1.setClientEntity(client);
		invoice1.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice1.setCreatedDate(LocalDateTime.now());
		invoice1.setExternalCreatedDate(LocalDateTime.now());
		invoice1 = invoicesRepository.save(invoice1);

		
		
		Invoices invoice2 = new Invoices();
		invoice2.setAmountGross(new BigDecimal(2200));
		invoice2.setAmountNet(new BigDecimal(2500));
		invoice2.setCurrency("INR");
		invoice2.setExternalId("AWS457744");
		invoice2.setExternalRef("ffgvvbbt5");
		invoice2.setInvoiceNumber("1001");
		invoice2.setLinkDoc("www.linkdoc.com");
		invoice2.setDueDate(LocalDateTime.now().plusDays(40));
		invoice2.setExpectedDate(LocalDateTime.now());
		invoice2.setClientEntity(client);
		invoice2.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice2.setCreatedDate(LocalDateTime.now());
		invoice2.setExternalCreatedDate(LocalDateTime.now());
		invoice2 = invoicesRepository.save(invoice2);

		
		Invoices invoice3 = new Invoices();
		invoice3.setAmountGross(new BigDecimal(2200));
		invoice3.setAmountNet(new BigDecimal(2500));
		invoice3.setCurrency("INR");
		invoice3.setExternalId("AWS457744");
		invoice3.setExternalRef("887weds");
		invoice3.setInvoiceNumber("1001");
		invoice3.setLinkDoc("www.linkdoc.com");
		invoice3.setDueDate(LocalDateTime.now().minusDays(40));
		invoice3.setExpectedDate(LocalDateTime.now());
		invoice3.setClientEntity(client);
		invoice3.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice3.setCreatedDate(LocalDateTime.now());
		invoice3.setExternalCreatedDate(LocalDateTime.now());
		invoice3 = invoicesRepository.save(invoice3);
		
		Invoices invoice4 = new Invoices();
		invoice4.setAmountGross(new BigDecimal(2200));
		invoice4.setAmountNet(new BigDecimal(2500));
		invoice4.setCurrency("INR");
		invoice4.setExternalId("AWS457744");
		invoice4.setExternalRef("3322eert5");
		invoice4.setInvoiceNumber("1001");
		invoice4.setLinkDoc("www.linkdoc.com");
		invoice4.setDueDate(LocalDateTime.now().minusDays(80));
		invoice4.setExpectedDate(LocalDateTime.now());
		invoice4.setClientEntity(client);
		invoice4.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice4.setCreatedDate(LocalDateTime.now());
		invoice4.setExternalCreatedDate(LocalDateTime.now());
		invoice4 = invoicesRepository.save(invoice4);
		
		
		
		Invoices invoice5 = new Invoices();
		invoice5.setAmountGross(new BigDecimal(2200));
		invoice5.setAmountNet(new BigDecimal(2500));
		invoice5.setCurrency("INR");
		invoice5.setExternalId("AWS457744");
		invoice5.setExternalRef("eerd1dtf532");
		invoice5.setInvoiceNumber("1001");
		invoice5.setLinkDoc("www.linkdoc.com");
		invoice5.setDueDate(LocalDateTime.now().plusDays(80));
		invoice5.setExpectedDate(LocalDateTime.now());
		invoice5.setClientEntity(client);
		invoice5.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice5.setCreatedDate(LocalDateTime.now());
		invoice5.setExternalCreatedDate(LocalDateTime.now());
		invoice5 = invoicesRepository.save(invoice5);

		
		
		Invoices invoice6 = new Invoices();
		invoice6.setAmountGross(new BigDecimal(2200));
		invoice6.setAmountNet(new BigDecimal(2500));
		invoice6.setCurrency("INR");
		invoice6.setExternalId("AWS457744");
		invoice6.setExternalRef("ffgvvb4bt5");
		invoice6.setInvoiceNumber("1001");
		invoice6.setLinkDoc("www.linkdoc.com");
		invoice6.setDueDate(LocalDateTime.now().plusDays(95));
		invoice6.setExpectedDate(LocalDateTime.now());
		invoice6.setClientEntity(client);
		invoice6.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice6.setCreatedDate(LocalDateTime.now());
		invoice6.setExternalCreatedDate(LocalDateTime.now());
		invoice6 = invoicesRepository.save(invoice6);

		
		Invoices invoice7 = new Invoices();
		invoice7.setAmountGross(new BigDecimal(2200));
		invoice7.setAmountNet(new BigDecimal(2500));
		invoice7.setCurrency("INR");
		invoice7.setExternalId("AWS457744");
		invoice7.setExternalRef("887wed1s");
		invoice7.setInvoiceNumber("1001");
		invoice7.setLinkDoc("www.linkdoc.com");
		invoice7.setDueDate(LocalDateTime.now().minusDays(95));
		invoice7.setExpectedDate(LocalDateTime.now());
		invoice7.setClientEntity(client);
		invoice7.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice7.setCreatedDate(LocalDateTime.now());
		invoice7.setExternalCreatedDate(LocalDateTime.now());
		invoice7 = invoicesRepository.save(invoice7);
		
		Invoices invoice8 = new Invoices();
		invoice8.setAmountGross(new BigDecimal(2200));
		invoice8.setAmountNet(new BigDecimal(2500));
		invoice8.setCurrency("INR");
		invoice8.setExternalId("AWS457744");
		invoice8.setExternalRef("3322eert51");
		invoice8.setInvoiceNumber("1001");
		invoice8.setLinkDoc("www.linkdoc.com");
		invoice8.setDueDate(LocalDateTime.now());
		invoice8.setExpectedDate(LocalDateTime.now());
		invoice8.setClientEntity(client);
		invoice8.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoice8.setCreatedDate(LocalDateTime.now());
		invoice8.setExternalCreatedDate(LocalDateTime.now().minusMonths(1));
		invoice8 = invoicesRepository.save(invoice8);

		//for MAIN_DEBTORS nOverdueDays>1
		Invoices invoice9 = new Invoices();
		invoice9.setAmountGross(new BigDecimal(25000));
		invoice9.setAmountNet(new BigDecimal(25000));
		invoice9.setCurrency("INR");
		invoice9.setExternalId("AWS457744");
		invoice9.setExternalRef("65ttyy64");
		invoice9.setInvoiceNumber("1001");
		invoice9.setLinkDoc("www.linkdoc.com");
		invoice9.setDueDate(LocalDateTime.now().minusDays(55));
		invoice9.setExpectedDate(LocalDateTime.now());
		invoice9.setClientEntity(client);
		invoice9.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice9.setCreatedDate(LocalDateTime.now());
		invoice9.setExternalCreatedDate(LocalDateTime.now());
		invoice9 = invoicesRepository.save(invoice9);
		
		
		Invoices invoice10 = new Invoices();
		invoice10.setAmountGross(new BigDecimal(2200));
		invoice10.setAmountNet(new BigDecimal(2500));
		invoice10.setCurrency("INR");
		invoice10.setExternalId("AWS457744");
		invoice10.setExternalRef("P16P");
		invoice10.setInvoiceNumber("1001");
		invoice10.setLinkDoc("www.linkdoc.com");
		invoice10.setDueDate(LocalDateTime.now().minusMonths(1));
		invoice10.setExpectedDate(LocalDateTime.now());
		invoice10.setClientEntity(client);
		invoice10.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoice10.setCreatedDate(LocalDateTime.now().minusMonths(1));
		invoice10.setExternalCreatedDate(LocalDateTime.now().minusMonths(1));
		invoice10 = invoicesRepository.save(invoice10);
		
		Long id = merchantEntity.getId();
		
		
		

		ObjectMapper mapper = new ObjectMapper();
		String URI = "/v1/widgets/" + id;
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WidgetsResponseBean responseBean = mapper.readValue(json.get("data").toString(), WidgetsResponseBean.class);
			 Widget widgets =responseBean.getTotalUnpaid();
				if (widgets != null) {

					if(widgets.getDue() != null && widgets.getOverDue() != null) {
					int dueRes = widgets.getDue().compareTo(new BigDecimal(0));
					int dueOverRes = widgets.getOverDue().compareTo(new BigDecimal(0));
					Boolean isNotNull = false;
					if (dueRes >= 0 && dueOverRes >= 0) {
						isNotNull = true;
					}
					assertEquals(true, isNotNull);
				    assertEquals(widgets.getTotal(), widgets.getDue().add(widgets.getOverDue()));
					}
					assertEquals("totalUnpaid", widgets.getTitle());

				}
				 TopUnpaidInvoicesWidgets topUnpaidInvoicesWidgets =responseBean.getMainDebtors();
				if (topUnpaidInvoicesWidgets != null) {
					assertEquals(DashboardEnums.MAIN_DEBTORS.getValue(),topUnpaidInvoicesWidgets.getTitle());
				List<TopUnpaidInvoiceBean> data	=topUnpaidInvoicesWidgets.getData();
				if(!CollectionUtils.isEmpty(data)) {
					assertTrue(data.get(0).getNOverdueDays()>1);
				}
				}

				TotalDueAndOverDueInvoicesWidget totalDueAndOverDueInvoicesWidget=responseBean.getAgingBalance();
				if (totalDueAndOverDueInvoicesWidget != null) {

					List<TotalDueAndOverDueInvoicesWithRange> dataMapList =totalDueAndOverDueInvoicesWidget.getData();
					Double totalDue = 0.0, totalOverdue = 0.0;
					if (!CollectionUtils.isEmpty(dataMapList)) {
						for (TotalDueAndOverDueInvoicesWithRange map1 : dataMapList) {
							if(map1.getOverdueAmount() != null) {
								totalOverdue = totalOverdue + map1.getOverdueAmount();
							}
							if(map1.getDueAmount() != null) {
								totalDue = totalDue + new Double(map1.getDueAmount());
							}
							
							
						}

						assertEquals(totalDueAndOverDueInvoicesWidget.getOverdueAmount(), totalOverdue);
						assertEquals(totalDueAndOverDueInvoicesWidget.getDueAmount(), totalDue);
						assertEquals(DashboardEnums.AGING_BALANCE.getValue(), totalDueAndOverDueInvoicesWidget.getTitle());
						
					}
				}
                   InvoicesWithAverageDaysWidget averageDaysWidget=responseBean.getAverageDays();
				if (averageDaysWidget != null) {

					List<InvoicesWithAverageDaysMonthWise> dataMapList = averageDaysWidget.getData();
					Long totalAvg = 0l;
					if (!CollectionUtils.isEmpty(dataMapList)) {
						for (InvoicesWithAverageDaysMonthWise map1 : dataMapList) {
							totalAvg = totalAvg +  map1.getNDays();
						}
					}

					assertEquals(averageDaysWidget.getAverageDays(), totalAvg);
					assertEquals(DashboardEnums.AVERAGE_DAYS.getValue(), averageDaysWidget.getTitle());

				}
			
		

	}

	
	
	@Test
	@Order(2)
	void testGetWidgetsForNewMerchantId() throws Exception {

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant for widget");
		merchantEntity = merchantRepository.save(merchantEntity);
		Long id = merchantEntity.getId();
		
		
		

		ObjectMapper mapper = new ObjectMapper();
		String URI = "/v1/widgets/" + id;
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WidgetsResponseBean responseBean = mapper.readValue(json.get("data").toString(), WidgetsResponseBean.class);
			 Widget widgets =responseBean.getTotalUnpaid();
				if (widgets != null) {

					if(widgets.getDue() != null && widgets.getOverDue() != null) {
					int dueRes = widgets.getDue().compareTo(new BigDecimal(0));
					int dueOverRes = widgets.getOverDue().compareTo(new BigDecimal(0));
					Boolean isNotNull = false;
					if (dueRes >= 0 && dueOverRes >= 0) {
						isNotNull = true;
					}
					assertEquals(true, isNotNull);
				    assertEquals(widgets.getTotal(), widgets.getDue().add(widgets.getOverDue()));
					}
					assertEquals("totalUnpaid", widgets.getTitle());

				}
				 TopUnpaidInvoicesWidgets topUnpaidInvoicesWidgets =responseBean.getMainDebtors();
				if (topUnpaidInvoicesWidgets != null) {
					assertEquals(DashboardEnums.MAIN_DEBTORS.getValue(),topUnpaidInvoicesWidgets.getTitle());
				}

				TotalDueAndOverDueInvoicesWidget totalDueAndOverDueInvoicesWidget=responseBean.getAgingBalance();
				if (totalDueAndOverDueInvoicesWidget != null) {

					List<TotalDueAndOverDueInvoicesWithRange> dataMapList =totalDueAndOverDueInvoicesWidget.getData();
					Double totalDue = 0.0, totalOverdue = 0.0;
					if (!CollectionUtils.isEmpty(dataMapList)) {
						for (TotalDueAndOverDueInvoicesWithRange map1 : dataMapList) {
							if(map1.getOverdueAmount() != null) {
								totalOverdue = totalOverdue + map1.getOverdueAmount();
							}
							if(map1.getDueAmount() != null) {
								totalDue = totalDue + new Double(map1.getDueAmount());
							}
							
							
						}

						assertEquals(totalDueAndOverDueInvoicesWidget.getOverdueAmount(), totalOverdue);
						assertEquals(totalDueAndOverDueInvoicesWidget.getDueAmount(), totalDue);
						assertEquals(DashboardEnums.AGING_BALANCE.getValue(), totalDueAndOverDueInvoicesWidget.getTitle());
						
					}
				}
                   InvoicesWithAverageDaysWidget averageDaysWidget=responseBean.getAverageDays();
				if (averageDaysWidget != null) {

					List<InvoicesWithAverageDaysMonthWise> dataMapList = averageDaysWidget.getData();
					Long totalAvg = 0l;
					if (!CollectionUtils.isEmpty(dataMapList)) {
						for (InvoicesWithAverageDaysMonthWise map1 : dataMapList) {
							totalAvg = totalAvg +  map1.getNDays();
						}
					}

					assertEquals(averageDaysWidget.getAverageDays(), totalAvg);
					assertEquals(DashboardEnums.AVERAGE_DAYS.getValue(), averageDaysWidget.getTitle());

				}
			
		assertNull(Utils.formatDecimal(null));

	}

	@Test
	@Order(3)
	void testGetWidgetsByMerchantIdForActionWidget() throws Exception {

		
		ActionTypeEntity typeEntity=actionTypeRepository.findById(1l).get();
		
		UserReq user = new UserReq();
		user.setId(1l);
		user.setCompanyName("Test Action comapy");
		user.setConfPassword("test12345678");
		user.setEmail("testActionWidget@test.com");
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
				
		ActionEntity actionEntity = new ActionEntity();
		Map<String, Object> ACTIONJSON =new HashMap<>();
		ACTIONJSON.put("recipients", "testRecipients");
		ACTIONJSON.put("CC", "testCC");
		ACTIONJSON.put("BCC", "testBCC");
		ACTIONJSON.put("subject", "testSubject");
		ACTIONJSON.put("message", "testMessage");
		actionEntity.setActionJson(Utils.convertMapIntoJsonString(ACTIONJSON));
		actionEntity.setActionType(typeEntity);
		actionEntity.setName("test action");
		actionEntity.setTriggerDays(-5);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setIsDeleted(false);
		actionEntity = actionRepository.save(actionEntity);
		
		ClientEntity client = new ClientEntity();
		Invoices invoice = new Invoices();
		client.setVatNumber("12224t345");
		client.setName("Test Client");
		client.setMerchantEntity(userEntity.getMerchant());
		client.setWorkflowEntity(workflowEntity);
		client.setDeleted(false);
		client = clientRepository.save(client);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("eerd2231dtf5321d");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().plusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice.setExternalCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);
		client = new ClientEntity();
		client.setVatNumber("16pp14");
		client.setName("Test Client");
		client.setMerchantEntity(userEntity.getMerchant());
		client.setWorkflowEntity(workflowEntity);
		client.setDeleted(false);
		client = clientRepository.save(client);
		Long id = userEntity.getMerchant().getId();

		
		URI = "/v1/widgets/" + id;
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk()).andReturn();

		String contentAsString = result.getResponse().getContentAsString();
		JSONObject json = new JSONObject(contentAsString);
		WidgetsResponseBean responseBean = mapper.readValue(json.get("data").toString(), WidgetsResponseBean.class);
		ActionWidget actionWidgets = responseBean.getActionWidget();
		if (actionWidgets != null) {

			assertEquals(1, actionWidgets.getNumberOfAction());
			assertEquals(invoice.getAmountGross(), actionWidgets.getTotalAmount());
			assertEquals("actionWidgets", actionWidgets.getTitle());

		}

	}
	
	
	

}
