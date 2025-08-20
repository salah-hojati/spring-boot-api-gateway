package com.api_gateway.dto;

import com.api_gateway.dto.enumDto.EventType;
import com.api_gateway.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEventDto {
    private UserEntity user;
    private EventType type;
    private Map<?,?> data;
}

