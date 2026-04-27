package models;

import java.sql.Date;

public class Undergraduate {

    private String ugId;
    private String name;
    private String email;
    private String nic;
    private Date   dob;
    private String dptName;
    private String houseNo;
    private String street;
    private String city;
    private String profilePic;   // path stored in USER.Profile_pic

    // ── Constructor ────────────────────────────────────────────────────────
    public Undergraduate(String ugId, String name, String email,
                         String nic, Date dob, String dptName,
                         String houseNo, String street, String city) {
        this.ugId       = ugId;
        this.name       = name;
        this.email      = email;
        this.nic        = nic;
        this.dob        = dob;
        this.dptName    = dptName;
        this.houseNo    = houseNo;
        this.street     = street;
        this.city       = city;
        this.profilePic = null;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getUgId()      { return ugId;       }
    public String getName()      { return name;       }
    public String getEmail()     { return email;      }
    public String getNic()       { return nic;        }
    public Date   getDob()       { return dob;        }
    public String getDptName()   { return dptName;    }
    public String getHouseNo()   { return houseNo;    }
    public String getStreet()    { return street;     }
    public String getCity()      { return city;       }
    public String getProfilePic(){ return profilePic; }

    // ── Setter (profile pic only — other fields set at login) ──────────────
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    // ── Utility ────────────────────────────────────────────────────────────
    /** Returns initials for avatar circle (e.g. "KP" from "Kamal Perera"). */
    public String getInitials() {
        if (name == null || name.isBlank()) return "??";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1)
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    /** Returns a single-line formatted address. */
    public String getAddress() {
        return (houseNo != null ? houseNo + ", " : "") + street + ", " + city;
    }

    @Override
    public String toString() { return name + " (" + ugId + ")"; }
}
