import React from 'react';
import { ShieldAlert, AlertOctagon, FileSearch, Flag } from 'lucide-react';

export default function FraudInvestigationHub() {
  const flags = [
    { id: 'C-9921', risk: 'HIGH', type: 'Duplicate Billing', amount: '$4,200', date: '2026-05-08' },
    { id: 'C-8810', risk: 'CRITICAL', type: 'Phantom Hospital', amount: '$12,500', date: '2026-05-07' },
    { id: 'C-7732', risk: 'MEDIUM', type: 'Upcoding Suspected', amount: '$1,800', date: '2026-05-06' },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <ShieldAlert className="text-red-500" /> Fraud Investigation Hub
          </h1>
          <p className="text-slate-400 mt-2">Deep-dive forensics into AI-flagged anomalous claims.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="col-span-2 bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
          <div className="p-4 border-b border-slate-800 bg-slate-800/30 font-bold text-slate-200">Active Investigations</div>
          <table className="w-full text-sm text-left text-slate-300">
            <thead className="bg-slate-800/50 text-slate-500 text-xs uppercase">
              <tr>
                <th className="px-4 py-3">Claim ID</th>
                <th className="px-4 py-3">Risk Tier</th>
                <th className="px-4 py-3">Fraud Type</th>
                <th className="px-4 py-3">Amount</th>
                <th className="px-4 py-3">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/50">
              {flags.map(f => (
                <tr key={f.id} className="hover:bg-slate-800/30">
                  <td className="px-4 py-3 font-mono text-slate-400">{f.id}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded text-[10px] font-bold ${f.risk === 'CRITICAL' ? 'bg-red-500/20 text-red-500' : f.risk === 'HIGH' ? 'bg-orange-500/20 text-orange-400' : 'bg-amber-500/20 text-amber-400'}`}>
                      {f.risk}
                    </span>
                  </td>
                  <td className="px-4 py-3">{f.type}</td>
                  <td className="px-4 py-3 font-mono">{f.amount}</td>
                  <td className="px-4 py-3">
                    <button className="bg-slate-800 hover:bg-slate-700 p-1.5 rounded text-white"><FileSearch size={14}/></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="space-y-4">
          <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
            <AlertOctagon className="text-red-500 mb-2" />
            <h3 className="text-slate-400 text-sm">Critical Threats</h3>
            <p className="text-3xl font-black text-white">14</p>
          </div>
          <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
            <Flag className="text-amber-500 mb-2" />
            <h3 className="text-slate-400 text-sm">Open Investigations</h3>
            <p className="text-3xl font-black text-white">42</p>
          </div>
        </div>
      </div>
    </div>
  );
}
