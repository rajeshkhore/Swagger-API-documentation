package net.bflows.pagafatture.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.ContactsEntity;

@Repository
public interface ContactRepository extends JpaRepository<ContactsEntity, Long> {

	@Query("SELECT  c from ContactsEntity c where c.clientEntity.id = :clientId and c.deleted=false")
	public List<ContactsEntity> findByClientEntityId(@Param("clientId")  Long clientId);

}
