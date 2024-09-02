package com.aurionpro.bankRest.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationErrorResponse {
    private String field;
    private String message;
}