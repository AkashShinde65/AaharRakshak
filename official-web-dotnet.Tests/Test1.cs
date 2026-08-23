using System.ComponentModel.DataAnnotations;
using System.Security.Claims;
using official_web_dotnet.Models;
using official_web_dotnet.Services;

namespace official_web_dotnet.Tests;

[TestClass]
public sealed class PortalValidationTests
{
    [TestMethod]
    public void CompanyResponseValidationRejectsUnsafeMetadata()
    {
        var model = new CompanyResponseFormViewModel
        {
            NoticeNumber = "SCN-TEST-0001",
            ResponseText = "Response",
            ObjectKey = "company-responses/SCN-TEST-0001/response.pdf",
            OriginalFileName = "response.pdf",
            ContentType = "application/pdf",
            SizeBytes = 12L * 1024 * 1024,
            ChecksumSha256 = "not-a-checksum"
        };

        var results = Validate(model);

        Assert.IsTrue(results.Any(result => result.MemberNames.Contains(nameof(CompanyResponseFormViewModel.SizeBytes))));
        Assert.IsTrue(results.Any(result => result.MemberNames.Contains(nameof(CompanyResponseFormViewModel.ChecksumSha256))));
    }

    [TestMethod]
    public void CompanyResponseValidationAcceptsSupportedDocumentMetadata()
    {
        var model = new CompanyResponseFormViewModel
        {
            NoticeNumber = "SCN-TEST-0001",
            ResponseText = "Response",
            ObjectKey = "company-responses/SCN-TEST-0001/response.pdf",
            OriginalFileName = "response.pdf",
            ContentType = "application/pdf",
            SizeBytes = 4096,
            ChecksumSha256 = new string('a', 64)
        };

        Assert.IsEmpty(Validate(model));
    }

    [TestMethod]
    public void AccessTokenReadsSecureCookieClaim()
    {
        var principal = new ClaimsPrincipal(new ClaimsIdentity(new[]
        {
            new Claim("access_token", "jwt-token")
        }));

        Assert.AreEqual("jwt-token", principal.AccessToken());
    }

    [TestMethod]
    public void HotspotDistrictValidationRejectsOverlongInput()
    {
        var model = new OfficialHotspotPageViewModel
        {
            District = new string('x', 81)
        };

        var results = Validate(model);

        Assert.IsTrue(results.Any(result => result.MemberNames.Contains(nameof(OfficialHotspotPageViewModel.District))));
    }

    [TestMethod]
    public void TrustScoreLookupValidationRejectsInvalidCompanyId()
    {
        var model = new TrustScoreLookupViewModel
        {
            CompanyId = 0
        };

        var results = Validate(model);

        Assert.IsTrue(results.Any(result => result.MemberNames.Contains(nameof(TrustScoreLookupViewModel.CompanyId))));
    }

    private static List<ValidationResult> Validate(object model)
    {
        var context = new ValidationContext(model);
        var results = new List<ValidationResult>();
        Validator.TryValidateObject(model, context, results, validateAllProperties: true);
        return results;
    }
}
