import { Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';

// Components
import Navbar from './components/Navbar';
import { ProtectedRoute, AdminRoute } from './components/RouteGuards';

// Pages
import Home from './pages/Home';
import Catalog from './pages/Catalog';
import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/AdminDashboard';

// Custom 404
const NotFound = () => (
  <div className="flex min-h-screen items-center justify-center bg-background">
    <div className="text-center">
      <h1 className="mb-4 text-6xl font-display text-primary">404</h1>
      <p className="mb-6 text-xl text-muted-foreground">Oops! Page not found</p>
      <a href="/" className="text-primary hover:underline font-medium">Return to Home</a>
    </div>
  </div>
);

function App() {
  return (
    <AuthProvider>
      <div className="min-h-screen font-sans bg-background text-foreground selection:bg-primary/20">
        <Navbar />
        <Toaster position="bottom-right" toastOptions={{ duration: 3000, className: 'font-medium' }} />
        
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* PROTECTED: User must be logged in to see the Menu/Catalog */}
          <Route element={<ProtectedRoute />}>
             <Route path="/menu" element={<Catalog />} />
          </Route>

          {/* PROTECTED: Admin Only */}
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<AdminDashboard />} />
          </Route>

          {/* Catch All */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </div>
    </AuthProvider>
  );
}

export default App;