import React, { useMemo } from 'react';
import { useDemoData } from '../../context/DemoDataProvider';
import { useEnterpriseEvents } from '../../hooks/useEnterpriseEvents';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area, ComposedChart } from 'recharts';
import { Activity, Terminal, Database, TrendingUp } from 'lucide-react';

export default function ProductUtilization() {
  const { insurancePlans } = useDemoData();
  const { state } = useEnterpriseEvents();
  const { liveClaims } = state;

  const activeLiveClaimsCount = Object.values(liveClaims).reduce((acc, curr) => acc + curr.length, 0);

  // Generate dense actuarial matrix data
  const cohortData = useMemo(() => {
    return insurancePlans.map(p => {
      const riskScore = p.utilization * 0.4 + (p.rejectionRatio * 2) + (p.claimFrequency * 0.5);
      return {
        id: p.id,
        name: p.shortName,
        q1: p.trend[0], q2: p.trend[1], q3: p.trend[2], q4: p.trend[3], q5: p.trend[4], q6: p.trend[5],
        utilization: p.utilization,
        exposure: (p.coverage * p.claimFrequency / 1000000).toFixed(2), // Mock Exposure Index
        lossRatio: (p.avgClaimAmt * p.claimFrequency / p.premium).toFixed(2),
        riskScore: riskScore.toFixed(1),
        status: riskScore > 65 ? 'CRIT' : riskScore > 45 ? 'WARN' : 'STBL'
      };
    }).sort((a,b) => b.riskScore - a.riskScore);
  }, [insurancePlans]);

  return (
    <div className="min-h-screen bg-[#000000] text-[#00ff41] font-mono p-2 flex flex-col overflow-hidden selection:bg-[#00ff41] selection:text-black">
      
      {/* Terminal Top Bar */}
      <div className="flex justify-between items-center border-b-2 border-[#00ff41]/50 pb-2 mb-2 px-2 shrink-0">
        <div className="flex items-center gap-4">
          <Terminal size={18} />
          <h1 className="text-lg font-black tracking-widest uppercase">ACTUARIAL UTILIZATION TERMINAL v9.2</h1>
          <span className="text-xs bg-[#00ff41] text-black px-2 font-bold animate-pulse">LIVE DATA</span>
        </div>
        <div className="flex gap-6 text-xs">
          <div className="flex gap-2"><span className="text-[#00ff41]/60">SYS_TIME:</span>{new Date().toISOString()}</div>
          <div className="flex gap-2"><span className="text-[#00ff41]/60">SESS_ID:</span>TX-8829-ACT</div>
          <div className="flex gap-2"><span className="text-[#00ff41]/60">LIVE_INGEST:</span>{activeLiveClaimsCount}</div>
        </div>
      </div>

      <div className="flex-1 grid grid-cols-12 grid-rows-6 gap-2 min-h-0">
        
        {/* Top Left: Multi-layer Seasonal Chart */}
        <div className="col-span-8 row-span-3 border border-[#00ff41]/30 bg-[#001100] p-2 flex flex-col">
          <div className="text-xs font-bold border-b border-[#00ff41]/30 pb-1 mb-2 flex justify-between">
            <span>[CHART_01] GLOBAL UTILIZATION TRENDS</span>
            <span className="text-amber-400">YTD FORECAST</span>
          </div>
          <div className="flex-1 min-h-0">
            <ResponsiveContainer width="100%" height="100%">
              <ComposedChart data={cohortData} margin={{ top: 5, right: 0, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#00ff41" opacity={0.1} />
                <XAxis dataKey="name" tick={{ fill: '#00ff41', fontSize: 9 }} interval={0} angle={-45} textAnchor="end" />
                <YAxis yAxisId="left" tick={{ fill: '#00ff41', fontSize: 9 }} />
                <YAxis yAxisId="right" orientation="right" tick={{ fill: '#ffaa00', fontSize: 9 }} />
                <Tooltip contentStyle={{ backgroundColor: '#000', border: '1px solid #00ff41', color: '#00ff41', fontSize: 10 }} />
                <Bar yAxisId="left" dataKey="utilization" fill="#00ff41" opacity={0.4} />
                <Line yAxisId="left" type="monotone" dataKey="riskScore" stroke="#ff003c" strokeWidth={2} dot={false} />
                <Area yAxisId="right" type="step" dataKey="exposure" fill="#ffaa00" opacity={0.2} stroke="#ffaa00" />
              </ComposedChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Top Right: Real-time Tickers */}
        <div className="col-span-4 row-span-3 border border-[#00ff41]/30 bg-[#001100] p-2 flex flex-col">
          <div className="text-xs font-bold border-b border-[#00ff41]/30 pb-1 mb-2">
            <span>[TICKER_X] RISK SATURATION INDICATORS</span>
          </div>
          <div className="flex-1 overflow-y-auto custom-scrollbar space-y-1">
            {cohortData.map((d, i) => (
              <div key={d.id} className="flex justify-between text-[10px] border-b border-[#00ff41]/10 py-1 hover:bg-[#00ff41]/10">
                <span className="w-8">{i+1}.</span>
                <span className="flex-1 truncate pr-2">{d.id}</span>
                <span className="w-16 text-right">{d.utilization}%</span>
                <span className={`w-12 text-right font-bold ${d.status === 'CRIT' ? 'text-[#ff003c] animate-pulse' : d.status === 'WARN' ? 'text-amber-400' : 'text-[#00ff41]'}`}>
                  {d.status}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Bottom Area: Dense Cohort Analysis Matrix */}
        <div className="col-span-12 row-span-3 border border-[#00ff41]/30 bg-[#001100] p-2 flex flex-col">
          <div className="text-xs font-bold border-b border-[#00ff41]/30 pb-1 mb-2 flex justify-between">
            <span>[MAT_01] COHORT ANALYSIS & LOSS RATIO MATRIX</span>
            <Database size={12} />
          </div>
          <div className="flex-1 overflow-auto custom-scrollbar">
            <table className="w-full text-[10px] text-left border-collapse">
              <thead className="bg-[#00ff41]/20 sticky top-0">
                <tr>
                  <th className="p-1 border border-[#00ff41]/30">PRODUCT_ID</th>
                  <th className="p-1 border border-[#00ff41]/30">NAME</th>
                  <th className="p-1 border border-[#00ff41]/30 text-right">EXPOSURE_IDX</th>
                  <th className="p-1 border border-[#00ff41]/30 text-right">LOSS_RATIO</th>
                  <th className="p-1 border border-[#00ff41]/30 text-right">RISK_SCR</th>
                  <th className="p-1 border border-[#00ff41]/30 text-center">T_M1</th>
                  <th className="p-1 border border-[#00ff41]/30 text-center">T_M2</th>
                  <th className="p-1 border border-[#00ff41]/30 text-center">T_M3</th>
                  <th className="p-1 border border-[#00ff41]/30 text-center">T_M4</th>
                  <th className="p-1 border border-[#00ff41]/30 text-center">T_M5</th>
                  <th className="p-1 border border-[#00ff41]/30 text-center">T_M6</th>
                  <th className="p-1 border border-[#00ff41]/30 text-center">ACTION_REQ</th>
                </tr>
              </thead>
              <tbody>
                {cohortData.map(d => (
                  <tr key={d.id} className="hover:bg-[#00ff41]/10">
                    <td className="p-1 border border-[#00ff41]/20 font-bold">{d.id}</td>
                    <td className="p-1 border border-[#00ff41]/20 truncate max-w-[120px]">{d.name}</td>
                    <td className="p-1 border border-[#00ff41]/20 text-right text-amber-400">{d.exposure}</td>
                    <td className="p-1 border border-[#00ff41]/20 text-right">{d.lossRatio}</td>
                    <td className="p-1 border border-[#00ff41]/20 text-right font-bold">{d.riskScore}</td>
                    
                    {/* Trend heatmap cells */}
                    <td className={`p-1 border border-[#00ff41]/20 text-center ${d.q1 > 60 ? 'bg-[#ff003c]/30' : ''}`}>{d.q1}</td>
                    <td className={`p-1 border border-[#00ff41]/20 text-center ${d.q2 > 60 ? 'bg-[#ff003c]/30' : ''}`}>{d.q2}</td>
                    <td className={`p-1 border border-[#00ff41]/20 text-center ${d.q3 > 60 ? 'bg-[#ff003c]/30' : ''}`}>{d.q3}</td>
                    <td className={`p-1 border border-[#00ff41]/20 text-center ${d.q4 > 60 ? 'bg-[#ff003c]/30' : ''}`}>{d.q4}</td>
                    <td className={`p-1 border border-[#00ff41]/20 text-center ${d.q5 > 60 ? 'bg-[#ff003c]/30' : ''}`}>{d.q5}</td>
                    <td className={`p-1 border border-[#00ff41]/20 text-center ${d.q6 > 60 ? 'bg-[#ff003c]/30' : ''}`}>{d.q6}</td>
                    
                    <td className={`p-1 border border-[#00ff41]/20 text-center font-bold tracking-widest ${d.status === 'CRIT' ? 'text-[#ff003c] bg-[#ff003c]/10' : d.status === 'WARN' ? 'text-amber-400' : ''}`}>
                      {d.status === 'CRIT' ? 'REPRICE' : d.status === 'WARN' ? 'MONITOR' : 'HOLD'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <style>{`
        .custom-scrollbar::-webkit-scrollbar { width: 4px; height: 4px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: transparent; border-left: 1px solid rgba(0,255,65,0.2); }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(0,255,65,0.5); }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(0,255,65,1); }
      `}</style>
    </div>
  );
}
