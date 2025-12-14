import { createContext, useState, useEffect, useContext } from 'react';
import { jwtDecode } from 'jwt-decode';
import api from '../services/api';
import toast from 'react-hot-toast';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = sessionStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        const role = decoded.role || decoded.roles || "USER";
        setUser({ username: decoded.sub, role });
      } catch (error) {
        console.error("[Auth] Token invalid:", error);
        sessionStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      const response = await api.post('/auth/login', { username, password });
      
      // Structure: { success: true, message: "...", data: { token: "..." } }
      const resData = response.data;
      
      // Extract token from nested 'data' object
      // We check resData.data.token (standard) or resData.token (fallback)
      const token = resData.data?.token || resData.token;
      
      if (!token) throw new Error("Token missing from login response");

      sessionStorage.setItem('token', token);
      
      const decoded = jwtDecode(token);
      const role = decoded.role || decoded.roles || "USER";
      setUser({ username: decoded.sub, role });
      
      // USE BACKEND MESSAGE
      toast.success(resData.message || 'Welcome back!');
      return true;
    } catch (error) {
      console.error("[Auth] Login Failed:", error);
      // Extract backend error message
      const errorMsg = error.response?.data?.message || 'Login failed. Please check credentials.';
      toast.error(errorMsg);
      return false;
    }
  };

  const register = async (username, password, adminKey) => {
    try {
      const response = await api.post('/auth/register', { username, password, adminKey });
      
      // Structure: { success: true, message: "User Registered...", data: null }
      const resData = response.data;

      // USE BACKEND MESSAGE
      toast.success(resData.message || 'Registration successful!');
      return true;
    } catch (error) {
      console.error("[Auth] Registration Failed:", error);
      const errorMsg = error.response?.data?.message || 'Registration failed.';
      toast.error(errorMsg);
      return false;
    }
  };

  const logout = () => {
    sessionStorage.removeItem('token');
    setUser(null);
    toast.success('Logged out successfully');
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isAdmin, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};