package DOARC.mvc.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class VoluntarioFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Não filtrar se não for rota de voluntário ou se for OPTIONS (CORS)
        boolean shouldNotFilter = !path.startsWith("/apis/voluntario") || "OPTIONS".equalsIgnoreCase(method);

        System.out.println("🔍 VoluntarioFilter - Path: " + path + " | Method: " + method + " | ShouldNotFilter: " + shouldNotFilter);

        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("🔒 VoluntarioFilter - Verificando acesso de voluntário");

        // Verificar se o usuário foi autenticado pelo JwtFilter
        Object authenticatedObj = request.getAttribute("authenticated");
        if (authenticatedObj == null || !Boolean.TRUE.equals(authenticatedObj)) {
            System.err.println("❌ VoluntarioFilter - Usuário não autenticado");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Autenticação necessária\",\"codigo\":\"AUTH_REQUIRED\"}");
            return;
        }

        // Verificar se o usuário tem role de USER ou ADMIN (voluntários podem ser USER ou ADMIN)
        Object roleObj = request.getAttribute("role");
        String role = roleObj != null ? roleObj.toString() : null;
        String email = (String) request.getAttribute("email");

        System.out.println("🔍 VoluntarioFilter - Email: " + email + " | Role: " + role);

        if (role == null || (!role.equalsIgnoreCase("USER") && !role.equalsIgnoreCase("ADMIN"))) {
            System.err.println("❌ VoluntarioFilter - Acesso negado. Role necessária: USER ou ADMIN, Role atual: " + role);

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Acesso restrito a voluntários\",\"codigo\":\"VOLUNTARIO_REQUIRED\",\"roleAtual\":\"" + role + "\"}");
            return;
        }

        System.out.println("✅ VoluntarioFilter - Acesso de voluntário autorizado para: " + email);
        filterChain.doFilter(request, response);
    }
}