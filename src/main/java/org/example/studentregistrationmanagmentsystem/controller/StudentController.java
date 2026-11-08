package org.example.studentregistrationmanagmentsystem.controller;

import org.example.studentregistrationmanagmentsystem.model.Student;
import org.example.studentregistrationmanagmentsystem.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("student") Student student,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            studentService.registerStudent(student);
            model.addAttribute("success", "Student registered successfully!");
            model.addAttribute("student", new Student());
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }

    @GetMapping("/all")
    public String showAll(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    // Bonus: reuses students.html
    @GetMapping("/{id}")
    public String showOne(@PathVariable int id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            model.addAttribute("students", List.of());
            model.addAttribute("message", "No student found with ID " + id);
        } else {
            model.addAttribute("students", List.of(student));
        }
        return "students";
    }
}