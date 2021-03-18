package net.bflows.pagafatture.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.entities.Invoices.InvoiceStateEnum;
import net.bflows.pagafatture.model.view.InvoiceView;
import net.bflows.pagafatture.model.view.ClientView;

/**
 * Invoice
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:42:43.418892+02:00[Europe/Rome]")
@Getter
@Setter
@JsonView(value = { ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class })
public class InvoiceReq {
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class ,InvoiceView.InvoicesReqBean.class})
	@NotNull
	private BigDecimal amountGross;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private BigDecimal amountNet;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String externalId;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String externalRef;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	@NotNull
	@NotEmpty
	private String linkDoc;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	@NotNull
	@NotEmpty
	private String invoiceNumber;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String currency;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private InvoiceStateEnum invoiceState;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private LocalDateTime expectedDate;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private LocalDateTime dueDate;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private LocalDateTime createdDate;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedDate;
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	@NotNull
	@Valid
	private ClientReq client;
	
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class,
			ClientView.ClientWithInvoiceDetailListBean.class, ClientView.InvoicesView.class,
			InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private LocalDateTime externalCreatedDate;

}
