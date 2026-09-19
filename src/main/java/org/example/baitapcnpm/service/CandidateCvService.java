package org.example.baitapcnpm.service;

import lombok.RequiredArgsConstructor;
import org.example.baitapcnpm.model.CandidateCv;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.repository.CandidateCvRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateCvService {

    private final CandidateCvRepository cvRepository;

    public List<CandidateCv> getCVsByCandidate(Long candidateId) {
        return cvRepository.findByCandidateIdOrderByUpdatedAtDesc(candidateId);
    }

    public Optional<CandidateCv> getLatestCV(Long candidateId) {
        return cvRepository.findFirstByCandidateIdOrderByUpdatedAtDesc(candidateId);
    }

    public Optional<CandidateCv> findById(Long id) {
        return cvRepository.findById(id);
    }

    @Transactional
    public CandidateCv saveOrUpdateCV(User candidate, Long cvId, String title, String summary,
                                      String skills, String education, String experience, String portfolioUrl) {
        CandidateCv cv;
        if (cvId != null) {
            cv = cvRepository.findById(cvId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy CV ID: " + cvId));
            if (!cv.getCandidate().getId().equals(candidate.getId())) {
                throw new SecurityException("Bạn không có quyền chỉnh sửa CV này!");
            }
        } else {
            cv = new CandidateCv();
            cv.setCandidate(candidate);
        }

        cv.setTitle(title);
        cv.setSummary(summary);
        cv.setSkills(skills);
        cv.setEducation(education);
        cv.setExperience(experience);
        cv.setPortfolioUrl(portfolioUrl);

        return cvRepository.save(cv);
    }

    @Transactional
    public void deleteCV(Long cvId, Long candidateId) {
        CandidateCv cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy CV ID: " + cvId));
        if (!cv.getCandidate().getId().equals(candidateId)) {
            throw new SecurityException("Bạn không có quyền xóa CV này!");
        }
        cvRepository.delete(cv);
    }
}
