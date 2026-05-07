import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  CheckSquare, 
  Square, 
  Zap, 
  ShieldCheck, 
  Search, 
  Filter,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';

const BulkSettlement = () => {
  const [claims, setClaims] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [stats, setStats] = useState(null);

  useEffect(() => {
    fetchLowRiskClaims();
  }, []);

  const fetchLowRiskClaims = async () => {
    try {
      const response = await axios.get('/api/v1/claims'); // In real app, filter by status=UNDER_REVIEW & riskScore < 30
      // Mock filtering for demo
      const lowRisk = response.data.filter(c => (c.riskScore || 0) < 40 && c.claimStatus !== 'SETTLED');
      setClaims(lowRisk);
    } catch (error) {
      console.error("Error fetching claims", error);
    } finally {
      setLoading(false);
    }
  };

  const toggleSelect = (id) => {
    setSelectedIds(prev => 
      prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]
    );
  };

  const selectAll = () => {
    if (selectedIds.length === claims.length) setSelectedIds([]);
    else setSelectedIds(claims.map(c => c.id));
  };

  const handleBulkApprove = async () => {
    if (selectedIds.length === 0) return;
    setProcessing(true);
    try {
      const response = await axios.post(`/api/v1/claims/bulk/approve?approvedBy=carrier_admin`, selectedIds);
      setStats(response.data);
      fetchLowRiskClaims();
      setSelectedIds([]);
    } catch (error) {
      console.error("Bulk approval failed", error);
    } finally {
      setProcessing(false);
    }
  };

  if (loading) return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
  );

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-6xl mx-auto">
        
        {/* Header */}
        <div className="flex justify-between items-start mb-10">
          <div>
            <h1 className="text-4xl font-black tracking-tight flex items-center">
              <Zap className="text-amber-500 mr-3" size={32} />
              Bulk Settlement Portal
            </h1>
            <p className="text-slate-400 mt-2 font-medium">Auto-filter for low-risk, high-confidence claims</p>
          </div>
          <div className="flex space-x-4">
             <div className="bg-slate-900 border border-slate-800 px-6 py-3 rounded-2xl flex flex-col items-end">
                <span className="text-[10px] uppercase tracking-widest text-slate-500 font-black">Selected</span>
                <span className="text-2xl font-black text-blue-400">{selectedIds.length}</span>
             </div>
             <button 
               onClick={handleBulkApprove}
               disabled={selectedIds.length === 0 || processing}
               className={`px-8 py-4 rounded-2xl font-black transition-all shadow-xl flex items-center ${
                 selectedIds.length === 0 ? 'bg-slate-800 text-slate-600 cursor-not-allowed' : 
                 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-500/20 scale-105 active:scale-95'
               }`}
             >
               {processing ? <div className="animate-spin rounded-full h-5 w-5 border-2 border-white mr-2 border-t-transparent"></div> : <ShieldCheck size={20} className="mr-2" />}
               Approve & Disburse Funds
             </button>
          </div>
        </div>

        {/* Status Alert */}
        {stats && (
           <div className="mb-8 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl p-6 flex items-center justify-between animate-in fade-in slide-in-from-top-4">
              <div className="flex items-center text-emerald-400">
                 <CheckCircle2 className="mr-3" />
                 <span className="font-bold">Batch Processed: {stats.success} successful, {stats.failed} failed.</span>
              </div>
              <button onClick={() => setStats(null)} className="text-xs font-black uppercase text-slate-500 hover:text-white">Dismiss</button>
           </div>
        )}

        {/* Toolbar */}
        <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-4 mb-6 flex items-center justify-between">
           <div className="flex items-center flex-1 max-w-md">
              <Search className="text-slate-500 ml-4" size={18} />
              <input type="text" placeholder="Search by Policy or ID..." className="bg-transparent border-0 focus:ring-0 text-sm w-full px-4 text-white" />
           </div>
           <div className="flex items-center space-x-2">
              <button onClick={selectAll} className="flex items-center px-4 py-2 text-xs font-bold bg-slate-800 hover:bg-slate-700 rounded-xl transition-colors">
                 {selectedIds.length === claims.length ? 'Deselect All' : 'Select All Low-Risk'}
              </button>
              <div className="w-px h-6 bg-slate-800 mx-2"></div>
              <button className="p-2 bg-slate-800 rounded-xl text-slate-400 hover:text-white transition-colors">
                 <Filter size={18} />
              </button>
           </div>
        </div>

        {/* Claims Table */}
        <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-900/80 border-b border-slate-800">
                <th className="p-6 w-12"></th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500">Claim ID</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500">Patient / Diagnosis</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500 text-right">Amount</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500 text-center">AI Confidence</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500 text-center">Risk Score</th>
              </tr>
            </thead>
            <tbody>
              {claims.length === 0 ? (
                <tr>
                  <td colSpan="6" className="p-20 text-center text-slate-600 italic">No low-risk claims pending for bulk settlement.</td>
                </tr>
              ) : (
                claims.map((c) => (
                  <tr 
                    key={c.id} 
                    onClick={() => toggleSelect(c.id)}
                    className={`border-b border-slate-800/50 hover:bg-white/5 transition-colors cursor-pointer group ${selectedIds.includes(c.id) ? 'bg-blue-500/5' : ''}`}
                  >
                    <td className="p-6 text-center">
                      {selectedIds.includes(c.id) ? 
                        <CheckSquare className="text-blue-500" size={20} /> : 
                        <Square className="text-slate-700 group-hover:text-slate-500" size={20} />
                      }
                    </td>
                    <td className="p-6 font-mono text-xs text-blue-400 font-bold">#{c.id}</td>
                    <td className="p-6">
                      <p className="font-bold text-slate-200">{c.patientName}</p>
                      <p className="text-xs text-slate-500 mt-1">{c.diagnosis}</p>
                    </td>
                    <td className="p-6 text-right font-black text-white">₹{c.amount?.toLocaleString()}</td>
                    <td className="p-6 text-center">
                       <div className="inline-flex items-center px-2 py-1 rounded-lg bg-emerald-500/10 text-emerald-400 text-[10px] font-black">
                          <Zap size={10} className="mr-1" /> 98%
                       </div>
                    </td>
                    <td className="p-6 text-center">
                       <div className="flex flex-col items-center">
                          <span className="text-lg font-black text-slate-300">{c.riskScore?.toFixed(0) || 0}</span>
                          <div className="w-12 h-1 bg-slate-800 rounded-full mt-1 overflow-hidden">
                             <div className="h-full bg-emerald-500" style={{ width: `${c.riskScore || 0}%` }}></div>
                          </div>
                       </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
};

export default BulkSettlement;
