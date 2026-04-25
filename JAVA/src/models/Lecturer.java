package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Lecturer model class.
 * Maps to: LECTURER table + LECTURER_PHONE table (multi-valued phones).
 * Inherits: userId, password, role, profilePic from User.
 *
 * DB columns mapped:
 *   Lec_id       -> userId     (from User)
 *   Password     -> password   (from User)
 *   Role         -> role       (from User, always "Lecturer")
 *   Profile_pic  -> profilePic (from User)
 *   Name         -> name
 *   Email        -> email
 *   Nic          -> nic
 *   Dob          -> dob
 *   Education_lvl-> educationLvl
 *   Dpt_name     -> dptName
 *   No           -> houseNo
 *   Street       -> street
 *   City         -> city
 *   phones       -> List<String> from LECTURER_PHONE table
 */
public class Lecturer extends User {

    // ── DB fields from LECTURER table ──────────────────────────────────────
    private String name;
    private String email;
    private String nic;
    private String dob;            // Stored as "YYYY-MM-DD" string
    private String educationLvl;   // ENUM: "Bachelor" | "Master" | "Phd"
    private String dptName;
    private String houseNo;        // Maps to column "No"
    private String street;
    private String city;

    // ── Multi-valued attribute from LECTURER_PHONE table ───────────────────
    private List<String> phones;

    // ── Constructor (without profilePic — uses default) ────────────────────
    public Lecturer(String lecId, String password,
                    String name, String email, String nic, String dob,
                    String educationLvl, String dptName,
                    String houseNo, String street, String city) {
        super(lecId, password, "Lecturer");   // calls User(id, pwd, role)
        this.name        = name;
        this.email       = email;
        this.nic         = nic;
        this.dob         = dob;
        this.educationLvl = educationLvl;
        this.dptName     = dptName;
        this.houseNo     = houseNo;
        this.street      = street;
        this.city        = city;
        this.phones      = new ArrayList<>();
    }

    // ── Constructor (with profilePic) ──────────────────────────────────────
    public Lecturer(String lecId, String password, String profilePic,
                    String name, String email, String nic, String dob,
                    String educationLvl, String dptName,
                    String houseNo, String street, String city) {
        super(lecId, password, "Lecturer", profilePic);
        this.name        = name;
        this.email       = email;
        this.nic         = nic;
        this.dob         = dob;
        this.educationLvl = educationLvl;
        this.dptName     = dptName;
        this.houseNo     = houseNo;
        this.street      = street;
        this.city        = city;
        this.phones      = new ArrayList<>();
    }

    // ── Abstract method implementation ─────────────────────────────────────
    @Override
    public void displayDashboard() {
        System.out.println("=== Lecturer Dashboard ===");
        System.out.println("Welcome, " + name + "  [" + getUserId() + "]");
        System.out.println("Department: " + dptName);
    }

    // ── Phone management (LECTURER_PHONE table) ────────────────────────────
    public void addPhone(String phone) {
        if (phone != null && !phone.isBlank() && !phones.contains(phone)) {
            phones.add(phone);
        }
    }

    public void removePhone(String phone) {
        phones.remove(phone);
    }

    public List<String> getPhones() {
        return new ArrayList<>(phones);   // return a copy (encapsulation)
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getNic()          { return nic; }
    public String getDob()          { return dob; }
    public String getEducationLvl() { return educationLvl; }
    public String getDptName()      { return dptName; }
    public String getHouseNo()      { return houseNo; }
    public String getStreet()       { return street; }
    public String getCity()         { return city; }

    // ── Setters (for profile editing) ──────────────────────────────────────
    public void setName(String name)               { this.name = name; }
    public void setEmail(String email)             { this.email = email; }
    public void setNic(String nic)                 { this.nic = nic; }
    public void setDob(String dob)                 { this.dob = dob; }
    public void setEducationLvl(String educationLvl){ this.educationLvl = educationLvl; }
    public void setDptName(String dptName)         { this.dptName = dptName; }
    public void setHouseNo(String houseNo)         { this.houseNo = houseNo; }
    public void setStreet(String street)           { this.street = street; }
    public void setCity(String city)               { this.city = city; }

    // ── toString (useful for debugging) ────────────────────────────────────
    @Override
    public String toString() {
        return "Lecturer{id=" + getUserId() + ", name=" + name +
                ", dept=" + dptName + ", edu=" + educationLvl + "}";
    }
}