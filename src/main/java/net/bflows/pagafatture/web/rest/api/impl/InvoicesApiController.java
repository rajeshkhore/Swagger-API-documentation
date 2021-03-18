package net.bflows.pagafatture.web.rest.api.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.InvoiceUpdateReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.InvoicesService;
import net.bflows.pagafatture.web.rest.api.InvoicesApi;
import net.bflows.pagafatture.web.rest.errors.BadRequestAlertException;
import net.bflows.pagafatture.web.rest.errors.Status;

@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class InvoicesApiController implements InvoicesApi {

	public final Logger log = LoggerFactory.getLogger(InvoicesApiController.class);

	public static final String GET_INVOICES_SC_MSG = "get_invoices_sc_msg";

	@Autowired
	private InvoicesService invoicesService;
	
	@Override
	public ResponseEntity<Response<List<InvoiceReq>>> getAllInvoicesByInvoiceState(HttpServletRequest request,
			String invoiceState) {
		try {
			
			List<InvoiceReq> invoices = invoicesService.getAllInvoicesByInvoiceState(request,invoiceState);
			return ResponseEntity.status(HttpStatus.OK).body(
					new Response<List<InvoiceReq>>(invoices, Status.SUCCESS, Translator.toLocale(GET_INVOICES_SC_MSG)));

		} catch (BadRequestAlertException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public ResponseEntity<Response<Void>> createMerchantInvoices(HttpServletRequest httpServletRequest, Long id,
			List<InvoiceReq> body) {

		invoicesService.createMerchantInvoices(httpServletRequest,id, body);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Response<Void>(null, Status.SUCCESS, Translator.toLocale("create_invoice_se_msg")));

	}

	@Override
	public ResponseEntity<Response<InvoiceReq>> updateInvoice(HttpServletRequest httpServletRequest, Long invoiceId,
			@Valid InvoiceUpdateReq body) {
		
		InvoiceReq invoices = invoicesService.updateMerchantInvoice(httpServletRequest,invoiceId, body);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<InvoiceReq>(invoices, Status.SUCCESS, Translator.toLocale("update_invoice_se_msg")));

	}

	@Override
	public ResponseEntity<Response<InvoiceResponseBean>> getInvoiceByIdOrExternalRef(HttpServletRequest httpServletRequest, Long invoiceId,
			String externalRef) {
		InvoiceResponseBean invoices = null;
		if (invoiceId != null) {
			invoices = invoicesService.getInvoiceById(httpServletRequest, invoiceId);
		}
		if (externalRef != null) {
			invoices = invoicesService.getInvoiceByExternalRef(httpServletRequest, externalRef);
		}
		if (invoices == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<InvoiceResponseBean>(null,
					Status.NOT_FOUND, Translator.toLocale("id_invoice_not_found")));
		}
		return ResponseEntity.status(HttpStatus.OK).body(
				new Response<InvoiceResponseBean>(invoices, Status.SUCCESS, Translator.toLocale(GET_INVOICES_SC_MSG)));

	}

	@Override
	public ResponseEntity<Response<List<InvoiceReq>>> getInvoicesByMerchant(HttpServletRequest httpServletRequest, Long id) {

		
		List<InvoiceReq> invoices = invoicesService.getInvoicesByMerchant(httpServletRequest,id);

		return ResponseEntity.status(HttpStatus.OK).body(
				new Response<List<InvoiceReq>>(invoices, Status.SUCCESS, Translator.toLocale(GET_INVOICES_SC_MSG)));

	}

}
