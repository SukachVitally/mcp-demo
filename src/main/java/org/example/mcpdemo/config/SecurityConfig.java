package org.example.mcpdemo.config;

import org.springaicommunity.mcp.security.server.apikey.ApiKeyEntityRepository;
import org.springaicommunity.mcp.security.server.apikey.memory.ApiKeyEntityImpl;
import org.springaicommunity.mcp.security.server.apikey.memory.InMemoryApiKeyEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springaicommunity.mcp.security.server.config.McpApiKeyConfigurer.mcpServerApiKey;

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            ApiKeyEntityRepository<ApiKeyEntityImpl> apiKeyRepository) {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .with(mcpServerApiKey(), apiKey -> apiKey.apiKeyRepository(apiKeyRepository))
                .build();
    }

    @Bean
    ApiKeyEntityRepository<ApiKeyEntityImpl> apiKeyRepository(@Value("${app.mcp.api-key.secret}") String secret) {
        var key = ApiKeyEntityImpl.builder()
                .name("demo key")
                .id("demo")
                .secret(secret)
                .build();
        return new InMemoryApiKeyEntityRepository<>(List.of(key));
    }
}
