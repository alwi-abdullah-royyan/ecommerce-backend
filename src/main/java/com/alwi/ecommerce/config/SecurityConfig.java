package com.alwi.ecommerce.config;

import com.alwi.ecommerce.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final UserService userService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserService userService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Swagger and API Docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-ui/index.html").permitAll()

                        // User and Authentication
                        .requestMatchers(HttpMethod.POST, "/api/user/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user/all", "/api/user/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/user/{id}").authenticated()

                        // Product Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/product/all", "/api/product/{id}",
                                "/api/product/image/{id}", "/api/product/filter").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/product/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/product/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/product/add").hasRole("ADMIN")

                        // Cart Endpoints
                        .requestMatchers(HttpMethod.PUT, "/api/cart/check/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/cart/manage").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/cart/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/{id}").authenticated()
                        //ORDER
                        .requestMatchers(HttpMethod.PUT, "/api/order/update/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/order/status_can_change_to/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/order/detail/{id}",
                                "/api/order/create","/api/order/user/filter").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/order/all","/api/oder/filter").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/order/delete/{id}").hasRole("ADMIN")
                        //ORDER ITEM
                        .requestMatchers(HttpMethod.GET, "/api/order_item/{id}").authenticated()
                        //order history
                        .requestMatchers(HttpMethod.GET, "/api/order_history/{id}").authenticated()
                        //category
                        .requestMatchers(HttpMethod.GET, "/api/category/all","/api/category/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/category").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/category/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/category/{id}").hasRole("ADMIN")
                        // Default rule
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000")); // Restrict to localhost:3000
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailService() {
        return userService::loadUserByUsername;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
