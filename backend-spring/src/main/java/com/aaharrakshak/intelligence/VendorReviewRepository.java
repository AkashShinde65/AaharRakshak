package com.aaharrakshak.intelligence;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorReviewRepository extends JpaRepository<VendorReview, Long> {

    boolean existsByCitizenIdAndCompanyIdAndReceiptChecksumSha256(Long citizenId, Long companyId, String checksum);

    boolean existsByCitizenIdAndCompanyIdAndCreatedAtAfter(Long citizenId, Long companyId, Instant after);

    List<VendorReview> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
