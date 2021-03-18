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
import net.bflows.pagafatture.entities.ActionEntity;
import net.bflows.pagafatture.entities.ClientEntity;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.WorkflowReq;
import net.bflows.pagafatture.model.WorkflowUpdateReq;
import net.bflows.pagafatture.repositories.ActionRepository;
import net.bflows.pagafatture.repositories.ClientRepository;
import net.bflows.pagafatture.repositories.WorkflowRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.Validator;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@Component
public class WorkflowDao {

	@Autowired
	private WorkflowRepository workflowRepository;
	
	@Autowired
	private ActionRepository actionRepository;
	
	@Autowired
	private ActionDao actionDao;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	Validator validator;
	

	public static final String INVALID_WORKFLOW = "invalid_workflow";
	public static final String EMPTY_WORKFLOW_NAME="empty_workflow_name_field";
	public static final String INVALID_CONTACT_DELAY="invalid_contact_delay";
	public static final String WORKFLOW_IS_AlREADY_IN_USE = "WORKFLOW_IS_AlREADY_IN_USE";

	public WorkflowReq createWorkflow(MerchantEntity merchant, WorkflowReq req) {

		WorkflowEntity dbWorkflowEntity = null;
		WorkflowEntity entity = null;
		if (req.getId() != null) {
			dbWorkflowEntity = workflowRepository.findByIdAndIsDeleted(req.getId(),  false);
			if (dbWorkflowEntity != null) {
				entity = createDuplicateWorkflow(dbWorkflowEntity);
				if(!StringUtils.isEmpty(req.getName())) {
					entity.setName(req.getName());
				}
				entity = workflowRepository.save(entity);
				return convertWorkflowEntityToWorkflowReq(entity);
			} else {
				throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
			}
		}
		if(StringUtils.isEmpty(req.getName())) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(EMPTY_WORKFLOW_NAME));
		}
		if(req.getMinimumContactDelay() != null && req.getMinimumContactDelay()<=0) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_CONTACT_DELAY));
		}
		entity = convertWorkflowReqToWorkflowEntity(req);
		entity.setMerchantEntity(merchant);
		entity.setDefaultWorkflow(false);
		entity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		entity.setIsDeleted(false);

		if (StringUtils.isEmpty(entity.getEmail())) {
			entity.setEmail(merchant.getEmail());
		}

		if (entity.getMinimumContactDelay() == null) {
			entity.setMinimumContactDelay(5);
		}
		entity = workflowRepository.save(entity);
		return convertWorkflowEntityToWorkflowReq(entity);
	}
	
	
	
	public List<WorkflowReq> getWorkflowsByMerchantId(Long merchantId) {
		List<WorkflowReq> workflows = new ArrayList<>();
		List<WorkflowEntity> workflowEntities = workflowRepository.findByMerchantEntityIdAndIsDeleted(merchantId,
				false);
		if (!CollectionUtils.isEmpty(workflowEntities)) {
			workflowEntities.forEach(workflow -> {
				WorkflowReq req = convertWorkflowEntityToWorkflowReq(workflow);
				workflows.add(req);
			});
		}
		return workflows;
	}
	
	
	public WorkflowReq updateWorkflow(Long workflowId, WorkflowUpdateReq request, Map<String, Object> userInfo) {

		WorkflowEntity dbWorkflowEntity = workflowRepository.findByIdAndIsDeleted(workflowId, false);
		if (dbWorkflowEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
		}
		validator.validateUserMerchantId(userInfo, dbWorkflowEntity.getMerchantEntity().getId());
		if(request.getMinimumContactDelay() != null && request.getMinimumContactDelay()<=0) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_CONTACT_DELAY));
		}
		setIfNotNull(dbWorkflowEntity::setName, request.getName());
		setIfNotNull(dbWorkflowEntity::setMinimumContactDelay, request.getMinimumContactDelay());
		setIfNotNull(dbWorkflowEntity::setEmail, request.getEmail());
		dbWorkflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		dbWorkflowEntity = workflowRepository.save(dbWorkflowEntity);
		return convertWorkflowEntityToWorkflowReq(dbWorkflowEntity);
	}
	
	public void deleteWorkflow(Long workflowId, Map<String, Object> userInfo) {
		Optional<WorkflowEntity> dbWorkflowEntity = workflowRepository.findById(workflowId);
		if (!dbWorkflowEntity.isPresent()) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
		}
		if(Boolean.TRUE.equals(dbWorkflowEntity.get().getIsDeleted())) {
			return ;
		}
		
		WorkflowEntity entity = dbWorkflowEntity.get();
		List<ClientEntity> clientEntities = clientRepository.findByWorkflowEntityId(entity.getId());
		if(!CollectionUtils.isEmpty(clientEntities)) {
			throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(WORKFLOW_IS_AlREADY_IN_USE)); 
		}
		validator.validateUserMerchantId(userInfo, entity.getMerchantEntity().getId());
		entity.setIsDeleted(true);
		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		workflowRepository.save(entity);
		
	}

	private <T> void setIfNotNull(final Consumer<T> setter, final T value) {
		if (value != null) {
			setter.accept(value);
		}
	}
	

	private WorkflowEntity createDuplicateWorkflow(WorkflowEntity dbWorkflowEntity) {
		WorkflowEntity entity = new WorkflowEntity();
		entity.setName(dbWorkflowEntity.getName() + " copy");
		entity.setEmail(dbWorkflowEntity.getEmail());
		entity.setDefaultWorkflow(dbWorkflowEntity.getDefaultWorkflow());
		entity.setIsDeleted(dbWorkflowEntity.getIsDeleted());
		entity.setMerchantEntity(dbWorkflowEntity.getMerchantEntity());
		entity.setMinimumContactDelay(dbWorkflowEntity.getMinimumContactDelay());
		entity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		entity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		return entity;
	}

	private WorkflowEntity convertWorkflowReqToWorkflowEntity(WorkflowReq req) {
		WorkflowEntity entity = new WorkflowEntity();
		entity.setDefaultWorkflow(req.getDefaultWorkflow());
		entity.setEmail(req.getEmail());
		entity.setIsDeleted(req.getIsDeleted());
		entity.setCreatedDate(req.getCreatedDate());
		entity.setUpdatedDate(req.getUpdatedDate());
		entity.setMinimumContactDelay(req.getMinimumContactDelay());
		entity.setDefaultWorkflow(req.getDefaultWorkflow());
		entity.setName(req.getName());
		return entity;
	}

	private WorkflowReq convertWorkflowEntityToWorkflowReq(WorkflowEntity entity) {
		WorkflowReq req = new WorkflowReq();
		req.setId(entity.getId());
		req.setName(entity.getName());
		req.setDefaultWorkflow(entity.getDefaultWorkflow());
		req.setEmail(entity.getEmail());
		req.setIsDeleted(entity.getIsDeleted());
		req.setCreatedDate(entity.getCreatedDate());
		req.setUpdatedDate(entity.getUpdatedDate());
		req.setMinimumContactDelay(entity.getMinimumContactDelay());
		req.setDefaultWorkflow(entity.getDefaultWorkflow());
		return req;
	}



	public WorkflowReq getWorkflowById(Long workflowId, Map<String, Object> userInfo) {
        WorkflowEntity entity = workflowRepository.findByIdAndIsDeleted(workflowId, false);
        List<ActionReq> actionReqs = new ArrayList<>();
        if(entity == null) {
        	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
        }
        validator.validateUserMerchantId(userInfo, entity.getMerchantEntity().getId());
        WorkflowReq workflowReq= convertWorkflowEntityToWorkflowReq(entity);
        List<ActionEntity> workflowActions= actionRepository.findByWorkflowEntityIdAndIsDeleted(workflowId, false);
        if(!workflowActions.isEmpty()) {
        	workflowActions.forEach(action->{
        		ActionReq actionReq = actionDao.convertActionEntityToActionReq(action);
        		actionReqs.add(actionReq);
        	});
        }
        workflowReq.setActions(actionReqs);
		return workflowReq;
	}

	public void applyWorkFlowToAllClient(Long merchantId, Long workFlowId) {

		WorkflowEntity workflowEntity = workflowRepository.findByIdAndMerchantEntityIdAndIsDeleted(workFlowId,merchantId, false);
		if (workflowEntity == null) {
			throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_WORKFLOW));
		}
		List<ClientEntity> clientEntities = clientRepository.findByMerchantEntityId(merchantId);
		for (ClientEntity clientEntity : clientEntities) {
			clientEntity.setWorkflowEntity(workflowEntity);
			clientEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
		}
		clientRepository.saveAll(clientEntities);
	}
}
