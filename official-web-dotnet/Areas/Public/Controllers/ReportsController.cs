using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using official_web_dotnet.Models;
using official_web_dotnet.Services;

namespace official_web_dotnet.Areas.Public.Controllers;

[Area("Public")]
[AllowAnonymous]
public sealed class ReportsController : Controller
{
    private readonly AaharRakshakApiClient _apiClient;

    public ReportsController(AaharRakshakApiClient apiClient)
    {
        _apiClient = apiClient;
    }

    [HttpGet]
    public async Task<IActionResult> Index([FromQuery] PublicSearchViewModel search, CancellationToken cancellationToken)
    {
        try
        {
            search.Results = await _apiClient.PublicSearchAsync(search, cancellationToken);
        }
        catch (ApiException ex)
        {
            ViewData["PortalError"] = ex.Message;
        }
        return View(search);
    }

    [HttpGet]
    public async Task<IActionResult> Complaint(string id, CancellationToken cancellationToken)
    {
        try
        {
            return View(await _apiClient.PublicComplaintAsync(id, cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Report(string id, CancellationToken cancellationToken)
    {
        try
        {
            return View(await _apiClient.PublicReportAsync(id, cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Licence(string licenceNumber = "12345678901234", CancellationToken cancellationToken = default)
    {
        try
        {
            return View(await _apiClient.LicenceStatusAsync(licenceNumber, cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Batch(string batchNumber = "TUR-2026-001", CancellationToken cancellationToken = default)
    {
        try
        {
            return View(await _apiClient.BatchStatusAsync(batchNumber, cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Alerts(CancellationToken cancellationToken)
    {
        try
        {
            return View(new PublicAlertsPageViewModel
            {
                Recalls = await _apiClient.RecallsAsync(cancellationToken),
                Alerts = await _apiClient.AlertsAsync(cancellationToken)
            });
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Trust([FromQuery] TrustScoreLookupViewModel model, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return View(model);
        }

        try
        {
            model.TrustScore = await _apiClient.PublicTrustScoreAsync(model.CompanyId, cancellationToken);
        }
        catch (ApiException ex)
        {
            ViewData["PortalError"] = ex.Message;
        }
        return View(model);
    }

    private IActionResult ApiError(ApiException ex)
    {
        ViewData["Message"] = ex.Message;
        ViewData["StatusCode"] = (int)ex.StatusCode;
        return View("~/Views/Shared/PortalError.cshtml");
    }
}
