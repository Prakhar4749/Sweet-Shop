import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { Candy, User, Lock, Key, Loader2 } from 'lucide-react'; // Changed Mail to User

const Register = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [data, setData] = useState({ username: '', password: '', confirmPassword: '', adminKey: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (data.password !== data.confirmPassword) {
      return; // Add toast error here if you want
    }
    setLoading(true);
    // Passing username as the first argument now
    const success = await register(data.username, data.password, data.adminKey || null);
    setLoading(false);
    if (success) navigate('/login');
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4 relative overflow-hidden">
      <div className="absolute top-[-10%] right-[-10%] w-96 h-96 bg-secondary/20 rounded-full blur-3xl" />
      <div className="absolute bottom-[-10%] left-[-10%] w-96 h-96 bg-primary/10 rounded-full blur-3xl" />

      <div className="w-full max-w-md glass-card rounded-3xl p-8 sweet-glow">
        <div className="flex flex-col items-center mb-6">
          <div className="w-14 h-14 bg-gradient-to-br from-primary to-accent rounded-2xl flex items-center justify-center shadow-lg mb-3">
            <Candy className="w-7 h-7 text-pink-600" />
          </div>

          
          <h2 className="text-3xl font-display gradient-text mb-1">Join Sweet Haven</h2>
          <p className="text-muted-foreground text-center text-sm">Create your account and start your sweet journey</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          
          {/* USERNAME FIELD (Replaced Full Name & Email) */}
          <div className="space-y-1">
            <label className="text-xs font-medium ml-1 text-muted-foreground">Username</label>
            <div className="relative">
              <User className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <input 
                type="text" 
                required
                placeholder="Choose a username"
                className="w-full pl-9 pr-4 py-2.5 bg-white border border-input rounded-xl focus:ring-2 focus:ring-secondary/20 focus:border-secondary outline-none text-sm transition-all"
                onChange={e => setData({...data, username: e.target.value})} 
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium ml-1 text-muted-foreground">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <input 
                type="password" 
                required
                placeholder="••••••••"
                className="w-full pl-9 pr-4 py-2.5 bg-white border border-input rounded-xl focus:ring-2 focus:ring-secondary/20 focus:border-secondary outline-none text-sm transition-all"
                onChange={e => setData({...data, password: e.target.value})} 
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-medium ml-1 text-muted-foreground">Confirm Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <input 
                type="password" 
                required
                placeholder="••••••••"
                className="w-full pl-9 pr-4 py-2.5 bg-white border border-input rounded-xl focus:ring-2 focus:ring-secondary/20 focus:border-secondary outline-none text-sm transition-all"
                onChange={e => setData({...data, confirmPassword: e.target.value})} 
              />
            </div>
          </div>

          {/* Admin Key Section */}
          <div className="pt-2">
             <label className="text-xs font-medium ml-1 text-muted-foreground flex justify-between">
                <span>Admin Secret Key <span className="text-gray-400 font-normal">(Optional)</span></span>
             </label>
             <div className="relative mt-1">
                <Key className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                <input 
                  type="password" 
                  placeholder="Enter admin key if you have one"
                  className="w-full pl-9 pr-4 py-2.5 bg-amber-50/50 border border-amber-200 rounded-xl focus:ring-2 focus:ring-amber-400/20 focus:border-amber-400 outline-none text-sm transition-all"
                  onChange={e => setData({...data, adminKey: e.target.value})} 
                />
             </div>
          </div>

          <button 
            disabled={loading}
            className="w-full bg-primary hover:bg-primary/90 text-white font-semibold py-3 rounded-xl shadow-lg shadow-primary/25 transition-all transform active:scale-95 flex items-center justify-center mt-4"
          >
            {loading ? <Loader2 className="animate-spin mr-2" /> : 'Create Account'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-muted-foreground text-sm">
            Already have an account?{' '}
            <Link to="/login" className="text-primary font-semibold hover:text-primary/80 transition-colors">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;