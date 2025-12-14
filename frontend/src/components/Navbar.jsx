import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, Candy } from 'lucide-react';

const Navbar = () => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Helper for active link classes
  const getLinkClass = ({ isActive }) => 
    `px-4 py-2 rounded-full text-sm font-medium transition-all duration-200 ${
      isActive 
        ? 'bg-primary/10 text-primary font-bold shadow-sm' 
        : 'text-muted-foreground hover:text-primary hover:bg-secondary/50'
    }`;

  return (
    <nav className="bg-white/80 backdrop-blur-md border-b border-border sticky top-0 z-40 h-16">
  <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-full">
    <div className="flex justify-between items-center h-full">
      
      {/* Logo */}
      <Link to="/" className="flex items-center space-x-2 group">
        <div className="p-2 bg-gradient-to-br from-primary to-accent rounded-lg shadow-md group-hover:scale-105 transition-transform">
          <Candy className="w-6 h-6 text-pink-600" />
        </div>
        <span className="text-2xl font-display gradient-text tracking-wide">
          SweetHaven
        </span>
      </Link>

          {/* Links */}
          <div className="flex items-center space-x-4">
            {user ? (
              <>
                <NavLink to="/menu" className={getLinkClass}>
                  Menu
                </NavLink>
                
                {isAdmin && (
                  <NavLink to="/admin" className={getLinkClass}>
                    Dashboard
                  </NavLink>
                )}
                
                <div className="h-6 w-px bg-border mx-2" />
                
                <div className="flex items-center gap-3">
                  <span className="text-sm font-semibold text-foreground hidden sm:block">
                    {user.username}
                  </span>
                  <button 
                    onClick={handleLogout} 
                    className="p-2 text-muted-foreground hover:text-destructive hover:bg-red-50 rounded-full transition-all"
                    title="Logout"
                  >
                    <LogOut className="w-5 h-5" />
                  </button>
                </div>
              </>
            ) : (
              <div className="flex items-center gap-3">
                <NavLink to="/login" className="text-sm font-medium text-muted-foreground hover:text-primary transition">
                  Login
                </NavLink>
                <Link to="/register" className="bg-primary text-white px-5 py-2 rounded-full text-sm font-bold shadow-lg shadow-primary/25 hover:bg-primary/90 hover:-translate-y-0.5 transition-all">
                  Get Started
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;