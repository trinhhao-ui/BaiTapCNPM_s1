package org.example.baitapcnpm.service;

import org.example.baitapcnpm.model.*;
import org.example.baitapcnpm.repository.CandidateCvRepository;
import org.example.baitapcnpm.repository.JobApplicationRepository;
import org.example.baitapcnpm.repository.JobPostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private CandidateCvRepository cvRepository;

    @InjectMocks
    private JobApplicationService applicationService;

    private User candidate;
    private User recruiter;
    private JobPosting job;
    private CandidateCv cv;

    @BeforeEach
    void setUp() {
        candidate = User.builder()
                .id(1L)
                .username("candidate_nam")
                .fullName("Nguyễn Văn Nam")
                .role(Role.ROLE_CANDIDATE)
                .build();

        recruiter = User.builder()
                .id(2L)
                .username("fpt_hr")
                .fullName("Lê Thu Hà")
                .role(Role.ROLE_RECRUITER)
                .build();

        job = JobPosting.builder()
                .id(100L)
                .recruiter(recruiter)
                .title("Senior Java Developer")
                .companyName("FPT Software")
                .status(JobStatus.APPROVED)
                .build();

        cv = CandidateCv.builder()
                .id(10L)
                .candidate(candidate)
                .title("CV Java Backend")
                .skills("Java, Spring Boot")
                .build();
    }

    @Test
    @DisplayName("Ứng viên nộp đơn ứng tuyển thành công")
    void testApplyForJob_Success() {
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));
        when(cvRepository.findById(10L)).thenReturn(Optional.of(cv));
        when(applicationRepository.existsByJobIdAndCandidateId(100L, 1L)).thenReturn(false);
        when(applicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> {
            JobApplication app = invocation.getArgument(0);
            app.setId(1L);
            return app;
        });

        JobApplication result = applicationService.applyForJob(candidate, 100L, 10L, "Kính gửi công ty, em mong muốn ứng tuyển vị trí này.");

        assertNotNull(result);
        assertEquals(ApplicationStatus.SUBMITTED, result.getStatus());
        assertEquals(candidate.getId(), result.getCandidate().getId());
        assertEquals(job.getId(), result.getJob().getId());
        assertEquals("CV Java Backend", result.getCv().getTitle());

        verify(applicationRepository, times(1)).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("Ngăn chặn ứng viên nộp đơn trùng lặp vào cùng một công việc")
    void testApplyForJob_DuplicateApplication_ThrowsException() {
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));
        when(cvRepository.findById(10L)).thenReturn(Optional.of(cv));
        when(applicationRepository.existsByJobIdAndCandidateId(100L, 1L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                applicationService.applyForJob(candidate, 100L, 10L, "Nộp đơn lần 2"));

        assertTrue(exception.getMessage().contains("Bạn đã nộp đơn ứng tuyển vào công việc này rồi"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Không cho phép nộp đơn vào tin tuyển dụng chưa được duyệt hoặc đã đóng")
    void testApplyForJob_UnapprovedJob_ThrowsException() {
        job.setStatus(JobStatus.PENDING_APPROVAL);
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                applicationService.applyForJob(candidate, 100L, 10L, "Thư xin việc"));

        assertTrue(exception.getMessage().contains("chưa mở nhận hồ sơ"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Không cho phép sử dụng CV của ứng viên khác")
    void testApplyForJob_OtherUserCV_ThrowsSecurityException() {
        User otherUser = User.builder().id(99L).build();
        cv.setCandidate(otherUser);

        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(job));
        when(cvRepository.findById(10L)).thenReturn(Optional.of(cv));

        assertThrows(SecurityException.class, () ->
                applicationService.applyForJob(candidate, 100L, 10L, "Thư xin việc"));
    }

    @Test
    @DisplayName("Nhà tuyển dụng cập nhật trạng thái hồ sơ ứng viên thành Trúng tuyển")
    void testUpdateApplicationStatus_Success() {
        JobApplication application = JobApplication.builder()
                .id(50L)
                .job(job)
                .candidate(candidate)
                .cv(cv)
                .status(ApplicationStatus.SUBMITTED)
                .build();

        when(applicationRepository.findById(50L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobApplication updated = applicationService.updateApplicationStatus(50L, recruiter.getId(), ApplicationStatus.ACCEPTED, "Chúc mừng bạn đã trúng tuyển!");

        assertNotNull(updated);
        assertEquals(ApplicationStatus.ACCEPTED, updated.getStatus());
        assertEquals("Chúc mừng bạn đã trúng tuyển!", updated.getRecruiterNotes());
        assertNotNull(updated.getReviewedAt());
    }
}
