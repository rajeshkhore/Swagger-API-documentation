package net.bflows.pagafatture.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.entities.InvoicingConnectionEntity;
import net.bflows.pagafatture.model.InvoicingConnectionReq;

public interface InvoicingConnectionService {

	public InvoicingConnectionEntity createInvoicingConnection(HttpServletRequest httpServletRequest, Long merchantId, InvoicingConnectionReq boady);
	
	public InvoicingConnectionEntity getInvoicingConnectionByMerchantId(HttpServletRequest httpServletRequest, Long merchantId);
	
	public List<InvoicingConnectionEntity> getInvoicingConnection(HttpServletRequest httpServletRequest, Long typeId);
}
