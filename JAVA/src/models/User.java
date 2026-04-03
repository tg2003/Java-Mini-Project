package models;

public abstract class User {
    private String userId;
    private String password;
    private String role;
    private String profilePic;

    //creating 2 User constructors for with or without PP (constructor overloading)
    public User(String userId, String password, String role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.profilePic = "resources/defaultPic.png";
    }
    public User(String userId, String password, String role, String profilePic) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.profilePic = profilePic;
    }

    public abstract void displayDashboard();

    //setters - set PP & set pwd
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    //getters (4 for all fields)

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
