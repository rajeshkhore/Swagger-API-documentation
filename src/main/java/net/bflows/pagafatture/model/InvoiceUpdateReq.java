package net.bflows.pagafatture.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Setter;
import lombok.Getter;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
@Getter
@Setter
public class InvoiceUpdateReq {
    
	 
    private BigDecimal amountGross;
    private BigDecimal amountNet;
    private String externalId;
    private String externalRef;
    private String linkDoc;
    private String invoiceNumber;
    private String currency;
    private InvoiceStateEnum invoiceState;
    private LocalDateTime expectedDate;
	
	private LocalDateTime dueDate;

	private LocalDateTime externalCreatedDate;
	
}
