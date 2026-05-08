import React, { useState, useEffect, useRef } from 'react';
import axiosInstance from '../api/axios';
import { Send, User, Building2, MessageSquare } from 'lucide-react';

const QueryThread = ({ claimId, currentUser, isCarrier }) => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const scrollRef = useRef(null);

  useEffect(() => {
    fetchMessages();
    const interval = setInterval(fetchMessages, 5000); // Poll every 5s
    return () => clearInterval(interval);
  }, [claimId]);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  const fetchMessages = async () => {
    try {
      const response = await axiosInstance.get(`/queries/${claimId}`);
      setMessages(response.data?.data || response.data || []);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching queries", error);
    }
  };

  const handleSend = async (e) => {
    e.preventDefault();
    if (!newMessage.trim()) return;

    try {
      await axiosInstance.post(`/queries/${claimId}?username=${currentUser}&isCarrier=${isCarrier}`, {
        message: newMessage
      });
      setNewMessage('');
      fetchMessages();
    } catch (error) {
      console.error("Error sending query", error);
    }
  };

  return (
    <div className="bg-slate-900/80 backdrop-blur-md border border-slate-800 rounded-3xl flex flex-col h-[500px] overflow-hidden shadow-2xl">
      {/* Header */}
      <div className="p-6 border-b border-slate-800 bg-slate-900/50 flex items-center justify-between">
        <div className="flex items-center">
          <div className="w-10 h-10 bg-blue-500/20 rounded-xl flex items-center justify-center text-blue-400 mr-3">
            <MessageSquare size={20} />
          </div>
          <div>
            <h3 className="font-bold text-white">Direct Query Management</h3>
            <p className="text-xs text-slate-500">Communicating regarding Claim #{claimId}</p>
          </div>
        </div>
        <div className="flex -space-x-2">
           <div className="w-8 h-8 rounded-full bg-slate-800 border-2 border-slate-900 flex items-center justify-center text-slate-400"><User size={14} /></div>
           <div className="w-8 h-8 rounded-full bg-blue-600 border-2 border-slate-900 flex items-center justify-center text-white"><Building2 size={14} /></div>
        </div>
      </div>

      {/* Messages Area */}
      <div 
        ref={scrollRef}
        className="flex-1 overflow-y-auto p-6 space-y-4 scrollbar-thin scrollbar-thumb-slate-800 scrollbar-track-transparent"
      >
        {loading ? (
          <div className="flex items-center justify-center h-full text-slate-600 italic">Initializing secure channel...</div>
        ) : messages.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full opacity-30">
            <MessageSquare size={48} className="mb-2" />
            <p className="text-sm">No queries raised on this claim yet.</p>
          </div>
        ) : (
          messages.map((m) => {
            const isMe = m.senderUsername === currentUser;
            return (
              <div key={m.id} className={`flex ${isMe ? 'justify-end' : 'justify-start'}`}>
                <div className={`max-w-[80%] p-4 rounded-2xl text-sm leading-relaxed ${
                  isMe ? 'bg-blue-600 text-white rounded-tr-none' : 'bg-slate-800 text-slate-200 rounded-tl-none'
                }`}>
                  <div className="flex items-center mb-1 text-[10px] font-bold uppercase tracking-wider opacity-60">
                    {m.isCarrierQuery ? <Building2 size={10} className="mr-1" /> : <User size={10} className="mr-1" />}
                    {m.senderUsername}
                  </div>
                  {m.message}
                  <div className="mt-2 text-[9px] opacity-40 text-right">
                    {new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Input Area */}
      <form onSubmit={handleSend} className="p-6 bg-slate-950/50 border-t border-slate-800">
        <div className="relative">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder="Type your query or response..."
            className="w-full bg-slate-900 border border-slate-800 rounded-2xl py-4 pl-6 pr-14 text-white focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 transition-all placeholder:text-slate-600"
          />
          <button 
            type="submit"
            className="absolute right-2 top-2 bottom-2 w-10 bg-blue-600 hover:bg-blue-500 text-white rounded-xl flex items-center justify-center transition-colors shadow-lg shadow-blue-500/20"
          >
            <Send size={18} />
          </button>
        </div>
      </form>
    </div>
  );
};

export default QueryThread;
