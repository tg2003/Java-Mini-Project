package util;

public class PasswordUtils {
    /** Plain comparison – in production replace with BCrypt. */
    public static boolean check(String plain, String stored) {
        return plain != null && plain.equals(stored);
    }
}
