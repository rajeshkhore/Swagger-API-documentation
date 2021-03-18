package net.bflows.pagafatture.web.rest.api.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.UserUpdateReq;
import net.bflows.pagafatture.model.util.JWTToken;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.UserService;
import net.bflows.pagafatture.web.rest.api.UsersApi;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-08-26T12:57:09.877+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class UsersApiController implements UsersApi {

    public final Logger log = LoggerFactory.getLogger(UsersApi.class);


    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<Response<Boolean>> changeUserPassword(String username, String password,
			String newPassword, String confPassword) {
		try {
			userService.changeUserPassword(username, password, newPassword, confPassword);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<Boolean>(null, Status.SUCCESS, Translator.toLocale("pass_ch_msg")));

		} catch (CustomValidationException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}
    
    @Override
    public ResponseEntity<Response<Boolean>> resetUserPassword(String username, String token,
            String newPassword, String confPassword) {
        try {
           userService.resetUserPassword(username, token, newPassword, confPassword);
           
           return ResponseEntity.status(HttpStatus.OK).body(
                        new Response<Boolean>(null, Status.SUCCESS, Translator.toLocale("pass_ch_msg")));   

        } catch (CustomValidationException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public ResponseEntity<Response<Boolean>> changeUserPasswordRequest(String username) {
        try {
            userService.changeUserPasswordRequest(username);
                
            return ResponseEntity.status(HttpStatus.OK).body(
                        new Response<Boolean>(null, Status.SUCCESS, Translator.toLocale("ch_usr_pass_req_msg")));   
            

        } catch (ResourceNotFoundException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public ResponseEntity<Response<UserEntity>> createUser(UserReq body) {

        try {
            UserEntity user = userService.createUser(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new Response<UserEntity>(user, Status.SUCCESS, Translator.toLocale("create_user_se_msg")));

        } catch (CustomValidationException ex) {
            log.error(ex.getMessage());
            throw ex;
        }

    }


    @Override
    public ResponseEntity<Response<UserEntity>> getUserByName(String username) {

        try {
            UserEntity userEntity = userService.getUserByName(username);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response<UserEntity>(userEntity, Status.SUCCESS, Translator.toLocale("get_user_sc_msg")));

        } catch (ResourceNotFoundException ex) {
            log.error(ex.getMessage());
            throw ex;
        }

    }
    
    @Override
    public ResponseEntity<Response<JWTToken>> loginUser(String username, String password, Boolean rememberMe) {

            JWTToken jwtToken = userService.loginUser(username, password, rememberMe);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response<JWTToken>(jwtToken, Status.SUCCESS, Translator.toLocale("login_se_msg")));



    }

    @Override
    public ResponseEntity<Response<UserEntity>> logoutUser(String username,HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response,
                        auth);
            }
            SecurityContextHolder.getContext().setAuthentication(null);
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response<UserEntity>(null, Status.SUCCESS, Translator.toLocale("logout_se_msg")));


        } catch (CustomValidationException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public ResponseEntity<Response<UserEntity>> updateUser(String username,UserUpdateReq body) {
        try {
            UserEntity userEntity = userService.update(username,body);
            
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response<UserEntity>(userEntity, Status.SUCCESS, Translator.toLocale("update_user_se_msg")));

        } catch (ResourceNotFoundException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }
    
    
    @Override
    public ResponseEntity<Response<Boolean>> verifyEmail(String username, String token) {
        try {
           String responseString=userService.verifyEmail(username, token);
           
           return ResponseEntity.status(HttpStatus.OK).body(
                        new Response<Boolean>(null, Status.SUCCESS,responseString ));   

        } catch (CustomValidationException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
    }
}