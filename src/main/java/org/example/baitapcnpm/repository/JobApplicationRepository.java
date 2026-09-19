package org.example.baitapcnpm.repository;

import org.example.baitapcnpm.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);

    Optional<JobApplication> findByJobIdAndCandidateId(Long jobId, Long candidateId);

    @Query("SELECT a FROM JobApplication a " +
           "JOIN FETCH a.job j " +
           "JOIN FETCH a.cv c " +
           "WHERE a.candidate.id = :candidateId " +
           "ORDER BY a.appliedAt DESC")
    List<JobApplication> findByCandidateIdWithDetails(@Param("candidateId") Long candidateId);

    @Query("SELECT a FROM JobApplication a " +
           "JOIN FETCH a.candidate c " +
           "JOIN FETCH a.cv cv " +
           "WHERE a.job.id = :jobId " +
           "ORDER BY a.appliedAt DESC")
    List<JobApplication> findByJobIdWithDetails(@Param("jobId") Long jobId);

    @Query("SELECT a FROM JobApplication a " +
           "JOIN FETCH a.job j " +
           "JOIN FETCH a.candidate c " +
           "JOIN FETCH a.cv cv " +
           "WHERE j.recruiter.id = :recruiterId " +
           "ORDER BY a.appliedAt DESC")
    List<JobApplication> findByRecruiterIdWithDetails(@Param("recruiterId") Long recruiterId);

    long countByJobId(Long jobId);
}
