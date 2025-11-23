package DOARC.mvc.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    private PasswordEncoder() {}

    /**
     * Criptografa a senha usando BCrypt
     * @param raw Senha em texto plano
     * @return Hash BCrypt da senha
     */
    public static String encode(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Senha não pode ser null");
        }
        return encoder.encode(raw);
    }

    /**
     * Alias para encode() - mantido para compatibilidade
     */
    public static String hash(String raw) {
        return encode(raw);
    }

    /**
     * Verifica se a senha em texto plano corresponde ao hash
     * @param raw Senha em texto plano
     * @param hashed Hash BCrypt armazenado
     * @return true se a senha corresponde
     */
    public static boolean matches(String raw, String hashed) {
        if (raw == null || hashed == null) {
            return false;
        }
        return encoder.matches(raw, hashed);
    }
}