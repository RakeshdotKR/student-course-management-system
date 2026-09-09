import axios from "axios";

// Central Axios instance - all API calls in the app go through this
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // reads from .env file
});

// Interceptor - runs before every request is sent.
// Attaches the JWT token (if present) to the Authorization header automatically.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
