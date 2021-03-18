package net.bflows.pagafatture.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.ActionTypeEntity;

@Repository
public interface ActionTypeRepository extends JpaRepository<ActionTypeEntity, Long> {

	public ActionTypeEntity findByName(String name);
}
