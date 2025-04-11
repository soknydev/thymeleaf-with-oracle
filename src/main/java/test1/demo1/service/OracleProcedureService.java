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
public class OracleProcedureService {

    @Autowired
    private DataSource dataSource;

    public List<Map<String, Object>> callReportProcedure(String dateFrom, String dateTo) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call PISETH.TPT_REPORT(?, ?, ?)}");

            stmt.setString(1, dateFrom);
            stmt.setString(2, dateTo);
            stmt.registerOutParameter(3, OracleTypes.CURSOR); // Requires oracle.jdbc.OracleTypes

            stmt.execute();

            ResultSet rs = (ResultSet) stmt.getObject(3);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultList;
    }
}
