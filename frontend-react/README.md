# AaharRakshak React Frontend

Production-grade React UI for the AaharRakshak Spring Boot REST API.

## Stack

- React + Vite
- Tailwind CSS
- React Router
- Axios
- Framer Motion
- Lucide icons
- Dark mode

## Run Locally

From the repository root:

```bash
cd frontend-react
npm install
cp .env.example .env
npm run dev
```

Open:

```text
http://localhost:5173
```

## Connect To Spring Boot Backend

Edit `frontend-react/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_ENABLE_MOCKS=false
```

Start the Spring Boot API separately, then use:

```text
http://localhost:8080/api/v1/health
```

The frontend API client lives in:

```text
src/services/api.js
```

It automatically:

- Reads `VITE_API_BASE_URL`
- Sends `Authorization: Bearer <token>` for protected requests
- Clears stored auth data on HTTP 401

Auth endpoints used:

```text
POST /auth/login
POST /auth/register/citizen
```

Complaint endpoints used:

```text
POST /citizen/complaints/drafts
POST /citizen/complaints/{draftId}/submit
GET  /public/transparency/complaints/{ticketNumber}/status
GET  /citizen/complaints
```

## Mock Mode

For frontend-only demos without the backend:

```env
VITE_ENABLE_MOCKS=true
```

Demo users:

```text
citizen@aaharrakshak.dev / password
company@aaharrakshak.dev / password
inspector@aaharrakshak.dev / password
lab@aaharrakshak.dev / password
district@aaharrakshak.dev / password
admin@aaharrakshak.dev / password
```

## Build

```bash
npm run build
npm run preview
```

## Folder Structure

```text
frontend-react/
  src/
    assets/              static frontend assets
    components/
      dashboard/         dashboard cards, tables and timeline components
      layout/            navbar, sidebar and reusable page layouts
      ui/                buttons, cards, inputs, modals, toasts and loaders
    contexts/            auth, theme and toast providers
    data/                mock data for frontend-only demos
    hooks/               reusable React hooks
    pages/               route-level pages
    services/            centralized API, auth and complaint services
    styles/              Tailwind entry CSS
    utils/               constants, formatters and validators
```

## Main Routes

```text
/                         landing page
/login                    login
/register                 citizen registration
/dashboard                user dashboard
/dashboard/complaints/new complaint submission
/dashboard/complaints/track complaint tracking
/dashboard/admin          admin analytics
/dashboard/profile        profile
```

## Backend CORS

Allow this origin in the Spring Boot backend:

```text
http://localhost:5173
```

For the existing backend config, set:

```env
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5080,http://localhost:8080,http://10.0.2.2:8080
```

## Notes

- Do not put real secrets in `.env`.
- Do not enter real Aadhaar data.
- Images and OCR results are presented only as triage aids; laboratory confirmation remains mandatory.
- Business rules stay in the Spring Boot API. The React app focuses on UI, validation, routing and API calls.
