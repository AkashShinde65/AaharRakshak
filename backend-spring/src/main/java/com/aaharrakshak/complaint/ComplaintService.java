package com.aaharrakshak.complaint;

import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.BatchRepository;
import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.catalog.ProductBarcodeRepository;
import com.aaharrakshak.catalog.ProductRepository;
import com.aaharrakshak.complaint.dto.ComplaintDraftRequest;
import com.aaharrakshak.complaint.dto.ComplaintResponse;
import com.aaharrakshak.complaint.dto.ComplaintStatusHistoryResponse;
import com.aaharrakshak.complaint.dto.EvidenceMetadataRequest;
import com.aaharrakshak.complaint.dto.EvidenceResponse;
import com.aaharrakshak.complaint.dto.GpsLocationRequest;
import com.aaharrakshak.investigation.Assignment;
import com.aaharrakshak.investigation.AssignmentRepository;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.storage.FileMetadataRequest;
import com.aaharrakshak.storage.FileStorageService;
import com.aaharrakshak.storage.StoredFileMetadata;
import com.aaharrakshak.user.RoleName;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComplaintService {

    private static final DateTimeFormatter TICKET_DATE = DateTimeFormatter.ofPattern("yyyyMMdd")
            .withZone(ZoneOffset.UTC);
    private static final String IMAGE_SAFETY_NOTE = FoodScanService.IMAGE_SAFETY_NOTE;

    private final ComplaintRepository complaintRepository;
    private final EvidenceRepository evidenceRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;
    private final ProductRepository productRepository;
    private final ProductBarcodeRepository productBarcodeRepository;
    private final BatchRepository batchRepository;
    private final AssignmentRepository assignmentRepository;
    private final FileStorageService fileStorageService;
    private final EvidenceFileValidator evidenceFileValidator;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();

    public ComplaintService(
            ComplaintRepository complaintRepository,
            EvidenceRepository evidenceRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository,
            ProductRepository productRepository,
            ProductBarcodeRepository productBarcodeRepository,
            BatchRepository batchRepository,
            AssignmentRepository assignmentRepository,
            FileStorageService fileStorageService,
            EvidenceFileValidator evidenceFileValidator,
            AuditService auditService) {
        this.complaintRepository = complaintRepository;
        this.evidenceRepository = evidenceRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.productRepository = productRepository;
        this.productBarcodeRepository = productBarcodeRepository;
        this.batchRepository = batchRepository;
        this.assignmentRepository = assignmentRepository;
        this.fileStorageService = fileStorageService;
        this.evidenceFileValidator = evidenceFileValidator;
        this.auditService = auditService;
    }

    @Transactional
    public ComplaintResponse createDraft(AuthenticatedUser principal, ComplaintDraftRequest request) {
        Complaint complaint = new Complaint(
                principal.getUser(),
                request.complaintType(),
                request.category(),
                generateTicketNumber());
        applyDraft(complaint, request);
        Complaint saved = complaintRepository.save(complaint);
        statusHistoryRepository.save(new ComplaintStatusHistory(
                saved,
                ComplaintStatus.DRAFT,
                principal.getUser(),
                "Citizen created complaint draft"));
        addEvidence(saved, request.evidence());
        auditService.record(principal.getUser(), "COMPLAINT_DRAFT_CREATED", "COMPLAINT", saved.getTicketNumber(),
                saved.getComplaintType().name());
        return toResponse(saved);
    }

    @Transactional
    public ComplaintResponse updateDraft(AuthenticatedUser principal, Long complaintId, ComplaintDraftRequest request) {
        Complaint complaint = loadOwnedComplaintById(principal, complaintId);
        ensureDraft(complaint);
        applyDraft(complaint, request);
        auditService.record(principal.getUser(), "COMPLAINT_DRAFT_UPDATED", "COMPLAINT", complaint.getTicketNumber(),
                "Citizen updated complaint draft");
        return toResponse(complaint);
    }

    @Transactional
    public ComplaintResponse addEvidence(AuthenticatedUser principal, Long complaintId, EvidenceMetadataRequest request) {
        Complaint complaint = loadOwnedComplaintById(principal, complaintId);
        ensureDraft(complaint);
        Evidence evidence = createEvidence(complaint, request);
        evidenceRepository.save(evidence);
        auditService.record(principal.getUser(), "COMPLAINT_EVIDENCE_ADDED", "COMPLAINT", complaint.getTicketNumber(),
                request.type().name());
        return toResponse(complaint);
    }

    @Transactional
    public ComplaintResponse submit(AuthenticatedUser principal, Long complaintId) {
        Complaint complaint = loadOwnedComplaintById(principal, complaintId);
        ensureDraft(complaint);
        validateReadyForSubmission(complaint);
        complaint.submit();
        statusHistoryRepository.save(new ComplaintStatusHistory(
                complaint,
                ComplaintStatus.SUBMITTED,
                principal.getUser(),
                "Citizen submitted complaint"));
        auditService.record(principal.getUser(), "COMPLAINT_SUBMITTED", "COMPLAINT", complaint.getTicketNumber(),
                complaint.getComplaintType().name());
        return toResponse(complaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> myComplaints(AuthenticatedUser principal) {
        return complaintRepository.findByCitizenIdOrderByCreatedAtDesc(principal.getUserId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplaintResponse myComplaint(AuthenticatedUser principal, String ticketNumber) {
        return toResponse(complaintRepository.findByTicketNumberAndCitizenId(ticketNumber, principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found")));
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> assignedComplaints(AuthenticatedUser principal) {
        return assignmentRepository.findByAssignedToIdOrderByAssignedAtDesc(principal.getUserId()).stream()
                .map(Assignment::getComplaint)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplaintResponse officialComplaint(AuthenticatedUser principal, String ticketNumber) {
        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        if (!canViewOfficialComplaint(principal, complaint)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Official is not assigned to this complaint");
        }
        return toResponse(complaint);
    }

    private void applyDraft(Complaint complaint, ComplaintDraftRequest request) {
        validateComplaintType(request);
        Product product = resolveProduct(request);
        Batch batch = resolveBatch(request, product);
        complaint.linkCatalogue(product, batch);
        GpsLocationRequest location = request.location();
        complaint.applyDraftDetails(
                blankToNull(request.scannedBarcode()),
                blankToNull(request.detectedProductName()),
                blankToNull(request.detectedCompanyName()),
                blankToNull(request.detectedFssaiLicenceNumber()),
                blankToNull(request.detectedBatchNumber()),
                request.detectedExpiryDate(),
                blankToNull(request.confirmedProductName()),
                blankToNull(request.confirmedCompanyName()),
                blankToNull(request.confirmedFssaiLicenceNumber()),
                blankToNull(request.confirmedBatchNumber()),
                request.confirmedExpiryDate(),
                blankToNull(request.vendorName()),
                blankToNull(request.vendorAddress()),
                blankToNull(request.description()),
                location != null && location.consentAccepted(),
                location == null ? null : location.latitude(),
                location == null ? null : location.longitude(),
                location == null ? null : blankToNull(location.address()));
    }

    private void validateComplaintType(ComplaintDraftRequest request) {
        if (request.complaintType() == ComplaintType.PREPARED_DISH && request.productId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prepared dish complaints should use vendor details");
        }
    }

    private Product resolveProduct(ComplaintDraftRequest request) {
        if (request.productId() != null) {
            return productRepository.findById(request.productId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
        }
        if (request.scannedBarcode() != null && !request.scannedBarcode().isBlank()) {
            return productBarcodeRepository.findByBarcode(request.scannedBarcode())
                    .map(barcode -> barcode.getProduct())
                    .orElse(null);
        }
        return null;
    }

    private Batch resolveBatch(ComplaintDraftRequest request, Product product) {
        if (request.batchId() == null) {
            return null;
        }
        Batch batch = batchRepository.findById(request.batchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch not found"));
        if (product == null || !batch.getProduct().getId().equals(product.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch does not belong to selected product");
        }
        return batch;
    }

    private void addEvidence(Complaint complaint, List<EvidenceMetadataRequest> evidenceRequests) {
        if (evidenceRequests == null) {
            return;
        }
        evidenceRequests.forEach(request -> evidenceRepository.save(createEvidence(complaint, request)));
    }

    private Evidence createEvidence(Complaint complaint, EvidenceMetadataRequest request) {
        evidenceFileValidator.validate(request);
        StoredFileMetadata storedFile = fileStorageService.storeMetadata(
                "complaint-evidence",
                new FileMetadataRequest(
                        request.objectKey(),
                        request.originalFileName(),
                        request.contentType().toLowerCase(Locale.ROOT),
                        request.sizeBytes()));
        return new Evidence(
                complaint,
                request.type(),
                storedFile,
                request.checksumSha256().toLowerCase(Locale.ROOT),
                request.capturedAt());
    }

    private void validateReadyForSubmission(Complaint complaint) {
        if (!Boolean.TRUE.equals(complaint.getGpsConsent())
                || complaint.getLatitude() == null
                || complaint.getLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GPS location with consent is required");
        }
        List<Evidence> evidence = evidenceRepository.findByComplaintIdOrderByUploadedAtAsc(complaint.getId());
        if (evidence.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one evidence file is required");
        }
        if (complaint.getComplaintType() == ComplaintType.PREPARED_DISH) {
            boolean hasDishImage = evidence.stream()
                    .anyMatch(item -> item.getType() == EvidenceType.DISH_IMAGE || item.getType() == EvidenceType.FOOD_PHOTO);
            boolean hasVendorImage = evidence.stream()
                    .anyMatch(item -> item.getType() == EvidenceType.VENDOR_IMAGE
                            || item.getType() == EvidenceType.SHOP_SIGNBOARD_PHOTO);
            if (!hasDishImage || !hasVendorImage) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Prepared dish complaints require dish and vendor images");
            }
        }
    }

    private Complaint loadOwnedComplaintById(AuthenticatedUser principal, Long complaintId) {
        return complaintRepository.findByIdAndCitizenId(complaintId, principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
    }

    private void ensureDraft(Complaint complaint) {
        if (complaint.getStatus() != ComplaintStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only draft complaints can be changed");
        }
    }

    private boolean canViewOfficialComplaint(AuthenticatedUser principal, Complaint complaint) {
        Set<RoleName> roles = principal.getRoles();
        if (roles.contains(RoleName.CENTRAL_ADMINISTRATOR)
                || roles.contains(RoleName.DISTRICT_ESCALATION_OFFICER)) {
            return true;
        }
        return assignmentRepository.existsByComplaintIdAndAssignedToId(complaint.getId(), principal.getUserId());
    }

    private ComplaintResponse toResponse(Complaint complaint) {
        Product product = complaint.getProduct();
        Batch batch = complaint.getBatch();
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTicketNumber(),
                complaint.getComplaintType(),
                complaint.getCategory(),
                complaint.getStatus(),
                complaint.getDescription(),
                complaint.getScannedBarcode(),
                product == null ? null : product.getId(),
                product == null ? null : product.getName(),
                complaint.getCompany() == null ? null : complaint.getCompany().getId(),
                complaint.getCompany() == null ? complaint.getConfirmedCompanyName() : complaint.getCompany().getLegalName(),
                batch == null ? null : batch.getId(),
                batch == null ? complaint.getConfirmedBatchNumber() : batch.getBatchNumber(),
                complaint.getDetectedProductName(),
                complaint.getDetectedCompanyName(),
                complaint.getDetectedFssaiLicenceNumber(),
                complaint.getDetectedBatchNumber(),
                complaint.getDetectedExpiryDate(),
                complaint.getConfirmedProductName(),
                complaint.getConfirmedCompanyName(),
                complaint.getConfirmedFssaiLicenceNumber(),
                complaint.getConfirmedBatchNumber(),
                complaint.getConfirmedExpiryDate(),
                complaint.getVendorName(),
                complaint.getVendorAddress(),
                complaint.getGpsConsent(),
                complaint.getLatitude(),
                complaint.getLongitude(),
                complaint.getLocationText(),
                complaint.getRiskScore(),
                complaint.getCreatedAt(),
                complaint.getSubmittedAt(),
                evidenceRepository.findByComplaintIdOrderByUploadedAtAsc(complaint.getId()).stream()
                        .map(this::toEvidenceResponse)
                        .toList(),
                statusHistoryRepository.findByComplaintIdOrderByCreatedAtAsc(complaint.getId()).stream()
                        .map(this::toStatusHistoryResponse)
                        .toList(),
                IMAGE_SAFETY_NOTE);
    }

    private EvidenceResponse toEvidenceResponse(Evidence evidence) {
        return new EvidenceResponse(
                evidence.getId(),
                evidence.getType(),
                evidence.getObjectKey(),
                evidence.getOriginalFileName(),
                evidence.getContentType(),
                evidence.getFileSizeBytes(),
                evidence.getChecksumSha256(),
                evidence.getCapturedAt(),
                evidence.getUploadedAt());
    }

    private ComplaintStatusHistoryResponse toStatusHistoryResponse(ComplaintStatusHistory history) {
        return new ComplaintStatusHistoryResponse(history.getStatus(), history.getNotes(), history.getCreatedAt());
    }

    private String generateTicketNumber() {
        String date = TICKET_DATE.format(Instant.now());
        for (int attempt = 0; attempt < 10; attempt++) {
            String ticketNumber = "ARK-" + date + "-" + randomDigits();
            if (!complaintRepository.existsByTicketNumber(ticketNumber)) {
                return ticketNumber;
            }
        }
        throw new IllegalStateException("Could not generate unique complaint ticket number");
    }

    private String randomDigits() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
