package net.bflows.pagafatture.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.bflows.pagafatture.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public Optional<UserEntity> findByEmailIgnoreCase(String email);
    public Optional<UserEntity> findByUserStatusAndEmailIgnoreCase(String userStatus, String email);
    public UserEntity findByMerchantId(Long id);
    
}
