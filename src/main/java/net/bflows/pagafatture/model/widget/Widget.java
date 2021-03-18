package net.bflows.pagafatture.model.widget;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Widget {

	
	private String title;
	
	private BigDecimal total;
	
	private BigDecimal due;
	
	private BigDecimal overDue;


		
	
}
