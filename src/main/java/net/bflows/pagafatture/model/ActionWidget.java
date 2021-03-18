package net.bflows.pagafatture.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionWidget {

	private String title;
	
	private BigDecimal totalAmount;
	
	private Integer numberOfAction;
}
