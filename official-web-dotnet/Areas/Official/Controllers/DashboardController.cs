using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using official_web_dotnet.Models;
using official_web_dotnet.Services;

namespace official_web_dotnet.Areas.Official.Controllers;

[Area("Official")]
[Authorize(Policy = "OfficialPortal")]
public sealed class DashboardController : Controller
{
    private const string OfficialPath = "api/v1/official/admin-actions";
    private readonly AaharRakshakApiClient _apiClient;

    public DashboardController(AaharRakshakApiClient apiClient)
    {
        _apiClient = apiClient;
    }

    [HttpGet]
    public async Task<IActionResult> Index(CancellationToken cancellationToken)
    {
        try
        {
            var dashboard = await _apiClient.OfficialDashboardAsync(Token(), cancellationToken);
            return View(dashboard);
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Notices(CancellationToken cancellationToken)
    {
        try
        {
            return View(await _apiClient.OfficialNoticesAsync(Token(), cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Hotspots(string district = "Pune", CancellationToken cancellationToken = default)
    {
        try
        {
            return View(new OfficialHotspotPageViewModel
            {
                District = district,
                Hotspots = await _apiClient.OfficialHotspotsAsync(Token(), district, cancellationToken)
            });
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> DetectHotspots(OfficialHotspotPageViewModel model, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return View(nameof(Hotspots), model);
        }

        try
        {
            await _apiClient.DetectHotspotsAsync(Token(), model.District, cancellationToken);
            return RedirectToAction(nameof(Hotspots), new { district = model.District });
        }
        catch (ApiException ex)
        {
            ModelState.AddModelError(string.Empty, ex.Message);
            model.Hotspots = await SafeHotspots(model.District, cancellationToken);
            return View(nameof(Hotspots), model);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Escalations(CancellationToken cancellationToken)
    {
        try
        {
            return View(await _apiClient.SlaEscalationsAsync(Token(), cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [Authorize(Policy = "SeniorOfficial")]
    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> CheckOverdue(CancellationToken cancellationToken)
    {
        try
        {
            await _apiClient.TriggerSlaCheckAsync(Token(), cancellationToken);
            return RedirectToAction(nameof(Escalations));
        }
        catch (ApiException ex)
        {
            TempData["PortalError"] = ex.Message;
            return RedirectToAction(nameof(Escalations));
        }
    }

    [Authorize(Policy = "SeniorOfficial")]
    [HttpGet]
    public async Task<IActionResult> Alerts(CancellationToken cancellationToken)
    {
        try
        {
            return View(new OfficialAlertPageViewModel
            {
                Outbox = await _apiClient.OfficialAlertOutboxAsync(Token(), cancellationToken),
                MockExternalEvents = await _apiClient.MockExternalEventsAsync(Token(), cancellationToken)
            });
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public async Task<IActionResult> Notice(string id, CancellationToken cancellationToken)
    {
        try
        {
            return View(await _apiClient.NoticeAsync(OfficialPath, Token(), id, cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [Authorize(Policy = "SeniorOfficial")]
    [HttpGet]
    public IActionResult Issue(long reportId)
    {
        return View(new IssueNoticeViewModel
        {
            ReportId = reportId,
            Subject = "Show-cause notice for published laboratory report",
            ResponseDueAt = DateTimeOffset.UtcNow.AddDays(14)
        });
    }

    [Authorize(Policy = "SeniorOfficial")]
    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> Issue(IssueNoticeViewModel model, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return View(model);
        }

        try
        {
            var notice = await _apiClient.IssueNoticeAsync(Token(), model, cancellationToken);
            return RedirectToAction(nameof(Notice), new { id = notice.NoticeNumber });
        }
        catch (ApiException ex)
        {
            ModelState.AddModelError(string.Empty, ex.Message);
            return View(model);
        }
    }

    [Authorize(Policy = "SeniorOfficial")]
    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> Review(string id, ReviewNoticeViewModel model, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return RedirectToAction(nameof(Notice), new { id });
        }

        try
        {
            await _apiClient.ReviewNoticeAsync(Token(), id, model, cancellationToken);
            return RedirectToAction(nameof(Notice), new { id });
        }
        catch (ApiException ex)
        {
            TempData["PortalError"] = ex.Message;
            return RedirectToAction(nameof(Notice), new { id });
        }
    }

    [Authorize(Policy = "SeniorOfficial")]
    [HttpGet]
    public IActionResult Decision(string id)
    {
        ViewData["NoticeNumber"] = id;
        return View(new DecisionViewModel
        {
            EffectiveDate = DateOnly.FromDateTime(DateTime.UtcNow.Date.AddDays(1))
        });
    }

    [Authorize(Policy = "SeniorOfficial")]
    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> Decision(string id, DecisionViewModel model, CancellationToken cancellationToken)
    {
        ViewData["NoticeNumber"] = id;
        if (!ModelState.IsValid)
        {
            return View(model);
        }

        try
        {
            await _apiClient.DecideAsync(Token(), id, model, cancellationToken);
            return RedirectToAction(nameof(Notice), new { id });
        }
        catch (ApiException ex)
        {
            ModelState.AddModelError(string.Empty, ex.Message);
            return View(model);
        }
    }

    private string Token()
    {
        return User.AccessToken() ?? string.Empty;
    }

    private async Task<List<HotspotViewModel>> SafeHotspots(string district, CancellationToken cancellationToken)
    {
        try
        {
            return await _apiClient.OfficialHotspotsAsync(Token(), district, cancellationToken);
        }
        catch (ApiException)
        {
            return [];
        }
    }

    private IActionResult ApiError(ApiException ex)
    {
        ViewData["Message"] = ex.Message;
        ViewData["StatusCode"] = (int)ex.StatusCode;
        return View("~/Views/Shared/PortalError.cshtml");
    }
}
