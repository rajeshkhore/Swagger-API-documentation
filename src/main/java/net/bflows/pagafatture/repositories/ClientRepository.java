package net.bflows.pagafatture.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.ClientEntity;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

	 public ClientEntity findByVatNumberAndMerchantEntityId(String vatNumber,Long merhcantId);
	 
	 @Query("SELECT  c from ClientEntity c where c.merchantEntity.id = :merchantId and c.deleted=false")
	 public List<ClientEntity> findByMerchantEntityId(@Param("merchantId")  Long merchantId);
	 
	 public ClientEntity findByIdAndMerchantEntityId(Long clientId,Long merhcantId);
	 
	 @Query("SELECT  c from ClientEntity c where c.workflowEntity.id = :workflowId and c.workflowEntity.isDeleted=false and c.deleted=false")
	 public List<ClientEntity> findByWorkflowEntityId(@Param("workflowId") Long workflowId);
}
