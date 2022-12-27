package com.example.email.users;


//import org.apache.tomcat.util.json.JSONParser;
//import org.json.JSONArray;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class UsersService {
    private String filename;
    private JSONArray users;
    private JSONArray mails;
    private JSONObject account;
    private String accountBody = "{\n" +
            "\"inbox\": [],\n" +
            "\"sent\": []\n" +
            "}";

    public UsersService() throws IOException, ParseException {

    }

    public void signUp(User user) throws IOException, ParseException {
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
    }

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

    public boolean sendingMail(String senderEmail, Mail mail) throws IOException, ParseException {
        filename = "users.json";
        Object objc = new JSONParser().parse(new FileReader(this.filename));
        users = (JSONArray) objc;

        boolean flag = false;
        for(int i = 0; i < users.size(); i++){
            Object userData = users.get(i);
            JSONObject jsonUser = (JSONObject) userData;
            if (jsonUser.get("userEmail").equals(mail.getReceiver())) flag = true;
        }
        if(flag == true) {
            filename = "accounts/" + senderEmail + "/" + senderEmail + ".json";
            objc = new JSONParser().parse(new FileReader(filename));
            account = (JSONObject) objc;
            mails = (JSONArray) account.get("sent");
            mails.add(mail);
            account.put("sent", mails);
            try {
                Files.write(Paths.get(filename), account.toJSONString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            filename = "accounts/" + mail.getReceiver() + "/" + mail.getReceiver() + ".json";
            objc = new JSONParser().parse(new FileReader(filename));
            account = (JSONObject) objc;
            mails = (JSONArray) account.get("inbox");
            mails.add(mail);
            account.put("inbox", mails);
            try {
                Files.write(Paths.get(filename), account.toJSONString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public JSONArray getInbox(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        return mails;
    }

    public JSONArray getSent(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("sent");
        return mails;
    }
}
