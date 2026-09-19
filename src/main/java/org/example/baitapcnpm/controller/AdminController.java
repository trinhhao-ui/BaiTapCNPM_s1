package org.example.baitapcnpm.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.JobPosting;
import org.example.baitapcnpm.model.JobStatus;
import org.example.baitapcnpm.model.Role;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.service.JobPostingService;
import org.example.baitapcnpm.service.UserService;
import org.example.baitapcnpm.util.SessionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JobPostingService jobPostingService;
    private final UserService userService;

    private User requireAdmin(HttpSession session) {
        User user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("Chức năng này chỉ dành cho Quản trị viên!");
        }
        return user;
    }

    @GetMapping("/jobs")
    public String listJobs(HttpSession session, Model model) {
        requireAdmin(session);
        List<JobPosting> jobs = jobPostingService.getAllJobsForAdmin();
        model.addAttribute("jobs", jobs);
        return "admin/jobs";
    }

    @PostMapping("/jobs/{id}/status")
    public String updateJobStatus(@PathVariable Long id,
                                  @RequestParam JobStatus status,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            requireAdmin(session);
            jobPostingService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái tin tuyển dụng thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        requireAdmin(session);
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            User admin = requireAdmin(session);
            if (admin.getId().equals(id)) {
                throw new IllegalArgumentException("Không thể tự xóa tài khoản quản trị viên đang đăng nhập!");
            }
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa người dùng thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam Role role,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            requireAdmin(session);
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật vai trò người dùng thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }
}
