package com.tpa.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.kie.api.definition.rule.All;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final int MAX_REQUESTS_PER_MINUTE = 60;
    private final ConcurrentHashMap<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String clientIp = httpServletRequest.getRemoteAddr();
        long currentTime = Instant.now().getEpochSecond();

        requestCounts.compute(clientIp, (key, requestInfo) -> {
            if (requestInfo == null || currentTime - requestInfo.timestamp > 60) {
                return new RequestInfo(1, currentTime);
            }
            requestInfo.count++;
            return requestInfo;
        });

        RequestInfo requestInfo = requestCounts.get(clientIp);

        if (requestInfo.count > MAX_REQUESTS_PER_MINUTE) {
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write("Too many requests. Please try again later.");
            return false;
        }

        return true;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class RequestInfo {
        int count;
        long timestamp;
    }
}
