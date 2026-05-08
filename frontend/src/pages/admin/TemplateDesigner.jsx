import React from 'react';
import { LayoutTemplate, Layers, Download, Save } from 'lucide-react';

const TemplateDesigner = () => {
  return (
    <div className="max-w-[1400px] mx-auto space-y-6">
      <div className="flex justify-between items-end flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <LayoutTemplate className="text-indigo-400" /> Template Designer for PDF Exports
          </h1>
          <p className="text-sm text-slate-400 mt-1">Drag-and-drop builder to customize the layout of decision reports for different carriers.</p>
        </div>
        <div className="flex gap-3">
          <button className="bg-slate-800 hover:bg-slate-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2">
            <Download className="w-4 h-4" /> Export Config
          </button>
          <button className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-xl text-sm font-medium transition-colors flex items-center gap-2">
            <Save className="w-4 h-4" /> Save Template
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 min-h-[600px]">
        {/* Components Panel */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 p-5">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-4 flex items-center gap-2">
            <Layers className="w-4 h-4" /> Draggable Components
          </h3>
          <div className="space-y-3">
            {['Carrier Header / Logo', 'Patient Details Block', 'Claim Summary Table', 'Medical ICD-10 Section', 'Approval / Rejection Signature', 'Financial Breakdown Table', 'Terms & Conditions Footer'].map(component => (
              <div key={component} className="bg-slate-900 border border-slate-700 p-3 rounded-lg text-sm text-slate-300 flex items-center gap-2 cursor-grab hover:border-indigo-500 transition-colors">
                <div className="w-4 h-4 flex flex-col justify-between">
                  <div className="w-full h-[2px] bg-slate-500 rounded-full"></div>
                  <div className="w-full h-[2px] bg-slate-500 rounded-full"></div>
                  <div className="w-full h-[2px] bg-slate-500 rounded-full"></div>
                </div>
                {component}
              </div>
            ))}
          </div>
        </div>

        {/* Canvas */}
        <div className="lg:col-span-3 bg-slate-900 rounded-2xl border border-slate-700 border-dashed p-10 flex flex-col items-center justify-center relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-full bg-[linear-gradient(to_right,#1e293b_1px,transparent_1px),linear-gradient(to_bottom,#1e293b_1px,transparent_1px)] bg-[size:40px_40px] opacity-20"></div>
          
          <div className="w-[600px] bg-white rounded shadow-2xl relative z-10 min-h-[800px] flex flex-col p-8">
             <div className="border-2 border-indigo-500 border-dashed bg-indigo-50 text-indigo-400 flex items-center justify-center p-8 rounded mb-4">
                Drag Header Component Here
             </div>
             <div className="border-2 border-slate-300 border-dashed bg-slate-50 text-slate-400 flex items-center justify-center flex-1 p-8 rounded">
                Drag Body Components Here
             </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TemplateDesigner;
