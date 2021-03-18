package net.bflows.pagafatture.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.TimelineEntity;
import net.bflows.pagafatture.entities.TimelineEntity.TimelineEntityTypeEnum;
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.InvoiceUpdateReq;
import net.bflows.pagafatture.model.TransactionReq;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.ContactRepository;
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
public class InvoicesDao {

    @Autowired
    private InvoicesRepository invoicesRepository;
    
    @Autowired
    MerchantRepository merchantRepository;
    
    @Autowired
    ClientRepository clientRepository;
    
    @Autowired
    TransactionRepository transactionRepository;
    
    @Autowired
    ClientDao clientDao;
    
    @Autowired
    TransactionDao transactionDao;
    
    @Autowired
	Validator validator;
    
    @Autowired
    ContactRepository contactRepository;
    
    @Autowired
    TimelineRepository timelineRepository;
    
	public static final String INVALID_INVOICE = "invalid_invoice";
	public static final String INVALID_EXTREF = "extRef_val_msg";
	public static final String EMPTY_FIELD ="empty_field";


    public List<InvoiceReq> getAllInvoicesByInvoiceState(List<String> invoiceStates, Map<String, Object> userInfo) {

        List<Invoices> invoices;
        List<InvoiceReq> invoiceResponse = new ArrayList<>();
        Long userLoggedInMerchantId = validator.getUserMerchantId(userInfo);
        if (invoiceStates.size() > 0) {
        	invoices = invoicesRepository.findByInvoiceStateInAndClientEntityDeletedAndClientEntityMerchantEntityId(invoiceStates, false,userLoggedInMerchantId);
        } else {
        	invoices = invoicesRepository.findByClientEntityDeleted(false,userLoggedInMerchantId);
        }

        for (Invoices inv : invoices) {
        	InvoiceReq req = convertInvoicesToInvoiceReq(inv);
        	invoiceResponse.add(req);
        }

        return invoiceResponse;
    }

	public void createMerchantInvoices(MerchantEntity merchantEntity, List<InvoiceReq> body) {


		List<Invoices> invList = new ArrayList<>();

		for (InvoiceReq invoiceReq : body) {
			Invoices invoices = createInvoice(merchantEntity, invoiceReq);
			
			invList.add(invoices);
		}

		invoicesRepository.saveAll(invList);
	}

	private Invoices createInvoice(MerchantEntity merchantEntity, InvoiceReq invoiceReq) {
		
		if(StringUtils.isEmpty(invoiceReq.getClient().getName()) ||StringUtils.isEmpty(invoiceReq.getClient().getVatNumber())) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(EMPTY_FIELD));
		}
	
		ClientEntity clientEntity = null;
		clientEntity = getOrCreateClient(merchantEntity, invoiceReq);
		Invoices invoices = convertToInvoices(invoiceReq);
		if (invoices.getCurrency() == null || invoices.getCurrency().equals("")) {
			invoices.setCurrency("EUR");
		}
		invoices.setClientEntity(clientEntity);
		if (invoices.getExternalRef() != null) {

			Optional<Invoices> invoiceOptional = invoicesRepository
					.findByExternalRefIgnoreCase(invoices.getExternalRef());

			if (invoiceOptional.isPresent()) {
				throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_EXTREF));
			}
		}
		
		if(invoices.getExternalCreatedDate() == null) {
			invoices.setExternalCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		} 

		if(invoices.getDueDate() == null) {
			invoices.setDueDate(calculateDueDate(merchantEntity, clientEntity, invoices));
			}
		return invoices;
	}

	private LocalDateTime calculateDueDate(MerchantEntity merchantEntity, ClientEntity clientEntity,
			Invoices invoices) {
		LocalDateTime dueDate;
		if (clientEntity.getDefaultPaymentDays() != null) {
			dueDate = setInvoiceDueDate(clientEntity.getDefaultPaymentDays(), invoices.getExternalCreatedDate());
		} else {

			dueDate = setInvoiceDueDate(merchantEntity.getDefaultPaymentDays(), invoices.getExternalCreatedDate());
		}

		return dueDate;
	}

	private LocalDateTime setInvoiceDueDate(Integer defaultPaymentDays, LocalDateTime createdDate) {
		return createdDate.plusDays(defaultPaymentDays);
	}
	private ClientEntity getOrCreateClient(MerchantEntity merchantEntity, InvoiceReq invoiceReq) {
		ClientEntity clientEntity;
		clientEntity = clientRepository.findByVatNumberAndMerchantEntityId(invoiceReq.getClient().getVatNumber(), merchantEntity.getId());
		if (clientEntity == null) {
			clientEntity = clientDao.convertClientReqToClientEntity(invoiceReq.getClient());
			clientEntity.setMerchantEntity(merchantEntity);
			if (clientEntity.getCountry() == null || clientEntity.getCountry().equals("")) {
				clientEntity.setCountry("IT");
			} else {
				clientEntity.setCountry(invoiceReq.getClient().getCountry());
			}
			clientEntity.setDeleted(false);
			clientEntity=clientRepository.save(clientEntity);
			clientDao.createContact(clientEntity);
			
		}
		return clientEntity;
	}
    
    public List<InvoiceReq> getInvoicesByMerchant(Long id) {


        List<InvoiceReq> invoiceReqs = new ArrayList<>();

        List<Invoices> invoices = invoicesRepository.findByMerchantEntityId(id);
       if(!CollectionUtils.isEmpty(invoices)) {
    	   for (Invoices invoice : invoices) {
			InvoiceReq req = convertInvoicesToInvoiceReq(invoice);
			invoiceReqs.add(req);
		}
       }
      return invoiceReqs;

        
    }
    
	public InvoiceResponseBean getInvoiceById(Long invoiceId, Map<String, Object> userInfo) {
		InvoiceResponseBean bean = new InvoiceResponseBean();

		Optional<Invoices> invoiceEntity = invoicesRepository.findById(invoiceId);

		if (!invoiceEntity.isPresent()) {
			return null;
		}
		Invoices invoice = invoiceEntity.get();
		validator.validateUserMerchantId(userInfo, invoice.getClientEntity().getMerchantEntity().getId());
		setInvoiceBean(bean, invoice);

		return bean;

	}

	public InvoiceReq convertInvoicesToInvoiceReq(Invoices invoicingEntity) {

		InvoiceReq invoiceReq = new InvoiceReq();
        invoiceReq.setId(invoicingEntity.getId());
		invoiceReq.setAmountGross(invoicingEntity.getAmountGross());
		invoiceReq.setAmountNet(invoicingEntity.getAmountNet());
		invoiceReq.setCreatedDate(invoicingEntity.getCreatedDate());
		invoiceReq.setCurrency(invoicingEntity.getCurrency());
		invoiceReq.setExternalId(invoicingEntity.getExternalId());
		invoiceReq.setExternalRef(invoicingEntity.getExternalRef());
		invoiceReq.setInvoiceNumber(invoicingEntity.getInvoiceNumber());
     	invoiceReq.setInvoiceState(InvoiceStateEnum.fromValue(invoicingEntity.getInvoiceState()));

		invoiceReq.setLinkDoc(invoicingEntity.getLinkDoc());

		if (invoicingEntity.getDueDate() != null) {
			invoiceReq.setDueDate(DateTimeUtil.DateServerToUTC(invoicingEntity.getDueDate()));
		}

		if (invoicingEntity.getExpectedDate() != null) {
			invoiceReq.setExpectedDate(DateTimeUtil.DateServerToUTC(invoicingEntity.getExpectedDate()));
		}
		if (invoicingEntity.getExternalCreatedDate() != null) {
			invoiceReq.setExternalCreatedDate(DateTimeUtil.DateServerToUTC(invoicingEntity.getExternalCreatedDate()));
		}
		invoiceReq.setClient(clientDao.convertClientEntityToClientReq(invoicingEntity.getClientEntity()));
		invoiceReq.setUpdatedDate(invoicingEntity.getUpdatedDate());
		return invoiceReq;
	}
    
	
	
	Invoices convertToInvoices(InvoiceReq invoiceReq) {

		Invoices invoicingEntity = new Invoices();

		invoicingEntity.setAmountGross(invoiceReq.getAmountGross());
		invoicingEntity.setAmountNet(invoiceReq.getAmountNet());
		invoicingEntity.setCreatedDate(invoiceReq.getCreatedDate());
		invoicingEntity.setCurrency(invoiceReq.getCurrency());
		invoicingEntity.setExternalId(invoiceReq.getExternalId());
		invoicingEntity.setExternalRef(invoiceReq.getExternalRef());
		invoicingEntity.setInvoiceNumber(invoiceReq.getInvoiceNumber());

		if (invoiceReq.getInvoiceState() != null) {
			invoicingEntity.setInvoiceState(invoiceReq.getInvoiceState().getValue());
		} else {
			invoicingEntity.setInvoiceState(InvoiceStateEnum.DUE.getValue());
		}

		invoicingEntity.setLinkDoc(invoiceReq.getLinkDoc());

		if (invoiceReq.getDueDate() != null) {
			invoicingEntity.setDueDate(DateTimeUtil.DateServerToUTC(invoiceReq.getDueDate()));
		}

		if (invoiceReq.getExpectedDate() != null) {
			invoicingEntity.setExpectedDate(DateTimeUtil.DateServerToUTC(invoiceReq.getExpectedDate()));
		}
		
		invoicingEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
        invoicingEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		
		if (invoiceReq.getExternalCreatedDate() != null) {
			invoicingEntity.setExternalCreatedDate(invoiceReq.getExternalCreatedDate());
		}
		
		return invoicingEntity;
	}
    
	public InvoiceReq updateMarchantInvoice(Long invoiceId, InvoiceUpdateReq updateInvoiceReq, Map<String, Object> userInfo) {

		Optional<Invoices> invoiceEntity = invoicesRepository.findById(invoiceId);

		if (!invoiceEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_INVOICE));
		}
		Invoices invoice = invoiceEntity.get();

		Optional<ClientEntity> clientEntity = clientRepository.findById(invoice.getClientEntity().getId());

		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_INVOICE));
		}

		validator.validateUserMerchantId(userInfo, clientEntity.get().getMerchantEntity().getId());
		setIfNotNull(invoice::setAmountGross, updateInvoiceReq.getAmountGross());
		setIfNotNull(invoice::setAmountNet, updateInvoiceReq.getAmountNet());
		setIfNotNull(invoice::setCurrency, updateInvoiceReq.getCurrency());
		setIfNotNull(invoice::setExternalId, updateInvoiceReq.getExternalId());
		setIfNotNull(invoice::setExternalRef, updateInvoiceReq.getExternalRef());
		setIfNotNull(invoice::setLinkDoc, updateInvoiceReq.getLinkDoc());
		if (updateInvoiceReq.getInvoiceState() != null
				&& !invoice.getInvoiceState().equals(updateInvoiceReq.getInvoiceState().getValue())) {
			TimelineEntity timelineEntity = createTimeLineForInvoice(updateInvoiceReq.getInvoiceState().getValue(), invoice);
			timelineRepository.save(timelineEntity);
			invoice.setInvoiceState(updateInvoiceReq.getInvoiceState().getValue());
		}

		if (updateInvoiceReq.getExpectedDate() != null) {
			invoice.setExpectedDate(DateTimeUtil.DateServerToUTC(updateInvoiceReq.getExpectedDate()));
		}
		
		if (updateInvoiceReq.getExternalCreatedDate() != null) {
			invoice.setExternalCreatedDate(updateInvoiceReq.getExternalCreatedDate());
		}
	
		setDueDate(updateInvoiceReq, invoice);
		invoice.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));

		invoice = invoicesRepository.save(invoice);

		return convertInvoicesToInvoiceReq(invoice);
	}

	public TimelineEntity createTimeLineForInvoice(String newInvoiceState, Invoices invoice) {
		TimelineEntity timelineEntity = new TimelineEntity();
		timelineEntity.setClientEntity(invoice.getClientEntity());
		timelineEntity.setInvoice(invoice);
		timelineEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setType(TimelineEntityTypeEnum.INVOICE_STATE_CHANGED.getValue());
		Map<String, Object> actionJson = new HashMap<>();
		actionJson.put("oldInvoiceState", invoice.getInvoiceState());
		actionJson.put("newInvoiceState", newInvoiceState);
		actionJson.put("invoiceAmount", invoice.getAmountGross());
		actionJson.put("currency", invoice.getCurrency());
		actionJson.put("invoiceNumber", invoice.getInvoiceNumber());
		timelineEntity.setActionJson(Utils.convertMapIntoJsonString(actionJson));
		timelineEntity.setStatus("SUCCESS");
		return timelineEntity;
	}
	
	private void setDueDate(InvoiceUpdateReq updateInvoiceReq, Invoices invoice) {
		if (updateInvoiceReq.getDueDate() != null) {
			invoice.setDueDate(DateTimeUtil.DateServerToUTC(updateInvoiceReq.getDueDate()));

			if (!(invoice.getInvoiceState().equals(InvoiceStateEnum.PAID.getValue())
					|| invoice.getInvoiceState().equals(InvoiceStateEnum.WRITE_OFF.getValue())
					|| invoice.getInvoiceState().equals(InvoiceStateEnum.DISPUTED.getValue()))) {

				if (DateTimeUtil.DateServerToUTC(LocalDateTime.now()).isAfter(updateInvoiceReq.getDueDate())) {
					invoice.setInvoiceState(InvoiceStateEnum.OVERDUE.getValue());
				} else {
					invoice.setInvoiceState(InvoiceStateEnum.DUE.getValue());
				}
			}

		}
	}
    
    private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

	public InvoiceResponseBean getInvoiceByExternalRef(String externalRef, Map<String, Object> userInfo) {
		InvoiceResponseBean bean = new InvoiceResponseBean();
		Invoices invoice = invoicesRepository.findByExternalRef(externalRef);
		if (invoice == null) {
			return null;
		}
		validator.validateUserMerchantId(userInfo, invoice.getClientEntity().getMerchantEntity().getId());
		setInvoiceBean(bean, invoice);
	return bean;
	}

	private void setInvoiceBean(InvoiceResponseBean bean, Invoices invoice) {
		
		Optional<ClientEntity> clientEntity = clientRepository.findById(invoice.getClientEntity().getId());
		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_INVOICE));
		}
		InvoiceReq invoiceReq = convertInvoicesToInvoiceReq(invoice);
		bean.setInvoice(invoiceReq);
		TransactionsEntity transactionsEntity = transactionRepository.findByInvoicesId(invoice.getId());
		if (transactionsEntity != null) {
			TransactionReq req = transactionDao.convertTransactionsEntityIntoTransactionReq(transactionsEntity);
			bean.setTransaction(req);
		}
	}

}
