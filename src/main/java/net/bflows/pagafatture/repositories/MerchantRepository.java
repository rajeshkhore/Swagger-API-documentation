package net.bflows.pagafatture.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.MerchantEntity;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {

}
