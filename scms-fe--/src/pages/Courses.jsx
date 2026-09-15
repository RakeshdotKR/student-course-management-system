import { useState, useEffect } from "react";
import api from "../api/axiosConfig";
import { useAuth } from "../context/AuthContext";

// Courses page - full CRUD UI for admins, read-only view for regular users
function Courses() {
  const { role } = useAuth();
  const isAdmin = role === "ROLE_ADMIN";

  const [courses, setCourses] = useState([]);
  const [students, setStudents] = useState([]); // used to populate the student dropdown
  const [form, setForm] = useState({
    courseName: "",
    courseCode: "",
    studentId: "",
  });
  const [editingId, setEditingId] = useState(null);
  const [filterStudentId, setFilterStudentId] = useState("");
  const [error, setError] = useState("");

  // Load courses and students once when the page loads
  useEffect(() => {
    fetchCourses();
    fetchStudents();
  }, []);

  const fetchCourses = async () => {
    try {
      const response = await api.get("/api/courses");

      // If response.data.content exists, use it.
      // If it doesn't, try response.data. If both fail, fall back to an empty array [].
      const dataPayload = response.data?.content || response.data || [];
      setCourses(dataPayload);
    } catch (err) {
      setError("Failed to load courses");
    }
  };

  const fetchStudents = async () => {
    try {
      const response = await api.get("/api/students");

      // Handles cases where students endpoint returns a wrapped object or page wrapper
      const studentData = response.data?.content || response.data || [];
      setStudents(Array.isArray(studentData) ? studentData : []);
    } catch (err) {
      setError("Failed to load students for dropdown");
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // Handles both Create (POST) and Update (PUT) based on editingId
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    const payload = { ...form, studentId: Number(form.studentId) };
    try {
      if (editingId) {
        await api.put(`/api/courses/${editingId}`, payload);
      } else {
        await api.post("/api/courses", payload);
      }
      setForm({ courseName: "", courseCode: "", studentId: "" });
      setEditingId(null);
      fetchCourses();
    } catch (err) {
      const apiError = err.response?.data;
      setError(apiError?.message || "Save failed");
    }
  };

  const handleEdit = (course) => {
    setForm({
      courseName: course.courseName,
      courseCode: course.courseCode,
      studentId: course.studentId,
    });
    setEditingId(course.id);
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/api/courses/${id}`);
      fetchCourses();
    } catch (err) {
      setError("Delete failed");
    }
  };

  const handleCancelEdit = () => {
    setForm({ courseName: "", courseCode: "", studentId: "" });
    setEditingId(null);
  };

  // Filter by student - calls /api/courses/student/{id}, or reloads all if cleared
  const handleFilterChange = async (e) => {
    const id = e.target.value;
    setFilterStudentId(id);
    try {
      if (id) {
        const response = await api.get(`/api/courses/student/${id}`);
        setCourses(response.data);
      } else {
        fetchCourses();
      }
    } catch (err) {
      setError("Failed to filter courses");
    }
  };

  return (
    <div className="page-container">
      <h2>Courses</h2>

      {/* Add/Edit form only visible to admins */}
      {isAdmin && (
        <form onSubmit={handleSubmit} className="form-row">
          <input
            type="text"
            name="courseName"
            placeholder="Course Name"
            value={form.courseName}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="courseCode"
            placeholder="Course Code"
            value={form.courseCode}
            onChange={handleChange}
            required
          />
          <select
            name="studentId"
            value={form.studentId}
            onChange={handleChange}
            required
          >
            <option value="">Select Student</option>
            {Array.isArray(students) &&
              students.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name} (id: {s.id})
                </option>
              ))}
          </select>

          <button type="submit">{editingId ? "Update" : "Add"} Course</button>
          {editingId && (
            <button type="button" onClick={handleCancelEdit}>
              Cancel
            </button>
          )}
        </form>
      )}

      {!isAdmin && <p><em>Read-only view. Admin login required to add/edit/delete.</em></p>}

      <div className="form-row">
        <select value={filterStudentId} onChange={handleFilterChange}>
          <option value="">Show All Courses</option>
          {Array.isArray(students) &&
            students.map((s) => (
              <option key={s.id} value={s.id}>
                Filter: {s.name} (id: {s.id})
              </option>
            ))}
        </select>
      </div>

      {error && <p className="error">{error}</p>}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Course Name</th>
            <th>Course Code</th>
            <th>Student ID</th>
            {isAdmin && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {Array.isArray(courses) && courses.length > 0 ? (
            courses.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>{c.courseName}</td>
                <td>{c.courseCode}</td>
                <td>{c.studentId}</td>
                {isAdmin && (
                  <td>
                    <button
                      className="action-btn edit-btn"
                      onClick={() => handleEdit(c)}
                    >
                      Edit
                    </button>
                    <button
                      className="action-btn delete-btn"
                      onClick={() => handleDelete(c.id)}
                    >
                      Delete
                    </button>
                  </td>
                )}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={isAdmin ? 5 : 4} style={{ textAlign: "center" }}>
                No courses available.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default Courses;