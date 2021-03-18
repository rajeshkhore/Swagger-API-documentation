package net.bflows.pagafatture.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.InvoicingConnectionEntity;

@Repository
public interface InvoicingConnectionRepository extends JpaRepository<InvoicingConnectionEntity, Long> {
	public InvoicingConnectionEntity findByMerchantEntityId(Long merchantId);
	
	@Query("select invoicingConnection from InvoicingConnectionEntity invoicingConnection where invoicingConnection.typeId =:typeId and (:merchantId is null or invoicingConnection.merchantEntity.id = :merchantId)")
	public List<InvoicingConnectionEntity> findByTypeIdAndMerchantEntityId(@Param("typeId") Long typeId, @Param("merchantId") Long merchantId);
	
}
