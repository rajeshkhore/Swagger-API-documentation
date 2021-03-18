package net.bflows.pagafatture.service;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.model.MerchantReq;

public interface MerchantService {

    public MerchantReq createMerchant(MerchantReq merchant);
    public MerchantReq updateMerchant(HttpServletRequest httpServletRequest, Long id , MerchantReq merchant);
    public MerchantReq getMerchantById(HttpServletRequest httpServletRequest, Long id);
    public Boolean deleteMerchant(HttpServletRequest httpServletRequest, Long id);
    
}
