package test1.demo1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
public class ProcedureMetadataService {

    @Autowired
    private DataSource dataSource;

    public List<Map<String, String>> getProcedureParameters(String schema, String procedureName) {
        List<Map<String, String>> parameters = new ArrayList<>();

        String sql = """
                SELECT argument_name, in_out, data_type
                FROM all_arguments
                WHERE owner = ? AND object_name = ?
                  AND package_name IS NULL
                  AND argument_name IS NOT NULL
                ORDER BY position
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, schema.toUpperCase());
            stmt.setString(2, procedureName.toUpperCase());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String paramName = rs.getString("ARGUMENT_NAME");
                String direction = rs.getString("IN_OUT");
                String dataType = rs.getString("DATA_TYPE");

                // not display row that row = REF CURSOR
                if (dataType.equalsIgnoreCase("REF CURSOR")) {
                    continue;
                }

                Map<String, String> param = new HashMap<>();
                param.put("name", paramName);
                param.put("type", dataType);
                param.put("direction", direction != null ? direction : "UNKNOWN");
                parameters.add(param);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parameters;
    }
}
