package models;

public class TechOfficer extends User{
    private String name;
    private String email;
    private String nic;
    private String dob;
    private String phone;
    private String address;

    public TechOfficer(String userId, String password, String role, String name, String email, String nic, String dob, String phone, String address) {
        super(userId, password, role);
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.dob = dob;
        this.phone = phone;
        this.address = address;
    }

    public TechOfficer(String userId, String password, String role, String profilePic, String name, String email, String nic, String phone, String dob, String address) {
        super(userId, password, role, profilePic);
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
    }


    @Override
    public void displayDashboard() {

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNic() {
        return nic;
    }

    public String getDob() {
        return dob;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
