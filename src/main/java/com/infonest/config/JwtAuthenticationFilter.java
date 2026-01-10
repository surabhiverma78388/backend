package com.infonest.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Auth endpoints (Login/Signup) ko filter se bahar rakhein
      String path = request.getServletPath();

if (
    path.equals("/") || 
    path.endsWith(".html") || 
    path.endsWith(".js") ||   // ".startsWith" ko ".endsWith" karein
    path.endsWith(".css") ||  // ".startsWith" ko ".endsWith" karein
    path.startsWith("/api/v1/auth") || 
    path.startsWith("/api/v1/clubs") || 
    path.startsWith("/js/") || 
    path.startsWith("/css/")
) {
    filterChain.doFilter(request, response);
    return;
}
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // 2. Authorization header se Token extract karein
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtUtils.extractEmail(token);
            } catch (Exception e) {
                logger.error("Token se email nahi nikal paye: " + e.getMessage());
            }
        }

        // 3. Email validate karein aur Security Context check karein
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtils.validateToken(token)) {
                
                // 4. Token se Role extract karein
                String role = jwtUtils.extractRole(token);
                
                // 5. Spring Security ke liye ROLE_ prefix lagana zaroori hai
                // hasRole('ADMIN') internally 'ROLE_ADMIN' dhoondta hai
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, 
                        null, 
                        Collections.singletonList(authority)
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 6. Security Context mein user ko set karein
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 7. Request ko aage bhej dein
        filterChain.doFilter(request, response);
    }
}