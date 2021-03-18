package net.bflows.pagafatture.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.DashboardDao;
import net.bflows.pagafatture.model.widget.InvoicesWithAverageDaysWidget;
import net.bflows.pagafatture.model.widget.TopUnpaidInvoicesWidgets;
import net.bflows.pagafatture.model.widget.TotalDueAndOverDueInvoicesWidget;
import net.bflows.pagafatture.model.widget.Widget;
import net.bflows.pagafatture.model.widget.WidgetsResponseBean;
import net.bflows.pagafatture.repositories.MerchantRepository;
import net.bflows.pagafatture.service.DashboardService;
import net.bflows.pagafatture.util.Validator;

@Service
public class DashoardServiceImpl implements DashboardService {

	@Autowired
	DashboardDao dashboardDao;
    
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	Validator  validator;
	
	@Override
	@Transactional(readOnly = true)
	public WidgetsResponseBean  getWidgets(HttpServletRequest request, Long merchantId) {
		
		Map<String, Object> userInfo = validator.getUserInfoFromToken(request); 
		validator.validateUserMerchantId(userInfo, merchantId);
		
		WidgetsResponseBean finalResponse = new WidgetsResponseBean ();
		Widget response = dashboardDao.getWidgets(merchantId);
		finalResponse.setTotalUnpaid(response);
		InvoicesWithAverageDaysWidget avgResponse = dashboardDao.getInvoicesWithAverageDays(merchantId);
		finalResponse.setAverageDays(avgResponse);
		TopUnpaidInvoicesWidgets unpaidInvoiceResponse =  dashboardDao.getTopUnpaidInvoices(merchantId);
        finalResponse.setMainDebtors(unpaidInvoiceResponse);
		TotalDueAndOverDueInvoicesWidget totalDueOrOverDueresponse = dashboardDao.getTotalDueAndOverDueInvoices(merchantId);
        finalResponse.setAgingBalance(totalDueOrOverDueresponse);
        finalResponse.setActionWidget(dashboardDao.getTotalActionAndTotalAmountByMerchantId(merchantId));
        return finalResponse;
	}

		

}
