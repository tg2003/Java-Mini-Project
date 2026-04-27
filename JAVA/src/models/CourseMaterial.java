package models;


public class CourseMaterial {


    private String cCode;
    private String materialPath;


    private String fileName;
    private String fileType;


    public CourseMaterial(String cCode, String materialPath) {
        this.cCode        = cCode;
        this.materialPath = materialPath;

        this.fileName  = extractFileName(materialPath);
        this.fileType  = extractFileType(materialPath);
    }


    private String extractFileName(String path) {
        if (path == null || path.isBlank()) return "";
        int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return (lastSlash >= 0) ? path.substring(lastSlash + 1) : path;
    }


    private String extractFileType(String path) {
        if (path == null || path.isBlank()) return "";
        String name = extractFileName(path);
        int dot = name.lastIndexOf('.');
        return (dot >= 0 && dot < name.length() - 1)
                ? name.substring(dot + 1).toLowerCase()
                : "";
    }


    public String getCCode()        { return cCode; }
    public String getMaterialPath() { return materialPath; }
    public String getFileName()     { return fileName; }
    public String getFileType()     { return fileType; }

    // set method
    public void setCCode(String cCode) {
        this.cCode = cCode;
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath = materialPath;
        this.fileName = extractFileName(materialPath);
        this.fileType = extractFileType(materialPath);
    }

    // print object details
    @Override
    public String toString() {
        return "CourseMaterial{course=" + cCode +
                ", file=" + fileName +
                ", type=" + fileType +
                ", path=" + materialPath + "}";
    }
}