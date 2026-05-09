import React, { useState, useEffect } from 'react';
import { useEnterpriseEvents } from '../../hooks/useEnterpriseEvents';
import { useDemoData } from '../../context/DemoDataProvider';
import { Package, Users, Shield, Maximize2, Calculator, Activity, Heart, ShieldCheck, DollarSign, TrendingUp, AlertTriangle } from 'lucide-react';

const PLANS = [
  { id: 'FAM-FLT', name: 'Family Floater', cov: 500000, act: 145020, prem: 1200, app: 94.2, rsk: 'Low', util: 68, p_hist: [40, 55, 45, 60, 48, 70], type: 'Health', minAge: 0, maxAge: 65 },
  { id: 'CRIT-ILL', name: 'Critical Illness', cov: 1000000, act: 42400, prem: 850, app: 88.5, rsk: 'High', util: 82, p_hist: [80, 75, 90, 85, 95, 88], type: 'Health', minAge: 18, maxAge: 60 },
  { id: 'HOSP-INS', name: 'Hospitalization Insurance', cov: 250000, act: 289000, prem: 600, app: 96.0, rsk: 'Medium', util: 74, p_hist: [50, 52, 55, 58, 60, 59], type: 'Health', minAge: 0, maxAge: 70 },
  { id: 'ADD-PRO', name: 'AD&D', cov: 750000, act: 115600, prem: 300, app: 99.1, rsk: 'Low', util: 22, p_hist: [10, 15, 12, 18, 14, 20], type: 'Accident', minAge: 18, maxAge: 65 },
  { id: 'RECUP-CARE', name: 'Recuperative Care', cov: 100000, act: 88900, prem: 400, app: 91.2, rsk: 'Medium', util: 55, p_hist: [30, 35, 32, 40, 38, 42], type: 'Recovery', minAge: 18, maxAge: 70 },
  { id: 'LIFE-INS', name: 'Life Insurance', cov: 2000000, act: 512000, prem: 1500, app: 85.0, rsk: 'High', util: 88, p_hist: [90, 92, 95, 94, 98, 96], type: 'Life', minAge: 18, maxAge: 60 },
  { id: 'MAT-COV', name: 'Maternity Cover', cov: 150000, act: 112000, prem: 550, app: 97.5, rsk: 'Low', util: 62, p_hist: [60, 62, 58, 65, 63, 68], type: 'Health', minAge: 18, maxAge: 45 },
  { id: 'SNR-CIT', name: 'Senior Citizen Plan', cov: 300000, act: 467000, prem: 1200, app: 98.0, rsk: 'High', util: 35, p_hist: [20, 22, 25, 24, 28, 26], type: 'Health', minAge: 60, maxAge: 85 },
];

export default function PolicyPerformance() {
  const { state } = useEnterpriseEvents();
  const { liveClaims, fraudSignals } = state;
  const { insurancePlans, planSummary } = useDemoData();

  const [expanded, setExpanded] = useState(null);
  const [simAge, setSimAge] = useState(30);
  const [simCoverageMult, setSimCoverageMult] = useState(1);
  const [simRiders, setSimRiders] = useState({ dental: false, vision: false, international: false });

  const totalLiveClaims = Object.values(liveClaims).reduce((sum, arr) => sum + arr.length, 0);

  // Merge live event data into demo plans for dynamic utilization
  const plans = insurancePlans.map(p => {
    const liveBoost = totalLiveClaims * 0.5;
    const activeFraud = fraudSignals.some(f => f.desc && f.desc.toLowerCase().includes(p.shortName.toLowerCase()));
    return {
      ...p,
      util: Math.min(100, p.utilization + liveBoost),
      rsk: activeFraud ? 'Critical' : p.riskLevel,
      prem: p.premium || 0,
      cov: p.coverage || 0,
      app: p.approvalRatio || 0,
      act: p.activeSubscribers || 0,
      p_hist: p.trend || [],
    };
  });

  const calculateSimulatedPremium = (basePrem) => {
    let prem = basePrem * simCoverageMult;
    if (simAge > 50) prem *= 1.4;
    if (simAge > 65) prem *= 1.8;
    if (simRiders.dental) prem += 150;
    if (simRiders.vision) prem += 80;
    if (simRiders.international) prem += 300;
    return prem;
  };

  return (
    <div className="min-h-screen bg-[#0f172a] text-slate-200 p-8 font-sans selection:bg-indigo-500/30">
      
      {/* Header - Glassmorphism */}
      <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-3xl p-8 mb-8 flex justify-between items-end shadow-2xl relative overflow-hidden">
        <div className="absolute -right-20 -top-40 w-96 h-96 bg-indigo-600/20 rounded-full blur-[100px] pointer-events-none" />
        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-2">
            <Package size={28} className="text-indigo-400" />
            <h1 className="text-3xl font-black text-white tracking-tight">Insurance Product Marketplace</h1>
          </div>
          <p className="text-slate-400 font-medium">Enterprise Product Catalog & Interactive Configurator</p>
        </div>
        <div className="relative z-10 flex gap-4">
          <div className="bg-indigo-500/10 border border-indigo-500/30 px-4 py-2 rounded-xl flex flex-col justify-center">
            <span className="text-[10px] text-indigo-300 font-bold uppercase tracking-widest mb-1">Global Pipeline Utilization Impact</span>
            <div className="flex items-center gap-2">
              <Activity size={14} className={totalLiveClaims > 10 ? 'text-red-400 animate-pulse' : 'text-emerald-400'} />
              <span className="text-sm font-bold text-white">{totalLiveClaims} Active Enterprise Claims processing</span>
            </div>
          </div>
        </div>
      </div>

      {/* Product Gallery Grid */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6 relative">
        {plans.map((p) => (
          <div key={p.id} 
            className={`group bg-slate-800/40 backdrop-blur-md border border-slate-700/50 rounded-3xl overflow-hidden transition-all duration-500 hover:border-indigo-500/50 hover:shadow-[0_10px_40px_rgba(79,70,229,0.15)] ${expanded === p.id ? 'xl:col-span-2 row-span-2' : ''}`}>
            
            <div className="p-8">
              {/* Card Header */}
              <div className="flex justify-between items-start mb-8">
                <div>
                  <div className="flex items-center gap-3 mb-1">
                    <h2 className="text-2xl font-bold text-white group-hover:text-indigo-300 transition-colors">{p.name}</h2>
                    <span className="bg-slate-700/50 text-slate-300 text-xs px-2 py-0.5 rounded-full font-bold">{p.type}</span>
                  </div>
                  <div className="text-sm font-semibold text-slate-500 uppercase tracking-widest">{p.id}</div>
                </div>
                <button 
                  onClick={() => setExpanded(expanded === p.id ? null : p.id)}
                  className={`p-2 rounded-xl transition-all ${expanded === p.id ? 'bg-indigo-500 text-white shadow-[0_0_15px_rgba(79,70,229,0.5)]' : 'bg-slate-700/50 hover:bg-indigo-500/20 text-slate-400 hover:text-indigo-300'}`}>
                  {expanded === p.id ? <Calculator size={18} /> : <Maximize2 size={18} />}
                </button>
              </div>

              {/* Top Core Metrics */}
              <div className="grid grid-cols-3 gap-6 mb-8">
                <div>
                  <div className="text-sm font-semibold text-slate-400 mb-1 flex items-center gap-2"><Shield size={16}/> Base Coverage</div>
                  <div className="text-xl font-black text-white">${p.cov?.toLocaleString()}</div>
                </div>
                <div>
                  <div className="text-sm font-semibold text-slate-400 mb-1 flex items-center gap-2"><DollarSign size={16}/> Base Premium/mo</div>
                  <div className="text-xl font-black text-emerald-400">${p.prem?.toLocaleString()}</div>
                </div>
                <div>
                  <div className="text-sm font-semibold text-slate-400 mb-1 flex items-center gap-2"><Users size={16}/> Active Policies</div>
                  <div className="text-xl font-black text-blue-400">{p.act?.toLocaleString()}</div>
                </div>
              </div>

              {/* Progress Bars */}
              <div className="grid grid-cols-2 gap-8">
                <div>
                  <div className="flex justify-between text-sm font-semibold mb-2">
                    <span className="text-slate-400">Live Utilization Score</span>
                    <span className={p.util > 80 ? 'text-red-400 font-bold' : 'text-indigo-400 font-bold'}>{p.util.toFixed(1)}/100</span>
                  </div>
                  <div className="h-2 bg-slate-900 rounded-full overflow-hidden">
                    <div className={`h-full transition-all duration-1000 ${p.util > 80 ? 'bg-red-500' : 'bg-indigo-500'}`} style={{width: `${p.util}%`}} />
                  </div>
                </div>
                <div>
                  <div className="flex justify-between text-sm font-semibold mb-2">
                    <span className="text-slate-400">Approval Ratio</span>
                    <span className="text-emerald-400 font-bold">{p.app}%</span>
                  </div>
                  <div className="h-2 bg-slate-900 rounded-full overflow-hidden">
                    <div className="h-full bg-emerald-500 transition-all duration-1000" style={{width: `${p.app}%`}} />
                  </div>
                </div>
              </div>

              {/* Expanded Interactive Configurator Area */}
              {expanded === p.id && (
                <div className="mt-8 pt-8 border-t border-slate-700/50 animate-[fadeIn_0.5s_ease-out]">
                  <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    
                    {/* Interactive Premium Simulator */}
                    <div className="bg-slate-900/50 rounded-2xl p-6 border border-slate-700/50">
                      <div className="flex items-center gap-2 mb-6">
                        <Calculator size={20} className="text-indigo-400" />
                        <h4 className="text-white text-lg font-bold">Premium Configurator</h4>
                      </div>
                      
                      <div className="space-y-6">
                        {/* Coverage Multiplier */}
                        <div>
                          <div className="flex justify-between text-sm font-semibold mb-2 text-slate-300">
                            <span>Coverage Limit</span>
                            <span className="text-white">${(p.cov * simCoverageMult).toLocaleString()}</span>
                          </div>
                          <input type="range" min="1" max="5" step="0.5" value={simCoverageMult} onChange={(e) => setSimCoverageMult(parseFloat(e.target.value))} className="w-full accent-indigo-500" />
                        </div>

                        {/* Age Band */}
                        <div>
                          <div className="flex justify-between text-sm font-semibold mb-2 text-slate-300">
                            <span>Primary Insured Age</span>
                            <span className="text-white">{simAge} years</span>
                          </div>
                          <input type="range" min={p.minAge} max={p.maxAge} value={simAge} onChange={(e) => setSimAge(parseInt(e.target.value))} className="w-full accent-indigo-500" />
                        </div>

                        {/* Rider Selection */}
                        <div>
                          <div className="text-sm font-semibold mb-3 text-slate-300">Optional Riders</div>
                          <div className="flex flex-col gap-2">
                            <label className="flex items-center gap-3 cursor-pointer group">
                              <input type="checkbox" checked={simRiders.dental} onChange={(e) => setSimRiders(r => ({...r, dental: e.target.checked}))} className="w-4 h-4 rounded text-indigo-500 bg-slate-800 border-slate-600 focus:ring-indigo-500" />
                              <span className="text-sm text-slate-400 group-hover:text-slate-200">Dental & Vision Core (+$150)</span>
                            </label>
                            <label className="flex items-center gap-3 cursor-pointer group">
                              <input type="checkbox" checked={simRiders.international} onChange={(e) => setSimRiders(r => ({...r, international: e.target.checked}))} className="w-4 h-4 rounded text-indigo-500 bg-slate-800 border-slate-600 focus:ring-indigo-500" />
                              <span className="text-sm text-slate-400 group-hover:text-slate-200">Global Coverage Roaming (+$300)</span>
                            </label>
                          </div>
                        </div>

                        {/* Configured Price Result */}
                        <div className="mt-6 pt-4 border-t border-slate-700 flex justify-between items-center">
                          <span className="text-slate-400 font-semibold">Configured Monthly Premium</span>
                          <span className="text-3xl font-black text-emerald-400">${calculateSimulatedPremium(p.prem).toLocaleString()}</span>
                        </div>
                      </div>
                    </div>

                    {/* AI Assessment & Family Matrix */}
                    <div className="flex flex-col gap-6">
                      
                      {/* Family Coverage Matrix */}
                      <div className="bg-slate-900/50 rounded-2xl p-6 border border-slate-700/50">
                        <h4 className="text-white text-lg font-bold mb-4 flex items-center gap-2"><Heart size={20} className="text-red-400"/> Dependent Matrix Rules</h4>
                        <div className="space-y-3">
                          <div className="flex items-start gap-3">
                            <ShieldCheck size={16} className="text-emerald-400 mt-0.5 shrink-0" />
                            <p className="text-sm text-slate-400 leading-relaxed">Spouse inclusion permitted without medical checkup for ages under 45.</p>
                          </div>
                          <div className="flex items-start gap-3">
                            <ShieldCheck size={16} className="text-emerald-400 mt-0.5 shrink-0" />
                            <p className="text-sm text-slate-400 leading-relaxed">Up to 3 children covered under base premium until age 21 (25 if full-time student).</p>
                          </div>
                          <div className="flex items-start gap-3">
                            <ShieldCheck size={16} className="text-emerald-400 mt-0.5 shrink-0" />
                            <p className="text-sm text-slate-400 leading-relaxed">Pre-existing condition waiting period: 24 months standard.</p>
                          </div>
                        </div>
                      </div>

                      {/* AI Plan Insight */}
                      <div className="bg-indigo-500/10 rounded-2xl p-6 border border-indigo-500/30 flex-1">
                        <h4 className="text-indigo-300 text-sm font-bold uppercase tracking-widest mb-3">AI Plan Insight</h4>
                        <p className="text-slate-300 text-sm leading-relaxed mb-4">
                          Based on current enterprise underwriting trends, {p.name} shows a highly favorable risk-to-premium ratio for the {p.minAge}-{Math.min(p.maxAge, p.minAge+15)} age demographic.
                        </p>
                        <div className="flex justify-between items-center bg-black/20 rounded-lg p-3">
                          <span className="text-slate-400 text-xs font-bold">CURRENT EXPOSURE RISK</span>
                          <span className={`text-xs font-black px-2 py-1 rounded ${p.rsk === 'Low' ? 'bg-emerald-500/20 text-emerald-400' : p.rsk === 'High' ? 'bg-red-500/20 text-red-400' : p.rsk === 'Critical' ? 'bg-red-500 text-white animate-pulse' : 'bg-amber-500/20 text-amber-400'}`}>
                            {p.rsk.toUpperCase()}
                          </span>
                        </div>
                      </div>

                    </div>
                  </div>
                </div>
              )}

            </div>
          </div>
        ))}
      </div>

      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(-10px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  );
}
