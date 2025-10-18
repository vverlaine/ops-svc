package com.proyecto.ops.sites.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.ops.sites.model.CustomerSite;

public interface CustomerSiteRepository extends JpaRepository<CustomerSite, UUID> {
    Page<CustomerSite> findByCustomerId(UUID customerId, Pageable pageable);
}