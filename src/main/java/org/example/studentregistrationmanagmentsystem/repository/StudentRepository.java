package org.example.studentregistrationmanagmentsystem.repository;



import org.example.studentregistrationmanagmentsystem.config.DatabaseConfig;
import org.example.studentregistrationmanagmentsystem.model.Student;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepository {

    private final DatabaseConfig databaseConfig;

    public StudentRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void save(Student student) {
        String sql = "INSERT INTO students (full_name, age, email) VALUES (?, ?, ?)";
        String fullName = student.getFirstName() + " " + student.getLastName();

        try (Connection con = databaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalStateException("This email is already registered.");
            }
            if ("23514".equals(e.getSQLState())) {
                throw new IllegalStateException("Age must be greater than 18.");
            }
            throw new IllegalStateException("Could not save student: " + e.getMessage());
        }
    }

    public List<Student> findAll() {
        String sql = "SELECT id, full_name, age, email FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();

        try (Connection con = databaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load students: " + e.getMessage());
        }
        return students;
    }

    public Student findById(int id) {
        String sql = "SELECT id, full_name, age, email FROM students WHERE id = ?";

        try (Connection con = databaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load student: " + e.getMessage());
        }
        return null;
    }


    private Student mapRow(ResultSet rs) throws SQLException {
        String fullName = rs.getString("full_name");
        String[] parts = fullName.split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";
        return new Student(rs.getInt("id"), firstName, lastName,
                rs.getString("email"), rs.getInt("age"));
    }
}