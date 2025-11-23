package DOARC.mvc.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Utilitário de validação para dados de entrada
 * Fornece validações seguras e completas para CPF, email, telefone, etc.
 */
public class ValidationUtil {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\d{10,11}$"
    );

    private static final Pattern CEP_PATTERN = Pattern.compile(
            "^\\d{8}$"
    );

    private static final Pattern UF_PATTERN = Pattern.compile(
            "^(AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO)$"
    );

    /**
     * Valida se uma string não é nula e não está vazia
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Valida formato de email
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida CPF com algoritmo de dígito verificador
     */
    public static boolean isValidCPF(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove caracteres não numéricos
        String cleanCPF = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cleanCPF.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cleanCPF.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cleanCPF.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }

        // Verifica primeiro dígito
        if (firstDigit != Character.getNumericValue(cleanCPF.charAt(9))) {
            return false;
        }

        // Calcula segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cleanCPF.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }

        // Verifica segundo dígito
        return secondDigit == Character.getNumericValue(cleanCPF.charAt(10));
    }

    /**
     * Valida telefone (10 ou 11 dígitos após limpeza)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        String cleanPhone = phone.replaceAll("[^0-9]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Valida CEP (8 dígitos após limpeza)
     */
    public static boolean isValidCEP(String cep) {
        if (cep == null) {
            return false;
        }

        String cleanCEP = cep.replaceAll("[^0-9]", "");
        return CEP_PATTERN.matcher(cleanCEP).matches();
    }

    /**
     * Valida UF (sigla do estado)
     */
    public static boolean isValidUF(String uf) {
        if (uf == null) {
            return false;
        }

        return UF_PATTERN.matcher(uf.toUpperCase()).matches();
    }

    /**
     * Valida data de nascimento (não pode ser futura, pessoa deve ter idade mínima)
     */
    public static boolean isValidBirthDate(LocalDate birthDate, int minAge) {
        if (birthDate == null) {
            return false;
        }

        LocalDate now = LocalDate.now();

        // Não pode ser data futura
        if (birthDate.isAfter(now)) {
            return false;
        }

        // Verifica idade mínima
        int age = Period.between(birthDate, now).getYears();
        return age >= minAge;
    }

    /**
     * Valida senha (mínimo de caracteres, complexidade)
     */
    public static boolean isValidPassword(String password, int minLength) {
        if (password == null || password.length() < minLength) {
            return false;
        }

        // Opcional: adicionar validação de complexidade
        // - Pelo menos uma letra maiúscula
        // - Pelo menos uma letra minúscula
        // - Pelo menos um número
        // boolean hasUpper = password.matches(".*[A-Z].*");
        // boolean hasLower = password.matches(".*[a-z].*");
        // boolean hasDigit = password.matches(".*\\d.*");
        // return hasUpper && hasLower && hasDigit;

        return true;
    }

    /**
     * Valida nome (mínimo e máximo de caracteres, apenas letras e espaços)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String trimmed = name.trim();

        // Nome deve ter entre 2 e 100 caracteres
        if (trimmed.length() < 2 || trimmed.length() > 100) {
            return false;
        }

        // Nome deve conter apenas letras, espaços e alguns caracteres especiais
        return trimmed.matches("^[A-Za-zÀ-ÿ\\s'-]+$");
    }

    /**
     * Valida sexo/gênero
     */
    public static boolean isValidSexo(String sexo) {
        if (sexo == null || sexo.trim().isEmpty()) {
            return false;
        }

        String upper = sexo.trim().toUpperCase();
        return upper.equals("M") || upper.equals("F") || upper.equals("O") ||
               upper.equals("MASCULINO") || upper.equals("FEMININO") || upper.equals("OUTRO") ||
               upper.equals("NÃO INFORMADO");
    }

    /**
     * Normaliza sexo para formato padrão (M, F, O)
     */
    public static String normalizeSexo(String sexo) {
        if (sexo == null || sexo.trim().isEmpty()) {
            return "O";
        }

        String upper = sexo.trim().toUpperCase();

        if (upper.equals("M") || upper.equals("MASCULINO")) {
            return "M";
        } else if (upper.equals("F") || upper.equals("FEMININO")) {
            return "F";
        } else {
            return "O";
        }
    }

    /**
     * Limpa e normaliza CPF (remove formatação)
     */
    public static String cleanCPF(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Limpa e normaliza telefone (remove formatação)
     */
    public static String cleanPhone(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }

    /**
     * Limpa e normaliza CEP (remove formatação)
     */
    public static String cleanCEP(String cep) {
        if (cep == null) {
            return null;
        }
        return cep.replaceAll("[^0-9]", "");
    }

    /**
     * Formata CPF para exibição (xxx.xxx.xxx-xx)
     */
    public static String formatCPF(String cpf) {
        String clean = cleanCPF(cpf);
        if (clean == null || clean.length() != 11) {
            return cpf;
        }
        return clean.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Formata telefone para exibição
     */
    public static String formatPhone(String phone) {
        String clean = cleanPhone(phone);
        if (clean == null) {
            return phone;
        }

        if (clean.length() == 11) {
            return clean.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (clean.length() == 10) {
            return clean.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }

        return phone;
    }

    /**
     * Formata CEP para exibição (xxxxx-xxx)
     */
    public static String formatCEP(String cep) {
        String clean = cleanCEP(cep);
        if (clean == null || clean.length() != 8) {
            return cep;
        }
        return clean.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }

    /**
     * Sanitiza string removendo caracteres perigosos
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }

        // Remove caracteres potencialmente perigosos
        return input.trim()
                .replaceAll("[<>\"']", "")
                .replaceAll("\\s+", " ");
    }

    /**
     * Valida comprimento de string
     */
    public static boolean hasValidLength(String value, int min, int max) {
        if (value == null) {
            return false;
        }

        int length = value.trim().length();
        return length >= min && length <= max;
    }
}
