package net.bflows.pagafatture.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import net.bflows.pagafatture.entities.ContactsEntity;
import net.bflows.pagafatture.entities.ContactsEntity.ContactsRoleEnum;
import net.bflows.pagafatture.model.ContactReq;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.ContactRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Validator;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@Component
public class ContactDao {

	public static final String INVALID_CLIENT = "invalid_client";
	public static final String INVALID_CONTACT = "invalid_contact";
	public static final String INVALID_CONTACT_ID = "contact_Id_not_found";
	public static final String EMPTY_NAME_OR_EMAILFIELD ="empty_name_or_emailfield";


	@Autowired
	ClientRepository clientRepository;

	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	Validator validator;

	public ContactReq createContact(Long clientId, ContactReq request, Map<String, Object> userInfo) {
		if(StringUtils.isEmpty(request.getName()) ||StringUtils.isEmpty(request.getEmail())) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(EMPTY_NAME_OR_EMAILFIELD));
		}
		
		Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		
		validator.validateUserMerchantId(userInfo, clientEntity.get().getMerchantEntity().getId());
		
		ContactsEntity entity = convertContactRequestToContactEntity(request);
		entity.setClientEntity(clientEntity.get());
		entity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		if(entity.getRole() == null) {
			entity.setRole(ContactsRoleEnum.ADMIN.getValue());
		}
		entity.setDeleted(false);
		entity = contactRepository.save(entity);
		return convertContactEntityToContactRequest(entity);
	}

	public ContactReq updateContact(Long clientId, Long contactId, ContactReq request, Map<String, Object> userInfo) {
		Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_CLIENT));
		}
		validator.validateUserMerchantId(userInfo, clientEntity.get().getMerchantEntity().getId());
		Optional<ContactsEntity> contacts = contactRepository.findById(contactId);

		if (!contacts.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CONTACT));
		}
		ContactsEntity contactsEntity= contacts.get();
		if (!contactsEntity.getClientEntity().getId().equals(clientId) ) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CONTACT_ID));
		}
		setIfNotNull(contactsEntity::setName, request.getName());
		setIfNotNull(contactsEntity::setEmail, request.getEmail());
		setIfNotNull(contactsEntity::setPhone, request.getPhone());
		setIfNotNull(contactsEntity::setLastName, request.getLastName());
		if(request.getRole() != null) {
			contactsEntity.setRole(request.getRole().getValue());
		}
		contactsEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		contactsEntity = contactRepository.save(contactsEntity);
		return convertContactEntityToContactRequest(contactsEntity);
	}

	public ContactReq getContactById(Long clientId, Long contactId, Map<String, Object> userInfo) {
		Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		
		validator.validateUserMerchantId(userInfo, clientEntity.get().getMerchantEntity().getId());
		Optional<ContactsEntity> entity = contactRepository.findById(contactId);

		if (!entity.isPresent() || entity.get().getDeleted() || !entity.get().getClientEntity().getId().equals(clientId)) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CONTACT));
		}
		ContactsEntity contactsEntity = entity.get();
		return convertContactEntityToContactRequest(contactsEntity);
	}
	
	public List<ContactReq> getContactByClientId(Long clientId, Map<String, Object> userInfo) {
		List<ContactReq> contactReqs= new ArrayList<ContactReq>();
		Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		
		validator.validateUserMerchantId(userInfo, clientEntity.get().getMerchantEntity().getId());
		List<ContactsEntity> contacts = contactRepository.findByClientEntityId(clientId);
		
		if(!CollectionUtils.isEmpty(contacts)) {
			contacts.forEach(entity->{
				 ContactReq contactReq=convertContactEntityToContactRequest(entity);
				 contactReqs.add(contactReq);
			});
		}
	return contactReqs;
	}

	public void deleteContact(Long clientId, Long contactId, Map<String, Object> userInfo) {
		Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
		if (!clientEntity.isPresent() || Boolean.TRUE.equals(clientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		
		validator.validateUserMerchantId(userInfo, clientEntity.get().getMerchantEntity().getId());
		Optional<ContactsEntity> entity = contactRepository.findById(contactId);

		if (!entity.isPresent() || !entity.get().getClientEntity().getId().equals(clientId)) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CONTACT));
		}
		ContactsEntity contactsEntity = entity.get();
		
		contactsEntity.setDeleted(true);
		contactRepository.save(contactsEntity);
	}

	private ContactsEntity convertContactRequestToContactEntity(ContactReq request) {
		ContactsEntity entity = new ContactsEntity();
		entity.setEmail(request.getEmail());
		entity.setName(request.getName());
		entity.setPhone(request.getPhone());
		entity.setLastName(request.getLastName());
		if(request.getRole() != null) {
			entity.setRole(request.getRole().getValue());
		}
		return entity;
	}

	private ContactReq convertContactEntityToContactRequest(ContactsEntity entity) {
		ContactReq request = new ContactReq();
		request.setEmail(entity.getEmail());
		request.setName(entity.getName());
		request.setPhone(entity.getPhone());
		request.setRole(ContactsRoleEnum.fromValue(entity.getRole()));
		request.setId(entity.getId());
		request.setLastName(entity.getLastName());
		return request;
	}

	private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
		if (value != null) {
			setter.accept(value);
		}
	}
}
