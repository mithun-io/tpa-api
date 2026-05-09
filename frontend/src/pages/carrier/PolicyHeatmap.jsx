import React, { useState, useEffect, useMemo } from 'react';
import { useEnterpriseEvents } from '../../hooks/useEnterpriseEvents';
import { Map, AlertTriangle, ShieldAlert, Activity, Target, Crosshair } from 'lucide-react';

const NODES = [
  { id: 'DEL', name: 'Delhi NCR', x: 35, y: 20, baseRisk: 15 },
  { id: 'BOM', name: 'Mumbai', x: 22, y: 55, baseRisk: 25 },
  { id: 'BLR', name: 'Bangalore', x: 35, y: 75, baseRisk: 10 },
  { id: 'MAA', name: 'Chennai', x: 45, y: 80, baseRisk: 12 },
  { id: 'HYD', name: 'Hyderabad', x: 42, y: 60, baseRisk: 8 },
  { id: 'CCU', name: 'Kolkata', x: 70, y: 45, baseRisk: 18 },
  { id: 'AMD', name: 'Ahmedabad', x: 18, y: 42, baseRisk: 5 },
  { id: 'PNQ', name: 'Pune', x: 25, y: 58, baseRisk: 7 }
];

const CONNECTIONS = [
  ['DEL', 'BOM'], ['DEL', 'CCU'], ['DEL', 'AMD'],
  ['BOM', 'PNQ'], ['BOM', 'HYD'], ['BOM', 'BLR'],
  ['BLR', 'MAA'], ['HYD', 'MAA'], ['HYD', 'CCU']
];

export default function PolicyHeatmap() {
  const { state } = useEnterpriseEvents();
  const { events, liveClaims, fraudSignals } = state;
  const [activeNode, setActiveNode] = useState(null);
  const [pulse, setPulse] = useState(0);

  // Animate map pulses
  useEffect(() => {
    const int = setInterval(() => setPulse(p => (p + 1) % 100), 50);
    return () => clearInterval(int);
  }, []);

  // Compute live node data based on claims and fraud
  const mapData = useMemo(() => {
    return NODES.map(node => {
      // Simulate mapping claims to regions (using id hash)
      const claims = Object.values(liveClaims).flat().filter(c => (c.id.charCodeAt(4) % NODES.length) === NODES.indexOf(node));
      const fraud = fraudSignals.filter(f => f.desc && f.desc.includes(node.name));
      
      const isHighRisk = claims.length > 3 || fraud.length > 0 || node.baseRisk > 20;
      
      return {
        ...node,
        activeClaims: claims.length,
        avgPayout: claims.length ? Math.floor(claims.reduce((s, c) => s + c.val, 0) / claims.length) : 0,
        fraudAlerts: fraud.length,
        isHighRisk
      };
    });
  }, [liveClaims, fraudSignals]);

  const topRisky = [...mapData].sort((a, b) => (b.activeClaims + b.fraudAlerts * 5) - (a.activeClaims + a.fraudAlerts * 5)).slice(0, 4);

  return (
    <div className="min-h-screen bg-[#050B14] text-cyan-500 font-mono flex flex-col overflow-hidden relative selection:bg-cyan-500 selection:text-black">
      
      {/* Background Grid */}
      <div className="absolute inset-0 pointer-events-none opacity-10"
        style={{ backgroundImage: 'linear-gradient(#00f0ff 1px, transparent 1px), linear-gradient(90deg, #00f0ff 1px, transparent 1px)', backgroundSize: '40px 40px' }}
      />

      {/* Top Header Workspace */}
      <div className="absolute top-0 left-0 right-0 h-20 border-b border-cyan-900/50 bg-[#050B14]/80 backdrop-blur-md flex items-center justify-between px-8 z-20">
        <div className="flex items-center gap-4">
          <Map size={28} className="text-cyan-400" />
          <div>
            <h1 className="text-2xl font-black tracking-[0.2em] text-cyan-400">ENTERPRISE RISK INTELLIGENCE MAP</h1>
            <p className="text-[10px] tracking-widest text-cyan-600 uppercase">Live Geographic Threat Matrix // India Subcontinent</p>
          </div>
        </div>
        <div className="flex gap-6">
          <div className="text-right">
            <div className="text-[10px] text-cyan-600 tracking-widest">ACTIVE SENSORS</div>
            <div className="text-xl font-bold text-cyan-400">8 / 8 ONLINE</div>
          </div>
          <div className="text-right border-l border-cyan-900/50 pl-6">
            <div className="text-[10px] text-cyan-600 tracking-widest">GLOBAL THREAT LEVEL</div>
            <div className="text-xl font-bold text-amber-500 animate-pulse">ELEVATED</div>
          </div>
        </div>
      </div>

      {/* Main Map Area */}
      <div className="flex-1 relative mt-20 z-10 flex">
        
        {/* SVG Topology */}
        <div className="flex-1 relative border-r border-cyan-900/50 flex items-center justify-center p-8">
          <div className="relative w-full max-w-4xl aspect-[4/3] bg-cyan-950/10 border border-cyan-900/30 rounded-3xl overflow-hidden shadow-[0_0_50px_rgba(0,240,255,0.05)]">
            
            {/* Map Frame Coordinates */}
            <div className="absolute top-4 left-4 text-[9px] text-cyan-700 tracking-widest">GEO_BOUNDS: 8.0N 68.7E // 37.6N 97.2E</div>
            <div className="absolute bottom-4 right-4 text-[9px] text-cyan-700 tracking-widest">PROJECTION: TPA_CYBER_MERCATOR</div>

            <svg className="w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
              {/* Connections */}
              {CONNECTIONS.map(([n1, n2], idx) => {
                const node1 = mapData.find(n => n.id === n1);
                const node2 = mapData.find(n => n.id === n2);
                if (!node1 || !node2) return null;
                const active = node1.isHighRisk || node2.isHighRisk;
                return (
                  <line 
                    key={idx}
                    x1={node1.x} y1={node1.y} 
                    x2={node2.x} y2={node2.y} 
                    stroke={active ? '#ef4444' : '#0891b2'} 
                    strokeWidth={active ? 0.3 : 0.1}
                    className={active ? 'animate-[pulse_2s_infinite]' : ''}
                  />
                );
              })}

              {/* Nodes */}
              {mapData.map(node => (
                <g 
                  key={node.id} 
                  transform={`translate(${node.x}, ${node.y})`}
                  onMouseEnter={() => setActiveNode(node.id)}
                  onMouseLeave={() => setActiveNode(null)}
                  className="cursor-pointer"
                >
                  {/* Outer Radar Pulse */}
                  {node.isHighRisk && (
                    <circle 
                      r={pulse / 10 + 2} 
                      fill="none" 
                      stroke={node.fraudAlerts > 0 ? '#ef4444' : '#f59e0b'} 
                      strokeWidth="0.2" 
                      opacity={1 - (pulse / 100)} 
                    />
                  )}
                  
                  {/* Core Node */}
                  <circle 
                    r={activeNode === node.id ? 2 : 1.2} 
                    fill={node.fraudAlerts > 0 ? '#ef4444' : node.isHighRisk ? '#f59e0b' : '#06b6d4'} 
                    className="transition-all duration-300"
                  />
                  
                  {/* Label */}
                  <text 
                    y="-3" 
                    textAnchor="middle" 
                    fill={node.isHighRisk ? '#fff' : '#0891b2'} 
                    fontSize="2.5" 
                    className="font-mono tracking-widest pointer-events-none drop-shadow-[0_0_2px_rgba(0,0,0,1)]"
                  >
                    {node.id}
                  </text>
                </g>
              ))}
            </svg>

            {/* Hover Tooltip Overlay */}
            {activeNode && (
              <div 
                className="absolute bg-[#050B14]/95 border border-cyan-500/50 p-4 shadow-[0_0_20px_rgba(0,240,255,0.2)] pointer-events-none backdrop-blur-md transition-all z-30"
                style={{
                  left: `${mapData.find(n => n.id === activeNode).x}%`,
                  top: `${mapData.find(n => n.id === activeNode).y}%`,
                  transform: 'translate(15px, -50%)'
                }}
              >
                {(() => {
                  const data = mapData.find(n => n.id === activeNode);
                  return (
                    <div className="w-48">
                      <div className="text-cyan-300 font-bold tracking-widest border-b border-cyan-800 pb-2 mb-2 flex justify-between">
                        {data.name}
                        {data.isHighRisk && <AlertTriangle size={14} className="text-red-500 animate-pulse" />}
                      </div>
                      <div className="space-y-1 text-xs">
                        <div className="flex justify-between">
                          <span className="text-cyan-700">LIVE CLAIMS:</span>
                          <span className="text-cyan-400 font-bold">{data.activeClaims}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-cyan-700">AVG PAYOUT:</span>
                          <span className="text-cyan-400 font-bold">${data.avgPayout.toLocaleString()}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-cyan-700">FRAUD ALERTS:</span>
                          <span className={data.fraudAlerts > 0 ? 'text-red-500 font-bold animate-pulse' : 'text-cyan-400 font-bold'}>{data.fraudAlerts}</span>
                        </div>
                        <div className="flex justify-between mt-2 pt-2 border-t border-cyan-900/50">
                          <span className="text-cyan-700">THREAT LEVEL:</span>
                          <span className={data.isHighRisk ? 'text-amber-500 font-bold' : 'text-emerald-500 font-bold'}>{data.isHighRisk ? 'ELEVATED' : 'NOMINAL'}</span>
                        </div>
                      </div>
                    </div>
                  );
                })()}
              </div>
            )}
          </div>
        </div>

        {/* Intelligence Side Rail */}
        <div className="w-96 bg-[#03070c] p-6 flex flex-col border-l border-cyan-900/50 shrink-0">
          <div className="flex items-center gap-2 text-cyan-400 mb-6">
            <Target size={18} />
            <h3 className="font-bold tracking-widest text-sm">TARGET ACQUISITION</h3>
          </div>
          
          <div className="space-y-4 flex-1">
            <div className="text-[10px] text-cyan-700 tracking-widest mb-2">TOP HIGH-RISK ZONES</div>
            {topRisky.map((node, i) => (
              <div key={node.id} className="bg-cyan-950/20 border border-cyan-800/50 p-3 relative overflow-hidden group">
                {node.isHighRisk && <div className="absolute left-0 top-0 bottom-0 w-1 bg-red-500" />}
                <div className="flex justify-between items-center mb-1">
                  <span className="font-bold text-cyan-300 ml-2">{node.name}</span>
                  <span className="text-[10px] bg-cyan-900/50 px-2 text-cyan-400">{node.id}</span>
                </div>
                <div className="ml-2 flex gap-4 text-[10px] mt-2 text-cyan-600">
                  <span>FREQ: <strong className="text-cyan-400">{node.activeClaims}</strong></span>
                  <span>FRAUD: <strong className={node.fraudAlerts > 0 ? 'text-red-400' : 'text-cyan-400'}>{node.fraudAlerts}</strong></span>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-6 border-t border-cyan-900/50 pt-6">
             <div className="text-[10px] text-cyan-700 tracking-widest mb-4">SYSTEM DIAGNOSTICS</div>
             <div className="space-y-2">
               <div className="flex justify-between text-xs">
                 <span className="text-cyan-600">SATELLITE UPLINK</span>
                 <span className="text-emerald-500">STABLE</span>
               </div>
               <div className="flex justify-between text-xs">
                 <span className="text-cyan-600">FRAUD AI ENGINE</span>
                 <span className="text-emerald-500">ACTIVE</span>
               </div>
               <div className="flex justify-between text-xs">
                 <span className="text-cyan-600">DATA STREAM LATENCY</span>
                 <span className="text-cyan-400">12ms</span>
               </div>
             </div>
          </div>
        </div>
      </div>

      {/* Bottom Live Event Stream */}
      <div className="h-12 border-t border-cyan-900/50 bg-[#02050A] flex items-center px-4 shrink-0 z-20">
        <div className="flex items-center gap-2 text-red-500 border-r border-cyan-900/50 pr-4 mr-4 shrink-0">
          <ShieldAlert size={14} className="animate-pulse" />
          <span className="text-[10px] font-bold tracking-widest">LIVE INTERCEPTS</span>
        </div>
        <div className="flex-1 overflow-hidden relative h-full flex items-center">
          <div className="flex gap-8 absolute whitespace-nowrap animate-[marquee_20s_linear_infinite]">
            {events.slice(0, 8).map((e, i) => (
              <span key={i} className="text-[10px] text-cyan-500 flex items-center gap-2">
                <span className="text-cyan-800">[{e.timestamp.substring(11,19)}]</span>
                <span className={e.type === 'CRITICAL' ? 'text-red-400' : e.type === 'WARNING' ? 'text-amber-400' : 'text-cyan-300'}>
                  {e.message}
                </span>
                <span className="text-cyan-900 ml-4">///</span>
              </span>
            ))}
            {/* Duplicate for seamless loop */}
            {events.slice(0, 8).map((e, i) => (
              <span key={`dup-${i}`} className="text-[10px] text-cyan-500 flex items-center gap-2">
                <span className="text-cyan-800">[{e.timestamp.substring(11,19)}]</span>
                <span className={e.type === 'CRITICAL' ? 'text-red-400' : e.type === 'WARNING' ? 'text-amber-400' : 'text-cyan-300'}>
                  {e.message}
                </span>
                <span className="text-cyan-900 ml-4">///</span>
              </span>
            ))}
          </div>
        </div>
      </div>

      <style>{`
        @keyframes marquee {
          0% { transform: translateX(0); }
          100% { transform: translateX(-50%); }
        }
      `}</style>
    </div>
  );
}
