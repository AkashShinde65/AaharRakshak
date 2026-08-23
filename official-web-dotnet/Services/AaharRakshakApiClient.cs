using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using official_web_dotnet.Models;

namespace official_web_dotnet.Services;

public sealed class AaharRakshakApiClient
{
    private static readonly JsonSerializerOptions JsonOptions = new(JsonSerializerDefaults.Web)
    {
        PropertyNameCaseInsensitive = true
    };

    private readonly HttpClient _httpClient;

    public AaharRakshakApiClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public Task<AuthResponseViewModel> LoginAsync(LoginViewModel model, CancellationToken cancellationToken)
    {
        return SendAsync<AuthResponseViewModel>(HttpMethod.Post, "api/v1/auth/login", null, model, cancellationToken);
    }

    public Task<List<OfficialDashboardItemViewModel>> OfficialDashboardAsync(string token, CancellationToken cancellationToken)
    {
        return SendAsync<List<OfficialDashboardItemViewModel>>(HttpMethod.Get, "api/v1/official/admin-actions/dashboard", token, null, cancellationToken);
    }

    public Task<List<HotspotViewModel>> OfficialHotspotsAsync(string token, string district, CancellationToken cancellationToken)
    {
        var query = QueryString(new Dictionary<string, string?>
        {
            ["district"] = district
        });
        return SendAsync<List<HotspotViewModel>>(HttpMethod.Get, "api/v1/official/intelligence/hotspots/district" + query, token, null, cancellationToken);
    }

    public Task<List<HotspotViewModel>> DetectHotspotsAsync(string token, string district, CancellationToken cancellationToken)
    {
        var query = QueryString(new Dictionary<string, string?>
        {
            ["district"] = district
        });
        return SendAsync<List<HotspotViewModel>>(HttpMethod.Post, "api/v1/official/intelligence/hotspots/detect" + query, token, null, cancellationToken);
    }

    public Task<List<SlaEscalationViewModel>> SlaEscalationsAsync(string token, CancellationToken cancellationToken)
    {
        return SendAsync<List<SlaEscalationViewModel>>(HttpMethod.Get, "api/v1/official/intelligence/sla/escalations", token, null, cancellationToken);
    }

    public Task<List<SlaEscalationViewModel>> TriggerSlaCheckAsync(string token, CancellationToken cancellationToken)
    {
        return SendAsync<List<SlaEscalationViewModel>>(HttpMethod.Post, "api/v1/official/intelligence/sla/check-overdue", token, null, cancellationToken);
    }

    public Task<List<AlertOutboxViewModel>> OfficialAlertOutboxAsync(string token, CancellationToken cancellationToken)
    {
        return SendAsync<List<AlertOutboxViewModel>>(HttpMethod.Get, "api/v1/official/intelligence/alerts/outbox", token, null, cancellationToken);
    }

    public Task<List<MockExternalEventViewModel>> MockExternalEventsAsync(string token, CancellationToken cancellationToken)
    {
        return SendAsync<List<MockExternalEventViewModel>>(HttpMethod.Get, "api/v1/official/intelligence/mock-external-events", token, null, cancellationToken);
    }

    public Task<List<ShowCauseNoticeViewModel>> OfficialNoticesAsync(string token, CancellationToken cancellationToken)
    {
        return SendAsync<List<ShowCauseNoticeViewModel>>(HttpMethod.Get, "api/v1/official/admin-actions/notices", token, null, cancellationToken);
    }

    public Task<ShowCauseNoticeViewModel> NoticeAsync(string pathPrefix, string token, string noticeNumber, CancellationToken cancellationToken)
    {
        return SendAsync<ShowCauseNoticeViewModel>(HttpMethod.Get, $"{pathPrefix}/notices/{Uri.EscapeDataString(noticeNumber)}", token, null, cancellationToken);
    }

    public Task<ShowCauseNoticeViewModel> IssueNoticeAsync(string token, IssueNoticeViewModel model, CancellationToken cancellationToken)
    {
        return SendAsync<ShowCauseNoticeViewModel>(
            HttpMethod.Post,
            $"api/v1/official/admin-actions/reports/{model.ReportId}/show-cause-notices",
            token,
            model,
            cancellationToken);
    }

    public Task<ShowCauseNoticeViewModel> ReviewNoticeAsync(string token, string noticeNumber, ReviewNoticeViewModel model, CancellationToken cancellationToken)
    {
        return SendAsync<ShowCauseNoticeViewModel>(
            HttpMethod.Post,
            $"api/v1/official/admin-actions/notices/{Uri.EscapeDataString(noticeNumber)}/review",
            token,
            model,
            cancellationToken);
    }

    public Task<AdministrativeActionViewModel> DecideAsync(string token, string noticeNumber, DecisionViewModel model, CancellationToken cancellationToken)
    {
        return SendAsync<AdministrativeActionViewModel>(
            HttpMethod.Post,
            $"api/v1/official/admin-actions/notices/{Uri.EscapeDataString(noticeNumber)}/decision",
            token,
            model,
            cancellationToken);
    }

    public Task<List<ShowCauseNoticeViewModel>> CompanyNoticesAsync(string token, CancellationToken cancellationToken)
    {
        return SendAsync<List<ShowCauseNoticeViewModel>>(HttpMethod.Get, "api/v1/company/admin-actions/notices", token, null, cancellationToken);
    }

    public Task<ShowCauseNoticeViewModel> SubmitCompanyResponseAsync(
        string token,
        string noticeNumber,
        CompanyResponseFormViewModel model,
        CancellationToken cancellationToken)
    {
        var request = new
        {
            responseText = model.ResponseText,
            document = new
            {
                objectKey = model.ObjectKey,
                originalFileName = model.OriginalFileName,
                contentType = model.ContentType,
                sizeBytes = model.SizeBytes,
                checksumSha256 = model.ChecksumSha256
            }
        };
        return SendAsync<ShowCauseNoticeViewModel>(
            HttpMethod.Post,
            $"api/v1/company/admin-actions/notices/{Uri.EscapeDataString(noticeNumber)}/responses",
            token,
            request,
            cancellationToken);
    }

    public Task<List<PublicSearchResultViewModel>> PublicSearchAsync(PublicSearchViewModel search, CancellationToken cancellationToken)
    {
        var query = QueryString(new Dictionary<string, string?>
        {
            ["complaintNumber"] = search.ComplaintNumber,
            ["company"] = search.Company,
            ["product"] = search.Product,
            ["batch"] = search.Batch,
            ["location"] = search.Location
        });
        return SendAsync<List<PublicSearchResultViewModel>>(HttpMethod.Get, "api/v1/public/transparency/search" + query, null, null, cancellationToken);
    }

    public Task<PublicComplaintStatusViewModel> PublicComplaintAsync(string ticketNumber, CancellationToken cancellationToken)
    {
        return SendAsync<PublicComplaintStatusViewModel>(
            HttpMethod.Get,
            $"api/v1/public/transparency/complaints/{Uri.EscapeDataString(ticketNumber)}/status",
            null,
            null,
            cancellationToken);
    }

    public Task<PublicLabReportViewModel> PublicReportAsync(string reportNumber, CancellationToken cancellationToken)
    {
        return SendAsync<PublicLabReportViewModel>(
            HttpMethod.Get,
            $"api/v1/public/transparency/reports/{Uri.EscapeDataString(reportNumber)}",
            null,
            null,
            cancellationToken);
    }

    public Task<PublicLicenceStatusViewModel> LicenceStatusAsync(string licenceNumber, CancellationToken cancellationToken)
    {
        return SendAsync<PublicLicenceStatusViewModel>(
            HttpMethod.Get,
            $"api/v1/public/transparency/licences/{Uri.EscapeDataString(licenceNumber)}/status",
            null,
            null,
            cancellationToken);
    }

    public Task<PublicBatchStatusViewModel> BatchStatusAsync(string batchNumber, CancellationToken cancellationToken)
    {
        return SendAsync<PublicBatchStatusViewModel>(
            HttpMethod.Get,
            $"api/v1/public/transparency/batches/{Uri.EscapeDataString(batchNumber)}/status",
            null,
            null,
            cancellationToken);
    }

    public Task<List<PublicAdministrativeActionViewModel>> RecallsAsync(CancellationToken cancellationToken)
    {
        return SendAsync<List<PublicAdministrativeActionViewModel>>(HttpMethod.Get, "api/v1/public/transparency/recalls", null, null, cancellationToken);
    }

    public Task<List<SafetyAlertViewModel>> AlertsAsync(CancellationToken cancellationToken)
    {
        return SendAsync<List<SafetyAlertViewModel>>(HttpMethod.Get, "api/v1/public/transparency/alerts", null, null, cancellationToken);
    }

    public Task<TrustScoreViewModel> PublicTrustScoreAsync(long companyId, CancellationToken cancellationToken)
    {
        return SendAsync<TrustScoreViewModel>(
            HttpMethod.Get,
            $"api/v1/public/trust/companies/{companyId}",
            null,
            null,
            cancellationToken);
    }

    private async Task<T> SendAsync<T>(
        HttpMethod method,
        string path,
        string? token,
        object? body,
        CancellationToken cancellationToken)
    {
        using var request = new HttpRequestMessage(method, path);
        if (!string.IsNullOrWhiteSpace(token))
        {
            request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        }
        if (body is not null)
        {
            request.Content = JsonContent.Create(body, options: JsonOptions);
        }

        using var response = await _httpClient.SendAsync(request, cancellationToken);
        if (!response.IsSuccessStatusCode)
        {
            var message = await response.Content.ReadAsStringAsync(cancellationToken);
            throw new ApiException(response.StatusCode, string.IsNullOrWhiteSpace(message) ? response.ReasonPhrase ?? "API request failed" : message);
        }

        var value = await response.Content.ReadFromJsonAsync<T>(JsonOptions, cancellationToken);
        return value ?? throw new ApiException(HttpStatusCode.NoContent, "API returned an empty response.");
    }

    private static string QueryString(Dictionary<string, string?> values)
    {
        var parts = values
            .Where(pair => !string.IsNullOrWhiteSpace(pair.Value))
            .Select(pair => $"{Uri.EscapeDataString(pair.Key)}={Uri.EscapeDataString(pair.Value!)}")
            .ToList();
        return parts.Count == 0 ? string.Empty : "?" + string.Join("&", parts);
    }
}

public sealed class ApiException : Exception
{
    public ApiException(HttpStatusCode statusCode, string message) : base(message)
    {
        StatusCode = statusCode;
    }

    public HttpStatusCode StatusCode { get; }
}
