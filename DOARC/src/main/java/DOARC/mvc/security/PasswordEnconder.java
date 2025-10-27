package DOARC.mvc.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {
    
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public String encode(String senha) {
        return encoder.encode(senha);
    }
    
    public boolean matches(String senha, String senhaHasheada) {
        return encoder.matches(senha, senhaHasheada);
    }
}