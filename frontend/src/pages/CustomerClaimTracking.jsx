import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { 
  CheckCircle, 
  Clock, 
  FileSearch, 
  CreditCard, 
  ShieldCheck, 
  AlertCircle,
  ArrowLeft
} from 'lucide-react';

const CustomerClaimTracking = () => {
  const { claimId } = useParams();
  const [claim, setClaim] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchClaim = async () => {
      try {
        const response = await axios.get(`/api/v1/claims/${claimId}`);
        setClaim(response.data);
      } catch (error) {
        console.error("Error fetching claim details", error);
      } finally {
        setLoading(false);
      }
    };
    fetchClaim();
  }, [claimId]);

  if (loading) return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
  );

  if (!claim) return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center text-white">
      <div className="text-center">
        <AlertCircle size={48} className="mx-auto text-red-500 mb-4" />
        <h2 className="text-2xl font-bold">Claim Not Found</h2>
        <p className="text-slate-400 mt-2">We couldn't find a claim with ID #{claimId}</p>
      </div>
    </div>
  );

  const steps = [
    { id: 'SUBMITTED', label: 'Received', icon: Clock, description: 'Documents uploaded and queued.' },
    { id: 'AI_VALIDATED', label: 'AI Verified', icon: FileSearch, description: 'OCR & integrity checks complete.' },
    { id: 'UNDER_REVIEW', label: 'Medical Review', icon: ShieldCheck, description: 'Final verification by TPA experts.' },
    { id: 'APPROVED', label: 'Approved', icon: CheckCircle, description: 'Claim cleared for settlement.' },
    { id: 'SETTLED', label: 'Disbursed', icon: CreditCard, description: 'Funds transferred via instant payout.' }
  ];

  const getCurrentStepIndex = () => {
    const statusMap = {
      'SUBMITTED': 0,
      'PENDING': 0,
      'AI_VALIDATED': 1,
      'UNDER_REVIEW': 2,
      'ADMIN_APPROVED': 3,
      'CARRIER_APPROVED': 3,
      'APPROVED': 3,
      'REJECTED': -1,
      'SETTLED': 4
    };
    return statusMap[claim.claimStatus] ?? 0;
  };

  const currentIdx = getCurrentStepIndex();
  const isRejected = claim.claimStatus === 'REJECTED';

  return (
    <div className="min-h-screen bg-slate-950 text-white font-sans selection:bg-blue-500/30">
      <div className="max-w-4xl mx-auto px-6 py-12">
        
        {/* Header */}
        <div className="flex items-center justify-between mb-12">
          <div>
            <button onClick={() => window.history.back()} className="flex items-center text-slate-400 hover:text-white transition-colors mb-4 group">
              <ArrowLeft size={18} className="mr-2 group-hover:-translate-x-1 transition-transform" /> Back to Dashboard
            </button>
            <h1 className="text-4xl font-extrabold tracking-tight bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">
              Claim Tracking
            </h1>
            <p className="text-slate-400 mt-2 font-medium">Claim ID: <span className="text-blue-400">#{claim.id}</span> • Policy: {claim.policyNumber}</p>
          </div>
          <div className="text-right">
             <div className={`px-4 py-2 rounded-full text-xs font-bold uppercase tracking-widest ${
               isRejected ? 'bg-red-500/20 text-red-400 border border-red-500/30' : 
               claim.claimStatus === 'SETTLED' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' :
               'bg-blue-500/20 text-blue-400 border border-blue-500/30'
             }`}>
               {claim.claimStatus?.replace('_', ' ')}
             </div>
          </div>
        </div>

        {/* Uber-style Progress Tracker */}
        <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-10 mb-8 shadow-2xl relative overflow-hidden">
          <div className="absolute top-0 right-0 p-8 opacity-5">
            <ShieldCheck size={120} />
          </div>
          
          <div className="relative z-10">
            <div className="flex justify-between relative mb-12">
              {/* Connecting Line */}
              <div className="absolute top-1/2 left-0 w-full h-1 bg-slate-800 -translate-y-1/2 z-0"></div>
              <div 
                className={`absolute top-1/2 left-0 h-1 bg-gradient-to-r from-blue-500 to-emerald-500 -translate-y-1/2 z-0 transition-all duration-1000 ease-out`}
                style={{ width: `${isRejected ? 0 : (currentIdx / (steps.length - 1)) * 100}%` }}
              ></div>

              {steps.map((step, index) => {
                const Icon = step.icon;
                const isCompleted = index <= currentIdx && !isRejected;
                const isCurrent = index === currentIdx && !isRejected;
                
                return (
                  <div key={step.id} className="relative z-10 flex flex-col items-center">
                    <div className={`w-14 h-14 rounded-2xl flex items-center justify-center transition-all duration-500 border-2 ${
                      isCompleted ? 'bg-blue-600 border-blue-400 shadow-lg shadow-blue-500/40 text-white scale-110' : 
                      isCurrent ? 'bg-slate-900 border-blue-500 text-blue-500' :
                      'bg-slate-900 border-slate-800 text-slate-600'
                    }`}>
                      <Icon size={24} />
                    </div>
                    <div className="absolute top-16 whitespace-nowrap text-center">
                      <p className={`text-sm font-bold mt-2 ${isCompleted ? 'text-white' : 'text-slate-500'}`}>{step.label}</p>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Current Status Description */}
            <div className="mt-24 bg-blue-500/5 border border-blue-500/10 rounded-2xl p-6">
              <h3 className="text-lg font-bold flex items-center">
                {isRejected ? (
                  <> <AlertCircle className="text-red-500 mr-2" /> Rejection Alert </>
                ) : (
                  <> <Clock className="text-blue-500 mr-2" /> Current Status </>
                )}
              </h3>
              <p className="text-slate-400 mt-2 leading-relaxed">
                {isRejected ? 
                  `Unfortunately, your claim has been rejected. Reason: ${claim.rejectionReason || 'Contact support for details.'}` :
                  steps[currentIdx]?.description || "Your claim is being processed by our automated systems."
                }
              </p>
            </div>
          </div>
        </div>

        {/* Details Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8">
            <h3 className="text-xl font-bold mb-6">Medical Details</h3>
            <div className="space-y-4">
              <DetailRow label="Diagnosis" value={claim.diagnosis} />
              <DetailRow label="ICD-10 Code" value={claim.icdCode} />
              <DetailRow label="Hospital" value={claim.hospitalName} />
              <DetailRow label="Patient" value={claim.patientName} />
            </div>
          </div>

          <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8">
            <h3 className="text-xl font-bold mb-6">Financial Summary</h3>
            <div className="space-y-4">
              <DetailRow label="Claimed Amount" value={`₹${claim.amount?.toLocaleString()}`} highlight />
              <DetailRow label="Total Bill Value" value={`₹${claim.totalBillAmount?.toLocaleString()}`} />
              <DetailRow label="Bill Number" value={claim.billNumber} />
              <DetailRow label="Claim Type" value={claim.claimType} />
            </div>
          </div>
        </div>

        {/* Risk Status (Only for Customer context, but keeping it professional) */}
        <div className="mt-8 bg-gradient-to-br from-slate-900 to-slate-800 border border-slate-700 rounded-3xl p-8 flex items-center justify-between">
          <div>
            <h3 className="text-lg font-bold">Document Health Score</h3>
            <p className="text-slate-400 text-sm">Verified by AI Document Forensic Engine</p>
          </div>
          <div className="flex items-center">
             <div className="text-right mr-6">
                <p className="text-3xl font-black text-white">{claim.healthScore || 0}%</p>
                <p className="text-xs text-slate-500 uppercase tracking-widest font-bold">Authenticity</p>
             </div>
             <div className="w-16 h-16 rounded-full border-4 border-slate-700 border-t-blue-500 animate-pulse"></div>
          </div>
        </div>

      </div>
    </div>
  );
};

const DetailRow = ({ label, value, highlight = false }) => (
  <div className="flex justify-between items-center py-2 border-b border-slate-800/50 last:border-0">
    <span className="text-slate-500 font-medium">{label}</span>
    <span className={`font-semibold ${highlight ? 'text-blue-400 text-lg' : 'text-slate-200'}`}>{value || 'N/A'}</span>
  </div>
);

export default CustomerClaimTracking;
