package com.aaharrakshak.investigation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "lab_test_results")
public class LabTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lab_report_id")
    private LabReport labReport;

    @Column(nullable = false, length = 120)
    private String parameterName;

    @Column(length = 120)
    private String testMethod;

    @Column(length = 80)
    private String permissibleLimit;

    @Column(nullable = false, length = 80)
    private String resultValue;

    @Column(length = 40)
    private String unit;

    @Column(nullable = false)
    private Boolean compliant;

    @Column(length = 500)
    private String remarks;

    protected LabTestResult() {
    }

    public LabTestResult(
            LabReport labReport,
            String parameterName,
            String testMethod,
            String permissibleLimit,
            String resultValue,
            String unit,
            Boolean compliant,
            String remarks) {
        this.labReport = labReport;
        this.parameterName = parameterName;
        this.testMethod = testMethod;
        this.permissibleLimit = permissibleLimit;
        this.resultValue = resultValue;
        this.unit = unit;
        this.compliant = compliant;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public String getPermissibleLimit() {
        return permissibleLimit;
    }

    public String getResultValue() {
        return resultValue;
    }

    public String getUnit() {
        return unit;
    }

    public Boolean getCompliant() {
        return compliant;
    }

    public String getRemarks() {
        return remarks;
    }
}
