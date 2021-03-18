package net.bflows.pagafatture.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.TransactionReq;

public interface TransactionService {

	public TransactionReq createTransaction(HttpServletRequest httpServletRequest, Long invoiceId, TransactionReq transactionReq);

	public TransactionReq updateTransaction(HttpServletRequest httpServletRequest, Long invoiceId, TransactionReq transactionReq);

	public TransactionsEntity getTransactionByInvoiceId(HttpServletRequest httpServletRequest, Long id);

	public List<InvoiceResponseBean> getAllTransactionByMerchantId(HttpServletRequest httpServletRequest, Long merchantId);

	public void deleteTransaction(HttpServletRequest httpServletRequest, Long id);
}
