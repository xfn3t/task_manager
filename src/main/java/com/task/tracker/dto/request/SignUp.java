package com.task.tracker.dto.request;

import lombok.Data;

@Data
public class SignUp {
    private String username;
    private  String email;
    private String password;
}
