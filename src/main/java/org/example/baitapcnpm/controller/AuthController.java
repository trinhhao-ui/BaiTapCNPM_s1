package org.example.baitapcnpm.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.Role;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.service.UserService;
import org.example.baitapcnpm.util.SessionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (SessionHelper.getCurrentUser(session) != null) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        Optional<User> authUser = userService.authenticate(username, password);
        if (authUser.isPresent()) {
            SessionHelper.setCurrentUser(session, authUser.get());
            redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công! Chào mừng " + authUser.get().getFullName());
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        if (SessionHelper.getCurrentUser(session) != null) {
            return "redirect:/";
        }
        model.addAttribute("roles", Role.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam(required = false) String phone,
                             @RequestParam Role role,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            User newUser = userService.register(username, password, fullName, email, phone, role);
            SessionHelper.setCurrentUser(session, newUser);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công!");
            return "redirect:/";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        SessionHelper.clear(session);
        redirectAttributes.addFlashAttribute("successMessage", "Đã đăng xuất khỏi hệ thống.");
        return "redirect:/";
    }

    /**
     * Tiện ích chuyển nhanh tài khoản phục vụ chấm bài / kiểm thử
     */
    @GetMapping("/demo-login")
    public String demoLogin(@RequestParam(required = false) String username,
                            @RequestParam(required = false) String role,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User user = null;
        if (username != null) {
            user = userService.findByUsername(username).orElse(null);
        } else if ("admin".equalsIgnoreCase(role)) {
            user = userService.findByUsername("admin").orElse(null);
        } else if ("recruiter".equalsIgnoreCase(role)) {
            user = userService.findByUsername("fpt_hr").orElse(null);
        } else if ("candidate".equalsIgnoreCase(role)) {
            user = userService.findByUsername("candidate_nam").orElse(null);
        }

        if (user != null) {
            SessionHelper.setCurrentUser(session, user);
            redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển đổi sang tài khoản: " + user.getFullName() + " (" + user.getRole().getDisplayName() + ")");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài khoản demo phù hợp!");
        }

        return "redirect:/";
    }
}
