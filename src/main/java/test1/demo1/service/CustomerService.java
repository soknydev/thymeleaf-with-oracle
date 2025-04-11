package test1.demo1.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import test1.demo1.model.Customer;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall simpleJdbcCall;

    public CustomerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void init() {
        this.simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GET_ALL_CUSTOMERS")
                .returningResultSet("p_cursor", new CustomerRowMapper());
    }

    public List<Customer> getAllCustomers() {
        Map<String, Object> result = simpleJdbcCall.execute();
        return (List<Customer>) result.get("p_cursor");
    }

    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer c = new Customer();
            c.setId(rs.getLong("CUS_ID"));
            c.setFirstName(rs.getString("FIRST_NAME"));
            c.setLastName(rs.getString("LAST_NAME"));
            c.setSalary(rs.getDouble("SALARY"));
            c.setDob(rs.getDate("DOB"));
            c.setGender(rs.getString("GENDER"));
            c.setMobilePhone(rs.getString("MOBILE_PHONE"));
            return c;
        }
    }
}
