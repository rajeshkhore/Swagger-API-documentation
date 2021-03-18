package net.bflows.pagafatture.web.rest.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.InvoicingConnectionEntity;
import net.bflows.pagafatture.entities.InvoicingEntity;
import net.bflows.pagafatture.model.InvoicingConnectionReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.InvoicingConnectionService;
import net.bflows.pagafatture.service.InvoicingService;
import net.bflows.pagafatture.web.rest.api.InvoicingsApi;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-08-26T12:57:09.877+02:00[Europe/Rome]")

@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class InvoicingsApiController implements InvoicingsApi {

	@Autowired
	private InvoicingService invoicingService;
	
	@Autowired
	private InvoicingConnectionService invoicingConnectionService;
	
	public final Logger log = LoggerFactory.getLogger(InvoicingsApiController.class);


	@Override
	public ResponseEntity<Response<List<InvoicingEntity>>> getAllInvoicing() {
		List<InvoicingEntity> lstExternalP = new ArrayList<>();
    
        	lstExternalP = invoicingService.getAll();
            
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response<List<InvoicingEntity>>(lstExternalP, Status.SUCCESS, Translator.toLocale("get_invoices_sc_msg")));

    
	}

	
	@Override
	public ResponseEntity<Response<InvoicingConnectionEntity>> getInvoicingConnectionByMerchantId(HttpServletRequest httpServletRequest, Long merchantId) {
		  try {
			
			  
			  InvoicingConnectionEntity entity = invoicingConnectionService.getInvoicingConnectionByMerchantId(httpServletRequest, merchantId);
	            
	            	  
	            return ResponseEntity.status(HttpStatus.OK).body(
	                    new Response<InvoicingConnectionEntity>(entity, Status.SUCCESS, Translator.toLocale("get_invoicing_connection_se_msg")));

	        } catch (ResourceNotFoundException ex) {
	        	log.error(ex.getMessage());
	        	throw ex;
	        	
	        }
	}

	
	/*@Override
	public ResponseEntity<Void> deleteInvoicingConnection(Long id, Long connectionId) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}*/
	

	@Override
	public ResponseEntity<Response<InvoicingConnectionEntity>> createInvoicingConnection(HttpServletRequest httpServletRequest, Long merchantId, InvoicingConnectionReq body) {
		  try {
			  InvoicingConnectionEntity entity = invoicingConnectionService.createInvoicingConnection(httpServletRequest, merchantId,body);
	            if(entity == null) {
	            	   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	   	                    new Response<InvoicingConnectionEntity>(null, Status.VALIDATION_ERROR, Translator.toLocale("bad_credentials")));

	            }
	            return ResponseEntity.status(HttpStatus.CREATED).body(
	                    new Response<InvoicingConnectionEntity>(entity, Status.SUCCESS, Translator.toLocale("create_invoicing_connection_se_msg")));

	        } catch (CustomValidationException ex) {
	            log.error(ex.getMessage());
	            throw ex;
	        }
	}

	
	@Override
	public ResponseEntity<Response<List<InvoicingConnectionEntity>>> getInvoicingConnection(HttpServletRequest httpServletRequest, Long typeId) {

		List<InvoicingConnectionEntity> entities = invoicingConnectionService.getInvoicingConnection(httpServletRequest, typeId);
	           
		return ResponseEntity.status(HttpStatus.OK).body(
	                    new Response<List<InvoicingConnectionEntity>>(entities, Status.SUCCESS, Translator.toLocale("get_invoicing_connection_se_msg")));

	}

/*	@Override
	public ResponseEntity<InvoicingConnection> getConnectionById(Long id,Long connectionId) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<InvoicingConnection> updateInvoicingConnection(Long id, Long connectionId, InvoicingConnection body) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}*/
	
	private final NativeWebRequest request;


	@org.springframework.beans.factory.annotation.Autowired
    public InvoicingsApiController(NativeWebRequest request) {
        this.request = request;
    }
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
