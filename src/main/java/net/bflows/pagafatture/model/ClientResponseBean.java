package net.bflows.pagafatture.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

import net.bflows.pagafatture.model.view.InvoiceView;
import net.bflows.pagafatture.model.view.ClientView;

@Getter
@Setter
@JsonView(value= {ClientView.ClientWithInvoiceDetailListBean.class,InvoiceView.InvoiceWithClientWithMerchant.class
		,InvoiceView.InvoicesBean.class})
public class ClientResponseBean {
	
	@JsonView(value= {ClientView.ClientWithInvoiceDetailListBean.class,ClientView.InvoicesView.class,
			InvoiceView.InvoiceWithClientWithMerchant.class,ClientView.ClientBean.class})
	private ClientReq client;
	@JsonView(value= {ClientView.ClientWithInvoiceDetailListBean.class})
	private ClientInvoiceDetail  invoiceDetail;
    
    @JsonView(value= {ClientView.InvoicesView.class})
    private MerchantReq merchant;
    @JsonView(value= {ClientView.InvoicesView.class})
	private List<InvoiceReq> invoicesList;
}
