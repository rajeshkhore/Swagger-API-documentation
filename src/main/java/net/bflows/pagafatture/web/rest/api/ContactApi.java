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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.model.ContactReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-05T16:42:43.418892+02:00[Europe/Rome]")
@Validated
@Api(value = "contacts", description = "the Contacts API")
public interface ContactApi {

	
	   default Optional<NativeWebRequest> getRequest() {
	        return Optional.empty();
	    }
	   
	    /**
	     * POST /clients : Create the contact
	     * This can only be done by the logged in user.
	     *
	     * @param clientId for Created client object (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     */
	   @Operation(summary ="Api to create contact",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/clients/{clientId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.POST)
	    default ResponseEntity<Response<ContactReq>> createContact(HttpServletRequest request, @ApiParam(value = "The client  id",required=true) @PathVariable("clientId") Long id , @ApiParam(value = "Created contact object" ,required=true )  @Valid @RequestBody ContactReq contactReq) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    

	    /**
	     * GET /clients/{id}/ : Get contact by id
	     *
	     * @param id The Id that needs to be fetched. Use user1 for testing.  (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Client not found (status code 404)
	     */
	   @Operation(summary ="Api to get contact by id",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/clients/{clientId}/contacts/{id}",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    default ResponseEntity<Response<ContactReq>> getContactById(HttpServletRequest request, @ApiParam(value = "The client  id",required=true) @PathVariable("clientId") Long clientId ,@ApiParam(value = "The id that needs to be fetched. ",required=true) @PathVariable("id") Long id) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    

	    /**
	     * PUT /clients/{id}/contacts/{contactId} : updates contact
	     *
	     * @param id The client  id (required)
	     * @param body Updated client object (required)
	     * @return successful operation (status code 200)
	     *         or Invalid client supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Invoice not found (status code 404)
	     */
	   @Operation(summary ="Api to update contact",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/clients/{id}/contacts/{contactId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.PUT)
	    default ResponseEntity<Response<ContactReq>> updateContact(HttpServletRequest request, @ApiParam(value = "The client  id",required=true) @PathVariable("id") Long id,@ApiParam(value = "The client id",required=true) @PathVariable("contactId") Long contactId,@ApiParam(value = "Updated contact object" ,required=true )  @Valid @RequestBody ContactReq body) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    /**
	     * GET /Clients/{id}/contacts : Get contacts by client id
	     *
	     * @param id The Id that needs to be fetched. Use user1 for testing.  (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Client not found (status code 404)
	     */
	   @Operation(summary ="Api to get contacts by clientId",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/clients/{id}/contacts",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    default ResponseEntity<Response<List<ContactReq>>> getContactsByClientId(HttpServletRequest request, @ApiParam(value = "The client id that needs to be fetched. ",required=true) @PathVariable("id") Long id) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	    /**
	     * DELETE /clients/{id} : Delete contact
	     * This can only be done by the logged in user.
	     *
	     * @param id The id that needs to be deleted (required)
	     * @return successful operation (status code 200)
	     *         or Invalid id supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Client not found (status code 404)
	     */
	   @Operation(summary ="Api to delete contact",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/clients/{id}/contacts/{contactId}",
	        method = RequestMethod.DELETE)
	    default ResponseEntity<Response<Void>> deleteContact(HttpServletRequest request, @ApiParam(value = "The client  id",required=true) @PathVariable("id") Long id,@ApiParam(value = "The contact id",required=true) @PathVariable("contactId") Long contactId) {
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }

}


