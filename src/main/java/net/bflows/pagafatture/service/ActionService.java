package net.bflows.pagafatture.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.model.ClientWithNextAction;
import net.bflows.pagafatture.model.TimelineReq;

public interface ActionService {

	public TimelineReq performAction(HttpServletRequest request, Long actionId, TimelineReq timelineReq);

	public List<ClientWithNextAction> getNextActionsByMerchantId(HttpServletRequest request, Long merchantId);
	
	public List<TimelineReq> getPerformedActionsByClientId(HttpServletRequest request, Long clientId);
	
	public List<TimelineReq> getPerformedActionsByInvoiceId(HttpServletRequest request, Long invoiceId);
}
