package org.example.baitapcnpm.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.CandidateCv;
import org.example.baitapcnpm.model.JobPosting;
import org.example.baitapcnpm.model.Role;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.service.CandidateCvService;
import org.example.baitapcnpm.service.JobApplicationService;
import org.example.baitapcnpm.service.JobPostingService;
import org.example.baitapcnpm.util.SessionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final JobPostingService jobPostingService;
    private final CandidateCvService candidateCvService;
    private final JobApplicationService jobApplicationService;

    @GetMapping({"/", "/jobs"})
    public String index(@RequestParam(required = false) String keyword, Model model) {
        List<JobPosting> jobs = jobPostingService.searchApprovedJobs(keyword);
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "index";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, HttpSession session, Model model) {
        JobPosting job = jobPostingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy việc làm có ID: " + id));

        User currentUser = SessionHelper.getCurrentUser(session);
        boolean hasApplied = false;
        List<CandidateCv> candidateCVs = List.of();

        if (currentUser != null && currentUser.getRole() == Role.ROLE_CANDIDATE) {
            hasApplied = jobApplicationService.hasApplied(job.getId(), currentUser.getId());
            candidateCVs = candidateCvService.getCVsByCandidate(currentUser.getId());
        }

        model.addAttribute("job", job);
        model.addAttribute("hasApplied", hasApplied);
        model.addAttribute("candidateCVs", candidateCVs);

        return "jobs/detail";
    }
}
