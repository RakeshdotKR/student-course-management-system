import { useState, useEffect } from "react";
import api from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";

// Students page - full CRUD UI for admins, read-only view for regular users
function Students() {
  const { role } = useAuth();
  const isAdmin = role === "ROLE_ADMIN";

  const [students, setStudents] = useState([]);
  const [form, setForm] = useState({ name: "", email: "", age: "" });
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchStudents();
  }, []);

  const fetchStudents = async () => {
    try {
      const response = await api.get("/api/students");
      setStudents(response.data.content);
    } catch (err) {
      setError("Failed to load students");
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    const payload = { ...form, age: Number(form.age) };
    try {
      if (editingId) {
        await api.put(`/api/students/${editingId}`, payload);
      } else {
        await api.post("/api/students", payload);
      }
      setForm({ name: "", email: "", age: "" });
      setEditingId(null);
      fetchStudents();
    } catch (err) {
      const apiError = err.response?.data;
      setError(apiError?.message || "Save failed");
    }
  };

  const handleEdit = (student) => {
    setForm({ name: student.name, email: student.email, age: student.age });
    setEditingId(student.id);
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/api/students/${id}`);
      fetchStudents();
    } catch (err) {
      setError("Delete failed");
    }
  };

  const handleCancelEdit = () => {
    setForm({ name: "", email: "", age: "" });
    setEditingId(null);
  };

  return (
    <div className="page-container">
      <h2>Students</h2>

      {/* Add/Edit form only visible to admins */}
      {isAdmin && (
        <form onSubmit={handleSubmit} className="form-row">
          <input type="text" name="name" placeholder="Name" value={form.name} onChange={handleChange} required />
          <input type="email" name="email" placeholder="Email" value={form.email} onChange={handleChange} required />
          <input type="number" name="age" placeholder="Age" value={form.age} onChange={handleChange} required />
          <button type="submit">{editingId ? "Update" : "Add"} Student</button>
          {editingId && <button type="button" onClick={handleCancelEdit}>Cancel</button>}
        </form>
      )}

      {!isAdmin && <p><em>Read-only view. Admin login required to add/edit/delete.</em></p>}

      {error && <p className="error">{error}</p>}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Age</th>
            {isAdmin && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {students.map((s) => (
            <tr key={s.id}>
              <td>{s.id}</td>
              <td>{s.name}</td>
              <td>{s.email}</td>
              <td>{s.age}</td>
              {isAdmin && (
                <td>
                  <button className="action-btn edit-btn" onClick={() => handleEdit(s)}>Edit</button>
                  <button className="action-btn delete-btn" onClick={() => handleDelete(s.id)}>Delete</button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Students;