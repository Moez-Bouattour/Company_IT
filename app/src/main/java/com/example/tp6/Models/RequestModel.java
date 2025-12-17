package com.example.tp6.Models;

public class RequestModel {

    private int id;
    private int userId;
    private int companyId;
    private String cvPath;
    private String status;

    // Constructeur
    public RequestModel(int id, int userId, int companyId, String cvPath, String status) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.cvPath = cvPath;
        this.status = status;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

