package dev.peyman.framework;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.boot.autoconfigure.AutoConfiguration;


@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity // برای استفاده از @PreAuthorize در میکروسرویس‌ها
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "security.module", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService(JwtProperties properties) {
        return new JwtService(properties);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, JwtProperties props, ObjectProvider<UserStatusChecker> statusCheckerProvider, ObjectProvider<RequestSessionTracker> requestSessionTrackers) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint())) // اضافه شد
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // اضافه کردن مسیرهای داینامیک از فایل yml
                    if (props.getPermitAllPaths() != null && !props.getPermitAllPaths().isEmpty()) {
                        auth.requestMatchers(props.getPermitAllPaths().toArray(new String[0])).permitAll();
                    }
                    auth.requestMatchers("/auth/**").permitAll() // مسیرهای پیش‌فرض
                            .anyRequest().authenticated();
                })
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, props, statusCheckerProvider, requestSessionTrackers), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}