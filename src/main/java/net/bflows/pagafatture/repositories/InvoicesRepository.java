package net.bflows.pagafatture.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.Invoices;
@Repository
public interface InvoicesRepository extends JpaRepository<Invoices, Long> {
    
	public Invoices findByExternalRef(String externalRef);
    public Optional<Invoices> findByExternalRefIgnoreCase(String externalRef);
    
    @Query("SELECT  sum(invoice.amountGross) from MerchantEntity m ,ClientEntity c,Invoices invoice where m.id=:merchantId and m.id = c.merchantEntity.id and c.id= invoice.clientEntity.id  and invoice.invoiceState=:invoiceState and c.deleted=false")
    public BigDecimal getInvoiceDueAmountByInvoiceState(@Param("merchantId") Long merchantId,@Param("invoiceState") String invoiceState);
    
    @Query("SELECT  invoice from MerchantEntity m ,ClientEntity c,Invoices invoice where m.id=:merchantId and m.id = c.merchantEntity.id and c.id= invoice.clientEntity.id and c.deleted=false")
    public List<Invoices> findByMerchantEntityId(@Param("merchantId") Long merchantId);

    @Query("SELECT  EXTRACT(day from avg((transactions.paymentDate-invoice.externalCreatedDate))) from Invoices invoice,TransactionsEntity transactions  Where EXTRACT(MONTH from invoice.externalCreatedDate)  = :month and EXTRACT(YEAR from invoice.externalCreatedDate)  = :year and invoice.clientEntity.merchantEntity.id=:merchantId"
			+ " and invoice.id =transactions.invoices.id and invoice.invoiceState = 'PAID' and invoice.clientEntity.deleted = false")
	public Double getAvgeDurationForInvoices(@Param("month") Integer month, @Param("year") Integer year,
			@Param("merchantId") Long merchantId);
	
	@Query("select DISTINCT c.name as clientName  , avg(EXTRACT(day from now()-i.dueDate)) as nOverDueDays ,"
			+ "c.id as id   from Invoices i , ClientEntity c where i.invoiceState IN('OVERDUE') "
			+ "and c.id=i.clientEntity.id   and c.deleted=false and c.merchantEntity.id=:merchantId group by  c.id"
			+ " ORDER by nOverDueDays DESC")
	public List<Map<String,Object>> getTopUnpaidinvoiceByClient(@Param("merchantId") Long merchantId, Pageable pageable);

	
	@Query("select DISTINCT sum (i.amountGross) as totalAmount" + 
			" from Invoices i , ClientEntity c" + 
			" where i.invoiceState in ('OVERDUE','DUE')" + 
			" and i.clientEntity.id =:clientId and   c.id=i.clientEntity.id and c.deleted=false")
	public Double calculateTotalAmountOfInvoicesByClientId(@Param("clientId") Long clientId);
	
	
	
	
	
	@Query("Select sum(invoice.amountGross) from MerchantEntity m ,ClientEntity c,Invoices invoice where   EXTRACT(day from (invoice.dueDate- invoice.externalCreatedDate ) ) BETWEEN :startRang  and :endRang and m.id=:merchantId and m.id = c.merchantEntity.id and c.id= invoice.clientEntity.id  and invoice.invoiceState=:invoiceState and c.deleted=false")
	public Double getTotalAmountByInvoiceStateAndMerchantId(@Param("invoiceState") String invoiceState,
			@Param("merchantId") Long merchantId, @Param("startRang") Integer startRang,
			@Param("endRang") Integer endRang);

	@Query("Select sum(invoice.amountGross) from MerchantEntity m ,ClientEntity c,Invoices invoice where   EXTRACT(day from (invoice.dueDate -invoice.externalCreatedDate) ) >90 and m.id=:merchantId and m.id = c.merchantEntity.id and c.id= invoice.clientEntity.id  and invoice.invoiceState=:invoiceState and c.deleted=false")
	public Double getTotalAmountByInvoiceStateAndMerchantIdGreaterThanNinety(@Param("invoiceState") String invoiceState,
			@Param("merchantId") Long merchantId);
	
	public List<Invoices> findAllByClientEntityIdAndClientEntityMerchantEntityId(Long merchantId, Long merchantId2);

  @Query("SELECT  sum(invoice.amountGross)  as total , count(invoice.currency) as numOfInvoices ,invoice.currency as currency from Invoices invoice where  invoice.clientEntity.id =:clientId and invoice.invoiceState=:invoiceState GROUP By  invoice.currency")
	public List<Map<String, Object>> getTotalAmountByClientIdAndCurrency(@Param("clientId") Long clientId , @Param("invoiceState") String invoiceState);


	@Query("select sum(invoice.amountGross) from Invoices invoice where invoice.invoiceState IN('DUE','OVERDUE') and invoice.clientEntity.id =:clientId  and invoice.clientEntity.deleted=false")
	public Double getTotalDueAndOverdueAmountByClientId(@Param("clientId") Long clientId);
	
	@Query("SELECT  EXTRACT(day from avg((now()-invoice.dueDate))) from Invoices invoice  Where   invoice.clientEntity.id =:clientId and invoice.invoiceState = 'OVERDUE' and invoice.clientEntity.deleted = false")
	public Double calculateAverageDelayInPaymentByClientId(@Param("clientId") Long clientId);
		
	public List<Invoices> findByClientEntityIdOrderByDueDateAsc(Long clientId,Pageable pageable);

	@Query("select invoice from Invoices invoice where invoice.clientEntity.id =:clientId and invoice.clientEntity.merchantEntity.id =:merchantId and  ( ((:invoiceState) is null ) or (invoice.invoiceState IN (:invoiceState)) )")
	public List<Invoices> findAllByClientEntityIdAndClientEntityMerchantEntityIdAndInvoiceState(@Param("clientId") Long clientId,@Param("merchantId") Long merchantId,@Param("invoiceState") List<String> invoiceState);
	
	public List<Invoices> findByInvoiceStateInAndClientEntityDeleted(List<String> invoiceStates,Boolean isDeleted);
	
	public List<Invoices> findByClientEntityDeleted(Boolean isDeleted);
	
	@Query("SELECT  invoice from ClientEntity c,Invoices invoice where c.id=:clientId and c.id= invoice.clientEntity.id and c.deleted=false and invoice.invoiceState IN('DUE','OVERDUE')")
	public List<Invoices> findByClientEntityId(@Param("clientId") Long clientId);
	
	
	@Query("select invoice from Invoices invoice where invoice.clientEntity.deleted =:isDeleted and  (:merchantId is null or invoice.clientEntity.merchantEntity.id = :merchantId) and  ( ((:invoiceStates) is null ) or (invoice.invoiceState IN (:invoiceStates)) )")
	public List<Invoices> findByInvoiceStateInAndClientEntityDeletedAndClientEntityMerchantEntityId( @Param("invoiceStates") List<String> invoiceStates,@Param("isDeleted") Boolean isDeleted,@Param("merchantId") Long merchantId);
	
	@Query("select invoice from Invoices invoice where invoice.clientEntity.deleted =:isDeleted and (:merchantId is null or invoice.clientEntity.merchantEntity.id = :merchantId)")
	public List<Invoices> findByClientEntityDeleted(@Param("isDeleted") Boolean isDeleted,@Param("merchantId") Long merchantId);

}
