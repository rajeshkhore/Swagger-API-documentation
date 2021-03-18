
package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.entities.TransactionsEntity.TransactionsMethodTypeEnum;
import net.bflows.pagafatture.model.TransactionReq;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.TransactionRepository;
import net.bflows.pagafatture.util.DateTimeUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class TransactionApiControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MerchantRepository merchantRepository;

	@Autowired
	private InvoicesRepository invoicesRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Test
	@Order(1)
	void testCreateTransaction() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("12345");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWSQW11144");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice = invoicesRepository.save(invoice);

		TransactionReq transactionReq = new TransactionReq();
		transactionReq.setMethodType(TransactionsMethodTypeEnum.CASH);
		transactionReq.setCurrency("");
		String URI = "/v1/transactions/" + invoice.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(transactionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.methodType", is(transactionReq.getMethodType().getValue())));
		
		transactionReq.setAmount(new BigDecimal(2200));
		transactionReq.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(transactionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		Invoices invoice1 = new Invoices();
		invoice1.setAmountGross(new BigDecimal(2200));
		invoice1.setAmountNet(new BigDecimal(2500));
		invoice1.setCurrency("INR");
		invoice1.setExternalId("AWS457744");
		invoice1.setExternalRef("AW1SQW11144");
		invoice1.setInvoiceNumber("1001");
		invoice1.setLinkDoc("www.linkdoc.com");
		invoice1.setDueDate(LocalDateTime.now());
		invoice1.setExpectedDate(LocalDateTime.now());
		invoice1.setClientEntity(clientEntity);
		invoice1.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice1 = invoicesRepository.save(invoice1);
		
		TransactionReq transactionReq1 = new TransactionReq();
		transactionReq1.setMethodType(TransactionsMethodTypeEnum.CASH);
		transactionReq1.setCurrency("INR");
		transactionReq1.setAmount(new BigDecimal(2500));
		URI = "/v1/transactions/" + invoice1.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(transactionReq1))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.methodType", is(transactionReq1.getMethodType().getValue())));
		
		Invoices invoice2 = new Invoices();
		invoice2.setAmountGross(new BigDecimal(2200));
		invoice2.setAmountNet(new BigDecimal(2500));
		invoice2.setCurrency("INR");
		invoice2.setExternalId("AWS457744");
		invoice2.setExternalRef("AW1SQW11144");
		invoice2.setInvoiceNumber("1001");
		invoice2.setLinkDoc("www.linkdoc.com");
		invoice2.setDueDate(LocalDateTime.now());
		invoice2.setExpectedDate(LocalDateTime.now());
		invoice2.setClientEntity(clientEntity);
		invoice2.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice2 = invoicesRepository.save(invoice2);
		
		TransactionReq transactionReq2 = new TransactionReq();
		transactionReq2.setMethodType(TransactionsMethodTypeEnum.CASH);
		transactionReq2.setAmount(new BigDecimal(2500));
		URI = "/v1/transactions/" + invoice2.getId();
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(transactionReq2))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.methodType", is(transactionReq1.getMethodType().getValue())));
		
	}
	
	@Test
	@Order(2)
	void testGetTransactionByInvocieId() throws Exception {
		TransactionsEntity transactionsEntity = createTransaction();
		String URI = "/v1/transactions/" + transactionsEntity.getInvoices().getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
	}

	
	@Test
	@Order(3)
	void testGetTransactionByMerchantId() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant232");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);
		

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("12345");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWSQW11144");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoice = invoicesRepository.save(invoice);

		TransactionsEntity transactionsEntity = new TransactionsEntity();
		transactionsEntity.setAmount(new BigDecimal(2200));
		transactionsEntity.setMethodType(TransactionsMethodTypeEnum.CASH.getValue());
		transactionsEntity.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setInvoices(invoice);
		transactionRepository.save(transactionsEntity);

		
		
		String URI = "/v1/merchant/transactions/" + merchant.getId();
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());
	}


	@Test
	@Order(4)
	void testUpdateTransaction() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("56yy35");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("22143");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoice = invoicesRepository.save(invoice);

		TransactionsEntity transactionsEntity = new TransactionsEntity();
		transactionsEntity.setAmount(new BigDecimal(2200));
		transactionsEntity.setMethodType(TransactionsMethodTypeEnum.CASH.getValue());
		transactionsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setInvoices(invoice);
        transactionsEntity = transactionRepository.save(transactionsEntity);
		TransactionReq transactionReq2 = new TransactionReq();
		transactionReq2.setMethodType(TransactionsMethodTypeEnum.BANK);
		transactionReq2.setAmount(new BigDecimal(2200));
		String URI = "/v1/transactions/" + transactionsEntity.getInvoices().getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(transactionReq2))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.methodType", is(transactionReq2.getMethodType().getValue())));

	}

	
	
	

	@Test
	@Order(5)
	void testCreateTransaction_FailById() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("12345");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWSQW11144");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice = invoicesRepository.save(invoice);

		TransactionReq transactionReq = new TransactionReq();
		transactionReq.setAmount(new BigDecimal(2200));
		transactionReq.setMethodType(TransactionsMethodTypeEnum.CASH);
		transactionReq.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		String URI = "/v1/transactions/" + 0;
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(transactionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(6)
	void testUpdateTransaction_Fail() throws Exception {

		TransactionReq transactionReq2 = new TransactionReq();
		transactionReq2.setMethodType(TransactionsMethodTypeEnum.BANK);

		String URI = "/v1/transactions/" + 0;
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(transactionReq2))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	@Order(7)
	void testDeleteTransaction_Fail() throws Exception {
		String URI = "/v1/transactions/" + 0;
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());
	}

	@Test
	@Order(8)
	void testDeleteTransaction() throws Exception {

		ClientEntity clientEntity = clientRepository.findById(1l).get();
		Invoices invoice = new Invoices();
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWSLP11144");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now().minusMonths(1));
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoice = invoicesRepository.save(invoice);

		TransactionsEntity transactionsEntity = new TransactionsEntity();
		transactionsEntity.setAmount(new BigDecimal(2200));
		transactionsEntity.setMethodType(TransactionsMethodTypeEnum.CASH.getValue());
		transactionsEntity.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setInvoices(invoice);
		transactionsEntity=transactionRepository.save(transactionsEntity);
		String URI = "/v1/transactions/" + transactionsEntity.getInvoices().getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isOk());
		
		transactionsEntity = createTransaction();
		URI = "/v1/transactions/" + transactionsEntity.getInvoices().getId();
		mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isOk());
	}


	TransactionsEntity createTransaction() {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("12345");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("AWSQW11144");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoice = invoicesRepository.save(invoice);

		TransactionsEntity transactionsEntity = new TransactionsEntity();
		transactionsEntity.setAmount(new BigDecimal(2200));
		transactionsEntity.setMethodType(TransactionsMethodTypeEnum.CASH.getValue());
		transactionsEntity.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		transactionsEntity.setInvoices(invoice);
		return transactionRepository.save(transactionsEntity);
	}
	
	@Test
	@Order(9)
	void testGetTransactionByInvocieId_Fail() throws Exception {
		String URI = "/v1/transactions/" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(10)
	void testGetTransactionByMerchantId_Fail() throws Exception {
		
		String URI = "/v1/merchant/transactions/" + 0;
		mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());
	}
	
	@Test
	@Order(11)
	void testUpdateTransaction_FailByInvoiceState() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("12345");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("rthyujk");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice = invoicesRepository.save(invoice);
		
		TransactionReq transactionReq2 = new TransactionReq();
		transactionReq2.setMethodType(TransactionsMethodTypeEnum.BANK);

		String URI = "/v1/transactions/" + invoice.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(transactionReq2))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@Test
	@Order(12)
	void testUpdateTransaction_FailByTransactionId() throws Exception {

		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("12345");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("rthyujk");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoice = invoicesRepository.save(invoice);
		
		TransactionReq transactionReq2 = new TransactionReq();
		transactionReq2.setMethodType(TransactionsMethodTypeEnum.BANK);

		String URI = "/v1/transactions/" + invoice.getId();
		mvc.perform(MockMvcRequestBuilders.put(URI).content(new ObjectMapper().writeValueAsString(transactionReq2))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}
	
	
	@Test
	@Order(12)
	void testCreateTransaction_FaildForInvalidRequest() throws Exception {
		MerchantEntity merchant = new MerchantEntity();
		merchant.setName("Merchant2");
		merchant.setVatNumber("12345678901");
		merchant.setDirectUrl("Http:www.www.it");
		merchant.setAddressCity("Cagliari");
		merchant.setAddressState("Sardinia");
		merchant.setAddressCAP("09100");
		merchant.setAddressStreet("Via PPP");
		merchant = merchantRepository.save(merchant);

		ClientEntity clientEntity = new ClientEntity();
		Invoices invoice = new Invoices();
		clientEntity.setVatNumber("fhfdsdby");
		clientEntity.setName("Test Client");
		clientEntity.setMerchantEntity(merchant);
		clientEntity.setDeleted(false);
		clientEntity = clientRepository.save(clientEntity);
		invoice.setAmountGross(new BigDecimal(2200));
		invoice.setAmountNet(new BigDecimal(2500));
		invoice.setCurrency("INR");
		invoice.setExternalId("AWS457744");
		invoice.setExternalRef("22bbgfdes");
		invoice.setInvoiceNumber("1001");
		invoice.setLinkDoc("www.linkdoc.com");
		invoice.setDueDate(LocalDateTime.now());
		invoice.setExpectedDate(LocalDateTime.now());
		invoice.setClientEntity(clientEntity);
		invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		invoice = invoicesRepository.save(invoice);

		TransactionReq transactionReq = new TransactionReq();
		String URI = "/v1/transactions/" + invoice.getId();
		transactionReq.setAmount(new BigDecimal(2200));
		transactionReq.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		mvc.perform(MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(transactionReq))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		
				
	}
}
