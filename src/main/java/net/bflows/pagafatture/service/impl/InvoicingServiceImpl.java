package net.bflows.pagafatture.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.dao.InvoicingDao;
import net.bflows.pagafatture.entities.InvoicingEntity;
import net.bflows.pagafatture.service.InvoicingService;

@Service
public class InvoicingServiceImpl implements InvoicingService {

	@Autowired
	private InvoicingDao invoicingDao;

	@Override
	@Transactional(readOnly = true)
	public List<InvoicingEntity> getAll() {
		List<InvoicingEntity> invoicingEntities = invoicingDao.getAll();
		return invoicingEntities;
	}

}
