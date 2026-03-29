package models;

public class Admin extends User{
    private String name;
    private String email;
    private String nic;
    private String dob;
    private String dptName;
    private String phone;
    private String no;
    private String street;
    private String city;

    //Admin Constructor 1 - with PP
    public Admin(String userId, String password, String profilePic, String name, String email,
                 String nic, String dob, String dptName, String phone, String no, String street,
                 String city) {
        super(userId, password, "Admin", profilePic);
        this.name    = name;
        this.email   = email;
        this.nic     = nic;
        this.dob     = dob;
        this.dptName = dptName;
        this.phone   = phone;
        this.no      = no;
        this.street  = street;
        this.city    = city;
    }

    //Admin Constructor 2 - without PP
    public Admin(String userId, String password,String name, String email, String nic, String dob,
                 String dptName, String phone, String no, String street, String city) {
        super(userId, password, "Admin");
        this.name    = name;
        this.email   = email;
        this.nic     = nic;
        this.dob     = dob;
        this.dptName = dptName;
        this.phone   = phone;
        this.no      = no;
        this.street  = street;
        this.city    = city;
    }

    @Override
    public void displayDashboard() {
        System.out.println("Hello "+name+", Welcome to the Admin Dashboard...");
    }

    // Getters
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getNic()     { return nic; }
    public String getDob()     { return dob; }
    public String getDptName() { return dptName; }
    public String getPhone()   { return phone; }
    public String getNo()      { return no; }
    public String getStreet()  { return street; }
    public String getCity()    { return city; }

    // Setters
    public void setName(String name)       { this.name    = name; }
    public void setEmail(String email)     { this.email   = email; }
    public void setPhone(String phone)     { this.phone   = phone; }
    public void setNic(String nic)         { this.nic     = nic; }
    public void setDob(String dob)         { this.dob     = dob; }
    public void setNo(String no)           { this.no      = no; }
    public void setStreet(String street)   { this.street  = street; }
    public void setCity(String city)       { this.city    = city; }
    public void setDptName(String dptName) { this.dptName = dptName; }
}
