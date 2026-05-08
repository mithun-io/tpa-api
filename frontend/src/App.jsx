import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './config/queryClient';

// Public pages
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import CarrierRegister from './pages/CarrierRegister';

// Customer pages
import CustomerDashboard from './pages/customer/CustomerDashboard';
import InsurancePlans from './pages/customer/InsurancePlans';
import UploadClaim from './pages/UploadClaim';
import ClaimTracker from './pages/customer/ClaimTracker';
import AiInsights from './pages/customer/AiInsights';
import Notifications from './pages/customer/Notifications';
import Policies from './pages/customer/Policies';
import Profile from './pages/Profile';
import ClaimDetail from './pages/ClaimDetail';
import ClaimList from './pages/ClaimList';
import ChangePassword from './pages/ChangePassword';
import HealthWallet from './pages/HealthWallet';
import HospitalFinder from './pages/HospitalFinder';

// Admin pages
import AdminDashboard from './pages/AdminDashboard';
import SystemIntelligence from './pages/admin/SystemIntelligence';
import ComplianceCenter from './pages/admin/ComplianceCenter';
import RuleBuilder from './pages/RuleBuilder';
import SystemMonitor from './pages/SystemMonitor';
import AnalyticsDashboard from './pages/AnalyticsDashboard';
import AdminUsers from './pages/admin/AdminUsers';
import TemplateDesigner from './pages/admin/TemplateDesigner';

// Carrier pages
import CarrierDashboard from './pages/CarrierDashboard';
import UnderwritingIntelligence from './pages/carrier/UnderwritingIntelligence';
import CustomerPortfolio from './pages/carrier/CustomerPortfolio';
import CarrierAnalytics from './pages/CarrierAnalytics';
import BulkSettlement from './pages/BulkSettlement';
import FraudDashboard from './pages/FraudDashboard';

import Layout from './components/Layout';
import ErrorBoundary from './components/ErrorBoundary';

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <Toaster
            position="top-right"
            toastOptions={{
              duration: 4000,
              style: {
                background: '#1e293b',
                color: '#e2e8f0',
                border: '1px solid #334155',
              },
            }}
          />
          <Routes>
            {/* Public routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/carrier-register" element={<CarrierRegister />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />

            {/* Protected routes */}
            <Route path="/" element={
              <ProtectedRoute>
                <ErrorBoundary>
                  <Layout />
                </ErrorBoundary>
              </ProtectedRoute>
            }>
              <Route index element={<Navigate to="/dashboard" replace />} />
              
              {/* Customer Routes */}
              <Route path="dashboard" element={<CustomerDashboard />} />
              <Route path="customer/coverage" element={<InsurancePlans />} />
              <Route path="claims/upload" element={<UploadClaim />} />
              <Route path="claims/tracker" element={<ClaimTracker />} />
              <Route path="claims/ai-insights" element={<AiInsights />} />
              <Route path="customer/reimbursement" element={<HealthWallet />} />
              <Route path="customer/hospitals" element={<HospitalFinder />} />
              <Route path="notifications" element={<Notifications />} />
              <Route path="policies" element={<Policies />} />
              <Route path="profile" element={<Profile />} />
              <Route path="claims" element={<ClaimList />} />
              <Route path="claims/:id" element={<ClaimDetail />} />
              <Route path="change-password" element={<ChangePassword />} />

              {/* Admin Routes */}
              <Route path="admin" element={<AdminDashboard />} />
              <Route path="admin/intelligence" element={<SystemIntelligence />} />
              <Route path="admin/compliance" element={<ComplianceCenter />} />
              <Route path="admin/workbasket" element={<ClaimList />} />
              <Route path="admin/sla-alerts" element={<ComplianceCenter />} />
              <Route path="admin/ai-validation" element={<FraudDashboard />} />
              <Route path="admin/rules" element={<RuleBuilder />} />
              <Route path="admin/agent-metrics" element={<ComplianceCenter />} />
              <Route path="admin/ocr-queue" element={<ClaimList />} />
              <Route path="admin/medical-vault" element={<SystemIntelligence />} />
              <Route path="admin/blacklist" element={<AdminUsers />} />
              <Route path="admin/sla-escalation" element={<AnalyticsDashboard />} />
              <Route path="analytics" element={<AnalyticsDashboard />} />
              <Route path="admin/audit-trail" element={<ClaimList />} />
              <Route path="admin/kafka-monitor" element={<SystemMonitor />} />
              <Route path="admin/template-designer" element={<TemplateDesigner />} />
              <Route path="admin/users" element={<AdminUsers />} />
              <Route path="admin/carriers" element={<AnalyticsDashboard />} />
              <Route path="admin/settings" element={<Profile />} />
              <Route path="admin/fraud-dashboard" element={<FraudDashboard />} />

              {/* Carrier Routes */}
              <Route path="carrier" element={<CarrierDashboard />} />
              <Route path="carrier/underwriting" element={<UnderwritingIntelligence />} />
              <Route path="carrier/portfolio" element={<CustomerPortfolio />} />
              <Route path="carrier/leakage" element={<FraudDashboard />} />
              <Route path="carrier/financial-insights" element={<CarrierAnalytics />} />
              <Route path="carrier/hospital-analytics" element={<CarrierAnalytics />} />
              <Route path="carrier/sla-tracker" element={<CarrierAnalytics />} />
              <Route path="carrier/bulk-approvals" element={<BulkSettlement />} />
              <Route path="carrier/fraud-dashboard" element={<FraudDashboard />} />
              <Route path="carrier/policy-performance" element={<CarrierAnalytics />} />
              <Route path="carrier/query-center" element={<ClaimList />} />
              <Route path="carrier/loss-ratio" element={<UnderwritingIntelligence />} />
              <Route path="carrier/ppn-config" element={<HospitalFinder />} />
              <Route path="carrier/export-center" element={<CarrierAnalytics />} />
              <Route path="carrier/settings" element={<Profile />} />
            </Route>

            {/* Catch-all */}
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        </Router>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;
