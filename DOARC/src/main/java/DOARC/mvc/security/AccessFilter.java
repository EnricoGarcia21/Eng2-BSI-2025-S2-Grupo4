package DOARC.mvc.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AccessFilter implements Filter {

    // Lista de endpoints públicos que não precisam de autenticação
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/acesso/logar",
        "/api/acesso/registrar",
        "/error"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        String path = req.getRequestURI();
        String method = req.getMethod();
        
        // Verifica se é um endpoint público
        if (isPublicEndpoint(path, method)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Para endpoints protegidos, verifica o token
        String token = extractToken(req);
        
        if (token != null && JWTTokenProvider.verifyToken(token)) {
            // Token válido, permite o acesso
            chain.doFilter(request, response);
        } else {
            // Token inválido ou ausente
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"mensagem\": \"Acesso não autorizado. Token inválido ou expirado.\"}");
        }
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private boolean isPublicEndpoint(String path, String method) {
        // Permite requisições OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        
        // Verifica se o path está na lista de endpoints públicos
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
}