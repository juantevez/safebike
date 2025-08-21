package com.safe.user.application.dto;


import lombok.Data;

@Data
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

}