import React, { useState } from 'react';
import { Map, AlertTriangle, Crosshair, Shield, TrendingUp, Search, Eye } from 'lucide-react';
import { useDemoData } from '../../context/DemoDataProvider';

const RISK_REGIONS = [
  { name: 'Delhi NCR', riskLevel: 86, hotspots: 12, trend: '+5%', city: 'Delhi', cases: 28 },
  { name: 'Mumbai Metro', riskLevel: 74, hotspots: 9, trend: '+3%', city: 'Mumbai', cases: 22 },
  { name: 'Hyderabad East', riskLevel: 91, hotspots: 18, trend: '+12%', city: 'Hyderabad', cases: 34 },
  { name: 'Kolkata South', riskLevel: 62, hotspots: 6, trend: '+1%', city: 'Kolkata', cases: 14 },
  { name: 'Chennai North', riskLevel: 48, hotspots: 4, trend: '-2%', city: 'Chennai', cases: 9 },
  { name: 'Bangalore West', riskLevel: 38, hotspots: 3, trend: '-4%', city: 'Bangalore', cases: 7 },
  { name: 'Pune Central', riskLevel: 29, hotspots: 2, trend: '0%', city: 'Pune', cases: 5 },
  { name: 'Ahmedabad', riskLevel: 18, hotspots: 1, trend: '-1%', city: 'Ahmedabad', cases: 3 },
];

const STATUS_BADGE = {
  INVESTIGATING: 'bg-red-500/20 text-red-400 border-red-500/30',
  CONFIRMED: 'bg-red-800/30 text-red-300 border-red-700/40',
  WATCHLIST: 'bg-amber-500/20 text-amber-400 border-amber-500/30',
  REVIEWING: 'bg-blue-500/20 text-blue-400 border-blue-500/30',
};

export default function FraudHeatmaps() {
  const { fraudSignals, hospitals } = useDemoData();
  const [search, setSearch] = useState('');
  const [selectedRegion, setSelectedRegion] = useState(null);

  const filtered = fraudSignals.filter(f =>
    !search || f.hospital.toLowerCase().includes(search.toLowerCase()) || f.claimRef.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-black text-white flex items-center gap-3">
            <div className="w-10 h-10 bg-red-500/20 border border-red-500/30 rounded-xl flex items-center justify-center">
              <Map size={20} className="text-red-400" />
            </div>
            Fraud Intelligence Heatmaps
          </h1>
          <p className="text-slate-400 text-sm mt-1">Geographic fraud clustering · Provider risk scoring · Real-time signal detection</p>
        </div>
        <div className="flex items-center gap-3 text-xs">
          <span className="px-3 py-1.5 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 font-bold animate-pulse">
            {fraudSignals.filter(f => f.status === 'INVESTIGATING').length} LIVE INVESTIGATIONS
          </span>
          <span className="px-3 py-1.5 bg-amber-500/10 border border-amber-500/30 rounded-lg text-amber-400 font-bold">
            {fraudSignals.filter(f => f.status === 'WATCHLIST').length} WATCHLIST
          </span>
        </div>
      </div>

      <div className="grid grid-cols-3 gap-6">
        {/* Geographic Risk Map + Region List */}
        <div className="col-span-1 space-y-3">
          <h3 className="text-slate-400 text-xs font-bold uppercase tracking-widest">Regional Risk Scores</h3>
          {RISK_REGIONS.map((r, i) => (
            <div
              key={i}
              onClick={() => setSelectedRegion(r.name === selectedRegion ? null : r.name)}
              className={`border rounded-xl p-4 cursor-pointer transition-all ${
                selectedRegion === r.name ? 'border-red-500/60 bg-red-500/10' : 'border-slate-800 bg-slate-900/60 hover:border-slate-600'
              }`}
            >
              <div className="flex justify-between items-start mb-2">
                <div>
                  <div className="text-white font-bold text-sm">{r.name}</div>
                  <div className="text-slate-500 text-xs mt-0.5">{r.cases} active cases</div>
                </div>
                <div className="text-right">
                  <div className={`text-lg font-black ${r.riskLevel > 80 ? 'text-red-400' : r.riskLevel > 50 ? 'text-amber-400' : 'text-emerald-400'}`}>
                    {r.riskLevel}
                  </div>
                  <div className={`text-xs font-bold ${r.trend.startsWith('+') ? 'text-red-400' : 'text-emerald-400'}`}>{r.trend} WoW</div>
                </div>
              </div>
              <div className="w-full bg-slate-800 rounded-full h-1.5">
                <div
                  className={`h-full rounded-full ${r.riskLevel > 80 ? 'bg-red-500' : r.riskLevel > 50 ? 'bg-amber-500' : 'bg-emerald-500'}`}
                  style={{ width: `${r.riskLevel}%` }}
                />
              </div>
            </div>
          ))}
        </div>

        {/* Main Fraud Signal Table */}
        <div className="col-span-2 space-y-4">
          {/* Search */}
          <div className="relative">
            <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
            <input
              type="text"
              placeholder="Search by hospital, claim reference..."
              value={search}
              onChange={e => setSearch(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700 rounded-xl pl-9 pr-4 py-2.5 text-sm text-slate-200 placeholder-slate-600 focus:outline-none focus:border-red-500/60"
            />
          </div>

          {/* Fraud Signals */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl overflow-hidden">
            <div className="px-5 py-3 border-b border-slate-800 flex justify-between items-center">
              <h3 className="text-white font-bold text-sm">Live Fraud Signal Feed</h3>
              <span className="text-slate-500 text-xs">{filtered.length} signals</span>
            </div>
            <div className="divide-y divide-slate-800/60">
              <div className="grid grid-cols-[2fr_1fr_1fr_1fr_auto] gap-3 px-5 py-2.5 text-xs font-bold text-slate-600 uppercase tracking-widest">
                <span>Hospital / Provider</span><span>Type</span><span>Claim Value</span><span>Fraud Score</span><span>Status</span>
              </div>
              {filtered.map(f => (
                <div key={f.id} className="grid grid-cols-[2fr_1fr_1fr_1fr_auto] gap-3 px-5 py-4 items-center hover:bg-slate-800/40 transition-colors">
                  <div>
                    <div className="text-white font-semibold text-sm">{f.hospital}</div>
                    <div className="text-slate-500 text-xs mt-0.5">{f.id} · {f.claimRef}</div>
                    <div className="text-slate-600 text-xs mt-1 font-mono">{f.flags.split(',').join(' · ')}</div>
                  </div>
                  <span className="text-slate-400 text-xs">{f.type}</span>
                  <span className="text-white font-bold text-sm">₹{f.amount.toLocaleString()}</span>
                  <div className="flex items-center gap-2">
                    <div className="w-16 bg-slate-800 rounded-full h-1.5">
                      <div className={`h-full rounded-full ${f.score > 0.8 ? 'bg-red-500' : f.score > 0.6 ? 'bg-amber-500' : 'bg-yellow-400'}`} style={{ width: `${f.score * 100}%` }} />
                    </div>
                    <span className={`text-xs font-bold ${f.score > 0.8 ? 'text-red-400' : f.score > 0.6 ? 'text-amber-400' : 'text-yellow-400'}`}>{(f.score * 100).toFixed(0)}%</span>
                  </div>
                  <span className={`px-2 py-1 rounded-lg border text-[10px] font-bold whitespace-nowrap ${STATUS_BADGE[f.status]}`}>{f.status}</span>
                </div>
              ))}
            </div>
          </div>

          {/* High Risk Hospitals */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5">
            <h3 className="text-white font-bold text-sm mb-4 flex items-center gap-2"><Shield size={14} className="text-amber-400" /> Provider Risk Rankings</h3>
            <div className="grid grid-cols-2 gap-3">
              {hospitals.slice(0, 6).map((h, i) => (
                <div key={i} className={`border rounded-xl p-3 ${h.fraudRate > 2 ? 'border-amber-500/30 bg-amber-500/5' : 'border-slate-700/50 bg-slate-800/30'}`}>
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="text-white font-semibold text-xs">{h.name}</div>
                      <div className="text-slate-500 text-xs">{h.city} · Tier {h.tier}</div>
                    </div>
                    <span className={`text-xs font-bold ${h.fraudRate > 2 ? 'text-amber-400' : 'text-emerald-400'}`}>{h.fraudRate}% fraud</span>
                  </div>
                  <div className="mt-2 flex gap-3 text-xs text-slate-500">
                    <span>{h.claimsProcessed.toLocaleString()} claims</span>
                    <span>{h.approvalRate}% approval</span>
                    <span>⭐ {h.rating}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
