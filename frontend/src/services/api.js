import axios from 'axios';

// Use the environment variable, fallback to localhost if missing
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api';

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach Token & Log
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // DEBUG: Log the outgoing request
    console.log(`[API Request] ${config.method.toUpperCase()} ${config.url}`, config.params || config.data);
    
    return config;
  },
  (error) => {
    console.error('[API Request Error]', error);
    return Promise.reject(error);
  }
);

// Response Interceptor: Log Result & Handle Errors
api.interceptors.response.use(
  (response) => {
    // DEBUG: Log the incoming response
    console.log(`[API Response] ${response.config.url}:`, response.data);
    return response;
  },
  (error) => {
    console.error('[API Response Error]', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;