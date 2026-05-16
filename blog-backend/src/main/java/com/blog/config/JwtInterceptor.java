package com.blog.config;

import com.blog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/admin") && !uri.equals("/api/admin/login")) {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                response.setStatus(401);
                response.getWriter().write("{\"message\":\"Unauthorized\"}");
                return false;
            }
            String token = auth.substring(7);
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(401);
                response.getWriter().write("{\"message\":\"Invalid token\"}");
                return false;
            }
        }
        return true;
    }
}
