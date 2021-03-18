package net.bflows.pagafatture.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.TimelineEntity;
import net.bflows.pagafatture.entities.TimelineEntity.TimelineEntityTypeEnum;
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.entities.TransactionsEntity.TransactionsMethodTypeEnum;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.TransactionReq;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.TimelineRepository;
import net.bflows.pagafatture.repositories.TransactionRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Utils;
import net.bflows.pagafatture.util.Validator;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@Component
public class TransactionDao {

	public static final String INVALID_INVOICE = "invalid_invoice";
	public static final String INVALID_TRANSACTION = "invalid_transaction";
	public static final String TRANSACTION_EXISTS = "transaction_exists";
	public static final String EMPTY_METHODTYPE_FIELD ="empty_methodType_field";


	@Autowired
	InvoicesRepository invoicesRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	TimelineRepository timelineRepository;
	
	@Autowired
	InvoicesDao invoicesDao;
	
	@Autowired
	Validator validator;

	public TransactionReq createTransaction(Long invoiceId, TransactionReq request, Map<String, Object> userInfo) {

		if(request.getMethodType() == null) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(EMPTY_METHODTYPE_FIELD));
		}
		
		Optional<Invoices> invoice = invoicesRepository.findById(invoiceId);

		if (!invoice.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_INVOICE));
		}
		Invoices invoices = invoice.get();
		validator.validateUserMerchantId(userInfo, invoices.getClientEntity().getMerchantEntity().getId());
		TransactionsEntity entity = transactionRepository.findByInvoicesId(invoiceId);
		if (entity != null) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(TRANSACTION_EXISTS));
		}
		entity = convertTransactionReqIntoTransactionsEntity(request);
		
		if (entity.getCurrency() == null || entity.getCurrency().equals("")) {
		    entity.setCurrency(invoice.get().getCurrency());
        }
		
		entity.setInvoices(invoices);
		entity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		if(entity.getAmount() == null) {
			entity.setAmount(invoices.getAmountGross());
		}
		if(entity.getPaymentDate() == null) {
			entity.setPaymentDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		}
		entity = transactionRepository.save(entity);
		timelineRepository.save(invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.PAID.getValue(), invoices));
		invoices.setInvoiceState(InvoiceStateEnum.PAID.getValue());
		invoices.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		invoicesRepository.save(invoices);
		
		TimelineEntity timelineEntity = new TimelineEntity();
		timelineEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setClientEntity(invoices.getClientEntity());
		timelineEntity.setInvoice(invoices);
		timelineEntity.setStatus("SUCCESS");
		timelineEntity.setType(TimelineEntityTypeEnum.TRANSACTION.getValue());
		Map<String, Object> actionJson = new HashMap<>();
		actionJson.put("transactionId", entity.getId());
		actionJson.put("amount",entity.getAmount());
		actionJson.put("methodType", entity.getMethodType());
		actionJson.put("isDeleted", false);
		actionJson.put("invoiceNumber", invoice.get().getInvoiceNumber());
		DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		actionJson.put("paymentDate",dTF.format(entity.getPaymentDate()));
		actionJson.put("currency", entity.getCurrency());
		timelineEntity.setActionJson(Utils.convertMapIntoJsonString(actionJson));
		timelineEntity = timelineRepository.save(timelineEntity);
		return convertTransactionsEntityIntoTransactionReq(entity);
	}

	private TransactionsEntity convertTransactionReqIntoTransactionsEntity(TransactionReq request) {
		TransactionsEntity entity = new TransactionsEntity();
		entity.setAmount(request.getAmount());
		entity.setPaymentDate(request.getPaymentDate());
		entity.setMethodType(request.getMethodType().getValue());
		entity.setCurrency(request.getCurrency());
		return entity;
	}


	public TransactionsEntity getTransactionByInvoiceId(Long id, Map<String, Object> userInfo) {
		TransactionsEntity transactionsEntity = transactionRepository.findByInvoicesId(id);

		if (transactionsEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND,Translator.toLocale(INVALID_TRANSACTION));
		}
		validator.validateUserMerchantId(userInfo, transactionsEntity.getInvoices().getClientEntity().getMerchantEntity().getId());
		return transactionsEntity;

	}

	public List<InvoiceResponseBean> getAllTransactionByMerchantId(Long id) {
		List<InvoiceResponseBean> invoicesBeans = new ArrayList<>();
		List<TransactionsEntity> transactionsEntities = transactionRepository.findByMerchantId(id);
		if(!CollectionUtils.isEmpty(transactionsEntities)) {
			transactionsEntities.forEach(entity->{
				TransactionReq transactionReq = convertTransactionsEntityIntoTransactionReq(entity);
				Invoices invoices = entity.getInvoices();
				InvoiceReq invoiceReq = invoicesDao.convertInvoicesToInvoiceReq(invoices);
				InvoiceResponseBean bean = new InvoiceResponseBean();
				bean.setInvoice(invoiceReq);
				bean.setTransaction(transactionReq);
				invoicesBeans.add(bean);
			});
		}
		return invoicesBeans;

	}

	public TransactionReq updateTransaction(Long invoiceId, TransactionReq request, Map<String, Object> userInfo) {

		Optional<Invoices> invocie = invoicesRepository.findById(invoiceId);

		if (!invocie.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_INVOICE));
		}
		Invoices invoices = invocie.get();
		validator.validateUserMerchantId(userInfo, invoices.getClientEntity().getMerchantEntity().getId());
		if (!invoices.getInvoiceState().equals(InvoiceStateEnum.PAID.getValue())) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale("invalid_invoice_state"));
		}

		TransactionsEntity entity = transactionRepository.findByInvoicesId(invoiceId);
		if (entity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND,Translator.toLocale(INVALID_TRANSACTION));
		}

		setIfNotNull(entity::setAmount, request.getAmount());
		setIfNotNull(entity::setCurrency, request.getCurrency());
		if (request.getMethodType() != null) {
			entity.setMethodType(request.getMethodType().getValue());
		}

		if (request.getPaymentDate() != null) {
			entity.setPaymentDate(DateTimeUtil.DateServerToUTC(request.getPaymentDate()));
		}

		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));

		entity = transactionRepository.save(entity);

		return convertTransactionsEntityIntoTransactionReq(entity);

	}

	public void deleteTransaction(Long id, Map<String, Object> userInfo) {
		 LocalDateTime currentDate = DateTimeUtil.DateServerToUTC(LocalDateTime.now());
		TransactionsEntity transactionsEntity = transactionRepository.findByInvoicesId(id);

		if (transactionsEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_TRANSACTION));
		}
		Invoices invoices = transactionsEntity.getInvoices();
		validator.validateUserMerchantId(userInfo, invoices.getClientEntity().getMerchantEntity().getId());
		
		TimelineEntity timelineEntity = new TimelineEntity();
		timelineEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setClientEntity(invoices.getClientEntity());
		timelineEntity.setInvoice(invoices);
		timelineEntity.setStatus("SUCCESS");
		timelineEntity.setType(TimelineEntityTypeEnum.TRANSACTION.getValue());
		Map<String, Object> actionJson = new HashMap<>();
		actionJson.put("transactionId", transactionsEntity.getId());
		actionJson.put("amount",transactionsEntity.getAmount());
		actionJson.put("methodType", transactionsEntity.getMethodType());
		DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		actionJson.put("paymentDate",dTF.format(transactionsEntity.getPaymentDate()));
		actionJson.put("currency", transactionsEntity.getCurrency());
		actionJson.put("isDeleted", true);
		actionJson.put("invoiceNumber", invoices.getInvoiceNumber());
		timelineEntity.setActionJson(Utils.convertMapIntoJsonString(actionJson));
		timelineEntity = timelineRepository.save(timelineEntity);
		transactionRepository.delete(transactionsEntity);
		 if (currentDate.isAfter(invoices.getDueDate())) {
			 invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.OVERDUE.getValue(), invoices);
			 invoices.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
		 } else {
			invoicesDao.createTimeLineForInvoice(InvoiceStateEnum.DUE.getValue(), invoices);
			 invoices.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		 }
		invoices.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		invoicesRepository.save(invoices);
	}

	private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
		if (value != null) {
			setter.accept(value);
		}
	}

	public TransactionReq convertTransactionsEntityIntoTransactionReq(TransactionsEntity entity) {
		TransactionReq request = new TransactionReq();
		request.setAmount(entity.getAmount());
		request.setPaymentDate(entity.getPaymentDate());
		request.setMethodType(TransactionsMethodTypeEnum.fromValue(entity.getMethodType()));
		request.setCreatedDate(entity.getCreatedDate());
		request.setUpdatedDate(entity.getUpdatedDate());
		request.setId(entity.getId());
		request.setCurrency(entity.getCurrency());
		return request;
	}
	
}
