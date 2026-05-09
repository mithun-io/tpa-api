import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { ShieldCheck, Activity, AlertTriangle } from 'lucide-react';

export default function CoverageUtilization() {
  const data = [
    { name: 'Jan', used: 400, limit: 1000 },
    { name: 'Feb', used: 300, limit: 1000 },
    { name: 'Mar', used: 200, limit: 1000 },
    { name: 'Apr', used: 600, limit: 1000 },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl">
        <h1 className="text-2xl font-bold text-white flex items-center gap-2">
          <Activity className="text-blue-400" /> Coverage Utilization
        </h1>
        <p className="text-slate-400 mt-2">Track your out-of-pocket expenses and network limits.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <ShieldCheck className="text-emerald-400 mb-2" size={32} />
          <h3 className="text-slate-400 text-sm">Remaining Deductible</h3>
          <p className="text-2xl font-bold text-white">$1,250.00</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <Activity className="text-amber-400 mb-2" size={32} />
          <h3 className="text-slate-400 text-sm">Out-of-Pocket Max</h3>
          <p className="text-2xl font-bold text-white">$4,500.00</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <AlertTriangle className="text-red-400 mb-2" size={32} />
          <h3 className="text-slate-400 text-sm">Non-Network Usage</h3>
          <p className="text-2xl font-bold text-white">$320.00</p>
        </div>
      </div>

      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl h-80">
        <h3 className="text-white font-bold mb-4">Utilization Timeline</h3>
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
            <XAxis dataKey="name" stroke="#94a3b8" />
            <YAxis stroke="#94a3b8" />
            <Tooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155' }} />
            <Bar dataKey="used" fill="#3b82f6" radius={[4, 4, 0, 0]} />
            <Bar dataKey="limit" fill="#1e293b" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
