package com.aaharrakshak.investigation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<Sample, Long> {

    boolean existsBySampleNumber(String sampleNumber);

    boolean existsBySealNumber(String sealNumber);

    List<Sample> findByComplaintIdOrderByCollectedAtAsc(Long complaintId);

    Optional<Sample> findBySampleNumber(String sampleNumber);
}
