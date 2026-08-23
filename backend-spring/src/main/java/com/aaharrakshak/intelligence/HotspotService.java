package com.aaharrakshak.intelligence;

import com.aaharrakshak.audit.AuditService;
import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.complaint.ComplaintRepository;
import com.aaharrakshak.complaint.ComplaintStatus;
import com.aaharrakshak.intelligence.dto.HotspotResponse;
import com.aaharrakshak.security.AuthenticatedUser;
import com.aaharrakshak.user.RoleName;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HotspotService {

    private static final Set<ComplaintStatus> HOTSPOT_STATUSES = EnumSet.of(
            ComplaintStatus.SUBMITTED,
            ComplaintStatus.VERIFIED,
            ComplaintStatus.ASSIGNED,
            ComplaintStatus.INSPECTION_SCHEDULED,
            ComplaintStatus.SAMPLE_COLLECTED,
            ComplaintStatus.LAB_TESTING,
            ComplaintStatus.REPORT_PUBLISHED,
            ComplaintStatus.ACTION_TAKEN,
            ComplaintStatus.ESCALATED);
    private static final String PRIVACY_NOTE = "Map coordinates are district-level hotspot centers. Individual citizen locations are not exposed publicly.";

    private final ComplaintRepository complaintRepository;
    private final ComplaintHotspotRepository hotspotRepository;
    private final ComplaintHotspotMemberRepository memberRepository;
    private final GeoDistanceCalculator distanceCalculator;
    private final AuditService auditService;
    private final double radiusKm;
    private final long windowHours;
    private final int criticalThreshold;

    public HotspotService(
            ComplaintRepository complaintRepository,
            ComplaintHotspotRepository hotspotRepository,
            ComplaintHotspotMemberRepository memberRepository,
            GeoDistanceCalculator distanceCalculator,
            AuditService auditService,
            @Value("${app.intelligence.hotspot-radius-km}") double radiusKm,
            @Value("${app.intelligence.hotspot-window-hours}") long windowHours,
            @Value("${app.intelligence.hotspot-critical-threshold}") int criticalThreshold) {
        this.complaintRepository = complaintRepository;
        this.hotspotRepository = hotspotRepository;
        this.memberRepository = memberRepository;
        this.distanceCalculator = distanceCalculator;
        this.auditService = auditService;
        this.radiusKm = radiusKm;
        this.windowHours = windowHours;
        this.criticalThreshold = criticalThreshold;
    }

    @Transactional
    public List<HotspotResponse> detectAndList(AuthenticatedUser principal, String district) {
        requireOfficial(principal);
        Instant windowEnd = Instant.now();
        Instant windowStart = windowEnd.minusSeconds(windowHours * 3600L);
        List<Complaint> candidates = complaintRepository
                .findByStatusInAndGpsConsentTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndSubmittedAtAfter(
                        HOTSPOT_STATUSES,
                        windowStart);
        Map<String, List<Complaint>> grouped = candidates.stream()
                .filter(complaint -> district == null || equalsIgnoreCase(district, complaint.getDistrict()))
                .collect(java.util.stream.Collectors.groupingBy(
                        this::relatedKey,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()));
        grouped.values().forEach(group -> saveIfHotspot(group, windowStart, windowEnd, principal));
        return list(principal, district);
    }

    @Transactional(readOnly = true)
    public List<HotspotResponse> list(AuthenticatedUser principal, String district) {
        requireOfficial(principal);
        List<ComplaintHotspot> hotspots = district == null || district.isBlank()
                ? hotspotRepository.findByActiveTrueOrderByDetectedAtDesc()
                : hotspotRepository.findByDistrictIgnoreCaseAndActiveTrueOrderByDetectedAtDesc(district);
        return hotspots.stream()
                .map(this::toResponse)
                .toList();
    }

    private void saveIfHotspot(List<Complaint> group, Instant windowStart, Instant windowEnd, AuthenticatedUser principal) {
        if (group.size() < 3) {
            return;
        }
        Complaint center = group.stream()
                .max(Comparator.comparing(Complaint::getRiskScore))
                .orElse(group.get(0));
        List<Complaint> nearby = group.stream()
                .filter(item -> distanceCalculator.distanceKm(
                        center.getLatitude(),
                        center.getLongitude(),
                        item.getLatitude(),
                        item.getLongitude()) <= radiusKm)
                .toList();
        if (nearby.size() < 3) {
            return;
        }
        String district = safeDistrict(center);
        String key = hotspotKey(district, relatedKey(center), windowStart);
        if (hotspotRepository.existsByHotspotKey(key)) {
            return;
        }
        ComplaintHotspot hotspot = hotspotRepository.save(new ComplaintHotspot(
                key,
                district,
                relatedKey(center),
                productOrVendor(center),
                riskLevel(nearby.size()),
                nearby.size(),
                BigDecimal.valueOf(radiusKm).setScale(3, RoundingMode.HALF_UP),
                averageLatitude(nearby),
                averageLongitude(nearby),
                windowStart,
                windowEnd));
        nearby.forEach(complaint -> memberRepository.save(new ComplaintHotspotMember(hotspot, complaint)));
        auditService.record(principal.getUser(), "HOTSPOT_DETECTED", "HOTSPOT", hotspot.getHotspotKey(),
                nearby.size() + " related complaints in " + district);
    }

    private HotspotResponse toResponse(ComplaintHotspot hotspot) {
        List<String> tickets = memberRepository.findByHotspotId(hotspot.getId()).stream()
                .map(member -> member.getComplaint().getTicketNumber())
                .toList();
        return new HotspotResponse(
                hotspot.getId(),
                hotspot.getHotspotKey(),
                hotspot.getDistrict(),
                hotspot.getRelatedKey(),
                hotspot.getProductOrVendor(),
                hotspot.getRiskLevel(),
                hotspot.getComplaintCount(),
                hotspot.getRadiusKm(),
                hotspot.getCenterLatitude(),
                hotspot.getCenterLongitude(),
                hotspot.getWindowStart(),
                hotspot.getWindowEnd(),
                hotspot.getDetectedAt(),
                tickets,
                PRIVACY_NOTE);
    }

    private RiskLevel riskLevel(int count) {
        if (count >= criticalThreshold) {
            return RiskLevel.CRITICAL;
        }
        if (count >= Math.max(7, criticalThreshold - 3)) {
            return RiskLevel.HIGH;
        }
        if (count >= 3) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }

    private BigDecimal averageLatitude(List<Complaint> complaints) {
        return BigDecimal.valueOf(complaints.stream()
                        .mapToDouble(item -> item.getLatitude().doubleValue())
                        .average()
                        .orElse(0))
                .setScale(7, RoundingMode.HALF_UP);
    }

    private BigDecimal averageLongitude(List<Complaint> complaints) {
        return BigDecimal.valueOf(complaints.stream()
                        .mapToDouble(item -> item.getLongitude().doubleValue())
                        .average()
                        .orElse(0))
                .setScale(7, RoundingMode.HALF_UP);
    }

    private String hotspotKey(String district, String relatedKey, Instant windowStart) {
        return ("HOT-" + district + "-" + relatedKey + "-" + windowStart.toString().substring(0, 13))
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-");
    }

    private String relatedKey(Complaint complaint) {
        if (complaint.getBatch() != null) {
            return "batch:" + complaint.getBatch().getBatchNumber();
        }
        if (complaint.getProduct() != null) {
            return "product:" + complaint.getProduct().getName();
        }
        if (complaint.getVendorName() != null && !complaint.getVendorName().isBlank()) {
            return "vendor:" + complaint.getVendorName();
        }
        return "manual:" + productOrVendor(complaint);
    }

    private String productOrVendor(Complaint complaint) {
        if (complaint.getProduct() != null) {
            return complaint.getProduct().getName();
        }
        if (complaint.getConfirmedProductName() != null) {
            return complaint.getConfirmedProductName();
        }
        if (complaint.getVendorName() != null) {
            return complaint.getVendorName();
        }
        return "Unknown product/vendor";
    }

    private String safeDistrict(Complaint complaint) {
        return complaint.getDistrict() == null || complaint.getDistrict().isBlank() ? "Unspecified District" : complaint.getDistrict();
    }

    private boolean equalsIgnoreCase(String expected, String actual) {
        return actual != null && actual.equalsIgnoreCase(expected);
    }

    private void requireOfficial(AuthenticatedUser principal) {
        if (principal.getRoles().contains(RoleName.FOOD_INSPECTOR)
                || principal.getRoles().contains(RoleName.DISTRICT_ESCALATION_OFFICER)
                || principal.getRoles().contains(RoleName.CENTRAL_ADMINISTRATOR)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Official role required");
    }
}
