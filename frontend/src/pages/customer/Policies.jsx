import React, { useState, useEffect } from 'react';
import { getClaims } from '../../api/claim.service';
import Loader from '../../components/Loader';
import { BookOpen, ShieldCheck, FileText } from 'lucide-react';

const Policies = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getClaims({ page: 0, size: 50 }).then(res => {
      setClaims(res.content || []);
      setLoading(false);
    });
  }, []);

  if (loading) return <Loader fullScreen />;

  // Group claims by policy number to infer policies
  const policyMap = claims.reduce((acc, claim) => {
    if (!acc[claim.policyNumber]) {
      acc[claim.policyNumber] = {
        policyNumber: claim.policyNumber,
        claimsCount: 0,
        patientName: claim.patientName,
        latestClaimDate: claim.createdDate
      };
    }
    acc[claim.policyNumber].claimsCount++;
    return acc;
  }, {});

  const policies = Object.values(policyMap);

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <h1 className="text-2xl font-bold text-white flex items-center gap-2">
        <BookOpen className="text-blue-400" /> Active Policies
      </h1>
      <p className="text-slate-400">View your active insurance policies and coverage details.</p>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {policies.map(policy => (
          <div key={policy.policyNumber} className="bg-slate-800 rounded-xl p-6 border border-slate-700 hover:border-blue-500/50 transition-colors">
            <div className="flex justify-between items-start mb-4 border-b border-slate-700 pb-4">
               <div>
                 <h2 className="text-lg font-bold text-white">Policy #{policy.policyNumber}</h2>
                 <p className="text-sm text-slate-400">Insured: {policy.patientName}</p>
               </div>
               <div className="px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 flex items-center gap-1">
                 <ShieldCheck className="w-3 h-3" /> ACTIVE
               </div>
            </div>

            <div className="space-y-3">
               <div className="flex items-center gap-3">
                 <FileText className="w-5 h-5 text-blue-400" />
                 <div>
                   <p className="text-sm font-semibold text-slate-200">{policy.claimsCount} Total Claims</p>
                   <p className="text-xs text-slate-400">Latest claim on {new Date(policy.latestClaimDate).toLocaleDateString()}</p>
                 </div>
               </div>
               {/* Note: Comprehensive coverage details would be fetched from a dedicated Policy API */}
            </div>
          </div>
        ))}
        {policies.length === 0 && <div className="col-span-2 text-center text-slate-500 py-10">No active policies found based on your claims.</div>}
      </div>
    </div>
  );
};

export default Policies;
