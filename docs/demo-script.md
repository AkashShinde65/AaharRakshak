# Five-Minute Demonstration Script

## 0:00-0:30 Introduction

Introduce AaharRakshak: a privacy-first academic food-safety platform for citizens, companies, officials and the public. State the safety boundary: images and AI never prove chemical adulteration; laboratory confirmation is mandatory.

## 0:30-1:20 Citizen Mobile Flow

Show the Android app:

1. Login/register with mock OTP.
2. Open packaged-food scan.
3. Barcode lookup returns demo product data.
4. Mock OCR suggests product/company/licence/batch/expiry.
5. User corrects details, gives GPS consent, adds evidence metadata and submits.
6. Show tracking number/history.

## 1:20-2:10 Investigation Flow

In Swagger or the Official portal:

1. Login as district officer.
2. Show priority dashboard and assign complaint to inspector.
3. Login as inspector; show assigned-only access.
4. Schedule inspection, check in, collect sample and show custody history.

## 2:10-3:00 Laboratory And Public Report

1. Login as lab officer.
2. Confirm sample receipt and submit PDF report metadata.
3. Login as district officer/admin.
4. Review and publish report.
5. Open public report and show anonymized output.

## 3:00-3:50 Company Due Process

1. Issue show-cause notice.
2. Login as company.
3. Submit response document metadata.
4. Senior official records simulated final decision.
5. Show public recall/alert when applicable.

## 3:50-4:35 Intelligence

1. Show hotspot dashboard with Leaflet/OpenStreetMap aggregate clusters.
2. Trigger SLA overdue check.
3. Show alert outbox, citizen alerts and WebSocket endpoint.
4. Show Trust Score with fairness note that raw complaints alone do not prove guilt.

## 4:35-5:00 Deployment And Wrap-Up

Show Docker Compose with MySQL, Redis and MinIO; mention Android debug APK path after build. Close with privacy and legal boundaries: no full Aadhaar, no unauthorized government data, no real SMS and no real licence cancellation.
