import React from 'react';
import { Share2, Database, DownloadCloud, Activity } from 'lucide-react';

export default function ReinsuranceExport() {
  const jobs = [
    { id: 'JOB-901', target: 'Munich Re Sync', records: 14500, status: 'Completed', time: '2026-05-08 02:00 AM' },
    { id: 'JOB-902', target: 'Swiss Re Daily', records: 8200, status: 'Processing', time: 'In Progress' },
    { id: 'JOB-903', target: 'Lloyds Syndication', records: 0, status: 'Scheduled', time: '2026-05-09 00:00 AM' },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Share2 className="text-cyan-400" /> Reinsurance Data Export
          </h1>
          <p className="text-slate-400 mt-2">Manage automated data bridging and bordereaux exports to reinsurers.</p>
        </div>
        <button className="bg-cyan-600 hover:bg-cyan-500 text-white px-4 py-2 rounded-xl text-sm font-bold flex items-center gap-2">
          <Database size={16} /> Force Sync
        </button>
      </div>

      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
        <div className="p-4 border-b border-slate-800 bg-slate-800/30 flex justify-between items-center text-slate-200 font-bold">
          Active Sync Pipelines
          <Activity size={18} className="text-cyan-500 animate-pulse" />
        </div>
        <table className="w-full text-sm text-left text-slate-300">
          <thead className="bg-slate-800/50 text-slate-500 text-xs uppercase">
            <tr>
              <th className="px-6 py-4">Job ID</th>
              <th className="px-6 py-4">Reinsurer Endpoint</th>
              <th className="px-6 py-4">Records Bridged</th>
              <th className="px-6 py-4">Status</th>
              <th className="px-6 py-4">Execution Time</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/50">
            {jobs.map(j => (
              <tr key={j.id} className="hover:bg-slate-800/30">
                <td className="px-6 py-4 font-mono text-slate-400">{j.id}</td>
                <td className="px-6 py-4 font-bold text-slate-200">{j.target}</td>
                <td className="px-6 py-4 font-mono">{j.records.toLocaleString()}</td>
                <td className="px-6 py-4">
                  <span className={`px-2 py-1 rounded text-xs font-bold ${
                    j.status === 'Completed' ? 'bg-emerald-500/20 text-emerald-400' :
                    j.status === 'Processing' ? 'bg-blue-500/20 text-blue-400 animate-pulse' :
                    'bg-slate-700 text-slate-300'
                  }`}>
                    {j.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-xs text-slate-500">{j.time}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
