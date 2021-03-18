package net.bflows.pagafatture.model.widget;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalDueAndOverDueInvoicesWidget {

	private String title;
	
	private Double dueAmount;
	
	private Double overdueAmount;
	
	private List<TotalDueAndOverDueInvoicesWithRange> data;
}
