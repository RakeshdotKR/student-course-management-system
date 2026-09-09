import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// Navbar - shown on every page, provides navigation and logout
function Navbar() {
  const { token, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <span className="brand">SCMS</span>
      {token && (
        <div className="nav-links">
          <Link to="/students">Students</Link>
          <Link to="/courses">Courses</Link>
          <button onClick={handleLogout}>Logout</button>
        </div>
      )}
    </nav>
  );
}

export default Navbar;
