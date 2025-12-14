import { useState, useCallback } from 'react';
import api from '../services/api';
import toast from 'react-hot-toast';

export const useSweets = () => {
  const [sweets, setSweets] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // Helper to handle API errors consistently
 const handleError = (err) => {
    const msg = err.response?.data?.message || err.message || "An unexpected error occurred";
    console.error("[useSweets] Error:", err);
    toast.error(msg);
    return msg;
  };

  const fetchSweets = useCallback(async (filters = {}) => {
    setIsLoading(true);
    setError(null);
    try {
      const params = {};
      if (filters.search) params.query = filters.search;
      if (filters.minPrice) params.minPrice = filters.minPrice;
      if (filters.maxPrice) params.maxPrice = filters.maxPrice;

      console.log("[useSweets] Fetching with params:", params);
      
      const endpoint = Object.keys(params).length > 0 ? '/sweets/search' : '/sweets';
      const res = await api.get(endpoint, { params });
      
      // DEBUG: Log the exact structure to help debug
      console.log("[useSweets] Raw API Response:", res.data);

      // Handle Structure: response.data.data (Array) OR response.data (Array)
      // Your requirement: response = { data: { data: [...] }, message, status }
      // So we look inside res.data.data
      let data = [];
      if (res.data && res.data.data) {
        if (Array.isArray(res.data.data)) {
           data = res.data.data;
        } else if (res.data.data.data && Array.isArray(res.data.data.data)) {
           // Handle potential double nesting edge case
           data = res.data.data.data;
        }
      } else if (Array.isArray(res.data)) {
        data = res.data;
      }

      console.log("[useSweets] Parsed Data:", data);
      setSweets(data);
    } catch (err) {
      setError(handleError(err));
    } finally {
      setIsLoading(false);
    }
  }, []);

  const addSweet = useCallback(async (data) => {
    setIsLoading(true);
    try {
      await api.post('/sweets', data);
      toast.success('Sweet created successfully!');
      await fetchSweets(); // Refresh list
    } catch (err) {
      handleError(err);
    } finally {
      setIsLoading(false);
    }
  }, [fetchSweets]);

  const updateSweet = useCallback(async (id, data) => {
    setIsLoading(true);
    try {
      await api.put(`/sweets/${id}`, data);
      toast.success('Sweet updated successfully!');
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
      await api.delete(`/sweets/${id}`);
      toast.success('Sweet deleted successfully');
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
      // Note: Backend expects query param ?quantity=X
      await api.post(`/sweets/${id}/restock?quantity=${quantity}`);
      toast.success('Stock updated!');
      await fetchSweets();
    } catch (err) {
      handleError(err);
    } finally {
      setIsLoading(false);
    }
  }, [fetchSweets]);

  const purchaseSweet = useCallback(async (id) => {
    try {
      console.log(`[useSweets] Purchasing ID: ${id}`);
      await api.post(`/sweets/${id}/purchase`);
      toast.success('Delicious choice! Order placed.');
      
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