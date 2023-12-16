package com.example.lorav4.Admin;

public class Order {
    private String orderId;
    private String userId;
    private String firstName,lastName,mobileNumber,order_name,order_type,order_count,order_amount;

    private double latitude, longitude;

    // Default no-argument constructor required for Firebase
    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }
    public Order(String orderId, String userId, String firstName,String lastName,String mobileNumber, String order_name, String order_type, String order_count, String order_amount,double latitude, double longitude) {
        this.orderId = orderId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.order_name = order_name;
        this.order_type = order_type;
        this.order_count = order_count;
        this.order_amount = order_amount;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getOrder_count() {
        return order_count;
    }

    public void setOrder_count(String order_count) {
        this.order_count = order_count;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
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
}