using System.Security.Claims;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using official_web_dotnet.Models;
using official_web_dotnet.Services;

namespace official_web_dotnet.Controllers;

public sealed class AuthController : Controller
{
    private readonly AaharRakshakApiClient _apiClient;

    public AuthController(AaharRakshakApiClient apiClient)
    {
        _apiClient = apiClient;
    }

    [AllowAnonymous]
    [HttpGet]
    public IActionResult Login(string? returnUrl = null)
    {
        ViewData["ReturnUrl"] = returnUrl;
        return View(new LoginViewModel());
    }

    [AllowAnonymous]
    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> Login(LoginViewModel model, string? returnUrl, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return View(model);
        }

        try
        {
            var auth = await _apiClient.LoginAsync(model, cancellationToken);
            var claims = new List<Claim>
            {
                new(ClaimTypes.Name, string.IsNullOrWhiteSpace(auth.FullName) ? auth.Email : auth.FullName),
                new(ClaimTypes.Email, auth.Email ?? model.Identifier),
                new("access_token", auth.AccessToken)
            };
            claims.AddRange(auth.Roles.Select(role => new Claim(ClaimTypes.Role, role)));

            var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            await HttpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme,
                new ClaimsPrincipal(identity),
                new AuthenticationProperties
                {
                    IsPersistent = false,
                    AllowRefresh = true
                });

            return RedirectToLocal(returnUrl, auth.Roles);
        }
        catch (ApiException ex)
        {
            ModelState.AddModelError(string.Empty, $"Login failed: {ex.Message}");
            return View(model);
        }
    }

    [Authorize]
    [ValidateAntiForgeryToken]
    [HttpPost]
    public async Task<IActionResult> Logout()
    {
        await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
        return RedirectToAction("Index", "Reports", new { area = "Public" });
    }

    [AllowAnonymous]
    [HttpGet]
    public IActionResult Denied()
    {
        return View();
    }

    private IActionResult RedirectToLocal(string? returnUrl, IReadOnlyCollection<string> roles)
    {
        if (!string.IsNullOrWhiteSpace(returnUrl) && Url.IsLocalUrl(returnUrl))
        {
            return Redirect(returnUrl);
        }

        if (roles.Contains("COMPANY"))
        {
            return RedirectToAction("Index", "Dashboard", new { area = "Company" });
        }

        if (roles.Any(role => role is "FOOD_INSPECTOR" or "LABORATORY_OFFICER" or "DISTRICT_ESCALATION_OFFICER" or "CENTRAL_ADMINISTRATOR"))
        {
            return RedirectToAction("Index", "Dashboard", new { area = "Official" });
        }

        return RedirectToAction("Index", "Reports", new { area = "Public" });
    }
}
