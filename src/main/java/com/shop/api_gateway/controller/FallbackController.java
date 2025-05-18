package com.shop.api_gateway.controller;
import com.shop.api_gateway.dto.ErrResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.shop.api_gateway.dto.enumDto.EnumResult.FALLBACK_SERVICE;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/shop")
    public Mono<ErrResponseDto> shopServiceFallback() {
        return Mono.just(new ErrResponseDto(FALLBACK_SERVICE));
    }
}
