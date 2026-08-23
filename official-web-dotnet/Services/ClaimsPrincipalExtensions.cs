using System.Security.Claims;

namespace official_web_dotnet.Services;

public static class ClaimsPrincipalExtensions
{
    public static string? AccessToken(this ClaimsPrincipal principal)
    {
        return principal.FindFirstValue("access_token");
    }
}
