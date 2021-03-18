package net.bflows.pagafatture.model;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.model.view.ActionView;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;

@Getter
@Setter
@JsonView(value = { ActionView.ActionWithoutTriggerDays.class})
public class ClientWithNextAction {

	private Long clientId;

	private String name;

	private Long invoiceId;

	@JsonView(value = { ActionView.ActionWithoutTriggerDays.class})
	private ActionReq nextAction;
	
	 private BigDecimal invoiceAmount;
	 
	 private String currency;
}

