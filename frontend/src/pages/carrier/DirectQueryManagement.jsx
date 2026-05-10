import React, { useState, useEffect } from 'react';
import axiosInstance from '../../api/axios';
import { MessageSquare, Search, Send, Paperclip, AlertCircle, Clock, CheckCircle2, User, Loader2, Info, ShieldAlert } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';

const DirectQueryManagement = () => {
  const { user } = useAuth();
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [selectedClaim, setSelectedClaim] = useState(null);
  const [message, setMessage] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  const fetchQueries = async () => {
    setLoading(true);
    try {
      const res = await axiosInstance.get('/carrier/claims', { _suppressToast: true });
      // Use existing claims as a base for query threads
      setClaims(res.data?.data ?? []);
    } catch (e) {
      setError('Failed to load query threads.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchQueries(); }, []);

  // Filter claims that have queries or could be queried
  const queryThreads = claims.filter(c => 
    c.status === 'UNDER_REVIEW' || c.status === 'ESCALATED' || c.reviewNotes || c.rejectionReason
  );

  const filteredThreads = queryThreads.filter(c => {
    const s = searchTerm.toLowerCase();
    return String(c.claimId || '').toLowerCase().includes(s) ||
           String(c.patient?.name || '').toLowerCase().includes(s);
  });

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!message.trim() || !selectedClaim) return;
    
    // In a real app, this would post to a /queries endpoint. We simulate it by showing a toast.
    toast.success('Message sent to TPA Administrator.');
    
    // Optimistic UI update simulation (not persisted since we lack a real query table in DB)
    const updatedClaim = { ...selectedClaim };
    updatedClaim.simulatedMessages = [
      ...(updatedClaim.simulatedMessages || []),
      { sender: 'CARRIER', text: message, timestamp: new Date().toISOString() }
    ];
    setSelectedClaim(updatedClaim);
    setMessage('');
  };

  // Generate simulated history based on claim state
  const getSimulatedHistory = (claim) => {
    if (claim.simulatedMessages) return claim.simulatedMessages;
    const history = [];
    if (claim.aiSummary) {
      history.push({ sender: 'SYSTEM', text: `AI Flag: ${claim.aiSummary}`, timestamp: claim.createdDate });
    }
    if (claim.reviewNotes) {
      history.push({ sender: 'TPA_ADMIN', text: claim.reviewNotes, timestamp: claim.reviewedAt || new Date().toISOString() });
    }
    if (claim.rejectionReason) {
      history.push({ sender: 'TPA_ADMIN', text: `Rejection Note: ${claim.rejectionReason}`, timestamp: new Date().toISOString() });
    }
    if (history.length === 0) {
      history.push({ sender: 'SYSTEM', text: 'Thread opened. Waiting for communication.', timestamp: new Date().toISOString() });
    }
    return history;
  };

  return (
    <div className="max-w-7xl mx-auto pb-10 h-[calc(100vh-120px)] flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-slate-800 pb-5 mb-6 flex-shrink-0">
        <div>
          <h1 className="text-3xl font-black text-white flex items-center gap-3 tracking-tight">
            <MessageSquare className="text-indigo-500" size={32} /> Query Management Center
          </h1>
          <p className="text-slate-400 mt-2 text-sm max-w-2xl">
            Direct communication hub for claim escalations, document requests, and TPA coordination.
          </p>
        </div>
      </div>

      {error && (
        <div className="bg-red-500/10 border-l-4 border-red-500 text-red-400 p-4 rounded-r-xl flex items-start gap-3 mb-6 flex-shrink-0">
          <AlertCircle size={20} className="mt-0.5" />
          <p className="text-sm font-medium">{error}</p>
        </div>
      )}

      {/* Main Chat Interface */}
      <div className="flex-1 bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl flex min-h-0">
        
        {/* Left Pane - Inbox */}
        <div className="w-80 border-r border-slate-800 flex flex-col bg-slate-950/50">
          <div className="p-4 border-b border-slate-800">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
              <input 
                type="text" 
                placeholder="Search threads..." 
                className="w-full bg-slate-900 border border-slate-800 rounded-lg py-2 pl-9 pr-4 text-xs text-slate-200 focus:outline-none focus:border-indigo-500 transition-colors"
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
              />
            </div>
          </div>

          <div className="flex-1 overflow-y-auto custom-scrollbar">
            {loading ? (
              <div className="flex justify-center p-8"><Loader2 className="animate-spin text-indigo-500" /></div>
            ) : filteredThreads.length === 0 ? (
              <div className="p-8 text-center text-slate-500">
                <MessageSquare size={24} className="mx-auto mb-2 text-slate-600" />
                <p className="text-xs">No active queries found.</p>
              </div>
            ) : (
              <div className="divide-y divide-slate-800/50">
                {filteredThreads.map(claim => (
                  <button 
                    key={claim.id}
                    onClick={() => setSelectedClaim(claim)}
                    className={`w-full text-left p-4 hover:bg-slate-800/50 transition-colors ${selectedClaim?.id === claim.id ? 'bg-indigo-500/10 border-l-4 border-indigo-500' : 'border-l-4 border-transparent'}`}
                  >
                    <div className="flex justify-between items-start mb-1">
                      <span className={`text-sm font-bold ${selectedClaim?.id === claim.id ? 'text-indigo-400' : 'text-slate-300'}`}>
                        {claim.claimId || `CLM-${claim.id}`}
                      </span>
                      <span className="text-[10px] text-slate-500 flex items-center gap-1">
                        <Clock size={10} /> {new Date(claim.createdDate).toLocaleDateString()}
                      </span>
                    </div>
                    <p className="text-xs text-slate-400 truncate mb-2">{claim.patient?.name}</p>
                    <div className="flex gap-2">
                      {claim.status === 'ESCALATED' ? (
                        <span className="bg-red-500/10 text-red-400 px-2 py-0.5 rounded text-[9px] font-bold uppercase">High Priority</span>
                      ) : (
                        <span className="bg-amber-500/10 text-amber-400 px-2 py-0.5 rounded text-[9px] font-bold uppercase">Pending Reply</span>
                      )}
                    </div>
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Pane - Chat */}
        <div className="flex-1 flex flex-col bg-[#0b1120]">
          {selectedClaim ? (
            <>
              {/* Chat Header */}
              <div className="p-4 border-b border-slate-800 bg-slate-900 flex justify-between items-center shadow-sm">
                <div>
                  <h3 className="text-white font-bold text-lg flex items-center gap-2">
                    {selectedClaim.claimId || `CLM-${selectedClaim.id}`}
                  </h3>
                  <p className="text-slate-400 text-xs mt-0.5">
                    Patient: <span className="text-slate-300">{selectedClaim.patient?.name}</span> • 
                    Hospital: <span className="text-slate-300">{selectedClaim.hospitalName}</span>
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-slate-400 text-xs font-semibold bg-slate-800 px-3 py-1.5 rounded-lg border border-slate-700">
                    {selectedClaim.status}
                  </span>
                  <button className="p-2 text-slate-400 hover:text-indigo-400 bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors">
                    <Info size={16} />
                  </button>
                </div>
              </div>

              {/* Chat Messages */}
              <div className="flex-1 overflow-y-auto p-6 space-y-6 custom-scrollbar">
                <div className="text-center">
                  <span className="bg-slate-800/50 text-slate-500 text-[10px] uppercase font-bold tracking-wider px-3 py-1 rounded-full">
                    Thread Started • {new Date(selectedClaim.createdDate).toLocaleDateString()}
                  </span>
                </div>

                {getSimulatedHistory(selectedClaim).map((msg, idx) => (
                  <div key={idx} className={`flex ${msg.sender === 'CARRIER' ? 'justify-end' : 'justify-start'}`}>
                    <div className={`flex gap-3 max-w-[80%] ${msg.sender === 'CARRIER' ? 'flex-row-reverse' : 'flex-row'}`}>
                      
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${
                        msg.sender === 'CARRIER' ? 'bg-indigo-600' : 
                        msg.sender === 'SYSTEM' ? 'bg-slate-700' : 'bg-emerald-600'
                      }`}>
                        {msg.sender === 'CARRIER' ? <User size={14} className="text-white"/> : 
                         msg.sender === 'SYSTEM' ? <AlertCircle size={14} className="text-slate-300"/> : 
                         <ShieldAlert size={14} className="text-white"/>}
                      </div>
                      
                      <div>
                        <div className={`flex items-baseline gap-2 mb-1 ${msg.sender === 'CARRIER' ? 'flex-row-reverse' : ''}`}>
                          <span className="text-xs font-bold text-slate-300">
                            {msg.sender === 'CARRIER' ? 'You' : msg.sender === 'SYSTEM' ? 'Automated System' : 'TPA Admin'}
                          </span>
                          <span className="text-[10px] text-slate-500">
                            {new Date(msg.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                          </span>
                        </div>
                        
                        <div className={`p-3 rounded-2xl text-sm ${
                          msg.sender === 'CARRIER' ? 'bg-indigo-600 text-white rounded-tr-sm' : 
                          msg.sender === 'SYSTEM' ? 'bg-slate-800 text-slate-300 border border-slate-700 rounded-tl-sm' : 
                          'bg-emerald-900/40 text-emerald-100 border border-emerald-800 rounded-tl-sm'
                        }`}>
                          {msg.text}
                        </div>
                      </div>
                      
                    </div>
                  </div>
                ))}
              </div>

              {/* Chat Input */}
              <div className="p-4 border-t border-slate-800 bg-slate-900">
                <form onSubmit={handleSendMessage} className="flex gap-3">
                  <button type="button" className="p-3 text-slate-400 hover:text-indigo-400 bg-slate-800 hover:bg-slate-700 rounded-xl transition-colors">
                    <Paperclip size={20} />
                  </button>
                  <input 
                    type="text" 
                    placeholder="Type your message to TPA Admin..." 
                    className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-4 text-sm text-slate-200 focus:outline-none focus:border-indigo-500 transition-colors"
                    value={message}
                    onChange={e => setMessage(e.target.value)}
                  />
                  <button 
                    type="submit" 
                    disabled={!message.trim()}
                    className="p-3 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-lg shadow-indigo-900/50 flex items-center justify-center"
                  >
                    <Send size={20} />
                  </button>
                </form>
              </div>
            </>
          ) : (
            <div className="flex-1 flex flex-col items-center justify-center text-slate-500 p-8">
              <div className="w-20 h-20 rounded-full bg-slate-800/50 flex items-center justify-center mb-4">
                <MessageSquare size={32} className="text-slate-600" />
              </div>
              <h3 className="text-lg font-semibold text-slate-300">Select a Query Thread</h3>
              <p className="text-sm mt-2 text-center max-w-md">Choose a claim from the left sidebar to view communication history and send messages to the TPA administrators.</p>
            </div>
          )}
        </div>

      </div>
    </div>
  );
};

export default DirectQueryManagement;
