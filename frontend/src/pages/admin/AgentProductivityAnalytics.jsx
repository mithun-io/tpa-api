import React from 'react';
import { Users, TrendingUp, BarChart2, Star } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function AgentProductivityAnalytics() {
  const data = [
    { name: 'Mon', claims: 45, accuracy: 98 },
    { name: 'Tue', claims: 52, accuracy: 96 },
    { name: 'Wed', claims: 49, accuracy: 99 },
    { name: 'Thu', claims: 63, accuracy: 95 },
    { name: 'Fri', claims: 58, accuracy: 97 },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Users className="text-blue-400" /> Agent Productivity Analytics
          </h1>
          <p className="text-slate-400 mt-2">Track claims processed, SLA adherence, and team performance metrics.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
          <TrendingUp className="text-emerald-400 mb-2" />
          <h3 className="text-slate-400 text-sm">Avg Claims/Day</h3>
          <p className="text-2xl font-bold text-white">53.4</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
          <Star className="text-amber-400 mb-2" />
          <h3 className="text-slate-400 text-sm">Quality Score</h3>
          <p className="text-2xl font-bold text-white">98.2%</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
          <BarChart2 className="text-purple-400 mb-2" />
          <h3 className="text-slate-400 text-sm">Turnaround Time</h3>
          <p className="text-2xl font-bold text-white">1.2 Hrs</p>
        </div>
      </div>

      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl h-96">
        <h3 className="text-white font-bold mb-4">Weekly Processing Throughput</h3>
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={data}>
            <defs>
              <linearGradient id="colorClaims" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
            <XAxis dataKey="name" stroke="#64748b" />
            <YAxis stroke="#64748b" />
            <Tooltip contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }} />
            <Area type="monotone" dataKey="claims" stroke="#3b82f6" fillOpacity={1} fill="url(#colorClaims)" />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
