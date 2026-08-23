import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
import { AnimatePresence, motion } from 'framer-motion';
import { useAuth } from './hooks/useAuth.js';
import LandingPage from './pages/LandingPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import ComplaintSubmissionPage from './pages/ComplaintSubmissionPage.jsx';
import ComplaintTrackingPage from './pages/ComplaintTrackingPage.jsx';
import AdminDashboardPage from './pages/AdminDashboardPage.jsx';
import InspectorDashboardPage from './pages/InspectorDashboardPage.jsx';
import LabDashboardPage from './pages/LabDashboardPage.jsx';
import CompanyDashboardPage from './pages/CompanyDashboardPage.jsx';
import DistrictDashboardPage from './pages/DistrictDashboardPage.jsx';
import ProfilePage from './pages/ProfilePage.jsx';
import NotFoundPage from './pages/NotFoundPage.jsx';
import DashboardLayout from './components/layout/DashboardLayout.jsx';

function ProtectedRoute({ children, adminOnly = false, roles = null }) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (adminOnly && !['CENTRAL_ADMINISTRATOR', 'DISTRICT_ESCALATION_OFFICER'].includes(user?.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  if (roles && !roles.includes(user?.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

function PageTransition({ children }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -8 }}
      transition={{ duration: 0.22, ease: 'easeOut' }}
    >
      {children}
    </motion.div>
  );
}

export default function App() {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route
          path="/"
          element={
            <PageTransition>
              <LandingPage />
            </PageTransition>
          }
        />
        <Route
          path="/login"
          element={
            <PageTransition>
              <LoginPage />
            </PageTransition>
          }
        />
        <Route
          path="/register"
          element={
            <PageTransition>
              <RegisterPage />
            </PageTransition>
          }
        />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<DashboardPage />} />
          <Route path="complaints/new" element={<ComplaintSubmissionPage />} />
          <Route path="complaints/track" element={<ComplaintTrackingPage />} />
          <Route
            path="admin"
            element={
              <ProtectedRoute adminOnly>
                <AdminDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="inspector"
            element={
              <ProtectedRoute roles={['FOOD_INSPECTOR']}>
                <InspectorDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="lab"
            element={
              <ProtectedRoute roles={['LABORATORY_OFFICER']}>
                <LabDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="company"
            element={
              <ProtectedRoute roles={['COMPANY']}>
                <CompanyDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="district"
            element={
              <ProtectedRoute roles={['DISTRICT_ESCALATION_OFFICER', 'CENTRAL_ADMINISTRATOR']}>
                <DistrictDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route path="profile" element={<ProfilePage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AnimatePresence>
  );
}
