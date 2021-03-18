package net.bflows.pagafatture.repositories;

import java.util.Optional;

import net.bflows.pagafatture.entities.RoleEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>{

    public Optional<RoleEntity> findByRoleName(String roleName);
}
