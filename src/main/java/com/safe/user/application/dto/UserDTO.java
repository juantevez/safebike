package com.safe.user.application.dto;


public class UserDTO {
    private String email;
    private String firstName;
    private String lastName;

    private String phone;
    public UserDTO() {}

    public UserDTO(String email, String firstName,String lastName ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public UserDTO(String email, String firstName,String lastName, String phone ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}