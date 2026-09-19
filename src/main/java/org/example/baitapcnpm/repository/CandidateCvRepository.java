package org.example.baitapcnpm.repository;

import org.example.baitapcnpm.model.CandidateCv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateCvRepository extends JpaRepository<CandidateCv, Long> {
    List<CandidateCv> findByCandidateIdOrderByUpdatedAtDesc(Long candidateId);
    Optional<CandidateCv> findFirstByCandidateIdOrderByUpdatedAtDesc(Long candidateId);
}
