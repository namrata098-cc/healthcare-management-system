package com.healthcare.controller;

import com.healthcare.entity.Appointment;
import com.healthcare.entity.Appointment.Status;
import com.healthcare.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Appointment> bookAppointment(
            @RequestBody BookAppointmentRequest request) {

        Appointment appointment = appointmentService.bookAppointment(
                request.getPatientId(),
                request.getDoctorId(),
                request.getAppointmentDateTime(),
                request.getReason()
        );

        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(
            @PathVariable Long patientId,
            @RequestParam(required = false) Status status) {

        if (status != null) {
            return ResponseEntity.ok(
                    appointmentService.findByPatientAndStatus(patientId, status)
            );
        }

        return ResponseEntity.ok(
                appointmentService.findByPatient(patientId)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(
            @PathVariable Long doctorId,
            @RequestParam(required = false) Status status) {

        if (status != null) {
            return ResponseEntity.ok(
                    appointmentService.findByDoctorAndStatus(doctorId, status)
            );
        }

        return ResponseEntity.ok(
                appointmentService.findByDoctor(doctorId)
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Appointment>> getAppointmentsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {

        return ResponseEntity.ok(
                appointmentService.findByDateRange(start, end)
        );
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Appointment> confirmAppointment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentService.confirmAppointment(id)
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Appointment> completeAppointment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentService.completeAppointment(id)
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentService.cancelAppointment(id)
        );
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Appointment> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody RescheduleAppointmentRequest request) {

        return ResponseEntity.ok(
                appointmentService.rescheduleAppointment(
                        id,
                        request.getNewDateTime(),
                        request.getNotes()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    public static class BookAppointmentRequest {

        private Long patientId;
        private Long doctorId;
        private LocalDateTime appointmentDateTime;
        private String reason;

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

        public LocalDateTime getAppointmentDateTime() {
            return appointmentDateTime;
        }

        public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
            this.appointmentDateTime = appointmentDateTime;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class RescheduleAppointmentRequest {

        private LocalDateTime newDateTime;
        private String notes;

        public LocalDateTime getNewDateTime() {
            return newDateTime;
        }

        public void setNewDateTime(LocalDateTime newDateTime) {
            this.newDateTime = newDateTime;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
