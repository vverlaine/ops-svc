package com.proyecto.ops.assets.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.ops.assets.model.Asset;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    Page<Asset> findByCustomerId(UUID customerId, Pageable pageable);
}