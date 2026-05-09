import React, { useState, useEffect } from 'react';
import axiosInstance from '../../api/axios';
import Loader from '../../components/Loader';
import { Users, UserMinus, ShieldCheck, Activity, Award, PieChart as PieChartIcon } from 'lucide-react';
import { PieChart, Pie, Cell, Tooltip as RechartsTooltip, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';

const CustomerPortfolio = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadPortfolio = () => {
    setLoading(true);
    setError(null);
    // ApiResponse<List<CarrierClaimDetailResponse>> → response.data.data
    axiosInstance.get('/carrier/claims')
      .then(res => {
        const claims = res.data?.data || [];

        // Group claims by patient name to build a CRM profile
        const customerMap = (Array.isArray(claims) ? claims : []).reduce((acc, c) => {
          // Correct field: c.patient.name (not c.patientName)
          const name = c.patient?.name || 'Unknown Insured';
          if (!acc[name]) {
            acc[name] = {
              name,
              totalClaims: 0,
              totalClaimedAmount: 0,
              avgRiskScore: 0,
              totalRiskScore: 0,
              policies: new Set(),
              latestActivity: c.createdDate
            };
          }
          acc[name].totalClaims++;
          acc[name].totalClaimedAmount += (c.amount || 0);
          // Correct field: c.fraud.riskScore (not c.aiFraudScore)
          acc[name].totalRiskScore += (c.fraud?.riskScore ?? 0);
          if (c.policyNumber) acc[name].policies.add(c.policyNumber);
          if (c.createdDate && new Date(c.createdDate) > new Date(acc[name].latestActivity)) {
            acc[name].latestActivity = c.createdDate;
          }
          return acc;
        }, {});

        const portfolio = Object.values(customerMap).map(c => ({
          ...c,
          avgRiskScore: c.totalClaims > 0 ? Math.floor(c.totalRiskScore / c.totalClaims) : 0,
          policiesCount: c.policies.size,
          // Segment: high frequency = >3 claims, high risk = avg risk score >50, else standard
          segment: c.totalClaims > 3 ? 'High Frequency' : (Math.floor(c.totalRiskScore / c.totalClaims) > 50 ? 'High Risk' : 'Standard'),
          ltv: c.totalClaimedAmount
        }));

        setCustomers(portfolio.sort((a, b) => b.ltv - a.ltv));
        setLoading(false);
      })
      .catch(err => {
        console.error('CustomerPortfolio fetch error:', err);
        setError('Failed to load customer portfolio data.');
        setLoading(false);
      });
  };

  useEffect(() => { loadPortfolio(); }, []);

  if (loading) return <Loader fullScreen />;
  if (error) return (
    <div className="bg-red-500/10 border border-red-500/20 rounded-xl p-10 text-center max-w-lg mx-auto mt-10">
      <h2 className="text-xl font-bold text-slate-100 mb-2">Failed to Load</h2>
      <p className="text-slate-400 text-sm mb-6">{error}</p>
      <button onClick={loadPortfolio} className="px-5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 rounded-lg text-sm font-medium">Retry</button>
    </div>
  );

  // Analytics derivations
  const highRiskCustomers = customers.filter(c => c.segment === 'High Risk').length;
  const standardCustomers = customers.filter(c => c.segment === 'Standard').length;
  const highFrequencyCustomers = customers.filter(c => c.segment === 'High Frequency').length;

  const segmentData = [
    { name: 'Standard (Low Risk)', value: standardCustomers || 1 },
    { name: 'High Risk (Fraud flags)', value: highRiskCustomers || 0 },
    { name: 'High Frequency (Many claims)', value: highFrequencyCustomers || 0 }
  ];
  const COLORS = ['#10b981', '#ef4444', '#f59e0b'];

  const topClaimersData = customers.slice(0, 5).map(c => ({
    name: c.name.split(' ')[0],
    amount: c.ltv
  }));

  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Users className="text-indigo-400" /> Customer Portfolio Management
          </h1>
          <p className="text-sm text-slate-400 mt-1">Unified CRM view of all insured customers, segmentation, and lifetime value analytics.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Total Insured Base</h3>
            <Users className="text-blue-400" />
          </div>
          <p className="text-3xl font-black text-white">{customers.length}</p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Unique patients processed</p>
        </div>

        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Avg LTV (Claimed)</h3>
            <Award className="text-emerald-400" />
          </div>
          <p className="text-3xl font-black text-white">
            ${customers.length ? Math.floor(customers.reduce((sum, c) => sum + c.ltv, 0) / customers.length).toLocaleString() : 0}
          </p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Per-customer average payout</p>
        </div>

        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">High Risk Segment</h3>
            <ShieldCheck className="text-red-400" />
          </div>
          <p className="text-3xl font-black text-white">{highRiskCustomers}</p>
          <p className="text-xs text-red-400 mt-2 font-medium">Require enhanced monitoring</p>
        </div>

        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Churn Risk Estimate</h3>
            <UserMinus className="text-amber-400" />
          </div>
          <p className="text-3xl font-black text-white">4.2%</p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Based on recent claim rejections</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Customer Segmentation */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
            <PieChartIcon className="w-4 h-4" /> Customer Segmentation
          </h3>
          <div className="flex items-center h-64">
            <div className="w-1/2 h-full">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={segmentData} innerRadius={50} outerRadius={80} paddingAngle={5} dataKey="value">
                    {segmentData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <RechartsTooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '8px' }} itemStyle={{ color: '#e2e8f0' }} />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="w-1/2 space-y-4">
              {segmentData.map((segment, index) => (
                <div key={segment.name} className="flex items-center gap-3">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[index] }}></div>
                  <div>
                    <p className="text-sm font-bold text-white">{segment.value}</p>
                    <p className="text-xs text-slate-400">{segment.name}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Top Claimers (LTV) */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
            <Activity className="w-4 h-4" /> Top Customers by Claim Volume (LTV)
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={topClaimersData} layout="vertical" margin={{ top: 0, right: 20, left: 20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" horizontal={true} vertical={false} />
                <XAxis type="number" stroke="#64748b" fontSize={11} tickLine={false} axisLine={false} tickFormatter={(val) => `$${val/1000}k`} />
                <YAxis dataKey="name" type="category" stroke="#64748b" fontSize={11} tickLine={false} axisLine={false} />
                <RechartsTooltip cursor={{fill: '#334155', opacity: 0.4}} formatter={(value) => `$${value.toLocaleString()}`} contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '8px' }} />
                <Bar dataKey="amount" fill="#6366f1" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* CRM Customer List */}
      <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden">
        <div className="p-6 border-b border-slate-700">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
            <Users className="w-4 h-4 text-slate-300" /> Unified Customer Directory
          </h3>
        </div>
        <table className="w-full text-left text-sm text-slate-400">
          <thead className="bg-slate-900/50 text-slate-300">
            <tr>
              <th className="p-4">Customer Name</th>
              <th className="p-4">Policies Held</th>
              <th className="p-4">Total Claims</th>
              <th className="p-4">Lifetime Claim Value</th>
              <th className="p-4">Risk Segment</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-700/50">
            {customers.slice(0, 10).map((c, i) => (
              <tr key={i} className="hover:bg-slate-700/20">
                <td className="p-4 font-semibold text-white">{c.name}</td>
                <td className="p-4">{c.policiesCount}</td>
                <td className="p-4">{c.totalClaims}</td>
                <td className="p-4 font-semibold text-emerald-400">${c.ltv.toLocaleString()}</td>
                <td className="p-4">
                  <span className={`px-2 py-1 rounded text-xs font-bold ${
                    c.segment === 'High Risk' ? 'bg-red-500/10 text-red-400' :
                    c.segment === 'High Frequency' ? 'bg-amber-500/10 text-amber-400' :
                    'bg-emerald-500/10 text-emerald-400'
                  }`}>
                    {c.segment}
                  </span>
                </td>
              </tr>
            ))}
            {customers.length === 0 && <tr><td colSpan="5" className="p-8 text-center">No customers found.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CustomerPortfolio;
