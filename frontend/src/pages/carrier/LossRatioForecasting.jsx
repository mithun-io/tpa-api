import React from 'react';
import { TrendingDown, Calendar, DollarSign } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function LossRatioForecasting() {
  const data = [
    { month: 'Jan', actual: 65, predicted: 65 },
    { month: 'Feb', actual: 68, predicted: 67 },
    { month: 'Mar', actual: 72, predicted: 70 },
    { month: 'Apr', actual: 75, predicted: 74 },
    { month: 'May', actual: null, predicted: 78 },
    { month: 'Jun', actual: null, predicted: 82 },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <TrendingDown className="text-rose-400" /> Loss Ratio Forecasting
          </h1>
          <p className="text-slate-400 mt-2">Predictive modeling of claims vs premiums over the next 6 months.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <TrendingDown className="text-rose-400 mb-2" size={32} />
          <h3 className="text-slate-400 text-sm">Current Loss Ratio</h3>
          <p className="text-3xl font-black text-white">75.0%</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <Calendar className="text-amber-400 mb-2" size={32} />
          <h3 className="text-slate-400 text-sm">Projected Q3 Ratio</h3>
          <p className="text-3xl font-black text-amber-400">82.4%</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <DollarSign className="text-emerald-400 mb-2" size={32} />
          <h3 className="text-slate-400 text-sm">Premium Collected</h3>
          <p className="text-3xl font-black text-white">$4.2M</p>
        </div>
      </div>

      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl h-96">
        <h3 className="text-white font-bold mb-4">6-Month Trend & Forecast</h3>
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
            <XAxis dataKey="month" stroke="#64748b" />
            <YAxis stroke="#64748b" domain={[40, 100]} />
            <Tooltip contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }} />
            <Line type="monotone" dataKey="actual" stroke="#10b981" strokeWidth={3} dot={{ r: 4 }} name="Actual %" />
            <Line type="monotone" dataKey="predicted" stroke="#f43f5e" strokeWidth={3} strokeDasharray="5 5" name="Predicted %" />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
