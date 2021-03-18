package net.bflows.pagafatture.model.widget;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoicesWithAverageDaysWidget {

	private String title;

	private Long averageDays;

	List<InvoicesWithAverageDaysMonthWise> data;
}
