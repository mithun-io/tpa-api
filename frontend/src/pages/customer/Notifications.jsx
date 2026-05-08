import React, { useState, useEffect } from 'react';
import axiosInstance from '../../api/axios';
import Loader from '../../components/Loader';
import { Bell, CheckCircle, Clock } from 'lucide-react';
import { useWebSocket } from '../../hooks/useWebSocket';
import { useAuth } from '../../context/AuthContext';

const Notifications = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const { messages } = useWebSocket([`/topic/notifications/${user?.id || ''}`]);

  const fetchNotifications = async () => {
    try {
      const res = await axiosInstance.get('/notifications');
      setNotifications(res.data || []);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  useEffect(() => {
    const wsNotifications = messages[`/topic/notifications/${user?.id || ''}`];
    if (wsNotifications && wsNotifications.length > 0) {
      setNotifications(prev => [wsNotifications[wsNotifications.length - 1], ...prev]);
    }
  }, [messages, user?.id]);

  const markAsRead = async (id) => {
    try {
      await axiosInstance.patch(`/notifications/${id}/read`);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
    } catch (e) {
      console.error(e);
    }
  };

  const markAllAsRead = async () => {
    try {
      await axiosInstance.patch(`/notifications/read-all`);
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    } catch (e) {
      console.error(e);
    }
  };

  if (loading) return <Loader fullScreen />;

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-white flex items-center gap-2">
          <Bell className="text-blue-400" /> Notifications
          {unreadCount > 0 && <span className="text-xs bg-red-500 text-white px-2 py-1 rounded-full">{unreadCount} Unread</span>}
        </h1>
        <button onClick={markAllAsRead} className="text-sm text-blue-400 hover:text-blue-300">Mark all as read</button>
      </div>

      <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
        {notifications.length === 0 ? (
          <div className="p-8 text-center text-slate-500">No notifications yet.</div>
        ) : (
          <div className="divide-y divide-slate-700">
            {notifications.map(n => (
              <div key={n.id} className={`p-4 flex gap-4 transition-colors ${n.isRead ? 'bg-slate-800' : 'bg-slate-700/30'}`}>
                <div className="mt-1">
                  {n.isRead ? <CheckCircle className="text-slate-500 w-5 h-5" /> : <div className="w-2 h-2 rounded-full bg-blue-500 mt-2" />}
                </div>
                <div className="flex-1">
                  <p className={`text-sm ${n.isRead ? 'text-slate-300' : 'text-white font-semibold'}`}>{n.message}</p>
                  <p className="text-xs text-slate-500 flex items-center gap-1 mt-1">
                    <Clock className="w-3 h-3" /> {new Date(n.createdDate || Date.now()).toLocaleString()}
                  </p>
                </div>
                {!n.isRead && (
                  <button onClick={() => markAsRead(n.id)} className="text-xs text-blue-400 hover:text-blue-300 h-fit">Mark as read</button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Notifications;
