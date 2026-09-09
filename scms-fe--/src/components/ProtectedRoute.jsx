import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// Protects a route - only renders children if a token exists, else redirects to /login
function ProtectedRoute({ children }) {
  const { token } = useAuth();
  return token ? children : <Navigate to="/login" />;
}

export default ProtectedRoute;
