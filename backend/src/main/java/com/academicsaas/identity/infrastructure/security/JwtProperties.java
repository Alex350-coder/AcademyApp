package com.academicsaas.identity.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpirationMs = 15 * 60 * 1000;
    private long refreshTokenExpirationMs = 7 * 24 * 60 * 60 * 1000;
    private String issuer = "academic-saas";

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getAccessTokenExpirationMs() { return accessTokenExpirationMs; }
    public void setAccessTokenExpirationMs(long accessTokenExpirationMs) { this.accessTokenExpirationMs = accessTokenExpirationMs; }
    public long getRefreshTokenExpirationMs() { return refreshTokenExpirationMs; }
    public void setRefreshTokenExpirationMs(long refreshTokenExpirationMs) { this.refreshTokenExpirationMs = refreshTokenExpirationMs; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
}
