package net.bflows.pagafatture.model.widget;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUnpaidInvoicesWidgets {

	private String title;
	
	private List<TopUnpaidInvoiceBean> data;
}
