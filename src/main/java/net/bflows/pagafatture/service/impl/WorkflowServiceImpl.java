package net.bflows.pagafatture.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.ActionDao;
import net.bflows.pagafatture.dao.WorkflowDao;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.ActionTypeReq;
import net.bflows.pagafatture.model.UpdateActionReq;
import net.bflows.pagafatture.model.WorkflowReq;
import net.bflows.pagafatture.model.WorkflowUpdateReq;
import net.bflows.pagafatture.service.WorkflowService;
import net.bflows.pagafatture.util.Validator;

@Service
public class WorkflowServiceImpl implements WorkflowService {

	@Autowired
	private WorkflowDao workflowDao;

	@Autowired
	ActionDao actionDao;
	
	@Autowired
	Validator validator;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public WorkflowReq createWorkflow(HttpServletRequest httpServletRequest, Long merchantId, WorkflowReq request) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		MerchantEntity merchantEntity =validator.validateUserMerchantId(userInfo, merchantId);
		return workflowDao.createWorkflow(merchantEntity, request);
	}

	@Override
	@Transactional(readOnly = true)
	public List<WorkflowReq> getWorkflowsByMerchantId(HttpServletRequest httpServletRequest, Long merchantId) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, merchantId);
		return workflowDao.getWorkflowsByMerchantId(merchantId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ActionReq createAction(HttpServletRequest httpServletRequest, Long workflowId, ActionReq request) {
		return actionDao.createAction(workflowId, request, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionTypeReq> getActionTypes() {
		return actionDao.getActionTypes();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public WorkflowReq updateWorkflow(HttpServletRequest httpServletRequest, Long workflowId, WorkflowUpdateReq request) {
		return workflowDao.updateWorkflow(workflowId, request, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteWorkflow(HttpServletRequest httpServletRequest, Long workflowId) {
          workflowDao.deleteWorkflow(workflowId, validator.getUserInfoFromToken(httpServletRequest));		
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ActionReq updateAction(HttpServletRequest httpServletRequest, Long workflowId, Long actionId, UpdateActionReq request) {
		return actionDao.updateAction(workflowId, actionId, request, validator.getUserInfoFromToken(httpServletRequest));
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteAction(HttpServletRequest httpServletRequest, Long workflowId, Long actionId) {
		actionDao.deleteAction(workflowId, actionId, validator.getUserInfoFromToken(httpServletRequest));
	}
	
	@Override
	@Transactional(readOnly = true)
	public WorkflowReq getWorkflowsById(HttpServletRequest httpServletRequest, Long workflowId) {
		return workflowDao.getWorkflowById(workflowId, validator.getUserInfoFromToken(httpServletRequest));
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void applyWorkFlowToAllClient(HttpServletRequest httpServletRequest, Long merchantId, Long workFlowId) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, merchantId);
		workflowDao.applyWorkFlowToAllClient(merchantId, workFlowId);
	}
}
