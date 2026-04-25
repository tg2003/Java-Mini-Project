package models;

/**
 * CourseMaterial model class.
 * Maps to the COURSE_MATERIAL table in LMS_DB.
 *
 * DB columns mapped:
 *   C_code    -> cCode        (composite PK part 1, FK → COURSE)
 *   Material  -> materialPath (composite PK part 2, file path or URL)
 *
 * A course can have MANY materials (one-to-many from COURSE → COURSE_MATERIAL).
 * The lecturer can upload new materials and view existing ones for a course.
 */
public class CourseMaterial {

    // ── DB fields ──────────────────────────────────────────────────────────
    private String cCode;           // FK → COURSE(C_code), e.g. "CS1001"
    private String materialPath;    // file path stored in DB, e.g. "resources/cs1001/lecture1.pdf"

    // ── Convenience fields (not in DB, derived for display in GUI) ─────────
    private String fileName;        // e.g. "lecture1.pdf" (extracted from path)
    private String fileType;        // e.g. "pdf", "pptx", "docx" (extracted from extension)

    // ── Constructor ────────────────────────────────────────────────────────
    public CourseMaterial(String cCode, String materialPath) {
        this.cCode        = cCode;
        this.materialPath = materialPath;
        // Automatically extract fileName and fileType from path
        this.fileName  = extractFileName(materialPath);
        this.fileType  = extractFileType(materialPath);
    }

    // ── Private helper: extract file name from full path ───────────────────
    // e.g. "resources/cs1001/lecture1.pdf" → "lecture1.pdf"
    private String extractFileName(String path) {
        if (path == null || path.isBlank()) return "";
        int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return (lastSlash >= 0) ? path.substring(lastSlash + 1) : path;
    }

    // ── Private helper: extract file extension/type ────────────────────────
    // e.g. "lecture1.pdf" → "pdf"
    private String extractFileType(String path) {
        if (path == null || path.isBlank()) return "";
        String name = extractFileName(path);
        int dot = name.lastIndexOf('.');
        return (dot >= 0 && dot < name.length() - 1)
                ? name.substring(dot + 1).toLowerCase()
                : "";
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getCCode()        { return cCode; }
    public String getMaterialPath() { return materialPath; }
    public String getFileName()     { return fileName; }
    public String getFileType()     { return fileType; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setCCode(String cCode) {
        this.cCode = cCode;
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath = materialPath;
        // Re-derive fileName and fileType when path changes
        this.fileName = extractFileName(materialPath);
        this.fileType = extractFileType(materialPath);
    }

    // ── toString ───────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "CourseMaterial{course=" + cCode +
                ", file=" + fileName +
                ", type=" + fileType +
                ", path=" + materialPath + "}";
    }
}