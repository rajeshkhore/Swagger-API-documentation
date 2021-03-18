package net.bflows.pagafatture.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceSearchReq {
	private String token;
	private String invoiceState;
}
