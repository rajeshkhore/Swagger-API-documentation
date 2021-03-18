package net.bflows.pagafatture.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.ActionEntity;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, Long> {

	public ActionEntity findByIdAndIsDeleted(Long actionId, Boolean isDeleted);
	
	public List<ActionEntity> findByWorkflowEntityIdAndIsDeleted(Long workflowId, Boolean isDeleted);
}
