import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute = () => {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <div className="min-h-screen flex items-center justify-center text-primary">Loading...</div>;

  // If not logged in, redirect to login, but save where they were trying to go
  return user ? <Outlet /> : <Navigate to="/login" state={{ from: location }} replace />;
};

export const AdminRoute = () => {
  const { user, isAdmin, loading } = useAuth();
  
  if (loading) return null;

  return (user && isAdmin) ? <Outlet /> : <Navigate to="/menu" replace />;
};