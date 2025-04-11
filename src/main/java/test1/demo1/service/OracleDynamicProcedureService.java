package test1.demo1.service;

import oracle.jdbc.internal.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OracleDynamicProcedureService {

    @Autowired
    private DataSource dataSource;

    public List<Map<String, Object>> callProcedure(String procedureName, Map<String, String> inputParams) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            String call = buildCallStatement(procedureName, inputParams.size());
            CallableStatement stmt = conn.prepareCall(call);

            int index = 1;
            for (String key : inputParams.keySet()) {
                stmt.setString(index++, inputParams.get(key));
            }

            stmt.registerOutParameter(index, OracleTypes.CURSOR);
            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(index);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                resultList.add(row);
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    private String buildCallStatement(String procedureName, int inputCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ call ").append(procedureName).append("(");
        sb.append("?,".repeat(Math.max(0, inputCount)));
        sb.append("?) }"); // One output cursor
        return sb.toString().replace(",?) }", "?) }");
    }
}
