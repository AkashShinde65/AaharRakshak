package com.aaharrakshak.company;

import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.company.dto.CompanyProfileResponse;
import com.aaharrakshak.company.dto.LicenceRejectionRequest;
import com.aaharrakshak.company.dto.LicenceResponse;
import com.aaharrakshak.company.dto.LicenceSubmissionRequest;
import com.aaharrakshak.company.dto.UpdateCompanyProfileRequest;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.storage.FileStorageService;
import com.aaharrakshak.storage.StoredFileMetadata;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final LicenceRepository licenceRepository;
    private final LicenceRegistryAdapter licenceRegistryAdapter;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;

    public CompanyService(
            CompanyRepository companyRepository,
            LicenceRepository licenceRepository,
            LicenceRegistryAdapter licenceRegistryAdapter,
            FileStorageService fileStorageService,
            AuditService auditService) {
        this.companyRepository = companyRepository;
        this.licenceRepository = licenceRepository;
        this.licenceRegistryAdapter = licenceRegistryAdapter;
        this.fileStorageService = fileStorageService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public CompanyProfileResponse myProfile(AuthenticatedUser principal) {
        return toProfileResponse(loadOwnedCompany(principal));
    }

    @Transactional
    public CompanyProfileResponse updateProfile(AuthenticatedUser principal, UpdateCompanyProfileRequest request) {
        Company company = loadOwnedCompany(principal);
        company.updateProfile(
                request.legalName(),
                request.tradeName(),
                request.gstin(),
                request.registeredAddress(),
                request.contactEmail(),
                request.contactMobile(),
                request.websiteUrl());
        auditService.record(principal.getUser(), "COMPANY_PROFILE_UPDATED", "COMPANY", company.getId().toString(),
                "Company updated profile details");
        return toProfileResponse(company);
    }

    @Transactional
    public LicenceResponse submitLicence(AuthenticatedUser principal, LicenceSubmissionRequest request) {
        Company company = loadOwnedCompany(principal);
        if (licenceRepository.existsByLicenceNumber(request.licenceNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Licence number is already submitted");
        }
        StoredFileMetadata labelImage = storeMetadata("licence-labels", request.licenceLabelImage());
        Licence licence = licenceRepository.save(new Licence(
                company,
                request.licenceNumber(),
                request.issuingAuthority(),
                request.validFrom(),
                request.validTo(),
                labelImage));
        auditService.record(principal.getUser(), "LICENCE_SUBMITTED", "LICENCE", licence.getId().toString(),
                "Company submitted FSSAI licence for review");
        return toLicenceResponse(licence);
    }

    @Transactional(readOnly = true)
    public List<LicenceResponse> myLicences(AuthenticatedUser principal) {
        return licenceRepository.findByCompanyOwnerUserIdOrderByIdDesc(principal.getUserId()).stream()
                .map(this::toLicenceResponse)
                .toList();
    }

    @Transactional
    public LicenceResponse verifyLicence(Long licenceId, AuthenticatedUser principal) {
        Licence licence = licenceRepository.findById(licenceId).orElseThrow();
        RegistryLicenceDetails registryDetails = licenceRegistryAdapter.lookup(licence.getLicenceNumber());
        if (registryDetails.verified()) {
            licence.verify(principal.getUser(), registryDetails);
            auditService.record(principal.getUser(), "LICENCE_VERIFIED", "LICENCE", licence.getId().toString(),
                    registryDetails.referenceToken());
        } else {
            licence.reject(
                    principal.getUser(),
                    registryDetails.message(),
                    registryDetails.status(),
                    registryDetails.referenceToken());
            auditService.record(principal.getUser(), "LICENCE_REJECTED_BY_REGISTRY", "LICENCE",
                    licence.getId().toString(), registryDetails.status());
        }
        return toLicenceResponse(licence);
    }

    @Transactional
    public LicenceResponse rejectLicence(Long licenceId, AuthenticatedUser principal, LicenceRejectionRequest request) {
        Licence licence = licenceRepository.findById(licenceId).orElseThrow();
        licence.reject(principal.getUser(), request.reason(), "MANUAL_REJECTION", null);
        auditService.record(principal.getUser(), "LICENCE_REJECTED", "LICENCE", licence.getId().toString(),
                request.reason());
        return toLicenceResponse(licence);
    }

    @Transactional
    public LicenceResponse expireLicence(Long licenceId, AuthenticatedUser principal) {
        Licence licence = licenceRepository.findById(licenceId).orElseThrow();
        licence.expire(principal.getUser());
        auditService.record(principal.getUser(), "LICENCE_EXPIRED", "LICENCE", licence.getId().toString(),
                "Official marked licence expired");
        return toLicenceResponse(licence);
    }

    @Transactional(readOnly = true)
    public Company loadOwnedCompany(AuthenticatedUser principal) {
        return companyRepository.findByOwnerUserId(principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company profile not found"));
    }

    public CompanyProfileResponse toProfileResponse(Company company) {
        return new CompanyProfileResponse(
                company.getId(),
                company.getLegalName(),
                company.getTradeName(),
                company.getGstin(),
                company.getRegisteredAddress(),
                company.getContactEmail(),
                company.getContactMobile(),
                company.getWebsiteUrl(),
                company.getStatus());
    }

    public LicenceResponse toLicenceResponse(Licence licence) {
        return new LicenceResponse(
                licence.getId(),
                licence.getCompany().getId(),
                licence.getLicenceNumber(),
                licence.getIssuingAuthority(),
                licence.getValidFrom(),
                licence.getValidTo(),
                licence.getStatus(),
                licence.getRegistryStatus(),
                licence.getRegistryReferenceToken(),
                licence.getRejectionReason(),
                licence.getLabelImageObjectKey(),
                licence.getLabelImageFileName(),
                licence.getLabelImageContentType(),
                licence.getLabelImageSizeBytes());
    }

    private StoredFileMetadata storeMetadata(String bucket, com.aaharrakshak.storage.FileMetadataRequest request) {
        try {
            return fileStorageService.storeMetadata(bucket, request);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
