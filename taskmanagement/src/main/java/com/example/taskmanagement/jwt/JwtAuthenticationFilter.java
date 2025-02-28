package com.example.taskmanagement.jwt;

import com.example.taskmanagement.security.UserDetailedService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final UserDetailedService userDetailedService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");


        if (request.getRequestURI().startsWith("/swagger-ui") ||
                request.getRequestURI().startsWith("/v3/api-docs") ||
                request.getRequestURI().startsWith("/swagger-resources")) {

            if (!response.isCommitted()) {
                try {
                    log.info("Before doFilter");
                    filterChain.doFilter(request, response);
                    log.info("After doFilter");
                } catch (Exception e) {
                    log.error("Error in filter chain", e);
                    throw e;
                }
                return;
            }
        }


        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (token.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unauthorized: Token is missing");
                    return;
                }

                if (jwtUtils.isTokenValid(token)) {
                    Claims claims = jwtUtils.extractAllClaims(token);
                    String email = claims.getSubject();

                    UserDetails userDetails = userDetailedService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("User authenticated: " + email);

                }
            }
        } catch (Exception e) {
            log.error("Authentication error: ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }


}