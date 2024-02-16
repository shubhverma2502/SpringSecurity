package com.security.authentication.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponsePayload {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private List<String> roles;


    public LoginResponsePayload(String token, Long id, String username, String firstname, String lastname, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.roles = roles;
    }
}
