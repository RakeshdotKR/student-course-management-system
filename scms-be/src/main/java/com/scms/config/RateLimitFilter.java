package com.scms.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Rate limiting filter - restricts how many requests a single IP can make.
// Runs before the JWT filter, so even unauthenticated requests (e.g. login attempts) are throttled.
@Component
@Order(1) // ensures this filter runs first, before JwtAuthFilter
public class RateLimitFilter extends OncePerRequestFilter {

    // One bucket per IP address for general API usage
    private final ConcurrentMap<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    // One bucket per IP address specifically for auth endpoints (stricter, prevents brute-forcing)
    private final ConcurrentMap<String, Bucket> authBuckets = new ConcurrentHashMap<>();

    // General limit: 60 requests per minute per IP
    private Bucket newGeneralBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(60)
                .refillGreedy(60, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    // Auth limit: 5 requests per minute per IP (login/register are sensitive - brute force target)
    private Bucket newAuthBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    // Extracts the real client IP, accounting for reverse proxies (Railway/Nginx/Vercel)
    // which forward the original IP via the X-Forwarded-For header.
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim(); // first IP in the list is the original client
        }
        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String ip = getClientIp(request);
        boolean isAuthEndpoint = request.getRequestURI().startsWith("/api/auth/");

        ConcurrentMap<String, Bucket> targetMap = isAuthEndpoint ? authBuckets : generalBuckets;
        Bucket bucket = targetMap.computeIfAbsent(ip, key -> isAuthEndpoint ? newAuthBucket() : newGeneralBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response); // token available, allow request through
        } else {
            response.setStatus(429); // 429 Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"status\":429,\"message\":\"Too many requests. Please slow down and try again shortly.\"}"
            );
        }
    }
}