package net.bflows.pagafatture.model.widget;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.model.ActionWidget;

@Getter
@Setter
public class WidgetsResponseBean {

	private Widget totalUnpaid;
	
	private InvoicesWithAverageDaysWidget averageDays;
	
	private TopUnpaidInvoicesWidgets mainDebtors;
	
	private TotalDueAndOverDueInvoicesWidget agingBalance;
	
	private ActionWidget actionWidget;
}
