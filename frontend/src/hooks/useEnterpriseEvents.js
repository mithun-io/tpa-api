import { useContext } from 'react';
import EventContext from '../context/EnterpriseEventEngine';

export const useEnterpriseEvents = () => {
  const context = useContext(EventContext);
  if (!context) {
    throw new Error('useEnterpriseEvents must be used within an EnterpriseEventProvider');
  }
  return context;
};
