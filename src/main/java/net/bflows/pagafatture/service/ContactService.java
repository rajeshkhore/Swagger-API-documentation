package net.bflows.pagafatture.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.model.ContactReq;

public interface ContactService {

	public ContactReq createContact(HttpServletRequest request, Long clientId, ContactReq body);

	public ContactReq updateContact(HttpServletRequest request, Long clientId, Long contactId, ContactReq body);

	public List<ContactReq> getContactsByClientId(HttpServletRequest request, Long clientId);

	public ContactReq getContactsById(HttpServletRequest request, Long clientId, Long contactId);

	public void deleteContact(HttpServletRequest request, Long clientId, Long contactId);
}
