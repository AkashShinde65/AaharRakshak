package com.aaharrakshak.investigation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.aaharrakshak.complaint.ComplaintStatus;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class ComplaintWorkflowValidatorTest {

    private final ComplaintWorkflowValidator validator = new ComplaintWorkflowValidator();

    @Test
    void allowsExpectedInvestigationTransitions() {
        assertThatNoException().isThrownBy(() ->
                validator.assertTransition(ComplaintStatus.SUBMITTED, ComplaintStatus.VERIFIED));
        assertThatNoException().isThrownBy(() ->
                validator.assertTransition(ComplaintStatus.VERIFIED, ComplaintStatus.ASSIGNED));
        assertThatNoException().isThrownBy(() ->
                validator.assertTransition(ComplaintStatus.LAB_TESTING, ComplaintStatus.REPORT_PUBLISHED));
    }

    @Test
    void rejectsSkippedWorkflowTransitions() {
        assertThatThrownBy(() ->
                validator.assertTransition(ComplaintStatus.SUBMITTED, ComplaintStatus.REPORT_PUBLISHED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid complaint status transition");
    }
}
