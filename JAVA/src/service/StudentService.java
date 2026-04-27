package service;

import dao.StudentDAO;
import models.Undergraduate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class StudentService {
    private final StudentDAO dao = new StudentDAO();

    // ── Authentication ─────────────────────────────────────────────────────
    public Undergraduate login(String userId, String password) {
        if (userId == null || userId.isBlank() || password == null || password.isBlank())
            return null;
        return dao.login(userId.trim(), password);
    }

    // ── Profile ────────────────────────────────────────────────────────────
    public Undergraduate getProfile(String ugId) {
        return dao.getById(ugId);
    }

    public List<String> getPhones(String ugId) {
        return dao.getPhones(ugId);
    }

    // ── Profile Picture ────────────────────────────────────────────────────
    /**
     * Copies the chosen image file into src/resources/userPP/<userId>.<ext>,
     * updates the USER table, and updates the in-memory student object.
     *
     * @param student   the logged-in Undergraduate (updated in place)
     * @param chosenFile the image file the user selected via JFileChooser
     * @return true on success, false on any failure
     */
    public boolean changeProfilePic(Undergraduate student, File chosenFile) {
        if (student == null || chosenFile == null || !chosenFile.exists()) return false;

        // Determine extension (.png / .jpg / .jpeg)
        String originalName = chosenFile.getName();
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) ext = originalName.substring(dot); // e.g. ".png"

        // Destination: src/resources/userPP/<userId><ext>
        String destRelPath = "src/resources/userPP/" + student.getUgId() + ext;
        Path destPath = Paths.get(destRelPath);

        try {
            // Make sure the directory exists
            Files.createDirectories(destPath.getParent());
            // Copy file (overwrite if already exists)
            Files.copy(chosenFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Save path to DB
        boolean saved = dao.updateProfilePic(student.getUgId(), destRelPath);
        if (saved) {
            // Keep in-memory model in sync so the UI can refresh immediately
            student.setProfilePic(destRelPath);
        }
        return saved;
    }

    /**
     * Loads the stored profile pic path from the database.
     * Returns null if none is set.
     */
    public String getProfilePicPath(String ugId) {
        return dao.getProfilePic(ugId);
    }

    // ── Dashboard counts ───────────────────────────────────────────────────
    public int getCourseCount(String ugId)   { return dao.getCourseCount(ugId); }
    public int getUnreadNoticeCount()        { return dao.getUnreadNoticeCount(); }
}
