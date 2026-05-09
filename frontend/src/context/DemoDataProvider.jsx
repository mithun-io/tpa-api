import React, { createContext, useContext, useState, useEffect } from 'react';
import { INSURANCE_PLANS, PLAN_SUMMARY } from '../data/demoInsurancePlans';
import { DEMO_CUSTOMER_CLAIMS, DEMO_CUSTOMER_NOTIFICATIONS, DEMO_CUSTOMER_STATS } from '../data/demoClaims';
import {
  DEMO_SETTLEMENT_TICKER, DEMO_FINANCIAL_METRICS, DEMO_MONTHLY_TRENDS, DEMO_LOSS_RATIOS,
  DEMO_FRAUD_SIGNALS, DEMO_SLA_BREACHES, DEMO_HOSPITALS,
  DEMO_ADMIN_ESCALATIONS, DEMO_ADMIN_METRICS, DEMO_FRAUD_CASES,
  DEMO_RULE_ENGINE_LOGS, DEMO_OCR_QUEUE
} from '../data/demoOperationalData';

const DemoDataContext = createContext(null);

export const DemoDataProvider = ({ children }) => {
  // ── Live Simulation Layer ──────────────────────────────────────────────
  const [ticker, setTicker] = useState(DEMO_SETTLEMENT_TICKER);
  const [financialMetrics, setFinancialMetrics] = useState(DEMO_FINANCIAL_METRICS);
  const [slaBreaches, setSlaBreaches] = useState(DEMO_SLA_BREACHES);
  const [fraudSignals, setFraudSignals] = useState(DEMO_FRAUD_SIGNALS);
  const [adminMetrics, setAdminMetrics] = useState(DEMO_ADMIN_METRICS);

  // Keep ticker live — new transactions every 3 seconds
  useEffect(() => {
    const hospitals = ['Apollo Hospitals', 'Fortis Healthcare', 'Manipal Hospital', 'Narayana Health', 'KIMS Hospital', 'Ruby Hall Clinic', 'Aster Medcity'];
    const policies = ['Family Floater', 'Hospitalization', 'Corporate Group', 'Maternity', 'Accident', 'OPD & Wellness'];
    const statuses = ['CLEARED', 'CLEARED', 'CLEARED', 'CLEARED', 'BLOCKED', 'PENALTY'];
    let seqNum = 84292;

    const interval = setInterval(() => {
      const amount = Math.floor(Math.random() * 150000) + 5000;
      const status = statuses[Math.floor(Math.random() * statuses.length)];
      const now = new Date();
      const timeStr = `${now.getHours().toString().padStart(2,'0')}:${now.getMinutes().toString().padStart(2,'0')}:${now.getSeconds().toString().padStart(2,'0')}`;
      
      setTicker(prev => [{
        id: `TXN-${seqNum++}`,
        claimRef: `CLM-${Math.floor(Math.random() * 90000) + 10000}`,
        hospital: hospitals[Math.floor(Math.random() * hospitals.length)],
        amount,
        status,
        time: timeStr,
        policy: policies[Math.floor(Math.random() * policies.length)]
      }, ...prev].slice(0, 20));

      // Update financial metrics live
      if (status === 'CLEARED') {
        setFinancialMetrics(prev => ({ ...prev, reserves: prev.reserves - amount, pendingSettlements: Math.max(0, prev.pendingSettlements - 1) }));
      } else if (status === 'PENALTY') {
        setFinancialMetrics(prev => ({ ...prev, breachPenalties: prev.breachPenalties + Math.floor(amount * 0.1) }));
      }
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  // ── Admin metrics live pulse ────────────────────────────────────────────
  useEffect(() => {
    const interval = setInterval(() => {
      setAdminMetrics(prev => ({
        ...prev,
        totalClaimsInQueue: prev.totalClaimsInQueue + Math.floor(Math.random() * 3) - 1,
        kafkaLag: Math.max(0, prev.kafkaLag + Math.floor(Math.random() * 5) - 2),
        ruleEngineExecutions: prev.ruleEngineExecutions + Math.floor(Math.random() * 3),
      }));
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  const value = {
    // Insurance Plans (12 complete)
    insurancePlans: INSURANCE_PLANS,
    planSummary: PLAN_SUMMARY,

    // Customer Portal data
    customerClaims: DEMO_CUSTOMER_CLAIMS,
    customerNotifications: DEMO_CUSTOMER_NOTIFICATIONS,
    customerStats: DEMO_CUSTOMER_STATS,

    // Carrier Portal — Financial
    settlementTicker: ticker,
    financialMetrics,
    monthlyTrends: DEMO_MONTHLY_TRENDS,
    lossRatios: DEMO_LOSS_RATIOS,

    // Carrier Portal — Operational
    fraudSignals,
    slaBreaches,
    hospitals: DEMO_HOSPITALS,

    // Admin Portal
    adminEscalations: DEMO_ADMIN_ESCALATIONS,
    adminMetrics,
    fraudCases: DEMO_FRAUD_CASES,
    ruleEngineLogs: DEMO_RULE_ENGINE_LOGS,
    ocrQueue: DEMO_OCR_QUEUE,
  };

  return (
    <DemoDataContext.Provider value={value}>
      {children}
    </DemoDataContext.Provider>
  );
};

export const useDemoData = () => {
  const ctx = useContext(DemoDataContext);
  if (!ctx) throw new Error('useDemoData must be used inside DemoDataProvider');
  return ctx;
};

export default DemoDataContext;
