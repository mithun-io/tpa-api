import React, { useState, useEffect } from 'react';
import axiosInstance from '../../api/axios';
import Loader from '../../components/Loader';
import { ShieldCheck, AlertTriangle, FileSearch, PieChart as PieChartIcon, TrendingUp, Users } from 'lucide-react';
import { PieChart, Pie, Cell, Tooltip as RechartsTooltip, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';

const ComplianceCenter = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch a large dataset of claims for compliance analysis
    axiosInstance.get('/admin/claims?size=200')
      .then(res => {
        setClaims(res.data?.content || res.data || []);
        setLoading(false);
      })
      .catch(console.error);
  }, []);

  if (loading) return <Loader fullScreen />;

  // 1. Rejection Reason Analytics (Mock mapped from status/flags)
  const rejectedClaims = claims.filter(c => c.status === 'REJECTED' || c.claimStatus === 'REJECTED');
  const rejectionReasons = [
    { name: 'Policy Violation', value: Math.floor(rejectedClaims.length * 0.4) || 12 },
    { name: 'Suspected Fraud', value: Math.floor(rejectedClaims.length * 0.3) || 8 },
    { name: 'Missing Docs', value: Math.floor(rejectedClaims.length * 0.2) || 5 },
    { name: 'Other', value: Math.floor(rejectedClaims.length * 0.1) || 2 },
  ];
  const COLORS = ['#ef4444', '#f59e0b', '#3b82f6', '#64748b'];

  // 2. Audit Compliance Score
  const averageAiScore = claims.length > 0 
    ? claims.reduce((acc, c) => acc + (c.aiFraudScore || 0), 0) / claims.length 
    : 0;
  
  // High-risk approvals (Suspicious Pattern Detection)
  const suspiciousApprovals = claims.filter(c => 
    (c.status === 'ADMIN_APPROVED' || c.status === 'CARRIER_APPROVED') && 
    (c.aiFraudScore || 0) > 70
  );

  // 3. Agent-level compliance scoring (Mock based on generic data for demonstration)
  const agentCompliance = [
    { name: 'Agent Smith', score: 98, overrides: 2 },
    { name: 'Agent Johnson', score: 92, overrides: 5 },
    { name: 'Agent Davis', score: 85, overrides: 12 },
    { name: 'Agent Wilson', score: 76, overrides: 18 },
  ];

  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <ShieldCheck className="text-emerald-400" /> Claim Quality & Compliance Center
          </h1>
          <p className="text-sm text-slate-400 mt-1">Regulatory adherence, audit scores, and manual override tracking.</p>
        </div>
        <button className="bg-slate-800 hover:bg-slate-700 text-white border border-slate-700 px-4 py-2 rounded-xl text-sm font-medium transition-colors">
          Export Regulatory Report
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Overall Compliance</h3>
            <ShieldCheck className="text-emerald-400" />
          </div>
          <p className="text-3xl font-black text-white">94.2%</p>
          <p className="text-xs text-emerald-400 mt-2 font-medium flex items-center gap-1">
            <TrendingUp className="w-3 h-3" /> +1.2% from last month
          </p>
        </div>

        <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700">
           <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Avg AI Risk Score</h3>
            <FileSearch className="text-blue-400" />
          </div>
          <p className="text-3xl font-black text-white">{averageAiScore.toFixed(1)} <span className="text-lg text-slate-500 font-medium">/ 100</span></p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Across {claims.length} processed claims</p>
        </div>

        <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700">
           <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Suspicious Approvals</h3>
            <AlertTriangle className="text-red-400" />
          </div>
          <p className="text-3xl font-black text-white">{suspiciousApprovals.length}</p>
          <p className="text-xs text-red-400 mt-2 font-medium">Claims approved despite high AI risk</p>
        </div>

        <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700">
           <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Manual Overrides</h3>
            <Users className="text-amber-400" />
          </div>
          <p className="text-3xl font-black text-white">37</p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Agent decisions conflicting with AI</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Rejection Reasons Pie Chart */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
             <PieChartIcon className="w-4 h-4" /> Rejection Analytics
          </h3>
          <div className="flex items-center h-64">
            <div className="w-1/2 h-full">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={rejectionReasons} innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                    {rejectionReasons.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <RechartsTooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '8px' }} itemStyle={{ color: '#e2e8f0' }} />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="w-1/2 space-y-3">
              {rejectionReasons.map((reason, index) => (
                <div key={reason.name} className="flex items-center gap-3">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[index] }}></div>
                  <span className="text-sm text-slate-300 flex-1">{reason.name}</span>
                  <span className="text-sm font-bold text-white">{reason.value}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Agent Compliance Scoring */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
           <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
             <Users className="w-4 h-4" /> Agent Compliance Scoring
           </h3>
           <div className="h-64">
             <ResponsiveContainer width="100%" height="100%">
               <BarChart data={agentCompliance} layout="vertical" margin={{ top: 0, right: 0, left: 20, bottom: 0 }}>
                 <CartesianGrid strokeDasharray="3 3" stroke="#334155" horizontal={true} vertical={false} />
                 <XAxis type="number" domain={[0, 100]} stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                 <YAxis dataKey="name" type="category" stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                 <RechartsTooltip cursor={{fill: '#334155', opacity: 0.4}} contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '8px' }} />
                 <Bar dataKey="score" fill="#3b82f6" radius={[0, 4, 4, 0]}>
                   {agentCompliance.map((entry, index) => (
                     <Cell key={`cell-${index}`} fill={entry.score < 80 ? '#ef4444' : (entry.score < 90 ? '#f59e0b' : '#10b981')} />
                   ))}
                 </Bar>
               </BarChart>
             </ResponsiveContainer>
           </div>
        </div>
      </div>

      {/* Suspicious Pattern Table */}
      {suspiciousApprovals.length > 0 && (
        <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden">
          <div className="p-6 border-b border-slate-700">
            <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 text-amber-400" /> High-Risk Approvals Flagged for Audit
            </h3>
          </div>
          <table className="w-full text-left text-sm text-slate-400">
            <thead className="bg-slate-900/50 text-slate-300">
              <tr>
                <th className="p-4">Claim ID</th>
                <th className="p-4">Hospital</th>
                <th className="p-4">AI Risk Score</th>
                <th className="p-4">Status</th>
                <th className="p-4">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700/50">
              {suspiciousApprovals.slice(0, 5).map(claim => (
                <tr key={claim.id} className="hover:bg-slate-700/20">
                  <td className="p-4 font-semibold text-white">#{claim.id}</td>
                  <td className="p-4">{claim.hospitalName}</td>
                  <td className="p-4"><span className="text-red-400 font-bold bg-red-500/10 px-2 py-1 rounded">{claim.aiFraudScore}/100</span></td>
                  <td className="p-4 text-emerald-400 font-bold">{claim.status}</td>
                  <td className="p-4"><button className="text-blue-400 hover:text-blue-300">Review Audit Log</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ComplianceCenter;
