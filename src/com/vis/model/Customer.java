package com.vis.model;

public class Customer {
    private int customerID;
    private String name;
    private String surname;
    private String address;
    private String phone;
    private String email;


    //  GETTERS
    public int getCustomerID(){
        return customerID;
    }
    public String getName() {
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getAddress(){
        return address;
    }
    public String getPhone(){
        return phone;
    }
    public String getEmail(){
        return email;
    }


    //  SETTERS
    public void setCustomerID(int customerID){
        this.customerID = customerID;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setSurname(String surname){
        this.surname = surname;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setEmail(String email){
        this.email = email;
    }

    //  CONSTRUCTOR
    public Customer(int customerID, String name, String surname, String address, String phone, String email){
        this.customerID = customerID;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public String getCustomerDetails(){
        return "Customer ID: " + customerID + "\n" + "Name: " + name + "\n" + "Surname: " + surname +"\n" + "Address: " + address + "\n" + "Phone: " + phone + "\n" + "Email: " + email;
    }
}
