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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.entities.InvoicingConnectionEntity;
import net.bflows.pagafatture.entities.InvoicingEntity;
import net.bflows.pagafatture.model.InvoicingConnectionReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:42:43.418892+02:00[Europe/Rome]")

@Validated
@Api(value = "invoicings", description = "the invoicings API")
public interface InvoicingsApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /invoicings/{id}/connections : create connection
     *
     * @param id The invoicing type (required)
     * @param body  (required)
     * @return Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     */
    @Operation(summary ="Api to create an invoicing connection",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/invoicings/{merchantId}/connections",
        method = RequestMethod.POST)
    default ResponseEntity<Response<InvoicingConnectionEntity>> createInvoicingConnection(HttpServletRequest httpServletRequest, @ApiParam(value = "The merchant id",required=true) @PathVariable("merchantId") Long merchantId,@ApiParam(value = "" ,required=true )  @Valid @RequestBody InvoicingConnectionReq body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /invoicings/{id}/connections : create connection
     *
     * @param id The id of the Merchant  that needs to be fetched. (required)
     * @return Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     */
    @Operation(summary ="Api to get an invoicing connection by merchantId",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/invoicings/{merchantId}/connections",
        method = RequestMethod.GET)
    default ResponseEntity<Response<InvoicingConnectionEntity>> getInvoicingConnectionByMerchantId(HttpServletRequest httpServletRequest, @ApiParam(value = "The merchant id",required=true) @PathVariable("merchantId") Long merchantId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }



    /**
     * GET /invoicings : Get all Invoicing platfomrs supported
     *
     * @return successful operation (status code 200)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     */
    @Operation(summary ="Api to get all invoicing",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/invoicings",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<Response<List<InvoicingEntity>>> getAllInvoicing() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    

    /**
     * GET /invoicings/connections : get all  invoicing connection
     *
     * @return Unauthorized (status code 401)
     * 
     * 
     *         or Forbidden (status code 403)
     */
    @Operation(summary ="Api to get an invoicing connection by typeId",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/invoicings/connections",
        method = RequestMethod.GET)
    
    default ResponseEntity<Response<List<InvoicingConnectionEntity>>> getInvoicingConnection(HttpServletRequest httpServletRequest, @RequestParam(value = "type", required = false) Long typeId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
