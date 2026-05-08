import React, { useState, useEffect } from 'react';
import axiosInstance from '../../api/axios';
import Loader from '../../components/Loader';
import { Users, Shield, Trash2, CheckCircle, XCircle } from 'lucide-react';

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = async () => {
    try {
      const res = await axiosInstance.get('/admin/users');
      setUsers(res.data.content || res.data || []);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const toggleBlock = async (id, isBlocked) => {
    try {
      if (isBlocked) {
        await axiosInstance.patch(`/admin/users/${id}/unblock`);
      } else {
        await axiosInstance.patch(`/admin/users/${id}/block`);
      }
      fetchUsers();
    } catch (e) {
      console.error(e);
    }
  };

  if (loading) return <Loader fullScreen />;

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-white flex items-center gap-2">
          <Users className="text-blue-400" /> User Management
        </h1>
      </div>

      <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
        <table className="w-full text-left text-sm text-slate-400">
          <thead className="bg-slate-900/50 text-slate-300">
            <tr>
              <th className="p-4">Name</th>
              <th className="p-4">Email</th>
              <th className="p-4">Role</th>
              <th className="p-4">Status</th>
              <th className="p-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-700/50">
            {users.map(u => (
              <tr key={u.id} className="hover:bg-slate-700/20">
                <td className="p-4 font-semibold text-white">{u.name || u.username}</td>
                <td className="p-4">{u.email}</td>
                <td className="p-4">
                  <span className="bg-blue-500/10 text-blue-400 px-2 py-1 rounded text-xs font-bold border border-blue-500/20">
                    {u.userRole || u.role}
                  </span>
                </td>
                <td className="p-4">
                  {u.blocked ? (
                    <span className="flex items-center gap-1 text-red-400 text-xs"><XCircle className="w-4 h-4"/> Blocked</span>
                  ) : (
                    <span className="flex items-center gap-1 text-emerald-400 text-xs"><CheckCircle className="w-4 h-4"/> Active</span>
                  )}
                </td>
                <td className="p-4 text-right">
                  <button onClick={() => toggleBlock(u.id, u.blocked)} className={`text-xs px-3 py-1.5 rounded-lg border transition-colors ${u.blocked ? 'border-emerald-500/50 text-emerald-400 hover:bg-emerald-500/10' : 'border-red-500/50 text-red-400 hover:bg-red-500/10'}`}>
                    {u.blocked ? 'Unblock' : 'Block'}
                  </button>
                </td>
              </tr>
            ))}
            {users.length === 0 && <tr><td colSpan="5" className="p-8 text-center">No users found.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminUsers;
