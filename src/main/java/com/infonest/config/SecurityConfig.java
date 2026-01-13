package com.infonest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Yeh password ko encrypt karke database mein save karne ke kaam aayega
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        
        .authorizeHttpRequests(auth -> auth
            // 1. Auth & Static Pages: Sabke liye open
            // SecurityConfig.java mein ye ensure karein:
            .requestMatchers("/api/v1/auth/**").permitAll() 
            .requestMatchers("/", "/index.html", "/login.html", "/signup.html", "/css/**", "/js/**", "/*.js","/clubdashboard.html" ).permitAll()
             .requestMatchers("/api/v1/events/upcoming", "/api/v1/clubs/all").permitAll()
            // 2. Public Events: Bina login ke events dekhne ke liye
            .requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll()
            // 2. Clubs & Events (Naya: /api/v1/clubs/** tak permit kiya taki details API bhi chale)
            .requestMatchers("/api/v1/events/upcoming", "/api/v1/clubs/**").permitAll()
            // 3. Admin Routes: Sirf ADMIN role ke liye (Spring internally ROLE_ADMIN dhoondta hai)
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            
            // 4. Faculty/Club Routes: Sirf FACULTY ke liye
            .requestMatchers("/api/v1/faculty/**").hasRole("FACULTY")
            
            // 5. Student/Registration: Sabhi authenticated users ke liye
            .requestMatchers("/api/v1/student/**").authenticated()
.requestMatchers("/student_db.html").hasRole("STUDENT")
.requestMatchers("/clubofficialdashboard.html").hasRole("FACULTY")
            .anyRequest().authenticated()
        )
        
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
    return http.build();
}
 @Bean
public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // '*' ki jagah exact URL dein
    configuration.setAllowedOrigins(Arrays.asList("localhost:8081")); 
    
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}   
}