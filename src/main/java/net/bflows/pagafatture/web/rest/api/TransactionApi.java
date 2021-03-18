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
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.TransactionReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.model.view.InvoiceView;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-05T16:42:43.418892+02:00[Europe/Rome]")
@Validated
@Api(value = "transaction", description = "the Transactions API")
public interface TransactionApi {

	   default Optional<NativeWebRequest> getRequest() {
	        return Optional.empty();
	    }
	   
	   
	   /**
	     * POST transactions/{invoiceId} : Create the transaction
	     * This can only be done by the logged in user.
	     *
	     * @param merchant Created transaction object (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     */
	   @Operation(summary ="Api to create transaction",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "transactions/{invoiceId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.POST)
	    default ResponseEntity<Response<TransactionReq>> createTransaction(HttpServletRequest request, @ApiParam(value = "The invoice  id",required=true) @PathVariable("invoiceId") Long id , @ApiParam(value = "Created transaction object" ,required=true )  @Valid @RequestBody TransactionReq transactionReq) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    
	    

	    /**
	     * PUT transactions/{invoiceId} : updates transaction
	     *
	     * @param invoiceId The invoice id (required)
	     * @param body Updated transaction object (required)
	     * @return successful operation (status code 200)
	     *         or Invalid invoice supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Invoice not found (status code 404)
	     */
	   @Operation(summary ="Api to update transaction",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "transactions/{invoiceId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.PUT)
	    default ResponseEntity<Response<TransactionReq>> updateTransaction(HttpServletRequest request, @ApiParam(value = "The invoice id",required=true) @PathVariable("invoiceId") Long invoiceId,@ApiParam(value = "Updated transaction object" ,required=true )  @Valid @RequestBody TransactionReq body) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    
	    
	    
	    /**
	     * GET transactions/{invoiceId} : Get trnasaction by merchant id
	     *
	     * @param id The merchant id that needs to be fetched. 
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or transaction not found (status code 404)
	     */
	   @Operation(summary ="Api to get transaction by invoiceId",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "transactions/{invoiceId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    default ResponseEntity<Response<TransactionsEntity>> getTransactionByInvocieId(HttpServletRequest request, @ApiParam(value = "The invoice id",required=true) @PathVariable("invoiceId") Long invoiceId) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    
	    
	    /**
	     * GET transactions/{invoiceId} : Get trnasaction by invoice id
	     *
	     * @param id The invocie id that needs to be fetched. 
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or transaction not found (status code 404)
	     */
	   @Operation(summary ="Api to get transactions by merchantId",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "merchant/transactions/{merchantId}",produces = { "application/json" },method = RequestMethod.GET)
	    @JsonView(value= {InvoiceView.InvoicesBean.class})
	    default ResponseEntity<Response<List<InvoiceResponseBean>>> getTransactionByMerchantId(HttpServletRequest request, @ApiParam(value = "The merchant id",required=true) @PathVariable("merchantId") Long merchantId) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    
	    /**
	     * DELETE transactions/{invoiceId} : Delete transaction
	     * This can only be done by the logged in user.
	     *
	     * @param id The id that needs to be deleted (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Client not found (status code 404)
	     */
	   @Operation(summary ="Api to delete transaction ",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "transactions/{invoiceId}",
	        method = RequestMethod.DELETE)
	    default ResponseEntity<Response<Void>> deleteTransaction(HttpServletRequest request, @ApiParam(value = "The transaction id",required=true) @PathVariable("invoiceId") Long invoiceId) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
}
