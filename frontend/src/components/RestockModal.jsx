import { useState, useEffect } from 'react';
import { Package, X, Loader2 } from 'lucide-react';

const RestockModal = ({ open, onClose, onSubmit, sweet, isLoading }) => {
  const [quantity, setQuantity] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (open) {
      setQuantity('');
      setError('');
    }
  }, [open, sweet]);

  if (!open || !sweet) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    const qty = parseInt(quantity);
    
    if (!qty || qty <= 0) {
      setError('Quantity must be greater than 0');
      return;
    }
    onSubmit(sweet.id, qty);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-in fade-in">
      <div className="bg-white w-full max-w-md rounded-3xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200">
        
        {/* Header */}
        <div className="px-6 py-4 border-b border-border flex justify-between items-center bg-secondary/20">
          <div className="flex items-center gap-2 text-primary">
            <Package className="h-5 w-5" />
            <h2 className="text-xl font-display">Restock Sweet</h2>
          </div>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground transition">
            <X size={20} />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit} className="p-6">
          <p className="text-muted-foreground mb-6">
            Add more stock for <span className="font-semibold text-foreground">{sweet.name}</span>
          </p>

          <div className="p-4 rounded-xl bg-muted/50 mb-6 flex justify-between items-center border border-border">
            <span className="text-sm font-medium text-muted-foreground">Current Stock</span>
            <span className="text-lg font-bold text-foreground">{sweet.quantity} units</span>
          </div>

          <div className="space-y-2 mb-6">
            <label className="text-sm font-medium ml-1">Quantity to Add</label>
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
              placeholder="Enter amount..."
              className="w-full px-4 py-3 rounded-xl border border-input focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all"
              autoFocus
            />
            {error && <p className="text-sm text-red-500 font-medium ml-1">{error}</p>}
          </div>

          {/* Footer */}
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-muted-foreground hover:bg-secondary/50 rounded-lg transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading || !quantity}
              className="px-6 py-2 bg-primary text-white rounded-lg font-medium shadow-lg shadow-primary/20 hover:bg-primary/90 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
            >
              {isLoading && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
              Restock Now
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RestockModal;