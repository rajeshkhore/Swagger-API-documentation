package net.bflows.pagafatture.web.rest.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.model.widget.WidgetsResponseBean;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-22T16:42:43.418892+02:00[Europe/Rome]")

@Validated
@Api(value = "dashboard", description = "the dashboard API")
public interface DashboardApi {

	 default Optional<NativeWebRequest> getRequest() {
	        return Optional.empty();
	    }
	 
	 
	 /**
	     * GET /widgets/{merchantId} : Get widgets by merchantId
	     *
	     * @param merchantId   (required)
	     * @return successful operation (status code 200)
	     *         or Invalid merchantId supplied (status code 400)
	     *         or Unauthorized (status code 401)
	     *         or Forbidden (status code 403)
	     *         or Not Found (status code 404)
	     */
	  @Operation(summary ="Api to get dashboard widgets",
				responses = { 
						@ApiResponse(responseCode = "200", description = "OK"), 
						@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
								@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
						@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
						@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
	    @RequestMapping(value = "/widgets/{merchantId}",
	        produces = { "application/json" }, 
	        method = RequestMethod.GET)
	    default ResponseEntity<Response<WidgetsResponseBean>> getWidgetsByMerchantId(HttpServletRequest httpServletRequest,@ApiParam(value = "The id of the Merchant whose invoice detail needs to be fetched.",required=true) @PathVariable("merchantId") Long merchantId) {
	        getRequest().ifPresent(request -> {
	            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
	                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
	                	  String exampleString = "{ \"apisecret\" : \"apisecret\", \"apikey\" : \"apikey\", \"merchantId\" : \"merchantId\", \"typeId\" : \"typeId\", \"id\" : 0 }";
	                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
	                    break;
	                }
	            }
	        });
	        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	    }
	    
	   
}
