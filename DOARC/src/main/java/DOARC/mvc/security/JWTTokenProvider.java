package DOARC.mvc.security;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JWTTokenProvider {
    private static final SecretKey CHAVE = Keys.hmacShaKeyFor(
            "DOARC_CHAVE_SECRETA_DOARC_CHAVE_SECRETA".getBytes(StandardCharsets.UTF_8));

    // Token com mais informações relevantes para o DOARC
    public static String getToken(String login, String nivelAcesso, String voluntarioId) 
    {       
        String jwtToken = Jwts.builder()
            .setSubject(login)
            .setIssuer("doarc-system")
            .claim("nivelAcesso", nivelAcesso)
            .claim("voluntarioId", voluntarioId)
            .claim("sistema", "DOARC")
            .setIssuedAt(new Date())
            .setExpiration(Date.from(LocalDateTime.now().plusHours(8L) // 8 horas de expiração
                .atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(CHAVE)
            .compact();
        return jwtToken;        
    }

    public static boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(CHAVE)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("Token inválido: " + e.getMessage());
        }
        return false;       
    }

    public static Claims getAllClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                .setSigningKey(CHAVE)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            System.out.println("Erro ao recuperar claims do token: " + e.getMessage());
        }
        return claims;        
    }

    // Métodos auxiliares para obter informações específicas do token
    public static String getLoginFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }
    
    public static String getNivelAcessoFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? (String) claims.get("nivelAcesso") : null;
    }
    
    public static String getVoluntarioIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims != null ? (String) claims.get("voluntarioId") : null;
    }
}