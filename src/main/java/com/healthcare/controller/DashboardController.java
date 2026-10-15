package com.healthcare.controller;

import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.healthcare.service.AppointmentService;
import com.healthcare.service.DoctorService;
import com.healthcare.service.PatientService;

@Controller
public class DashboardController {
    private final DoctorService doctorService;
private final PatientService patientService;
private final AppointmentService appointmentService;

public DashboardController(
        DoctorService doctorService,
        PatientService patientService,
        AppointmentService appointmentService) {
    this.doctorService = doctorService;
    this.patientService = patientService;
    this.appointmentService = appointmentService;
}

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            return "redirect:/doctor/dashboard";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            return "redirect:/patient/dashboard";
        }

        return "redirect:/login";
    }
    

@GetMapping("/admin/dashboard")
public String adminDashboard(Model model) {

    model.addAttribute("totalDoctors", doctorService.findAllDoctors().size());
    model.addAttribute("totalPatients", patientService.findAllPatients().size());
    model.addAttribute("totalAppointments", appointmentService.countAppointments());
    model.addAttribute("pendingAppointments", appointmentService.countPendingAppointments());

    return "admin-dashboard";
}

@GetMapping("/patient/dashboard")
public String patientDashboard() {
    return "patient-dashboard";
}
}