import React from 'react';
import { Network, Activity, Layers, ArrowRight } from 'lucide-react';

export default function ClaimLineageViewer() {
  const nodes = [
    { id: 1, label: 'Hospital API Ingestion', type: 'source', status: 'success' },
    { id: 2, label: 'OCR & Data Extraction', type: 'process', status: 'success' },
    { id: 3, label: 'AI Fraud Pre-check', type: 'ai', status: 'warning' },
    { id: 4, label: 'Admin Workbasket', type: 'manual', status: 'success' },
    { id: 5, label: 'Carrier API Sync', type: 'target', status: 'pending' },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl">
        <h1 className="text-2xl font-bold text-white flex items-center gap-2">
          <Network className="text-fuchsia-400" /> Full Claim Lineage
        </h1>
        <p className="text-slate-400 mt-2">Data provenance and state mutation tracking across all microservices.</p>
      </div>

      <div className="bg-slate-900 border border-slate-800 p-8 rounded-2xl overflow-x-auto">
        <div className="flex items-center min-w-max gap-4 p-8">
          {nodes.map((n, i) => (
            <React.Fragment key={n.id}>
              <div className={`p-6 rounded-xl border-2 flex flex-col items-center justify-center w-48 h-32 relative ${
                n.status === 'success' ? 'bg-emerald-500/10 border-emerald-500/30' :
                n.status === 'warning' ? 'bg-amber-500/10 border-amber-500/30' :
                'bg-slate-800 border-slate-700 opacity-60'
              }`}>
                {n.type === 'ai' ? <Activity className="text-fuchsia-400 mb-2"/> : <Layers className="text-slate-400 mb-2"/>}
                <span className="text-sm font-bold text-slate-200 text-center">{n.label}</span>
                <span className={`absolute -top-2 -right-2 text-[10px] px-2 py-0.5 rounded-full font-bold ${
                  n.status === 'success' ? 'bg-emerald-500 text-white' : n.status === 'warning' ? 'bg-amber-500 text-white' : 'bg-slate-600 text-white'
                }`}>{n.status.toUpperCase()}</span>
              </div>
              {i < nodes.length - 1 && (
                <div className="flex flex-col items-center px-2">
                  <span className="text-[10px] text-slate-500 mb-1 font-mono">TCP/JSON</span>
                  <ArrowRight className="text-slate-600" size={24} />
                </div>
              )}
            </React.Fragment>
          ))}
        </div>
      </div>
    </div>
  );
}
