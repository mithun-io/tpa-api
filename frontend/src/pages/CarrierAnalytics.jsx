import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axios';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area,
  PieChart, Pie, Cell
} from 'recharts';
import {
  TrendingUp,
  ShieldAlert,
  Clock,
  Activity,
  ArrowUpRight,
  ArrowDownRight,
  Zap,
  AlertTriangle,
  RefreshCw
} from 'lucide-react';
import { useLocation } from 'react-router-dom';

// ── Page-title mapping based on route ────────────────────────────────────────
const ROUTE_TITLES = {
  '/carrier/hospital-analytics': 'Hospital Analytics',
  '/carrier/sla-tracker':        'Real-time SLA Tracker',
  '/carrier/policy-performance': 'Policy Performance',
  '/carrier/export-center':      'Reinsurance Export Center',
  '/carrier/leakage':            'Claim Leakage & Savings',
};

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444'];

// ── Helpers ───────────────────────────────────────────────────────────────────
const safe = (v, fallback = 0) => (v != null && !isNaN(v) ? v : fallback);

const CarrierAnalytics = () => {
  const location = useLocation();
  const pageTitle = ROUTE_TITLES[location.pathname] || 'Analytics Dashboard';

  const [analytics,  setAnalytics]  = useState(null);
  const [leakage,    setLeakage]    = useState(null);
  const [hospitals,  setHospitals]  = useState(null);
  const [sla,        setSla]        = useState(null);
  const [loading,    setLoading]    = useState(true);
  const [error,      setError]      = useState(null);

  const fetchAll = async () => {
    setLoading(true);
    setError(null);
    try {
      const [anaRes, leakRes, hospRes, slaRes] = await Promise.allSettled([
        axiosInstance.get('/analytics/dashboard'),
        axiosInstance.get('/analytics/leakage'),
        axiosInstance.get('/analytics/hospitals'),
        axiosInstance.get('/analytics/sla/performance'),
      ]);

      if (anaRes.status  === 'fulfilled') setAnalytics(anaRes.value.data);
      if (leakRes.status === 'fulfilled') setLeakage(leakRes.value.data);
      if (hospRes.status === 'fulfilled') setHospitals(hospRes.value.data);
      if (slaRes.status  === 'fulfilled') setSla(slaRes.value.data);
    } catch (err) {
      setError('Failed to load analytics data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  // ── Derived stats (null-safe) ─────────────────────────────────────────────
  const totalClaims      = safe(analytics?.totalClaims);
  const totalPayout      = safe(analytics?.totalClaimAmount ?? leakage?.totalClaimedAmount);
  const leakageSaved     = safe(leakage?.leakageAmount);
  const avgTAT           = safe(sla?.avgProcessingHours);
  const slaBreached      = safe(sla?.slaBreached);
  const slaCompliance    = safe(sla?.slaComplianceRate);
  const leakageRate      = safe(leakage?.leakageRate);
  const uniqueHospitals  = safe(hospitals?.totalUniqueHospitals);

  // Hospital volume chart
  const hospitalVolumeData = hospitals?.topHospitalsByVolume
    ? Object.entries(hospitals.topHospitalsByVolume)
        .slice(0, 6)
        .map(([name, count]) => ({ name: name.length > 15 ? name.substring(0, 15) + '…' : name, volume: count }))
    : [];

  // Monthly trend (mock enriched with real totals)
  const monthlyTrend = [
    { month: 'Jan', volume: Math.round(totalClaims * 0.12) },
    { month: 'Feb', volume: Math.round(totalClaims * 0.14) },
    { month: 'Mar', volume: Math.round(totalClaims * 0.16) },
    { month: 'Apr', volume: Math.round(totalClaims * 0.18) },
    { month: 'May', volume: Math.round(totalClaims * 0.20) },
    { month: 'Jun', volume: Math.round(totalClaims * 0.20) },
  ];

  // SLA pie chart
  const slaPieData = [
    { name: 'Within SLA',  value: safe(sla?.withinSla,   totalClaims) },
    { name: 'SLA Breached', value: slaBreached },
    { name: 'Escalated',   value: safe(sla?.escalated) },
  ].filter(d => d.value > 0);

  // ── Loading ───────────────────────────────────────────────────────────────
  if (loading) return (
    <div className="min-h-[400px] flex items-center justify-center">
      <div className="flex flex-col items-center gap-4 text-slate-400">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500" />
        <p className="text-sm font-medium">Loading analytics…</p>
      </div>
    </div>
  );

  // ── Error state (graceful) ────────────────────────────────────────────────
  if (error && !analytics && !leakage && !hospitals && !sla) return (
    <div className="bg-red-500/10 border border-red-500/20 rounded-xl p-10 text-center max-w-lg mx-auto mt-10">
      <AlertTriangle className="w-12 h-12 text-red-500 mx-auto mb-4" />
      <h2 className="text-xl font-bold text-slate-100 mb-2">Analytics Unavailable</h2>
      <p className="text-slate-400 text-sm mb-6">{error}</p>
      <button
        onClick={fetchAll}
        className="px-5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 rounded-lg text-sm font-medium flex items-center gap-2 mx-auto"
      >
        <RefreshCw className="w-4 h-4" /> Retry
      </button>
    </div>
  );

  return (
    <div className="p-0 text-slate-200">
      <div className="max-w-7xl mx-auto space-y-8">

        {/* Header */}
        <div className="flex justify-between items-end flex-wrap gap-4">
          <div>
            <h1 className="text-3xl font-black tracking-tight text-white">{pageTitle}</h1>
            <p className="text-slate-400 font-medium mt-1">Strategic overview of claim performance &amp; savings</p>
          </div>
          <button
            onClick={fetchAll}
            className="flex items-center gap-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-300 rounded-xl text-sm font-medium transition-colors"
          >
            <RefreshCw size={14} /> Refresh
          </button>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatCard
            title="Total Claims"
            value={totalClaims.toLocaleString()}
            change="+12.5%"
            isPositive={false}
            icon={Activity}
            color="blue"
          />
          <StatCard
            title="Leakage Detected"
            value={`$${(leakageSaved / 1000).toFixed(1)}K`}
            change={`${leakageRate.toFixed(1)}% rate`}
            isPositive={false}
            icon={ShieldAlert}
            color="red"
          />
          <StatCard
            title="Avg. Turnaround"
            value={`${avgTAT.toFixed(1)}h`}
            change={slaBreached > 0 ? `${slaBreached} breaches` : 'On Track'}
            isPositive={slaBreached === 0}
            icon={Clock}
            color="blue"
          />
          <StatCard
            title="SLA Compliance"
            value={`${slaCompliance.toFixed(1)}%`}
            change={`${uniqueHospitals} hospitals`}
            isPositive={slaCompliance >= 80}
            icon={Zap}
            color={slaCompliance >= 80 ? 'emerald' : 'amber'}
          />
        </div>

        {/* Charts Row */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

          {/* Volume Trend */}
          <div className="lg:col-span-2 bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8">
            <div className="flex justify-between items-center mb-8">
              <h3 className="text-xl font-bold flex items-center gap-2">
                <TrendingUp className="text-blue-500" size={20} />
                Monthly Claim Volume Trend
              </h3>
            </div>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={monthlyTrend}>
                  <defs>
                    <linearGradient id="colorVolume" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%"  stopColor="#3b82f6" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="#3b82f6" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" vertical={false} />
                  <XAxis dataKey="month" stroke="#64748b" axisLine={false} tickLine={false} />
                  <YAxis stroke="#64748b" axisLine={false} tickLine={false} />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #1e293b', borderRadius: '12px' }}
                    itemStyle={{ color: '#f8fafc' }}
                  />
                  <Area type="monotone" dataKey="volume" stroke="#3b82f6" strokeWidth={3} fillOpacity={1} fill="url(#colorVolume)" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* SLA Performance */}
          <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8">
            <h3 className="text-xl font-bold mb-8">SLA Performance</h3>
            {slaPieData.length > 0 ? (
              <div className="h-[200px]">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={slaPieData} cx="50%" cy="50%" innerRadius={55} outerRadius={80} paddingAngle={5} dataKey="value">
                      {slaPieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip
                      contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #1e293b', borderRadius: '12px' }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <div className="h-[200px] flex items-center justify-center text-slate-500 text-sm">No SLA data available</div>
            )}
            <div className="space-y-3 mt-4">
              {slaPieData.map((entry, i) => (
                <div key={entry.name} className="flex items-center justify-between text-sm">
                  <div className="flex items-center gap-2">
                    <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: COLORS[i] }} />
                    <span className="text-slate-400">{entry.name}</span>
                  </div>
                  <span className="font-bold text-white">{entry.value}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Hospital Performance */}
        {hospitalVolumeData.length > 0 && (
          <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8">
            <h3 className="text-xl font-bold mb-8 flex items-center gap-2">
              <Activity className="text-emerald-400" size={20} />
              Top Hospitals by Claim Volume
            </h3>
            <div className="h-[280px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={hospitalVolumeData} margin={{ top: 0, right: 20, left: 0, bottom: 40 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" vertical={false} />
                  <XAxis
                    dataKey="name"
                    stroke="#64748b"
                    axisLine={false}
                    tickLine={false}
                    angle={-30}
                    textAnchor="end"
                    tick={{ fontSize: 11 }}
                  />
                  <YAxis stroke="#64748b" axisLine={false} tickLine={false} />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #1e293b', borderRadius: '12px' }}
                    itemStyle={{ color: '#f8fafc' }}
                  />
                  <Bar dataKey="volume" fill="#10b981" radius={[4, 4, 0, 0]} name="Claims" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {/* Leakage Summary */}
        {leakage && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-slate-900/50 border border-slate-800 rounded-3xl p-6">
              <p className="text-slate-400 text-sm mb-1">Total Claimed Amount</p>
              <p className="text-2xl font-black text-white">${safe(leakage.totalClaimedAmount).toLocaleString(undefined, { maximumFractionDigits: 0 })}</p>
            </div>
            <div className="bg-slate-900/50 border border-red-500/20 rounded-3xl p-6">
              <p className="text-red-400 text-sm mb-1">Leakage / Overpayment</p>
              <p className="text-2xl font-black text-red-400">${safe(leakage.leakageAmount).toLocaleString(undefined, { maximumFractionDigits: 0 })}</p>
              <p className="text-xs text-slate-500 mt-1">{leakageRate.toFixed(2)}% of total</p>
            </div>
            <div className="bg-slate-900/50 border border-amber-500/20 rounded-3xl p-6">
              <p className="text-amber-400 text-sm mb-1">Amount Mismatch Count</p>
              <p className="text-2xl font-black text-amber-400">{safe(leakage.amountMismatchCount)}</p>
              <p className="text-xs text-slate-500 mt-1">Claims with billing discrepancies</p>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};

// ── Stat Card ─────────────────────────────────────────────────────────────────
const StatCard = ({ title, value, change, isPositive, icon: Icon, color = 'blue' }) => {
  const colors = {
    blue:    'text-blue-500 bg-blue-500/10',
    emerald: 'text-emerald-500 bg-emerald-500/10',
    amber:   'text-amber-500 bg-amber-500/10',
    red:     'text-red-500 bg-red-500/10',
  };

  return (
    <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-6 transition-all hover:scale-[1.02] hover:border-slate-700 cursor-default">
      <div className="flex justify-between items-start mb-4">
        <div className={`p-3 rounded-2xl ${colors[color] || colors.blue}`}>
          <Icon size={24} />
        </div>
        <div className={`flex items-center text-xs font-bold px-2 py-1 rounded-lg ${isPositive ? 'text-emerald-400 bg-emerald-400/10' : 'text-red-400 bg-red-400/10'}`}>
          {isPositive ? <ArrowUpRight size={14} className="mr-1" /> : <ArrowDownRight size={14} className="mr-1" />}
          {change}
        </div>
      </div>
      <div>
        <p className="text-slate-400 text-sm font-medium">{title}</p>
        <p className="text-3xl font-black text-white mt-1">{value}</p>
      </div>
    </div>
  );
};

export default CarrierAnalytics;
