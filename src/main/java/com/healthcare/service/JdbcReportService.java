
package com.healthcare.service;

import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class JdbcReportService {

    private final DataSource dataSource;

    public JdbcReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long getTotalAppointments() throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            return 0;
        }
    }
}

