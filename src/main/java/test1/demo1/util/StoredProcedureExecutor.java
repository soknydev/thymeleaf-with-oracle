package test1.demo1.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class StoredProcedureExecutor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> executeProcedure(String procedureName, List<Object> inputParams) {
        return jdbcTemplate.execute((Connection con) -> {
            // Build the callable statement string with placeholders
            StringJoiner paramPlaceholders = new StringJoiner(",", "(", ")");
            for (int i = 0; i < inputParams.size(); i++) {
                paramPlaceholders.add("?");
            }
            String call = "{call " + procedureName + paramPlaceholders.toString() + "}";

            try (CallableStatement cs = con.prepareCall(call)) {
                // Set input parameters
                for (int i = 0; i < inputParams.size(); i++) {
                    cs.setObject(i + 1, inputParams.get(i));
                }

                boolean hasResultSet = cs.execute();
                List<Map<String, Object>> resultList = new ArrayList<>();

                // Process the result set
                if (hasResultSet) {
                    try (ResultSet rs = cs.getResultSet()) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();

                        while (rs.next()) {
                            Map<String, Object> row = new HashMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                row.put(rsmd.getColumnLabel(i), rs.getObject(i));
                            }
                            resultList.add(row);
                        }
                    }
                }

                return resultList;
            }
        });
    }
}
