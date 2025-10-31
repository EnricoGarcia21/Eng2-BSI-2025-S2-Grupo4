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

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/apis/acesso/logar",
            "/apis/acesso/registrar",
            "/apis/voluntario",
            "/apis/doadores",
            "/apis/donatarios",
            "/apis/produtos",
            "/apis/categorias",
            "/apis/campanha",
            "/error"
    );


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 🔥 Adiciona os cabeçalhos CORS
        res.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        // 🔁 Libera requisições OPTIONS (pré-flight)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = req.getRequestURI();
        String method = req.getMethod();

        if (isPublicEndpoint(path, method)) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractToken(req);

        if (token != null && JWTTokenProvider.verifyToken(token)) {
            chain.doFilter(request, response);
        } else {
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