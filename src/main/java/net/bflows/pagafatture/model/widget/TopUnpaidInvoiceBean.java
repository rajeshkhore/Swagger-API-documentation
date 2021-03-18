package net.bflows.pagafatture.model.widget;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopUnpaidInvoiceBean {

	private String clientName;

	private Double amount;

	private Long nOverdueDays;
	
	private Long clientId;

}
