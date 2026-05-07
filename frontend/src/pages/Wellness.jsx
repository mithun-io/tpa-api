import React, { useState } from 'react';
import { Heart, Activity, Zap, Bot, Send, User, ChevronRight, Apple, Smartphone } from 'lucide-react';
import { analyzeClaimAI } from '../api/claim.service';
import toast from 'react-hot-toast';

const Wellness = () => {
  const [messages, setMessages] = useState([
    { role: 'bot', text: "Hello! I'm your AI Care Buddy. How are you feeling today? I can help with policy queries, symptom checking, or health tips." }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSend = async () => {
    if (!input.trim() || loading) return;
    
    const userMsg = input.trim();
    setMessages(prev => [...prev, { role: 'user', text: userMsg }]);
    setInput('');
    setLoading(true);

    try {
      // We use claimId 0 or a generic prompt for wellness
      const response = await analyzeClaimAI(0, userMsg);
      setMessages(prev => [...prev, { role: 'bot', text: response.recommendation || "I'm here to help. What else would you like to know?" }]);
    } catch (err) {
      setMessages(prev => [...prev, { role: 'bot', text: "I'm having a bit of trouble connecting right now. But generally, I recommend staying active and checking your policy limits regularly!" }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-[1400px] mx-auto">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-100 tracking-tight">Wellness & Care</h1>
          <p className="text-sm text-slate-400 mt-1">Manage your health and get AI-powered assistance</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left: Wellness Stats & Wearable Sync */}
        <div className="lg:col-span-1 space-y-6">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
            <h3 className="text-base font-bold text-slate-100 mb-6 flex items-center gap-2">
              <Zap className="w-5 h-5 text-amber-400" /> Wellness Score
            </h3>
            <div className="flex flex-col items-center py-4">
               <div className="relative w-40 h-40 flex items-center justify-center">
                  <svg className="w-full h-full -rotate-90">
                    <circle cx="80" cy="80" r="70" stroke="currentColor" strokeWidth="8" fill="transparent" className="text-slate-700" />
                    <circle cx="80" cy="80" r="70" stroke="currentColor" strokeWidth="8" fill="transparent" strokeDasharray={440} strokeDashoffset={440 * (1 - 0.78)} className="text-emerald-500 transition-all duration-1000" />
                  </svg>
                  <div className="absolute inset-0 flex flex-col items-center justify-center">
                     <span className="text-4xl font-black text-slate-100">78</span>
                     <span className="text-[10px] font-bold text-slate-500 uppercase tracking-widest">Out of 100</span>
                  </div>
               </div>
               <p className="mt-6 text-sm text-slate-300 font-medium">Excellent Progress!</p>
               <p className="text-xs text-slate-500 text-center mt-2 px-4 leading-relaxed">
                  You're in the top 15% of healthy members. Keep it up to earn a 5% premium discount next year!
               </p>
            </div>
          </div>

          <div className="bg-slate-800 rounded-2xl border border-slate-700 p-6">
            <h3 className="text-sm font-bold text-slate-100 mb-4">Sync Devices</h3>
            <div className="space-y-3">
               <button className="w-full flex items-center justify-between p-4 bg-slate-900 border border-slate-700 rounded-xl hover:border-blue-500/50 transition-all group">
                  <div className="flex items-center gap-3">
                     <Apple className="w-5 h-5 text-slate-100" />
                     <span className="text-sm font-medium text-slate-300">Apple Health</span>
                  </div>
                  <span className="text-[10px] font-bold text-blue-400 bg-blue-500/10 px-2 py-1 rounded-md border border-blue-500/20">CONNECTED</span>
               </button>
               <button className="w-full flex items-center justify-between p-4 bg-slate-900 border border-slate-700 rounded-xl hover:border-emerald-500/50 transition-all group opacity-50 grayscale hover:grayscale-0">
                  <div className="flex items-center gap-3">
                     <Smartphone className="w-5 h-5 text-emerald-400" />
                     <span className="text-sm font-medium text-slate-300">Google Fit</span>
                  </div>
                  <ChevronRight className="w-4 h-4 text-slate-600" />
               </button>
            </div>
          </div>
        </div>

        {/* Right: AI Care Buddy Chat */}
        <div className="lg:col-span-2 bg-slate-800 rounded-2xl border border-slate-700 flex flex-col h-[600px] shadow-2xl relative overflow-hidden">
          <div className="p-4 border-b border-slate-700 flex items-center gap-3 bg-slate-800/50 backdrop-blur-md sticky top-0 z-10">
            <div className="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-900/30">
               <Bot className="w-6 h-6 text-white" />
            </div>
            <div>
               <h3 className="text-sm font-bold text-slate-100">Care Buddy AI</h3>
               <div className="flex items-center gap-1.5">
                  <div className="w-1.5 h-1.5 bg-emerald-500 rounded-full animate-pulse" />
                  <span className="text-[10px] font-bold text-emerald-500 uppercase tracking-widest">Online & Ready</span>
               </div>
            </div>
          </div>

          <div className="flex-1 overflow-y-auto p-6 space-y-4 custom-scrollbar">
            {messages.map((m, idx) => (
              <div key={idx} className={`flex ${m.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                <div className={`max-w-[80%] flex gap-3 ${m.role === 'user' ? 'flex-row-reverse' : ''}`}>
                  <div className={`w-8 h-8 rounded-lg shrink-0 flex items-center justify-center ${m.role === 'user' ? 'bg-slate-700' : 'bg-blue-600'}`}>
                    {m.role === 'user' ? <User className="w-4 h-4 text-slate-300" /> : <Bot className="w-4 h-4 text-white" />}
                  </div>
                  <div className={`p-4 rounded-2xl text-sm leading-relaxed ${
                    m.role === 'user' 
                    ? 'bg-blue-600 text-white rounded-tr-none shadow-lg shadow-blue-900/20' 
                    : 'bg-slate-900 text-slate-300 border border-slate-700 rounded-tl-none'
                  }`}>
                    {m.text}
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="p-4 bg-slate-900/50 border-t border-slate-700">
            <div className="relative">
              <input 
                type="text" 
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSend()}
                placeholder="Ask me anything about your health or policy..."
                className="w-full pl-4 pr-14 py-3.5 bg-slate-800 border border-slate-700 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm text-slate-200 shadow-inner"
              />
              <button 
                onClick={handleSend}
                className="absolute right-2 top-1/2 -translate-y-1/2 p-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-all shadow-lg"
              >
                <Send className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Wellness;
