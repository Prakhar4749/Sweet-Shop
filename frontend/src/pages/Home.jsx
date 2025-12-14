import { Link } from 'react-router-dom';
import { Candy, ArrowRight, Star } from 'lucide-react';

const Home = () => {
  return (
    <div className="min-h-[calc(100vh-4rem)] flex flex-col justify-center relative overflow-hidden">
      {/* Background Blobs */}
      <div className="absolute top-20 left-10 w-72 h-72 bg-primary/10 rounded-full blur-3xl animate-float" />
      <div className="absolute bottom-20 right-10 w-96 h-96 bg-accent/10 rounded-full blur-3xl" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 text-center">
        
        <div className="inline-flex items-center gap-2 bg-white border border-border px-4 py-1.5 rounded-full text-sm font-medium text-muted-foreground mb-8 shadow-sm">
          <Star className="w-4 h-4 text-accent" fill="currentColor" />
          <span>Best Sweet Shop 2025</span>
        </div>

        <h1 className="text-6xl md:text-8xl font-display mb-6 leading-tight">
          <span className="text-foreground">Taste the</span> <br />
          <span className="gradient-text">Magic of Sweets</span>
        </h1>

        <p className="text-xl text-muted-foreground mb-10 max-w-2xl mx-auto leading-relaxed">
          Handcrafted daily using traditional recipes and the finest ingredients. 
          Experience the joy of pure sugar bliss.
        </p>

        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <Link 
            to="/register" 
            className="group bg-primary text-white px-8 py-4 rounded-full text-lg font-bold shadow-xl shadow-primary/30 hover:bg-primary/90 hover:scale-105 transition-all flex items-center gap-2"
          >
            Start Your Journey
            <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
          </Link>
          <Link 
            to="/login" 
            className="px-8 py-4 rounded-full text-lg font-medium text-foreground hover:bg-secondary/50 transition-all border border-transparent hover:border-border"
          >
            Sign In
          </Link>
        </div>

        {/* Feature Icons */}
        <div className="mt-20 grid grid-cols-1 md:grid-cols-3 gap-8 max-w-4xl mx-auto">
          {[
            { title: "Freshly Made", desc: "Crafted every morning" },
            { title: "Premium Quality", desc: "100% Organic ingredients" },
            { title: "Fast Delivery", desc: "Sweetness at your door" }
          ].map((item, i) => (
            <div key={i} className="bg-white/50 backdrop-blur-sm border border-white/50 p-6 rounded-2xl shadow-sm">
              <h3 className="font-bold text-foreground text-lg mb-1">{item.title}</h3>
              <p className="text-sm text-muted-foreground">{item.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Home;