import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './config/queryClient';
import { EnterpriseEventProvider } from './context/EnterpriseEventEngine';

// Public pages
import Login from './pages/Login';
import Register from './pages/Register';
import ForgotPassword from './pages/ForgotPassword';
import CarrierRegister from './pages/CarrierRegister';
import Profile from './pages/Profile';
import ChangePassword from './pages/ChangePassword';
import ClaimList from './pages/ClaimList';
import ClaimDetail from './pages/ClaimDetail';

// Customer pages
import CustomerDashboard from './pages/customer/CustomerDashboard';
import InsurancePlans from './pages/customer/InsurancePlans';
import UploadClaim from './pages/UploadClaim';
import ClaimTracker from './pages/customer/ClaimTracker';
import AiInsights from './pages/customer/AiInsights';
import Notifications from './pages/customer/Notifications';
import Policies from './pages/customer/Policies';
import HealthWallet from './pages/HealthWallet';
import HospitalFinder from './pages/HospitalFinder';
import Wellness from './pages/Wellness';
import CoverageUtilization from './pages/customer/CoverageUtilization';
import HealthPolicyDocuments from './pages/customer/HealthPolicyDocuments';
import ClaimTimelinePage from './pages/customer/ClaimTimelinePage';
import FamilyCoverageManager from './pages/customer/FamilyCoverageManager';
import DownloadCenter from './pages/customer/DownloadCenter';

// Admin pages
import AdminDashboard from './pages/AdminDashboard';
import SystemIntelligence from './pages/admin/SystemIntelligence';
import RuleBuilder from './pages/RuleBuilder';
import SystemMonitor from './pages/SystemMonitor';
import AnalyticsDashboard from './pages/AnalyticsDashboard';
import AdminUsers from './pages/admin/AdminUsers';
import TemplateDesigner from './pages/admin/TemplateDesigner';
import ClaimLineageViewer from './pages/admin/ClaimLineageViewer';
import AgentProductivityAnalytics from './pages/admin/AgentProductivityAnalytics';
import FraudInvestigationHub from './pages/admin/FraudInvestigationHub';

// Carrier pages — UNIQUE dedicated components for each tab
import CarrierDashboard from './pages/CarrierDashboard';
import OperationsSLADashboard from './pages/carrier/OperationsSLADashboard';
import FinancialAnalysis from './pages/carrier/FinancialAnalysis';
import PolicyHeatmap from './pages/carrier/PolicyHeatmap';
import CarrierInsurancePlans from './pages/carrier/InsurancePlans';
import ProductUtilization from './pages/carrier/ProductUtilization';
import SLATracker from './pages/carrier/SLATracker';
import DirectQueryManagement from './pages/carrier/DirectQueryManagement';
import BulkSettlement from './pages/BulkSettlement';
import FraudDashboard from './pages/FraudDashboard';
import LossRatioForecasting from './pages/carrier/LossRatioForecasting';
import ReinsuranceExport from './pages/carrier/ReinsuranceExport';
import FraudHeatmaps from './pages/carrier/FraudHeatmaps';
import ComplianceReports from './pages/carrier/ComplianceReports';

import Layout from './components/Layout';
import ErrorBoundary from './components/ErrorBoundary';
import { DemoDataProvider } from './context/DemoDataProvider';

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <EnterpriseEventProvider>
          <DemoDataProvider>
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

              {/* ───── Customer Routes ───── */}
              <Route path="dashboard" element={<CustomerDashboard />} />
              <Route path="claims/upload" element={<UploadClaim />} />
              <Route path="claims/tracker" element={<ClaimTracker />} />
              <Route path="claims/ai-insights" element={<AiInsights />} />
              <Route path="customer/coverage" element={<InsurancePlans />} />
              <Route path="customer/utilization" element={<CoverageUtilization />} />
              <Route path="customer/hospitals" element={<HospitalFinder />} />
              <Route path="customer/reimbursement" element={<HealthWallet />} />
              <Route path="notifications" element={<Notifications />} />
              <Route path="customer/documents" element={<HealthPolicyDocuments />} />
              <Route path="customer/timeline" element={<ClaimTimelinePage />} />
              <Route path="customer/wellness" element={<Wellness />} />
              <Route path="customer/family" element={<FamilyCoverageManager />} />
              <Route path="customer/downloads" element={<DownloadCenter />} />
              <Route path="policies" element={<Policies />} />
              <Route path="profile" element={<Profile />} />
              <Route path="change-password" element={<ChangePassword />} />
              <Route path="claims" element={<ClaimList />} />
              <Route path="claims/:id" element={<ClaimDetail />} />

              {/* ───── Admin Routes ───── */}
              <Route path="admin" element={<AdminDashboard />} />
              <Route path="admin/workbasket" element={<ClaimList />} />
              <Route path="admin/sla-escalation" element={<AnalyticsDashboard />} />
              <Route path="admin/rules" element={<RuleBuilder />} />
              <Route path="admin/ocr-queue" element={<ClaimList />} />
              <Route path="admin/medical-vault" element={<SystemIntelligence />} />
              <Route path="admin/blacklist" element={<AdminUsers />} />
              <Route path="admin/audit-trail" element={<ClaimList />} />
              <Route path="admin/kafka-monitor" element={<SystemMonitor />} />
              <Route path="admin/agent-metrics" element={<AgentProductivityAnalytics />} />
              <Route path="admin/ai-validation" element={<FraudDashboard />} />
              <Route path="admin/claim-lineage" element={<ClaimLineageViewer />} />
              <Route path="admin/system-monitor" element={<SystemMonitor />} />
              <Route path="admin/template-designer" element={<TemplateDesigner />} />
              <Route path="admin/fraud-investigation" element={<FraudInvestigationHub />} />
              <Route path="analytics" element={<AnalyticsDashboard />} />
              <Route path="admin/settings" element={<Profile />} />

              {/* ───── Carrier Routes — UNIQUE component per tab ───── */}
              <Route path="carrier" element={<CarrierDashboard />} />
              <Route path="carrier/operations" element={<OperationsSLADashboard />} />
              <Route path="carrier/financial-insights" element={<FinancialAnalysis />} />
              <Route path="carrier/fraud-dashboard" element={<FraudDashboard />} />
              <Route path="carrier/settlement" element={<BulkSettlement />} />
              <Route path="carrier/policy-performance" element={<PolicyHeatmap />} />
              <Route path="carrier/bulk-approvals" element={<BulkSettlement />} />
              <Route path="carrier/query-center" element={<DirectQueryManagement />} />
              <Route path="carrier/export-center" element={<ReinsuranceExport />} />
              <Route path="carrier/loss-ratio" element={<LossRatioForecasting />} />
              <Route path="carrier/ppn-config" element={<HospitalFinder />} />
              <Route path="carrier/fraud-heatmaps" element={<FraudHeatmaps />} />
              <Route path="carrier/sla-tracker" element={<SLATracker />} />
              <Route path="carrier/settings" element={<Profile />} />
              {/* Sidebar links — must all be registered to avoid catch-all redirect to /login */}
              <Route path="carrier/compliance" element={<ComplianceReports />} />
              <Route path="carrier/insurance-plans" element={<CarrierInsurancePlans />} />
              <Route path="carrier/product-utilization" element={<ProductUtilization />} />
            </Route>

            {/* Catch-all */}
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
          </Router>
          </DemoDataProvider>
        </EnterpriseEventProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;
