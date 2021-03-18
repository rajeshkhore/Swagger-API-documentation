package net.bflows.pagafatture.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.dao.InvoicesDao;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.InvoiceUpdateReq;
import net.bflows.pagafatture.service.InvoicesService;
import net.bflows.pagafatture.util.Validator;
import net.bflows.pagafatture.web.rest.errors.BadRequestAlertException;

@Service
public class InvoicesServiceImpl implements InvoicesService {

    @Autowired
    private InvoicesDao invoicesDao;
    
    @Autowired
    Validator validator;

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceReq> getAllInvoicesByInvoiceState(HttpServletRequest httpServletRequest, String invoiceState) {

    	Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest);
    	List<String> invoiceStateList = new ArrayList<>();
        if (StringUtils.isNotEmpty(invoiceState)) {
            String invoiceStateArr[] = invoiceState.split(",");
            if (0 < invoiceStateArr.length) {
                for (String state : invoiceStateArr) {
                    try {
                        InvoiceStateEnum invoiceStateEnum = InvoiceStateEnum.fromValue(state);
                        if (invoiceStateEnum != null) {
                            invoiceStateList.add(invoiceStateEnum.getValue().toString());
                        }
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestAlertException(Translator.toLocale("invalid_invoice_state")+" :- "+state, null, null);
                    }
                }
            }
        }
        return invoicesDao.getAllInvoicesByInvoiceState(invoiceStateList, userInfo);
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createMerchantInvoices(HttpServletRequest httpServletRequest, Long id, List<InvoiceReq> body) {

		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest);
		MerchantEntity merchantEntity=validator.validateUserMerchantId(userInfo, id);
		invoicesDao.createMerchantInvoices(merchantEntity, body);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InvoiceReq> getInvoicesByMerchant(HttpServletRequest httpServletRequest, Long id) {

		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest);
		validator.validateUserMerchantId(userInfo, id);
		// Calling DAO Function
		return invoicesDao.getInvoicesByMerchant(id);
	}

	@Override
	@Transactional(readOnly = true)
	public InvoiceResponseBean getInvoiceById(HttpServletRequest httpServletRequest, Long invoiceId) {

		// Calling DAO Function
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest);
		return invoicesDao.getInvoiceById(invoiceId, userInfo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public InvoiceReq updateMerchantInvoice(HttpServletRequest httpServletRequest, Long invoiceId, InvoiceUpdateReq body) {

		// Calling DAO Function
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest);
		return invoicesDao.updateMarchantInvoice(invoiceId, body, userInfo);

	}

	@Override
	public InvoiceResponseBean getInvoiceByExternalRef(HttpServletRequest httpServletRequest, String externalRef) {
		// Calling DAO Function
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest);
		return invoicesDao.getInvoiceByExternalRef(externalRef, userInfo);
	}

}
