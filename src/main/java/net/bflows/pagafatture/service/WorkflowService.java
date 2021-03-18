package net.bflows.pagafatture.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.ActionTypeReq;
import net.bflows.pagafatture.model.UpdateActionReq;
import net.bflows.pagafatture.model.WorkflowReq;
import net.bflows.pagafatture.model.WorkflowUpdateReq;

public interface WorkflowService {

	public WorkflowReq createWorkflow(HttpServletRequest httpServletRequest, Long merchantId, WorkflowReq request);

	public List<WorkflowReq> getWorkflowsByMerchantId(HttpServletRequest httpServletRequest, Long merchantId);
	
	public ActionReq createAction(HttpServletRequest httpServletRequest, Long workflowId, ActionReq request);
	
	public List<ActionTypeReq> getActionTypes();
	

	public WorkflowReq updateWorkflow(HttpServletRequest httpServletRequest, Long workflowId, WorkflowUpdateReq request);

	public void deleteWorkflow(HttpServletRequest httpServletRequest, Long workflowId);
	
	public ActionReq updateAction(HttpServletRequest httpServletRequest, Long workflowId, Long actionId, UpdateActionReq request);

	public void deleteAction(HttpServletRequest httpServletRequest, Long workflowId, Long actionId);
	
	public WorkflowReq getWorkflowsById(HttpServletRequest httpServletRequest, Long workflowId);
	
	void applyWorkFlowToAllClient(HttpServletRequest httpServletRequest, Long merchantId, Long workFlowId) ;
}
