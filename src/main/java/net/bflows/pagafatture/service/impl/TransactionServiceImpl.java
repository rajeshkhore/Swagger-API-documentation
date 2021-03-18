package net.bflows.pagafatture.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.TransactionDao;
import net.bflows.pagafatture.entities.TransactionsEntity;
import net.bflows.pagafatture.model.InvoiceResponseBean;
import net.bflows.pagafatture.model.TransactionReq;
import net.bflows.pagafatture.service.TransactionService;
import net.bflows.pagafatture.util.Validator;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	TransactionDao transactionDao;
	
	@Autowired
	Validator validator;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TransactionReq createTransaction(HttpServletRequest httpServletRequest, Long invoiceId, TransactionReq transactionReq) {
		return transactionDao.createTransaction(invoiceId, transactionReq, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TransactionReq updateTransaction(HttpServletRequest httpServletRequest, Long invoiceId,  TransactionReq transactionReq) {
		return transactionDao.updateTransaction(invoiceId,  transactionReq, validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(readOnly = true)
	public TransactionsEntity getTransactionByInvoiceId(HttpServletRequest httpServletRequest, Long id) {
		return transactionDao.getTransactionByInvoiceId(id,validator.getUserInfoFromToken(httpServletRequest));
	}

	@Override
	@Transactional(readOnly = true)
	public List<InvoiceResponseBean> getAllTransactionByMerchantId(HttpServletRequest httpServletRequest, Long merchantId) {
		Map<String, Object> userInfo = validator.getUserInfoFromToken(httpServletRequest); 
		validator.validateUserMerchantId(userInfo, merchantId);
		return transactionDao.getAllTransactionByMerchantId(merchantId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteTransaction(HttpServletRequest httpServletRequest, Long id) {
		transactionDao.deleteTransaction(id, validator.getUserInfoFromToken(httpServletRequest));
	}

}
