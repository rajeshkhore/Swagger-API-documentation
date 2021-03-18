package net.bflows.pagafatture.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.entities.TransactionsEntity.TransactionsMethodTypeEnum;
import net.bflows.pagafatture.model.view.InvoiceView;

@ApiModel(description = "Transactions Entity")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-01T16:42:43.418892+02:00[Europe/Rome]")
@Getter
@Setter
@JsonView(value= {InvoiceView.InvoicesBean.class})
public class TransactionReq {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id ;
	private BigDecimal amount;
	private TransactionsMethodTypeEnum methodType;
	private LocalDateTime paymentDate;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdDate;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedDate;
	private String currency;
}
