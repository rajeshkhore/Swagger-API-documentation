package net.bflows.pagafatture.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.model.view.ClientView;

@Getter
@Setter
@JsonView(value= {ClientView.ClientListWithInvoiceDetailBean.class})
public class InvoiceDetails {

	private Long numOfDueInvoices=0l;
	
	private Long numOfOverdueInvoices=0l;
	
	private List<Map<String,Object>> totalDue;
	
	private List<Map<String,Object>> totalOverdue;
	
}
