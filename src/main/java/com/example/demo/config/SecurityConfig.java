package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Şifreleri hash'lemek için kullanılan bean
    // UserService'teki @Autowired PasswordEncoder bunu bulacak
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF'i kapatıyoruz çünkü REST API + frontend ayrı çalışacak
            // (Eğer ileride form-based bir şey eklersen bunu tekrar düşünmemiz gerekir)
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Session tabanlı: kullanıcı login olunca session oluşturulur
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            .authorizeHttpRequests(auth -> auth
                // Kayıt ve login herkese açık olmalı
                .requestMatchers("/api/auth/**").permitAll()
                // Ürün/kategori listeleme herkese açık (müşteri login olmadan da gezebilsin)
                .requestMatchers("/api/products/**", "/api/categories/**").permitAll()
                // Admin işlemleri sadece ADMIN rolü
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Diğer her şey (sepet, sipariş) giriş yapmış kullanıcı gerektirir
                .anyRequest().authenticated()
            )

            // Basit session-login: Spring'in varsayılan form login mekanizmasını
            // REST API olarak kullanacağız (JSON login endpoint'i Controller'da yazacağız)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // Frontend'in (örn. React, localhost:3000 gibi) bu API'ye istek atabilmesi için
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // session cookie'sinin gidip gelmesi için ZORUNLU

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}