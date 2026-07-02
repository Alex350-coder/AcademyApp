package com.academicsaas.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.email")
public class ResendProperties {

    private String provider = "log";
    private String fromEmail = "noreply@academiasaas.com";
    private String apiKey = "";

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
