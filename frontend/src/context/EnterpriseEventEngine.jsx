import React, { createContext, useContext, useReducer, useEffect, useCallback } from 'react';

const EventContext = createContext();

const initialState = {
  events: [],
  liveClaims: {
    ingestion: [],
    aiReview: [],
    medicalReview: [],
    approval: [],
    settlement: []
  },
  slaBreaches: [],
  financialAnomalies: [],
  fraudSignals: [],
  processors: {
    ai: { util: 45, status: 'OPTIMAL' },
    medical: { util: 82, status: 'WARNING' },
    finance: { util: 60, status: 'OPTIMAL' }
  },
  metrics: {
    reserves: 14500000,
    savings: 1250000,
    breachPenalties: 0
  }
};

const reducer = (state, action) => {
  switch (action.type) {
    case 'TICK': {
      let nextState = { ...state };
      
      // Update processor utilization dynamically
      nextState.processors = {
        ai: { util: Math.max(10, Math.min(99, state.processors.ai.util + (Math.random() * 10 - 5))), status: state.processors.ai.util > 85 ? 'WARNING' : 'OPTIMAL' },
        medical: { util: Math.max(10, Math.min(99, state.processors.medical.util + (Math.random() * 12 - 6))), status: state.processors.medical.util > 90 ? 'CRITICAL' : state.processors.medical.util > 75 ? 'WARNING' : 'OPTIMAL' },
        finance: { util: Math.max(10, Math.min(99, state.processors.finance.util + (Math.random() * 8 - 4))), status: state.processors.finance.util > 80 ? 'WARNING' : 'OPTIMAL' }
      };

      return nextState;
    }

    case 'ADD_EVENT': {
      const e = { ...action.payload, id: `EVT-${Date.now()}`, timestamp: new Date().toISOString() };
      return { ...state, events: [e, ...state.events].slice(0, 50) };
    }

    case 'INGEST_CLAIM': {
      const c = { id: `TRX-${Math.floor(Math.random()*9000)+1000}`, val: Math.floor(Math.random()*25000), time: 0, risk: 'LOW' };
      return { 
        ...state, 
        liveClaims: { ...state.liveClaims, ingestion: [c, ...state.liveClaims.ingestion].slice(0, 15) }
      };
    }

    case 'MOVE_CLAIM': {
      const { from, to, claimId, modifications = {} } = action.payload;
      const claim = state.liveClaims[from].find(c => c.id === claimId);
      if (!claim) return state;

      const updatedClaim = { ...claim, ...modifications };
      return {
        ...state,
        liveClaims: {
          ...state.liveClaims,
          [from]: state.liveClaims[from].filter(c => c.id !== claimId),
          [to]: [updatedClaim, ...state.liveClaims[to]].slice(0, 15)
        }
      };
    }

    case 'TRIGGER_SLA_BREACH': {
      const { claim } = action.payload;
      const penalty = Math.floor(claim.val * 0.1); // 10% penalty
      return {
        ...state,
        slaBreaches: [{ ...claim, breachTime: new Date().toISOString(), penalty }, ...state.slaBreaches].slice(0, 10),
        metrics: { ...state.metrics, breachPenalties: state.metrics.breachPenalties + penalty }
      };
    }

    case 'TRIGGER_FINANCIAL_ANOMALY': {
      const anomaly = action.payload;
      return {
        ...state,
        financialAnomalies: [{ ...anomaly, time: new Date().toISOString() }, ...state.financialAnomalies].slice(0, 10)
      };
    }
    
    case 'TRIGGER_FRAUD_SIGNAL': {
      const signal = action.payload;
      return {
        ...state,
        fraudSignals: [{ ...signal, time: new Date().toISOString() }, ...state.fraudSignals].slice(0, 10)
      };
    }
    
    case 'PROCESS_SETTLEMENT': {
      const { amount, success } = action.payload;
      if (success) {
        return {
          ...state,
          metrics: { ...state.metrics, reserves: state.metrics.reserves - amount }
        };
      }
      return state;
    }

    default:
      return state;
  }
};

export const EnterpriseEventProvider = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    // Initial Seed
    for(let i=0; i<3; i++) dispatch({ type: 'INGEST_CLAIM' });

    // The Master Event Loop
    const masterTick = setInterval(() => {
      dispatch({ type: 'TICK' });

      // Simulate Ingestion
      if (Math.random() > 0.6) {
        dispatch({ type: 'INGEST_CLAIM' });
      }

      // Progress claims through the pipeline
      // Ingestion -> AI
      if (state.liveClaims.ingestion.length > 0 && Math.random() > 0.4) {
        const c = state.liveClaims.ingestion[state.liveClaims.ingestion.length - 1];
        dispatch({ type: 'MOVE_CLAIM', payload: { from: 'ingestion', to: 'aiReview', claimId: c.id } });
        dispatch({ type: 'ADD_EVENT', payload: { type: 'INFO', module: 'ROUTING', message: `Claim ${c.id} entering AI Validation Engine` }});
      }

      // AI -> Medical OR Approval
      if (state.liveClaims.aiReview.length > 0 && Math.random() > 0.5) {
        const c = state.liveClaims.aiReview[state.liveClaims.aiReview.length - 1];
        if (c.val > 10000 || Math.random() > 0.8) {
          // Escalate to Medical
          dispatch({ type: 'MOVE_CLAIM', payload: { from: 'aiReview', to: 'medicalReview', claimId: c.id, modifications: { risk: 'HIGH', crit: true } } });
          dispatch({ type: 'ADD_EVENT', payload: { type: 'WARNING', module: 'AI', message: `High value claim ${c.id} escalated to Medical Review` }});
        } else {
          // Auto approve
          dispatch({ type: 'MOVE_CLAIM', payload: { from: 'aiReview', to: 'approval', claimId: c.id, modifications: { aiScore: 98 } } });
        }
      }

      // Medical -> Approval
      if (state.liveClaims.medicalReview.length > 0 && Math.random() > 0.6) {
        const c = state.liveClaims.medicalReview[state.liveClaims.medicalReview.length - 1];
        dispatch({ type: 'MOVE_CLAIM', payload: { from: 'medicalReview', to: 'approval', claimId: c.id } });
        dispatch({ type: 'ADD_EVENT', payload: { type: 'INFO', module: 'MEDICAL', message: `Medical L2 cleared claim ${c.id}` }});
      }

      // Approval -> Settlement
      if (state.liveClaims.approval.length > 0 && Math.random() > 0.7) {
        const c = state.liveClaims.approval[state.liveClaims.approval.length - 1];
        dispatch({ type: 'MOVE_CLAIM', payload: { from: 'approval', to: 'settlement', claimId: c.id } });
        dispatch({ type: 'PROCESS_SETTLEMENT', payload: { amount: c.val, success: true } });
        dispatch({ type: 'ADD_EVENT', payload: { type: 'INFO', module: 'TREASURY', message: `Claim ${c.id} queued for payout block` }});
      }

      // Cross-Module Triggers
      // Trigger SLA Breach
      if (state.liveClaims.medicalReview.length > 3 && Math.random() > 0.85) {
        const c = state.liveClaims.medicalReview[0];
        dispatch({ type: 'TRIGGER_SLA_BREACH', payload: { claim: c } });
        dispatch({ type: 'ADD_EVENT', payload: { type: 'CRITICAL', module: 'COMPLIANCE', message: `SLA BREACH on ${c.id}! Financial penalty applied.` }});
      }

      // Trigger Financial Anomaly
      if (Math.random() > 0.9) {
        const anomaly = { id: `ANO-${Math.floor(Math.random()*900)}`, desc: 'Orthopedic Payout Spike Detected', var: '+22%', risk: 'HIGH' };
        dispatch({ type: 'TRIGGER_FINANCIAL_ANOMALY', payload: anomaly });
        dispatch({ type: 'ADD_EVENT', payload: { type: 'WARNING', module: 'TREASURY', message: `Anomaly Detected: ${anomaly.desc}` }});
      }

    }, 2000);

    return () => clearInterval(masterTick);
  }, [state.liveClaims]); // Depend on liveClaims to pick up the latest for moving

  const publishEvent = useCallback((event) => {
    dispatch({ type: 'ADD_EVENT', payload: event });
  }, []);

  const manualRoute = useCallback((claimId, from, to) => {
    dispatch({ type: 'MOVE_CLAIM', payload: { claimId, from, to } });
    dispatch({ type: 'ADD_EVENT', payload: { type: 'ESCALATED', module: 'OPS', message: `Manual override: ${claimId} forced to ${to}` }});
  }, []);

  return (
    <EventContext.Provider value={{ state, dispatch, publishEvent, manualRoute }}>
      {children}
    </EventContext.Provider>
  );
};

export default EventContext;
