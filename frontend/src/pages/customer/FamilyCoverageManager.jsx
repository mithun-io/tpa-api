import React from 'react';
import { Users, UserPlus, Heart, Shield } from 'lucide-react';

export default function FamilyCoverageManager() {
  const members = [
    { id: 1, name: 'Mithun (Self)', relation: 'Primary', dob: '1985-05-20', status: 'Active' },
    { id: 2, name: 'Sarah (Spouse)', relation: 'Dependent', dob: '1988-08-14', status: 'Active' },
    { id: 3, name: 'Leo (Child)', relation: 'Dependent', dob: '2015-11-30', status: 'Active' },
  ];

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl shadow-xl flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Users className="text-pink-400" /> Family Coverage
          </h1>
          <p className="text-slate-400 mt-2">Manage dependents and view their active insurance status.</p>
        </div>
        <button className="bg-pink-600 hover:bg-pink-500 text-white px-4 py-2 rounded-xl text-sm font-bold flex items-center gap-2">
          <UserPlus size={16} /> Add Dependent
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {members.map(m => (
          <div key={m.id} className="bg-slate-900 border border-slate-800 p-6 rounded-2xl flex flex-col items-center text-center">
            <div className="w-16 h-16 bg-slate-800 rounded-full flex items-center justify-center mb-4 border border-slate-700">
              <Heart className="text-pink-400" size={24} />
            </div>
            <h3 className="text-lg font-bold text-white mb-1">{m.name}</h3>
            <span className="text-xs bg-slate-800 text-slate-300 px-2 py-1 rounded mb-4">{m.relation}</span>
            <div className="w-full space-y-2 text-sm">
              <div className="flex justify-between border-b border-slate-800 pb-2">
                <span className="text-slate-500">DOB</span><span className="text-slate-300">{m.dob}</span>
              </div>
              <div className="flex justify-between border-b border-slate-800 pb-2">
                <span className="text-slate-500">Status</span>
                <span className="text-emerald-400 flex items-center gap-1"><Shield size={12}/> {m.status}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
