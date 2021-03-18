package net.bflows.pagafatture.model;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.model.view.InvoiceView;
import net.bflows.pagafatture.model.view.ClientView;
@Getter
@Setter
@JsonView(value= {InvoiceView.InvoicesBean.class})
public class InvoiceResponseBean {

	@JsonView(value= {InvoiceView.InvoicesBean.class,InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class})
	private InvoiceReq invoice;
	@JsonView(value= {InvoiceView.InvoicesBean.class})
	private TransactionReq transaction;
}
