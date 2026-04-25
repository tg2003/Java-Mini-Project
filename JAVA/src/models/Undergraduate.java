package models;

import java.sql.Date;

public class Undergraduate {

    private String ugId;
    private String name;
    private String email;
    private String nic;
    private Date dob;
    private String dptName;
    private String houseNo;
    private String street;
    private String city;

    public Undergraduate(String ugId, String name, String email,
                         String nic, Date dob, String dptName,
                         String houseNo, String street, String city) {
        this.ugId = ugId;
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.dob = dob;
        this.dptName = dptName;
        this.houseNo = houseNo;
        this.street = street;
        this.city = city;
    }

    public String getUgId() { return ugId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getNic() { return nic; }
    public Date getDob() { return dob; }
    public String getDptName() { return dptName; }
    public String getHouseNo() { return houseNo; }
    public String getStreet() { return street; }
    public String getCity() { return city; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setNic(String nic) { this.nic = nic; }
    public void setDob(Date dob) { this.dob = dob; }
    public void setDptName(String dptName) { this.dptName = dptName; }
    public void setHouseNo(String houseNo) { this.houseNo = houseNo; }
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }

    public String getInitials() {
        if (name == null || name.isBlank()) return "??";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public String getAddress() {
        return (houseNo != null ? houseNo + ", " : "") + street + ", " + city;
    }

    @Override
    public String toString() {
        return name + " (" + ugId + ")";
    }
}
