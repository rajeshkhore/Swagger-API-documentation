package net.bflows.pagafatture.model.widget;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalDueAndOverDueInvoicesWithRange {

	private String dueRange;
	private Double overdueAmount;
	private Double dueAmount;
}
