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
import net.bflows.pagafatture.model.ClientWithNextAction;
import net.bflows.pagafatture.model.TimelineReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.ActionService;
import net.bflows.pagafatture.web.rest.api.ActionApi;
import net.bflows.pagafatture.web.rest.errors.Status;

@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class ActionApiController implements ActionApi {


	public final Logger log = LoggerFactory.getLogger(ActionApiController.class);

	@Autowired
	ActionService actionService;

	@Override
	public ResponseEntity<Response<TimelineReq>> performAction(HttpServletRequest request, Long actionId, TimelineReq body) {

		
		TimelineReq req = actionService.performAction(request,actionId, body);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Response<TimelineReq>(req, Status.SUCCESS, Translator.toLocale("create_timeline_se_msg")));

	}
	
	
	@Override
	public ResponseEntity<Response<List<ClientWithNextAction>>> getNextActionsByMerchantId(HttpServletRequest request, Long merchantId) {

		List<ClientWithNextAction> clients = actionService.getNextActionsByMerchantId(request, merchantId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<List<ClientWithNextAction>>(clients, Status.SUCCESS, Translator.toLocale("get_next_action_se_msg")));

	}
	
	
	@Override
	public ResponseEntity<Response<List<TimelineReq>>> getPerformedActionsByClientId(HttpServletRequest request, Long clientId) {

		List<TimelineReq> req = actionService.getPerformedActionsByClientId(request, clientId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<List<TimelineReq>>(req, Status.SUCCESS, Translator.toLocale("get_timeline_se_msg")));

	}
	
	@Override
	public ResponseEntity<Response<List<TimelineReq>>> getPerformedActionsByInvoiceId(HttpServletRequest request, Long invoiceId) {

		List<TimelineReq> req = actionService.getPerformedActionsByInvoiceId(request,invoiceId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response<List<TimelineReq>>(req, Status.SUCCESS, Translator.toLocale("get_timeline_se_msg")));

	}
}
