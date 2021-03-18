package net.bflows.pagafatture.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.TimelineEntity;

@Repository
public interface TimelineRepository extends JpaRepository<TimelineEntity, Long> {

	List<TimelineEntity> findByClientEntityId(Long clientId);
	
	List<TimelineEntity> findByInvoiceId(Long invoiceId);
	
	@Query(value="SELECT lastPerformedAction FROM TimelineEntity lastPerformedAction where lastPerformedAction.type ='ACTION' and lastPerformedAction.skip=false and lastPerformedAction.reschedule=false and lastPerformedAction.clientEntity.id=:id ORDER BY  lastPerformedAction.createdDate DESC")
	List<TimelineEntity> findLastActionPerformedByClientId(@Param("id") Long id, Pageable pageable); 
	
	@Query(value="SELECT performedAction FROM TimelineEntity performedAction where  performedAction.clientEntity.id=:clientId and performedAction.clientEntity.deleted= false " + 
			"and performedAction.invoice.id=:invoiceId and performedAction.actionEntity.id=:actionId and performedAction.actionEntity.isDeleted=false")
	TimelineEntity findByClientIdAndInvoiceIdAndActionId(@Param("clientId") Long clientId,@Param("invoiceId") Long invoiceId,@Param("actionId") Long actionId);
}
