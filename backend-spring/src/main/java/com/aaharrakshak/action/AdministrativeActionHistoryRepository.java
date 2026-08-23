package com.aaharrakshak.action;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrativeActionHistoryRepository extends JpaRepository<AdministrativeActionHistory, Long> {

    List<AdministrativeActionHistory> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);

    List<AdministrativeActionHistory> findByNoticeIdOrderByCreatedAtAsc(Long noticeId);
}
