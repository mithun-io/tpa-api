import React from 'react';
import { Wallet, ShieldCheck, Download, CreditCard, Calendar, Activity } from 'lucide-react';

const HealthWallet = () => {
  const cardData = {
    holderName: "Jane Smith",
    policyNumber: "POL-US-2024-123456",
    expiryDate: "12/2025",
    memberId: "MEM-8829-110",
    coverageType: "Family Floater",
    totalLimit: 50000,
    availableLimit: 38500
  };

  return (
    <div className="space-y-6 max-w-[1400px] mx-auto">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-100 tracking-tight">Health Wallet</h1>
          <p className="text-sm text-slate-400 mt-1">Your digital health ID and coverage details</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-xl text-sm font-medium transition-all shadow-lg shadow-blue-900/20">
          <Download className="w-4 h-4" /> Download e-Card
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Digital Card Section */}
        <div className="lg:col-span-2 space-y-6">
          <div className="relative overflow-hidden aspect-[1.6/1] bg-gradient-to-br from-indigo-600 via-blue-600 to-blue-700 rounded-3xl shadow-2xl p-8 border border-white/10 group transition-all duration-500 hover:scale-[1.01]">
            {/* Background pattern */}
            <div className="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full blur-3xl -mr-20 -mt-20 group-hover:bg-white/10 transition-all duration-700" />
            <div className="absolute bottom-0 left-0 w-48 h-48 bg-black/10 rounded-full blur-2xl -ml-16 -mb-16" />
            
            <div className="relative h-full flex flex-col justify-between">
              <div className="flex justify-between items-start">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-white/10 backdrop-blur-md flex items-center justify-center border border-white/20">
                    <span className="text-white font-black text-lg">T</span>
                  </div>
                  <div>
                    <p className="text-white font-bold text-sm leading-tight">TPA ClaimSys</p>
                    <p className="text-blue-100/60 text-[10px] font-bold uppercase tracking-widest">Health Network</p>
                  </div>
                </div>
                <div className="flex items-center gap-2 bg-white/10 backdrop-blur-md px-3 py-1.5 rounded-lg border border-white/20">
                  <ShieldCheck className="w-4 h-4 text-emerald-400" />
                  <span className="text-white text-[10px] font-bold uppercase tracking-widest">Active</span>
                </div>
              </div>

              <div className="space-y-1">
                <p className="text-blue-200/60 text-[10px] font-bold uppercase tracking-widest">Policy Holder</p>
                <p className="text-white text-2xl font-bold tracking-tight">{cardData.holderName}</p>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-blue-200/60 text-[10px] font-bold uppercase tracking-widest">Member ID</p>
                  <p className="text-white font-mono font-bold text-sm tracking-widest">{cardData.memberId}</p>
                </div>
                <div className="text-right">
                  <p className="text-blue-200/60 text-[10px] font-bold uppercase tracking-widest">Expires</p>
                  <p className="text-white font-bold text-sm">{cardData.expiryDate}</p>
                </div>
              </div>
            </div>
            
            {/* NFC Icon simulation */}
            <div className="absolute top-1/2 right-8 -translate-y-1/2 opacity-20">
              <Activity className="w-12 h-12 text-white" />
            </div>
          </div>

          <div className="bg-slate-800 rounded-2xl border border-slate-700 p-8">
            <h3 className="text-lg font-bold text-slate-100 mb-6 flex items-center gap-3">
              <CreditCard className="w-5 h-5 text-blue-400" /> Policy Information
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-8">
              {[
                { label: 'Policy Number', value: cardData.policyNumber },
                { label: 'Coverage Type', value: cardData.coverageType },
                { label: 'Total Sum Insured', value: `$${cardData.totalLimit.toLocaleString()}` },
                { label: 'Available Limit', value: `$${cardData.availableLimit.toLocaleString()}`, highlight: true },
              ].map((item, idx) => (
                <div key={idx} className="space-y-1">
                  <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest">{item.label}</p>
                  <p className={`text-lg font-bold ${item.highlight ? 'text-emerald-400' : 'text-slate-200'}`}>{item.value}</p>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Sidebar Info */}
        <div className="space-y-6">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6 overflow-hidden relative">
            <div className="absolute -top-10 -right-10 w-32 h-32 bg-blue-500/10 rounded-full blur-3xl" />
            <h3 className="text-sm font-bold text-slate-100 mb-4 flex items-center gap-2">
              <Calendar className="w-4 h-4 text-blue-400" /> Renewal Status
            </h3>
            <div className="space-y-4">
              <div className="flex justify-between text-sm">
                <span className="text-slate-400">Days Remaining</span>
                <span className="text-slate-200 font-bold">214 Days</span>
              </div>
              <div className="w-full bg-slate-700 h-2 rounded-full overflow-hidden">
                <div className="bg-blue-500 h-full rounded-full" style={{ width: '65%' }} />
              </div>
              <p className="text-xs text-slate-500 leading-relaxed italic">
                Your policy is valid until Dec 31, 2025. No action required at this time.
              </p>
            </div>
          </div>

          <div className="bg-emerald-500/10 border border-emerald-500/20 rounded-2xl p-6">
            <h3 className="text-sm font-bold text-emerald-400 mb-3 flex items-center gap-2">
              <ShieldCheck className="w-4 h-4" /> Network Advantage
            </h3>
            <p className="text-xs text-emerald-200/70 leading-relaxed">
              Present this digital card at any network hospital for <strong>100% Cashless</strong> medical services.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HealthWallet;
