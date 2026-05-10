import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axios';
import { Layers, Search, Filter, CheckSquare, Square, Download, CheckCircle, XCircle, Banknote, CreditCard, Send, Loader2, AlertCircle, Sparkles } from 'lucide-react';
import toast from 'react-hot-toast';

const BulkSettlement = () => {
  const [claims, setClaims] = useState([]);
  const [selected, setSelected] = useState(new Set());
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchPendingClaims = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await axiosInstance.get('/carrier/claims', { _suppressToast: true });
      // Only show claims that are ADMIN_APPROVED and ready for Carrier Settlement
      const pending = (res.data?.data ?? []).filter(c => c.status === 'ADMIN_APPROVED');
      setClaims(pending);
    } catch (e) {
      setError('Failed to load claims for bulk settlement.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPendingClaims(); }, []);

  const toggleSelect = (id) => {
    const newSet = new Set(selected);
    if (newSet.has(id)) newSet.delete(id);
    else newSet.add(id);
    setSelected(newSet);
  };

  const selectAll = () => {
    if (selected.size === filteredClaims.length) setSelected(new Set());
    else setSelected(new Set(filteredClaims.map(c => c.id)));
  };

  const handleBulkAction = async (action) => {
    if (selected.size === 0) return;
    setProcessing(true);
    try {
      // Assuming a generic endpoint or looping if no bulk endpoint exists
      const endpoint = action === 'APPROVE' ? '/approve' : '/reject';
      
      const promises = Array.from(selected).map(id => 
        axiosInstance.post(`/carrier/claims/${id}${endpoint}`, 
          action === 'APPROVE' ? null : { reason: 'Bulk rejected by carrier' }
        )
      );
      
      await Promise.allSettled(promises);
      toast.success(`Successfully ${action === 'APPROVE' ? 'approved' : 'rejected'} ${selected.size} claims.`);
      setSelected(new Set());
      fetchPendingClaims();
    } catch (e) {
      toast.error('Some claims failed to process.');
    } finally {
      setProcessing(false);
    }
  };

  const filteredClaims = claims.filter(c => {
    const s = searchTerm.toLowerCase();
    return String(c.claimId || '').toLowerCase().includes(s) ||
           String(c.patient?.name || '').toLowerCase().includes(s) ||
           String(c.hospitalName || '').toLowerCase().includes(s);
  });

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-24">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-5">
        <div>
          <h1 className="text-3xl font-black text-white flex items-center gap-3 tracking-tight">
            <Layers className="text-amber-500" size={32} /> Bulk Settlement Portal
          </h1>
          <p className="text-slate-400 mt-2 text-sm max-w-2xl">
            Enterprise batch processing of low-risk, high-confidence claims ready for payout.
          </p>
        </div>
        <div className="flex gap-3">
          <button className="flex items-center gap-2 px-4 py-2 border border-slate-700 hover:bg-slate-800 text-slate-300 rounded-xl text-sm font-semibold transition-all">
            <Download size={16} /> Export NEFT Batch
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-red-500/10 border-l-4 border-red-500 text-red-400 p-4 rounded-r-xl flex items-start gap-3">
          <AlertCircle size={20} className="mt-0.5" />
          <p className="text-sm font-medium">{error}</p>
        </div>
      )}

      {/* Toolbar */}
      <div className="flex flex-col sm:flex-row justify-between gap-4 bg-slate-900 border border-slate-800 p-4 rounded-2xl shadow-lg">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={18} />
          <input 
            type="text" 
            placeholder="Search by ID, Patient, or Hospital..." 
            className="w-full bg-slate-950 border border-slate-800 rounded-xl py-2 pl-10 pr-4 text-sm text-slate-200 focus:outline-none focus:border-blue-500 transition-colors"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-3">
          <span className="text-sm text-slate-400"><strong>{selected.size}</strong> selected</span>
          <div className="w-px h-6 bg-slate-800 mx-1"></div>
          <button className="flex items-center gap-2 px-3 py-1.5 hover:bg-slate-800 text-slate-300 rounded-lg text-sm transition-colors border border-transparent hover:border-slate-700">
            <Filter size={14} /> Filters
          </button>
        </div>
      </div>

      {/* Data Grid */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl relative">
        {loading ? (
          <div className="flex flex-col items-center justify-center py-20 text-slate-500">
            <Loader2 size={32} className="animate-spin text-amber-500 mb-4" />
            <p className="text-sm font-medium">Loading settlement queue...</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-950 border-b border-slate-800 text-slate-400 text-[10px] uppercase tracking-widest">
                  <th className="p-4 w-12 text-center">
                    <button onClick={selectAll} className="text-slate-500 hover:text-white transition-colors focus:outline-none">
                      {selected.size > 0 && selected.size === filteredClaims.length ? <CheckSquare size={18} className="text-blue-500"/> : <Square size={18}/>}
                    </button>
                  </th>
                  <th className="p-4 font-semibold">Claim Details</th>
                  <th className="p-4 font-semibold">Hospital / Provider</th>
                  <th className="p-4 font-semibold text-right">Settlement Amt</th>
                  <th className="p-4 font-semibold text-center">AI Recommendation</th>
                  <th className="p-4 font-semibold text-center">Payout Method</th>
                </tr>
              </thead>
              <tbody className="text-sm divide-y divide-slate-800/50">
                {filteredClaims.map((claim) => {
                  const isSelected = selected.has(claim.id);
                  const isLowRisk = claim.fraudScore < 20;
                  const method = ['NEFT', 'RTGS', 'UPI'][Math.floor(Math.random() * 3)];
                  
                  return (
                    <tr key={claim.id} className={`transition-colors ${isSelected ? 'bg-blue-500/5' : 'hover:bg-slate-800/30'}`}>
                      <td className="p-4 text-center">
                        <button onClick={() => toggleSelect(claim.id)} className="text-slate-500 hover:text-white transition-colors focus:outline-none mt-1">
                          {isSelected ? <CheckSquare size={18} className="text-blue-500"/> : <Square size={18}/>}
                        </button>
                      </td>
                      <td className="p-4">
                        <p className="text-white font-mono font-medium">{claim.claimId || `CLM-${claim.id}`}</p>
                        <p className="text-slate-500 text-xs mt-0.5">{claim.patient?.name || 'Unknown Patient'}</p>
                      </td>
                      <td className="p-4">
                        <p className="text-slate-300 font-medium">{claim.hospitalName || 'Unknown Hospital'}</p>
                        <p className="text-slate-500 text-xs mt-0.5">{claim.policyName || 'Standard Policy'}</p>
                      </td>
                      <td className="p-4 text-right">
                        <p className="text-emerald-400 font-black tracking-tight">${(claim.amount || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
                      </td>
                      <td className="p-4 text-center">
                        {isLowRisk ? (
                          <div className="inline-flex items-center gap-1.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 px-2 py-1 rounded-full text-[10px] font-bold uppercase">
                            <Sparkles size={12}/> Auto-Approve
                          </div>
                        ) : (
                          <div className="inline-flex items-center gap-1.5 bg-amber-500/10 text-amber-400 border border-amber-500/20 px-2 py-1 rounded-full text-[10px] font-bold uppercase">
                            <AlertCircle size={12}/> Manual Verify
                          </div>
                        )}
                      </td>
                      <td className="p-4 text-center">
                        <div className="flex items-center justify-center gap-1.5 text-xs font-semibold text-slate-400 bg-slate-950 border border-slate-800 px-2 py-1 rounded max-w-max mx-auto">
                          {method === 'UPI' ? <Send size={12}/> : method === 'NEFT' ? <Banknote size={12}/> : <CreditCard size={12}/>}
                          {method}
                        </div>
                      </td>
                    </tr>
                  );
                })}
                {filteredClaims.length === 0 && !loading && (
                  <tr>
                    <td colSpan="6" className="p-12 text-center text-slate-500">
                      <Layers size={40} className="mx-auto mb-4 text-slate-700" />
                      <p className="text-base font-semibold text-slate-400">No claims ready for bulk settlement.</p>
                      <p className="text-sm mt-1">All ADMIN_APPROVED claims have been processed.</p>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Floating Action Bar */}
      {selected.size > 0 && (
        <div className="fixed bottom-8 left-1/2 -translate-x-1/2 bg-slate-800 border border-slate-700 shadow-[0_10px_40px_rgba(0,0,0,0.5)] rounded-2xl px-6 py-4 flex items-center gap-6 z-50 animate-in slide-in-from-bottom-10 fade-in duration-300">
          <div className="flex items-center gap-3 pr-6 border-r border-slate-700">
            <div className="bg-blue-500 text-white w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm">
              {selected.size}
            </div>
            <span className="text-slate-300 font-medium text-sm">claims selected</span>
          </div>
          
          <div className="flex items-center gap-3">
            <button 
              onClick={() => handleBulkAction('REJECT')}
              disabled={processing}
              className="flex items-center gap-2 px-4 py-2 hover:bg-slate-700 text-rose-400 rounded-xl text-sm font-semibold transition-colors focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <XCircle size={18} /> Reject Batch
            </button>
            <button 
              onClick={() => handleBulkAction('APPROVE')}
              disabled={processing}
              className="flex items-center gap-2 px-6 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-sm font-bold transition-all shadow-lg shadow-emerald-900/50 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {processing ? <Loader2 size={18} className="animate-spin" /> : <CheckCircle size={18} />} 
              Approve & Settle
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default BulkSettlement;
