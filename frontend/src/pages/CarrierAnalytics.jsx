import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area, 
  PieChart, Pie, Cell 
} from 'recharts';
import { 
  TrendingUp, 
  ShieldAlert, 
  Clock, 
  Activity,
  ArrowUpRight,
  ArrowDownRight,
  Zap
} from 'lucide-react';

const CarrierAnalytics = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get('/api/v1/analytics/carrier?carrierId=1');
        setData(response.data);
      } catch (error) {
        console.error("Error fetching analytics", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
  );

  const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444'];

  return (
    <div className="p-8 bg-slate-950 min-h-screen text-slate-200">
      <div className="max-w-7xl mx-auto">
        
        {/* Header */}
        <div className="flex justify-between items-end mb-10">
          <div>
            <h1 className="text-4xl font-black tracking-tight text-white mb-2">Executive Analytics</h1>
            <p className="text-slate-400 font-medium">Strategic overview of claim performance & savings</p>
          </div>
          <div className="flex bg-slate-900 border border-slate-800 p-1 rounded-xl">
             <button className="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-bold shadow-lg shadow-blue-500/20">30 Days</button>
             <button className="px-4 py-2 text-slate-400 hover:text-white rounded-lg text-sm font-bold transition-colors">90 Days</button>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
          <StatCard 
            title="Total Claim Payout" 
            value={`₹${(data.totalClaimAmount / 10000000).toFixed(1)}Cr`} 
            change="+12.5%" 
            isPositive={false}
            icon={Activity}
          />
          <StatCard 
            title="Leakage Saved" 
            value={`₹${(data.savingsFromFraud / 100000).toFixed(1)}L`} 
            change="+8.2%" 
            isPositive={true}
            icon={ShieldAlert}
            color="emerald"
          />
          <StatCard 
            title="Avg. Turnaround" 
            value={`${data.averageTAT}h`} 
            change="-4.2h" 
            isPositive={true}
            icon={Clock}
            color="blue"
          />
          <StatCard 
            title="Rejection Rate" 
            value={`${data.rejectionRate}%`} 
            change="+1.2%" 
            isPositive={false}
            icon={Zap}
            color="amber"
          />
        </div>

        {/* Charts Row */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-10">
          
          {/* Volume Trend */}
          <div className="lg:col-span-2 bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8">
            <div className="flex justify-between items-center mb-8">
              <h3 className="text-xl font-bold flex items-center">
                <TrendingUp className="mr-2 text-blue-500" size={20} />
                Monthly Volume & Payout Trend
              </h3>
            </div>
            <div className="h-[350px]">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={data.monthlyTrend}>
                  <defs>
                    <linearGradient id="colorVolume" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" vertical={false} />
                  <XAxis dataKey="month" stroke="#64748b" axisLine={false} tickLine={false} />
                  <YAxis stroke="#64748b" axisLine={false} tickLine={false} />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #1e293b', borderRadius: '12px' }}
                    itemStyle={{ color: '#f8fafc' }}
                  />
                  <Area type="monotone" dataKey="volume" stroke="#3b82f6" strokeWidth={3} fillOpacity={1} fill="url(#colorVolume)" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Hospital Performance */}
          <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8">
            <h3 className="text-xl font-bold mb-8">Provider Performance</h3>
            <div className="space-y-6">
              {data.hospitalPerformance.map((h, i) => (
                <div key={i} className="flex items-center justify-between p-4 bg-slate-950/50 rounded-2xl border border-slate-800/50">
                  <div>
                    <p className="font-bold text-white text-sm">{h.name}</p>
                    <p className="text-xs text-slate-500 mt-1">{h.claims} Claims Submitted</p>
                  </div>
                  <div className="text-right">
                    <span className={`text-[10px] font-black px-2 py-1 rounded-md uppercase tracking-tighter ${
                      h.risk === 'HIGH' ? 'bg-red-500/20 text-red-400' :
                      h.risk === 'MEDIUM' ? 'bg-amber-500/20 text-amber-400' :
                      'bg-emerald-500/20 text-emerald-400'
                    }`}>
                      {h.risk} RISK
                    </span>
                    <p className="text-xs font-bold text-slate-300 mt-1">{h.rejections} Rejections</p>
                  </div>
                </div>
              ))}
            </div>
            <button className="w-full mt-8 py-3 bg-slate-800 hover:bg-slate-700 text-slate-300 font-bold rounded-xl text-sm transition-all border border-slate-700/50">
              View Detailed Network Report
            </button>
          </div>

        </div>

      </div>
    </div>
  );
};

const StatCard = ({ title, value, change, isPositive, icon: Icon, color = "blue" }) => {
  const colors = {
    blue: "text-blue-500 bg-blue-500/10",
    emerald: "text-emerald-500 bg-emerald-500/10",
    amber: "text-amber-500 bg-amber-500/10"
  };

  return (
    <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-6 transition-all hover:scale-[1.02] hover:border-slate-700 cursor-default">
      <div className="flex justify-between items-start mb-4">
        <div className={`p-3 rounded-2xl ${colors[color]}`}>
          <Icon size={24} />
        </div>
        <div className={`flex items-center text-xs font-bold px-2 py-1 rounded-lg ${isPositive ? 'text-emerald-400 bg-emerald-400/10' : 'text-red-400 bg-red-400/10'}`}>
          {isPositive ? <ArrowUpRight size={14} className="mr-1" /> : <ArrowDownRight size={14} className="mr-1" />}
          {change}
        </div>
      </div>
      <div>
        <p className="text-slate-400 text-sm font-medium">{title}</p>
        <p className="text-3xl font-black text-white mt-1">{value}</p>
      </div>
    </div>
  );
};

export default CarrierAnalytics;
