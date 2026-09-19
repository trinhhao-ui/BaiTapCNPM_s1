package org.example.baitapcnpm.service;

import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.JobPosting;
import org.example.baitapcnpm.model.JobStatus;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.repository.JobPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public List<JobPosting> getApprovedJobs() {
        return jobPostingRepository.findByStatusOrderByCreatedAtDesc(JobStatus.APPROVED);
    }

    public List<JobPosting> searchApprovedJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getApprovedJobs();
        }
        return jobPostingRepository.searchJobs(JobStatus.APPROVED, keyword.trim());
    }

    public List<JobPosting> getJobsByRecruiter(Long recruiterId) {
        return jobPostingRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId);
    }

    public List<JobPosting> getAllJobsForAdmin() {
        return jobPostingRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<JobPosting> findById(Long id) {
        return jobPostingRepository.findById(id);
    }

    @Transactional
    public JobPosting createJob(User recruiter, String title, String companyName, String location,
                                String salaryRange, String employmentType, String description,
                                String requirements, String benefits) {
        JobPosting job = JobPosting.builder()
                .recruiter(recruiter)
                .title(title)
                .companyName(companyName)
                .location(location)
                .salaryRange(salaryRange)
                .employmentType(employmentType)
                .description(description)
                .requirements(requirements)
                .benefits(benefits)
                .status(JobStatus.PENDING_APPROVAL) // Mặc định chờ quản trị viên duyệt
                .build();
        return jobPostingRepository.save(job);
    }

    @Transactional
    public JobPosting updateJob(Long jobId, Long recruiterId, String title, String companyName,
                                String location, String salaryRange, String employmentType,
                                String description, String requirements, String benefits) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng ID: " + jobId));

        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new SecurityException("Bạn không có quyền sửa tin tuyển dụng này!");
        }

        job.setTitle(title);
        job.setCompanyName(companyName);
        job.setLocation(location);
        job.setSalaryRange(salaryRange);
        job.setEmploymentType(employmentType);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setBenefits(benefits);

        return jobPostingRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long jobId, Long recruiterId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng ID: " + jobId));

        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new SecurityException("Bạn không có quyền xóa tin tuyển dụng này!");
        }

        jobPostingRepository.delete(job);
    }

    @Transactional
    public JobPosting updateStatus(Long jobId, JobStatus newStatus) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng ID: " + jobId));
        job.setStatus(newStatus);
        return jobPostingRepository.save(job);
    }
}
