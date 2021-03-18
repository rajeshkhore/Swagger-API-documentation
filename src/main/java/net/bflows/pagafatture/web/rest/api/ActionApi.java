package net.bflows.pagafatture.web.rest.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.model.ClientWithNextAction;
import net.bflows.pagafatture.model.TimelineReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.model.view.ActionView;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;

public interface ActionApi {

	default Optional<NativeWebRequest> getRequest() {
		return Optional.empty();
	}

	@Operation(summary="Api to perform an action",
			responses = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@PostMapping(value = "actions/{actionId}", produces = { "application/json" })
	default ResponseEntity<Response<TimelineReq>> performAction(HttpServletRequest request,
			@ApiParam(value = "The merchant logged id", required = true) @PathVariable("actionId") Long id,
			@ApiParam(value = "Created timeline object", required = true) @Valid @RequestBody TimelineReq timelineReq) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	
	@Operation(summary="Api to get next actions by merchant id",
			responses = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@GetMapping(value = "actions/{merchantId}", produces = { "application/json" })
	@JsonView(value = { ActionView.ActionWithoutTriggerDays.class})
	default ResponseEntity<Response<List<ClientWithNextAction>>> getNextActionsByMerchantId(HttpServletRequest request,
			@ApiParam(value = "The merchant logged id", required = true) @PathVariable("merchantId") Long id) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	@Operation(summary="Api to get performed actions by client id",
			responses = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@GetMapping(value = "actions/clients/{clientId}", produces = { "application/json" })
	default ResponseEntity<Response<List<TimelineReq>>> getPerformedActionsByClientId(HttpServletRequest request,
			@ApiParam(value = "The client id", required = true) @PathVariable("clientId") Long id) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
	
	
	@Operation(summary="Api to get performed actions by invoice id",
			responses = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	@GetMapping(value = "actions/invoices/{invoiceId}", produces = { "application/json" })
	default ResponseEntity<Response<List<TimelineReq>>> getPerformedActionsByInvoiceId(HttpServletRequest request,
			@ApiParam(value = "The invoice id", required = true) @PathVariable("invoiceId") Long id) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}
	
}
