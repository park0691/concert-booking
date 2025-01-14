package io.project.concertbooking.interfaces.api.interceptor;

import io.project.concertbooking.common.annotation.TokenRequired;
import io.project.concertbooking.domain.queue.QueueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class QueueInterceptor implements HandlerInterceptor {

    private final QueueService queueService;

    @Value("${queue.request-header-key}")
    private String queueTokenHeader;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        TokenRequired tokenRequired = handlerMethod.getMethod().getAnnotation(TokenRequired.class);
        if (isNull(tokenRequired)) {
            return true;
        }

        String token = request.getHeader(queueTokenHeader);
        if (isBlank(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, queueTokenHeader + " header is missing.");
            return false;
        }

        queueService.validateToken(token);
        request.setAttribute(queueTokenHeader, token);

        return true;
    }
}
