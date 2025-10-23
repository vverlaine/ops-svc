/*
 * -----------------------------------------------------------------------------
 * CustomerJdbcRepository.java
 * -----------------------------------------------------------------------------
 * Propósito:
 *   Repositorio JDBC que gestiona las operaciones directas de lectura y escritura
 *   en la tabla `app.customers` del microservicio "customers-svc".
 *
 * Contexto de uso:
 *   - Utiliza JdbcTemplate para ejecutar consultas SQL nativas.
 *   - Se emplea para operaciones CRUD y búsquedas paginadas de clientes.
 *   - Ofrece una alternativa ligera a JPA para mejorar el rendimiento en lecturas.
 *
 * Diseño:
 *   - Anotado con @Repository para su gestión automática por el contenedor Spring.
 *   - Define un RowMapper para mapear los resultados SQL al DTO {@link com.proyecto.ops.customers.model.CustomerBasic}.
 *   - Implementa paginación manual mediante LIMIT y OFFSET en consultas SQL.
 *
 * Métodos principales:
 *   list(page, size)         → Devuelve una lista paginada de clientes.
 *   countAll()               → Cuenta el total de clientes en la base de datos.
 *   search(q, page, size)    → Realiza búsquedas filtradas por nombre, correo o tax_id.
 *   countSearch(q)           → Devuelve el número de coincidencias en una búsqueda.
 *   create(...)              → Inserta un nuevo cliente y devuelve el registro creado.
 *   updatePartial(...)       → Actualiza dinámicamente los campos no nulos de un cliente.
 *   delete(id)               → Elimina un cliente existente.
 *   findById(id)             → Busca un cliente por su identificador.
 *
 * Mantenibilidad:
 *   - Ideal para consultas optimizadas o específicas que no requieren el uso de JPA.
 *   - Se puede extender con filtros adicionales o procedimientos almacenados.
 * -----------------------------------------------------------------------------
 */
package com.proyecto.ops.customers.repo;

import com.proyecto.ops.customers.model.CustomerBasic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Repositorio JDBC para acceder y manipular datos de clientes.
 *
 * Utiliza {@link org.springframework.jdbc.core.JdbcTemplate} para ejecutar
 * operaciones SQL directamente sobre la base de datos.
 */
@Repository
public class CustomerJdbcRepository {

    private final JdbcTemplate jdbc;

    /**
     * Constructor que recibe el JdbcTemplate inyectado por Spring.
     *
     * @param jdbc Componente de acceso a base de datos para ejecutar consultas SQL.
     */
    public CustomerJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapeador de filas: convierte los resultados del ResultSet en objetos CustomerBasic.
    private static final RowMapper<CustomerBasic> BASIC_MAPPER = new RowMapper<>() {
        @Override public CustomerBasic mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Crea una instancia de CustomerBasic con los valores obtenidos de la fila actual.
            return new CustomerBasic(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("name"),
                    rs.getString("tax_id"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getObject("created_at", OffsetDateTime.class)
            );
        }
    };

    // ---------- List & Search ----------
    /**
     * Devuelve una lista paginada de clientes ordenados por fecha de creación descendente.
     *
     * @param page Número de página (base 0).
     * @param size Tamaño de la página (número de registros por página).
     * @return Lista de clientes en la página solicitada.
     */
    public List<CustomerBasic> list(int page, int size) {
        // Calcula los valores de límite y desplazamiento (OFFSET) para la consulta SQL.
        int limit = Math.max(size, 1);
        int offset = Math.max(page, 0) * limit;
        String sql = """
            select id, name, tax_id, email, phone, address, created_at
            from app.customers
            order by created_at desc
            limit ? offset ?
        """;
        return jdbc.query(sql, BASIC_MAPPER, limit, offset);
    }

    /**
     * Cuenta el número total de clientes registrados en la base de datos.
     *
     * @return Total de registros en la tabla `app.customers`.
     */
    public long countAll() {
        return Optional.ofNullable(
                jdbc.queryForObject("select count(*) from app.customers", Long.class)
        ).orElse(0L);
    }

    /**
     * Realiza una búsqueda paginada de clientes por nombre, correo electrónico o tax_id.
     *
     * @param q    Término de búsqueda (se usa en cláusulas LIKE).
     * @param page Número de página (base 0).
     * @param size Tamaño de la página.
     * @return Lista de clientes que coinciden con el término de búsqueda.
     */
    public List<CustomerBasic> search(String q, int page, int size) {
        // Genera la expresión LIKE en minúsculas para búsqueda parcial.
        int limit = Math.max(size, 1);
        int offset = Math.max(page, 0) * limit;
        String like = "%" + q.toLowerCase() + "%";
        String sql = """
            select id, name, tax_id, email, phone, address, created_at
            from app.customers
            where lower(name) like ? or lower(coalesce(email,'')) like ? or lower(coalesce(tax_id,'')) like ?
            order by created_at desc
            limit ? offset ?
        """;
        return jdbc.query(sql, BASIC_MAPPER, like, like, like, limit, offset);
    }

    /**
     * Cuenta el número de clientes que coinciden con un término de búsqueda.
     *
     * @param q Término de búsqueda.
     * @return Número de coincidencias encontradas.
     */
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
    /**
     * Inserta un nuevo cliente en la base de datos.
     *
     * @param name  Nombre del cliente.
     * @param taxId Identificador fiscal del cliente.
     * @param email Correo electrónico del cliente.
     * @param phone Teléfono de contacto.
     * @return Objeto CustomerBasic que representa el cliente recién creado.
     */
    public CustomerBasic create(String name, String taxId, String email, String phone, String address) {
        // Genera un nuevo UUID para el cliente a crear.
        UUID id = UUID.randomUUID();
        String sql = """
            insert into app.customers(id, name, tax_id, email, phone, address)
            values(?, ?, ?, ?, ?, ?)
        """;
        jdbc.update(sql, id, name, taxId, email, phone, address);
        return findById(id).orElseThrow();
    }

    /**
     * Actualiza dinámicamente los campos no nulos de un cliente existente.
     *
     * @param id     UUID del cliente a actualizar.
     * @param name   Nuevo nombre (opcional).
     * @param taxId  Nuevo identificador fiscal (opcional).
     * @param email  Nuevo correo electrónico (opcional).
     * @param phone  Nuevo número telefónico (opcional).
     * @return Cliente actualizado o vacío si no se encontró el registro.
     */
    public Optional<CustomerBasic> updatePartial(UUID id, String name, String taxId, String email, String phone, String address) {
        // Construye dinámicamente el SQL para actualizar solo los campos que no sean nulos.
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder("update app.customers set ");
        boolean first = true;

        if (name != null) { sb.append(first ? "" : ", ").append("name=?"); params.add(name); first = false; }
        if (taxId != null) { sb.append(first ? "" : ", ").append("tax_id=?"); params.add(taxId); first = false; }
        if (email != null) { sb.append(first ? "" : ", ").append("email=?"); params.add(email); first = false; }
        if (phone != null) { sb.append(first ? "" : ", ").append("phone=?"); params.add(phone); first = false; }
        if (address != null) { sb.append(first ? "" : ", ").append("address=?"); params.add(address); first = false; }

        if (first) return findById(id); // nada que actualizar

        sb.append(" where id=?");
        params.add(id);

        int updated = jdbc.update(sb.toString(), params.toArray());
        return updated > 0 ? findById(id) : Optional.empty();
    }

    /**
     * Elimina un cliente existente por su UUID.
     *
     * @param id UUID del cliente a eliminar.
     * @return true si el cliente fue eliminado; false en caso contrario.
     */
    public boolean delete(UUID id) {
        return jdbc.update("delete from app.customers where id=?", id) > 0;
    }

    /**
     * Busca un cliente por su identificador único.
     *
     * @param id UUID del cliente.
     * @return Optional con el cliente si existe; vacío si no se encuentra.
     */
    public Optional<CustomerBasic> findById(UUID id) {
        List<CustomerBasic> list = jdbc.query("""
            select id, name, tax_id, email, phone, address, created_at
            from app.customers where id=?
        """, BASIC_MAPPER, id);
        return list.stream().findFirst();
    }
}
