package com.healthcare.controller;

import com.healthcare.entity.MedicalRecord;
import com.healthcare.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> createRecord(
            @RequestBody CreateRecordRequest request) {

        MedicalRecord record = medicalRecordService.createRecord(
                request.getPatientId(),
                request.getDoctorId(),
                request.getAppointmentId(),
                request.getRecord()
        );

        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getRecordById(@PathVariable Long id) {
        return medicalRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getPatientRecords(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                medicalRecordService.findByPatient(patientId)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecord>> getDoctorRecords(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                medicalRecordService.findByDoctor(doctorId)
        );
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecord> getRecordByAppointment(
            @PathVariable Long appointmentId) {

        return medicalRecordService.findByAppointment(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecord>> getRecordsByPatientAndDoctor(
            @PathVariable Long patientId,
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                medicalRecordService.findByPatientAndDoctor(patientId, doctorId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateRecord(
            @PathVariable Long id,
            @RequestBody UpdateRecordRequest request) {

        MedicalRecord updatedRecord = medicalRecordService.updateRecord(
                id,
                request.getDiagnosis(),
                request.getPrescription(),
                request.getLabResults(),
                request.getFollowUpNotes()
        );

        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateRecordRequest {

        private Long patientId;
        private Long doctorId;
        private Long appointmentId;
        private MedicalRecord record;

        public Long getPatientId() {
            return patientId;
        }

        public void setPatientId(Long patientId) {
            this.patientId = patientId;
        }

        public Long getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(Long doctorId) {
            this.doctorId = doctorId;
        }

        public Long getAppointmentId() {
            return appointmentId;
        }

        public void setAppointmentId(Long appointmentId) {
            this.appointmentId = appointmentId;
        }

        public MedicalRecord getRecord() {
            return record;
        }

        public void setRecord(MedicalRecord record) {
            this.record = record;
        }
    }

    public static class UpdateRecordRequest {

        private String diagnosis;
        private String prescription;
        private String labResults;
        private String followUpNotes;

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getPrescription() {
            return prescription;
        }

        public void setPrescription(String prescription) {
            this.prescription = prescription;
        }

        public String getLabResults() {
            return labResults;
        }

        public void setLabResults(String labResults) {
            this.labResults = labResults;
        }

        public String getFollowUpNotes() {
            return followUpNotes;
        }

        public void setFollowUpNotes(String followUpNotes) {
            this.followUpNotes = followUpNotes;
        }
    }
}
