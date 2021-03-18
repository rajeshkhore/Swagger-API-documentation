package net.bflows.pagafatture.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import net.bflows.pagafatture.model.view.InvoiceView;
import net.bflows.pagafatture.model.view.ClientView;	

@ApiModel(description = "Client Entity")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-01T16:42:43.418892+02:00[Europe/Rome]")
@Getter
@Setter
@JsonView(value = { ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,
		ClientView.ClientListWithInvoiceDetailBean.class, InvoiceView.InvoiceWithClientWithMerchant.class,
		ClientView.ClientBean.class })
public class ClientReq {
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { InvoiceView.InvoicesBean.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.InvoicesView.class, ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.ClientBean.class,InvoiceView.InvoicesReqBean.class })
	private Long id;

	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String name;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String vatNumber;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.InvoicesView.class, ClientView.ClientBean.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String exIdClient;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String cf;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String address;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String zipCode;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String city;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String province;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String addressExtra;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String paCode;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class ,InvoiceView.InvoicesReqBean.class})
	private String pec;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String email;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String country;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String token;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private String telephone;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class })
	private Long mercantId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class })
	private InvoiceDetails invoiceDetails;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private Boolean deleted;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private LocalDateTime createdDate;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { InvoiceView.InvoiceWithClientWithMerchant.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private LocalDateTime updatedDate;
	

	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class })
	private Integer defaultPaymentDays;
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class})
	private Long workflowId;
	
	
	@JsonView(value = { ClientView.ClientListWithInvoiceDetailBean.class,
			InvoiceView.InvoiceWithClientWithMerchant.class, ClientView.ClientWithInvoiceDetailListBean.class,
			ClientView.ClientBean.class, ClientView.InvoicesView.class, InvoiceView.InvoicesBean.class,InvoiceView.InvoicesReqBean.class})
	private String workflowName;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { ClientView.ClientWithInvoiceDetailListBean.class})
	private LocalDateTime lastCommunicationDate;

}
