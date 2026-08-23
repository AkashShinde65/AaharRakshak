using System.ComponentModel.DataAnnotations;

namespace official_web_dotnet.Models;

public sealed class LoginViewModel
{
    [Required]
    [Display(Name = "Email or mobile")]
    public string Identifier { get; set; } = string.Empty;

    [Required]
    [DataType(DataType.Password)]
    public string Password { get; set; } = string.Empty;
}

public sealed class AuthResponseViewModel
{
    public string AccessToken { get; set; } = string.Empty;
    public string TokenType { get; set; } = "Bearer";
    public string FullName { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public List<string> Roles { get; set; } = [];
}

public sealed class OfficialDashboardItemViewModel
{
    public long ReportId { get; set; }
    public string ReportNumber { get; set; } = string.Empty;
    public string TicketNumber { get; set; } = string.Empty;
    public string ComplaintStatus { get; set; } = string.Empty;
    public string Outcome { get; set; } = string.Empty;
    public string CompanyName { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public string BatchNumber { get; set; } = string.Empty;
    public string District { get; set; } = string.Empty;
    public string? NoticeNumber { get; set; }
    public string? NoticeStatus { get; set; }
    public string? ActionType { get; set; }
    public DateTimeOffset? ResponseDueAt { get; set; }
    public DateTimeOffset? PublishedAt { get; set; }
}

public sealed class OfficialHotspotPageViewModel
{
    [Required, StringLength(80)]
    public string District { get; set; } = "Pune";

    public List<HotspotViewModel> Hotspots { get; set; } = [];
}

public sealed class HotspotViewModel
{
    public long HotspotId { get; set; }
    public string HotspotKey { get; set; } = string.Empty;
    public string District { get; set; } = string.Empty;
    public string RelatedKey { get; set; } = string.Empty;
    public string ProductOrVendor { get; set; } = string.Empty;
    public string RiskLevel { get; set; } = string.Empty;
    public int ComplaintCount { get; set; }
    public decimal RadiusKm { get; set; }
    public decimal CenterLatitude { get; set; }
    public decimal CenterLongitude { get; set; }
    public DateTimeOffset WindowStart { get; set; }
    public DateTimeOffset WindowEnd { get; set; }
    public DateTimeOffset DetectedAt { get; set; }
    public List<string> ComplaintNumbers { get; set; } = [];
    public string PrivacyNote { get; set; } = string.Empty;
}

public sealed class SlaEscalationViewModel
{
    public long EscalationId { get; set; }
    public string TicketNumber { get; set; } = string.Empty;
    public string District { get; set; } = string.Empty;
    public int RiskScore { get; set; }
    public string PreviousStatus { get; set; } = string.Empty;
    public string? AssignedInspectorName { get; set; }
    public string EscalatedToName { get; set; } = string.Empty;
    public string Reason { get; set; } = string.Empty;
    public DateTimeOffset EscalatedAt { get; set; }
}

public sealed class AlertOutboxViewModel
{
    public long AlertId { get; set; }
    public long UserId { get; set; }
    public string EventType { get; set; } = string.Empty;
    public string Channel { get; set; } = string.Empty;
    public string Subject { get; set; } = string.Empty;
    public string Body { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
    public int RetryCount { get; set; }
    public DateTimeOffset CreatedAt { get; set; }
}

public sealed class MockExternalEventViewModel
{
    public long EventId { get; set; }
    public string EventType { get; set; } = string.Empty;
    public string TargetType { get; set; } = string.Empty;
    public string TargetId { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
    public DateTimeOffset CreatedAt { get; set; }
    public string SafetyNote { get; set; } = string.Empty;
}

public sealed class OfficialAlertPageViewModel
{
    public List<AlertOutboxViewModel> Outbox { get; set; } = [];
    public List<MockExternalEventViewModel> MockExternalEvents { get; set; } = [];
}

public sealed class IssueNoticeViewModel
{
    [Required]
    public long ReportId { get; set; }

    [Required, StringLength(180)]
    public string Subject { get; set; } = string.Empty;

    [Required, StringLength(1000)]
    public string Reason { get; set; } = string.Empty;

    [Required, StringLength(1200)]
    [Display(Name = "Evidence summary")]
    public string EvidenceSummary { get; set; } = string.Empty;

    [Required]
    [Display(Name = "Response due at")]
    public DateTimeOffset ResponseDueAt { get; set; } = DateTimeOffset.UtcNow.AddDays(14);
}

public sealed class ReviewNoticeViewModel
{
    [Required, StringLength(1000)]
    public string Notes { get; set; } = string.Empty;
}

public sealed class DecisionViewModel
{
    [Required]
    [Display(Name = "Action type")]
    public string ActionType { get; set; } = "WARNING";

    [Required, StringLength(1000)]
    public string Reason { get; set; } = string.Empty;

    [Required, StringLength(1200)]
    [Display(Name = "Evidence summary")]
    public string EvidenceSummary { get; set; } = string.Empty;

    [Required]
    [DataType(DataType.Date)]
    [Display(Name = "Effective date")]
    public DateOnly EffectiveDate { get; set; } = DateOnly.FromDateTime(DateTime.UtcNow.Date.AddDays(1));

    [Required, StringLength(1000)]
    [Display(Name = "Public summary")]
    public string PublicSummary { get; set; } = string.Empty;
}

public sealed class ShowCauseNoticeViewModel
{
    public long NoticeId { get; set; }
    public string NoticeNumber { get; set; } = string.Empty;
    public string TicketNumber { get; set; } = string.Empty;
    public string ReportNumber { get; set; } = string.Empty;
    public string Outcome { get; set; } = string.Empty;
    public long CompanyId { get; set; }
    public string CompanyName { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public string BatchNumber { get; set; } = string.Empty;
    public string Subject { get; set; } = string.Empty;
    public string Reason { get; set; } = string.Empty;
    public string EvidenceSummary { get; set; } = string.Empty;
    public DateTimeOffset ResponseDueAt { get; set; }
    public string Status { get; set; } = string.Empty;
    public DateTimeOffset IssuedAt { get; set; }
    public List<CompanyNoticeResponseViewModel> Responses { get; set; } = [];
    public AdministrativeActionViewModel? Action { get; set; }
    public List<AdministrativeActionHistoryViewModel> History { get; set; } = [];
}

public sealed class CompanyNoticeResponseViewModel
{
    public long ResponseId { get; set; }
    public string ResponseText { get; set; } = string.Empty;
    public CompanyNoticeDocumentViewModel Document { get; set; } = new();
    public DateTimeOffset SubmittedAt { get; set; }
}

public sealed class CompanyNoticeDocumentViewModel
{
    public string ObjectKey { get; set; } = string.Empty;
    public string OriginalFileName { get; set; } = string.Empty;
    public string ContentType { get; set; } = string.Empty;
    public long SizeBytes { get; set; }
    public string ChecksumSha256 { get; set; } = string.Empty;
}

public sealed class CompanyResponseFormViewModel
{
    [Required]
    public string NoticeNumber { get; set; } = string.Empty;

    [Required, StringLength(3000)]
    [Display(Name = "Response text")]
    public string ResponseText { get; set; } = string.Empty;

    [Required, StringLength(500)]
    [Display(Name = "Object key")]
    public string ObjectKey { get; set; } = string.Empty;

    [Required, StringLength(180)]
    [Display(Name = "File name")]
    public string OriginalFileName { get; set; } = string.Empty;

    [Required, StringLength(120)]
    [Display(Name = "Content type")]
    public string ContentType { get; set; } = "application/pdf";

    [Range(1, 10485760)]
    [Display(Name = "Size bytes")]
    public long SizeBytes { get; set; }

    [Required, RegularExpression("[a-fA-F0-9]{64}")]
    [Display(Name = "SHA-256 checksum")]
    public string ChecksumSha256 { get; set; } = string.Empty;
}

public sealed class AdministrativeActionViewModel
{
    public long ActionId { get; set; }
    public string ActionNumber { get; set; } = string.Empty;
    public string TicketNumber { get; set; } = string.Empty;
    public string ReportNumber { get; set; } = string.Empty;
    public string CompanyName { get; set; } = string.Empty;
    public string ActionType { get; set; } = string.Empty;
    public string Reason { get; set; } = string.Empty;
    public string EvidenceSummary { get; set; } = string.Empty;
    public DateOnly EffectiveDate { get; set; }
    public long ApprovingOfficialId { get; set; }
    public string ApprovingOfficialName { get; set; } = string.Empty;
    public bool Simulated { get; set; }
    public string PublicSummary { get; set; } = string.Empty;
    public DateTimeOffset DecidedAt { get; set; }
}

public sealed class AdministrativeActionHistoryViewModel
{
    public string EventType { get; set; } = string.Empty;
    public string Notes { get; set; } = string.Empty;
    public string ActorName { get; set; } = string.Empty;
    public DateTimeOffset CreatedAt { get; set; }
}

public sealed class PublicSearchViewModel
{
    public string? ComplaintNumber { get; set; }
    public string? Company { get; set; }
    public string? Product { get; set; }
    public string? Batch { get; set; }
    public string? Location { get; set; }
    public List<PublicSearchResultViewModel> Results { get; set; } = [];
}

public sealed class PublicSearchResultViewModel
{
    public string TicketNumber { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
    public string CompanyName { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public string BatchNumber { get; set; } = string.Empty;
    public string District { get; set; } = string.Empty;
    public string LatestOutcome { get; set; } = string.Empty;
    public DateTimeOffset? LatestPublishedAt { get; set; }
}

public sealed class PublicComplaintStatusViewModel
{
    public string TicketNumber { get; set; } = string.Empty;
    public string ComplaintType { get; set; } = string.Empty;
    public string Status { get; set; } = string.Empty;
    public string Category { get; set; } = string.Empty;
    public string CompanyName { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public string BatchNumber { get; set; } = string.Empty;
    public string District { get; set; } = string.Empty;
    public DateTimeOffset? SubmittedAt { get; set; }
    public DateTimeOffset? UpdatedAt { get; set; }
    public List<PublicLabReportViewModel> PublishedReports { get; set; } = [];
}

public sealed class PublicLabReportViewModel
{
    public string ReportNumber { get; set; } = string.Empty;
    public string TicketNumber { get; set; } = string.Empty;
    public string Outcome { get; set; } = string.Empty;
    public string ResultSummary { get; set; } = string.Empty;
    public DateTimeOffset? PublishedAt { get; set; }
    public string CompanyName { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public string BatchNumber { get; set; } = string.Empty;
    public string District { get; set; } = string.Empty;
    public List<PublicLabResultViewModel> Results { get; set; } = [];
    public PublicAdministrativeActionViewModel? Action { get; set; }
    public string PrivacyNotice { get; set; } = string.Empty;
}

public sealed class PublicLabResultViewModel
{
    public string ParameterName { get; set; } = string.Empty;
    public string PermissibleLimit { get; set; } = string.Empty;
    public string ResultValue { get; set; } = string.Empty;
    public string Unit { get; set; } = string.Empty;
    public bool Compliant { get; set; }
    public string Remarks { get; set; } = string.Empty;
}

public sealed class PublicAdministrativeActionViewModel
{
    public string ActionNumber { get; set; } = string.Empty;
    public string ActionType { get; set; } = string.Empty;
    public DateOnly EffectiveDate { get; set; }
    public bool Simulated { get; set; }
    public string PublicSummary { get; set; } = string.Empty;
    public DateTimeOffset DecidedAt { get; set; }
}

public sealed class PublicLicenceStatusViewModel
{
    public string LicenceNumber { get; set; } = string.Empty;
    public string CompanyName { get; set; } = string.Empty;
    public string CompanyStatus { get; set; } = string.Empty;
    public string RegistryBackedStatus { get; set; } = string.Empty;
    public string SimulatedAdministrativeStatus { get; set; } = string.Empty;
    public DateOnly? ValidTo { get; set; }
    public string SafetyNote { get; set; } = string.Empty;
}

public sealed class PublicBatchStatusViewModel
{
    public string BatchNumber { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public string CompanyName { get; set; } = string.Empty;
    public string PlatformStatus { get; set; } = string.Empty;
    public DateOnly? ManufacturedOn { get; set; }
    public DateOnly? ExpiresOn { get; set; }
    public string SafetyNote { get; set; } = string.Empty;
}

public sealed class SafetyAlertViewModel
{
    public long AlertId { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Message { get; set; } = string.Empty;
    public string Severity { get; set; } = string.Empty;
    public string CompanyName { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public string BatchNumber { get; set; } = string.Empty;
    public string Location { get; set; } = string.Empty;
    public DateTimeOffset PublishedAt { get; set; }
}

public sealed class PublicAlertsPageViewModel
{
    public List<PublicAdministrativeActionViewModel> Recalls { get; set; } = [];
    public List<SafetyAlertViewModel> Alerts { get; set; } = [];
}

public sealed class TrustScoreLookupViewModel
{
    [Range(1, long.MaxValue)]
    [Display(Name = "Company ID")]
    public long CompanyId { get; set; } = 1;

    public TrustScoreViewModel? TrustScore { get; set; }
}

public sealed class TrustScoreViewModel
{
    public long CompanyId { get; set; }
    public string CompanyName { get; set; } = string.Empty;
    public decimal Score { get; set; }
    public string RiskLevel { get; set; } = string.Empty;
    public decimal InspectionPoints { get; set; }
    public decimal LabPoints { get; set; }
    public decimal RecallPoints { get; set; }
    public decimal ReviewPoints { get; set; }
    public int ReviewCount { get; set; }
    public string Explanation { get; set; } = string.Empty;
    public string RawComplaintFairnessNote { get; set; } = string.Empty;
    public DateTimeOffset RecalculatedAt { get; set; }
}
