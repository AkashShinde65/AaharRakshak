package com.aaharrakshak.action;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyAlertRepository extends JpaRepository<SafetyAlert, Long> {

    List<SafetyAlert> findByActiveTrueOrderByPublishedAtDesc();
}
