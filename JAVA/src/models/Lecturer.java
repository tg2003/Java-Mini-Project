package models;

import java.util.ArrayList;
import java.util.List;

public class Lecturer extends User {


    private String name;
    private String email;
    private String nic;
    private String dob;
    private String educationLvl;
    private String dptName;
    private String houseNo;
    private String street;
    private String city;


    private List<String> phones;

    // Constructor
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

    // lec dash board
    @Override
    public void displayDashboard() {
        System.out.println("=== Lecturer Dashboard ===");
        System.out.println("Welcome, " + name + "  [" + getUserId() + "]");
        System.out.println("Department: " + dptName);
    }

    // phone num
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


    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getNic()          { return nic; }
    public String getDob()          { return dob; }
    public String getEducationLvl() { return educationLvl; }
    public String getDptName()      { return dptName; }
    public String getHouseNo()      { return houseNo; }
    public String getStreet()       { return street; }
    public String getCity()         { return city; }


    public void setName(String name)               { this.name = name; }
    public void setEmail(String email)             { this.email = email; }
    public void setNic(String nic)                 { this.nic = nic; }
    public void setDob(String dob)                 { this.dob = dob; }
    public void setEducationLvl(String educationLvl){ this.educationLvl = educationLvl; }
    public void setDptName(String dptName)         { this.dptName = dptName; }
    public void setHouseNo(String houseNo)         { this.houseNo = houseNo; }
    public void setStreet(String street)           { this.street = street; }
    public void setCity(String city)               { this.city = city; }


    @Override
    public String toString() {
        return "Lecturer{id=" + getUserId() + ", name=" + name +
                ", dept=" + dptName + ", edu=" + educationLvl + "}";
    }
}