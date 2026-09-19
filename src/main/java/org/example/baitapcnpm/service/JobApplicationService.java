package org.example.baitapcnpm.service;

import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.*;
import org.example.baitapcnpm.repository.CandidateCvRepository;
import org.example.baitapcnpm.repository.JobApplicationRepository;
import org.example.baitapcnpm.repository.JobPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateCvRepository cvRepository;

    /**
     * Nghiệp vụ chính: Ứng viên nộp đơn ứng tuyển vào một công việc
     */
    @Transactional
    public JobApplication applyForJob(User candidate, Long jobId, Long cvId, String coverLetter) {
        if (candidate.getRole() != Role.ROLE_CANDIDATE) {
            throw new IllegalArgumentException("Chỉ tài khoản Ứng viên mới có thể nộp đơn ứng tuyển!");
        }

        // 1. Kiểm tra tin tuyển dụng
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin tuyển dụng với ID: " + jobId));

        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalStateException("Tin tuyển dụng này hiện chưa mở nhận hồ sơ hoặc đã đóng!");
        }

        // 2. Kiểm tra hồ sơ CV
        CandidateCv cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ CV với ID: " + cvId));

        if (!cv.getCandidate().getId().equals(candidate.getId())) {
            throw new SecurityException("Hồ sơ CV không thuộc về tài khoản của bạn!");
        }

        // 3. Kiểm tra xem ứng viên đã ứng tuyển vào công việc này chưa (ngăn trùng lặp)
        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())) {
            throw new IllegalStateException("Bạn đã nộp đơn ứng tuyển vào công việc này rồi. Vui lòng theo dõi trạng thái tại trang đơn ứng tuyển!");
        }

        // 4. Tạo đối tượng JobApplication mới với trạng thái ban đầu là SUBMITTED
        JobApplication application = JobApplication.builder()
                .job(job)
                .candidate(candidate)
                .cv(cv)
                .coverLetter(coverLetter)
                .status(ApplicationStatus.SUBMITTED)
                .appliedAt(LocalDateTime.now())
                .build();

        // 5. Lưu vào CSDL và trả về kết quả
        return applicationRepository.save(application);
    }

    public boolean hasApplied(Long jobId, Long candidateId) {
        if (candidateId == null) return false;
        return applicationRepository.existsByJobIdAndCandidateId(jobId, candidateId);
    }

    public List<JobApplication> getApplicationsByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateIdWithDetails(candidateId);
    }

    public List<JobApplication> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobIdWithDetails(jobId);
    }

    public List<JobApplication> getApplicationsByRecruiter(Long recruiterId) {
        return applicationRepository.findByRecruiterIdWithDetails(recruiterId);
    }

    public Optional<JobApplication> findById(Long id) {
        return applicationRepository.findById(id);
    }

    /**
     * Nghiệp vụ Nhà tuyển dụng cập nhật trạng thái ứng viên
     */
    @Transactional
    public JobApplication updateApplicationStatus(Long applicationId, Long recruiterId, ApplicationStatus newStatus, String notes) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn ứng tuyển ID: " + applicationId));

        if (!application.getJob().getRecruiter().getId().equals(recruiterId)) {
            throw new SecurityException("Bạn không có quyền quản lý đơn ứng tuyển này!");
        }

        application.setStatus(newStatus);
        if (notes != null && !notes.trim().isEmpty()) {
            application.setRecruiterNotes(notes.trim());
        }
        application.setReviewedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }
}
