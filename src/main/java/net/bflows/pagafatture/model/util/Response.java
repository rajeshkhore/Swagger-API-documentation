package net.bflows.pagafatture.model.util;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.bflows.pagafatture.model.view.ActionView;
import net.bflows.pagafatture.model.view.ClientView;
import net.bflows.pagafatture.model.view.InvoiceView;
import net.bflows.pagafatture.model.view.WorkflowView;
import net.bflows.pagafatture.web.rest.errors.Status;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonView(value= {ClientView.ClientWithInvoiceDetailListBean.class,ClientView.ClientListWithInvoiceDetailBean.class,ClientView.InvoicesView.class
		,InvoiceView.InvoiceWithClientWithMerchant.class,InvoiceView.InvoicesBean.class,ClientView.ClientBean.class,InvoiceView.InvoicesReqBean.class,WorkflowView.WorkflowWithoutAction.class
		,ActionView.ActionWithoutTriggerDays.class})
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;


	private T data;

	private Status status;

	private String message;

}
