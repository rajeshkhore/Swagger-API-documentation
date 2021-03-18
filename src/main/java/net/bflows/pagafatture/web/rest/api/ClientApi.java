package net.bflows.pagafatture.web.rest.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.ClientResponseBean;
import net.bflows.pagafatture.model.InvoiceSearchReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.model.view.ClientView;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-05T16:42:43.418892+02:00[Europe/Rome]")
@Validated
@Api(value = "clients", description = "the Clients API")
public interface ClientApi {

	
	   default Optional<NativeWebRequest> getRequest() {
	        return Optional.empty();
	    }
	   
	    /**
	     * POST /clients : Create the client
	     * This can only be done by the logged in user.
	     *
	     * @param merchant Created client object (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     */
	   
	   
	@Operation(summary ="Api to create client",
			responses = {
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/merchants//{merchantId}/clients",
	        produces = { "application/json" }, 
	        method = RequestMethod.POST)
	    @JsonView(value= {ClientView.ClientBean.class})
	    default ResponseEntity<Response<ClientReq>> createClient(HttpServletRequest request, @ApiParam(value = "The merchant logged id",required=true) @PathVariable("merchantId") Long id , @ApiParam(value = "Created client object" ,required=true )  @Valid @RequestBody ClientReq clientReq) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    
	

	    /**
	     * GET /clients/{id}/ : Get client by id
	     *
	     * @param id The Id that needs to be fetched. Use user1 for testing.  (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Merchant not found (status code 404)
	     */
	@Operation(summary ="Api to get client by id",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/merchants/{merchantId}/clients/{id}",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    @JsonView(value= {ClientView.ClientWithInvoiceDetailListBean.class})
	    default ResponseEntity<Response<ClientResponseBean>> getClientById(HttpServletRequest request, @ApiParam(value = "The merchant logged id",required=true) @PathVariable("merchantId") Long merchantId ,@ApiParam(value = "The id that needs to be fetched. ",required=true) @PathVariable("id") Long id) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    

	    /**
	     * PUT /merchants/{id}/clients/{clientId} : updates client
	     *
	     * @param id The merchant logged id (required)
	     * @param body Updated client object (required)
	     * @return successful operation (status code 200)
	     *         or Invalid client supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Invoice not found (status code 404)
	     */
	@Operation(summary ="Api to update client",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/merchants/{id}/clients/{clientId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.PUT)
	    @JsonView(value= {ClientView.ClientBean.class})
	    default ResponseEntity<Response<ClientReq>> updateClient(HttpServletRequest request, @ApiParam(value = "The merchant logged id",required=true) @PathVariable("id") Long id,@ApiParam(value = "The client id",required=true) @PathVariable("clientId") Long clientId,@ApiParam(value = "Updated client object" ,required=true )  @Valid @RequestBody ClientReq body) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    /**
	     * GET /merchants/{id}/ : Get clients by merchant id
	     *
	     * @param id The Id that needs to be fetched. Use user1 for testing.  (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Merchant not found (status code 404)
	     */
	@Operation(summary ="Api to get client by merchantId",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/merchants/{id}/clients",
	        produces = {"application/json" }, 
	        method = RequestMethod.GET)
	    @JsonView(value= {ClientView.ClientListWithInvoiceDetailBean.class})
	    default ResponseEntity<Response<List<ClientReq>>> getClientsByMerchantId(HttpServletRequest request, @ApiParam(value = "The merchant id that needs to be fetched. ",required=true) @PathVariable("id") Long id) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    /**
	     * DELETE /clients/{id} : Delete client
	     * This can only be done by the logged in user.
	     *
	     * @param id The id that needs to be deleted (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Client not found (status code 404)
	     */
	@Operation(summary ="Api to delete client",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/merchants/{id}/clients/{clientId}",
	        method = RequestMethod.DELETE)
	    default ResponseEntity<Response<Void>> deleteClient(HttpServletRequest request, @ApiParam(value = "The merchant logged id",required=true) @PathVariable("id") Long id,@ApiParam(value = "The client id",required=true) @PathVariable("clientId") Long clientId) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    /**
	     * GET clients/invoices : Get invoices by client id and merchant id
	     *
	     * @param token that needs to be fetched.(required)
	     * @return successful operation (status code 200)
	     *         not found invoices (status code 404)
	     *         bad request param is missing (status code 400)
	     */
	@Operation(summary ="Api to get invoices by merchantId and clientId",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "clients/invoices",
	        method = RequestMethod.POST)
	    @JsonView(value= {ClientView.InvoicesView.class})
	    default ResponseEntity getInvoicesByMerchantIdAndClientId(HttpServletRequest httpServletRequest, @RequestBody InvoiceSearchReq request) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	    }
	
}