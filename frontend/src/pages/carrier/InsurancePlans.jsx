import React, { useState } from 'react';
import { useDemoData } from '../../context/DemoDataProvider';
import { ShoppingBag, ChevronRight, CheckCircle2, XCircle, FileText, Calculator, Shield, Activity } from 'lucide-react';

export default function InsurancePlans() {
  const { insurancePlans } = useDemoData();
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [calcAge, setCalcAge] = useState(30);
  const [calcDependents, setCalcDependents] = useState(0);

  const calculatePremium = (basePremium) => {
    let multiplier = 1 + ((calcAge - 30) * 0.02);
    if (calcDependents > 0) multiplier += (calcDependents * 0.15);
    return Math.floor(basePremium * multiplier);
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 font-sans p-8 selection:bg-blue-200">
      
      {/* Marketplace Header */}
      <div className="mb-10 text-center max-w-3xl mx-auto">
        <div className="inline-flex items-center gap-2 bg-blue-100 text-blue-700 px-4 py-1.5 rounded-full text-sm font-bold mb-4">
          <ShoppingBag size={16} />
          <span>ENTERPRISE MARKETPLACE</span>
        </div>
        <h1 className="text-4xl font-black text-slate-900 tracking-tight mb-4">Insurance Product Catalog</h1>
        <p className="text-slate-500 text-lg">Browse, compare, and configure enterprise-grade insurance products for your organization. Select any plan to view detailed coverage matrices and run live premium simulations.</p>
      </div>

      <div className="flex gap-8 relative items-start">
        
        {/* Main Catalog Grid */}
        <div className={`grid gap-6 transition-all duration-500 ${selectedPlan ? 'w-1/2 grid-cols-1' : 'w-full grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4'}`}>
          {insurancePlans.map(plan => (
            <div 
              key={plan.id}
              onClick={() => setSelectedPlan(plan)}
              className={`bg-white rounded-3xl p-6 cursor-pointer border-2 transition-all duration-300 hover:shadow-2xl hover:-translate-y-1 ${selectedPlan?.id === plan.id ? 'border-blue-500 shadow-xl ring-4 ring-blue-500/10' : 'border-slate-100 shadow-sm'}`}
            >
              <div className="flex justify-between items-start mb-6">
                <div className={`w-12 h-12 rounded-2xl flex items-center justify-center text-2xl shadow-inner ${plan.border.replace('border-', 'bg-').replace('/30', '/10')}`}>
                  {plan.icon}
                </div>
                <div className="bg-slate-100 text-slate-500 text-xs font-bold px-2 py-1 rounded-md">{plan.type}</div>
              </div>
              
              <h3 className="text-xl font-bold text-slate-900 mb-1">{plan.name}</h3>
              <p className="text-slate-500 text-sm mb-6 h-10 line-clamp-2">Comprehensive {plan.type.toLowerCase()} coverage designed for enterprise security.</p>
              
              <div className="pt-4 border-t border-slate-100 flex items-end justify-between">
                <div>
                  <div className="text-xs text-slate-400 font-bold mb-1">STARTING AT</div>
                  <div className="text-2xl font-black text-slate-900">₹{plan.premium.toLocaleString()}<span className="text-sm font-medium text-slate-400">/yr</span></div>
                </div>
                <div className="w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center text-slate-400">
                  <ChevronRight size={16} />
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Expandable Product Drawer (Side Panel) */}
        {selectedPlan && (
          <div className="w-1/2 bg-white rounded-3xl shadow-2xl border border-slate-200 overflow-hidden sticky top-8 flex flex-col h-[calc(100vh-4rem)] animate-[fadeInRight_0.3s_ease-out]">
            
            {/* Drawer Header */}
            <div className={`p-8 bg-gradient-to-br ${selectedPlan.gradient}`}>
              <div className="flex justify-between items-start mb-6">
                <div className="w-16 h-16 bg-white rounded-2xl shadow-sm flex items-center justify-center text-3xl">
                  {selectedPlan.icon}
                </div>
                <button onClick={() => setSelectedPlan(null)} className="bg-white/50 hover:bg-white text-slate-700 p-2 rounded-full transition-colors backdrop-blur-sm">
                  <XCircle size={24} />
                </button>
              </div>
              <div className="inline-block bg-white/60 backdrop-blur-sm text-slate-800 text-xs font-bold px-3 py-1 rounded-full mb-3">
                {selectedPlan.id}
              </div>
              <h2 className="text-3xl font-black text-slate-900 mb-2">{selectedPlan.name}</h2>
              <div className="flex gap-4 text-sm font-medium text-slate-700">
                <span className="flex items-center gap-1"><Shield size={16} /> Coverage: {selectedPlan.coverageDisplay}</span>
                <span className="flex items-center gap-1"><Activity size={16} /> Approval: {selectedPlan.approvalRatio}%</span>
              </div>
            </div>

            <div className="flex-1 overflow-y-auto p-8 custom-scrollbar">
              
              {/* Premium Simulator */}
              <div className="bg-slate-50 border border-slate-200 rounded-2xl p-6 mb-8">
                <div className="flex items-center gap-2 mb-6">
                  <Calculator size={20} className="text-blue-600" />
                  <h3 className="font-bold text-slate-900 text-lg">Premium Simulator</h3>
                </div>
                <div className="grid grid-cols-2 gap-6 mb-6">
                  <div>
                    <label className="block text-xs font-bold text-slate-500 mb-2">PRIMARY AGE: {calcAge}</label>
                    <input 
                      type="range" min="18" max="75" value={calcAge} onChange={(e) => setCalcAge(parseInt(e.target.value))}
                      className="w-full accent-blue-600"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-bold text-slate-500 mb-2">DEPENDENTS: {calcDependents}</label>
                    <input 
                      type="range" min="0" max="5" value={calcDependents} onChange={(e) => setCalcDependents(parseInt(e.target.value))}
                      className="w-full accent-blue-600"
                    />
                  </div>
                </div>
                <div className="bg-blue-600 text-white rounded-xl p-4 flex justify-between items-center shadow-lg shadow-blue-600/20">
                  <span className="font-bold">Estimated Annual Premium</span>
                  <span className="text-2xl font-black">₹{calculatePremium(selectedPlan.premium).toLocaleString()}</span>
                </div>
              </div>

              {/* Coverage Matrix Table */}
              <div className="mb-8">
                <h3 className="font-bold text-slate-900 text-lg mb-4 flex items-center gap-2">
                  <FileText size={20} className="text-blue-600" /> Coverage Matrix
                </h3>
                <div className="border border-slate-200 rounded-xl overflow-hidden">
                  <table className="w-full text-sm text-left">
                    <tbody className="divide-y divide-slate-100">
                      <tr className="bg-slate-50"><th className="px-4 py-3 font-medium text-slate-500 w-1/3">Deductible</th><td className="px-4 py-3 font-bold text-slate-900">₹{selectedPlan.deductible.toLocaleString()}</td></tr>
                      <tr><th className="px-4 py-3 font-medium text-slate-500">Waiting Period</th><td className="px-4 py-3 font-bold text-slate-900">{selectedPlan.waitingPeriodDisplay}</td></tr>
                      <tr className="bg-slate-50"><th className="px-4 py-3 font-medium text-slate-500">Active Policies</th><td className="px-4 py-3 font-bold text-blue-600">{selectedPlan.activeSubscribers.toLocaleString()}</td></tr>
                      <tr><th className="px-4 py-3 font-medium text-slate-500">Claim Frequency</th><td className="px-4 py-3 font-bold text-slate-900">{selectedPlan.claimFrequency}%</td></tr>
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Included / Excluded */}
              <div className="grid grid-cols-2 gap-6">
                <div>
                  <h4 className="font-bold text-slate-900 mb-3 flex items-center gap-2"><CheckCircle2 size={16} className="text-emerald-500"/> Optional Riders</h4>
                  <ul className="space-y-2">
                    {selectedPlan.riders.map((r, i) => (
                      <li key={i} className="text-sm text-slate-600 flex items-start gap-2">
                        <div className="mt-1 w-1.5 h-1.5 rounded-full bg-emerald-500 shrink-0" />
                        {r}
                      </li>
                    ))}
                  </ul>
                </div>
                <div>
                  <h4 className="font-bold text-slate-900 mb-3 flex items-center gap-2"><XCircle size={16} className="text-red-500"/> Exclusions</h4>
                  <ul className="space-y-2">
                    {selectedPlan.exclusions.map((e, i) => (
                      <li key={i} className="text-sm text-slate-600 flex items-start gap-2">
                        <div className="mt-1 w-1.5 h-1.5 rounded-full bg-red-500 shrink-0" />
                        {e}
                      </li>
                    ))}
                  </ul>
                </div>
              </div>

            </div>
            
            {/* Drawer Footer */}
            <div className="p-6 border-t border-slate-200 bg-slate-50 flex justify-end gap-4 shrink-0">
              <button className="px-6 py-2.5 rounded-xl font-bold text-slate-600 hover:bg-slate-200 transition-colors">
                Export Specs PDF
              </button>
              <button className="px-6 py-2.5 rounded-xl font-bold text-white bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-600/20 transition-all hover:-translate-y-0.5">
                Generate Proposal
              </button>
            </div>
          </div>
        )}
      </div>

      <style>{`
        @keyframes fadeInRight {
          from { opacity: 0; transform: translateX(20px); }
          to { opacity: 1; transform: translateX(0); }
        }
        .custom-scrollbar::-webkit-scrollbar { width: 6px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
      `}</style>
    </div>
  );
}
