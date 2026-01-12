package com.app.dhcp.security;

import com.app.dhcp.Jwt.JwtAuthenticationFilter;
import com.app.dhcp.Jwt.JwtAuthorizationFilter;
import com.app.dhcp.Jwt.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration  {

    private final JwtUtil jwtUtil;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;


    @Autowired
    public SecurityConfiguration(JwtUtil jwtUtil, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtUtil = jwtUtil;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
        JwtAuthenticationFilter authFilter = new JwtAuthenticationFilter(jwtUtil);
        authFilter.setAuthenticationManager(authenticationManager);
        authFilter.setFilterProcessesUrl("/login");

        // Disable CSRF
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        // Configure session management
        httpSecurity.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Function to configure CORS
        corsConfig(httpSecurity);
        // Function to handle requests
        authorizedRequests(httpSecurity);
        // Function to adding filters
        httpSecurity
                .addFilter(authFilter)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    //Cors configuration
    private void corsConfig (HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors( cors -> cors.configurationSource(request -> {
            CorsConfiguration cfg = new CorsConfiguration();
            cfg.setAllowedOriginPatterns(List.of("*")); // especifica tu origen en prod
            cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
            cfg.setAllowedHeaders(List.of("*"));
            cfg.setAllowCredentials(true);
            cfg.setExposedHeaders(List.of(
                    "Content-Disposition",
                    "X-Total-Count",
                    "Authorization"
            ));
            return cfg;
        }));
    }

    //Rules for protect endpoints
    private void authorizedRequests(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
                );
    }
}
