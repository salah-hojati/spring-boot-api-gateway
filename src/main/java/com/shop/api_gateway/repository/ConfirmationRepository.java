package com.shop.api_gateway.repository;

import com.shop.api_gateway.entity.profile.ConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {

    Optional<ConfirmationEntity> findByKey(String key);

}
