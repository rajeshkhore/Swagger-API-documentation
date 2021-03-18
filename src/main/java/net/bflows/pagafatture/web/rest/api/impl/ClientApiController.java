package net.bflows.pagafatture.web.rest.api.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.ClientResponseBean;
import net.bflows.pagafatture.model.InvoiceSearchReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.ClientService;
import net.bflows.pagafatture.web.rest.api.ClientApi;
import net.bflows.pagafatture.web.rest.errors.BadRequestAlertException;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-06T12:57:09.877+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class ClientApiController implements ClientApi {

	public final Logger log = LoggerFactory.getLogger(ClientApiController.class);

	@Autowired
	ClientService clientService;
	
	

	@Override
	public ResponseEntity<Response<ClientReq>> createClient(HttpServletRequest request, Long id, ClientReq body) {
		try {
			ClientReq req = clientService.createClient(request,id, body);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new Response<ClientReq>(req, Status.SUCCESS, Translator.toLocale("create_client_se_msg")));

		} catch (CustomValidationException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public ResponseEntity<Response<ClientResponseBean>> getClientById(HttpServletRequest request, Long merchantId, Long id) {

		try {
			ClientResponseBean response = clientService.getClientById(request,merchantId, id);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<ClientResponseBean>(response, Status.SUCCESS, Translator.toLocale("get_client_sc_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

	}

	@Override
	public ResponseEntity<Response<List<ClientReq>>> getClientsByMerchantId(HttpServletRequest request, Long merchantId) {

		try {
			
			List<ClientReq> clients = clientService.getClientsByMerchantId(request, merchantId);

			return ResponseEntity.status(HttpStatus.OK).body(
					new Response<List<ClientReq>>(clients, Status.SUCCESS, Translator.toLocale("get_client_sc_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

	}

	@Override
	public ResponseEntity<Response<ClientReq>> updateClient(HttpServletRequest request,Long id, Long clientId, ClientReq body) {
		try {
			
			ClientReq req = clientService.updateClient(request, id, clientId, body);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<ClientReq>(req, Status.SUCCESS, Translator.toLocale("update_client_se_msg")));

		} catch (CustomValidationException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public ResponseEntity<Response<Void>> deleteClient(HttpServletRequest request, Long id, Long clientId) {
		try {
			clientService.deleteClient(request, id,clientId);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<>(null, Status.SUCCESS, Translator.toLocale("delete_client_se_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}
	
	@Override
	public ResponseEntity getInvoicesByMerchantIdAndClientId(HttpServletRequest httpServletRequest,InvoiceSearchReq request) {
		
        try {
        	
        	ClientResponseBean invoicesView = clientService.getInvoicesByMerchantIdAndClientId(httpServletRequest,request);
        	return ResponseEntity.status(HttpStatus.OK).body(
        			new Response<ClientResponseBean>(invoicesView, Status.SUCCESS, Translator.toLocale("get_invoices_sc_msg")));
        	
        } catch (BadRequestAlertException ex) {
            log.error(ex.getMessage());
            throw ex;
        }
	}
}
