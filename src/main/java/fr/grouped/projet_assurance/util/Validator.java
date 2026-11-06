package fr.grouped.projet_assurance.util;



import java.util.regex.Pattern;

public final class Validator {

    private static final Pattern EMAIL =
        Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern TELEPHONE =
        Pattern.compile("^[0-9+ .-]{8,20}$");

    private Validator() {}

    public static boolean isEmailValide(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    public static boolean isTelephoneValide(String tel) {
        return tel == null || TELEPHONE.matcher(tel).matches();
    }

    public static boolean isNonVide(String s) {
        return s != null && !s.isBlank();
    }

    public static boolean isMontantPositif(double m) {
        return m > 0;
    }

    public static boolean isMotDePasseValide(String mdp) {
        return mdp != null && mdp.length() >= 6;
    }
}