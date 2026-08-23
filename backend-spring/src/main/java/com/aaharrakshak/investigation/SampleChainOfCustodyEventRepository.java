package com.aaharrakshak.investigation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleChainOfCustodyEventRepository extends JpaRepository<SampleChainOfCustodyEvent, Long> {

    List<SampleChainOfCustodyEvent> findBySampleIdOrderByEventAtAsc(Long sampleId);
}
