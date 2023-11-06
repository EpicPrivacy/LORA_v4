package com.example.lorav4;

public class Helper {

    String first_name,last_namse,m_number,delivery_add,password;

    public Helper() {

    }

    public Helper(String first_name, String last_namse, String m_number, String delivery_add, String password) {
        this.first_name = first_name;
        this.last_namse = last_namse;
        this.m_number = m_number;
        this.delivery_add = delivery_add;
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_namse() {
        return last_namse;
    }

    public void setLast_namse(String last_namse) {
        this.last_namse = last_namse;
    }

    public String getM_number() {
        return m_number;
    }

    public void setM_number(String m_number) {
        this.m_number = m_number;
    }

    public String getDelivery_add() {
        return delivery_add;
    }

    public void setDelivery_add(String delivery_add) {
        this.delivery_add = delivery_add;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
