package net.bflows.pagafatture.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.MerchantDAO;
import net.bflows.pagafatture.model.MerchantReq;
import net.bflows.pagafatture.service.MerchantService;
import net.bflows.pagafatture.util.Validator;
@Service
public class MerchantServiceImpl implements MerchantService{
    
    @Autowired
    MerchantDAO merchantDAO;
    
    @Autowired
    Validator validator;


    @Override
    @Transactional(rollbackFor=Exception.class)
	public MerchantReq createMerchant(MerchantReq merchant) {
		return merchantDAO.createMerchant(merchant);
	}

    @Override
    @Transactional(rollbackFor=Exception.class)
	public MerchantReq updateMerchant(HttpServletRequest httpServletRequest, Long id, MerchantReq merchant) {
    	Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, id);
    	return merchantDAO.updateMarchant(id, merchant);
	}

    @Override
    @Transactional(readOnly = true)
	public MerchantReq getMerchantById(HttpServletRequest httpServletRequest, Long id) {
    	Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, id);
    	return merchantDAO.getMerchantById(id);
	}

	@Override
    @Transactional(rollbackFor=Exception.class)
	public Boolean deleteMerchant(HttpServletRequest httpServletRequest, Long id) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, id);
		return merchantDAO.deleteMerchant(id);
		
	}

}
