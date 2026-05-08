import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  Activity, Server, Database, AlertTriangle, 
  RefreshCcw, CheckCircle, Clock, Zap
} from 'lucide-react';

const SystemMonitor = () => {
  const [health, setHealth] = useState(null);
  const [topics, setTopics] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchMetrics = async () => {
    setLoading(true);
    try {
      const [healthRes, topicsRes] = await Promise.all([
        axios.get('/api/v1/admin/kafka/health'),
        axios.get('/api/v1/admin/kafka/topics')
      ]);
      setHealth(healthRes.data);
      setTopics(topicsRes.data);
    } catch (error) {
      console.error('Failed to fetch system metrics', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMetrics();
    const interval = setInterval(fetchMetrics, 30000);
    return () => clearInterval(interval);
  }, []);

  if (loading && !health) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  const isConnected = health?.kafkaStatus === 'CONNECTED';

  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-100 tracking-tight flex items-center gap-2">
            <Server className="text-blue-500" /> System Monitoring Hub
          </h1>
          <p className="text-sm text-slate-400 mt-1">Real-time Kafka event pipeline and DLQ metrics</p>
        </div>
        <button 
          onClick={fetchMetrics}
          className="flex items-center gap-2 bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 px-4 py-2 rounded-xl text-sm font-medium transition-colors shadow-sm"
        >
          <RefreshCcw className="w-4 h-4" /> Refresh
        </button>
      </div>

      {/* Global Status */}
      <div className={`p-5 rounded-2xl border flex items-center gap-4 ${isConnected ? 'bg-emerald-500/10 border-emerald-500/20' : 'bg-red-500/10 border-red-500/20'}`}>
        <div className={`p-3 rounded-xl ${isConnected ? 'bg-emerald-500/20' : 'bg-red-500/20'}`}>
          {isConnected ? <CheckCircle className="w-6 h-6 text-emerald-400" /> : <AlertTriangle className="w-6 h-6 text-red-400" />}
        </div>
        <div>
          <h3 className={`text-lg font-bold ${isConnected ? 'text-emerald-400' : 'text-red-400'}`}>
            Kafka Event Broker: {health?.kafkaStatus || 'UNKNOWN'}
          </h3>
          <p className="text-sm text-slate-400 mt-0.5">Last synchronized: {health?.retrievedAt ? new Date(health.retrievedAt).toLocaleTimeString() : 'N/A'}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* KPI Cards */}
        <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Total Events</h3>
            <Activity className="text-blue-400" />
          </div>
          <p className="text-3xl font-black text-white">{health?.totalEvents || 0}</p>
          <p className="text-xs text-slate-500 mt-2 font-medium">{health?.successRate || 100}% processing success rate</p>
        </div>

        <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Pending Tasks</h3>
            <Clock className="text-amber-400" />
          </div>
          <p className="text-3xl font-black text-white">{health?.pendingEvents || 0}</p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Currently in processing queues</p>
        </div>

        <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 text-sm font-bold uppercase tracking-wider">Dead Letter Queue</h3>
            <AlertTriangle className="text-red-400" />
          </div>
          <p className="text-3xl font-black text-white">{health?.dlqMessageCount || 0}</p>
          <p className="text-xs text-slate-500 mt-2 font-medium">Failed events requiring intervention</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Pipeline Stage Breakdown */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden">
          <div className="p-5 border-b border-slate-700 flex items-center justify-between">
            <h3 className="font-bold text-slate-100 flex items-center gap-2">
              <Zap className="w-5 h-5 text-blue-400" /> Pipeline Stage Breakdown
            </h3>
          </div>
          <div className="p-5 space-y-4 max-h-[300px] overflow-y-auto">
            {health?.stageBreakdown && Object.entries(health.stageBreakdown).map(([stage, count]) => (
              <div key={stage}>
                <div className="flex justify-between text-sm mb-1.5">
                  <span className="text-slate-300 font-medium">{stage}</span>
                  <span className="text-slate-400 font-bold">{count}</span>
                </div>
                <div className="w-full bg-slate-900 rounded-full h-1.5">
                  <div className="bg-blue-500 h-1.5 rounded-full" style={{ width: `${Math.min((count / (health.totalEvents || 1)) * 100, 100)}%` }}></div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Kafka Topics */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden">
          <div className="p-5 border-b border-slate-700 flex items-center justify-between">
            <h3 className="font-bold text-slate-100 flex items-center gap-2">
              <Database className="w-5 h-5 text-indigo-400" /> Active Kafka Topics
            </h3>
            <span className="text-xs font-bold text-slate-400 bg-slate-900 px-2 py-1 rounded-md">{topics?.totalTopics || 0} Topics</span>
          </div>
          <div className="p-0 max-h-[300px] overflow-y-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-900/50 sticky top-0">
                <tr>
                  <th className="p-4 font-bold text-slate-500 uppercase tracking-widest text-[10px]">Topic Name</th>
                  <th className="p-4 font-bold text-slate-500 uppercase tracking-widest text-[10px]">Partitions</th>
                  <th className="p-4 font-bold text-slate-500 uppercase tracking-widest text-[10px] text-right">Type</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700/50">
                {topics?.topics?.map((topic, i) => (
                  <tr key={i} className="hover:bg-white/5 transition-colors">
                    <td className="p-4 text-slate-300 font-medium">{topic.name}</td>
                    <td className="p-4 text-slate-400 font-mono text-xs">{topic.partitions}</td>
                    <td className="p-4 text-right">
                      {topic.isDlq ? 
                        <span className="text-[10px] font-bold text-red-400 bg-red-500/10 px-2 py-1 rounded-md">DLQ</span> : 
                        <span className="text-[10px] font-bold text-emerald-400 bg-emerald-500/10 px-2 py-1 rounded-md">STANDARD</span>
                      }
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SystemMonitor;
