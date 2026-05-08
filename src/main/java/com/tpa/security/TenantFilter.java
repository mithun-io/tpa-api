package com.tpa.security;

import com.tpa.config.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts the tenant ID from the X-Tenant-ID HTTP header and stores it
 * in TenantContext (ThreadLocal). Falls back to "default" if header is absent.
 *
 * Must run BEFORE JwtAuthFilter so that tenant-aware services can use it.
 */
@Slf4j
@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String tenantId = request.getHeader(TENANT_HEADER);
            if (tenantId == null || tenantId.isBlank()) {
                tenantId = TenantContext.DEFAULT_TENANT;
            }
            TenantContext.setTenantId(tenantId);
            log.debug("[TENANT] Request from tenant='{}' path='{}'", tenantId, request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear(); // Always clear to prevent thread pool contamination
        }
    }
}
