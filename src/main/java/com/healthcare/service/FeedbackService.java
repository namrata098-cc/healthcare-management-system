package com.healthcare.service;

import com.healthcare.entity.Doctor;
import com.healthcare.entity.Feedback;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.FeedbackRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link Feedback} operations.
 *
 * <p>Allows users (typically patients) to rate and comment on doctors.
 * Enforces one feedback entry per user per doctor.
 */
@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           UserRepository userRepository,
                           DoctorRepository doctorRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    /**
     * Submit feedback for a doctor from a user.
     *
     * <p>Business rules:
     * <ul>
     *   <li>Both the user and doctor must exist.</li>
     *   <li>A user can only leave one review per doctor.</li>
     *   <li>Rating must be between 1 and 5 (enforced by the entity's @Min/@Max).</li>
     * </ul>
     *
     * @param userId   the ID of the user submitting the feedback
     * @param doctorId the ID of the doctor being reviewed
     * @param rating   a star rating from 1 to 5
     * @param comment  an optional written comment
     * @return the saved Feedback
     */
    public Feedback submitFeedback(Long userId, Long doctorId, Integer rating, String comment) {
        // 1. Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + userId));

        // 2. Validate doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Doctor not found with id: " + doctorId));

        // 3. One review per user per doctor
        if (feedbackRepository.existsByUserIdAndDoctorId(userId, doctorId)) {
            throw new IllegalStateException(
                    "You have already submitted feedback for this doctor.");
        }

        // 4. Validate rating range manually as an extra safety net
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Feedback feedback = new Feedback(rating, user, doctor);
        feedback.setComment(comment);

        return feedbackRepository.save(feedback);
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    /**
     * Find a single feedback entry by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<Feedback> findById(Long id) {
        return feedbackRepository.findById(id);
    }

    /**
     * Get all feedback for a doctor, newest first.
     */
    @Transactional(readOnly = true)
    public List<Feedback> findByDoctor(Long doctorId) {
        return feedbackRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    /**
     * Get all feedback written by a specific user.
     */
    @Transactional(readOnly = true)
    public List<Feedback> findByUser(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    /**
     * Get all feedback for a doctor filtered by star rating.
     * Example: retrieve only 5-star reviews for a doctor.
     */
    @Transactional(readOnly = true)
    public List<Feedback> findByDoctorAndRating(Long doctorId, Integer rating) {
        return feedbackRepository.findByDoctorIdAndRating(doctorId, rating);
    }

    /**
     * Calculate the average star rating for a doctor.
     * Returns 0.0 if no feedback has been submitted yet.
     */
    @Transactional(readOnly = true)
    public double getAverageRating(Long doctorId) {
        List<Feedback> feedbackList = feedbackRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
        if (feedbackList.isEmpty()) {
            return 0.0;
        }
        return feedbackList.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Edit the rating and comment of an existing feedback entry.
     * Only the original author should be allowed to do this
     * (ownership check will be added in the security layer).
     */
    public Feedback updateFeedback(Long feedbackId, Integer newRating, String newComment) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Feedback not found with id: " + feedbackId));

        if (newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        feedback.setRating(newRating);
        feedback.setComment(newComment);

        return feedbackRepository.save(feedback);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Delete a feedback entry by ID.
     */
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new IllegalArgumentException("Feedback not found with id: " + id);
        }
        feedbackRepository.deleteById(id);
    }
}
