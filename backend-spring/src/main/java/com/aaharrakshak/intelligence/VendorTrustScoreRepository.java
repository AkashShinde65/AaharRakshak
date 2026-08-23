package com.aaharrakshak.intelligence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorTrustScoreRepository extends JpaRepository<VendorTrustScore, Long> {

    Optional<VendorTrustScore> findByCompanyId(Long companyId);
}
