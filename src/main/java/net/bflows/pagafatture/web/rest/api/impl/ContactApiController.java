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
import net.bflows.pagafatture.model.ContactReq;
import net.bflows.pagafatture.model.util.Response;
import net.bflows.pagafatture.service.ContactService;
import net.bflows.pagafatture.web.rest.api.ContactApi;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-06T12:57:09.877+02:00[Europe/Rome]")
@RestController
@RequestMapping("${openapi.swaggerBflowsMiddleware.base-path:/v1}")
public class ContactApiController implements ContactApi {

	public final Logger log = LoggerFactory.getLogger(ContactApiController.class);

	@Autowired
	ContactService contactService;

	@Override
	public ResponseEntity<Response<ContactReq>> createContact(HttpServletRequest request, Long id, ContactReq body) {
		try {
			ContactReq contactReq = contactService.createContact(request,id, body);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new Response<ContactReq>(contactReq, Status.SUCCESS, Translator.toLocale("create_contact_se_msg")));

		} catch (CustomValidationException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public ResponseEntity<Response<ContactReq>> getContactById(HttpServletRequest request, Long clientId, Long id) {

		try {
			ContactReq contactReq = contactService.getContactsById(request, clientId, id);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<ContactReq>(contactReq, Status.SUCCESS, Translator.toLocale("get_contact_sc_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

	}

	@Override
	public ResponseEntity<Response<List<ContactReq>>> getContactsByClientId(HttpServletRequest request, Long clientId) {

		try {
			List<ContactReq> contactReqs = contactService.getContactsByClientId(request, clientId);

			return ResponseEntity.status(HttpStatus.OK).body(
					new Response<List<ContactReq>>(contactReqs, Status.SUCCESS, Translator.toLocale("get_contact_sc_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}

	}

	@Override
	public ResponseEntity<Response<ContactReq>> updateContact(HttpServletRequest request, Long id, Long contactId, ContactReq body) {
		try {
			ContactReq contactReq = contactService.updateContact(request, id, contactId, body);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<ContactReq>(contactReq, Status.SUCCESS, Translator.toLocale("update_contact_se_msg")));

		} catch (ResourceNotFoundException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		}
	}

	@Override
	public ResponseEntity<Response<Void>> deleteContact(HttpServletRequest request, Long id, Long contactId) {
		try {
			contactService.deleteContact(request, id, contactId);

			return ResponseEntity.status(HttpStatus.OK)
					.body(new Response<>(null, Status.SUCCESS, Translator.toLocale("delete_contact_se_msg")));

		} catch (ResourceNotFoundException ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

}
