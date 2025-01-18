package io.project.concertbooking.interfaces.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final String ALLOWED_PATTERN = "/api/v1/**";
    private final List<String> EXCLUDE_PATTERNS = List.of(
            "/api/v1/swagger-ui/**",
            "/api/v1/docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        long startTime = System.currentTimeMillis();
        MDC.put("requestId", UUID.randomUUID().toString().substring(0, 8));
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
            logRequest(requestWrapper);
        } finally {
            logResponse(responseWrapper, startTime);
            responseWrapper.copyBodyToResponse();
        }
        MDC.clear();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return !pathMatcher.match(ALLOWED_PATTERN, requestURI)
                || EXCLUDE_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String queryString = request.getQueryString();
        log.info("""
                
                === [REQUEST] ===
                >> Request ID = {}
                >> Method: {}
                >> URL: {}
                >> Headers: {}
                >> Content-Type: {}
                >> {}
                =================
                """,
                MDC.get("requestId"),
                request.getMethod(),
                isNull(queryString) ? request.getRequestURI() : request.getRequestURI() + "?" + queryString,
                getHeaderToString(request),
                request.getContentType(),
                getPayloadToString(request.getContentType(), request.getContentAsByteArray())
        );
    }

    private void logResponse(ContentCachingResponseWrapper response, long startTime) {
        log.info("""
                
                === [RESPONSE] ===
                >> Request ID = {}
                >> Status: {}
                >> Transaction Time = {} ms
                >> {}
                ==================
                """,
                MDC.get("requestId"),
                response.getStatus(),
                System.currentTimeMillis() - startTime,
                getPayloadToString(response.getContentType(), response.getContentAsByteArray())
        );
    }

    private String getPayloadToString(String contentType, byte[] rowData) {
        boolean visible = isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType));

        if (visible) {
            if (rowData.length > 0) {
                return "Payload: %s".formatted(new String(rowData));
            } else {
                return "Payload: null";
            }
        } else {
            return "Payload: Binary Content";
        }
    }

    private String getHeaderToString(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + ": " + request.getHeader(headerName))
                .collect(Collectors.joining(","));
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = Arrays.asList(
                MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"),
                MediaType.MULTIPART_FORM_DATA
        );

        return VISIBLE_TYPES.stream()
                .anyMatch(visibleType -> visibleType.includes(mediaType));
    }
}
