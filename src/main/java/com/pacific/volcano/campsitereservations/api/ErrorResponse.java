package com.pacific.volcano.campsitereservations.api;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ErrorResponse {
    HttpStatus status;
    String message;
    List<String> errors;
}
