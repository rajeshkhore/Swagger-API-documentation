package net.bflows.pagafatture.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.bflows.pagafatture.entities.InvoicingEntity;
import net.bflows.pagafatture.repositories.InvoicingRepository;

@Component
public class InvoicingDao {

	@Autowired
	private InvoicingRepository invoicingRepository;

	public List<InvoicingEntity> getAll() {
		return invoicingRepository.findAll();
	}
}
