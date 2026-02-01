package dev.peyman.framework;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "custom.security.jwt")
public class JwtProperties {
    private String secret = "default-very-long-secret-key-for-dev-only-32chars";
    private long expiration = 86400000;
    private String header = "Authorization";
    private String prefix = "Bearer ";

    private List<String> permitAllPaths = new ArrayList<>();

    // Getter and Setter
    public List<String> getPermitAllPaths() { return permitAllPaths; }
    public void setPermitAllPaths(List<String> permitAllPaths) { this.permitAllPaths = permitAllPaths; }

    // Getters & Setters
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getExpiration() { return expiration; }
    public void setExpiration(long expiration) { this.expiration = expiration; }
    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
}