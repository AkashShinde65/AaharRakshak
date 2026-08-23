package com.aaharrakshak.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByProductIdOrderByExpiresOnAsc(Long productId);

    Optional<Batch> findByIdAndProductCompanyOwnerUserId(Long id, Long ownerUserId);

    Optional<Batch> findFirstByBatchNumberIgnoreCaseOrderByIdAsc(String batchNumber);
}
