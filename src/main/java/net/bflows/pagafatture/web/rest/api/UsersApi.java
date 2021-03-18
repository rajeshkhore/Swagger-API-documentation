/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (4.3.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package net.bflows.pagafatture.web.rest.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.UserUpdateReq;
import net.bflows.pagafatture.model.util.JWTToken;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.web.rest.errors.ErrorConstants;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:42:43.418892+02:00[Europe/Rome]")

@Validated
@Api(value = "users", description = "the users API")
public interface UsersApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PUT /users/{username}/passwords-auth : changing password
     *
     * @param username The user name for login (required)
     * @param password The password to change in clear text (required)
     * @param newPassword The new password (required)
     * @param confPassword The new password confirmation new and conf must be equal (required)
     * @return successful operation (status code 200)
     *         or Invalid username/password supplied (status code 400)
     */
    @Operation(summary ="Api to change user password",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}/passwords-auth",
        produces = { "application/json" }, 
        method = RequestMethod.PUT)
    default ResponseEntity<Response<Boolean>> changeUserPassword(@ApiParam(value = "The user name for login",required=true) @PathVariable("username") String username,@NotNull @ApiParam(value = "The password to change in clear text", required = true) @Valid @RequestParam(value = "password", required = true) String password,@ApiParam(value = "The new password") @Valid @RequestParam(value = "newPassword", required = true) String newPassword,@ApiParam(value = "The new password confirmation new and conf must be equal") @Valid @RequestParam(value = "confPassword", required = true) String confPassword) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }
    
    /**
     * PUT /users/{username}/passwords : Reset password
     *
     * @param username The user name for login (required)
      * @param token The temporary token (required)
     * @param newPassword The new password (required)
     * @param confPassword The new password confirmation new and conf must be equal (required)
     * @return successful operation (status code 200)
     *         or Invalid username/password supplied (status code 400)
     */
    @Operation(summary ="Api to reset user password",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}/passwords",
        produces = { "application/json" }, 
        method = RequestMethod.PUT)
    default ResponseEntity<Response<Boolean>> resetUserPassword(@ApiParam(value = "The user name for login",required=true) @PathVariable("username") String username,@ApiParam(value = "The temporary token") @Valid @RequestParam(value = "token", required = true) String token,@ApiParam(value = "The new password") @Valid @RequestParam(value = "newPassword", required = true) String newPassword,@ApiParam(value = "The new password confirmation new and conf must be equal") @Valid @RequestParam(value = "confPassword", required = true) String confPassword) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /users/{username}/passwords : change password request
     *
     * @param username The user name (required)
     * @return successful operation (status code 200)
     *         or Invalid username/password supplied (status code 400)
     */
    @Operation(summary ="Api to change user password request",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}/passwords",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Response<Boolean>> changeUserPasswordRequest(@ApiParam(value = "The user name",required=true) @PathVariable("username") String username) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /users : Create user
     * user registration
     *
     * @param body Created user object (required)
     * @return OK (status code 200)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Malformed (status code 405)
     */
    @Operation(summary ="Api to create user",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    default ResponseEntity<Response<UserEntity>> createUser(@ApiParam(value = "Created user object" ,required=true )  @Valid @RequestBody UserReq body) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"password\" : \"password\", \"role\" : \"role\", \"userStatus\" : \"ACTIVE\", \"phone\" : \"phone\", \"merchantId\" : \"merchantId\", \"companyName\" : \"companyName\", \"id\" : 0, \"confPassword\" : \"confPassword\", \"email\" : \"email\", \"username\" : \"username\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /users/{username} : Delete user
     * This can only be done by the logged in user. This can only be done by the logged in user
     *
     * @param username The name that needs to be deleted (required)
     * @return Invalid username supplied (status code 400)
     *         or Not Found (status code 404)
     */
    @Operation(summary ="Api to delete user",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}",
        method = RequestMethod.DELETE)
    default ResponseEntity<Void> deleteUser(@ApiParam(value = "The name that needs to be deleted",required=true) @PathVariable("username") String username) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /users/{username} : Get user by user name
     *
     * @param username   (required)
     * @return successful operation (status code 200)
     *         or Invalid username supplied (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @Operation(summary ="Api to get user by userName",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<Response<UserEntity>> getUserByName(@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. This can only be done by logged in user ",required=true) @PathVariable("username") String username) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }



    /**
     * POST /users/{username}/sessions : Logs user into the system
     *
     * @param username The user name for login (required)
     * @param password The password for login in clear text (required)
     * @return successful operation (status code 200)
     *         or Invalid username/password supplied (status code 400)
     *         or Not Found (status code 404)
     */
    @Operation(summary ="Api to login user",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}/sessions",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    default ResponseEntity<Response<JWTToken>> loginUser(@ApiParam(value = "The user name for login",required=true) @PathVariable("username") String username,@NotNull @ApiParam(value = "The password for login in clear text", required = true) @Valid @RequestParam(value = "password", required = true) String password,
    		@Valid @RequestParam(value = "rememberMe", required = false) Boolean rememberMe) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /users/{username}/sessions : Logout the  user
     *
     * @param username The user name for login (required)
     * @param password The password for login in clear text (required)
     * @return successful operation (status code 200)
     *         or Invalid username/password supplied (status code 400)
     */
    @Operation(summary ="Api to logout user",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}/sessions",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    default ResponseEntity<Response<UserEntity>> logoutUser(@ApiParam(value = "The user name for logout",required=true) @PathVariable("username") String username,HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /users/{username} : Update user
     * This can only be done by the logged in user. This can only be done by the logged in user
     *
     * @param username name that need to be updated (required)
     * @param body Updated user object (required)
     * @return successful operation (status code 200)
     *         or Invalid username supplied (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @Operation(summary ="Api to update user",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/{username}",
        produces = { "application/json" }, 
        method = RequestMethod.PUT)
    default ResponseEntity<Response<UserEntity>> updateUser(@ApiParam(value = "name that need to be updated",required=true) @PathVariable("username") String username,@ApiParam(value = "Updated user object" ,required=true )  @Valid @RequestBody UserUpdateReq body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }
    
    
    
    @Operation(summary ="Api to verfiy user email",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK"), 
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.NOTFOUND_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
							@ExampleObject(value = ErrorConstants.UNAUTHORIZED_ERROR_EXAMPLE) })),
					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
					@ExampleObject(value = ErrorConstants.BADREQUEST_ERROR_EXAMPLE) })) })
    @RequestMapping(value = "/users/emails/{username}",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    default ResponseEntity<Response<Boolean>> verifyEmail(@ApiParam(value = "The user name for login",required=true) @Valid @PathVariable(value = "username", required = true)  String username,@ApiParam(value = "The verify token") @Valid @RequestParam(value = "token", required = true) String token) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}