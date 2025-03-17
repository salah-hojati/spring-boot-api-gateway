package com.shop.api_gateway.repository;

import com.shop.api_gateway.entity.permissionEnt.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    PermissionEntity findByName(String name);
}