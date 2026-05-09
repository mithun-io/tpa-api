import React, { useState, useEffect } from 'react';
import { ShieldCheck, FileText, AlertTriangle, CheckCircle, Clock, TrendingDown, Download, Filter, BarChart2, Activity } from 'lucide-react';

const COMPLIANCE_REPORTS = [
  { id: 'IRDA-2026-Q1', name: 'IRDAI Quarterly Regulatory Filing', period: 'Q1 2026', status: 'SUBMITTED', due: '2026-04-30', score: 98.2, type: 'Regulatory' },
  { id: 'SLA-MAY-01', name: 'SLA Adherence — May Week 1', period: 'May 1–7 2026', status: 'PASSED', due: '2026-05-08', score: 96.7, type: 'SLA' },
  { id: 'FRAUD-APR', name: 'Anti-Fraud & Leakage Audit', period: 'April 2026', status: 'PASSED', due: '2026-05-05', score: 99.1, type: 'Audit' },
  { id: 'RESERVE-Q1', name: 'Reserve Adequacy Declaration', period: 'Q1 2026', status: 'REVIEW', due: '2026-05-15', score: 87.4, type: 'Financial' },
  { id: 'GDPR-2026', name: 'Data Privacy & DPDP Compliance', period: 'FY 2025–26', status: 'OVERDUE', due: '2026-04-25', score: 71.0, type: 'Privacy' },
  { id: 'CLAIMS-TAT', name: 'TAT Compliance — Claims Processing', period: 'May 2026', status: 'PASSED', due: '2026-05-10', score: 94.3, type: 'SLA' },
  { id: 'REINS-DISC', name: 'Reinsurance Disclosure Statement', period: 'Q1 2026', status: 'DRAFT', due: '2026-05-20', score: null, type: 'Regulatory' },
  { id: 'OMBUD-MAR', name: 'Ombudsman Case Report', period: 'March 2026', status: 'SUBMITTED', due: '2026-04-15', score: 100, type: 'Regulatory' },
];

const STATUS_STYLES = {
  SUBMITTED: { bg: 'bg-blue-500/20', text: 'text-blue-400', border: 'border-blue-500/40', icon: CheckCircle },
  PASSED: { bg: 'bg-emerald-500/20', text: 'text-emerald-400', border: 'border-emerald-500/40', icon: CheckCircle },
  REVIEW: { bg: 'bg-amber-500/20', text: 'text-amber-400', border: 'border-amber-500/40', icon: Clock },
  OVERDUE: { bg: 'bg-red-500/20', text: 'text-red-400', border: 'border-red-500/40', icon: AlertTriangle },
  DRAFT: { bg: 'bg-slate-500/20', text: 'text-slate-400', border: 'border-slate-500/40', icon: FileText },
};

const ScoreBar = ({ score }) => {
  if (score === null) return <span className="text-slate-500 text-xs italic">Pending</span>;
  const color = score >= 95 ? 'bg-emerald-500' : score >= 80 ? 'bg-amber-500' : 'bg-red-500';
  return (
    <div className="flex items-center gap-3 w-full">
      <div className="flex-1 bg-slate-900 rounded-full h-1.5">
        <div className={`${color} h-full rounded-full transition-all duration-1000`} style={{ width: `${score}%` }} />
      </div>
      <span className={`text-xs font-bold w-10 text-right ${score >= 95 ? 'text-emerald-400' : score >= 80 ? 'text-amber-400' : 'text-red-400'}`}>{score}%</span>
    </div>
  );
};

export default function ComplianceReports() {
  const [filter, setFilter] = useState('ALL');
  const [liveScore, setLiveScore] = useState(94.1);
  const [overdueCount, setOverdueCount] = useState(1);

  const types = ['ALL', 'Regulatory', 'SLA', 'Audit', 'Financial', 'Privacy'];
  const filtered = filter === 'ALL' ? COMPLIANCE_REPORTS : COMPLIANCE_REPORTS.filter(r => r.type === filter);

  const passedCount = COMPLIANCE_REPORTS.filter(r => r.status === 'PASSED' || r.status === 'SUBMITTED').length;
  const totalCount = COMPLIANCE_REPORTS.length;

  useEffect(() => {
    // Simulate live compliance score drift
    const interval = setInterval(() => {
      setLiveScore(prev => parseFloat((94.0 + Math.random() * 0.5).toFixed(1)));
    }, 4000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="min-h-screen bg-slate-900 text-slate-200 font-sans">
      
      {/* Top Command Bar */}
      <div className="bg-slate-800/80 border-b border-slate-700/60 backdrop-blur-sm px-8 py-5 flex justify-between items-center">
        <div className="flex items-center gap-4">
          <div className="w-10 h-10 bg-indigo-500/20 border border-indigo-500/40 rounded-xl flex items-center justify-center">
            <ShieldCheck size={20} className="text-indigo-400" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-white leading-none">Compliance Reports</h1>
            <p className="text-slate-400 text-sm mt-0.5">Regulatory, SLA & Audit Filing Dashboard</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <button className="flex items-center gap-2 px-4 py-2 bg-slate-700 hover:bg-slate-600 text-slate-200 text-sm font-medium rounded-lg border border-slate-600/60 transition-all">
            <Download size={16} /> Export All
          </button>
          <button className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-bold rounded-lg transition-all shadow-[0_0_15px_rgba(99,102,241,0.3)]">
            <FileText size={16} /> Generate New Report
          </button>
        </div>
      </div>

      <div className="p-8 space-y-8">
        
        {/* KPI Row — completely different from reinsurance export */}
        <div className="grid grid-cols-4 gap-6">
          <div className="bg-slate-800/60 border border-slate-700/50 rounded-2xl p-6 flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <span className="text-slate-400 text-sm font-medium">Overall Compliance Score</span>
              <Activity size={18} className="text-indigo-400" />
            </div>
            <div className="text-4xl font-black text-white">{liveScore}%</div>
            <div className="w-full bg-slate-900 rounded-full h-2">
              <div className="bg-indigo-500 h-full rounded-full transition-all duration-1000" style={{ width: `${liveScore}%` }} />
            </div>
          </div>
          <div className="bg-slate-800/60 border border-slate-700/50 rounded-2xl p-6 flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <span className="text-slate-400 text-sm font-medium">Reports Filed (This Quarter)</span>
              <CheckCircle size={18} className="text-emerald-400" />
            </div>
            <div className="text-4xl font-black text-emerald-400">{passedCount}</div>
            <div className="text-slate-500 text-sm">of {totalCount} total filings</div>
          </div>
          <div className="bg-slate-800/60 border border-red-500/20 rounded-2xl p-6 flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <span className="text-slate-400 text-sm font-medium">Overdue Filings</span>
              <AlertTriangle size={18} className={overdueCount > 0 ? 'text-red-400 animate-pulse' : 'text-slate-500'} />
            </div>
            <div className={`text-4xl font-black ${overdueCount > 0 ? 'text-red-400' : 'text-emerald-400'}`}>{overdueCount}</div>
            <div className="text-slate-500 text-sm">{overdueCount > 0 ? 'Requires immediate action' : 'All filings on track'}</div>
          </div>
          <div className="bg-slate-800/60 border border-slate-700/50 rounded-2xl p-6 flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <span className="text-slate-400 text-sm font-medium">Pending Review</span>
              <Clock size={18} className="text-amber-400" />
            </div>
            <div className="text-4xl font-black text-amber-400">
              {COMPLIANCE_REPORTS.filter(r => r.status === 'REVIEW' || r.status === 'DRAFT').length}
            </div>
            <div className="text-slate-500 text-sm">awaiting sign-off</div>
          </div>
        </div>

        {/* Filter Tabs */}
        <div className="flex items-center gap-2 flex-wrap">
          <span className="flex items-center gap-1.5 text-slate-400 text-sm mr-2"><Filter size={14}/> Filter by type:</span>
          {types.map(t => (
            <button 
              key={t}
              onClick={() => setFilter(t)}
              className={`px-4 py-1.5 rounded-lg text-sm font-semibold transition-all border ${
                filter === t 
                  ? 'bg-indigo-600 text-white border-indigo-500 shadow-[0_0_10px_rgba(99,102,241,0.3)]' 
                  : 'bg-slate-800 text-slate-400 border-slate-700 hover:border-slate-500'
              }`}>
              {t}
            </button>
          ))}
        </div>

        {/* Reports Table — distinct from Reinsurance Export's pipeline table */}
        <div className="bg-slate-800/60 border border-slate-700/50 rounded-2xl overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-700/50 flex justify-between items-center">
            <h2 className="text-white font-bold">Filing Registry</h2>
            <span className="text-slate-500 text-sm">{filtered.length} records</span>
          </div>
          <div className="divide-y divide-slate-700/40">
            {/* Header */}
            <div className="grid grid-cols-[2fr_1fr_1fr_1fr_2fr_auto] gap-4 px-6 py-3 text-xs font-bold text-slate-500 uppercase tracking-widest">
              <span>Report Name</span>
              <span>Type</span>
              <span>Period</span>
              <span>Status</span>
              <span>Compliance Score</span>
              <span>Action</span>
            </div>
            {filtered.map(r => {
              const s = STATUS_STYLES[r.status];
              const StatusIcon = s.icon;
              return (
                <div key={r.id} className="grid grid-cols-[2fr_1fr_1fr_1fr_2fr_auto] gap-4 px-6 py-4 items-center hover:bg-slate-700/30 transition-colors group">
                  <div>
                    <div className="text-white font-semibold text-sm group-hover:text-indigo-300 transition-colors">{r.name}</div>
                    <div className="text-slate-500 text-xs mt-0.5">{r.id} · Due {r.due}</div>
                  </div>
                  <span className="text-slate-400 text-sm">{r.type}</span>
                  <span className="text-slate-400 text-sm">{r.period}</span>
                  <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg border text-xs font-bold w-fit ${s.bg} ${s.text} ${s.border}`}>
                    <StatusIcon size={12} />
                    {r.status}
                  </div>
                  <ScoreBar score={r.score} />
                  <button className="opacity-0 group-hover:opacity-100 transition-opacity px-3 py-1.5 bg-slate-700 hover:bg-indigo-600 text-slate-300 hover:text-white rounded-lg text-xs font-bold flex items-center gap-1.5 whitespace-nowrap">
                    <Download size={12} /> Download
                  </button>
                </div>
              );
            })}
          </div>
        </div>

        {/* Bottom Compliance Timeline */}
        <div className="bg-slate-800/60 border border-slate-700/50 rounded-2xl p-6">
          <h3 className="text-white font-bold mb-6 flex items-center gap-2"><BarChart2 size={18} className="text-indigo-400" /> Upcoming Filing Deadlines</h3>
          <div className="flex gap-4 overflow-x-auto pb-2">
            {COMPLIANCE_REPORTS.filter(r => r.status === 'REVIEW' || r.status === 'DRAFT' || r.status === 'OVERDUE').map(r => {
              const s = STATUS_STYLES[r.status];
              return (
                <div key={r.id} className={`min-w-[220px] border ${s.border} ${s.bg} rounded-xl p-4 flex flex-col gap-2`}>
                  <div className={`text-[10px] font-bold uppercase tracking-widest ${s.text}`}>{r.status}</div>
                  <div className="text-white font-semibold text-sm leading-tight">{r.name}</div>
                  <div className="text-slate-400 text-xs">Due: {r.due}</div>
                  <button className={`mt-2 text-xs font-bold py-1.5 rounded-lg border ${s.border} ${s.text} hover:bg-white/10 transition-colors`}>
                    Take Action →
                  </button>
                </div>
              );
            })}
          </div>
        </div>

      </div>
    </div>
  );
}
