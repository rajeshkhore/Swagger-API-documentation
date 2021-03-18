package net.bflows.pagafatture.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.ContactsEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.TimelineEntity;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.entities.ContactsEntity.ContactsRoleEnum;
import net.bflows.pagafatture.model.ClientInvoiceDetail;
import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.ClientResponseBean;
import net.bflows.pagafatture.model.InvoiceDetails;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.ContactRepository;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.repositories.TimelineRepository;
import net.bflows.pagafatture.repositories.WorkflowRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Utils;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;




@Component
public class ClientDao {

	public static final String INVALID_CLIENT = "invalid_client";
	public static final String CLIENT_EXISTS = "client_exists";
	public static final String EMPTY_FIELD ="empty_field";
	public static final String NUMOFINVOICES="numOfInvoices";
	public static final String TOTAL ="total";
	public static final String CURRENCY="currency";
	public static final String INVALID_WORKFLOW = "invalid_workflow";
	public static final String DIFFERENT_MERCHANTID = "different_merchantId";

	@Autowired
	ClientRepository clientRepository;

	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	InvoicesRepository invoicesRepository;
	
	@Autowired
	InvoicesDao invoicesDao;
	
	@Autowired
	WorkflowRepository workflowRepository;
	
	@Autowired
	TimelineRepository timelineRepository;
	
	@Autowired
	ContactRepository contactRepository;


	public ClientReq createClient(MerchantEntity merchantEntity, ClientReq clientReq) {
		if(StringUtils.isEmpty(clientReq.getName()) ||StringUtils.isEmpty(clientReq.getVatNumber())) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(EMPTY_FIELD));
		}
		
		ClientEntity entity = clientRepository.findByVatNumberAndMerchantEntityId(clientReq.getVatNumber(), merchantEntity.getId());

		if (entity != null) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(CLIENT_EXISTS));
		}
		entity = convertClientReqToClientEntity(clientReq);
		entity.setMerchantEntity(merchantEntity);
		if (entity.getCountry() == null || entity.getCountry().equals("")) {
			entity.setCountry("IT");
		}
		entity.setDeleted(false);
		if(clientReq.getWorkflowId() != null) {
			WorkflowEntity workflowEntity = workflowRepository.findByIdAndIsDeleted(clientReq.getWorkflowId(), false);
			if(workflowEntity == null) {
				throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
			}
			
			if(!merchantEntity.getId().equals(workflowEntity.getMerchantEntity().getId())) {
				throw new CustomValidationException(Status.NOT_FOUND, Translator.toLocale(DIFFERENT_MERCHANTID));
			}
			entity.setWorkflowEntity(workflowEntity);
		}
		
		if(entity.getDefaultPaymentDays() == null) {
			entity.setDefaultPaymentDays(merchantEntity.getDefaultPaymentDays());
		}
		entity = clientRepository.save(entity);
		createContact(entity);
		
		return convertClientEntityToClientReq(entity);
	}

	public void createContact(ClientEntity entity) {
		if(!StringUtils.isEmpty(entity.getEmail())) {
		ContactsEntity contactsEntity = new ContactsEntity();
		contactsEntity.setClientEntity(entity);
		contactsEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		contactsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		contactsEntity.setDeleted(false);
		contactsEntity.setName("ADMIN");
		contactsEntity.setRole(ContactsRoleEnum.ADMIN.getValue());
		contactsEntity.setEmail(entity.getEmail());
		contactRepository.save(contactsEntity);
		}
	}

	public ClientEntity convertClientReqToClientEntity(ClientReq clientReq) {
		ClientEntity entity = new ClientEntity();
		entity.setVatNumber(clientReq.getVatNumber());
		entity.setName(clientReq.getName());
		entity.setExIdClient(clientReq.getExIdClient());
		entity.setCf(clientReq.getCf());
		entity.setAddress(clientReq.getAddress());
		entity.setAddressExtra(clientReq.getAddressExtra());
		entity.setCity(clientReq.getCity());
		entity.setCountry(clientReq.getCountry());
		entity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		entity.setEmail(clientReq.getEmail());
        entity.setTelephone(clientReq.getTelephone());
		entity.setZipCode(clientReq.getZipCode());
		entity.setPaCode(clientReq.getPaCode());
		entity.setPec(clientReq.getPec());
		entity.setProvince(clientReq.getProvince());
		entity.setDefaultPaymentDays(clientReq.getDefaultPaymentDays());
		return entity;
	}

	public ClientReq convertClientEntityToClientReq(ClientEntity entity) {
		ClientReq clientReq = new ClientReq();
		clientReq.setId(entity.getId());
		clientReq.setVatNumber(entity.getVatNumber());
		clientReq.setName(entity.getName());
		clientReq.setExIdClient(entity.getExIdClient());
		clientReq.setCf(entity.getCf());
		clientReq.setAddress(entity.getAddress());
		clientReq.setAddressExtra(entity.getAddressExtra());
		clientReq.setCity(entity.getCity());
		clientReq.setCountry(entity.getCountry());
		clientReq.setEmail(entity.getEmail());
		clientReq.setZipCode(entity.getZipCode());
		clientReq.setPaCode(entity.getPaCode());
		clientReq.setPec(entity.getPec());
		clientReq.setProvince(entity.getProvince());
        clientReq.setTelephone(entity.getTelephone());
        clientReq.setMercantId(entity.getMerchantEntity().getId());
        clientReq.setDeleted(entity.getDeleted());
        clientReq.setUpdatedDate(entity.getUpdatedDate());
        clientReq.setCreatedDate(entity.getCreatedDate());
        clientReq.setDefaultPaymentDays(entity.getDefaultPaymentDays());
		if (entity.getWorkflowEntity() != null) {

			clientReq.setWorkflowId(entity.getWorkflowEntity().getId());
			clientReq.setWorkflowName(entity.getWorkflowEntity().getName());
		}
		Pageable pageable = PageRequest.of(0, 1);
		List<TimelineEntity> lastActionPerformed = timelineRepository
				.findLastActionPerformedByClientId(entity.getId(), pageable);
		if(!CollectionUtils.isEmpty(lastActionPerformed)) {
			clientReq.setLastCommunicationDate(lastActionPerformed.get(0).getCreatedDate());
		}
		return clientReq;
	}
	

	public ClientResponseBean getClientById(Long merchantId , Long clientId) {
		ClientResponseBean beanResponse = new ClientResponseBean();
		List<InvoiceReq> clientInvoiceBeans = new ArrayList<>();
		ClientInvoiceDetail invoiceDetail = new ClientInvoiceDetail();
		Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted()) || !clientEntity.get().getMerchantEntity().getId().equals(merchantId)) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		ClientEntity entity =clientEntity.get();
		Double totalDueAndOverdueAmount = invoicesRepository.getTotalDueAndOverdueAmountByClientId(entity.getId());
		Double averageDelayInPayment	 = invoicesRepository.calculateAverageDelayInPaymentByClientId(entity.getId());
		if(totalDueAndOverdueAmount==null) {
			totalDueAndOverdueAmount=0d;
		}
		if(averageDelayInPayment == null || averageDelayInPayment < 0) {
			averageDelayInPayment=0d;
		}
		
	    Pageable pageable = PageRequest.of(0, 10);
		List<Invoices> invoices=invoicesRepository.findByClientEntityIdOrderByDueDateAsc(entity.getId(),pageable);
		
		for (Invoices inv : invoices) {
			InvoiceReq req =invoicesDao.convertInvoicesToInvoiceReq(inv);
			clientInvoiceBeans.add(req);
        }
		invoiceDetail.setTotalDueAndOverdueAmount(Utils.formatDecimal(totalDueAndOverdueAmount));
		invoiceDetail.setAverageDelayInPayment(averageDelayInPayment);
		invoiceDetail.setInvoices(clientInvoiceBeans);
		beanResponse.setClient(convertClientEntityToClientReq(entity));
		beanResponse.setInvoiceDetail(invoiceDetail);
		return beanResponse;
	}

	public ClientReq updateClient(Long merchantId, Long clientId, ClientReq request) {
		Optional<ClientEntity> dbClientEntity = clientRepository.findById(clientId);
		if (!dbClientEntity.isPresent() || !dbClientEntity.get().getMerchantEntity().getId().equals(merchantId) ) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		ClientEntity entity = dbClientEntity.get();
		if(request.getVatNumber()!= null && !request.getVatNumber().equals("") && !entity.getVatNumber().equals(request.getVatNumber())) {
        	ClientEntity clientEntity = clientRepository.findByVatNumberAndMerchantEntityId(request.getVatNumber(), merchantId);

        	if (clientEntity != null) {
        			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(CLIENT_EXISTS));
        		}
        	setIfNotNull(entity::setVatNumber, request.getVatNumber());
        }
	
		
		setIfNotNull(entity::setName, request.getName());
		setIfNotNull(entity::setExIdClient, request.getExIdClient());
		setIfNotNull(entity::setCf, request.getCf());
		setIfNotNull(entity::setAddress, request.getAddress());
		setIfNotNull(entity::setZipCode, request.getZipCode());
		setIfNotNull(entity::setCity, request.getCity());
		setIfNotNull(entity::setProvince, request.getProvince());
		setIfNotNull(entity::setAddressExtra, request.getAddressExtra());
		setIfNotNull(entity::setPaCode, request.getPaCode());
		setIfNotNull(entity::setPec, request.getPec());
		setIfNotNull(entity::setEmail, request.getEmail());
		setIfNotNull(entity::setCountry, request.getCountry());
		setIfNotNull(entity::setDeleted, request.getDeleted());
		setIfNotNull(entity::setTelephone, request.getTelephone());
		setIfNotNull(entity::setDefaultPaymentDays, request.getDefaultPaymentDays());

		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));

		if(request.getWorkflowId() != null) {
			WorkflowEntity workflowEntity = workflowRepository.findByIdAndIsDeleted(request.getWorkflowId(), false);
			if(workflowEntity == null) {
				throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
			}
			
			if(!merchantId.equals(workflowEntity.getMerchantEntity().getId())) {
				throw new CustomValidationException(Status.NOT_FOUND, Translator.toLocale(DIFFERENT_MERCHANTID));
			}
			entity.setWorkflowEntity(workflowEntity);
		}
		
		entity = clientRepository.save(entity);
		return convertClientEntityToClientReq(entity);
	}

	public List<ClientReq> getClientsByMerchantId(Long merchantId) {
		
		List<ClientReq> clients = new ArrayList<>();
		List<ClientEntity> clientEntities = clientRepository.findByMerchantEntityId(merchantId);
		if (!clientEntities.isEmpty()) {
			for (ClientEntity entity : clientEntities) {
				List<Map<String, Object>> totalDue = new ArrayList<>();
				List<Map<String, Object>> totalOverdue = new ArrayList<>();
				ClientReq req = convertClientEntityToClientReq(entity);
				InvoiceDetails invoiceDetails = new InvoiceDetails();
                List<Map<String, Object>> invoiceDetailsMapForDue = invoicesRepository.getTotalAmountByClientIdAndCurrency(entity.getId(), InvoiceStateEnum.DUE.getValue()); 			
                		addTotalDueOrOverDue(totalDue, invoiceDetails, invoiceDetailsMapForDue, InvoiceStateEnum.DUE.getValue());
                		
                		 List<Map<String, Object>> invoiceDetailsMapForOverDue = invoicesRepository.getTotalAmountByClientIdAndCurrency(entity.getId(), InvoiceStateEnum.OVERDUE.getValue());
                		 addTotalDueOrOverDue(totalOverdue, invoiceDetails, invoiceDetailsMapForOverDue, InvoiceStateEnum.OVERDUE.getValue());	
            invoiceDetails.setTotalDue(totalDue);
            invoiceDetails.setTotalOverdue(totalOverdue);
            req.setInvoiceDetails(invoiceDetails);
            clients.add(req);
			}
		}
		return clients;
	}

	private void addTotalDueOrOverDue(List<Map<String, Object>> totalDue, InvoiceDetails invoiceDetails,
			List<Map<String, Object>> invoiceDetailsMap,String invoiceState) {
		if (!invoiceDetailsMap.isEmpty()) {
			for (Map<String, Object> map : invoiceDetailsMap) {
				Map<String, Object> jsonMap = new HashMap<String, Object>();
				setNumberOfDueOrOverdueInvoices(invoiceDetails, invoiceState, map);
				jsonMap.put("amount",Utils.formatDecimal(Double.valueOf(map.get(TOTAL).toString())));
				jsonMap.put(CURRENCY,map.get(CURRENCY));
				totalDue.add(jsonMap);
			}
		}
	}

	private void setNumberOfDueOrOverdueInvoices(InvoiceDetails invoiceDetails, String invoiceState,
			Map<String, Object> map) {
		if(InvoiceStateEnum.DUE.getValue().equals(invoiceState)){
			
			invoiceDetails.setNumOfDueInvoices(invoiceDetails.getNumOfDueInvoices()+Long.valueOf(map.get(NUMOFINVOICES).toString()));
		}else {
			invoiceDetails.setNumOfOverdueInvoices(invoiceDetails.getNumOfOverdueInvoices()+Long.valueOf(map.get(NUMOFINVOICES).toString()));
		}
	}

	public void deleteClientById(Long id , Long clientId) {
		Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
		if (!clientEntity.isPresent() || !clientEntity.get().getMerchantEntity().getId().equals(id)) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		ClientEntity entity = clientEntity.get();
		
		entity.setDeleted(true);
		clientRepository.save(entity);
	}

	private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
		if (value != null) {
			setter.accept(value);
		}
	}

	public List<Invoices> getInvoicesByMerchantIdAndClientId(Long clientId ,Long merchantId,List<String> invoiceState) {
		return invoicesRepository.findAllByClientEntityIdAndClientEntityMerchantEntityIdAndInvoiceState(clientId,merchantId,invoiceState);
	}
	
	public ClientEntity getClientByIdAndMerchantId(Long clientId ,Long merchantId) {
		return clientRepository.findByIdAndMerchantEntityId(clientId, merchantId);
	}

}
