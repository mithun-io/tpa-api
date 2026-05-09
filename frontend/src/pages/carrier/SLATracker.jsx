import React, { useState, useEffect } from 'react';
import { useEnterpriseEvents } from '../../hooks/useEnterpriseEvents';
import { Target, Crosshair, AlertTriangle, RadioReceiver, ShieldAlert, AlertOctagon } from 'lucide-react';

export default function SLATracker() {
  const { state } = useEnterpriseEvents();
  const { liveClaims, slaBreaches, events } = state;
  
  const [rotation, setRotation] = useState(0);
  const [approaching, setApproaching] = useState([]);
  const [time, setTime] = useState('');

  // Calculate live compliance score based on current breaches
  const totalClaims = Object.values(liveClaims).reduce((sum, arr) => sum + arr.length, 0);
  const complianceScore = totalClaims === 0 ? 100 : Math.max(0, 100 - (slaBreaches.length * 5) - (totalClaims * 0.1));

  useEffect(() => {
    const radarInt = setInterval(() => setRotation(r => (r + 3) % 360), 50);
    
    const secInt = setInterval(() => {
      setTime(new Date().toISOString().substring(11, 19) + " UTC");
    }, 1000);

    return () => { clearInterval(radarInt); clearInterval(secInt); };
  }, []);

  // Compute approaching breaches dynamically from the live claims in Medical review (the riskiest queue)
  useEffect(() => {
    // Medical claims automatically get assigned a fake "T-minus" for simulation purposes
    const medicalClaims = liveClaims.medicalReview.map(c => ({
      id: c.id,
      time: Math.floor((100000 - c.val) / 100) // fake calculation for demo
    })).sort((a,b) => a.time - b.time).slice(0, 7);
    setApproaching(medicalClaims);
  }, [liveClaims.medicalReview]);

  const fmtTime = (secs) => {
    if(secs < 0) return 'BREACH IMMINENT';
    const m = Math.floor(secs/60);
    const s = secs % 60;
    return `${m.toString().padStart(2,'0')}:${s.toString().padStart(2,'0')}`;
  };

  return (
    <div className="min-h-screen bg-[#020202] text-[#ff9900] font-mono flex flex-col overflow-hidden relative selection:bg-[#ff003c] selection:text-white">
      
      {/* Background Grid Lines (Radar Feel) */}
      <div className="absolute inset-0 pointer-events-none opacity-20"
        style={{ backgroundImage: 'linear-gradient(#ff9900 1px, transparent 1px), linear-gradient(90deg, #ff9900 1px, transparent 1px)', backgroundSize: '40px 40px' }}
      />
      
      {/* Top HUD Frame */}
      <div className="absolute top-0 left-0 right-0 h-16 border-b border-[#ff9900]/30 flex justify-between items-center px-8 z-10 bg-[#020202]/80 backdrop-blur-sm">
        <div className="flex items-center gap-4">
          <Crosshair size={24} className="text-[#ff003c] animate-pulse" />
          <div className="text-2xl font-black tracking-[0.3em]">SLA MISSION CONTROL</div>
        </div>
        <div className="flex items-center gap-8">
          <div className="text-sm">
            <span className="text-[#ff9900]/60 mr-2">COMPLIANCE FORECAST:</span>
            <span className={`text-xl font-bold ${complianceScore < 85 ? 'text-[#ff003c]' : 'text-[#00ff66]'}`}>{complianceScore.toFixed(1)}%</span>
          </div>
          <div className="text-xl font-bold tracking-[0.2em] text-[#ff003c]">{time}</div>
        </div>
      </div>

      {/* Central Radar Structure */}
      <div className="flex-1 flex items-center justify-center relative mt-16 z-0">
        
        {/* Massive Radar Circle */}
        <div className="relative w-[500px] h-[500px] border-4 border-[#ff9900]/30 rounded-full flex items-center justify-center shadow-[0_0_50px_rgba(255,153,0,0.1)]">
          <div className="absolute w-[350px] h-[350px] border-2 border-[#ff9900]/20 rounded-full" />
          <div className="absolute w-[200px] h-[200px] border border-[#ff9900]/10 rounded-full" />
          
          {/* Crosshairs */}
          <div className="absolute w-full h-[1px] bg-[#ff9900]/30" />
          <div className="absolute h-full w-[1px] bg-[#ff9900]/30" />

          {/* Sweeper */}
          <div 
            className="absolute w-[250px] h-[250px] origin-bottom-right opacity-40"
            style={{ 
              background: 'conic-gradient(from 180deg at 100% 100%, transparent 0deg, #ff9900 90deg)', 
              transform: `rotate(${rotation}deg)`,
              right: '50%', bottom: '50%'
            }} 
          />

          {/* Center Info */}
          <div className="absolute text-center bg-[#020202] rounded-full p-6 border border-[#ff003c] shadow-[0_0_30px_rgba(255,0,60,0.3)] flex flex-col items-center justify-center z-10 w-32 h-32">
            <div className="text-[#ff003c] font-black text-4xl animate-pulse">{slaBreaches.length}</div>
            <div className="text-[9px] font-bold tracking-widest text-[#ff9900] mt-1 text-center leading-tight">ACTIVE<br/>BREACHES</div>
          </div>
          
          {/* Simulated Blips based on live claims */}
          {approaching.map((a, i) => {
            // Calculate a random fixed position for each claim based on its ID
            const idHash = a.id.charCodeAt(4) + a.id.charCodeAt(5);
            const angle = (idHash * 37) % 360;
            const dist = 100 + ((idHash * 13) % 120); // between 100 and 220
            const top = `calc(50% - ${Math.cos(angle * Math.PI / 180) * dist}px)`;
            const left = `calc(50% + ${Math.sin(angle * Math.PI / 180) * dist}px)`;
            
            return (
              <div key={a.id} 
                className={`absolute w-3 h-3 rounded-full -ml-1.5 -mt-1.5 z-20 transition-opacity duration-1000
                ${a.time < 300 ? 'bg-[#ff003c] shadow-[0_0_10px_rgba(255,0,60,1)] animate-pulse' : 'bg-[#ff9900] shadow-[0_0_10px_rgba(255,153,0,1)]'}`}
                style={{ top, left }}
              />
            );
          })}
        </div>

        {/* Floating Left Panel (Predictive Engine) */}
        <div className="absolute left-8 top-1/2 -translate-y-1/2 w-80 bg-[#020202]/90 border border-[#ff9900] shadow-[0_0_15px_rgba(255,153,0,0.2)] p-4 flex flex-col gap-3 z-10 backdrop-blur-md">
          <div className="flex items-center gap-2 border-b border-[#ff9900]/50 pb-2">
            <Target size={16} />
            <span className="text-sm font-bold tracking-widest">PREDICTIVE BREACH ENGINE</span>
          </div>
          <div className="space-y-2 max-h-64 overflow-y-auto custom-scrollbar pr-2">
            {approaching.length === 0 && <div className="text-xs text-slate-500">No active risks detected.</div>}
            {approaching.map((a) => (
              <div key={a.id} className="flex flex-col gap-1 text-xs border-b border-[#ff9900]/20 pb-2">
                <div className="flex justify-between items-center">
                  <span className="text-white font-bold">{a.id}</span>
                  <span className={`font-bold tracking-widest ${a.time < 300 ? 'text-[#ff003c] animate-pulse' : 'text-[#ff9900]'}`}>
                    T-{fmtTime(a.time)}
                  </span>
                </div>
                <div className="flex justify-between items-center text-[9px] text-[#ff9900]/60">
                  <span>PROBABILITY</span>
                  <span>{a.time < 300 ? '98%' : '64%'}</span>
                </div>
                {/* Probability Bar */}
                <div className="w-full bg-[#ff9900]/10 h-1 mt-1">
                  <div className={`h-full ${a.time < 300 ? 'bg-[#ff003c]' : 'bg-[#ff9900]'}`} style={{width: a.time < 300 ? '98%' : '64%'}} />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Floating Right Panel (Executive Alert Stream) */}
        <div className="absolute right-8 top-1/2 -translate-y-1/2 w-80 bg-[#020202]/90 border border-[#ff9900] shadow-[0_0_15px_rgba(255,153,0,0.2)] p-4 flex flex-col gap-3 z-10 backdrop-blur-md">
          <div className="flex items-center gap-2 border-b border-[#ff9900]/50 pb-2 text-[#ff003c]">
            <ShieldAlert size={16} className="animate-pulse"/>
            <span className="text-sm font-bold tracking-widest">EXECUTIVE COMPLIANCE ALERTS</span>
          </div>
          <div className="space-y-3 max-h-64 overflow-y-auto custom-scrollbar pr-2">
            {events.filter(e => e.module === 'COMPLIANCE' || e.type === 'CRITICAL').length === 0 && (
              <div className="text-xs text-slate-500">System operating within SLA parameters.</div>
            )}
            {events.filter(e => e.module === 'COMPLIANCE' || e.type === 'CRITICAL').map((e) => (
              <div key={e.id} className="bg-[#ff003c]/10 border border-[#ff003c]/30 p-2 text-[10px]">
                <div className="flex justify-between text-[#ff003c] font-bold mb-1">
                  <span>{e.type} ALERT</span>
                  <span>{e.timestamp.substring(11,19)}</span>
                </div>
                <div className="text-white leading-tight">{e.message}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Red-Zone Breach Wall */}
      <div className={`shrink-0 border-t-2 border-[#ff003c] bg-[#ff003c]/10 relative transition-all duration-300 ${slaBreaches.length > 0 ? 'h-48' : 'h-12'}`}>
        {slaBreaches.length > 0 && (
          <div className="absolute inset-0 bg-[#ff003c]/5 animate-[pulse_2s_infinite]" />
        )}
        <div className="px-8 py-2 flex items-center justify-between border-b border-[#ff003c]/30">
          <div className="flex items-center gap-2 text-[#ff003c]">
            <AlertOctagon size={16} className={slaBreaches.length > 0 ? "animate-pulse" : ""} />
            <span className="text-xs font-bold tracking-[0.2em]">{slaBreaches.length > 0 ? 'CRITICAL BREACH WALL ACTIVE' : 'NO ACTIVE BREACHES'}</span>
          </div>
        </div>
        
        {slaBreaches.length > 0 && (
          <div className="p-4 flex gap-4 overflow-x-auto custom-scrollbar relative z-10 h-[calc(100%-40px)]">
            {slaBreaches.map((b) => (
              <div key={b.id} className="min-w-[240px] border border-[#ff003c] bg-[#1a0000] p-3 flex flex-col justify-between hover:bg-[#330000] transition-colors">
                <div>
                  <div className="flex justify-between">
                    <span className="text-white font-bold">{b.id}</span>
                    <span className="text-[10px] text-[#ff003c] bg-[#ff003c]/20 px-1 font-bold">PENALTY: ${b.penalty}</span>
                  </div>
                  <div className="text-[10px] text-slate-400 mt-1">Breached at: {b.breachTime.substring(11,19)}</div>
                </div>
                <button className="mt-3 w-full bg-[#ff003c] text-white text-[10px] font-bold py-1 hover:bg-white hover:text-[#ff003c] transition-colors uppercase tracking-wider">
                  Generate Incident Report
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <style>{`
        .custom-scrollbar::-webkit-scrollbar { height: 4px; width: 4px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: rgba(255, 153, 0, 0.1); }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: #ff9900; }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #ff003c; }
      `}</style>
    </div>
  );
}
