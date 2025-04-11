package test1.demo1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
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
                int columnType = rs.getInt("COLUMN_TYPE"); // 1=IN, 2=INOUT, 4=OUT
                int dataType = rs.getInt("DATA_TYPE");

                if (columnType == DatabaseMetaData.procedureColumnIn ||
                    columnType == DatabaseMetaData.procedureColumnInOut) {
                    Map<String, String> param = new HashMap<>();
                    param.put("name", paramName);
                    param.put("type", String.valueOf(dataType));
                    parameters.add(param);
                }
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parameters;
    }
}
