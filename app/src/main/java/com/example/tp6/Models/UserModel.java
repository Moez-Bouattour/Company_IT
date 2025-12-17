package com.example.tp6.Models;

public class UserModel {
    private int id;
    private String name;
    private String password;
    private String role;
    private String email;
    private String gender;
    private String phone;
    private String dateOfBirth;
    private String place;

    public UserModel() {}

    public UserModel(int id, String name, String password, String email, String role,
                     String gender, String phone, String dateOfBirth, String place) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.place = place;
    }

    // Getters et Setters pour tous les attributs
    public long getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getPlace() { return place; }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setPlace(String place) { this.place = place; }
}
