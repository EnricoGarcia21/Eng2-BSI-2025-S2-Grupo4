package DOARC.mvc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // Rotas que não precisam de autenticação
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/apis/acesso/logar",
            "/apis/acesso/registrar",
            "/apis/acesso/registrar-admin",
            // CORREÇÃO: Libera a verificação de CPF para o cadastro
            "/apis/acesso/verificar-cpf",
            "/error",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Log para debug
        System.out.println("🔍 JwtFilter - Path: " + path + " | Method: " + method);

        // Adicionar CORS headers manualmente
        String origin = request.getHeader("Origin");
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        // Permitir OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Jws<Claims> claims = jwtUtil.validarToken(token);
                String email = claims.getBody().getSubject();
                String role = claims.getBody().get("role", String.class);

                // Adicionar informações do usuário na request
                request.setAttribute("email", email);
                request.setAttribute("role", role);
                request.setAttribute("authenticated", true);

            } catch (JwtException e) {
                System.err.println("❌ Token inválido: " + e.getMessage());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"erro\":\"Token inválido ou expirado\",\"codigo\":\"TOKEN_INVALID\"}");
                return;
            }
        } else {
            // Não há token - verificar se a rota precisa de autenticação
            if (!isPublicPath(path)) {
                System.err.println("❌ Token ausente para rota protegida: " + path);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"erro\":\"Token de autenticação necessário\",\"codigo\":\"TOKEN_REQUIRED\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return isPublicPath(path);
    }

    private boolean isPublicPath(String path) {
        // CORREÇÃO: 'startsWith' garante que /apis/acesso/verificar-cpf/123456... seja aceito
        return PUBLIC_PATHS.stream().anyMatch(publicPath ->
                path.startsWith(publicPath) || path.equals(publicPath)
        );
    }
}