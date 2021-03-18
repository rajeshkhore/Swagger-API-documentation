package net.bflows.pagafatture.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.InvoicingConnectionDao;
import net.bflows.pagafatture.entities.InvoicingConnectionEntity;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.InvoicingConnectionReq;
import net.bflows.pagafatture.service.InvoicingConnectionService;
import net.bflows.pagafatture.util.Validator;

@Service
public class InvoicingConnectionServiceImpl implements InvoicingConnectionService {

	@Autowired
	InvoicingConnectionDao invoicingConnectionDao;
	
	@Autowired
	Validator validator;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public InvoicingConnectionEntity createInvoicingConnection(HttpServletRequest httpServletRequest, Long merchantId,InvoicingConnectionReq boady) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		MerchantEntity merchantEntity=  validator.validateUserMerchantId(userInfo, merchantId);
		return invoicingConnectionDao.createInvoicingConnection(merchantEntity, boady);
	}

	@Override
	@Transactional(readOnly = true)
	public InvoicingConnectionEntity getInvoicingConnectionByMerchantId(HttpServletRequest httpServletRequest, Long merchantId) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, merchantId);
		return invoicingConnectionDao.getInvoicingConnectionByMerchantId(merchantId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<InvoicingConnectionEntity> getInvoicingConnection(HttpServletRequest httpServletRequest, Long typeId) {
		return invoicingConnectionDao.getInvoicingConnection(typeId, validator.getUserInfoFromToken(httpServletRequest));
	}
}
