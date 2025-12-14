import { AlertTriangle, Loader2 } from 'lucide-react';

const DeleteConfirmModal = ({ open, onClose, onConfirm, sweetName, isLoading }) => {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-in fade-in">
      <div className="bg-white w-full max-w-md rounded-3xl shadow-2xl p-6 animate-in zoom-in-95 duration-200 border border-red-100">
        
        <div className="flex items-start gap-4 mb-4">
          <div className="p-3 rounded-full bg-red-100 text-red-600 shrink-0">
            <AlertTriangle className="h-6 w-6" />
          </div>
          <div>
            <h2 className="text-xl font-bold text-gray-900 mb-2">Delete Sweet?</h2>
            <p className="text-gray-500 leading-relaxed">
              Are you sure you want to delete <span className="font-semibold text-gray-900">{sweetName}</span>? 
              This action cannot be undone and will permanently remove this item from your shop.
            </p>
          </div>
        </div>

        <div className="flex justify-end gap-3 mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-lg transition"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            disabled={isLoading}
            className="px-4 py-2 bg-red-600 text-white rounded-lg font-medium shadow-lg shadow-red-200 hover:bg-red-700 transition flex items-center"
          >
            {isLoading && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
            Delete Sweet
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmModal;