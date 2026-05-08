import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getClaims } from '../../api/claim.service';
import { useAuth } from '../../context/AuthContext';
import StatusBadge from '../../components/StatusBadge';
import Loader from '../../components/Loader';
import ErrorMessage from '../../components/ErrorMessage';
import { useWebSocket } from '../../hooks/useWebSocket';
import {
  FileText, CheckCircle, XCircle, Clock, Plus, ArrowRight,
  TrendingUp, AlertCircle, RefreshCw
} from 'lucide-react';

const StatCard = ({ label, value, icon: Icon, color, bg, onClick }) => (
  <button
    onClick={onClick}
    className={`bg-slate-800 rounded-xl shadow-sm border border-slate-700 p-5 flex items-center gap-4 w-full text-left hover:border-slate-600 hover:shadow-md hover:shadow-black/20 transition-all duration-200 group ${onClick ? 'cursor-pointer' : ''}`}
  >
    <div className={`${bg} p-3 rounded-xl flex-shrink-0 border border-white/5`}>
      <Icon className={`w-6 h-6 ${color}`} />
    </div>
    <div>
      <p className="text-sm text-slate-400 font-medium group-hover:text-slate-300 transition-colors">{label}</p>
      <p className={`text-2xl font-bold ${color}`}>{value}</p>
    </div>
  </button>
);

const CustomerDashboard = () => {
  const { user } = useAuth();
  const navigate  = useNavigate();
  const [claims, setClaims]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);

  const { messages, connected } = useWebSocket([`/topic/claims/${user?.id || ''}`]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true); setError(null);
      const data = await getClaims({ page: 0, size: 12 });
      const mapped = (data?.content || []).map(c => ({ ...c, status: c.claimStatus || c.status }));
      setClaims(mapped);
    } catch {
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchDashboardData(); }, []);

  // Update claims via WebSocket
  useEffect(() => {
    const wsClaims = messages[`/topic/claims/${user?.id || ''}`];
    if (wsClaims && wsClaims.length > 0) {
      const latestUpdate = wsClaims[wsClaims.length - 1];
      setClaims(prev => prev.map(c => c.id === latestUpdate.claimId ? { ...c, status: latestUpdate.newStatus } : c));
    }
  }, [messages, user?.id]);

  if (loading) return <Loader fullScreen message="Loading dashboard…" />;
  if (error)   return <ErrorMessage message={error} onRetry={fetchDashboardData} />;

  const total    = claims.length;
  const approved = claims.filter(c => c.status === 'CARRIER_APPROVED' || c.status === 'SETTLED').length;
  const rejected = claims.filter(c => c.status === 'REJECTED').length;
  const pending  = claims.filter(c => ['SUBMITTED', 'AI_VALIDATED', 'UNDER_REVIEW', 'ADMIN_APPROVED', 'PAYMENT_PENDING'].includes(c.status)).length;

  const stats = [
    { label: 'Total Claims', value: total,    icon: FileText,    color: 'text-blue-400',   bg: 'bg-blue-500/10' },
    { label: 'Approved',     value: approved,  icon: CheckCircle, color: 'text-emerald-400',bg: 'bg-emerald-500/10' },
    { label: 'In Progress',  value: pending,   icon: Clock,       color: 'text-amber-400',  bg: 'bg-amber-500/10' },
    { label: 'Rejected',     value: rejected,  icon: XCircle,     color: 'text-red-400',    bg: 'bg-red-500/10' },
  ];

  return (
    <div className="space-y-6 max-w-[1400px] mx-auto">
      <div className="flex justify-between items-start flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-100 tracking-tight">
            Welcome, {user?.name || user?.username || 'User'} 👋
          </h1>
          <p className="text-sm text-slate-400 mt-1 flex items-center gap-2">
            Here's a summary of your insurance claims
            {connected && <span className="flex items-center gap-1 text-emerald-400 text-xs bg-emerald-400/10 px-2 py-0.5 rounded-full"><span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span> Live Updates Active</span>}
          </p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => navigate('/claims/upload')}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-xl text-sm font-medium transition-colors shadow-md shadow-blue-900/20"
          >
            <Plus className="w-4 h-4" /> New Claim
          </button>
        </div>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map(s => (
          <StatCard
            key={s.label}
            {...s}
            onClick={s.label !== 'Total Claims' ? () => navigate('/claims') : undefined}
          />
        ))}
      </div>

      <div className="bg-slate-800 rounded-2xl shadow-sm border border-slate-700">
        <div className="px-6 py-5 border-b border-slate-700 flex items-center justify-between">
          <h2 className="text-base font-bold text-slate-100">Recent Claims Overview</h2>
        </div>
        <div className="p-6">
          <table className="w-full text-left text-sm text-slate-400">
            <thead className="text-xs uppercase bg-slate-800/50 text-slate-300">
              <tr>
                <th className="px-4 py-3 rounded-l-lg">ID</th>
                <th className="px-4 py-3">Patient</th>
                <th className="px-4 py-3">Hospital</th>
                <th className="px-4 py-3">Amount</th>
                <th className="px-4 py-3 rounded-r-lg">Status</th>
              </tr>
            </thead>
            <tbody>
              {claims.slice(0, 5).map(claim => (
                 <tr key={claim.id} className="border-b border-slate-700/50 hover:bg-slate-700/20 cursor-pointer" onClick={() => navigate(`/claims/${claim.id}`)}>
                   <td className="px-4 py-4 text-blue-400 font-medium">#{claim.id}</td>
                   <td className="px-4 py-4 text-slate-200">{claim.patientName}</td>
                   <td className="px-4 py-4">{claim.hospitalName}</td>
                   <td className="px-4 py-4 font-semibold text-slate-200">${claim.amount}</td>
                   <td className="px-4 py-4"><StatusBadge status={claim.status} /></td>
                 </tr>
              ))}
            </tbody>
          </table>
          {claims.length === 0 && <p className="text-center text-slate-500 py-4">No claims found.</p>}
        </div>
      </div>
    </div>
  );
};

export default CustomerDashboard;
