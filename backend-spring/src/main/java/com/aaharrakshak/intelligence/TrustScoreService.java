package com.aaharrakshak.intelligence;

import com.aaharrakshak.action.SafetyAlertRepository;
import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.BatchRepository;
import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.catalog.ProductRepository;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.company.CompanyRepository;
import com.aaharrakshak.intelligence.dto.TrustScoreResponse;
import com.aaharrakshak.intelligence.dto.VendorReviewRequest;
import com.aaharrakshak.intelligence.dto.VendorReviewResponse;
import com.aaharrakshak.investigation.ActionRepository;
import com.aaharrakshak.investigation.ActionType;
import com.aaharrakshak.investigation.InspectionVisitRepository;
import com.aaharrakshak.investigation.InspectionVisitStatus;
import com.aaharrakshak.investigation.LabOutcome;
import com.aaharrakshak.investigation.LabReport;
import com.aaharrakshak.investigation.LabReportRepository;
import com.aaharrakshak.investigation.LabReportStatus;
import com.aaharrakshak.security.AuthenticatedUser;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TrustScoreService {

    public static final String RAW_COMPLAINT_FAIRNESS_NOTE =
            "Raw complaints do not directly prove guilt and are not used alone to reduce Trust Score.";

    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final VendorReviewRepository reviewRepository;
    private final VendorTrustScoreRepository trustScoreRepository;
    private final ReceiptOcrAdapter receiptOcrAdapter;
    private final InspectionVisitRepository inspectionVisitRepository;
    private final LabReportRepository labReportRepository;
    private final ActionRepository actionRepository;
    @SuppressWarnings("unused")
    private final SafetyAlertRepository safetyAlertRepository;
    private final long reviewRateLimitMinutes;

    public TrustScoreService(
            CompanyRepository companyRepository,
            ProductRepository productRepository,
            BatchRepository batchRepository,
            VendorReviewRepository reviewRepository,
            VendorTrustScoreRepository trustScoreRepository,
            ReceiptOcrAdapter receiptOcrAdapter,
            InspectionVisitRepository inspectionVisitRepository,
            LabReportRepository labReportRepository,
            ActionRepository actionRepository,
            SafetyAlertRepository safetyAlertRepository,
            @Value("${app.intelligence.review-rate-limit-minutes}") long reviewRateLimitMinutes) {
        this.companyRepository = companyRepository;
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.reviewRepository = reviewRepository;
        this.trustScoreRepository = trustScoreRepository;
        this.receiptOcrAdapter = receiptOcrAdapter;
        this.inspectionVisitRepository = inspectionVisitRepository;
        this.labReportRepository = labReportRepository;
        this.actionRepository = actionRepository;
        this.safetyAlertRepository = safetyAlertRepository;
        this.reviewRateLimitMinutes = reviewRateLimitMinutes;
    }

    @Transactional
    public VendorReviewResponse submitReview(AuthenticatedUser principal, VendorReviewRequest request) {
        validateReceipt(request);
        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company not found"));
        Product product = request.productId() == null ? null : productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
        if (product != null && !product.getCompany().getId().equals(company.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product does not belong to company");
        }
        Batch batch = request.batchId() == null ? null : batchRepository.findById(request.batchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch not found"));
        if (batch != null && (product == null || !batch.getProduct().getId().equals(product.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch does not belong to product");
        }
        String checksum = request.receipt().checksumSha256().toLowerCase(Locale.ROOT);
        if (reviewRepository.existsByCitizenIdAndCompanyIdAndReceiptChecksumSha256(
                principal.getUserId(),
                company.getId(),
                checksum)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate receipt-backed review");
        }
        if (reviewRepository.existsByCitizenIdAndCompanyIdAndCreatedAtAfter(
                principal.getUserId(),
                company.getId(),
                Instant.now().minusSeconds(reviewRateLimitMinutes * 60L))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Review rate limit exceeded");
        }
        ReceiptOcrResult receiptResult = receiptOcrAdapter.verify(request.receipt());
        VendorReview review = reviewRepository.save(new VendorReview(
                principal.getUser(),
                company,
                product,
                batch,
                request.rating(),
                blankToNull(request.reviewText()),
                request.receipt().objectKey(),
                request.receipt().originalFileName(),
                request.receipt().contentType().toLowerCase(Locale.ROOT),
                request.receipt().sizeBytes(),
                checksum,
                receiptResult.verified(),
                receiptResult.verificationToken()));
        recalculate(company);
        return new VendorReviewResponse(
                review.getId(),
                company.getId(),
                review.getRating(),
                review.getReceiptVerified(),
                review.getReceiptVerificationToken(),
                review.getCreatedAt(),
                "Receipt-backed review accepted. Moderation and anti-spam checks were applied.");
    }

    @Transactional
    public TrustScoreResponse recalculate(Company company) {
        List<VendorReview> reviews = reviewRepository.findByCompanyIdOrderByCreatedAtDesc(company.getId());
        long completedInspections = inspectionVisitRepository.countByComplaintCompanyIdAndStatus(
                company.getId(),
                InspectionVisitStatus.COMPLETED);
        List<LabReport> reports = labReportRepository.findBySampleComplaintCompanyIdAndStatus(
                company.getId(),
                LabReportStatus.PUBLISHED);
        long recallCount = actionRepository.countByCompanyIdAndTypeIn(
                company.getId(),
                List.of(ActionType.BATCH_RECALL, ActionType.TEMPORARY_SUSPENSION, ActionType.CANCELLATION));
        BigDecimal inspectionPoints = BigDecimal.valueOf(Math.min(10, completedInspections * 2));
        BigDecimal labPoints = BigDecimal.valueOf(reports.stream().mapToInt(this::labOutcomePoints).sum())
                .max(BigDecimal.valueOf(-30))
                .min(BigDecimal.valueOf(10));
        BigDecimal recallPoints = BigDecimal.valueOf(-Math.min(30, recallCount * 15));
        BigDecimal reviewPoints = reviewPoints(reviews);
        BigDecimal score = BigDecimal.valueOf(75)
                .add(inspectionPoints)
                .add(labPoints)
                .add(recallPoints)
                .add(reviewPoints)
                .max(BigDecimal.ZERO)
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        RiskLevel level = trustRisk(score);
        String explanation = "Trust Score uses verified inspections, published lab outcomes, simulated recalls and receipt-backed reviews. "
                + RAW_COMPLAINT_FAIRNESS_NOTE;
        VendorTrustScore saved = trustScoreRepository.findByCompanyId(company.getId())
                .map(existing -> {
                    existing.update(score, level, inspectionPoints, labPoints, recallPoints, reviewPoints, reviews.size(), explanation);
                    return existing;
                })
                .orElseGet(() -> trustScoreRepository.save(new VendorTrustScore(
                        company,
                        score,
                        level,
                        inspectionPoints,
                        labPoints,
                        recallPoints,
                        reviewPoints,
                        reviews.size(),
                        explanation)));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TrustScoreResponse trustScore(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return trustScoreRepository.findByCompanyId(company.getId())
                .map(this::toResponse)
                .orElseGet(() -> defaultScore(company));
    }

    private void validateReceipt(VendorReviewRequest request) {
        if (request.receipt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt upload is required for verified review");
        }
        String contentType = request.receipt().contentType().toLowerCase(Locale.ROOT);
        if (!contentType.equals("application/pdf") && !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt must be a PDF or image");
        }
        if (request.receipt().sizeBytes() > 5 * 1024 * 1024L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt file is too large");
        }
        if (!request.receipt().checksumSha256().matches("[a-fA-F0-9]{64}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receipt checksum must be SHA-256 hex");
        }
    }

    private int labOutcomePoints(LabReport report) {
        if (report.getOutcome() == LabOutcome.SAFE) {
            return 5;
        }
        if (report.getOutcome() == LabOutcome.SUSPICIOUS) {
            return -10;
        }
        if (report.getOutcome() == LabOutcome.ADULTERATED) {
            return -20;
        }
        return 0;
    }

    private BigDecimal reviewPoints(Collection<VendorReview> reviews) {
        if (reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }
        double average = reviews.stream().mapToInt(VendorReview::getRating).average().orElse(3);
        return BigDecimal.valueOf((average - 3) * 5).setScale(2, RoundingMode.HALF_UP);
    }

    private RiskLevel trustRisk(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return RiskLevel.LOW;
        }
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return RiskLevel.MEDIUM;
        }
        if (score.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return RiskLevel.HIGH;
        }
        return RiskLevel.CRITICAL;
    }

    private TrustScoreResponse defaultScore(Company company) {
        return new TrustScoreResponse(
                company.getId(),
                company.getLegalName(),
                BigDecimal.valueOf(75).setScale(2, RoundingMode.HALF_UP),
                RiskLevel.MEDIUM,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                "Default score until verified inspections, lab outcomes, recalls or receipt-backed reviews are available.",
                RAW_COMPLAINT_FAIRNESS_NOTE,
                null);
    }

    private TrustScoreResponse toResponse(VendorTrustScore score) {
        return new TrustScoreResponse(
                score.getCompany().getId(),
                score.getCompany().getLegalName(),
                score.getScore(),
                score.getRiskLevel(),
                score.getInspectionPoints(),
                score.getLabPoints(),
                score.getRecallPoints(),
                score.getReviewPoints(),
                score.getReviewCount(),
                score.getExplanation(),
                RAW_COMPLAINT_FAIRNESS_NOTE,
                score.getRecalculatedAt());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
