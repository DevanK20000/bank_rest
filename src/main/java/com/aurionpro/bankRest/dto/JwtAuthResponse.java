package com.aurionpro.bankRest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
