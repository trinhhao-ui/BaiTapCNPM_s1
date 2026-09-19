package org.example.baitapcnpm.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.CandidateCv;
import org.example.baitapcnpm.model.JobApplication;
import org.example.baitapcnpm.model.Role;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.service.CandidateCvService;
import org.example.baitapcnpm.service.JobApplicationService;
import org.example.baitapcnpm.util.SessionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateCvService candidateCvService;
    private final JobApplicationService jobApplicationService;

    private User requireCandidate(HttpSession session) {
        User user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRole() != Role.ROLE_CANDIDATE) {
            throw new SecurityException("Vui lòng đăng nhập với vai trò Ứng viên để thực hiện chức năng này!");
        }
        return user;
    }

    /**
     * Quản lý CV của Ứng viên
     */
    @GetMapping("/candidate/cv")
    public String manageCV(@RequestParam(required = false) Long cvId, HttpSession session, Model model) {
        User candidate = requireCandidate(session);

        List<CandidateCv> cvList = candidateCvService.getCVsByCandidate(candidate.getId());
        CandidateCv currentCv;

        if (cvId != null) {
            currentCv = candidateCvService.findById(cvId).orElse(new CandidateCv());
        } else if (!cvList.isEmpty()) {
            currentCv = cvList.get(0);
        } else {
            currentCv = new CandidateCv();
            currentCv.setTitle("CV " + candidate.getFullName());
        }

        model.addAttribute("cvList", cvList);
        model.addAttribute("currentCv", currentCv);
        return "candidate/cv";
    }

    @PostMapping("/candidate/cv/save")
    public String saveCV(@RequestParam(required = false) Long id,
                         @RequestParam String title,
                         @RequestParam String summary,
                         @RequestParam String skills,
                         @RequestParam String education,
                         @RequestParam String experience,
                         @RequestParam(required = false) String portfolioUrl,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            User candidate = requireCandidate(session);
            candidateCvService.saveOrUpdateCV(candidate, id, title, summary, skills, education, experience, portfolioUrl);
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu thông tin CV thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/candidate/cv";
    }

    /**
     * Nghiệp vụ cốt lõi: Ứng viên nộp đơn ứng tuyển
     */
    @PostMapping("/jobs/{id}/apply")
    public String applyForJob(@PathVariable("id") Long jobId,
                              @RequestParam Long cvId,
                              @RequestParam(required = false) String coverLetter,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            User candidate = requireCandidate(session);
            jobApplicationService.applyForJob(candidate, jobId, cvId, coverLetter);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Chúc mừng! Bạn đã nộp đơn ứng tuyển thành công. Vui lòng theo dõi trạng thái tại danh sách đơn ứng tuyển bên dưới.");
            return "redirect:/candidate/applications";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/jobs/" + jobId;
        }
    }

    /**
     * Theo dõi trạng thái ứng tuyển của Ứng viên
     */
    @GetMapping("/candidate/applications")
    public String myApplications(HttpSession session, Model model) {
        User candidate = requireCandidate(session);
        List<JobApplication> applications = jobApplicationService.getApplicationsByCandidate(candidate.getId());
        model.addAttribute("applications", applications);
        return "candidate/applications";
    }
}
