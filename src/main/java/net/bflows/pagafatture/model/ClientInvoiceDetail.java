package net.bflows.pagafatture.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

import net.bflows.pagafatture.model.view.ClientView;

@Getter
@Setter
@JsonView(value= {ClientView.ClientWithInvoiceDetailListBean.class})
public class ClientInvoiceDetail {

	private Double totalDueAndOverdueAmount;

	private Double averageDelayInPayment;

	private List<InvoiceReq> invoices;
}
