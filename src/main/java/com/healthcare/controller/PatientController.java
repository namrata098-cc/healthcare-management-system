package com.healthcare.controller;

import com.healthcare.entity.Patient;
import com.healthcare.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Patient> createPatientProfile(
            @PathVariable Long userId,
            @RequestBody Patient patient) {

        Patient createdPatient = patientService.createPatientProfile(userId, patient);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Patient> getPatientByUserId(@PathVariable Long userId) {
        return patientService.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.findAllPatients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatientProfile(
            @PathVariable Long id,
            @RequestBody UpdatePatientRequest request) {

        Patient updatedPatient = patientService.updatePatientProfile(
                id,
                request.getAddress(),
                request.getMedicalHistory(),
                request.getAllergies()
        );

        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatientProfile(@PathVariable Long id) {
        patientService.deletePatientProfile(id);
        return ResponseEntity.noContent().build();
    }

    public static class UpdatePatientRequest {

        private String address;
        private String medicalHistory;
        private String allergies;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMedicalHistory() {
            return medicalHistory;
        }

        public void setMedicalHistory(String medicalHistory) {
            this.medicalHistory = medicalHistory;
        }

        public String getAllergies() {
            return allergies;
        }

        public void setAllergies(String allergies) {
            this.allergies = allergies;
        }
    }
}
