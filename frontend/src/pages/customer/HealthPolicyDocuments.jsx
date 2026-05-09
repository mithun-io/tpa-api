import React from 'react';
import { FileText, Download, Eye, FileDigit } from 'lucide-react';

export default function HealthPolicyDocuments() {
  const docs = [
    { id: 1, name: 'Comprehensive Health Plan 2026', type: 'PDF', size: '2.4 MB', date: '2026-01-01' },
    { id: 2, name: 'Dental Add-on Coverage', type: 'PDF', size: '1.1 MB', date: '2026-02-15' },
    { id: 3, name: 'TPA Network Hospitals List', type: 'CSV', size: '0.8 MB', date: '2026-05-01' },
    { id: 4, name: 'Terms and Conditions', type: 'PDF', size: '3.5 MB', date: '2025-12-20' },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <FileDigit className="text-indigo-400" /> Policy Documents Vault
          </h1>
          <p className="text-slate-400 mt-2">Securely access all your official health insurance documents.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {docs.map(doc => (
          <div key={doc.id} className="bg-slate-900 border border-slate-800 p-5 rounded-2xl hover:border-indigo-500/50 transition-colors">
            <div className="w-12 h-12 bg-indigo-500/10 rounded-xl flex items-center justify-center mb-4">
              <FileText className="text-indigo-400" />
            </div>
            <h3 className="text-slate-200 font-semibold mb-1 truncate" title={doc.name}>{doc.name}</h3>
            <p className="text-xs text-slate-500 mb-4">{doc.type} • {doc.size} • {doc.date}</p>
            
            <div className="flex gap-2">
              <button className="flex-1 bg-slate-800 hover:bg-slate-700 text-slate-300 py-1.5 rounded-lg text-xs font-semibold flex items-center justify-center gap-2 transition-colors">
                <Eye size={14} /> View
              </button>
              <button className="flex-1 bg-indigo-600 hover:bg-indigo-500 text-white py-1.5 rounded-lg text-xs font-semibold flex items-center justify-center gap-2 transition-colors">
                <Download size={14} /> DL
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
