package net.bflows.pagafatture.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.ContactDao;
import net.bflows.pagafatture.model.ContactReq;
import net.bflows.pagafatture.service.ContactService;
import net.bflows.pagafatture.util.Validator;

@Service
public class ContactServiceImpl implements ContactService {

	@Autowired
	ContactDao contactDao;
	
	@Autowired
	Validator validator;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ContactReq createContact(HttpServletRequest httpServletRequest, Long clientId, ContactReq body) {
		return contactDao.createContact(clientId, body, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ContactReq updateContact(HttpServletRequest httpServletRequest, Long clientId, Long contactId, ContactReq body) {
			
		return contactDao.updateContact(clientId, contactId, body, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ContactReq> getContactsByClientId(HttpServletRequest httpServletRequest, Long clientId) {
		return contactDao.getContactByClientId(clientId, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(readOnly = true)
	public ContactReq getContactsById(HttpServletRequest httpServletRequest, Long clientId, Long contactId) {
		return contactDao.getContactById(clientId, contactId, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteContact(HttpServletRequest httpServletRequest, Long clientId, Long contactId) {
		contactDao.deleteContact(clientId, contactId, validator.getUserInfoFromToken(httpServletRequest));
		
	}
}
