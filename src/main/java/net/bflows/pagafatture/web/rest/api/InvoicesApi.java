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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.InvoiceUpdateReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.model.view.InvoiceView;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:42:43.418892+02:00[Europe/Rome]")

@Validated
@Api(value = "invoices", description = "the merchants API")
public interface InvoicesApi {

	
	 default Optional<NativeWebRequest> getRequest() {
	        return Optional.empty();
	    }
	 
	 

	    /**
	     * GET /invoices : Get all due and overdue invoices 
	     * List of due or overdue invoices .
	     *
	     * 
	     * @return successful operation (status code 200)
	     *         or Invalid  supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or not found (status code 404)
	     */
	  @Operation(summary ="Api to get invoices by invoiceState",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/invoices",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    @JsonView(value= {InvoiceView.InvoiceWithClientWithMerchant.class})
	    default ResponseEntity<Response<List<InvoiceReq>>> getAllInvoicesByInvoiceState(HttpServletRequest request, @ApiParam(value = "Invoice state")@RequestParam(value = "invoiceState", required = false) String invoiceState) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    

	    /**
	     * POST /merchants/{id}/invoices : create merchant invoices
	     * This can only be done by the logged in merchant.
	     *
	     * @param id The merchant logged id (required)
	     * @param body  list invoices object (required)
	     * @return Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Merchant not found (status code 404)
	     */
	  @Operation(summary ="Api to create an invoices",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/merchants/{id}/invoices",
	        method = RequestMethod.POST)
	    @JsonView(value= {InvoiceView.InvoicesReqBean.class})
	    default ResponseEntity<Response<Void>> createMerchantInvoices(HttpServletRequest httpServletRequest, @ApiParam(value = "The merchant logged id",required=true) @PathVariable("id") Long id,@ApiParam(value = " list invoices object" ,required=true )  @Valid @RequestBody List<InvoiceReq> body) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    
	    /**
	     * PUT /invoices/{invoiceId} : updates invoice
	     * @param body Updated invoice object (required)
	     * @return successful operation (status code 200)
	     *         or Invalid invoice supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Invoice not found (status code 404)
	     */
	  @Operation(summary ="Api to update an invoice",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/invoices/{invoiceId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.PUT)
	  @JsonView(value= {InvoiceView.InvoicesReqBean.class})
		default ResponseEntity<Response<InvoiceReq>> updateInvoice(HttpServletRequest httpServletRequest, @ApiParam(value = "The invoice id",required=true) @PathVariable("invoiceId") Long invoiceId,@ApiParam(value = "Updated invoice object" ,required=true )  @Valid @RequestBody InvoiceUpdateReq body) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    

	    /**
	     * GET /invoice : Get invoices by  id OR externalRef
	     * Get single invoice for the logged merchant. This can only be done by the logged in merchant.
	     * Either invoiceId OR externalRef one of them is mandatory.
	     * @param invoiceId The invoice id  (optional)
	     * @param externalRef The external ref  (optional) 
	     * @param id The merchant logged id  (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Merchant not found (status code 404)
	     */
	  @Operation(summary ="Api to get an invoice by id or externalRef",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/invoice",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    @JsonView(value= {InvoiceView.InvoicesBean.class})
	    default ResponseEntity<Response<InvoiceResponseBean>> getInvoiceByIdOrExternalRef(HttpServletRequest httpServletRequest, @RequestParam(value = "invoiceId", required = false) Long invoiceId,@RequestParam(value = "externalRef", required = false) String externalRef) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }


	    
	    
	    
	    
	    
	    
	    
	    /**
	     * GET /merchants/{id}/invoices : Get invoices by merchant id
	     * List invoices for the logged merchant. This can only be done by the logged in merchant.
	     *
	     * @param id The merchant logged id  (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Merchant not found (status code 404)
	     */
	  @Operation(summary ="Api to get an invoices by merchantId",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/merchants/{id}/invoices",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    @JsonView(value= {InvoiceView.InvoicesReqBean.class})
	    default ResponseEntity<Response<List<InvoiceReq>>> getInvoicesByMerchant(HttpServletRequest httpServletRequest, @ApiParam(value = "The merchant logged id ",required=true) @PathVariable("id") Long id) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED );

	    }
	    
	    

	    /**
	     * DELETE /invoices/{invoiceId} : delete invoice
	     *
	     * @param invoiceId The invoiceId to be deleted (required)
	     * 
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Invoice not found (status code 404)
	     */
	  @Operation(summary ="Api to delete an invoice",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/invoices/{invoiceId}",
	        method = RequestMethod.DELETE)
	    default ResponseEntity<Void> deleteInvoice(@ApiParam(value = "The invoiceId to be deleted",required=true) @PathVariable("invoiceId") String invoiceId) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED  );

	    }

}
