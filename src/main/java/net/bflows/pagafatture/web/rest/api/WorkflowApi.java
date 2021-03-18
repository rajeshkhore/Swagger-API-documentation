package net.bflows.pagafatture.web.rest.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.model.ActionReq;
import net.bflows.pagafatture.model.ActionTypeReq;
import net.bflows.pagafatture.model.ClientWithNextAction;
import net.bflows.pagafatture.model.UpdateActionReq;
import net.bflows.pagafatture.model.WorkflowReq;
import net.bflows.pagafatture.model.WorkflowUpdateReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.model.view.WorkflowView;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;

@Validated
@Api(value = "workflows")
public interface WorkflowApi {

	default Optional<NativeWebRequest> getRequest() {
		return Optional.empty();
	}

	@Operation(summary ="Api to create workflow",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@PostMapping(value = "workflows/{merchantId}",produces = { "application/json" })
	@JsonView(value= {WorkflowView.WorkflowWithoutAction.class})
	default ResponseEntity<Response<WorkflowReq>> createWorkflow(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The merchant logged id", required = true) @PathVariable("merchantId") Long id,
			@ApiParam(value = "Created workflow object", required = true) @Valid @RequestBody WorkflowReq workflowReq) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	@Operation(summary ="Api to get workflows by merchantId",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@GetMapping(value = "workflows/{merchantId}", produces = { "application/json" })
	@JsonView(value= {WorkflowView.WorkflowWithoutAction.class})
	default ResponseEntity<Response<List<WorkflowReq>>> getWorkflowsByMerchantId(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The merchant logged id", required = true) @PathVariable("merchantId") Long id) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	@Operation(summary ="Api to update workflow",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@PutMapping(value = "workflows/{workflowId}",produces = { "application/json" })
	@JsonView(value= {WorkflowView.WorkflowWithoutAction.class})
	default ResponseEntity<Response<WorkflowReq>> updateWorkflow(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The workflow logged id", required = true) @PathVariable("workflowId") Long workflowId,
			@ApiParam(value = "Update workflow object", required = true) @Valid @RequestBody WorkflowUpdateReq workflowReq) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	@Operation(summary ="Api to delete workflow",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@DeleteMapping(value = "workflows/{workflowId}",produces = { "application/json" })
	default ResponseEntity<Response<Void>> deleteWorkflow(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The workflow logged id", required = true) @PathVariable("workflowId") Long workflowId) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	@Operation(summary ="Api to create an action",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@PostMapping(value = "workflows/{workflowId}/actions",produces = { "application/json" })
	default ResponseEntity<Response<ActionReq>> createAction(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The workflow logged id", required = true) @PathVariable("workflowId") Long id,
			@ApiParam(value = "Created action object", required = true) @Valid @RequestBody ActionReq actionReq) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	@Operation(summary ="Api to get actionTypes",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@GetMapping(value = "/actionTypes", produces = { "application/json" })
	default ResponseEntity<Response<List<ActionTypeReq>>> getActionTypes() {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	@Operation(summary ="Api to update action",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@PutMapping(value = "workflows/{workflowId}/actions/{actionId}",produces = { "application/json" })
	default ResponseEntity<Response<ActionReq>> updateAction(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The workflow logged id", required = true) @PathVariable("workflowId") Long id,
			@ApiParam(value = "The action logged id", required = true) @PathVariable("actionId") Long actionId,
			@ApiParam(value = "Update action object", required = true) @Valid @RequestBody UpdateActionReq actionReq) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}	
	
	@Operation(summary ="Api to delete action",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@DeleteMapping(value = "workflows/{workflowId}/actions/{actionId}",produces = { "application/json" })
	default ResponseEntity<Response<Void>> deleteAction(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The workflow logged id", required = true) @PathVariable("workflowId") Long id,
			@ApiParam(value = "The action logged id", required = true) @PathVariable("actionId") Long actionId) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	
	@Operation(summary ="Api to get workflow by id",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@GetMapping(value = "workflow/{workflowId}", produces = { "application/json" })
	default ResponseEntity<Response<WorkflowReq>> getWorkflowById(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The workflow  id", required = true) @PathVariable("workflowId") Long workflowId) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	
	@Operation(summary ="Api to apply workflow to all client",responses = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@PostMapping(value = "workflows/{workflowId}/merchants/{merchantId}", produces = { "application/json" })
	default ResponseEntity<Response<Void>> applyWorkFlowToAllClient(HttpServletRequest httpServletRequest,
			@ApiParam(value = "The merchant logged id", required = true) @PathVariable("merchantId") Long merchantId,
			@ApiParam(value = "The workflow id", required = true) @PathVariable("workflowId") Long workflowId) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
}
