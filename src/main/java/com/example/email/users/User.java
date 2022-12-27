package com.example.email.users;

public class User {
    private String userName;
    private String userEmail;
    private String userPassword;

    public User(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "{" +
                "\"userName\": " + "\"" + userName + "\"" + ", " +
                "\"userEmail\": " + "\"" + userEmail + "\"" + ", " +
                "\"userPassword\": " + "\"" + userPassword + "\"" +
                '}';
    }

}
