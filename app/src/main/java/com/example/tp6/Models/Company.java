package com.example.tp6.Models;

public class Company {

    private int id;
    private String name;
    private String services;
    private String phone;
    private String website;
    private String localisation;
    private String imageResId;
    private String email;
    private String imageUri;

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }


    public Company(int id, String name, String services, String phone, String website,
                   String localisation, String imageResId,String email) {
        this.id = id;
        this.name = name;
        this.services = services;
        this.phone = phone;
        this.website = website;
        this.localisation = localisation;
        this.imageUri = imageResId;
        this.email=email;
    }

    public Company(String name, String services, String phone, String website,
                   String localisation, String imageResId, String email) {
        this.name = name;
        this.services = services;
        this.phone = phone;
        this.website = website;
        this.localisation = localisation;
        this.imageUri = imageResId;
        this.email = email;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getServices() { return services; }
    public String getPhone() { return phone; }
    public String getWebsite() { return website; }
    public String getLocalisation() { return localisation; }
    public String getImageResId() { return imageUri; }

    public String getEmail() { return email; }
}

