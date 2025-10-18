package com.proyecto.ops.technicians.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.ops.technicians.model.Technician;

public interface TechnicianRepository extends JpaRepository<Technician, UUID> {

  @Query(
    value = """
            select *
            from app.technicians t
            where (:active is null or t.active = :active)
              and (:skill  is null or :skill = any (t.skills))
            order by t.created_at desc
            """,
    countQuery = """
            select count(*)
            from app.technicians t
            where (:active is null or t.active = :active)
              and (:skill  is null or :skill = any (t.skills))
            """,
    nativeQuery = true
  )
  Page<Technician> search(
      @Param("active") Boolean active,
      @Param("skill")  String skill,
      Pageable pageable
  );
}