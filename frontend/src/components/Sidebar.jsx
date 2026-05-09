import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, FileText, UploadCloud, ShieldCheck, LogOut, ChevronRight, ChevronDown, 
  BarChart3, User, Truck, ShieldAlert, Activity, Bell, BookOpen, Settings, Users, Server, 
  Database, Stethoscope, Search, Layers, Briefcase, FileSearch, Zap, TrendingDown, 
  HeartPulse, FolderDown, Share2, AlertOctagon, Heart, Map, Clock, Network, DollarSign
} from 'lucide-react';

const SidebarItem = ({ item, isActive, location }) => {
  const [expanded, setExpanded] = useState(false);
  const Icon = item.icon;
  const active = isActive(item.path);
  const hasChildren = item.children && item.children.length > 0;

  if (hasChildren) {
    const isChildActive = item.children.some(child => location.pathname.startsWith(child.path));
    return (
      <div className="mb-1">
        <button
          onClick={() => setExpanded(!expanded)}
          className={`w-full group flex items-center px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150 ${
            isChildActive ? 'bg-slate-800 text-white' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
          }`}
        >
          <Icon className={`w-4.5 h-4.5 mr-3 flex-shrink-0 ${isChildActive ? 'text-white' : 'text-slate-500 group-hover:text-slate-300'}`} size={18} />
          <span className="flex-1 text-left">{item.name}</span>
          {expanded || isChildActive ? (
            <ChevronDown className="w-3.5 h-3.5 text-slate-400" />
          ) : (
            <ChevronRight className="w-3.5 h-3.5 text-slate-400" />
          )}
        </button>
        {(expanded || isChildActive) && (
          <div className="ml-6 pl-3 border-l border-slate-700 mt-1 space-y-1">
            {item.children.map(child => (
              <Link
                key={child.name}
                to={child.path}
                className={`group flex items-center px-3 py-2 rounded-lg text-xs font-medium transition-all duration-150 ${
                  location.pathname === child.path
                    ? 'bg-blue-600/20 text-blue-400'
                    : 'text-slate-500 hover:bg-slate-800/50 hover:text-slate-300'
                }`}
              >
                <span className="flex-1">{child.name}</span>
              </Link>
            ))}
          </div>
        )}
      </div>
    );
  }

  return (
    <Link
      to={item.path}
      className={`group flex items-center px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150 mb-1 ${
        active
          ? 'bg-blue-600 text-white shadow-md shadow-blue-900/40'
          : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
      }`}
    >
      <Icon className={`w-4.5 h-4.5 mr-3 flex-shrink-0 ${active ? 'text-white' : 'text-slate-500 group-hover:text-slate-300'}`} size={18} />
      <span className="flex-1">{item.name}</span>
      {active && <ChevronRight className="w-3.5 h-3.5 text-blue-200" />}
      {item.admin && !active && (
        <span className="text-[10px] bg-indigo-600/30 text-indigo-300 border border-indigo-700/50 px-1.5 py-0.5 rounded-md font-semibold">
          Admin
        </span>
      )}
    </Link>
  );
};

const Sidebar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const isAdmin = user?.userRole === 'FMG_ADMIN';
  const isCarrier = user?.userRole === 'CARRIER_USER';
  const isCustomer = user?.userRole === 'CUSTOMER';

  const customerItems = [
    { name: 'Smart Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Upload Claim', path: '/claims/upload', icon: UploadCloud },
    { name: 'Claim Journey Tracker', path: '/claims/tracker', icon: Activity },
    { name: 'AI Claim Assistant', path: '/claims/ai-insights', icon: ShieldCheck },
    { name: 'Insurance Plans & Benefits', path: '/customer/coverage', icon: BookOpen },
    { name: 'Coverage Utilization', path: '/customer/utilization', icon: BarChart3 },
    { name: 'Hospital Network Explorer', path: '/customer/hospitals', icon: Stethoscope },
    { name: 'Reimbursement Wallet', path: '/customer/reimbursement', icon: Database },
    { name: 'Notifications Center', path: '/notifications', icon: Bell },
    { name: 'Health Policy Documents', path: '/customer/documents', icon: FileText },
    { name: 'Claim Timeline', path: '/customer/timeline', icon: Clock },
    { name: 'Wellness Benefits', path: '/customer/wellness', icon: HeartPulse },
    { name: 'Family Coverage Manager', path: '/customer/family', icon: Users },
    { name: 'Download Center', path: '/customer/downloads', icon: FolderDown }
  ];

  const adminItems = [
    { name: 'Intelligent Workbasket', path: '/admin/workbasket', icon: Briefcase, admin: true },
    { name: 'SLA Escalation Center', path: '/admin/sla-escalation', icon: AlertOctagon, admin: true },
    { name: 'Visual Rule Builder', path: '/admin/rules', icon: Layers, admin: true },
    { name: 'OCR Correction Queue', path: '/admin/ocr-queue', icon: FileSearch, admin: true },
    { name: 'Medical Vault', path: '/admin/medical-vault', icon: Stethoscope, admin: true },
    { name: 'Blacklist Manager', path: '/admin/blacklist', icon: ShieldAlert, admin: true },
    { name: 'Audit Trail Viewer', path: '/admin/audit-trail', icon: Search, admin: true },
    { name: 'Kafka Pipeline Monitor', path: '/admin/kafka-monitor', icon: Server, admin: true },
    { name: 'Agent Productivity Analytics', path: '/admin/agent-metrics', icon: Users, admin: true },
    { name: 'AI Validation Center', path: '/admin/ai-validation', icon: Zap, admin: true },
    { name: 'Claim Lineage Viewer', path: '/admin/claim-lineage', icon: Network, admin: true },
    { name: 'System Monitoring', path: '/admin/system-monitor', icon: Activity, admin: true },
    { name: 'PDF Template Designer', path: '/admin/template-designer', icon: FileText, admin: true },
    { name: 'Fraud Investigation Hub', path: '/admin/fraud-investigation', icon: ShieldAlert, admin: true }
  ];

  const carrierItems = [
    { name: 'Carrier Dashboard', path: '/carrier', icon: LayoutDashboard },
    {
      name: 'Operations', icon: Activity,
      children: [
        { name: 'Operations Command Center', path: '/carrier/operations' },
        { name: 'SLA Mission Control', path: '/carrier/sla-tracker' },
        { name: 'Bulk Settlement', path: '/carrier/bulk-approvals' },
        { name: 'Query Management', path: '/carrier/query-center' }
      ]
    },
    {
      name: 'Financial Intelligence', icon: DollarSign,
      children: [
        { name: 'Financial Analysis', path: '/carrier/financial-insights' },
        { name: 'Loss Ratio Forecasting', path: '/carrier/loss-ratio' },
        { name: 'Fraud Savings Ledger', path: '/carrier/fraud-dashboard' }
      ]
    },
    {
      name: 'Insurance Products', icon: BookOpen,
      children: [
        { name: 'Policy Heatmap', path: '/carrier/policy-performance' },
        { name: 'Insurance Plans', path: '/carrier/insurance-plans' },
        { name: 'Product Utilization', path: '/carrier/product-utilization' }
      ]
    },
    {
      name: 'Provider Network', icon: Map,
      children: [
        { name: 'PPN Configuration', path: '/carrier/ppn-config' },
        { name: 'Fraud Heatmaps', path: '/carrier/fraud-heatmaps' }
      ]
    },
    {
      name: 'Exports & Compliance', icon: ShieldCheck,
      children: [
        { name: 'Reinsurance Export', path: '/carrier/export-center' },
        { name: 'Compliance Reports', path: '/carrier/compliance' }
      ]
    }
  ];

  let navItems = [];
  if (isCustomer) navItems = customerItems;
  if (isAdmin) navItems = adminItems;
  if (isCarrier) navItems = carrierItems;

  const isActive = (path) => {
    if (path === '/dashboard' || path === '/admin' || path === '/carrier') {
      return location.pathname === path;
    }
    return location.pathname.startsWith(path);
  };

  return (
    <aside className="w-64 flex-shrink-0 bg-slate-950 border-r border-slate-800 flex flex-col h-full overflow-hidden">
      {/* Brand */}
      <div className="px-6 py-5 border-b border-slate-800">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center shadow-lg shadow-blue-900/40 flex-shrink-0">
            <span className="text-white font-black text-base">T</span>
          </div>
          <div>
            <p className="text-white font-bold text-sm leading-tight">TPA ClaimSys</p>
            <p className="text-slate-500 text-xs">Insurance Platform</p>
          </div>
        </div>
      </div>

      {/* User card */}
      <div className="mx-4 mt-4 px-3 py-3 bg-slate-800/60 rounded-xl border border-slate-700/50 flex items-center gap-3">
        <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center flex-shrink-0">
          <span className="text-white font-bold text-xs">
            {(user?.name || user?.username || 'U')[0].toUpperCase()}
          </span>
        </div>
        <div className="min-w-0">
          <p className="text-slate-200 text-sm font-semibold truncate">
            {user?.name || user?.username}
          </p>
          <p className="text-slate-500 text-xs truncate">{user?.userRole}</p>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 mt-5 space-y-0.5 overflow-y-auto overflow-x-hidden custom-scrollbar pb-4">
        <p className="text-slate-600 text-[10px] font-semibold uppercase tracking-widest px-3 mb-2">
          Navigation
        </p>
        {navItems.map((item) => (
          <SidebarItem key={item.name} item={item} isActive={isActive} location={location} />
        ))}
      </nav>

      {/* Logout */}
      <div className="p-3 border-t border-slate-800 mt-auto bg-slate-950">
        <button
          id="sidebar-logout"
          onClick={logout}
          className="w-full flex items-center px-3 py-2.5 rounded-xl text-sm font-medium text-slate-500 hover:bg-red-500/10 hover:text-red-400 transition-all duration-150 group"
        >
          <LogOut className="w-4 h-4 mr-3 group-hover:text-red-400" size={18} />
          Sign Out
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
