package com.example.lorav4.Driver;

public class Drivers_helper {
    private String userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String password;
    private String userType;


    // Empty constructor for Firebase
    public Drivers_helper() {
    }
    // Constructor with parameters
    public Drivers_helper(String userId, String firstName, String lastName, String mobileNumber, String password, String userType) {
        // Initialize variables
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}