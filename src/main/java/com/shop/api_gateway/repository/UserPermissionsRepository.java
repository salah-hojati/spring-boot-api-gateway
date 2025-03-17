package com.shop.api_gateway.repository;

import com.shop.api_gateway.entity.permissionEnt.UserPermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserPermissionsRepository extends JpaRepository<UserPermissionsEntity, Long> {

    List<UserPermissionsEntity> findByUserId(UUID userId);

    List<UserPermissionsEntity> findByUserIdAndServiceId(UUID userId, Long serviceId);
}