package com.example.email.users;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConcreteAccountsManipulator extends AbstractAccountsManipulation {
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

    public boolean logIn(User user) throws IOException, ParseException {
        filename = "users.json";
        Object objc = new JSONParser().parse(new FileReader(this.filename));
        users = (JSONArray) objc;

        for(int i = 0; i < users.size(); i++){
            Object userData = users.get(i);
            JSONObject jsonUser = (JSONObject) userData;
            if (jsonUser.get("userName").equals(user.getUserName())
                    && jsonUser.get("userEmail").equals(user.getUserEmail())
                    && jsonUser.get("userPassword").equals(user.getUserPassword())
            )
                return true;
        }
        return false;
    }
    public boolean signUp(User user) throws IOException, ParseException {
        if (AccountExits(user) == true) return false;
        else {
            filename = "users.json";
            Object objc = new JSONParser().parse(new FileReader(this.filename));
            users = (JSONArray) objc;

            users.add(user);
            try {
                Files.write(Paths.get(this.filename), users.toJSONString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File("accounts/" + user.getUserEmail()).mkdirs();
            String path = "accounts/" + user.getUserEmail() + "/" + user.getUserEmail() + ".json";
            File file = new File(path);
            file.createNewFile();
            Files.write(Paths.get(path), accountBody.getBytes());
            return true;
        }
    }
}
