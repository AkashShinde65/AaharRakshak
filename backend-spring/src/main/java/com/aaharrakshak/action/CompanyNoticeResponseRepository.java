package com.aaharrakshak.action;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyNoticeResponseRepository extends JpaRepository<CompanyNoticeResponse, Long> {

    List<CompanyNoticeResponse> findByNoticeIdOrderBySubmittedAtAsc(Long noticeId);
}
