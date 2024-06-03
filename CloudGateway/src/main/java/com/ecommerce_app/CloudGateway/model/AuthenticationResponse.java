package com.ecommerce_app.CloudGateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Long expiresAt;
    private Collection<String> authorityList;
}
