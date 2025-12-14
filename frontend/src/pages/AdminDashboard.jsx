import { useState, useEffect } from 'react';
import { useSweets } from '../hooks/useSweets';
import RestockModal from '../components/RestockModal';
import DeleteConfirmModal from '../components/DeleteConfirmModal';
import { Plus, Edit2, Trash2, PackagePlus, Search } from 'lucide-react';

const AdminDashboard = () => {
  const { sweets, fetchSweets, addSweet, updateSweet, deleteSweet, restockSweet, isLoading } = useSweets();
  
  // Modal States
  const [restockModal, setRestockModal] = useState({ open: false, sweet: null });
  const [deleteModal, setDeleteModal] = useState({ open: false, sweet: null });
  const [isFormOpen, setIsFormOpen] = useState(false);
  
  // Form State
  const [formData, setFormData] = useState({ id: null, name: '', category: '', price: '', quantity: '' });

  useEffect(() => {
    fetchSweets();
  }, [fetchSweets]);

  // Handlers
  const handleRestock = async (id, qty) => {
    await restockSweet(id, qty);
    setRestockModal({ open: false, sweet: null });
  };

  const handleDelete = async () => {
    if (deleteModal.sweet) {
      await deleteSweet(deleteModal.sweet.id);
      setDeleteModal({ open: false, sweet: null });
    }
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (formData.id) {
      await updateSweet(formData.id, formData);
    } else {
      await addSweet(formData);
    }
    setIsFormOpen(false);
  };

  const openEdit = (sweet) => {
    setFormData(sweet);
    setIsFormOpen(true);
  };

  const openAdd = () => {
    setFormData({ id: null, name: '', category: '', price: '', quantity: '' });
    setIsFormOpen(true);
  };

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex flex-col md:flex-row justify-between items-center mb-8 gap-4">
          <div>
            <h1 className="text-3xl font-display text-foreground">Admin Dashboard</h1>
            <p className="text-muted-foreground">Manage your sweet inventory</p>
          </div>
          <button 
            onClick={openAdd} 
            className="bg-primary text-white px-6 py-3 rounded-xl font-medium shadow-lg shadow-primary/25 hover:bg-primary/90 transition flex items-center gap-2"
          >
            <Plus size={20} /> Add New Sweet
          </button>
        </div>

        {/* Table */}
        <div className="bg-white rounded-3xl shadow-sm border border-border overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-secondary/30 text-secondary-foreground">
                <tr>
                  <th className="px-6 py-4 font-semibold text-sm">Name</th>
                  <th className="px-6 py-4 font-semibold text-sm">Category</th>
                  <th className="px-6 py-4 font-semibold text-sm">Price</th>
                  <th className="px-6 py-4 font-semibold text-sm">Stock</th>
                  <th className="px-6 py-4 font-semibold text-sm text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {sweets.map((sweet) => (
                  <tr key={sweet.id} className="hover:bg-amber-50/50 transition">
                    <td className="px-6 py-4 font-medium">{sweet.name}</td>
                    <td className="px-6 py-4"><span className="px-3 py-1 rounded-full bg-gray-100 text-xs font-medium">{sweet.category}</span></td>
                    <td className="px-6 py-4 text-primary font-bold">${sweet.price}</td>
                    <td className="px-6 py-4">
                      <div className={`flex items-center gap-2 ${sweet.quantity < 10 ? 'text-red-500' : 'text-green-600'}`}>
                        <span className={`w-2 h-2 rounded-full ${sweet.quantity < 10 ? 'bg-red-500' : 'bg-green-600'}`}></span>
                        {sweet.quantity}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-right space-x-2">
                      <button onClick={() => setRestockModal({ open: true, sweet })} className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition" title="Restock">
                        <PackagePlus size={18} />
                      </button>
                      <button onClick={() => openEdit(sweet)} className="p-2 text-amber-600 hover:bg-amber-50 rounded-lg transition" title="Edit">
                        <Edit2 size={18} />
                      </button>
                      <button onClick={() => setDeleteModal({ open: true, sweet })} className="p-2 text-red-500 hover:bg-red-50 rounded-lg transition" title="Delete">
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modals */}
      <RestockModal 
        open={restockModal.open} 
        sweet={restockModal.sweet} 
        onClose={() => setRestockModal({ open: false, sweet: null })}
        onSubmit={handleRestock}
        isLoading={isLoading}
      />

      <DeleteConfirmModal 
        open={deleteModal.open}
        sweetName={deleteModal.sweet?.name}
        onClose={() => setDeleteModal({ open: false, sweet: null })}
        onConfirm={handleDelete}
        isLoading={isLoading}
      />

      {/* Add/Edit Modal (Simple Inline Implementation) */}
      {isFormOpen && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-3xl shadow-2xl w-full max-w-md p-6">
            <h2 className="text-2xl font-display text-primary mb-6">{formData.id ? 'Edit Sweet' : 'Add New Sweet'}</h2>
            <form onSubmit={handleFormSubmit} className="space-y-4">
              <div>
                <label className="text-xs font-medium text-muted-foreground">Name</label>
                <input required type="text" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} 
                  className="w-full px-4 py-2 border border-input rounded-xl focus:ring-2 focus:ring-primary/20 outline-none" />
              </div>
              <div>
                <label className="text-xs font-medium text-muted-foreground">Category</label>
                <input required type="text" value={formData.category} onChange={e => setFormData({...formData, category: e.target.value})} 
                  className="w-full px-4 py-2 border border-input rounded-xl focus:ring-2 focus:ring-primary/20 outline-none" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-xs font-medium text-muted-foreground">Price</label>
                  <input required type="number" step="0.01" value={formData.price} onChange={e => setFormData({...formData, price: e.target.value})} 
                    className="w-full px-4 py-2 border border-input rounded-xl focus:ring-2 focus:ring-primary/20 outline-none" />
                </div>
                <div>
                  <label className="text-xs font-medium text-muted-foreground">Initial Qty</label>
                  <input required type="number" value={formData.quantity} onChange={e => setFormData({...formData, quantity: e.target.value})} 
                    className="w-full px-4 py-2 border border-input rounded-xl focus:ring-2 focus:ring-primary/20 outline-none" />
                </div>
              </div>
              <div className="flex justify-end gap-3 mt-8">
                <button type="button" onClick={() => setIsFormOpen(false)} className="px-4 py-2 text-muted-foreground hover:bg-secondary/50 rounded-xl transition">Cancel</button>
                <button type="submit" disabled={isLoading} className="px-6 py-2 bg-primary text-white rounded-xl font-medium shadow-lg hover:bg-primary/90 transition">
                  {formData.id ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;