package com.example.lorav4;

public class Helper {
    private String userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String password;
    private double latitude;
    private double longitude;


    // Empty constructor for Firebase
    public Helper() {
    }


    // Constructor with parameters
    public Helper(String userId, String firstName, String lastName, String mobileNumber, String password, double latitude, double longitude) {
        // Initialize variables
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    // Getters and setters for all fields

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}