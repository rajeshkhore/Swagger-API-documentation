package net.bflows.pagafatture.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.model.ClientReq;
import net.bflows.pagafatture.model.ClientResponseBean;
import net.bflows.pagafatture.model.InvoiceSearchReq;

public interface ClientService {

	ClientReq createClient(HttpServletRequest httpServletRequest, Long id, ClientReq body);

	ClientResponseBean getClientById(HttpServletRequest httpServletRequest, Long merchantId,Long id);

	ClientReq updateClient(HttpServletRequest httpServletRequest, Long id, Long clientId, ClientReq body);

	void deleteClient(HttpServletRequest httpServletRequest, Long id, Long clientId);

	List<ClientReq> getClientsByMerchantId(HttpServletRequest httpServletRequest, Long merchantId);

	ClientResponseBean getInvoicesByMerchantIdAndClientId(HttpServletRequest httpServletRequest, InvoiceSearchReq request);
	
}
