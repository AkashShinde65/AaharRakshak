using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using official_web_dotnet.Models;
using official_web_dotnet.Services;

namespace official_web_dotnet.Areas.Company.Controllers;

[Area("Company")]
[Authorize(Policy = "CompanyPortal")]
public sealed class DashboardController : Controller
{
    private const string CompanyPath = "api/v1/company/admin-actions";
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
            return View(await _apiClient.CompanyNoticesAsync(Token(), cancellationToken));
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
            return View(await _apiClient.NoticeAsync(CompanyPath, Token(), id, cancellationToken));
        }
        catch (ApiException ex)
        {
            return ApiError(ex);
        }
    }

    [HttpGet]
    public IActionResult Respond(string id)
    {
        return View(new CompanyResponseFormViewModel
        {
            NoticeNumber = id,
            ContentType = "application/pdf"
        });
    }

    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> Respond(CompanyResponseFormViewModel model, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return View(model);
        }

        try
        {
            await _apiClient.SubmitCompanyResponseAsync(Token(), model.NoticeNumber, model, cancellationToken);
            return RedirectToAction(nameof(Notice), new { id = model.NoticeNumber });
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

    private IActionResult ApiError(ApiException ex)
    {
        ViewData["Message"] = ex.Message;
        ViewData["StatusCode"] = (int)ex.StatusCode;
        return View("~/Views/Shared/PortalError.cshtml");
    }
}
