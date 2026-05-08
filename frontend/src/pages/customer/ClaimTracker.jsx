import React, { useState, useEffect } from 'react';
import { getClaims } from '../../api/claim.service';
import Loader from '../../components/Loader';
import { motion } from 'framer-motion';
import { Check, Clock, X } from 'lucide-react';

const STEPS = [
  { id: 'SUBMITTED', label: 'Claim Submitted', desc: 'Received by the system' },
  { id: 'AI_VALIDATED', label: 'AI Validation', desc: 'Automated fraud & document check' },
  { id: 'UNDER_REVIEW', label: 'Under Review', desc: 'Manual review by admin' },
  { id: 'ADMIN_APPROVED', label: 'Admin Approved', desc: 'Forwarded to carrier' },
  { id: 'CARRIER_APPROVED', label: 'Carrier Approved', desc: 'Payment pending' },
  { id: 'SETTLED', label: 'Settled', desc: 'Amount paid to patient' }
];

const ClaimTracker = () => {
  const [claims, setClaims] = useState([]);
  const [selectedClaimId, setSelectedClaimId] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getClaims({ page: 0, size: 20 }).then(res => {
      setClaims(res.content || []);
      if (res.content?.length > 0) setSelectedClaimId(res.content[0].id);
      setLoading(false);
    });
  }, []);

  if (loading) return <Loader fullScreen />;

  const claim = claims.find(c => c.id === selectedClaimId);
  const currentIdx = claim ? STEPS.findIndex(s => s.id === (claim.claimStatus || claim.status)) : -1;
  const isRejected = claim?.claimStatus === 'REJECTED';

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <h1 className="text-2xl font-bold text-white">Claim Tracker</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="md:col-span-1 bg-slate-800 rounded-xl p-4 border border-slate-700 h-[600px] overflow-y-auto">
          <h2 className="text-sm font-semibold text-slate-400 mb-4 uppercase tracking-wider">Your Claims</h2>
          <div className="space-y-2">
            {claims.map(c => (
              <button
                key={c.id}
                onClick={() => setSelectedClaimId(c.id)}
                className={`w-full text-left p-3 rounded-lg transition-colors ${selectedClaimId === c.id ? 'bg-blue-600 border border-blue-500' : 'bg-slate-900 border border-slate-700 hover:border-slate-500'}`}
              >
                <div className="text-sm font-bold text-white">Claim #{c.id}</div>
                <div className="text-xs text-slate-400 truncate">{c.patientName}</div>
              </button>
            ))}
          </div>
        </div>

        <div className="md:col-span-2 bg-slate-800 rounded-xl p-8 border border-slate-700">
          {claim ? (
            <div>
              <div className="mb-8 border-b border-slate-700 pb-4">
                <h2 className="text-xl font-bold text-white">Tracking Claim #{claim.id}</h2>
                <p className="text-slate-400">Policy: {claim.policyNumber}</p>
              </div>
              
              <div className="relative border-l-2 border-slate-700 ml-4 space-y-8 pb-4">
                {STEPS.map((step, idx) => {
                  const isCompleted = idx <= currentIdx;
                  const isActive = idx === currentIdx;
                  
                  // Handle rejection scenario
                  if (isRejected && idx >= currentIdx) {
                     if (idx === currentIdx + 1) {
                        return (
                          <motion.div key="REJECTED" initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} className="relative pl-8">
                            <div className="absolute -left-[11px] top-1 bg-red-500 rounded-full p-1 border-4 border-slate-800">
                              <X className="w-3 h-3 text-white" />
                            </div>
                            <h3 className="text-lg font-bold text-red-400">Claim Rejected</h3>
                            <p className="text-sm text-slate-400">Unfortunately, this claim was rejected.</p>
                          </motion.div>
                        );
                     }
                     if (idx > currentIdx + 1) return null;
                  }

                  return (
                    <motion.div 
                      key={step.id} 
                      initial={{ opacity: 0, x: -20 }} 
                      animate={{ opacity: 1, x: 0 }} 
                      transition={{ delay: idx * 0.1 }}
                      className="relative pl-8"
                    >
                      <div className={`absolute -left-[11px] top-1 rounded-full p-1 border-4 border-slate-800 transition-colors ${
                        isCompleted ? 'bg-blue-500' : 'bg-slate-700'
                      }`}>
                        {isCompleted ? <Check className="w-3 h-3 text-white" /> : <Clock className="w-3 h-3 text-slate-400" />}
                      </div>
                      <h3 className={`text-lg font-bold ${isCompleted ? 'text-white' : 'text-slate-500'}`}>
                        {step.label} {isActive && <span className="ml-2 text-xs bg-blue-500/20 text-blue-400 px-2 py-1 rounded-full">Current Status</span>}
                      </h3>
                      <p className={`text-sm ${isCompleted ? 'text-slate-400' : 'text-slate-600'}`}>{step.desc}</p>
                    </motion.div>
                  );
                })}
              </div>
            </div>
          ) : (
            <div className="text-center text-slate-500 mt-20">Select a claim to track</div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ClaimTracker;
