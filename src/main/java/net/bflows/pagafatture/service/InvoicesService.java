package net.bflows.pagafatture.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.model.InvoiceReq;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.InvoiceUpdateReq;

public interface InvoicesService {

	public List<InvoiceReq> getAllInvoicesByInvoiceState(HttpServletRequest httpServletRequest,String invoiceState);

	public void createMerchantInvoices(HttpServletRequest httpServletRequest, Long id, List<InvoiceReq> body);

	public List<InvoiceReq> getInvoicesByMerchant(HttpServletRequest httpServletRequest,Long id);

	public InvoiceResponseBean getInvoiceById(HttpServletRequest httpServletRequest, Long invoiceId);
	
	public InvoiceResponseBean getInvoiceByExternalRef(HttpServletRequest httpServletRequest, String externalRef);

	public InvoiceReq updateMerchantInvoice(HttpServletRequest httpServletRequest, Long invoiceId, InvoiceUpdateReq body);

}
