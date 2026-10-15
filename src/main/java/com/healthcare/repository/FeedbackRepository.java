package com.healthcare.repository;

import com.healthcare.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Feedback} entities.
 *
 * <p>Provides CRUD operations and domain-specific derived queries
 * for reading and writing doctor feedback/ratings.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Get all feedback entries submitted for a specific doctor,
     * ordered from newest to oldest.
     */
    List<Feedback> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    /**
     * Get all feedback entries submitted by a specific user.
     */
    List<Feedback> findByUserId(Long userId);

    /**
     * Check if a user has already submitted feedback for a specific doctor.
     * Useful for enforcing one-review-per-patient rules.
     */
    boolean existsByUserIdAndDoctorId(Long userId, Long doctorId);

    /**
     * Get all feedback for a doctor with a specific star rating.
     * Example use: find all 5-star reviews for a doctor.
     */
    List<Feedback> findByDoctorIdAndRating(Long doctorId, Integer rating);
}
