import React from 'react';
import { FolderDown, FileSpreadsheet, FileText, Download } from 'lucide-react';

export default function DownloadCenter() {
  const exports = [
    { id: 1, name: '2025 Claim History Export', type: 'CSV', status: 'Ready' },
    { id: 2, name: 'Tax Deductibles Summary', type: 'PDF', status: 'Ready' },
    { id: 3, name: 'Network Hospitals Complete List', type: 'XLSX', status: 'Processing...' },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <FolderDown className="text-cyan-400" /> Download Center
          </h1>
          <p className="text-slate-400 mt-2">Generate and download bulk reports and historical claim data.</p>
        </div>
        <button className="bg-cyan-600 hover:bg-cyan-500 text-white px-4 py-2 rounded-xl text-sm font-bold flex items-center gap-2">
          New Export Request
        </button>
      </div>

      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
        <table className="w-full text-sm text-left text-slate-300">
          <thead className="bg-slate-800/50 border-b border-slate-800 text-slate-500 uppercase text-xs">
            <tr>
              <th className="px-6 py-4">Report Name</th>
              <th className="px-6 py-4">Format</th>
              <th className="px-6 py-4">Status</th>
              <th className="px-6 py-4 text-right">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/50">
            {exports.map(e => (
              <tr key={e.id} className="hover:bg-slate-800/30">
                <td className="px-6 py-4 font-medium text-slate-200">{e.name}</td>
                <td className="px-6 py-4">
                  <span className="flex items-center gap-1.5 text-xs font-bold text-slate-400">
                    {e.type === 'PDF' ? <FileText size={14}/> : <FileSpreadsheet size={14}/>} {e.type}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <span className={`text-xs px-2 py-1 rounded font-bold ${e.status === 'Ready' ? 'bg-emerald-500/10 text-emerald-400' : 'bg-amber-500/10 text-amber-400'}`}>
                    {e.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-right">
                  <button disabled={e.status !== 'Ready'} className="bg-slate-800 hover:bg-slate-700 disabled:opacity-50 text-white p-2 rounded-lg transition-colors">
                    <Download size={16} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
