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
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.TransactionReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.TransactionService;
import net.bflows.pagafatture.web.rest.api.TransactionApi;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-15T12:57:09.877+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class TransactionApiController implements TransactionApi {

	public final Logger log = LoggerFactory.getLogger(TransactionApiController.class);

	@Autowired
	TransactionService transactionService;
	
	@Override
	public ResponseEntity<Response<TransactionReq>> createTransaction(HttpServletRequest httpServletRequest, Long id, TransactionReq body) {
		try {
			TransactionReq req = transactionService.createTransaction(httpServletRequest, id, body);

			return ResponseEntity.status(HttpStatus.CREATED).body(new Response<TransactionReq>(req, Status.SUCCESS,
					Translator.toLocale("create_transaction_se_msg")));

		} catch (CustomValidationException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public ResponseEntity<Response<TransactionReq>> updateTransaction(HttpServletRequest httpServletRequest, Long invoiceId, TransactionReq body) {
		try {
			TransactionReq req = transactionService.updateTransaction(httpServletRequest,invoiceId, body);

			return ResponseEntity.status(HttpStatus.OK).body(new Response<TransactionReq>(req, Status.SUCCESS,
					Translator.toLocale("update_transaction_se_msg")));

		} catch (CustomValidationException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public ResponseEntity<Response<TransactionsEntity>> getTransactionByInvocieId(HttpServletRequest httpServletRequest, Long invoiceId) {

		try {
			TransactionsEntity transactionsEntity = transactionService.getTransactionByInvoiceId(httpServletRequest, invoiceId);

			return ResponseEntity.status(HttpStatus.OK).body(new Response<TransactionsEntity>(transactionsEntity,
					Status.SUCCESS, Translator.toLocale("get_transaction_sc_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

	}

	@Override
	public ResponseEntity<Response<List<InvoiceResponseBean>>> getTransactionByMerchantId(HttpServletRequest httpServletRequest, Long merchantId) {

		try {
			List<InvoiceResponseBean> transactionsEntities = transactionService
					.getAllTransactionByMerchantId(httpServletRequest, merchantId);

			return ResponseEntity.status(HttpStatus.OK).body(new Response<List<InvoiceResponseBean>>(
					transactionsEntities, Status.SUCCESS, Translator.toLocale("get_transactions_sc_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

	}
	
	@Override
	public ResponseEntity<Response<Void>> deleteTransaction(HttpServletRequest httpServletRequest, Long tranactionId) {
		try {
			 transactionService.deleteTransaction(httpServletRequest, tranactionId);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<>(null, Status.SUCCESS, Translator.toLocale("delete_tranasction_se_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}
}
