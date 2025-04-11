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

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet rs = metaData.getProcedureColumns(null, schema.toUpperCase(), procedureName.toUpperCase(), null);

            while (rs.next()) {
                String paramName = rs.getString("COLUMN_NAME");
                int columnType = rs.getInt("COLUMN_TYPE"); // IN/OUT/INOUT
                int dataType = rs.getInt("DATA_TYPE");     // SQL type (e.g., 12 for VARCHAR)

                Map<String, String> param = new HashMap<>();
                param.put("name", paramName);
                param.put("type", mapSQLType(dataType));
                param.put("direction", mapDirection(columnType));
                parameters.add(param);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parameters;
    }

    private String mapDirection(int code) {
        return switch (code) {
            case DatabaseMetaData.procedureColumnIn -> "IN";
            case DatabaseMetaData.procedureColumnInOut -> "INOUT";
            case DatabaseMetaData.procedureColumnOut -> "OUT";
            default -> "UNKNOWN";
        };
    }

    private String mapSQLType(int type) {
        return switch (type) {
            case Types.VARCHAR -> "VARCHAR";
            case Types.NVARCHAR -> "NVARCHAR";
            case Types.NUMERIC -> "NUMBER";
            case Types.INTEGER -> "INTEGER";
            case Types.DATE -> "DATE";
            case Types.TIMESTAMP -> "TIMESTAMP";
            case Types.REF_CURSOR -> "REF_CURSOR";
            default -> "OTHER(" + type + ")";
        };
    }


}
