package com.proyecto.ops.customers.repo;

import com.proyecto.ops.customers.model.CustomerBasic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class CustomerJdbcRepository {

    private final JdbcTemplate jdbc;

    public CustomerJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- Mappers ----------
    private static final RowMapper<CustomerBasic> BASIC_MAPPER = new RowMapper<>() {
        @Override public CustomerBasic mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CustomerBasic(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("name"),
                    rs.getString("tax_id"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getObject("created_at", OffsetDateTime.class)
            );
        }
    };

    // ---------- List & Search ----------
    public List<CustomerBasic> list(int page, int size) {
        int limit = Math.max(size, 1);
        int offset = Math.max(page, 0) * limit;
        String sql = """
            select id, name, tax_id, email, phone, created_at
            from app.customers
            order by created_at desc
            limit ? offset ?
        """;
        return jdbc.query(sql, BASIC_MAPPER, limit, offset);
    }

    public long countAll() {
        return Optional.ofNullable(
                jdbc.queryForObject("select count(*) from app.customers", Long.class)
        ).orElse(0L);
    }

    public List<CustomerBasic> search(String q, int page, int size) {
        int limit = Math.max(size, 1);
        int offset = Math.max(page, 0) * limit;
        String like = "%" + q.toLowerCase() + "%";
        String sql = """
            select id, name, tax_id, email, phone, created_at
            from app.customers
            where lower(name) like ? or lower(coalesce(email,'')) like ? or lower(coalesce(tax_id,'')) like ?
            order by created_at desc
            limit ? offset ?
        """;
        return jdbc.query(sql, BASIC_MAPPER, like, like, like, limit, offset);
    }

    public long countSearch(String q) {
        String like = "%" + q.toLowerCase() + "%";
        String sql = """
            select count(*)
            from app.customers
            where lower(name) like ? or lower(coalesce(email,'')) like ? or lower(coalesce(tax_id,'')) like ?
        """;
        return Optional.ofNullable(jdbc.queryForObject(sql, Long.class, like, like, like)).orElse(0L);
    }

    // ---------- Create / Update / Delete ----------
    public CustomerBasic create(String name, String taxId, String email, String phone) {
        UUID id = UUID.randomUUID();
        String sql = """
            insert into app.customers(id, name, tax_id, email, phone)
            values(?, ?, ?, ?, ?)
        """;
        jdbc.update(sql, id, name, taxId, email, phone);
        return findById(id).orElseThrow();
    }

    public Optional<CustomerBasic> updatePartial(UUID id, String name, String taxId, String email, String phone) {
        // construye dinámicamente sólo los campos no nulos
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("update app.customers set ");
        boolean first = true;

        if (name != null) { sb.append(first ? "" : ", ").append("name=?"); params.add(name); first = false; }
        if (taxId != null) { sb.append(first ? "" : ", ").append("tax_id=?"); params.add(taxId); first = false; }
        if (email != null) { sb.append(first ? "" : ", ").append("email=?"); params.add(email); first = false; }
        if (phone != null) { sb.append(first ? "" : ", ").append("phone=?"); params.add(phone); first = false; }

        if (first) return findById(id); // nada que actualizar

        sb.append(" where id=?");
        params.add(id);

        int updated = jdbc.update(sb.toString(), params.toArray());
        return updated > 0 ? findById(id) : Optional.empty();
    }

    public boolean delete(UUID id) {
        return jdbc.update("delete from app.customers where id=?", id) > 0;
    }

    public Optional<CustomerBasic> findById(UUID id) {
        List<CustomerBasic> list = jdbc.query("""
            select id, name, tax_id, email, phone, created_at
            from app.customers where id=?
        """, BASIC_MAPPER, id);
        return list.stream().findFirst();
    }
}