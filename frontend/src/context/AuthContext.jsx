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
    console.log("[Auth] Checking session token:", token ? "Found" : "Not Found");
    
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
    console.log("[Auth] Attempting login for:", username);
    try {
      const response = await api.post('/auth/login', { username, password });
      
      // DEBUG: Check structure
      console.log("[Auth] Login Raw Response:", response.data);

      // Handle response structure: { data: { token: "..." }, ... } OR { token: "..." }
      const token = response.data.data?.token || response.data.token;
      
      if (!token) throw new Error("Token not found in response");

      sessionStorage.setItem('token', token);
      
      const decoded = jwtDecode(token);
      const role = decoded.role || decoded.roles || "USER";
      
      setUser({ username: decoded.sub, role });
      toast.success('Welcome back!');
      return true;
    } catch (error) {
      console.error("[Auth] Login Failed:", error);
      toast.error('Invalid credentials');
      return false;
    }
  };

  const register = async (username, password, adminKey) => {
    console.log("[Auth] Registering user:", username);
    try {
      await api.post('/auth/register', { username, password, adminKey });
      toast.success('Registration successful! Please login.');
      return true;
    } catch (error) {
      console.error("[Auth] Registration Failed:", error);
      toast.error('Registration failed. Username might be taken.');
      return false;
    }
  };

  const logout = () => {
    console.log("[Auth] Logging out");
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