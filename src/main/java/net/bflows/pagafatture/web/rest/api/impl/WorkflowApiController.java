package net.bflows.pagafatture.web.rest.api.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.ActionTypeReq;
import net.bflows.pagafatture.model.UpdateActionReq;
import net.bflows.pagafatture.model.WorkflowReq;
import net.bflows.pagafatture.model.WorkflowUpdateReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.WorkflowService;
import net.bflows.pagafatture.web.rest.api.WorkflowApi;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class WorkflowApiController implements WorkflowApi {

	public final Logger log = LoggerFactory.getLogger(WorkflowApiController.class);

	@Autowired
	WorkflowService workflowService;

	@Override
	public ResponseEntity<Response<WorkflowReq>> createWorkflow(HttpServletRequest httpServletRequest, Long merchantId, WorkflowReq body) {

		WorkflowReq req = workflowService.createWorkflow(httpServletRequest,merchantId, body);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Response<WorkflowReq>(req, Status.SUCCESS, Translator.toLocale("create_workflow_se_msg")));

	}
	
	@Override
	public ResponseEntity<Response<List<WorkflowReq>>> getWorkflowsByMerchantId(HttpServletRequest httpServletRequest, Long merchantId) {

		List<WorkflowReq> workflows = workflowService.getWorkflowsByMerchantId(httpServletRequest, merchantId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<List<WorkflowReq>>(workflows, Status.SUCCESS, Translator.toLocale("get_workflow_se_msg")));

	}
	
	@Override
	public ResponseEntity<Response<ActionReq>> createAction(HttpServletRequest httpServletRequest, Long workflowId, ActionReq body) {

		ActionReq req = workflowService.createAction(httpServletRequest, workflowId, body);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Response<ActionReq>(req, Status.SUCCESS, Translator.toLocale("create_action_se_msg")));

	}
	
	@Override
	public ResponseEntity<Response<List<ActionTypeReq>>> getActionTypes() {

		List<ActionTypeReq> actionTypes = workflowService.getActionTypes();

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<List<ActionTypeReq>>(actionTypes, Status.SUCCESS, Translator.toLocale("get_action_type_se_msg")));
	}

	@Override
	public ResponseEntity<Response<WorkflowReq>> updateWorkflow(HttpServletRequest httpServletRequest, Long workflowId, WorkflowUpdateReq request) {
		
		WorkflowReq req = workflowService.updateWorkflow(httpServletRequest, workflowId, request);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<WorkflowReq>(req, Status.SUCCESS, Translator.toLocale("update_workflow_se_msg")));
	}
	
	
	@Override
	public ResponseEntity<Response<Void>> deleteWorkflow(HttpServletRequest httpServletRequest, Long workflowId) {
		
		workflowService.deleteWorkflow(httpServletRequest, workflowId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<Void>(null, Status.SUCCESS, Translator.toLocale("delete_workflow_se_msg")));
	}
	
	@Override
	public ResponseEntity<Response<ActionReq>> updateAction(HttpServletRequest httpServletRequest, Long workflowId, Long actionId, UpdateActionReq body) {

		ActionReq req = workflowService.updateAction(httpServletRequest, workflowId, actionId,body);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<ActionReq>(req, Status.SUCCESS, Translator.toLocale("update_action_se_msg")));

	}
	
	@Override
	public ResponseEntity<Response<Void>> deleteAction(HttpServletRequest httpServletRequest, Long workflowId, Long actionId) {

	    workflowService.deleteAction(httpServletRequest, workflowId, actionId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<Void>(null, Status.SUCCESS, Translator.toLocale("delete_action_se_msg")));

	}
	
	
	@Override
	public ResponseEntity<Response<WorkflowReq>> getWorkflowById(HttpServletRequest httpServletRequest, Long workflowId) {

		WorkflowReq workflows = workflowService.getWorkflowsById(httpServletRequest, workflowId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<WorkflowReq>(workflows, Status.SUCCESS, Translator.toLocale("get_workflow_se_msg")));

	}
	
	@Override
	public ResponseEntity<Response<Void>> applyWorkFlowToAllClient(HttpServletRequest httpServletRequest, Long merchantId, Long workFlowId) {
		try {
			workflowService.applyWorkFlowToAllClient(httpServletRequest, merchantId, workFlowId);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<Void>(null, Status.SUCCESS, Translator.toLocale("apply_workflow_se_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}
}
