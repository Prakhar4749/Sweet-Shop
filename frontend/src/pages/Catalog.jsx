import { useEffect, useState } from 'react';
import { useSweets } from '../hooks/useSweets';
import { Search, ShoppingBag, AlertCircle, RefreshCw } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';

const Catalog = () => {
  const { sweets, isLoading, error, fetchSweets, purchaseSweet } = useSweets();
  
  // Filters State
  const [searchParams] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState('');
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });

  // Debounced Search Logic
  useEffect(() => {
    const timer = setTimeout(() => {
      // Pass 'search' which hook maps to 'query' parameter for backend
      fetchSweets({ 
        search: searchQuery, 
        minPrice: priceRange.min, 
        maxPrice: priceRange.max 
      });
    }, 500); // 500ms delay to stop spamming API
    return () => clearTimeout(timer);
  }, [searchQuery, priceRange.min, priceRange.max, fetchSweets]);

  return (
    <div className="min-h-screen bg-background pb-20">
      {/* Header */}
      <div className="pt-12 pb-8 px-4 text-center">
         <h1 className="text-4xl md:text-5xl font-display text-primary mb-3">Our Menu</h1>
         <p className="text-muted-foreground">Find your favorite treat by name or price.</p>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Search & Filter Bar */}
        <div className="bg-white rounded-2xl shadow-sm border border-border p-6 mb-8">
           <div className="flex flex-col md:flex-row gap-4 items-center">
             
             {/* Search Input */}
             <div className="relative flex-grow w-full md:w-auto">
               <Search className="absolute left-4 top-3.5 text-muted-foreground h-5 w-5" />
               <input 
                 type="text" 
                 placeholder="Search by name or category..." 
                 className="w-full pl-11 pr-4 py-3 bg-secondary/30 border-transparent rounded-xl focus:bg-white focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all"
                 value={searchQuery}
                 onChange={(e) => setSearchQuery(e.target.value)}
               />
             </div>

             {/* Price Filters */}
             <div className="flex gap-2 w-full md:w-auto">
               <div className="relative w-1/2 md:w-32">
                 <span className="absolute left-3 top-3 text-xs text-muted-foreground">$ Min</span>
                 <input 
                   type="number" 
                   min="0"
                   className="w-full pl-12 pr-3 py-3 bg-secondary/30 border-transparent rounded-xl focus:bg-white focus:border-primary outline-none"
                   value={priceRange.min}
                   onChange={e => setPriceRange({...priceRange, min: e.target.value})}
                 />
               </div>
               <div className="relative w-1/2 md:w-32">
                 <span className="absolute left-3 top-3 text-xs text-muted-foreground">$ Max</span>
                 <input 
                   type="number" 
                   min="0"
                   className="w-full pl-12 pr-3 py-3 bg-secondary/30 border-transparent rounded-xl focus:bg-white focus:border-primary outline-none"
                   value={priceRange.max}
                   onChange={e => setPriceRange({...priceRange, max: e.target.value})}
                 />
               </div>
             </div>
           </div>
        </div>

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-xl p-4 mb-8 flex items-center gap-3 text-red-700">
            <AlertCircle className="w-5 h-5" />
            <p className="flex-1 text-sm">{error}</p>
            <button onClick={() => fetchSweets()} className="text-xs underline">Retry</button>
          </div>
        )}

        {/* Loading State */}
        {isLoading && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {[1,2,3,4].map(n => <div key={n} className="h-80 bg-gray-100 rounded-2xl animate-pulse" />)}
          </div>
        )}

        {/* Sweets Grid */}
        {!isLoading && !error && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {sweets.map((sweet) => (
              <div key={sweet.id} className="group bg-white rounded-2xl overflow-hidden border border-border hover:border-primary/50 transition-all duration-300 sweet-glow hover:sweet-glow-hover flex flex-col h-full">
                
                {/* Visual Placeholder */}
                <div className="h-48 bg-gradient-to-br from-orange-50 to-rose-50 relative overflow-hidden flex items-center justify-center">
                  <span className="text-6xl group-hover:scale-110 transition-transform duration-500 cursor-default">
                     🍬
                  </span>
                  <div className="absolute top-3 right-3 bg-white/90 px-3 py-1 rounded-full text-xs font-bold text-primary shadow-sm">
                    {sweet.category}
                  </div>
                </div>

                <div className="p-5 flex flex-col flex-1">
                  <div className="flex justify-between items-start mb-2">
                    <h3 className="font-display text-xl text-foreground line-clamp-1">{sweet.name}</h3>
                    <span className="font-bold text-lg text-primary">${sweet.price}</span>
                  </div>
                  
                  <div className="flex items-center gap-2 text-sm text-muted-foreground mb-4">
                    <span className={`w-2 h-2 rounded-full ${sweet.quantity > 0 ? 'bg-green-500' : 'bg-red-500'}`}></span>
                    {sweet.quantity > 0 ? `${sweet.quantity} in stock` : 'Out of stock'}
                  </div>

                  {/* BUY BUTTON FIX: Explicit Text Color and Click to Buy Rename */}
                  <button
                    onClick={() => purchaseSweet(sweet.id)}
                    disabled={sweet.quantity <= 0}
                    className={`mt-auto w-full py-3 rounded-xl font-bold transition-all flex items-center justify-center gap-2 shadow-md
                      ${sweet.quantity > 0 
                        ? 'bg-primary text-white hover:bg-primary/90 hover:shadow-lg active:scale-95' 
                        : 'bg-muted text-muted-foreground cursor-not-allowed opacity-70'}`}
                  >
                    <ShoppingBag size={18} />
                    {sweet.quantity > 0 ? 'Click to Buy' : 'Sold Out'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {!isLoading && !error && sweets.length === 0 && (
          <div className="text-center py-20 text-muted-foreground">
            No sweets found matching your search.
          </div>
        )}
      </div>
    </div>
  );
};

export default Catalog;