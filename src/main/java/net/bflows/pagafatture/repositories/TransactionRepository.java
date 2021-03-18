package net.bflows.pagafatture.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.TransactionsEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionsEntity, Long> {

	 @Query("SELECT  transaction from TransactionsEntity transaction where transaction.invoices.clientEntity.merchantEntity.id=:merchantId and transaction.invoices.clientEntity.deleted=false")
	 public List<TransactionsEntity> findByMerchantId(@Param("merchantId") Long merchantId);
	 
	 public TransactionsEntity findByInvoicesId(Long invoiceId);
}
