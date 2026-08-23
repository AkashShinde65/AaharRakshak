package com.aaharrakshak.complaint;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    boolean existsByTicketNumber(String ticketNumber);

    Optional<Complaint> findByIdAndCitizenId(Long id, Long citizenId);

    Optional<Complaint> findByTicketNumberAndCitizenId(String ticketNumber, Long citizenId);

    Optional<Complaint> findByTicketNumber(String ticketNumber);

    List<Complaint> findByCitizenIdOrderByCreatedAtDesc(Long citizenId);

    List<Complaint> findByStatusInOrderByRiskScoreDescCreatedAtAsc(Set<ComplaintStatus> statuses);

    List<Complaint> findByStatusInAndGpsConsentTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndSubmittedAtAfter(
            Set<ComplaintStatus> statuses,
            Instant submittedAfter);

    List<Complaint> findByStatusInAndRiskScoreGreaterThanEqualAndSlaDueAtBeforeOrderBySlaDueAtAsc(
            Set<ComplaintStatus> statuses,
            Integer riskScore,
            Instant now);

    @Query("""
            select distinct c
            from Complaint c
            where c.batch.id = :batchId
              and (:district is null or lower(c.district) = lower(:district))
            """)
    List<Complaint> findAffectedBatchComplaints(
            @Param("batchId") Long batchId,
            @Param("district") String district);

    @Query("""
            select distinct c
            from Complaint c
            left join c.company company
            left join c.product product
            left join c.batch batch
            where c.status in :statuses
              and (:complaintNumber is null or lower(c.ticketNumber) like lower(concat('%', :complaintNumber, '%')))
              and (:company is null or lower(company.legalName) like lower(concat('%', :company, '%'))
                   or lower(c.confirmedCompanyName) like lower(concat('%', :company, '%')))
              and (:product is null or lower(product.name) like lower(concat('%', :product, '%'))
                   or lower(c.confirmedProductName) like lower(concat('%', :product, '%')))
              and (:batch is null or lower(batch.batchNumber) like lower(concat('%', :batch, '%'))
                   or lower(c.confirmedBatchNumber) like lower(concat('%', :batch, '%')))
              and (:location is null or lower(c.locationText) like lower(concat('%', :location, '%'))
                   or lower(c.district) like lower(concat('%', :location, '%')))
            order by c.updatedAt desc
            """)
    List<Complaint> searchPublic(
            @Param("statuses") Set<ComplaintStatus> statuses,
            @Param("complaintNumber") String complaintNumber,
            @Param("company") String company,
            @Param("product") String product,
            @Param("batch") String batch,
            @Param("location") String location);
}
