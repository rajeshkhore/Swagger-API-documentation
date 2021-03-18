package net.bflows.pagafatture.web.rest.errors;

import java.util.ArrayList;
import java.util.List;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.model.util.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlers {
    
    public final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomValidationException.class)
    @ResponseBody
    public Response<Object> handleCustomValidationException(final CustomValidationException ex) {
        log.error(ex.getMessage());
        log.error(ex.getLocalizedMessage());
        return new Response<>(null, ex.getStatus(), ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response<Object> handleThrowable(final Throwable ex) {
        log.error(ex.getMessage());
        log.error(ex.getLocalizedMessage());
        return new Response<>(null, Status.EXCEPTION_ERROR, Translator.toLocale("common_message"));
    }
    
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response<Object> handleValiationException(final MethodArgumentNotValidException ex) {
        
        log.error(ex.getMessage());
        log.error(ex.getLocalizedMessage());
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return new Response<>(errors, Status.EXCEPTION_ERROR, Translator.toLocale("req_validation_er"));
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public Response<Object> handleUnAuthorizedException(final Throwable ex) {
        log.error(ex.getMessage());
        log.error(ex.getLocalizedMessage());
        return new Response<>(null, Status.VALIDATION_ERROR, Translator.toLocale("invalid_login"));
    }
  
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestAlertException.class)
    @ResponseBody
    public Response<Object> badRequestAlertException(final Throwable ex) {
        log.error(ex.getMessage());
        log.error(ex.getLocalizedMessage());
        return new Response<>(null, Status.EXCEPTION_ERROR, ex.getMessage());
    }
    
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public Response<Object> resourceNotFoundException(final Throwable ex) {
        log.error(ex.getMessage());
        log.error(ex.getLocalizedMessage());
        return new Response<>(null, Status.NOT_FOUND, ex.getMessage());
    }
    
    
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public Response<Object> handleInvalidTokenException(final Throwable ex) {
        log.error(ex.getMessage());
        log.error(ex.getLocalizedMessage());
        return new Response<>(ex.getMessage(), Status.UNAUTHORIZED, Translator.toLocale("invalid_signature"));
    }
    
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UnauthorizedAccessException.class)
	@ResponseBody
	public Response<Object> unauthorizedAccessException(final Throwable ex) {
		log.error(ex.getMessage());
		log.error(ex.getLocalizedMessage());
		return new Response<>(null, Status.UNAUTHORIZED, ex.getMessage());
	}
}
