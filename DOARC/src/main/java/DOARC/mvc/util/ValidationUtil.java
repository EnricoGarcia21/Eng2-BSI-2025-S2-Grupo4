package DOARC.mvc.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static String cleanCPF(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("\\D", "");
    }

    public static String cleanPhone(String phone) {
        if (phone == null) return "";
        // 1. Remove tudo que não for número
        String cleaned = phone.replaceAll("\\D", "");

        // CORREÇÃO FINAL: Limitar a 12 caracteres para CASAR com o erro "VARCHAR(12)" do banco.
        if (cleaned.length() > 12) {
            return cleaned.substring(0, 12);
        }
        return cleaned;
    }

    public static String cleanCEP(String cep) {
        if (cep == null) return "";
        return cep.replaceAll("\\D", "");
    }

    public static String normalizeSexo(String sexo) {
        if (sexo == null || sexo.trim().isEmpty()) return "O";
        String s = sexo.trim().toUpperCase();
        if (s.startsWith("M")) return "M";
        if (s.startsWith("F")) return "F";
        return "O";
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 3;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password, int minLength) {
        return password != null && password.length() >= minLength;
    }

    public static boolean isValidPhone(String phone) {
        String p = cleanPhone(phone);
        // Ajustado para aceitar até 12 caracteres (máximo que o banco suporta atualmente)
        return p.length() >= 10 && p.length() <= 12;
    }

    public static String formatCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static String formatPhone(String phone) {
        if (phone == null) return "";
        String p = cleanPhone(phone);
        if (p.length() == 11) return p.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        if (p.length() == 10) return p.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        // Retorna o valor limpo se a formatação BR não se aplicar (como o caso de 12 dígitos ou DDI)
        return p;
    }
}