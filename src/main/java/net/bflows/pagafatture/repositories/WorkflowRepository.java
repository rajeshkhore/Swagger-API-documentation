package net.bflows.pagafatture.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.WorkflowEntity;

@Repository
public interface WorkflowRepository extends JpaRepository<WorkflowEntity, Long> {

	public WorkflowEntity findByIdAndIsDeleted(Long id, Boolean isDeleted);
	
	public List<WorkflowEntity> findByMerchantEntityIdAndIsDeleted(Long merchantId, Boolean isDeleted);
	
	public WorkflowEntity findByIdAndMerchantEntityIdAndIsDeleted(Long id,Long merchantId, Boolean isDeleted);

}
