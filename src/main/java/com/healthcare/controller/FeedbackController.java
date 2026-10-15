package com.healthcare.controller;

import com.healthcare.entity.Feedback;
import com.healthcare.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(
            @RequestBody SubmitFeedbackRequest request) {

        Feedback feedback = feedbackService.submitFeedback(
                request.getUserId(),
                request.getDoctorId(),
                request.getRating(),
                request.getComment()
        );

        return new ResponseEntity<>(feedback, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        return feedbackService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Feedback>> getDoctorFeedback(
            @PathVariable Long doctorId,
            @RequestParam(required = false) Integer rating) {

        if (rating != null) {
            return ResponseEntity.ok(
                    feedbackService.findByDoctorAndRating(doctorId, rating)
            );
        }

        return ResponseEntity.ok(
                feedbackService.findByDoctor(doctorId)
        );
    }

    @GetMapping("/doctor/{doctorId}/average")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                feedbackService.getAverageRating(doctorId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feedback>> getUserFeedback(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                feedbackService.findByUser(userId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable Long id,
            @RequestBody UpdateFeedbackRequest request) {

        Feedback updatedFeedback = feedbackService.updateFeedback(
                id,
                request.getRating(),
                request.getComment()
        );

        return ResponseEntity.ok(updatedFeedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    public static class SubmitFeedbackRequest {

        private Long userId;
        private Long doctorId;
        private Integer rating;
        private String comment;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(Long doctorId) {
            this.doctorId = doctorId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class UpdateFeedbackRequest {

        private Integer rating;
        private String comment;

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
