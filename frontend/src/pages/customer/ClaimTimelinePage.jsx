import React from 'react';
import { Clock, History, CheckCircle, Circle, MapPin } from 'lucide-react';

export default function ClaimTimelinePage() {
  const events = [
    { id: 1, title: 'Claim Submitted', date: '2026-05-01 10:00 AM', status: 'completed', desc: 'Hospital submitted initial claim documents.' },
    { id: 2, title: 'AI Validation', date: '2026-05-01 10:05 AM', status: 'completed', desc: 'Automated fraud and policy checks passed.' },
    { id: 3, title: 'Admin Review', date: '2026-05-02 14:30 PM', status: 'completed', desc: 'TPA Admin approved the claim line items.' },
    { id: 4, title: 'Carrier Finalization', date: 'Pending', status: 'active', desc: 'Waiting for carrier underwriter approval.' },
    { id: 5, title: 'Payment Processing', date: 'Pending', status: 'upcoming', desc: 'Funds transfer to hospital account.' },
  ];

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl">
        <h1 className="text-2xl font-bold text-white flex items-center gap-2">
          <History className="text-orange-400" /> Claim Lifetime Timeline
        </h1>
        <p className="text-slate-400 mt-2">End-to-end transparent view of your claim's progression.</p>
      </div>

      <div className="bg-slate-900 border border-slate-800 p-8 rounded-2xl relative">
        <div className="absolute left-[39px] top-8 bottom-8 w-px bg-slate-800"></div>
        <div className="space-y-8">
          {events.map((e, idx) => (
            <div key={e.id} className="relative flex items-start gap-6">
              <div className="relative z-10 flex-shrink-0 bg-slate-900 py-1">
                {e.status === 'completed' ? (
                  <CheckCircle className="text-emerald-500 bg-slate-900" size={20} />
                ) : e.status === 'active' ? (
                  <Circle className="text-orange-500 fill-orange-500/20 bg-slate-900 animate-pulse" size={20} />
                ) : (
                  <Circle className="text-slate-600 bg-slate-900" size={20} />
                )}
              </div>
              <div className={`flex-1 ${e.status === 'upcoming' ? 'opacity-50' : ''}`}>
                <h3 className={`text-lg font-bold ${e.status === 'completed' ? 'text-slate-200' : e.status === 'active' ? 'text-orange-400' : 'text-slate-500'}`}>
                  {e.title}
                </h3>
                <div className="flex items-center gap-2 mt-1 mb-2">
                  <Clock size={12} className="text-slate-500" />
                  <span className="text-xs text-slate-400 font-medium">{e.date}</span>
                </div>
                <div className="bg-slate-800/50 border border-slate-700/50 p-3 rounded-xl text-sm text-slate-300">
                  {e.desc}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
