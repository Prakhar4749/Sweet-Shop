import { useState, useCallback } from 'react';
import api from '../services/api';
import toast from 'react-hot-toast';

export const useSweets = () => {
  const [sweets, setSweets] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // Helper to extract backend error message
  const handleError = (err) => {
    const msg = err.response?.data?.message || err.message || "An unexpected error occurred";
    console.error("[useSweets] Error:", err);
    toast.error(msg);
    return msg;
  };

  // Helper to extract backend success message
  const handleSuccess = (res, defaultMsg) => {
    const msg = res.data?.message || defaultMsg;
    toast.success(msg);
  };

  const fetchSweets = useCallback(async (filters = {}) => {
    setIsLoading(true);
    setError(null);
    try {
      const params = {};
      if (filters.search) params.query = filters.search;
      if (filters.minPrice) params.minPrice = filters.minPrice;
      if (filters.maxPrice) params.maxPrice = filters.maxPrice;

      const endpoint = Object.keys(params).length > 0 ? '/sweets/search' : '/sweets';
      const res = await api.get(endpoint, { params });
      
      // Structure: { success: true, message: "...", data: [ ...sweets ] }
      // The array is inside res.data.data
      let data = [];
      if (res.data && Array.isArray(res.data.data)) {
        data = res.data.data;
      } else if (Array.isArray(res.data)) {
        // Fallback if backend sends direct array
        data = res.data;
      }

      setSweets(data);
    } catch (err) {
      const msg = err.response?.data?.message || "Failed to load sweets";
      setError(msg);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const addSweet = useCallback(async (data) => {
    setIsLoading(true);
    try {
      const res = await api.post('/sweets', data);
      handleSuccess(res, 'Sweet added successfully!');
      await fetchSweets(); 
    } catch (err) {
      handleError(err);
    } finally {
      setIsLoading(false);
    }
  }, [fetchSweets]);

  const updateSweet = useCallback(async (id, data) => {
    setIsLoading(true);
    try {
      const res = await api.put(`/sweets/${id}`, data);
      handleSuccess(res, 'Sweet updated successfully!');
      await fetchSweets();
    } catch (err) {
      handleError(err);
    } finally {
      setIsLoading(false);
    }
  }, [fetchSweets]);

  const deleteSweet = useCallback(async (id) => {
    setIsLoading(true);
    try {
      const res = await api.delete(`/sweets/${id}`);
      handleSuccess(res, 'Sweet deleted successfully');
      setSweets(prev => prev.filter(s => s.id !== id));
    } catch (err) {
      handleError(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const restockSweet = useCallback(async (id, quantity) => {
    setIsLoading(true);
    try {
      const res = await api.post(`/sweets/${id}/restock?quantity=${quantity}`);
      handleSuccess(res, 'Stock updated successfully!');
      await fetchSweets();
    } catch (err) {
      handleError(err);
    } finally {
      setIsLoading(false);
    }
  }, [fetchSweets]);

  const purchaseSweet = useCallback(async (id) => {
    try {
      const res = await api.post(`/sweets/${id}/purchase`);
      
      // Show backend message: "Purchase successful" or similar
      handleSuccess(res, 'Purchase successful!');
      
      // Optimistic update
      setSweets(prev => prev.map(s => 
        s.id === id ? { ...s, quantity: Math.max(0, s.quantity - 1) } : s
      ));
    } catch (err) {
      handleError(err);
    }
  }, []);

  return {
    sweets,
    isLoading,
    error,
    fetchSweets,
    addSweet,
    updateSweet,
    deleteSweet,
    restockSweet,
    purchaseSweet
  };
};