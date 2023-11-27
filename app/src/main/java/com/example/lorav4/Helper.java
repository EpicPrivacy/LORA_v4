package com.example.lorav4;

public class Helper {
    private String userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String deliveryAddress;
    private String password;

    // Empty constructor for Firebase
    public Helper() {
    }

    // Constructor with parameters
    public Helper(String userId, String firstName, String lastName, String mobileNumber, String deliveryAddress, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.deliveryAddress = deliveryAddress;
        this.password = password;
    }

    // Getters and setters for all fields

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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
