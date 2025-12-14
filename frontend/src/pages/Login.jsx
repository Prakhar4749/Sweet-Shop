import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { Candy, Lock, User, Loader2 } from 'lucide-react'; // Changed Mail to User

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [data, setData] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    const success = await login(data.username, data.password);
    setLoading(false);
    if (success) navigate('/menu');
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4 relative overflow-hidden">
      <div className="absolute top-[-10%] left-[-10%] w-96 h-96 bg-primary/10 rounded-full blur-3xl" />
      <div className="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-accent/10 rounded-full blur-3xl" />

      <div className="w-full max-w-md glass-card rounded-3xl p-8 sweet-glow animate-float">
        <div className="flex flex-col items-center mb-8">
          {/* Logo */}
      
        <div className="w-14 h-14 bg-gradient-to-br from-primary to-accent rounded-2xl flex items-center justify-center shadow-lg mb-3">
            <Candy className="w-7 h-7 text-pink-600" />
          </div>
        
      
          <h2 className="text-3xl font-display gradient-text mb-2">Welcome Back</h2>
          <p className="text-muted-foreground text-center">Sign in to access your sweet treats</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2">
            <label className="text-sm font-medium ml-1">Username</label>
            <div className="relative">
              <User className="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
              <input 
                type="text" 
                placeholder="Enter your username"
                required 
                className="w-full pl-10 pr-4 py-3 bg-white border border-input rounded-xl focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all"
                onChange={e => setData({...data, username: e.target.value})} 
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium ml-1">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-3.5 h-5 w-5 text-muted-foreground" />
              <input 
                type="password" 
                placeholder="••••••••"
                required 
                className="w-full pl-10 pr-4 py-3 bg-white border border-input rounded-xl focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all"
                onChange={e => setData({...data, password: e.target.value})} 
              />
            </div>
          </div>

          <button 
            disabled={loading}
            className="w-full bg-primary hover:bg-primary/90 text-white font-semibold py-3.5 rounded-xl shadow-lg shadow-primary/25 transition-all transform active:scale-95 flex items-center justify-center"
          >
            {loading ? <Loader2 className="animate-spin mr-2" /> : 'Sign In'}
          </button>
        </form>

        <div className="mt-8 text-center">
          <p className="text-muted-foreground text-sm">
            Don't have an account?{' '}
            <Link to="/register" className="text-primary font-semibold hover:text-primary/80 transition-colors">
              Create one
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;