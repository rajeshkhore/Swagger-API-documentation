package net.bflows.pagafatture.service;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.model.widget.WidgetsResponseBean;

public interface DashboardService {

	public WidgetsResponseBean  getWidgets(HttpServletRequest request, Long merchantId);
	
}
