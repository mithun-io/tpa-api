import React, { useState } from 'react';
import { MapPin, Search, Phone, Navigation, Star, ShieldCheck, Filter } from 'lucide-react';

const HospitalFinder = () => {
  const [searchQuery, setSearchQuery] = useState('');
  
  const hospitals = [
    { id: 1, name: "City General Hospital", address: "123 Medical Plaza, New York", rating: 4.8, distance: "1.2 miles", specialty: "Multi-specialty", cashless: true, phone: "+1 212-555-0199" },
    { id: 2, name: "St. Jude's Children Center", address: "45 Hope Ave, Brooklyn", rating: 4.9, distance: "2.5 miles", specialty: "Pediatrics", cashless: true, phone: "+1 718-555-0123" },
    { id: 3, name: "Wellness Cardiac Institute", address: "88 Heart Beat Way, Queens", rating: 4.7, distance: "3.8 miles", specialty: "Cardiology", cashless: true, phone: "+1 347-555-0888" },
    { id: 4, name: "North Shore Orthopedics", address: "12 Bone St, Manhattan", rating: 4.5, distance: "4.1 miles", specialty: "Orthopedics", cashless: false, phone: "+1 212-555-0444" }
  ];

  return (
    <div className="space-y-6 h-[calc(100vh-120px)] flex flex-col">
      <div className="flex justify-between items-end flex-wrap gap-4 shrink-0">
        <div>
          <h1 className="text-2xl font-bold text-slate-100 tracking-tight">Network Hospitals</h1>
          <p className="text-sm text-slate-400 mt-1">Find nearby hospitals with cashless facilities</p>
        </div>
      </div>

      <div className="flex-1 flex gap-6 overflow-hidden">
        {/* Left: Sidebar Search & Results */}
        <div className="w-96 flex flex-col gap-4 overflow-hidden shrink-0">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
            <input 
              type="text" 
              placeholder="Search by name, specialty..."
              className="w-full pl-11 pr-4 py-3 bg-slate-800 border border-slate-700 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm text-slate-200"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div className="flex-1 overflow-y-auto space-y-3 pr-2 custom-scrollbar">
            {hospitals.map(hospital => (
              <div key={hospital.id} className="bg-slate-800 border border-slate-700 p-4 rounded-2xl hover:border-blue-500/50 transition-all cursor-pointer group">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-bold text-slate-200 group-hover:text-blue-400 transition-colors">{hospital.name}</h3>
                  {hospital.cashless && (
                    <span className="flex items-center gap-1 bg-emerald-500/10 text-emerald-400 text-[10px] font-bold px-2 py-0.5 rounded-full border border-emerald-500/20">
                      <ShieldCheck className="w-3 h-3" /> CASHLESS
                    </span>
                  )}
                </div>
                <div className="flex items-center gap-1.5 text-xs text-slate-500 mb-3">
                  <MapPin className="w-3.5 h-3.5" /> {hospital.address}
                </div>
                <div className="flex items-center justify-between mt-4 pt-3 border-t border-slate-700/50">
                  <div className="flex items-center gap-3">
                    <span className="flex items-center gap-1 text-xs font-bold text-amber-400">
                      <Star className="w-3.5 h-3.5 fill-current" /> {hospital.rating}
                    </span>
                    <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest">{hospital.distance}</span>
                  </div>
                  <div className="flex gap-2">
                    <button className="p-2 rounded-lg bg-slate-700 text-slate-300 hover:bg-slate-600 transition-colors">
                      <Phone className="w-3.5 h-3.5" />
                    </button>
                    <button className="p-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-colors">
                      <Navigation className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Right: Map Placeholder (Simulated for now, would integrate Leaflet) */}
        <div className="flex-1 bg-slate-900 rounded-3xl border border-slate-700 overflow-hidden relative shadow-inner">
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="text-center space-y-4">
              <div className="w-16 h-16 bg-slate-800 rounded-full flex items-center justify-center mx-auto border border-slate-700 shadow-xl">
                <MapPin className="w-8 h-8 text-blue-500 animate-bounce" />
              </div>
              <p className="text-slate-400 font-medium italic">Interactive Map View Rendering...</p>
              <div className="flex justify-center gap-4">
                 <div className="h-2 w-24 bg-slate-800 rounded-full overflow-hidden">
                    <div className="h-full bg-blue-500/50 animate-pulse w-full" />
                 </div>
              </div>
            </div>
          </div>
          
          {/* Map Controls */}
          <div className="absolute top-6 right-6 space-y-2">
            <button className="w-10 h-10 bg-slate-800 border border-slate-700 rounded-xl flex items-center justify-center text-slate-400 hover:text-white shadow-lg">+</button>
            <button className="w-10 h-10 bg-slate-800 border border-slate-700 rounded-xl flex items-center justify-center text-slate-400 hover:text-white shadow-lg">-</button>
          </div>
          
          {/* Legend */}
          <div className="absolute bottom-6 left-6 bg-slate-800/90 backdrop-blur-md border border-slate-700 p-4 rounded-2xl shadow-2xl">
            <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest mb-2">Hospital Map Legend</p>
            <div className="flex gap-4">
               <div className="flex items-center gap-2">
                  <div className="w-2.5 h-2.5 bg-blue-500 rounded-full" />
                  <span className="text-xs text-slate-300">Network</span>
               </div>
               <div className="flex items-center gap-2">
                  <div className="w-2.5 h-2.5 bg-emerald-500 rounded-full" />
                  <span className="text-xs text-slate-300">Cashless</span>
               </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HospitalFinder;
