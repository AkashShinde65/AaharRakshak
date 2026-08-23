package com.aaharrakshak.investigation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabTestResultRepository extends JpaRepository<LabTestResult, Long> {

    List<LabTestResult> findByLabReportIdOrderByIdAsc(Long labReportId);
}
