package com.aaharrakshak.intelligence;

import com.aaharrakshak.intelligence.dto.TrustScoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/trust")
@Tag(name = "Public Trust Score")
public class PublicTrustController {

    private final TrustScoreService trustScoreService;

    public PublicTrustController(TrustScoreService trustScoreService) {
        this.trustScoreService = trustScoreService;
    }

    @GetMapping("/companies/{companyId}")
    @Operation(summary = "Public vendor Trust Score without using raw complaints as proof")
    TrustScoreResponse companyTrustScore(@PathVariable Long companyId) {
        return trustScoreService.trustScore(companyId);
    }
}
