package util;

public class DataValidator {
    public static String validateDob(String dob) {
        if (!dob.matches("\\d{4}-\\d{2}-\\d{2}"))
            return "DOB format must be yyyy-MM-dd!";

        String[] parts = dob.split("-");
        int month = Integer.parseInt(parts[1]);
        int day   = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12)
            return "Invalid month!";
        if (day < 1 || day > 31)
            return "Invalid day!";

        return null;
    }

    public static String validateEmail(String email) {
        if (!email.contains("@"))
            return "Invalid email!";
        return null;
    }

    public static String validateNic(String nic) {
        if (nic.length() > 12)
            return "NIC must be 12 characters or less!";
        return null;
    }

    public static String validatePhone(String phone) {
        if (!phone.matches("\\d{10}"))
            return "Phone must be 10 digits!";
        return null;
    }

    public static String validateTime(String time) {
        if (!time.matches("\\d{2}:\\d{2}:\\d{2}"))
            return "Time format must be HH:mm:ss!";

        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        if (hours < 0 || hours > 23)
            return "Invalid hours (00-23)!";
        if (minutes < 0 || minutes > 59)
            return "Invalid minutes (00-59)!";
        if (seconds < 0 || seconds > 59)
            return "Invalid seconds (00-59)!";

        return null;
    }
}