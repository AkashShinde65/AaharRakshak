package com.aaharrakshak.mobile

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aaharrakshak.mobile.data.EvidenceType
import com.aaharrakshak.mobile.data.HotspotResponse
import com.aaharrakshak.mobile.data.MobileFormValidator
import com.aaharrakshak.mobile.ui.MobileScreen
import com.aaharrakshak.mobile.ui.Phase8UiState
import com.aaharrakshak.mobile.ui.Phase8ViewModel

class MainActivity : ComponentActivity() {
    private val phase8ViewModel: Phase8ViewModel by viewModels {
        val repository = (application as AaharRakshakMobileApp).container.repository
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                Phase8ViewModel(repository) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AaharRakshakTheme {
                MobileApp(phase8ViewModel)
            }
        }
    }
}

@Composable
private fun AaharRakshakTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2E7D32),
            secondary = Color(0xFFF6B73C),
            tertiary = Color(0xFF16697A),
            background = Color(0xFFFAFCF8),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onSecondary = Color(0xFF15201A),
            onSurface = Color(0xFF15201A)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileApp(viewModel: Phase8ViewModel) {
    val state by viewModel.state.collectAsState()
    val drafts by viewModel.offlineDrafts.collectAsState()
    var otpCode by remember { mutableStateOf("123456") }

    val filePicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.attachEvidence(EvidenceType.PRODUCT_LABEL_PHOTO, uri.lastPathSegment ?: "mobile-evidence.jpg")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AaharRakshak") },
                actions = {
                    if (state.loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 16.dp))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = state.screen == item.screen,
                        onClick = {
                            if (item.screen == MobileScreen.PACKAGE_COMPLAINT) {
                                viewModel.updateComplaintForm(preparedDish = false)
                            }
                            viewModel.updateScreen(item.screen)
                            if (item.screen == MobileScreen.ALERTS) viewModel.loadAlerts()
                            if (item.screen == MobileScreen.HOTSPOTS) viewModel.loadHotspots()
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusMessages(state)
            when (state.screen) {
                MobileScreen.AUTH -> AuthScreen(state, otpCode, { otpCode = it }, viewModel)
                MobileScreen.DASHBOARD -> DashboardScreen(state, viewModel)
                MobileScreen.PACKAGE_COMPLAINT -> ComplaintScreen(state, preparedDish = false, viewModel, filePicker::launch)
                MobileScreen.DISH_COMPLAINT -> ComplaintScreen(state, preparedDish = true, viewModel, filePicker::launch)
                MobileScreen.DRAFTS -> DraftsScreen(drafts)
                MobileScreen.TRACKING -> TrackingScreen(state)
                MobileScreen.LOOKUP -> LookupScreen(state, viewModel)
                MobileScreen.ALERTS -> AlertsScreen(state)
                MobileScreen.HOTSPOTS -> HotspotScreen(state.hotspots)
                MobileScreen.TRUST -> TrustScoreScreen(state, viewModel)
                MobileScreen.PRIVACY -> PrivacyScreen()
            }
        }
    }
}

@Composable
private fun StatusMessages(state: Phase8UiState) {
    state.message?.let { Surface(color = Color(0xFFE8F5E9), modifier = Modifier.fillMaxWidth()) { Text(it, Modifier.padding(12.dp)) } }
    state.error?.let { Surface(color = Color(0xFFFFEBEE), modifier = Modifier.fillMaxWidth()) { Text(it, Modifier.padding(12.dp)) } }
}

@Composable
private fun AuthScreen(
    state: Phase8UiState,
    otpCode: String,
    onOtpChanged: (String) -> Unit,
    viewModel: Phase8ViewModel
) {
    Section("Citizen Access") {
        Field("Full name", state.fullName) { viewModel.updateAuth(name = it) }
        Field("Email or mobile", state.identifier) { viewModel.updateAuth(identifier = it) }
        Field("Mobile", state.mobile, KeyboardType.Phone) { viewModel.updateAuth(mobile = it) }
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.updateAuth(password = it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::login, enabled = !state.loading) {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Login")
            }
            OutlinedButton(onClick = viewModel::register, enabled = !state.loading) {
                Text("Register")
            }
        }
    }

    Section("Mock OTP") {
        Text("Development OTP flow uses mock verification. Full Aadhaar is never requested or stored.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::requestOtp) { Text("Request OTP") }
            OutlinedTextField(
                value = otpCode,
                onValueChange = onOtpChanged,
                modifier = Modifier.weight(1f),
                label = { Text("OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = { viewModel.verifyOtp(otpCode) }) { Text("Verify") }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DashboardScreen(state: Phase8UiState, viewModel: Phase8ViewModel) {
    Section("Home Dashboard") {
        Text(if (state.isAuthenticated) "Welcome ${state.displayName}" else "Use public lookup, or sign in to submit complaints.")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionChip("Packaged scan", Icons.Default.QrCodeScanner) {
                viewModel.updateComplaintForm(preparedDish = false)
                viewModel.updateScreen(MobileScreen.PACKAGE_COMPLAINT)
            }
            ActionChip("Prepared dish", Icons.Default.Storefront) {
                viewModel.updateComplaintForm(preparedDish = true)
                viewModel.updateScreen(MobileScreen.DISH_COMPLAINT)
            }
            ActionChip("History", Icons.Default.History) {
                viewModel.updateScreen(MobileScreen.TRACKING)
                viewModel.loadHistory()
            }
            ActionChip("Public lookup", Icons.Default.Search) { viewModel.updateScreen(MobileScreen.LOOKUP) }
            ActionChip("Alerts", Icons.Default.Notifications) {
                viewModel.updateScreen(MobileScreen.ALERTS)
                viewModel.loadAlerts()
            }
            ActionChip("Hotspots", Icons.Default.Map) {
                viewModel.updateScreen(MobileScreen.HOTSPOTS)
                viewModel.loadHotspots()
            }
            ActionChip("Trust Score", Icons.Default.Shield) {
                viewModel.updateScreen(MobileScreen.TRUST)
                viewModel.loadTrustScore(1)
            }
            ActionChip("Privacy", Icons.Default.Policy) { viewModel.updateScreen(MobileScreen.PRIVACY) }
        }
    }
    Section("Low Bandwidth Mode") {
        Text("Drafts save locally with Room, uploads are represented by checksummed object metadata, and users can submit once the API is reachable.")
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ComplaintScreen(
    state: Phase8UiState,
    preparedDish: Boolean,
    viewModel: Phase8ViewModel,
    openFilePicker: (String) -> Unit
) {
    Section(if (preparedDish) "Prepared Dish Complaint" else "Packaged Food Scan") {
        Text(MobileFormValidator.chemicalAdulterationDisclaimer())
        if (!preparedDish) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::scanBarcodeThenLookup) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan barcode")
                }
                OutlinedButton(onClick = viewModel::scanPackageImage) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("OCR label")
                }
            }
            Field("Barcode/GTIN", state.barcode) { }
        }
        Field(if (preparedDish) "Dish name" else "Product name", state.productName) {
            viewModel.updateComplaintForm(productName = it)
        }
        Field(if (preparedDish) "Vendor" else "Company", state.companyOrVendor) {
            viewModel.updateComplaintForm(companyOrVendor = it)
        }
        if (!preparedDish) {
            Field("FSSAI licence", state.fssaiLicenceNumber, KeyboardType.Number) {
                viewModel.updateComplaintForm(licence = it)
            }
            Field("Batch/Lot", state.batchNumber) { viewModel.updateComplaintForm(batch = it) }
            Field("Expiry date YYYY-MM-DD", state.expiryDate) { viewModel.updateComplaintForm(expiry = it) }
        }
        Field("Description", state.description) { viewModel.updateComplaintForm(description = it) }
        Field("Address", state.address) { viewModel.updateComplaintForm(address = it) }
        Row {
            Checkbox(
                checked = state.gpsConsent,
                onCheckedChange = { viewModel.updateComplaintForm(gpsConsent = it) }
            )
            Text("Share GPS for routing and regional alerts")
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { openFilePicker("image/*") }) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Image")
            }
            OutlinedButton(onClick = { viewModel.attachEvidence(EvidenceType.VIDEO, "mobile-video.mp4") }) {
                Text("Video metadata")
            }
            OutlinedButton(onClick = { viewModel.attachEvidence(EvidenceType.RECEIPT_FILE, "receipt.pdf") }) {
                Text("Receipt")
            }
        }
        Text("Evidence attached: ${state.evidence.size}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::submitComplaint, enabled = !state.loading) { Text("Submit") }
            OutlinedButton(onClick = viewModel::saveOfflineDraft) {
                Icon(Icons.Default.CloudOff, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save draft")
            }
        }
    }
}

@Composable
private fun PrivacyScreen() {
    Section("Privacy and Consent") {
        Text("JWT access and refresh tokens are stored using Android Keystore-backed encrypted storage.")
        Text("Offline complaint drafts remain on-device in Room until the citizen submits them.")
        Text("GPS coordinates are sent only when the user accepts consent for routing and regional alerts.")
        Text("Aadhaar integration remains mock-only; full Aadhaar details, images or biometrics are never collected.")
        Text(MobileFormValidator.chemicalAdulterationDisclaimer())
    }
}

@Composable
private fun DraftsScreen(drafts: List<com.aaharrakshak.mobile.data.OfflineComplaintDraftEntity>) {
    Section("Offline Drafts") {
        if (drafts.isEmpty()) {
            Text("No local drafts.")
        } else {
            drafts.forEach { draft ->
                Text("${draft.complaintType}: ${draft.productOrDishName ?: draft.companyOrVendorName ?: "Unnamed"} - ${draft.syncStatus}")
            }
        }
    }
}

@Composable
private fun TrackingScreen(state: Phase8UiState) {
    Section("Complaint Tracking") {
        state.submittedComplaint?.let { Text("Latest tracking number: ${it.ticketNumber}") }
        if (state.history.isEmpty()) {
            Text("No complaint history loaded.")
        } else {
            state.history.forEach { complaint ->
                Text("${complaint.ticketNumber}: ${complaint.status} - ${complaint.confirmedProductName ?: complaint.confirmedCompanyName ?: "Complaint"}")
                complaint.statusHistory.forEach { history ->
                    Text("  ${history.changedAt}: ${history.status} ${history.note.orEmpty()}")
                }
            }
        }
    }
}

@Composable
private fun LookupScreen(state: Phase8UiState, viewModel: Phase8ViewModel) {
    Section("Public Lookup") {
        Field("Product name", state.productQuery) { viewModel.searchProducts(it) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.searchProducts(state.productQuery) }) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Search")
            }
        }
        state.productLookupResults.forEach { product ->
            Text("${product.name} - ${product.companyName.orEmpty()} - barcode ${product.primaryBarcode.orEmpty()}")
        }
    }
}

@Composable
private fun AlertsScreen(state: Phase8UiState) {
    Section("Recall and Regional Safety Alerts") {
        if (state.publicAlerts.isEmpty()) {
            Text("No public alerts loaded.")
        } else {
            state.publicAlerts.forEach { alert ->
                Text("${alert.title}: ${alert.message} ${alert.location.orEmpty()}")
            }
        }
    }
}

@Composable
private fun HotspotScreen(hotspots: List<HotspotResponse>) {
    Section("Hotspot Map") {
        Text("Map uses aggregate coordinates only; citizen-level locations are not displayed publicly.")
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadDataWithBaseURL("https://www.openstreetmap.org", hotspotHtml(hotspots), "text/html", "UTF-8", null)
                }
            },
            update = { it.loadDataWithBaseURL("https://www.openstreetmap.org", hotspotHtml(hotspots), "text/html", "UTF-8", null) }
        )
    }
}

@Composable
private fun TrustScoreScreen(state: Phase8UiState, viewModel: Phase8ViewModel) {
    Section("Vendor Trust Score") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.loadTrustScore(1) }) { Text("Load demo company") }
            OutlinedButton(onClick = {
                viewModel.attachEvidence(EvidenceType.RECEIPT_FILE, "review-receipt.pdf")
            }) { Text("Attach receipt") }
        }
        state.trustScore?.let {
            Text("${it.companyName ?: "Company"}: ${it.score} (${it.riskLevel})")
            Text(it.explanation.orEmpty())
            Text(it.rawComplaintFairnessNote.orEmpty())
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = label != "Description"
    )
}

@Composable
private fun ActionChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) }
    )
}

private data class BottomItem(
    val label: String,
    val screen: MobileScreen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomItems = listOf(
    BottomItem("Home", MobileScreen.DASHBOARD, Icons.Default.Shield),
    BottomItem("Scan", MobileScreen.PACKAGE_COMPLAINT, Icons.Default.QrCodeScanner),
    BottomItem("Lookup", MobileScreen.LOOKUP, Icons.Default.Search),
    BottomItem("Alerts", MobileScreen.ALERTS, Icons.Default.Notifications),
    BottomItem("Map", MobileScreen.HOTSPOTS, Icons.Default.Map)
)

private fun hotspotHtml(hotspots: List<HotspotResponse>): String {
    val markers = hotspots.joinToString("\n") { hotspot ->
        """
        L.circle([${hotspot.centerLatitude}, ${hotspot.centerLongitude}], {
          radius: ${hotspot.radiusKm * 1000},
          color: '${riskColor(hotspot.riskLevel)}',
          fillOpacity: 0.22
        }).addTo(map).bindPopup('${hotspot.riskLevel}: ${hotspot.complaintCount} complaints - ${hotspot.productOrVendor ?: "Aggregate"}');
        """.trimIndent()
    }
    val center = hotspots.firstOrNull()
    val lat = center?.centerLatitude ?: 18.52043
    val lon = center?.centerLongitude ?: 73.85674
    return """
        <!doctype html>
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>html, body, #map { height: 100%; margin: 0; }</style>
          </head>
          <body>
            <div id="map"></div>
            <script>
              const map = L.map('map').setView([$lat, $lon], 12);
              L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 18,
                attribution: '&copy; OpenStreetMap contributors'
              }).addTo(map);
              $markers
            </script>
          </body>
        </html>
    """.trimIndent()
}

private fun riskColor(riskLevel: String): String = when (riskLevel) {
    "CRITICAL" -> "#B00020"
    "HIGH" -> "#E65100"
    "MEDIUM" -> "#F6B73C"
    else -> "#2E7D32"
}
