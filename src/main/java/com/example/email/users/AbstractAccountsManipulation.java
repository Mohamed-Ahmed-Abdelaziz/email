package com.example.email.users;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class AbstractAccountsManipulation {
    private String filename;
    private JSONArray users;
    private JSONArray mails;
    private JSONArray contacts;
    private JSONObject account;
    private String accountBody = "{\n" +
            "\"inbox\": [],\n" +
            "\"sent\": [],\n" +
            "\"draft\": [],\n" +
            "\"trash\": [],\n" +
            "\"contacts\": []\n" +
            "}";
    public boolean AccountExits(User userAccount) throws IOException, ParseException {
        filename = "users.json";
        Object objc = new JSONParser().parse(new FileReader(this.filename));
        users = (JSONArray) objc;

        for(int i = 0; i < users.size(); i++){
            Object userData = users.get(i);
            JSONObject jsonUser = (JSONObject) userData;
            if (jsonUser.get("userEmail").equals(userAccount.getUserEmail()))
                return true;
        }
        return false;
    }
    abstract boolean logIn(User user) throws IOException, ParseException;

    abstract boolean signUp(User user) throws IOException, ParseException;
}
