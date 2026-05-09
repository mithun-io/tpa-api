import React, { useMemo } from 'react';
import { useEnterpriseEvents } from '../../hooks/useEnterpriseEvents';
import { Terminal, Activity, Zap, ShieldAlert, Cpu, Network, ArrowRightCircle } from 'lucide-react';

export default function OperationsSLADashboard() {
  const { state, manualRoute } = useEnterpriseEvents();
  const { liveClaims, events, processors } = state;

  // Derive congestion logic
  const stages = useMemo(() => [
    { key: 'ingestion', name: 'INGESTION LAYER', color: 'border-slate-800', text: 'text-slate-400' },
    { key: 'aiReview', name: 'AI VALIDATION', color: 'border-indigo-900', text: 'text-indigo-400', proc: 'ai' },
    { key: 'medicalReview', name: 'MEDICAL L2', color: 'border-[#ff003c]', text: 'text-[#ff003c]', proc: 'medical' },
    { key: 'approval', name: 'APPROVAL Q', color: 'border-amber-700', text: 'text-amber-400', proc: 'finance' },
    { key: 'settlement', name: 'SETTLEMENT', color: 'border-[#00f0ff]/50', text: 'text-[#00f0ff]' }
  ], []);

  // Filter escalations based on live events
  const escalations = events.filter(e => e.type === 'ESCALATED' || e.type === 'CRITICAL').slice(0, 5);

  const getCardColor = (claim, stageColor) => {
    if (claim.crit) return 'border-[#ff003c] bg-[#ff003c]/20 shadow-[0_0_15px_rgba(255,0,60,0.4)] animate-pulse';
    if (claim.risk === 'HIGH') return 'border-amber-500 bg-amber-500/10';
    return stageColor.replace('border-', 'border-') + '/40 bg-black';
  };

  return (
    <div className="min-h-screen bg-black text-[#00f0ff] p-4 font-mono overflow-hidden flex flex-col selection:bg-[#00f0ff] selection:text-black">
      
      {/* Absolute Top Header */}
      <div className="flex items-center justify-between border-b border-[#00f0ff]/30 pb-3 mb-6 shrink-0">
        <div className="flex items-center gap-4">
          <Activity size={24} className="text-[#00f0ff]" />
          <div>
            <h1 className="text-xl font-bold tracking-[0.2em] text-[#00f0ff] leading-none">NOC COMMAND CENTER</h1>
            <p className="text-[10px] text-[#00f0ff]/60 tracking-widest mt-1">GLOBAL EVENT ORCHESTRATION MATRIX // LIVE</p>
          </div>
        </div>
        
        {/* Processor Heatbars */}
        <div className="flex gap-8 text-xs">
          {Object.entries(processors).map(([key, data]) => (
            <div key={key} className="flex flex-col gap-1 w-32">
              <div className="flex justify-between text-[9px] uppercase tracking-wider text-slate-400">
                <span>{key} NODE</span>
                <span className={data.status === 'CRITICAL' ? 'text-[#ff003c]' : data.status === 'WARNING' ? 'text-amber-400' : 'text-[#00ff66]'}>{Math.floor(data.util)}%</span>
              </div>
              <div className="h-1 bg-slate-900 w-full overflow-hidden">
                <div 
                  className={`h-full transition-all duration-1000 ${data.status === 'CRITICAL' ? 'bg-[#ff003c] animate-pulse' : data.status === 'WARNING' ? 'bg-amber-400' : 'bg-[#00ff66]'}`}
                  style={{ width: `${data.util}%` }}
                />
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Horizontal Kanban Pipeline (Top 60%) */}
      <div className="flex-1 flex gap-4 overflow-x-auto custom-scrollbar pb-4 min-h-[400px]">
        {stages.map((stage, idx) => {
          const isCongested = stage.proc && processors[stage.proc]?.status === 'CRITICAL';
          return (
            <div key={stage.key} className={`flex-1 min-w-[240px] flex flex-col border ${stage.color} ${isCongested ? 'bg-[#ff003c]/5 border-[#ff003c] shadow-[0_0_20px_rgba(255,0,60,0.1)]' : 'bg-[#050914]/50'} rounded-md overflow-hidden relative group transition-all duration-500`}>
              
              {/* Header */}
              <div className={`p-2 border-b ${isCongested ? 'border-[#ff003c] bg-[#ff003c]/20' : `${stage.color} bg-black/40`} flex justify-between items-center shrink-0`}>
                <span className={`text-[10px] font-bold tracking-widest ${isCongested ? 'text-white animate-pulse' : stage.text}`}>{stage.name}</span>
                <span className={`text-xs font-black ${isCongested ? 'text-[#ff003c]' : stage.text}`}>{liveClaims[stage.key].length}</span>
              </div>
              
              {/* Cards Area */}
              <div className="flex-1 p-2 space-y-2 overflow-y-auto custom-scrollbar relative">
                {liveClaims[stage.key].map(c => (
                  <div key={c.id} className={`p-2 border transition-all duration-500 animate-[slideIn_0.3s_ease-out] ${getCardColor(c, stage.color)}`}>
                    <div className="flex justify-between items-start mb-2">
                      <span className="text-xs font-bold text-white">{c.id}</span>
                      {c.crit && <span className="text-[9px] bg-[#ff003c] text-white px-1 font-bold animate-pulse">SLA RISK</span>}
                    </div>
                    <div className="flex justify-between items-end text-[10px]">
                      <span className="text-[#00f0ff]/70">${c.val.toLocaleString()}</span>
                      
                      {/* Manual Override Action for congested states */}
                      {isCongested && idx < stages.length - 1 && (
                        <button 
                          onClick={() => manualRoute(c.id, stage.key, stages[idx+1].key)}
                          className="flex items-center gap-1 bg-[#ff003c] hover:bg-white hover:text-[#ff003c] text-white px-1.5 py-0.5 rounded-sm transition-colors text-[8px] uppercase tracking-widest font-bold">
                          Force Route <ArrowRightCircle size={10} />
                        </button>
                      )}
                    </div>
                  </div>
                ))}
                
                {/* Empty State */}
                {liveClaims[stage.key].length === 0 && (
                  <div className="absolute inset-0 flex items-center justify-center">
                    <span className={`text-[10px] uppercase tracking-widest opacity-30 ${stage.text}`}>Awaiting Traffic</span>
                  </div>
                )}
              </div>
              
              {/* Pipeline Flow Indicator */}
              {idx < stages.length - 1 && (
                <div className="absolute -right-3 top-1/2 -translate-y-1/2 z-10 text-[#00f0ff]/30 group-hover:text-[#00f0ff] transition-colors">
                  ▶
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Bottom Split Terminal (Bottom 40%) */}
      <div className="h-64 mt-4 grid grid-cols-[1fr_2fr] gap-4 shrink-0">
        
        {/* AI Routing Recommendations & Escalations */}
        <div className="border border-[#ff003c]/40 bg-[#050202] rounded-md flex flex-col">
          <div className="p-2 border-b border-[#ff003c]/40 bg-[#ff003c]/10 flex items-center justify-between text-[#ff003c] shrink-0">
            <div className="flex items-center gap-2">
              <ShieldAlert size={14} className="animate-pulse" />
              <span className="text-[10px] font-bold tracking-widest">AI ESCALATION MATRIX</span>
            </div>
            <span className="text-[8px] bg-[#ff003c] text-white px-1">LIVE</span>
          </div>
          <div className="flex-1 p-2 overflow-y-auto space-y-2 custom-scrollbar">
            {escalations.length === 0 && (
              <div className="text-[10px] text-slate-600 h-full flex items-center justify-center">NO ESCALATIONS DETECTED</div>
            )}
            {escalations.map(esc => (
              <div key={esc.id} className="border border-[#ff003c]/30 p-2 bg-[#ff003c]/5">
                <div className="flex justify-between items-center text-[9px] mb-1">
                  <span className="text-white font-bold">EVENT {esc.id.split('-')[1]}</span>
                  <span className="text-slate-500">{esc.timestamp.substring(11,19)}</span>
                </div>
                <div className="text-[10px] text-amber-400 font-bold uppercase mb-1">{esc.module} ALERT</div>
                <div className="text-[10px] text-slate-300 leading-tight">{esc.message}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Global Operational Event Stream */}
        <div className="border border-[#00f0ff]/30 bg-[#000510] rounded-md flex flex-col relative overflow-hidden">
          <div className="p-2 border-b border-[#00f0ff]/30 bg-black/40 flex justify-between items-center text-[#00f0ff] shrink-0 z-10">
            <div className="flex items-center gap-2">
              <Terminal size={14} />
              <span className="text-[10px] font-bold tracking-widest">GLOBAL UNIFIED EVENT BUS</span>
            </div>
            <div className="flex gap-2">
              <span className="w-1.5 h-1.5 rounded-full bg-[#00ff66] animate-pulse"></span>
              <span className="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse delay-75"></span>
              <span className="w-1.5 h-1.5 rounded-full bg-[#00f0ff] animate-pulse delay-150"></span>
            </div>
          </div>
          
          <div className="flex-1 p-3 overflow-y-auto custom-scrollbar font-mono text-[10px] leading-relaxed relative z-10">
            {events.map((e) => (
              <div key={e.id} className="flex gap-3 hover:bg-[#00f0ff]/10 py-0.5 transition-colors">
                <span className="text-slate-500 shrink-0">[{e.timestamp.substring(11,19)}]</span>
                <span className={`shrink-0 w-16 font-bold ${
                  e.type === 'CRITICAL' ? 'text-[#ff003c]' : 
                  e.type === 'WARNING' ? 'text-amber-400' : 
                  e.type === 'ESCALATED' ? 'text-purple-400' : 'text-[#00ff66]'
                }`}>
                  [{e.module}]
                </span>
                <span className={e.type === 'CRITICAL' ? 'text-white font-bold' : 'text-slate-300'}>{e.message}</span>
              </div>
            ))}
          </div>
          
          {/* Scanline overlay */}
          <div className="absolute inset-0 pointer-events-none bg-[linear-gradient(rgba(0,240,255,0.03)_50%,rgba(0,0,0,0.25)_50%)] bg-[length:100%_4px] z-20"></div>
        </div>

      </div>

      <style>{`
        @keyframes slideIn {
          from { transform: translateX(-10px); opacity: 0; }
          to { transform: translateX(0); opacity: 1; }
        }
        .custom-scrollbar::-webkit-scrollbar { width: 4px; height: 4px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: rgba(0, 240, 255, 0.05); }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(0, 240, 255, 0.3); }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(0, 240, 255, 0.6); }
      `}</style>
    </div>
  );
}
