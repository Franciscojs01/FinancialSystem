package com.example.financialSystem.dto;

public class LoginDto {
    private int Id;
    private String userName;
    private String token;

    public LoginDto(int userId, String userName, String token) {
        this.Id = userId;
        this.userName = userName;
        this.token = token;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
