package com.example.citycycle;

public class UserModel {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String address;
    private String dob;
    private String gender;
    private String password;
    private byte[] profileImage;


    public UserModel() {
    }


    public UserModel(int id, String firstname, String lastname, String email, String phone,
                     String address, String dob, String gender, String password, byte[] profileImage) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.password = password;
        this.profileImage = profileImage;
    }


    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}