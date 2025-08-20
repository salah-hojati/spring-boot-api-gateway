package com.api_gateway.controller;
import com.api_gateway.dto.ResponseDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.api_gateway.dto.enumDto.EnumResult.FALLBACK_SERVICE;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<ResponseDto> shopServiceFallback() {
        return Mono.just(new ResponseDto(FALLBACK_SERVICE));
    }
}
