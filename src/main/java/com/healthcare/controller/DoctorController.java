package com.healthcare.controller;

import com.healthcare.entity.Doctor;
import com.healthcare.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Doctor> createDoctorProfile(
            @PathVariable Long userId,
            @RequestBody Doctor doctor) {

        Doctor createdDoctor = doctorService.createDoctorProfile(userId, doctor);
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Doctor> getDoctorByUserId(@PathVariable Long userId) {
        return doctorService.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Boolean available) {

        if (specialization != null && available != null && available) {
            return ResponseEntity.ok(
                    doctorService.findAvailableBySpecialization(specialization)
            );
        }

        if (specialization != null) {
            return ResponseEntity.ok(
                    doctorService.findBySpecialization(specialization)
            );
        }

        if (available != null && available) {
            return ResponseEntity.ok(
                    doctorService.findAvailableDoctors()
            );
        }

        return ResponseEntity.ok(doctorService.findAllDoctors());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctorProfile(
            @PathVariable Long id,
            @RequestBody UpdateDoctorRequest request) {

        Doctor updatedDoctor = doctorService.updateDoctorProfile(
                id,
                request.getSpecialization(),
                request.getQualification(),
                request.getExperienceYears(),
                request.getConsultationFee()
        );

        return ResponseEntity.ok(updatedDoctor);
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Doctor> setAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {

        Doctor updatedDoctor = doctorService.setAvailability(id, available);
        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorProfile(@PathVariable Long id) {
        doctorService.deleteDoctorProfile(id);
        return ResponseEntity.noContent().build();
    }

    public static class UpdateDoctorRequest {

        private String specialization;
        private String qualification;
        private Integer experienceYears;
        private Double consultationFee;

        public String getSpecialization() {
            return specialization;
        }

        public void setSpecialization(String specialization) {
            this.specialization = specialization;
        }

        public String getQualification() {
            return qualification;
        }

        public void setQualification(String qualification) {
            this.qualification = qualification;
        }

        public Integer getExperienceYears() {
            return experienceYears;
        }

        public void setExperienceYears(Integer experienceYears) {
            this.experienceYears = experienceYears;
        }

        public Double getConsultationFee() {
            return consultationFee;
        }

        public void setConsultationFee(Double consultationFee) {
            this.consultationFee = consultationFee;
        }
    }
}
