import React, { useState, useEffect } from 'react';
import { useEnterpriseEvents } from '../../hooks/useEnterpriseEvents';
import { useDemoData } from '../../context/DemoDataProvider';
import { DollarSign, TrendingUp, TrendingDown, RefreshCw, BarChart2, Briefcase, ChevronRight, AlertTriangle } from 'lucide-react';

export default function FinancialAnalysis() {
  const { state } = useEnterpriseEvents();
  const { liveClaims, metrics, financialAnomalies, events, slaBreaches } = state;
  const { settlementTicker: demoTicker, financialMetrics } = useDemoData();

  // Seed ticker with demo data immediately, then merge live events
  const [ticker, setTicker] = useState(demoTicker);

  useEffect(() => {
    setTicker(demoTicker);
  }, [demoTicker]);

  useEffect(() => {
    const interval = setInterval(() => {
      setTicker(prev => {
        const amt = Math.floor(Math.random() * 20000) + 5000;
        const isPenalty = Math.random() > 0.9 && slaBreaches.length > 0;
        const st = isPenalty ? 'PENALTY' : (Math.random() > 0.8 ? 'BLOCKED' : 'CLEARED');
        const hospitals = ['Apollo Hospitals', 'Fortis Healthcare', 'Manipal Hospital', 'KIMS Hospital'];
        const policies = ['Family Floater', 'Hospitalization', 'Corporate Group', 'Accident'];
        const now = new Date();
        const t = `${now.getHours().toString().padStart(2,'0')}:${now.getMinutes().toString().padStart(2,'0')}:${now.getSeconds().toString().padStart(2,'0')}`;
        return [{ id: `T-${Math.floor(Math.random()*9000)+1000}`, amount: isPenalty ? -amt : amt, status: st, hospital: hospitals[Math.floor(Math.random()*hospitals.length)], policy: policies[Math.floor(Math.random()*policies.length)], time: t }, ...prev].slice(0, 20);
      });
    }, 2500);
    return () => clearInterval(interval);
  }, [slaBreaches]);

  const projectedDrain = liveClaims.approval.reduce((sum, c) => sum + c.val, 0) + financialMetrics.projectedDrain4h;
  const drainVelocity = projectedDrain > 5000000 ? 'HIGH' : projectedDrain > 2000000 ? 'MEDIUM' : 'OPTIMAL';

  return (
    <div className="min-h-screen bg-[#000814] text-[#a0aec0] font-mono flex flex-col p-2 space-y-2">
      
      {/* Top Banner Ticker */}
      <div className="bg-[#00122a] border-y border-[#1e3a8a] py-1 flex items-center overflow-hidden whitespace-nowrap shrink-0 relative">
        <div className="bg-[#ffd700] text-black text-[10px] font-black px-3 py-1 absolute left-0 z-10 flex items-center h-full">
          LIVE SETTLEMENTS
        </div>
        <div className="ml-36 flex gap-6 animate-[ticker_20s_linear_infinite]">
          {ticker.map((t, i) => (
            <div key={i} className="flex items-center gap-2 text-xs">
              <span className="text-white font-bold">{t.id}</span>
              <span className={t.status === 'CLEARED' ? 'text-[#00ff66]' : t.status === 'PENALTY' ? 'text-[#ff003c]' : t.status === 'BLOCKED' ? 'text-amber-500' : 'text-[#ffd700]'}>
                {t.amount > 0 ? '+' : ''}${t.amount.toLocaleString()}
              </span>
              <span className="text-[#a0aec0]/50 text-[10px]">{t.status}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Main Grid - Bloomberg Style */}
      <div className="flex-1 grid grid-cols-12 gap-2">
        
        {/* Left Column (Wide) */}
        <div className="col-span-8 flex flex-col gap-2">
          
          {/* Top Metrics Row */}
          <div className="grid grid-cols-4 gap-2 shrink-0">
            <div className="bg-[#00122a] border border-[#1e3a8a] p-3 flex flex-col justify-between">
              <span className="text-[9px] font-bold text-[#4a5568] tracking-widest">LIQUIDITY RESERVES</span>
              <span className="text-2xl font-black text-white">${(metrics.reserves/1000000).toFixed(2)}M</span>
            </div>
            <div className="bg-[#00122a] border border-[#1e3a8a] p-3 flex flex-col justify-between">
              <span className="text-[9px] font-bold text-[#4a5568] tracking-widest">PROJECTED 4H DRAIN</span>
              <div className="flex items-end gap-2">
                <span className="text-2xl font-black text-[#ffd700]">${(projectedDrain/1000).toFixed(1)}k</span>
                <span className={`text-[10px] font-bold mb-1 ${drainVelocity === 'HIGH' ? 'text-[#ff003c]' : 'text-slate-500'}`}>{drainVelocity}</span>
              </div>
            </div>
            <div className="bg-[#00122a] border border-[#1e3a8a] p-3 flex flex-col justify-between">
              <span className="text-[9px] font-bold text-[#4a5568] tracking-widest">SLA BREACH PENALTIES</span>
              <div className="flex items-center gap-2 text-2xl font-black text-[#ff003c]">
                {metrics.breachPenalties > 0 && <AlertTriangle size={18} className="animate-pulse" />}
                ${(metrics.breachPenalties/1000).toFixed(1)}k
              </div>
            </div>
            <div className="bg-[#00122a] border border-[#1e3a8a] p-3 flex flex-col justify-between">
              <span className="text-[9px] font-bold text-[#4a5568] tracking-widest">CARRIER SAVINGS (YTD)</span>
              <span className="text-2xl font-black text-[#00ff66]">${(metrics.savings/1000000).toFixed(2)}M</span>
            </div>
          </div>

          {/* Global Financial Event Stream */}
          <div className="flex-1 bg-[#00122a] border border-[#1e3a8a] flex flex-col overflow-hidden relative">
            <div className="bg-[#1e3a8a] text-white text-[10px] font-bold px-3 py-1.5 flex items-center justify-between shrink-0 z-10">
              <span>UNIFIED FINANCIAL INTELLIGENCE LEDGER</span>
              <span className="flex items-center gap-2"><span className="w-1.5 h-1.5 rounded-full bg-[#00ff66] animate-pulse"></span> LIVE</span>
            </div>
            <div className="flex-1 overflow-y-auto custom-scrollbar z-10 relative">
              <table className="w-full text-left text-[10px] whitespace-nowrap">
                <thead className="text-[#a0aec0] bg-[#000814] border-b border-[#1e3a8a] sticky top-0">
                  <tr>
                    <th className="font-normal px-3 py-1 w-24">TIME (UTC)</th>
                    <th className="font-normal px-3 py-1 w-24">MODULE</th>
                    <th className="font-normal px-3 py-1">EVENT CONTEXT</th>
                    <th className="font-normal px-3 py-1 text-right">IMPACT / TYPE</th>
                  </tr>
                </thead>
                <tbody>
                  {events.filter(e => e.module === 'TREASURY' || e.module === 'COMPLIANCE').map((e, i) => (
                    <tr key={e.id} className={`${i % 2 === 0 ? 'bg-[#00122a]' : 'bg-[#001835]'} border-b border-[#000814] hover:bg-[#1e3a8a]/30 transition-colors`}>
                      <td className="px-3 py-1.5 text-[#4a5568]">{e.timestamp.substring(11,19)}</td>
                      <td className={`px-3 py-1.5 font-bold ${e.module === 'COMPLIANCE' ? 'text-[#ff003c]' : 'text-indigo-400'}`}>{e.module}</td>
                      <td className={`px-3 py-1.5 ${e.type === 'CRITICAL' ? 'text-[#ff003c] font-bold' : e.type === 'WARNING' ? 'text-[#ffd700]' : 'text-slate-300'}`}>{e.message}</td>
                      <td className={`px-3 py-1.5 text-right font-bold ${e.type === 'CRITICAL' ? 'text-[#ff003c]' : 'text-white'}`}>{e.type}</td>
                    </tr>
                  ))}
                  {events.filter(e => e.module === 'TREASURY' || e.module === 'COMPLIANCE').length === 0 && (
                    <tr><td colSpan="4" className="text-center py-4 text-slate-600">Awaiting financial streams...</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

        </div>

        {/* Right Column (Narrow) */}
        <div className="col-span-4 flex flex-col gap-2">
          
          {/* Anomaly Engine */}
          <div className="flex-1 bg-[#00122a] border border-[#1e3a8a] flex flex-col relative overflow-hidden">
            <div className="bg-[#ff003c]/20 border-b border-[#ff003c]/50 text-[#ff003c] text-[10px] font-bold px-3 py-1.5 shrink-0 flex items-center justify-between">
              <span>FINANCIAL ANOMALY ENGINE</span>
              {financialAnomalies.length > 0 && <AlertTriangle size={12} className="animate-pulse" />}
            </div>
            <div className="p-2 space-y-2 overflow-y-auto z-10">
              {financialAnomalies.map((a) => (
                <div key={a.id} className="border border-[#1e3a8a] p-2 bg-[#000814] flex flex-col gap-1 hover:border-[#ff003c]/50 transition-colors">
                  <div className="flex justify-between items-center text-[9px] font-bold">
                    <span className="text-white">{a.id}</span>
                    <span className={a.risk === 'HIGH' ? 'text-[#ff003c] bg-[#ff003c]/10 px-1' : a.risk === 'MED' ? 'text-[#ffd700] bg-[#ffd700]/10 px-1' : 'text-[#00ff66] bg-[#00ff66]/10 px-1'}>{a.risk} RISK</span>
                  </div>
                  <div className="text-[10px] text-[#e2e8f0]">{a.desc}</div>
                  <div className="flex justify-between items-end mt-1">
                    <span className="text-[9px] text-slate-500">{a.time.substring(11,19)}</span>
                    <span className={`text-xs font-black ${a.var.startsWith('+') ? 'text-[#ff003c]' : 'text-[#00ff66]'}`}>{a.var}</span>
                  </div>
                </div>
              ))}
              {financialAnomalies.length === 0 && (
                <div className="text-[10px] text-slate-600 text-center mt-4">NO ANOMALIES DETECTED</div>
              )}
            </div>
            {/* Scanline overlay */}
            <div className="absolute inset-0 pointer-events-none bg-[linear-gradient(rgba(0,0,0,0)_50%,rgba(0,0,0,0.4)_50%)] bg-[length:100%_4px] z-0"></div>
          </div>

          {/* Quick Actions / Routing */}
          <div className="h-32 bg-[#00122a] border border-[#1e3a8a] flex flex-col">
            <div className="bg-[#1e3a8a] text-white text-[10px] font-bold px-3 py-1.5 shrink-0">
              TREASURY COMMANDS
            </div>
            <div className="p-2 grid grid-cols-2 gap-2 h-full">
              <button className="bg-[#001835] hover:bg-[#1e3a8a] border border-[#1e3a8a] text-[10px] text-white font-bold flex items-center justify-center transition-colors">
                AUTHORIZE BATCH
              </button>
              <button className="bg-[#ff003c]/10 hover:bg-[#ff003c]/30 border border-[#ff003c]/30 text-[10px] text-[#ff003c] font-bold flex items-center justify-center transition-colors">
                HALT PAYOUTS
              </button>
              <button className="bg-[#001835] hover:bg-[#1e3a8a] border border-[#1e3a8a] text-[10px] text-white font-bold flex items-center justify-center transition-colors col-span-2">
                GENERATE LEDGER EXPORT
              </button>
            </div>
          </div>

        </div>
      </div>

      <style>{`
        @keyframes ticker {
          0% { transform: translateX(100%); }
          100% { transform: translateX(-100%); }
        }
        .custom-scrollbar::-webkit-scrollbar { width: 4px; height: 4px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: rgba(30, 58, 138, 0.2); }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(30, 58, 138, 0.8); }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #ffd700; }
      `}</style>
    </div>
  );
}
