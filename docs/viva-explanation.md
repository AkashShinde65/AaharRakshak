# Interview And Viva Explanation

## Problem Statement

Food-safety reporting often lacks a single auditable flow from citizen complaint to official investigation, laboratory confirmation, company due process and public transparency. AaharRakshak demonstrates how such a platform can be designed without overclaiming from images or bypassing legal processes.

## Architecture

The backend is a Spring Boot modular monolith. This keeps transactional workflows, authorization, validation and audit logging in one service for the academic prototype. The ASP.NET portal and Android app are clients only; they do not duplicate business rules.

## Security

Authentication uses JWT access and refresh tokens with BCrypt password storage. Roles separate citizens, companies, inspectors, lab officers, district officers and administrators. Android stores JWTs with Android Keystore-backed encryption. Public reports are anonymized.

## Database

Flyway migrations V1-V7 manage normalized MySQL schema areas: identity/RBAC, company/licence/catalogue, complaints/evidence, investigation/lab, administrative action/transparency, notifications/audit and intelligence/hotspots/SLA/trust.

## Android

The Android app is Kotlin-native with Material Design Compose. It supports registration/login, mock OTP, barcode scan adapter, CameraX-ready capture, mock OCR, correction screens, prepared-dish complaints, GPS consent, evidence metadata, offline Room drafts, report/alert/trust/hotspot screens and Hindi/English resources.

## AI And OCR Boundary

OCR and mock AI are triage tools only. They can suggest visible categories and extract label text, but laboratory confirmation remains mandatory for chemical adulteration conclusions.

## Administrative Boundary

The platform records show-cause notices, company responses and simulated actions. It never performs a real licence cancellation or government registry update.

## Deployment

Docker Compose runs Spring Boot, MySQL, Redis, MinIO and the ASP.NET portal. Production deployment should use managed secrets, HTTPS, exact CORS origins and managed persistent storage.

## Why This Design

A modular monolith is easier to verify for a phase-wise academic project than distributed microservices. Adapter interfaces keep external systems mockable and replaceable, while Flyway keeps schema evolution traceable.
