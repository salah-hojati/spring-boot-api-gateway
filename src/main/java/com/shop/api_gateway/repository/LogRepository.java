package com.shop.api_gateway.repository;

import com.shop.api_gateway.entity.permissionEnt.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
}
