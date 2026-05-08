import React, { useState, useEffect } from 'react';
import axiosInstance from '../../api/axios';
import Loader from '../../components/Loader';
import { ShieldAlert, TrendingUp, MapPin, Activity, PieChart as PieChartIcon } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, LineChart, Line } from 'recharts';

const UnderwritingIntelligence = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);

  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch carrier specific claims for underwriting analysis
    // ApiResponse<List<CarrierClaimDetailResponse>> → response.data.data
    axiosInstance.get('/carrier/claims')
      .then(res => {
        const data = res.data?.data || [];
        setClaims(Array.isArray(data) ? data : []);
        setLoading(false);
      })
      .catch(err => {
        console.error('UnderwritingIntelligence fetch error:', err);
        setError('Failed to load claims data.');
        setLoading(false);
      });
  }, []);

  if (loading) return <Loader fullScreen />;
  if (error) return (
    <div className="bg-red-500/10 border border-red-500/20 rounded-xl p-10 text-center max-w-lg mx-auto mt-10">
      <h2 className="text-xl font-bold text-slate-100 mb-2">Failed to Load</h2>
      <p className="text-slate-400 text-sm">{error}</p>
    </div>
  );

  // 1. Policy-wise risk scoring
  const policyRiskData = claims.reduce((acc, c) => {
    // Guard: policyNumber may be null
    const pn = c.policyNumber || 'UNKNOWN';
    const p = pn.length > 5 ? pn.substring(0, 5) + '***' : pn;
    if (!acc[p]) acc[p] = { policy: p, claimCount: 0, totalAmount: 0, riskScore: 0 };
    acc[p].claimCount++;
    acc[p].totalAmount += (c.amount || 0);
    // Use fraud.riskScore if available, fallback to 10
    acc[p].riskScore += (c.fraud?.riskScore ?? 10);
    return acc;
  }, {});
  
  const riskChartData = Object.values(policyRiskData)
    .map(p => ({ ...p, riskScore: Math.min(100, Math.floor(p.riskScore / p.claimCount)) }))
    .sort((a, b) => b.riskScore - a.riskScore)
    .slice(0, 10);

  // 2. High-risk demographic/hospital analysis
  const hospitalRiskData = claims.reduce((acc, c) => {
    const h = c.hospitalName || 'Unknown';
    if (!acc[h]) acc[h] = { hospital: h, claims: 0, avgRisk: 0, totalRisk: 0 };
    acc[h].claims++;
    // Use fraud.riskScore (0-100) if available
    acc[h].totalRisk += (c.fraud?.riskScore ?? 0);
    return acc;
  }, {});

  const topRiskHospitals = Object.values(hospitalRiskData)
    .map(h => ({ ...h, avgRisk: Math.floor(h.totalRisk / h.claims) }))
    .filter(h => h.claims > 1) // Only hospitals with multiple claims
    .sort((a, b) => b.avgRisk - a.avgRisk)
    .slice(0, 5);

  // 3. Historical Claim Risk Trends (Mock line chart data)
  const historicalRiskData = [
    { month: 'Jan', avgRisk: 34, claims: 120 },
    { month: 'Feb', avgRisk: 36, claims: 135 },
    { month: 'Mar', avgRisk: 42, claims: 150 },
    { month: 'Apr', avgRisk: 38, claims: 142 },
    { month: 'May', avgRisk: 45, claims: 180 }, // Assuming we are around May and seeing a spike
    { month: 'Jun', avgRisk: 52, claims: 210 },
  ];

  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <ShieldAlert className="text-red-400" /> Risk & Underwriting Intelligence Hub
          </h1>
          <p className="text-sm text-slate-400 mt-1">Advanced risk analytics, predictive underwriting, and demographic profiling.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Avg Portfolio Risk</h3>
            <Activity className="text-amber-400" />
          </div>
          <p className="text-3xl font-black text-white">42.8 <span className="text-lg text-slate-500 font-medium">/ 100</span></p>
          <div className="w-full bg-slate-900 rounded-full h-1.5 mt-4">
            <div className="bg-amber-500 h-1.5 rounded-full" style={{ width: '42.8%' }}></div>
          </div>
        </div>
        
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Flagged Policies</h3>
            <ShieldAlert className="text-red-400" />
          </div>
          <p className="text-3xl font-black text-white">{riskChartData.filter(p => p.riskScore > 60).length}</p>
          <p className="text-xs text-red-400 mt-2 font-medium">Require premium adjustment review</p>
        </div>

        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Risk Trend</h3>
            <TrendingUp className="text-blue-400" />
          </div>
          <p className="text-3xl font-black text-white">+18%</p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Increase in high-risk claims over 90 days</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Policy Risk Scoring Chart */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
            <PieChartIcon className="w-4 h-4" /> Policy-wise Risk Scoring (Top 10)
          </h3>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={riskChartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                <XAxis dataKey="policy" stroke="#64748b" fontSize={11} tickLine={false} axisLine={false} />
                <YAxis stroke="#64748b" fontSize={11} tickLine={false} axisLine={false} domain={[0, 100]} />
                <RechartsTooltip cursor={{fill: '#334155', opacity: 0.4}} contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '8px' }} />
                <Bar dataKey="riskScore" fill="#ef4444" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Historical Risk Trend */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
            <TrendingUp className="w-4 h-4" /> Historical Claim Risk Trends
          </h3>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={historicalRiskData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                <XAxis dataKey="month" stroke="#64748b" fontSize={11} tickLine={false} axisLine={false} />
                <YAxis stroke="#64748b" fontSize={11} tickLine={false} axisLine={false} />
                <RechartsTooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '8px' }} />
                <Line type="monotone" dataKey="avgRisk" name="Avg AI Risk Score" stroke="#f59e0b" strokeWidth={3} dot={{r: 4, fill: '#1e293b', strokeWidth: 2}} />
                <Line type="monotone" dataKey="claims" name="Total Claims Volume" stroke="#3b82f6" strokeWidth={3} dot={{r: 4, fill: '#1e293b', strokeWidth: 2}} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* High-Risk Providers Table */}
      <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden">
        <div className="p-6 border-b border-slate-700">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
            <MapPin className="w-4 h-4 text-indigo-400" /> Fraud-Prone Provider / Region Detection
          </h3>
        </div>
        <table className="w-full text-left text-sm text-slate-400">
          <thead className="bg-slate-900/50 text-slate-300">
            <tr>
              <th className="p-4">Hospital/Provider</th>
              <th className="p-4">Claims Processed</th>
              <th className="p-4">Avg Risk Score</th>
              <th className="p-4">Underwriting Suggestion</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-700/50">
            {topRiskHospitals.length === 0 ? (
              <tr><td colSpan="4" className="p-8 text-center">No high-risk providers detected.</td></tr>
            ) : topRiskHospitals.map(h => (
              <tr key={h.hospital} className="hover:bg-slate-700/20">
                <td className="p-4 font-semibold text-white">{h.hospital}</td>
                <td className="p-4">{h.claims}</td>
                <td className="p-4"><span className={`font-bold px-2 py-1 rounded ${h.avgRisk > 60 ? 'text-red-400 bg-red-500/10' : 'text-amber-400 bg-amber-500/10'}`}>{h.avgRisk}/100</span></td>
                <td className="p-4 text-indigo-400 font-medium">Apply strict ML validation overlay</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UnderwritingIntelligence;
