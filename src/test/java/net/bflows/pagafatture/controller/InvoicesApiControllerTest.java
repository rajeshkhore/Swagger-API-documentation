package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.entities.TransactionsEntity.TransactionsMethodTypeEnum;
import net.bflows.pagafatture.enums.DashboardEnums;
import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceSearchReq;
import net.bflows.pagafatture.model.InvoiceUpdateReq;
import net.bflows.pagafatture.model.widget.InvoicesWithAverageDaysMonthWise;
import net.bflows.pagafatture.model.widget.InvoicesWithAverageDaysWidget;
import net.bflows.pagafatture.model.widget.TopUnpaidInvoicesWidgets;
import net.bflows.pagafatture.model.widget.TotalDueAndOverDueInvoicesWidget;
import net.bflows.pagafatture.model.widget.TotalDueAndOverDueInvoicesWithRange;
import net.bflows.pagafatture.model.widget.Widget;
import net.bflows.pagafatture.model.widget.WidgetsResponseBean;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.TransactionRepository;
import net.bflows.pagafatture.scheduler.InvoiceScheduler;
import net.bflows.pagafatture.util.DateTimeUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class InvoicesApiControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private MerchantRepository merchantRepository;
	
	@Autowired
	private InvoicesRepository invoicesRepository;
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private InvoiceScheduler invoiceScheduler;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private TransactionRepository transactionRepository;

	@Test
	@Order(1)
	void testgetAllInvoicesByInvoiceStateBlank() throws Exception {

		String URI = "/v1/invoices";
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
	}
	
	@Test
    @Order(2)
    void testgetAllInvoicesByInvoiceState() throws Exception {

        String URI = "/v1/invoices?invoiceState=DUE";
        mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
    }
	

    @Test
    @Order(3)
    void testgetAllInvoicesByMultipleInvoiceState() throws Exception {

        String URI = "/v1/invoices?invoiceState=DUE,PAID,OVERDUE";
        mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
    }
    
    @Test
    @Order(4)
    void testgetAllInvoicesByInvalidInvoiceState() throws Exception {

        String URI = "/v1/invoices?invoiceState=DUE,test";
        mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isBadRequest());
    }

	@Test
	@Order(5)
	void testCreateMerchantInvoices() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity = merchantRepository.save(merchantEntity);
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

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(6)
	void testGetInvoicesByMerchant() throws Exception {

		String URI = "/v1/merchants/1/invoices";

		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

	}

	@Test
	@Order(7)
	void testGetInvoiceById() throws Exception {
		
		
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test Merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		ClientEntity request = new ClientEntity();
		request.setName("test Client for externelRef");
		request.setVatNumber("cdfj667");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6682");
		request.setDeleted(false);
		request.setMerchantEntity(merchant);
		request= clientRepository.save(request);
        Invoices invoice = new Invoices();
        invoice.setAmountGross(new BigDecimal(755));
        invoice.setAmountNet(new BigDecimal(755));
        invoice.setExternalRef("fjjfnn");
        invoice.setExternalId("AWS457744");
        invoice.setInvoiceNumber("1001");
        invoice.setLinkDoc("www.linkdoc.com");
        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
        invoice.setDueDate(LocalDateTime.now());
        invoice.setExpectedDate(LocalDateTime.now());
        invoice.setClientEntity(request);
        invoice = invoicesRepository.save(invoice);
		
        TransactionsEntity transactionsEntity = new TransactionsEntity();
		transactionsEntity.setAmount(new BigDecimal(2200));
		transactionsEntity.setMethodType(TransactionsMethodTypeEnum.CASH.getValue());
		transactionsEntity.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setInvoices(invoice);
		transactionRepository.save(transactionsEntity);
		
		String URI = "/v1/invoice?invoiceId=" + invoice.getId();

		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

	}

	@Test
	@Order(8)
	void testGetInvoiceByExternalRefWithExternalRef() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test Merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		ClientEntity request = new ClientEntity();
		request.setName("test Client for externelRef");
		request.setVatNumber("332146776");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setDeleted(false);
		request.setMerchantEntity(merchant);
		request= clientRepository.save(request);
        Invoices invoice = new Invoices();
        invoice.setAmountGross(new BigDecimal(755));
        invoice.setAmountNet(new BigDecimal(755));
        invoice.setExternalRef("ddffggb");
        invoice.setExternalId("AWS457744");
        invoice.setInvoiceNumber("1001");
        invoice.setLinkDoc("www.linkdoc.com");
        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
        invoice.setDueDate(LocalDateTime.now());
        invoice.setExpectedDate(LocalDateTime.now());
        invoice.setClientEntity(request);
        invoice = invoicesRepository.save(invoice);
		
        TransactionsEntity transactionsEntity = new TransactionsEntity();
		transactionsEntity.setAmount(new BigDecimal(2200));
		transactionsEntity.setMethodType(TransactionsMethodTypeEnum.CASH.getValue());
		transactionsEntity.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setInvoices(invoice);
		transactionRepository.save(transactionsEntity);
		
		String URI = "/v1/invoice?externalRef=" + invoice.getExternalRef();

		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

	}

	@Test
	@Order(9)
	void testGetInvoiceByIdWithInvoiceId_Fail() throws Exception {
		String URI = "/v1/invoice?invoiceId=" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	}

	@Test
	@Order(10)
	void testGetInvoiceByExternalRefWithExternalRef_Fail() throws Exception {

		String URI = "/v1/invoice?externalRef=" + 0;

		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	}

	@Test
	@Order(11)
	void testUpdateInvoice() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("AWS33455").get();

		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setInvoiceState(InvoiceStateEnum.SENT);
		invoice.setExternalCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		invoice.setDueDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		invoice.setExpectedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.externalRef", is(invoice.getExternalRef())));
		
		invoice = new InvoiceUpdateReq();
		invoice.setInvoiceState(InvoiceStateEnum.SENT);
		URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		invoice = new InvoiceUpdateReq();
		invoice.setInvoiceState(InvoiceStateEnum.DUE);
		URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2201));
		URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
				
	}
	
	
	@Test
	@Order(12)
	void testGetWidgetsByMerchantId() throws Exception {

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
			
		

	}

	@Test
	@Order(13)
	void getInvoicesByMerchantIdAndClientIdSuccess() throws Exception {
		String token = "bWVyY2hhbnQtMS5jbGllbnQtMQ==";
		String URI = "/v1/clients/invoices";
		InvoiceSearchReq invoiceSearchRequest = new InvoiceSearchReq();
		invoiceSearchRequest.setToken(token);
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoiceSearchRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@Order(14)
	void getInvoicesByMerchantIdAndClientIdFail() throws Exception {
		String token = "";
		String URI = "/v1/clients/invoices";

		InvoiceSearchReq invoiceSearchRequest = new InvoiceSearchReq();
		invoiceSearchRequest.setToken(token);
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoiceSearchRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(15)
	void testCreateMerchantInvoices_To_Test_DefaultPaymentDays() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant for defaultDays");
		merchantEntity.setDefaultPaymentDays(60);
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("12345");
		clientReq.setName("Test Client");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		// invoice.setCustomer("External customer");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS334551");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");

		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(16)
	void testUpdateInvoiceWithDueDateBeforeForStateDue() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("AWS33455").get();

		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		// invoice.setCustomer("External customer");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.of(2020, 10, 2, 0, 0));

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.invoiceState", is(InvoiceStateEnum.OVERDUE.getValue())));
	}

	@Test
	@Order(17)
	void testUpdateInvoiceWithDueDateAfterForStateDue() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("AWS33455").get();

		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()).plusMonths(1));

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.invoiceState", is(InvoiceStateEnum.DUE.getValue())));
	}

	@Test
	@Order(18)
	void testCreateMerchantInvoicesWithOverdueState() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant overdue");
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("OVERDUEPIVA123");
		clientReq.setName("Test Client new");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("OVERDUEID");
		invoice.setInvoiceState(InvoiceStateEnum.OVERDUE);
		invoice.setExternalRef("OVERDUEREF");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(19)
	void testUpdateInvoiceWithDueDateBeforeForStateOverDue() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("OVERDUEREF").get();

		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("OVERDUEID");
		invoice.setExternalRef("OVERDUEREF");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.of(2020, 10, 2, 0, 0));

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.invoiceState", is(InvoiceStateEnum.OVERDUE.getValue())));
	}

	@Test
	@Order(20)
	void testUpdateInvoiceWithDueDateAfterForStateOverdue() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("OVERDUEREF").get();

		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("OVERDUEID");
		invoice.setExternalRef("OVERDUEREF");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()).plusMonths(1));

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.invoiceState", is(InvoiceStateEnum.DUE.getValue())));
	}

	@Test
	@Order(21)
	void testCreateMerchantInvoicesWithDisputedState() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant dispute");
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("DISPUTEDPIVA123");
		clientReq.setName("Test Client dispute");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("DISPUTEDID");
		invoice.setInvoiceState(InvoiceStateEnum.DISPUTED);
		invoice.setExternalRef("DISPUTEDREF");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}

	@Test
	@Order(22)
	void testUpdateInvoiceWithDueDateBeforeForStateDisputed() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("DISPUTEDREF").get();

		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("DISPUTEDID");
		invoice.setExternalRef("DISPUTEDREF");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.of(2020, 10, 2, 0, 0));

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.invoiceState", is(InvoiceStateEnum.DISPUTED.getValue())));
	}

	@Test
	@Order(23)
	void testUpdateInvoiceWithDueDateAfterForStateDisputed() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("DISPUTEDREF").get();

		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("DISPUTEDID");
		invoice.setExternalRef("DISPUTEDREF");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()).plusMonths(1));

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.invoiceState", is(InvoiceStateEnum.DISPUTED.getValue())));
	}

	@Test
	@Order(24)
	void testGetInvoiceByIdWithInvoiceId_FailByMerchantId() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test Merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		ClientEntity request = new ClientEntity();
		request.setName("test Client for externelRef");
		request.setVatNumber("334233246776");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setDeleted(true);
		request.setMerchantEntity(merchant);
		request= clientRepository.save(request);
        Invoices invoice = new Invoices();
        invoice.setAmountGross(new BigDecimal(755));
        invoice.setAmountNet(new BigDecimal(755));
        invoice.setExternalRef("232212");
        invoice.setExternalId("AWS457744");
        invoice.setInvoiceNumber("1001");
        invoice.setLinkDoc("www.linkdoc.com");
        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
        invoice.setDueDate(LocalDateTime.now());
        invoice.setExpectedDate(LocalDateTime.now());
        invoice.setClientEntity(request);
        invoice = invoicesRepository.save(invoice);

		String URI = "/v1/invoice?invoiceId=" + invoice.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	}

	@Test
	@Order(25)
	void testGetInvoiceByExternalRefWithExternalRef_FailByClientId() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("test Merchant");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		
		ClientEntity request = new ClientEntity();
		request.setName("test Client for externelRef");
		request.setVatNumber("3342w776");
		request.setEmail("test@gmail.com");
		request.setAddress("Italy");
		request.setTelephone("6673382");
		request.setDeleted(true);
		request.setMerchantEntity(merchant);
		request= clientRepository.save(request);
        Invoices invoice = new Invoices();
        invoice.setAmountGross(new BigDecimal(755));
        invoice.setAmountNet(new BigDecimal(755));
        invoice.setExternalRef("334566643");
        invoice.setExternalId("AWS457744");
        invoice.setInvoiceNumber("1001");
        invoice.setLinkDoc("www.linkdoc.com");
        invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
        invoice.setDueDate(LocalDateTime.now());
        invoice.setExpectedDate(LocalDateTime.now());
        invoice.setClientEntity(request);
        invoice = invoicesRepository.save(invoice);

		String URI = "/v1/invoice?externalRef=" + invoice.getExternalRef();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

	}

	@Test
	@Order(26)
	void testUpdateInvoice_FailInvalidClient() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("AWS33455").get();
		ClientEntity clientEntity = invoices.getClientEntity();
		
		clientEntity.setDeleted(true);
		clientRepository.save(clientEntity);
		Long invoiceId = invoices.getId();

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		// invoice.setCustomer("External customer");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");

		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(27)
	void testUpdateInvoice_FailInvoiceId() throws Exception {

		Invoices invoices = invoicesRepository.findByExternalRefIgnoreCase("AWS33455").get();
		ClientEntity clientEntity = invoices.getClientEntity();
		clientEntity.setDeleted(true);
		clientRepository.save(clientEntity);

		InvoiceUpdateReq invoice = new InvoiceUpdateReq();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");

		String URI = "/v1/invoices/" + 0;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoice))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(28)
	void testInvoiceScheduler() {

		MerchantEntity merchant = merchantRepository.findById(1l).get();
		ClientEntity client = new ClientEntity();
		client.setVatNumber("ppop");
		client.setName("Test Client");
		client.setMerchantEntity(merchant);
		client.setDeleted(false);
		client = clientRepository.save(client);
		
		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("SPSP1");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("SPSP1");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().plusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("SPSP1");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().minusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.SENT.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("PPWTR");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().plusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.SENT.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);
		
		invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("55t65");
		invoice.setExternalRef("eerddtf532");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().plusDays(5));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.SENT.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);
		
		TransactionsEntity transactionsEntity = new TransactionsEntity();
		transactionsEntity.setAmount(new BigDecimal(2200));
		transactionsEntity.setMethodType(TransactionsMethodTypeEnum.CASH.getValue());
		transactionsEntity.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setInvoices(invoice);
		transactionRepository.save(transactionsEntity);
		
		invoiceScheduler.updateInvoiceStateForAllInvoices();
	}
	
	
	
	@Test
	@Order(30)
	void testCreateMerchantInvoices_FaildByDublicateExternelRef() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("12345");
		clientReq.setName("Test Client");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		// invoice.setCustomer("External customer");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);
		invoice.setCurrency("India");

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	
	@Test
	@Order(31)
	void testCreateMerchantInvoicesWithCountry() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("33445ki56");
		clientReq.setName("Test Client");
		clientReq.setCountry("india");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		
		// invoice.setCustomer("External customer");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("1122542");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);
		

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	@Test
	@Order(32)
	void getInvoicesByMerchantIdAndClientIdSuccessWithInvoiceState() throws Exception {
		String token = "bWVyY2hhbnQtMS5jbGllbnQtMQ==";
		String URI = "/v1/clients/invoices";
		InvoiceSearchReq invoiceSearchRequest = new InvoiceSearchReq();
		invoiceSearchRequest.setToken(token);
		invoiceSearchRequest.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoiceSearchRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@Test
	@Order(33)
	void getInvoicesByMerchantIdAndClientIdFaildWithInvalidToken() throws Exception {
		String token = "bWVyY2hhbnQtMT5jbGllbnQtMQ==";
		String URI = "/v1/clients/invoices";
		InvoiceSearchReq invoiceSearchRequest = new InvoiceSearchReq();
		invoiceSearchRequest.setToken(token);
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoiceSearchRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(34)
	void testCreateMerchantInvoices_FaildForInvalidRequest() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("cc44r5rr");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		// invoice.setCustomer("External customer");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWS33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(35)
	void testCreateMerchantInvoicesWithDefaultPaymentDay() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("12345");
		clientReq.setName("Test Client");
		clientReq.setDefaultPaymentDays(5);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWSRST457744");
		invoice.setExternalRef("AWSRST33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	
	

	@Test
	@Order(36)
	void testCreateMerchantInvoicesWithExternalCreatedDate() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity.setDefaultPaymentDays(60);
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("25p");
		clientReq.setName("Test Client");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWP33455");
		invoice.setInvoiceNumber("1002");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);
		invoice.setExternalCreatedDate(LocalDateTime.now());
		invoices.add(invoice);
		
		invoice = new InvoiceReq();
		clientReq = new ClientReq();
		clientReq.setVatNumber("125p");
		clientReq.setName("Test Client");
		clientReq.setDefaultPaymentDays(50);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("APP33455");
		invoice.setInvoiceNumber("1003");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);
		invoice.setExternalCreatedDate(LocalDateTime.now());
		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	
	@Test
	@Order(37)
	void testCreateClientWithContact() throws Exception {

		List<InvoiceReq> invoices = new ArrayList<InvoiceReq>();
		InvoiceReq invoice = new InvoiceReq();
		ClientReq clientReq = new ClientReq();

		MerchantEntity merchantEntity = new MerchantEntity();
		merchantEntity.setName("Test merchant");
		merchantEntity = merchantRepository.save(merchantEntity);
		clientReq.setVatNumber("454516");
		clientReq.setName("Test Client");
		clientReq.setEmail("test@test.com");
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("SP33455");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClient(clientReq);

		invoices.add(invoice);

		String URI = "/v1/merchants/" + merchantEntity.getId() + "/invoices";

		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(invoices))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
	}
	
	
	@Test
	@Order(38)
	void testUpdateInvoice_WithSameInvoiceState() throws Exception {

		MerchantEntity merchant = merchantRepository.findById(1l).get();
		ClientEntity client = new ClientEntity();
		client.setVatNumber("ppop");
		client.setName("Test Client");
		client.setMerchantEntity(merchant);
		client.setDeleted(false);
		client = clientRepository.save(client);
		
		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("SLPV1");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(client);
		invoice.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		invoice.setCreatedDate(LocalDateTime.now());
		invoice = invoicesRepository.save(invoice);

		Long invoiceId = invoice.getId();

		
		InvoiceUpdateReq	invoiceUpdateReq = new InvoiceUpdateReq();
		invoiceUpdateReq.setInvoiceState(InvoiceStateEnum.SENT);
		String URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoiceUpdateReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		invoiceUpdateReq = new InvoiceUpdateReq();
		invoiceUpdateReq.setInvoiceState(InvoiceStateEnum.SENT);
		URI = "/v1/invoices/" + invoiceId;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(invoiceUpdateReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		
		
				
	}
}
