package com.proyecto.ops.customers.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.proyecto.ops.customers.model.CustomerBasic;

@Repository
public class CustomerJdbcRepository {

    private final JdbcTemplate jdbc;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    private CustomerBasic mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CustomerBasic(
                (UUID) rs.getObject("id"),
                rs.getString("name")
        );
    }

    public List<CustomerBasic> findPage(int page, int size) {
        int offset = Math.max(0, page) * Math.max(1, size);
        // Solo seleccionamos columnas muy seguras: id y name
        return jdbc.query(
                "SELECT id, name FROM app.customers ORDER BY name ASC LIMIT ? OFFSET ?",
                this::mapRow, size, offset
        );
    }

    public Optional<CustomerBasic> findById(UUID id) {
        List<CustomerBasic> list = jdbc.query(
                "SELECT id, name FROM app.customers WHERE id = ?",
                this::mapRow, id
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public int countAll() {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM app.customers", Integer.class);
        return c == null ? 0 : c;
    }

    public List<CustomerBasic> searchByName(String q, int page, int size) {
        int offset = Math.max(0, page) * Math.max(1, size);
        String like = "%" + (q == null ? "" : q.trim().toLowerCase()) + "%";
        return jdbc.query(
                """
      SELECT id, name
      FROM app.customers
      WHERE LOWER(name) LIKE ?
      ORDER BY name ASC
      LIMIT ? OFFSET ?
      """,
                this::mapRow, like, size, offset
        );
    }

    public int countByName(String q) {
        String like = "%" + (q == null ? "" : q.trim().toLowerCase()) + "%";
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM app.customers WHERE LOWER(name) LIKE ?",
                Integer.class, like
        );
        return c == null ? 0 : c;
    }

    public UUID create(String name) {
        UUID id = java.util.UUID.randomUUID();
        jdbc.update(
                "INSERT INTO app.customers (id, name, created_at) VALUES (?, ?, now())",
                ps -> {
                    ps.setObject(1, id);
                    ps.setString(2, name);
                }
        );
        return id;
    }
}
