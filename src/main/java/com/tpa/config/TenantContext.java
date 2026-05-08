package com.tpa.config;

/**
 * ThreadLocal holder for the current tenant context.
 * Set by TenantFilter on each incoming request; cleared at request end.
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new InheritableThreadLocal<>();

    /** Default tenant slug used when no X-Tenant-ID header is present. */
    public static final String DEFAULT_TENANT = "default";

    private TenantContext() {}

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId != null ? tenantId : DEFAULT_TENANT);
    }

    public static String getTenantId() {
        String tenant = CURRENT_TENANT.get();
        return tenant != null ? tenant : DEFAULT_TENANT;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
