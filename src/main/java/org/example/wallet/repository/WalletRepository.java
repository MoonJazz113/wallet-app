package org.example.wallet.repository;

import jakarta.persistence.LockModeType;
import org.example.wallet.dto.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WalletEntity> findById(UUID id);
}
