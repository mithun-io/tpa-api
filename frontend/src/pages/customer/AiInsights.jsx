import React, { useState, useEffect } from 'react';
import { getClaims } from '../../api/claim.service';
import Loader from '../../components/Loader';
import { ShieldCheck, AlertTriangle, FileSearch } from 'lucide-react';

const AiInsights = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getClaims({ page: 0, size: 20 }).then(res => {
      setClaims(res.content || []);
      setLoading(false);
    });
  }, []);

  if (loading) return <Loader fullScreen />;

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <h1 className="text-2xl font-bold text-white flex items-center gap-2">
        <ShieldCheck className="text-blue-400" /> AI Insights
      </h1>
      <p className="text-slate-400">View automated AI analysis and risk scores for your claims.</p>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {claims.map(claim => (
          <div key={claim.id} className="bg-slate-800 rounded-xl p-6 border border-slate-700 hover:border-slate-500 transition-colors">
            <div className="flex justify-between items-start mb-4 border-b border-slate-700 pb-4">
               <div>
                 <h2 className="text-lg font-bold text-white">Claim #{claim.id}</h2>
                 <p className="text-sm text-slate-400">{claim.patientName} - {claim.hospitalName}</p>
               </div>
               <div className={`px-3 py-1 rounded-full text-xs font-bold ${claim.aiFraudScore > 50 ? 'bg-red-500/20 text-red-400' : 'bg-emerald-500/20 text-emerald-400'}`}>
                 Risk Score: {claim.aiFraudScore || 0}/100
               </div>
            </div>

            <div className="space-y-4">
               <div className="flex items-start gap-3">
                 <AlertTriangle className="w-5 h-5 text-amber-400 mt-0.5" />
                 <div>
                   <h3 className="text-sm font-semibold text-slate-200">Missing Documents</h3>
                   <p className="text-xs text-slate-400 mt-1">{claim.missingDocuments?.length ? claim.missingDocuments.join(', ') : 'None detected'}</p>
                 </div>
               </div>
               
               <div className="flex items-start gap-3">
                 <FileSearch className="w-5 h-5 text-blue-400 mt-0.5" />
                 <div>
                   <h3 className="text-sm font-semibold text-slate-200">AI Summary</h3>
                   <p className="text-xs text-slate-400 mt-1">{claim.aiSummary || 'Analysis pending or not available.'}</p>
                 </div>
               </div>
            </div>
          </div>
        ))}
        {claims.length === 0 && <div className="col-span-2 text-center text-slate-500 py-10">No claims available for AI analysis.</div>}
      </div>
    </div>
  );
};

export default AiInsights;
