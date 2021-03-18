package net.bflows.pagafatture.dao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.ActionEntity;
import net.bflows.pagafatture.entities.ActionTypeEntity;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.ContactsEntity;
import net.bflows.pagafatture.entities.Invoices;
import net.bflows.pagafatture.entities.TimelineEntity;
import net.bflows.pagafatture.entities.TimelineEntity.TimelineEntityTypeEnum;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.ActionTypeReq;
import net.bflows.pagafatture.model.ClientWithNextAction;
import net.bflows.pagafatture.model.TimelineReq;
import net.bflows.pagafatture.model.UpdateActionReq;
import net.bflows.pagafatture.model.util.MailReq;
import net.bflows.pagafatture.repositories.ActionRepository;
import net.bflows.pagafatture.repositories.ActionTypeRepository;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.InvoicesRepository;
import net.bflows.pagafatture.repositories.TimelineRepository;
import net.bflows.pagafatture.repositories.WorkflowRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.MailUtil;
import net.bflows.pagafatture.util.Utils;
import net.bflows.pagafatture.util.Validator;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@Component
public class ActionDao {

	@Autowired
	private ActionRepository actionRepository;

	@Autowired
	private WorkflowRepository workflowRepository;

	@Autowired
	private ActionTypeRepository actionTypeRepository;
	
	@Autowired
	private TimelineRepository timelineRepository;

	@Autowired
	private InvoicesRepository invoicesRepository;


	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private ObjectMapper mapper;
	
	 @Value("${senderEmail}")
	public String senderEmail;
	 
	@Autowired
	private MailUtil mailUtil;	
	
	public static final String MESSAGE="message";
	public static final String RECIPIENTS="recipients";
	public static final String CC="CC";
	public static final String BCC="BCC";
	public static final String SUBJECT="subject";
	public static final String CALLSCRIPT="callScript";
	protected static final List<String> MANUAL_EMAIL_KEYS = Arrays.asList(RECIPIENTS,SUBJECT,MESSAGE);
//	protected static final List<String> AUTOMATIC_EMAIL_KEYS = Arrays.asList(RECIPIENTS,SUBJECT,MESSAGE);
	protected static final List<String> LETTER_KEYS = Arrays.asList(MESSAGE);
	public static final List<String> TELEPHONE_CALL_KEYS = Arrays.asList(RECIPIENTS,CALLSCRIPT);
	
	public final Logger log = LoggerFactory.getLogger(ActionDao.class);

	public static final String INVALID_WORKFLOW = "invalid_workflow";
	public static final String EMPTY_ACTION_FIELDS = "empty_action_fields";
	public static final String INVALID_ACTION_TYPE = "invalid_action_type";
	public static final String INVALID_ACTION = "invalid_action";
	public static final String INVALID_ACTION_JSON = "invalid_action_json";
	public static final String INVALID_INVOICE = "invalid_invoice";
	public static final String INVALID_CLIENT = "invalid_client";
	public static final String DIFFERENT_WORKFLOW = "different_workflow";
	public static final String EMPTY_CLIENTID_INVOICEID = "empty_clientId_invoiceId";
	public static final String INVALID_EMAIL_ACTION_JSON = "invalid_email_action_json";
	public static final String ACTIONTYPEID="actionTypeId";
	public static final String ACTIONTYPENAME="actionTypeName";
	public ActionReq createAction(Long workflowId, ActionReq request, Map<String, Object> userInfo) {

		WorkflowEntity workflowEntity = validateWorkflowId(workflowId);
		validator.validateUserMerchantId(userInfo, workflowEntity.getMerchantEntity().getId());
		ActionEntity actionEntity = null;
		if (request.getId() != null) {
			ActionEntity dbActionEntity = actionRepository.findByIdAndIsDeleted(request.getId(), false);
			if (dbActionEntity != null) {
				actionEntity = createDuplicateAction(dbActionEntity);
				if (!StringUtils.isEmpty(request.getName())) {
					actionEntity.setName(request.getName());
				}
				actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
				actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
				actionEntity.setIsDeleted(false);
				actionEntity = actionRepository.save(actionEntity);
				return convertActionEntityToActionReq(actionEntity);
			} else {
				throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ACTION));
			}
		}

		if (StringUtils.isEmpty(request.getName()) || CollectionUtils.isEmpty(request.getActionJson())
				|| request.getActionTypeId() == null || request.getTriggerDays() == null) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(EMPTY_ACTION_FIELDS));
		}
		ActionTypeEntity actionTypeEntity = validateActionTypeId(request.getActionTypeId());
		actionEntity = convertActionReqToActionEnitiy(request);
		actionEntity.setActionType(actionTypeEntity);
		validateActionJson(request.getActionJson() , actionTypeEntity);
		actionEntity.setWorkflowEntity(workflowEntity);
		actionEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity.setIsDeleted(false);
		actionEntity = actionRepository.save(actionEntity);
		return convertActionEntityToActionReq(actionEntity);
	}

	private void validateActionJson(Map<String, Object> actionJson, ActionTypeEntity actionTypeEntity) {
		for (Map.Entry<String, Object> entry : actionJson.entrySet()) {
			if (StringUtils.isEmpty(entry.getKey())) {
				throw new CustomValidationException(Status.VALIDATION_ERROR,
						Translator.toLocale(INVALID_ACTION_JSON));
			}
		}
		if(actionTypeEntity.getId().equals(1l)) {
            validateJsonKeys(actionJson, MANUAL_EMAIL_KEYS);
            validateEmailKeys(actionJson);
		}
		/*if(actionTypeEntity.getId().equals(2l)) {
			validateJsonKeys(actionJson,AUTOMATIC_EMAIL_KEYS);
			validateEmailKeys(actionJson);
		}*/
		if(actionTypeEntity.getId().equals(3l)) {
			validateJsonKeys(actionJson,LETTER_KEYS);
		}
		if(actionTypeEntity.getId().equals(4l)) {
			validateJsonKeys(actionJson,TELEPHONE_CALL_KEYS);
			
		}
		
	}
	
	
	private void validateEmailKeys(Map<String, Object> json) {
		
		try {
			
			if (CollectionUtils.isEmpty((List<String>) json.get(RECIPIENTS))
					|| (json.get(CC) != null && !(json.get(CC) instanceof List<?>))
					||  (json.get(BCC) != null && !(json.get(BCC) instanceof List<?>))) {
				
				throw new CustomValidationException(Status.VALIDATION_ERROR,
						Translator.toLocale(INVALID_EMAIL_ACTION_JSON));
			}

			
		} catch (Exception e) {
			throw new CustomValidationException(Status.VALIDATION_ERROR,
					Translator.toLocale(INVALID_EMAIL_ACTION_JSON));
		}
	}
	

	private void validateJsonKeys(Map<String, Object> actionJson, List<String> validkeys) {
		for (String key : validkeys) {
			if(!actionJson.containsKey(key)) {
				throw new CustomValidationException(Status.VALIDATION_ERROR,
						Translator.toLocale(INVALID_ACTION_JSON));
			}
		}
		
	}

	private WorkflowEntity validateWorkflowId(Long workflowId) {
		WorkflowEntity workflowEntity = workflowRepository.findByIdAndIsDeleted(workflowId, false);
		if (workflowEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
		}
		return workflowEntity;
	}

	private ActionTypeEntity validateActionTypeId(Long typeId) {
		Optional<ActionTypeEntity> actionTypeEntity = actionTypeRepository.findById(typeId);

		if (!actionTypeEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ACTION_TYPE));
		}
		return actionTypeEntity.get();
	}

	public List<ActionTypeReq> getActionTypes() {
		List<ActionTypeReq> actionTypeReqs = new ArrayList<>();
		List<ActionTypeEntity> actionTypeEntities =actionTypeRepository.findAll();
		for (ActionTypeEntity actionTypeEntity : actionTypeEntities) {
			
			ActionTypeReq req =convertActionTypeEntityToActionTypeReq(actionTypeEntity);
			actionTypeReqs.add(req);
		}
		return actionTypeReqs;
	}

	private ActionTypeReq convertActionTypeEntityToActionTypeReq(ActionTypeEntity actionTypeEntity) {
		ActionTypeReq req = new ActionTypeReq();
		req.setId(actionTypeEntity.getId());
		req.setName(actionTypeEntity.getName());
		req.setCreatedDate(actionTypeEntity.getCreatedDate());
		req.setUpdatedDate(actionTypeEntity.getUpdatedDate());
		Map<String, Object> actionTypes = Utils.convertIntoJson(actionTypeEntity.getJson());
		req.setJson(actionTypes);
		return req;
	}
	private ActionEntity createDuplicateAction(ActionEntity dbActionEntity) {
		ActionEntity entity = new ActionEntity();
		entity.setActionJson(dbActionEntity.getActionJson());
		entity.setName(dbActionEntity.getName() + " copy");
		entity.setActionType(dbActionEntity.getActionType());
		entity.setIsDeleted(dbActionEntity.getIsDeleted());
		entity.setTriggerDays(dbActionEntity.getTriggerDays());
		entity.setWorkflowEntity(dbActionEntity.getWorkflowEntity());
		return entity;
	}

	public ActionReq convertActionEntityToActionReq(ActionEntity actionEntity) {
		ActionReq req = new ActionReq();
		req.setId(actionEntity.getId());
		req.setActionJson(Utils.convertIntoJson(actionEntity.getActionJson()));
		req.setIsDeleted(actionEntity.getIsDeleted());
		req.setCreatedDate(actionEntity.getCreatedDate());
		req.setUpdatedDate(actionEntity.getUpdatedDate());
		req.setActionTypeId(actionEntity.getActionType().getId());
		req.setActionTypeName(actionEntity.getActionType().getName());
		req.setName(actionEntity.getName());
		req.setTriggerDays(actionEntity.getTriggerDays());
		return req;
	}

	private ActionEntity convertActionReqToActionEnitiy(ActionReq request) {

		ActionEntity actionEntity = new ActionEntity();
		actionEntity.setName(request.getName());
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),true);
		String json =Utils.convertMapIntoJsonString(request.getActionJson());
		actionEntity.setActionJson(json);
		actionEntity.setTriggerDays(request.getTriggerDays());
		return actionEntity;
	}
	
	public ActionReq updateAction(Long workflowId, Long actionId, UpdateActionReq request, Map<String, Object> userInfo) {
		WorkflowEntity workflowEntity =validateWorkflowId(workflowId);
		validator.validateUserMerchantId(userInfo, workflowEntity.getMerchantEntity().getId());
		ActionEntity actionEntity = actionRepository.findByIdAndIsDeleted(actionId, false);
		if (actionEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ACTION));
		}
		if(!actionEntity.getWorkflowEntity().getId().equals(workflowId)) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ACTION));
		}
		if (request.getActionTypeId() != null) {
			ActionTypeEntity entity = validateActionTypeId(request.getActionTypeId());
			actionEntity.setActionType(entity);
		}
		setIfNotNull(actionEntity::setName, request.getName());
		setIfNotNull(actionEntity::setTriggerDays, request.getTriggerDays());
		if(request.getActionJson() != null) {
			validateActionJson(request.getActionJson(), actionEntity.getActionType());
			setIfNotNull(actionEntity::setActionJson, Utils.convertMapIntoJsonString(request.getActionJson()));
		}
		actionEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionEntity = actionRepository.save(actionEntity);
		return convertActionEntityToActionReq(actionEntity);
	}

	private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
		if (value != null) {
			setter.accept(value);
		}
	}

	public void deleteAction(Long workflowId, Long actionId, Map<String, Object> userInfo) {

		WorkflowEntity workflowEntity =validateWorkflowId(workflowId);
		validator.validateUserMerchantId(userInfo, workflowEntity.getMerchantEntity().getId());

		Optional<ActionEntity> dbActionEntity = actionRepository.findById(actionId);
		if (!dbActionEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ACTION));
		}
		
		ActionEntity entity = dbActionEntity.get();
		if(!entity.getWorkflowEntity().getId().equals(workflowId)) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ACTION));
		}
		if (Boolean.TRUE.equals(entity.getIsDeleted())) {
			return;
		}
		entity.setIsDeleted(true);
		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		actionRepository.save(entity);
	}
	
	public TimelineReq performAction(Long actionId, TimelineReq timelineReq, Map<String, Object> userInfo) {
		
		
		TimelineEntity timelineEntity = new TimelineEntity();

		ActionEntity dbActionEntity = actionRepository.findByIdAndIsDeleted(actionId, false);
		if (dbActionEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ACTION));
		}

		checkActionJson(timelineReq, dbActionEntity);
		
		if (timelineReq.getClientId() == null || timelineReq.getInvoiceId() == null) {
			throw new CustomValidationException(Status.BAD_REQUEST, Translator.toLocale(EMPTY_CLIENTID_INVOICEID));
		}
		Optional<Invoices> invoiceEntity = invoicesRepository.findById(timelineReq.getInvoiceId());

		if (!invoiceEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_INVOICE));
		}

		Optional<ClientEntity> dbClientEntity = clientRepository.findById(timelineReq.getClientId());
		if (!dbClientEntity.isPresent() || Boolean.TRUE.equals(dbClientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}
		ClientEntity clientEntity = dbClientEntity.get();
		if (!clientEntity.getWorkflowEntity().getId().equals(dbActionEntity.getWorkflowEntity().getId())) {
			throw new CustomValidationException(Status.BAD_REQUEST, Translator.toLocale(DIFFERENT_WORKFLOW));
		}
  
		validator.validateUserMerchantId(userInfo, clientEntity.getMerchantEntity().getId());

		ActionTypeEntity actionTypeEntity = dbActionEntity.getActionType();
		
		
		timelineEntity.setInvoice(invoiceEntity.get());
		timelineEntity.setClientEntity(clientEntity);
		timelineEntity.setActionEntity(dbActionEntity);
		timelineEntity.setReschedule(timelineReq.getReschedule());
		timelineEntity.setSkip(timelineReq.getSkip());
		timelineEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		timelineEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		if (timelineEntity.getReschedule() == null) {
			timelineEntity.setReschedule(false);
		}
		if (timelineEntity.getSkip() == null) {
			timelineEntity.setSkip(false);
		}
		if (Boolean.FALSE.equals(timelineEntity.getReschedule()) && Boolean.FALSE.equals(timelineEntity.getSkip())) {
			
		/*	if(actionTypeEntity.getId().equals(2l)) {
				performAction(actionTypeEntity, timelineEntity, Utils.convertIntoJson(dbActionEntity.getActionJson()));		
			}else{*/
			   validateActionJson(timelineReq.getActionJson(), actionTypeEntity);
			   timelineReq.getActionJson().put(ACTIONTYPEID, actionTypeEntity.getId());
			   timelineReq.getActionJson().put("actionName", dbActionEntity.getName());
			   timelineEntity.setActionJson(Utils.convertMapIntoJsonString(timelineReq.getActionJson()));
			   if(actionTypeEntity.getId().equals(4l)) {
					timelineEntity.setMessage(timelineReq.getActionJson().get(CALLSCRIPT).toString());
				}
			   performAction(actionTypeEntity, timelineEntity,timelineReq.getActionJson());
//			}
		}
		
		if (StringUtils.isEmpty(timelineEntity.getStatus())) {
			timelineEntity.setStatus("SUCCESS");
		}
		timelineEntity.setType(TimelineEntityTypeEnum.ACTION.getValue());
		timelineEntity = timelineRepository.save(timelineEntity);

		return convertTimelineEntityToTimelineReq(timelineEntity);
	}

	private void checkActionJson(TimelineReq timelineReq, ActionEntity dbActionEntity) {
		if(!((timelineReq.getReschedule() != null &&  Boolean.TRUE.equals(timelineReq.getReschedule()))
				|| (timelineReq.getSkip() != null	&& Boolean.TRUE.equals(timelineReq.getSkip())))) 
			{		
				if(!dbActionEntity.getActionType().getId().equals(2l) && timelineReq.getActionJson() == null) {
						throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_ACTION_JSON));		
				}
			}
	}

	private TimelineReq convertTimelineEntityToTimelineReq(TimelineEntity timelineEntity) {
		TimelineReq timelineReq = new TimelineReq();
		timelineReq.setId(timelineEntity.getId());
		if(timelineEntity.getActionEntity() != null) {
			timelineReq.setActionId(timelineEntity.getActionEntity().getId());
		}
		timelineReq.setClientId(timelineEntity.getClientEntity().getId());
		timelineReq.setInvoiceId(timelineEntity.getInvoice().getId());
		timelineReq.setMessage(timelineEntity.getMessage());
		timelineReq.setStatus(timelineEntity.getStatus());
		timelineReq.setSkip(timelineEntity.getSkip());
		timelineReq.setReschedule(timelineEntity.getReschedule());
		timelineReq.setCreatedDate(timelineEntity.getCreatedDate());
		timelineReq.setUpdatedDate(timelineEntity.getUpdatedDate());
		timelineReq.setType(TimelineEntityTypeEnum.fromValue(timelineEntity.getType()));
		Map<String, Object> jsonMap = Utils.convertIntoJson(timelineEntity.getActionJson());
		if(jsonMap != null && jsonMap.get(ACTIONTYPEID) != null) {
			jsonMap.put(ACTIONTYPENAME, timelineEntity.getActionEntity().getActionType().getName());
		}
		timelineReq.setActionJson(jsonMap);
		return timelineReq;
	}

	private void performAction(ActionTypeEntity actionTypeEntity,
			TimelineEntity timelineEntity, Map<String, Object> actionJson) {
		try {
			if (actionTypeEntity.getId().equals(1l)) {
				sendManualEmail(actionJson);
			}
			/*else if (actionTypeEntity.getId().equals(2l)) {
				sendAutomaticEmail(actionJson);
			}*/

		} catch (Exception e) {
			log.error(e.getMessage());
			timelineEntity.setStatus("FAILURE");
		}

	}

/*	private void sendAutomaticEmail(Map<String, Object> json) {
		MailReq mailReq = new MailReq();
		if (json.get(RECIPIENTS) != null) {
		
			mailReq.setRecipients((List<String>)json.get(RECIPIENTS));
		}

		if (json.get(CC) != null) {
			List<String> cCList = (List<String>)json.get(CC);
			mailReq.setCC(cCList);
		}

		if (json.get(BCC) != null) {
			List<String> bCCList = (List<String>)json.get(BCC);
			mailReq.setBCC(bCCList);
		}

		setMailReq(json, mailReq);

	}
*/
	private void setMailReq(Map<String, Object> json, MailReq mailReq) {
		String subject = (String) json.get(SUBJECT);
		String message = (String) json.get(MESSAGE);
		Map<String, String> params = new HashMap<>();
		params.put("body", message);
		mailReq.setSenderEmail(senderEmail);
		mailReq.setTemplateId(10l);
		mailReq.setSubject(subject);
		mailReq.setParams(params);
		if (Boolean.FALSE.equals(mailUtil.isTestProfileActivated())) {
			mailUtil.sendMailForAction(mailReq);
		}
	}
	
	
	private void sendManualEmail(Map<String, Object> json) {
		MailReq mailReq = new MailReq();
		if (json.get(RECIPIENTS) != null) {
			mailReq.setRecipients(getContactEmail(json.get(RECIPIENTS)));
		}

		if (json.get(CC) != null) {
			mailReq.setCC(getContactEmail(json.get(CC)));
		}

		if (json.get(BCC) != null) {
			mailReq.setBCC(getContactEmail(json.get(BCC)));
		}

		setMailReq(json, mailReq);

	}

	private List<String> getContactEmail(Object json) {
		List<String> contactIds= new ArrayList<>();
		for (Object object : (List<Object>)json) {
			ContactsEntity contactsEntity = mapper.convertValue(object, ContactsEntity.class);
			contactIds.add(contactsEntity.getEmail());
		}
		return contactIds;
	}

	public List<ClientWithNextAction> getNextActionsByMerchantId(Long merchantId, Boolean callByScheduler) {
		List<ClientWithNextAction> clientWithNextActions = new ArrayList<>();
		LocalDateTime todaysDate = DateTimeUtil.DateServerToUTC(LocalDateTime.now());
		List<ClientEntity> clients = clientRepository.findByMerchantEntityId(merchantId);
		if (clients.isEmpty()) {
			return clientWithNextActions;
		}

		for (ClientEntity clientEntity : clients) {
			List<Invoices> invoices = invoicesRepository.findByClientEntityId(clientEntity.getId());
			if (clientEntity.getWorkflowEntity() != null) {

				WorkflowEntity workflowEntity = workflowRepository
						.findByIdAndIsDeleted(clientEntity.getWorkflowEntity().getId(), false);
				Pageable pageable = PageRequest.of(0, 1);
				List<TimelineEntity> lastActionPerformed = timelineRepository
						.findLastActionPerformedByClientId(clientEntity.getId(), pageable);
				Long daysBetween = null;
				if (!CollectionUtils.isEmpty(lastActionPerformed)) {
					daysBetween = Duration.between(lastActionPerformed.get(0).getCreatedDate(), todaysDate).toDays();
				}

				if (daysBetween == null || daysBetween > workflowEntity.getMinimumContactDelay()) {

					List<ActionEntity> actions = actionRepository
							.findByWorkflowEntityIdAndIsDeleted(workflowEntity.getId(), false);

					ClientWithNextAction clientWithNextAction = getClosestAction(todaysDate, invoices, actions, clientEntity, callByScheduler);
					if (clientWithNextAction != null) {
						clientWithNextAction.setClientId(clientEntity.getId());
						clientWithNextAction.setName(clientEntity.getName());
						clientWithNextActions.add(clientWithNextAction);

					}

				}
			}
		}
		return clientWithNextActions;
	}

	
	
	public ClientWithNextAction getClosestAction(LocalDateTime todaysDate, List<Invoices> invoices,
			List<ActionEntity> actions, ClientEntity clientEntity, Boolean callByScheduler) {
		ClientWithNextAction clientWithNextAction = null;
		LocalDateTime closestInvoiceDueDate = null;
		Date closestActionDate = null;
		for (ActionEntity actionEntity : actions) {
			if(!actionEntity.getActionType().getId().equals(2l) || callByScheduler) {
				
				for (Invoices invoice : invoices) {
					
					TimelineEntity timelineEntity = timelineRepository.findByClientIdAndInvoiceIdAndActionId(
							clientEntity.getId(), invoice.getId(), actionEntity.getId());
					Date currentDate = DateTimeUtil.getDateWithoutTime(todaysDate);
					Date dueDate = DateTimeUtil
							.getDateWithoutTime(invoice.getDueDate().plusDays(actionEntity.getTriggerDays()));
					if ((timelineEntity == null)
							&& (currentDate.compareTo(dueDate) > 0 || currentDate.compareTo(dueDate) == 0)
							&& (closestInvoiceDueDate == null || invoice.getDueDate().isBefore(closestInvoiceDueDate)
							|| invoice.getDueDate().equals(closestInvoiceDueDate))
							&& (closestActionDate == null || dueDate.before(closestActionDate))) {
						closestActionDate = dueDate;
						closestInvoiceDueDate = invoice.getDueDate();
						clientWithNextAction = new ClientWithNextAction();
						clientWithNextAction.setInvoiceId(invoice.getId());
						clientWithNextAction.setNextAction(convertActionEntityToActionReq(actionEntity));
						clientWithNextAction.setInvoiceAmount(Utils.formatDecimalForBigDecimal(invoice.getAmountGross()));
						clientWithNextAction.setCurrency(invoice.getCurrency());
					}
				}
			}
		}
		return clientWithNextAction;
	}

 

 
	public List<TimelineReq> getPerformedActionsByClientId(Long clientId, Map<String, Object> userInfo) {
		List<TimelineReq> performedActionList = new ArrayList<>();
		Optional<ClientEntity> dbClientEntity = clientRepository.findById(clientId);
		if (!dbClientEntity.isPresent() || Boolean.TRUE.equals(dbClientEntity.get().getDeleted())) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_CLIENT));
		}

		validator.validateUserMerchantId(userInfo, dbClientEntity.get().getMerchantEntity().getId());
		List<TimelineEntity> performedActions = timelineRepository.findByClientEntityId(clientId);
		for (TimelineEntity timelineEntity : performedActions) {
			TimelineReq timelineReq = convertTimelineEntityToTimelineReq(timelineEntity);
			performedActionList.add(timelineReq);
		}
		return performedActionList;
	}
	
	public List<TimelineReq> getPerformedActionsByInvoiceId(Long invoiceId, Map<String, Object> userInfo) {
		List<TimelineReq> performedActionList = new ArrayList<>();
		Optional<Invoices> invoiceEntity = invoicesRepository.findById(invoiceId);

		if (!invoiceEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_INVOICE));
		}

		validator.validateUserMerchantId(userInfo, invoiceEntity.get().getClientEntity().getMerchantEntity().getId());
		List<TimelineEntity> performedActions = timelineRepository.findByInvoiceId(invoiceId);
		for (TimelineEntity timelineEntity : performedActions) {
			TimelineReq timelineReq = convertTimelineEntityToTimelineReq(timelineEntity);
			performedActionList.add(timelineReq);
		}
		return performedActionList;
	}
}
