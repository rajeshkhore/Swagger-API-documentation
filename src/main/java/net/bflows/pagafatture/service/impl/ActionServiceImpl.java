package net.bflows.pagafatture.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.ActionDao;
import net.bflows.pagafatture.model.ClientWithNextAction;
import net.bflows.pagafatture.model.TimelineReq;
import net.bflows.pagafatture.service.ActionService;
import net.bflows.pagafatture.util.Validator;

@Service
public class ActionServiceImpl implements ActionService {

	@Autowired
	ActionDao actionDao;
	
	@Autowired
	Validator validator;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TimelineReq performAction(HttpServletRequest request, Long actionId, TimelineReq timelineReq) {
		return actionDao.performAction(actionId, timelineReq, validator.getUserInfoFromToken(request));
	}
	
	@Override
	public List<ClientWithNextAction> getNextActionsByMerchantId(HttpServletRequest request, Long merchantId) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(request); 
		validator.validateUserMerchantId(userInfo, merchantId);
		return  actionDao.getNextActionsByMerchantId(merchantId,false);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimelineReq> getPerformedActionsByClientId(HttpServletRequest request, Long clientId) {
		return actionDao.getPerformedActionsByClientId(clientId, validator.getUserInfoFromToken(request));
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimelineReq> getPerformedActionsByInvoiceId(HttpServletRequest request, Long invoiceId) {
		return actionDao.getPerformedActionsByInvoiceId(invoiceId, validator.getUserInfoFromToken(request));
	}
}
