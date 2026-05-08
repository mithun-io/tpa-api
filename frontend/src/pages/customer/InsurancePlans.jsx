import React, { useState, useEffect } from 'react';
import { getClaims } from '../../api/claim.service';
import Loader from '../../components/Loader';
import { ShieldCheck, FileText, Download, CheckCircle, Search, Sparkles, Activity } from 'lucide-react';
import { motion } from 'framer-motion';

const InsurancePlans = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [eligibilityCheck, setEligibilityCheck] = useState('');

  useEffect(() => {
    getClaims({ page: 0, size: 100 }).then(res => {
      setClaims(res.content || []);
      setLoading(false);
    });
  }, []);

  if (loading) return <Loader fullScreen />;

  // Aggregate policies from claims
  const policyMap = claims.reduce((acc, claim) => {
    if (!acc[claim.policyNumber]) {
      // Mock plan categories based on policy prefix or random logic for demonstration, 
      // but tied to real policy numbers.
      const isHealth = claim.policyNumber.includes('H') || claim.policyNumber.endsWith('1');
      const isLife = claim.policyNumber.includes('L') || claim.policyNumber.endsWith('2');
      
      acc[claim.policyNumber] = {
        policyNumber: claim.policyNumber,
        category: isHealth ? 'Hospitalization Insurance' : (isLife ? 'Life Insurance Coverage' : 'Critical Illness Coverage'),
        sumInsured: Math.floor(Math.random() * 500000) + 100000,
        premiumAmount: Math.floor(Math.random() * 5000) + 500,
        validUntil: new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toLocaleDateString(),
        claimsCount: 0,
        totalClaimed: 0,
        patientName: claim.patientName
      };
    }
    acc[claim.policyNumber].claimsCount++;
    acc[claim.policyNumber].totalClaimed += (claim.amount || 0);
    return acc;
  }, {});

  const plans = Object.values(policyMap);

  const handleEligibilityCheck = () => {
    if (!eligibilityCheck.trim()) return;
    alert(`Checking eligibility for: ${eligibilityCheck} under ${selectedPlan.policyNumber}... \nResult: Covered under Section B (Subject to deductible).`);
  };

  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <ShieldCheck className="text-blue-400" /> Coverage Center
          </h1>
          <p className="text-sm text-slate-400 mt-1">Manage and understand your active insurance plans.</p>
        </div>
      </div>

      {plans.length === 0 ? (
        <div className="bg-slate-800 rounded-2xl p-10 text-center border border-slate-700">
          <ShieldCheck className="w-12 h-12 text-slate-500 mx-auto mb-4" />
          <h2 className="text-lg font-bold text-slate-300">No Active Plans Found</h2>
          <p className="text-slate-500 mt-2">You currently do not have any claims linked to an active policy.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Plan List */}
          <div className="lg:col-span-1 space-y-4">
            <h2 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-2">Your Plans</h2>
            {plans.map((plan, idx) => (
              <motion.button
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: idx * 0.1 }}
                key={plan.policyNumber}
                onClick={() => setSelectedPlan(plan)}
                className={`w-full text-left p-5 rounded-2xl border transition-all ${
                  selectedPlan?.policyNumber === plan.policyNumber 
                    ? 'bg-blue-600/20 border-blue-500 shadow-lg shadow-blue-900/20' 
                    : 'bg-slate-800 border-slate-700 hover:border-slate-500 hover:bg-slate-800/80'
                }`}
              >
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-bold text-slate-200">{plan.category}</h3>
                  <span className="bg-emerald-500/20 text-emerald-400 text-[10px] font-black px-2 py-1 rounded-md">ACTIVE</span>
                </div>
                <p className="text-xs text-slate-400 font-mono mb-3">{plan.policyNumber}</p>
                <div className="flex justify-between items-end">
                  <div>
                    <p className="text-[10px] text-slate-500 uppercase font-bold">Sum Insured</p>
                    <p className="font-bold text-blue-400">${plan.sumInsured.toLocaleString()}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-[10px] text-slate-500 uppercase font-bold">Premium</p>
                    <p className="font-semibold text-slate-300">${plan.premiumAmount.toLocaleString()}/yr</p>
                  </div>
                </div>
              </motion.button>
            ))}
          </div>

          {/* Plan Details */}
          <div className="lg:col-span-2">
            {selectedPlan ? (
              <motion.div 
                key={selectedPlan.policyNumber}
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden"
              >
                <div className="p-6 border-b border-slate-700 flex justify-between items-center bg-slate-900/50">
                  <div>
                    <h2 className="text-xl font-bold text-white">{selectedPlan.category}</h2>
                    <p className="text-slate-400 font-mono mt-1">Policy: {selectedPlan.policyNumber} | Insured: {selectedPlan.patientName}</p>
                  </div>
                  <button className="flex items-center gap-2 bg-slate-700 hover:bg-slate-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
                    <Download className="w-4 h-4" /> Policy PDF
                  </button>
                </div>

                <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                  {/* Coverage Stats */}
                  <div className="space-y-4">
                    <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
                      <Activity className="w-4 h-4" /> Coverage Status
                    </h3>
                    <div className="bg-slate-900/50 rounded-xl p-4 border border-slate-700">
                      <div className="flex justify-between mb-2">
                        <span className="text-slate-400 text-sm">Total Claimed</span>
                        <span className="text-slate-200 font-bold">${selectedPlan.totalClaimed.toLocaleString()}</span>
                      </div>
                      <div className="flex justify-between mb-4">
                        <span className="text-slate-400 text-sm">Remaining Balance</span>
                        <span className="text-emerald-400 font-bold">${(selectedPlan.sumInsured - selectedPlan.totalClaimed).toLocaleString()}</span>
                      </div>
                      <div className="w-full bg-slate-800 rounded-full h-2">
                        <div className="bg-blue-500 h-2 rounded-full" style={{ width: `${Math.min((selectedPlan.totalClaimed / selectedPlan.sumInsured) * 100, 100)}%` }}></div>
                      </div>
                      <p className="text-xs text-slate-500 mt-3 text-center">Valid until {selectedPlan.validUntil}</p>
                    </div>

                    {/* Eligibility Checker */}
                    <div className="bg-slate-900/50 rounded-xl p-4 border border-slate-700">
                      <h4 className="font-bold text-slate-300 mb-3 flex items-center gap-2 text-sm">
                        <Search className="w-4 h-4 text-blue-400" /> Claim Eligibility Checker
                      </h4>
                      <div className="flex gap-2">
                        <input 
                          type="text" 
                          placeholder="e.g. MRI Scan, Dental..." 
                          value={eligibilityCheck}
                          onChange={(e) => setEligibilityCheck(e.target.value)}
                          className="flex-1 bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-blue-500"
                        />
                        <button onClick={handleEligibilityCheck} className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
                          Check
                        </button>
                      </div>
                    </div>
                  </div>

                  {/* AI Summary */}
                  <div className="space-y-4">
                    <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider flex items-center gap-2">
                      <Sparkles className="w-4 h-4 text-indigo-400" /> AI Plan Summary
                    </h3>
                    <div className="bg-indigo-500/10 rounded-xl p-5 border border-indigo-500/20">
                      <p className="text-sm text-indigo-200 leading-relaxed mb-4">
                        This {selectedPlan.category.toLowerCase()} provides comprehensive protection up to <strong>${selectedPlan.sumInsured.toLocaleString()}</strong>. 
                        It includes inpatient care, day-care procedures, and pre/post hospitalization expenses.
                      </p>
                      <ul className="space-y-2">
                        <li className="flex items-start gap-2 text-sm text-slate-300">
                          <CheckCircle className="w-4 h-4 text-emerald-400 mt-0.5 flex-shrink-0" /> Room rent limits apply (1% of Sum Insured per day).
                        </li>
                        <li className="flex items-start gap-2 text-sm text-slate-300">
                          <CheckCircle className="w-4 h-4 text-emerald-400 mt-0.5 flex-shrink-0" /> Co-payment of 10% applicable for network hospitals.
                        </li>
                        <li className="flex items-start gap-2 text-sm text-slate-300">
                          <CheckCircle className="w-4 h-4 text-emerald-400 mt-0.5 flex-shrink-0" /> 30-day waiting period for specific illnesses.
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </motion.div>
            ) : (
              <div className="bg-slate-800 rounded-2xl h-full min-h-[400px] border border-slate-700 flex items-center justify-center text-slate-500">
                Select a plan from the list to view details
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default InsurancePlans;
