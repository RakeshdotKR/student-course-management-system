import { createContext, useState, useContext } from "react";

// Context object - holds auth state accessible from any component
const AuthContext = createContext();

// Provider component - wraps the whole app, manages token state
export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token"));

  // Called after successful login - saves token in state + localStorage
  const login = (newToken) => {
    localStorage.setItem("token", newToken);
    setToken(newToken);
  };

  // Clears token from state + localStorage
  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook - lets any component easily access { token, login, logout }
export function useAuth() {
  return useContext(AuthContext);
}
