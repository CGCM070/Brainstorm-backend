package org.brainstorm.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.brainstorm.service.SessionTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor  implements HandlerInterceptor {

    @Autowired
    private SessionTokenService sessionTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequiresAuth requiresAuth = handlerMethod.getMethodAnnotation(RequiresAuth.class);

            if (requiresAuth != null) {
                Long userId = extraerUserId(request);
                String token = request.getHeader("User-Token");

                if (!sessionTokenService.validateToken(userId, token)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    private Long extraerUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("User-Id");
        if (userIdHeader != null) {
            return Long.parseLong(userIdHeader);
        }

        // Alternativa: extraer de la URL para endpoints de usuario
        String path = request.getRequestURI();
        if (path.matches("/v1/api/users/\\d+.*")) {
            String[] segments = path.split("/");
            return Long.parseLong(segments[4]); // Formato: /v1/api/users/{id}
        }

        return null;
    }

}
