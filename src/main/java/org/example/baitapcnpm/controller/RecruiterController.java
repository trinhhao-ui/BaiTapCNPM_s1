package org.example.baitapcnpm.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.*;
import org.example.baitapcnpm.service.CandidateCvService;
import org.example.baitapcnpm.service.JobApplicationService;
import org.example.baitapcnpm.service.JobPostingService;
import org.example.baitapcnpm.util.SessionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final JobPostingService jobPostingService;
    private final JobApplicationService jobApplicationService;
    private final CandidateCvService candidateCvService;

    private User requireRecruiter(HttpSession session) {
        User user = SessionHelper.getCurrentUser(session);
        if (user == null || user.getRole() != Role.ROLE_RECRUITER) {
            throw new SecurityException("Chức năng này chỉ dành cho Nhà tuyển dụng!");
        }
        return user;
    }

    @GetMapping("/jobs")
    public String myJobs(HttpSession session, Model model) {
        User recruiter = requireRecruiter(session);
        List<JobPosting> jobs = jobPostingService.getJobsByRecruiter(recruiter.getId());
        model.addAttribute("jobs", jobs);
        return "recruiter/jobs";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(HttpSession session, Model model) {
        requireRecruiter(session);
        model.addAttribute("job", new JobPosting());
        model.addAttribute("isNew", true);
        return "recruiter/job-form";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable Long id, HttpSession session, Model model) {
        User recruiter = requireRecruiter(session);
        JobPosting job = jobPostingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin ID: " + id));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new SecurityException("Bạn không có quyền sửa tin này!");
        }

        model.addAttribute("job", job);
        model.addAttribute("isNew", false);
        return "recruiter/job-form";
    }

    @PostMapping("/jobs/save")
    public String saveJob(@RequestParam(required = false) Long id,
                          @RequestParam String title,
                          @RequestParam String companyName,
                          @RequestParam String location,
                          @RequestParam String salaryRange,
                          @RequestParam String employmentType,
                          @RequestParam String description,
                          @RequestParam String requirements,
                          @RequestParam(required = false) String benefits,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            User recruiter = requireRecruiter(session);
            if (id == null) {
                jobPostingService.createJob(recruiter, title, companyName, location, salaryRange, employmentType, description, requirements, benefits);
                redirectAttributes.addFlashAttribute("successMessage", "Đăng tin tuyển dụng thành công! Tin đang ở trạng thái chờ Quản trị viên duyệt.");
            } else {
                jobPostingService.updateJob(id, recruiter.getId(), title, companyName, location, salaryRange, employmentType, description, requirements, benefits);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tin tuyển dụng thành công!");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/recruiter/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User recruiter = requireRecruiter(session);
            jobPostingService.deleteJob(id, recruiter.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tin tuyển dụng thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/applications")
    public String allApplications(HttpSession session, Model model) {
        User recruiter = requireRecruiter(session);
        List<JobApplication> applications = jobApplicationService.getApplicationsByRecruiter(recruiter.getId());
        model.addAttribute("applications", applications);
        model.addAttribute("allStatuses", ApplicationStatus.values());
        return "recruiter/applicants";
    }

    @GetMapping("/jobs/{id}/applications")
    public String jobApplications(@PathVariable Long id, HttpSession session, Model model) {
        requireRecruiter(session);
        List<JobApplication> applications = jobApplicationService.getApplicationsByJob(id);
        JobPosting job = jobPostingService.findById(id).orElse(null);
        model.addAttribute("applications", applications);
        model.addAttribute("job", job);
        model.addAttribute("allStatuses", ApplicationStatus.values());
        return "recruiter/applicants";
    }

    @PostMapping("/applications/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ApplicationStatus status,
                               @RequestParam(required = false) String notes,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            User recruiter = requireRecruiter(session);
            jobApplicationService.updateApplicationStatus(id, recruiter.getId(), status, notes);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái ứng viên thành công!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/recruiter/applications";
    }

    @GetMapping("/cv/{id}")
    public String viewApplicantCV(@PathVariable Long id, HttpSession session, Model model) {
        requireRecruiter(session);
        CandidateCv cv = candidateCvService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy CV ID: " + id));
        model.addAttribute("cv", cv);
        return "recruiter/cv-detail";
    }
}
