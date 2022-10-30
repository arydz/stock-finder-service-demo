package com.arydz.stockfinder.domain.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();
    String message;
}
