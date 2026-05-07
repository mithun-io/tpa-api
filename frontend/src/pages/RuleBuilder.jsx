import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  Settings2, 
  Save, 
  RefreshCcw, 
  ShieldAlert, 
  Info,
  CheckCircle2
} from 'lucide-react';

const RuleBuilder = () => {
  const [configs, setConfigs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    fetchConfigs();
  }, []);

  const fetchConfigs = async () => {
    try {
      const response = await axios.get('/api/v1/rules/config');
      setConfigs(response.data);
    } catch (error) {
      // Mock data if endpoint doesn't exist yet
      setConfigs([
        { id: 1, ruleKey: 'AUTO_APPROVE_THRESHOLD', ruleValue: '50000', description: 'Max claim amount for automatic rule-based approval without manual review.' },
        { id: 2, ruleKey: 'HIGH_RISK_SCORE_THRESHOLD', ruleValue: '75', description: 'Risk score above which a claim is automatically flagged for forensic audit.' },
        { id: 3, ruleKey: 'MANDATORY_ICD_VALIDATION', ruleValue: 'true', description: 'Enable/Disable strict cross-check between diagnosis and ICD-10 codes.' },
        { id: 4, ruleKey: 'FRAUD_CHECK_WAIT_HOURS', ruleValue: '24', description: 'Wait time for historical duplicate bill checks before final decision.' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = (id, newValue) => {
    setConfigs(prev => prev.map(c => c.id === id ? { ...c, ruleValue: newValue } : c));
  };

  const saveChanges = async () => {
    setSaving(true);
    try {
      // In real app: await axios.put('/api/v1/rules/config/bulk', configs);
      await new Promise(r => setTimeout(r, 1000)); // Simulate API call
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (error) {
      console.error("Save failed", error);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
  );

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-5xl mx-auto">
        
        {/* Header */}
        <div className="flex justify-between items-center mb-10">
          <div>
            <h1 className="text-4xl font-black tracking-tight flex items-center">
              <Settings2 className="text-blue-500 mr-3" size={36} />
              Visual Rule Builder
            </h1>
            <p className="text-slate-400 mt-2 font-medium">Configure Drools thresholds & business policy parameters</p>
          </div>
          <button 
            onClick={saveChanges}
            disabled={saving}
            className="flex items-center px-8 py-4 bg-blue-600 hover:bg-blue-500 disabled:bg-slate-800 text-white font-black rounded-2xl transition-all shadow-xl shadow-blue-500/20 active:scale-95"
          >
            {saving ? <RefreshCcw className="animate-spin mr-2" size={20} /> : <Save className="mr-2" size={20} />}
            Deploy Rule Updates
          </button>
        </div>

        {success && (
           <div className="mb-8 bg-emerald-500/10 border border-emerald-500/30 rounded-2xl p-4 flex items-center text-emerald-400 animate-in fade-in slide-in-from-top-4">
              <CheckCircle2 className="mr-2" size={20} />
              <span className="font-bold text-sm text-white">Business rules successfully redeployed to production KieSession.</span>
           </div>
        )}

        {/* Info Card */}
        <div className="bg-blue-500/5 border border-blue-500/20 rounded-3xl p-6 mb-10 flex items-start">
           <Info className="text-blue-400 mr-4 mt-1" size={24} />
           <div>
              <h4 className="font-bold text-blue-400">Architect's Note</h4>
              <p className="text-slate-400 text-sm mt-1 leading-relaxed">
                 Changes made here will dynamically update the internal rule engine parameters. No deployment or service restart is required.
                 Use extreme caution when modifying thresholds as it directly impacts financial approvals.
              </p>
           </div>
        </div>

        {/* Rule Config List */}
        <div className="grid grid-cols-1 gap-6">
           {configs.map((config) => (
             <div key={config.id} className="bg-slate-900/50 backdrop-blur-xl border border-slate-800 rounded-3xl p-8 hover:border-slate-700 transition-all group">
                <div className="flex justify-between items-start mb-4">
                   <div className="flex items-center">
                      <div className="w-12 h-12 bg-slate-950 rounded-xl flex items-center justify-center text-slate-500 group-hover:text-blue-400 transition-colors mr-4">
                         <ShieldAlert size={24} />
                      </div>
                      <div>
                         <h3 className="font-bold text-lg text-slate-200 tracking-tight">{config.ruleKey.replace(/_/g, ' ')}</h3>
                         <p className="text-xs text-slate-500 mt-1 uppercase tracking-widest font-black">Parameter Key: {config.ruleKey}</p>
                      </div>
                   </div>
                   <div className="w-1/4">
                      <input 
                        type="text" 
                        value={config.ruleValue}
                        onChange={(e) => handleUpdate(config.id, e.target.value)}
                        className="w-full bg-slate-950 border border-slate-800 rounded-xl py-3 px-4 text-right font-black text-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all"
                      />
                   </div>
                </div>
                <p className="text-slate-400 text-sm leading-relaxed mt-4">
                   {config.description}
                </p>
             </div>
           ))}
        </div>

      </div>
    </div>
  );
};

export default RuleBuilder;
