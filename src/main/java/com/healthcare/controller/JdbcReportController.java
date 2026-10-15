
package com.healthcare.controller;

import com.healthcare.service.JdbcReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.SQLException;

@RestController
public class JdbcReportController {

    private final JdbcReportService jdbcReportService;

    public JdbcReportController(JdbcReportService jdbcReportService) {
        this.jdbcReportService = jdbcReportService;
    }

    @GetMapping("/jdbc-report")
    public String getAppointmentReport() throws SQLException {
        long total = jdbcReportService.getTotalAppointments();
        return "Total appointments in database: " + total;
    }
}

