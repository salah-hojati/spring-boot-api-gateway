package com.shop.api_gateway.repository;

import com.shop.api_gateway.entity.permission.UserServicePermissionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionsRepository extends JpaRepository<UserServicePermissionEntity, Long> {


    @Query("SELECT u FROM UserServicePermissionEntity u " +
            "WHERE u.user.username = :username "+
            "AND u.endDate >= CURRENT_DATE")
    List<UserServicePermissionEntity> findValidPermissionByUsername(@Param("username") String username);


}