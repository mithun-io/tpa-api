import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axios';
import toast from 'react-hot-toast';
import {
  CheckSquare,
  Square,
  Zap,
  ShieldCheck,
  Search,
  Filter,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Loader2
} from 'lucide-react';

const BulkSettlement = () => {
  const [claims, setClaims]         = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [loading, setLoading]       = useState(true);
  const [processing, setProcessing] = useState(false);
  const [stats, setStats]           = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => { fetchLowRiskClaims(); }, []);

  // Fetch carrier-assigned claims and filter for low-risk ones
  const fetchLowRiskClaims = async () => {
    setLoading(true);
    try {
      // Use authenticated axiosInstance + carrier endpoint
      const response = await axiosInstance.get('/carrier/claims');
      // ApiResponse<List<CarrierClaimDetailResponse>> → response.data.data
      const all = response.data?.data || [];
      // Filter: low risk (riskScore < 40) and not yet in final state
      const FINAL_STATUSES = ['SETTLED', 'REJECTED', 'CARRIER_APPROVED', 'PAYMENT_PENDING'];
      const lowRisk = all.filter(c =>
        (c.fraud?.riskScore ?? 0) < 40 &&
        !FINAL_STATUSES.includes(c.status)
      );
      setClaims(lowRisk);
    } catch (error) {
      console.error('Error fetching claims for bulk settlement:', error);
      toast.error('Failed to load claims for bulk settlement.');
    } finally {
      setLoading(false);
    }
  };

  const toggleSelect = (claimId) => {
    setSelectedIds(prev =>
      prev.includes(claimId) ? prev.filter(i => i !== claimId) : [...prev, claimId]
    );
  };

  const selectAll = () => {
    const visible = filteredClaims.map(c => c.claimId);
    if (selectedIds.length === visible.length && visible.length > 0) {
      setSelectedIds([]);
    } else {
      setSelectedIds(visible);
    }
  };

  const handleBulkApprove = async () => {
    if (selectedIds.length === 0) return;
    setProcessing(true);
    try {
      // Backend: POST /api/v1/claims/bulk/approve?approvedBy=carrier_admin with List<Long>
      const response = await axiosInstance.post(
        '/claims/bulk/approve?approvedBy=carrier_admin',
        selectedIds
      );
      const result = response.data;
      setStats({
        success: result.success ?? result.successCount ?? selectedIds.length,
        failed:  result.failed  ?? result.failureCount ?? 0,
      });
      toast.success(`Bulk approved ${result.success ?? selectedIds.length} claims`);
      fetchLowRiskClaims();
      setSelectedIds([]);
    } catch (error) {
      console.error('Bulk approval failed:', error);
      toast.error(error.response?.data?.message || 'Bulk approval failed.');
    } finally {
      setProcessing(false);
    }
  };

  // Filtered list for search
  const filteredClaims = claims.filter(c => {
    if (!searchTerm) return true;
    const q = searchTerm.toLowerCase();
    return (
      String(c.claimId).includes(q) ||
      (c.policyNumber || '').toLowerCase().includes(q) ||
      (c.patient?.name || '').toLowerCase().includes(q)
    );
  });

  if (loading) return (
    <div className="min-h-[400px] flex items-center justify-center">
      <div className="flex flex-col items-center gap-3 text-slate-500">
        <Loader2 size={32} className="animate-spin text-blue-500/50" />
        <p className="text-sm">Loading low-risk claims…</p>
      </div>
    </div>
  );

  return (
    <div className="text-white space-y-8">
      <div className="max-w-6xl mx-auto">

        {/* Header */}
        <div className="flex justify-between items-start mb-8 flex-wrap gap-4">
          <div>
            <h1 className="text-3xl font-black tracking-tight flex items-center gap-3">
              <Zap className="text-amber-500" size={28} />
              Bulk Settlement Portal
            </h1>
            <p className="text-slate-400 mt-2 font-medium">Auto-filtered low-risk, high-confidence claims ready for batch approval</p>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={fetchLowRiskClaims}
              className="flex items-center gap-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-300 rounded-xl text-sm font-medium transition-colors"
            >
              <RefreshCw size={14} /> Refresh
            </button>
            <div className="bg-slate-900 border border-slate-800 px-6 py-3 rounded-2xl flex flex-col items-end">
              <span className="text-[10px] uppercase tracking-widest text-slate-500 font-black">Selected</span>
              <span className="text-2xl font-black text-blue-400">{selectedIds.length}</span>
            </div>
            <button
              onClick={handleBulkApprove}
              disabled={selectedIds.length === 0 || processing}
              className={`px-8 py-4 rounded-2xl font-black transition-all shadow-xl flex items-center gap-2 ${
                selectedIds.length === 0
                  ? 'bg-slate-800 text-slate-600 cursor-not-allowed'
                  : 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-500/20 scale-105 active:scale-95'
              }`}
            >
              {processing
                ? <><Loader2 size={18} className="animate-spin" /> Processing…</>
                : <><ShieldCheck size={20} /> Approve &amp; Disburse</>
              }
            </button>
          </div>
        </div>

        {/* Status Alert */}
        {stats && (
          <div className="mb-8 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl p-6 flex items-center justify-between">
            <div className="flex items-center text-emerald-400 gap-3">
              <CheckCircle2 size={20} />
              <span className="font-bold">
                Batch Processed: <strong>{stats.success}</strong> successful,{' '}
                <strong>{stats.failed}</strong> failed.
              </span>
            </div>
            <button onClick={() => setStats(null)} className="text-xs font-black uppercase text-slate-500 hover:text-white">
              Dismiss
            </button>
          </div>
        )}

        {/* Toolbar */}
        <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-4 mb-6 flex items-center justify-between gap-4">
          <div className="flex items-center flex-1 max-w-md gap-3 bg-slate-800/60 border border-slate-700 rounded-xl px-4 py-2">
            <Search className="text-slate-500 flex-shrink-0" size={16} />
            <input
              type="text"
              placeholder="Search by ID, Policy or Patient…"
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
              className="bg-transparent border-0 focus:ring-0 text-sm w-full text-white placeholder-slate-500 outline-none"
            />
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={selectAll}
              className="flex items-center px-4 py-2 text-xs font-bold bg-slate-800 hover:bg-slate-700 rounded-xl transition-colors"
            >
              {selectedIds.length === filteredClaims.length && filteredClaims.length > 0 ? 'Deselect All' : 'Select All Low-Risk'}
            </button>
            <div className="w-px h-6 bg-slate-800 mx-2" />
            <button className="p-2 bg-slate-800 rounded-xl text-slate-400 hover:text-white transition-colors">
              <Filter size={16} />
            </button>
          </div>
        </div>

        {/* Claims Table */}
        <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-900/80 border-b border-slate-800">
                <th className="p-6 w-12" />
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500">Claim ID</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500">Patient / Diagnosis</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500">Policy</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500 text-right">Amount</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500 text-center">Status</th>
                <th className="p-6 text-[10px] font-black uppercase tracking-widest text-slate-500 text-center">Risk Score</th>
              </tr>
            </thead>
            <tbody>
              {filteredClaims.length === 0 ? (
                <tr>
                  <td colSpan="7" className="p-20 text-center text-slate-600 italic">
                    {claims.length === 0
                      ? 'No low-risk claims pending for bulk settlement.'
                      : 'No claims match your search.'}
                  </td>
                </tr>
              ) : (
                filteredClaims.map((c) => {
                  const riskScore = Math.round(c.fraud?.riskScore ?? 0);
                  const isSelected = selectedIds.includes(c.claimId);
                  return (
                    <tr
                      key={c.claimId}
                      onClick={() => toggleSelect(c.claimId)}
                      className={`border-b border-slate-800/50 hover:bg-white/5 transition-colors cursor-pointer group ${isSelected ? 'bg-blue-500/5' : ''}`}
                    >
                      <td className="p-6 text-center">
                        {isSelected
                          ? <CheckSquare className="text-blue-500" size={20} />
                          : <Square className="text-slate-700 group-hover:text-slate-500" size={20} />
                        }
                      </td>
                      <td className="p-6 font-mono text-xs text-blue-400 font-bold">#{c.claimId}</td>
                      <td className="p-6">
                        <p className="font-bold text-slate-200">{c.patient?.name || '—'}</p>
                        <p className="text-xs text-slate-500 mt-1">{c.diagnosis || c.hospitalName || '—'}</p>
                      </td>
                      <td className="p-6 font-mono text-xs text-slate-400">{c.policyNumber || '—'}</td>
                      <td className="p-6 text-right font-black text-white">
                        ${(c.amount ?? 0).toLocaleString()}
                      </td>
                      <td className="p-6 text-center">
                        <span className="px-2 py-1 rounded text-[10px] font-black uppercase bg-slate-700/60 text-slate-300">
                          {c.status}
                        </span>
                      </td>
                      <td className="p-6 text-center">
                        <div className="flex flex-col items-center gap-1">
                          <span className={`text-lg font-black ${riskScore > 60 ? 'text-red-400' : riskScore > 30 ? 'text-amber-400' : 'text-emerald-400'}`}>
                            {riskScore}
                          </span>
                          <div className="w-12 h-1 bg-slate-800 rounded-full overflow-hidden">
                            <div
                              className={`h-full ${riskScore > 60 ? 'bg-red-500' : riskScore > 30 ? 'bg-amber-500' : 'bg-emerald-500'}`}
                              style={{ width: `${riskScore}%` }}
                            />
                          </div>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Summary footer */}
        {filteredClaims.length > 0 && (
          <div className="flex justify-between items-center text-sm text-slate-500 pt-2">
            <span>{filteredClaims.length} low-risk claim{filteredClaims.length !== 1 ? 's' : ''} eligible for bulk settlement</span>
            {selectedIds.length > 0 && (
              <span className="text-blue-400 font-semibold">
                {selectedIds.length} selected · Total: ${filteredClaims
                  .filter(c => selectedIds.includes(c.claimId))
                  .reduce((sum, c) => sum + (c.amount ?? 0), 0)
                  .toLocaleString()}
              </span>
            )}
          </div>
        )}

      </div>
    </div>
  );
};

export default BulkSettlement;
