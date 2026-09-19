package org.example.baitapcnpm.repository;

import org.example.baitapcnpm.model.JobPosting;
import org.example.baitapcnpm.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatusOrderByCreatedAtDesc(JobStatus status);

    List<JobPosting> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);

    List<JobPosting> findAllByOrderByCreatedAtDesc();

    @Query("SELECT j FROM JobPosting j WHERE j.status = :status AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<JobPosting> searchJobs(@Param("status") JobStatus status, @Param("keyword") String keyword);
}
