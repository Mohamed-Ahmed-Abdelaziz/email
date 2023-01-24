package com.example.email.users;


import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class UsersService implements IUsersService {
    private static UsersService instance = new UsersService();
    private AttachmentsManipulation attachmentsManipulation = new AttachmentsManipulation();
    private ConcreteAccountsManipulator accountsManipulator = new ConcreteAccountsManipulator();
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
    String datePattern = "dd/MM/yyyy HH:mm:ss ";
    DateTimeFormatter df = DateTimeFormatter.ofPattern(datePattern);

    private UsersService() {
    }
    public static UsersService getInstance(){
        return instance;
    }

    @Override
    public boolean signUp(User user) throws IOException, ParseException {
        return accountsManipulator.signUp(user);
    }

    @Override
    public boolean logIn(User user) throws IOException, ParseException {
        return accountsManipulator.logIn(user);
    }

    @Override
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

        LocalDateTime now = LocalDateTime.now();
        String dateNow = df.format(now);
        mail.setDate(dateNow);
        mail.setHasAttachment(false);
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
            // checking Emails so that it can be added to contacts
            if(!containContact(senderEmail, mail.getReceiver())){
                filename = "accounts/" + senderEmail + "/" + senderEmail + ".json";
                objc = new JSONParser().parse(new FileReader(filename));
                account = (JSONObject) objc;
                contacts = (JSONArray) account.get("contacts");
                addContact(senderEmail, mail.getReceiver());
                account.put("contacts", contacts);
                try {
                    Files.write(Paths.get(filename), account.toJSONString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(!containContact(mail.getReceiver(), senderEmail)){
                filename = "accounts/" + mail.getReceiver() + "/" + mail.getReceiver() + ".json";
                objc = new JSONParser().parse(new FileReader(filename));
                account = (JSONObject) objc;
                contacts = (JSONArray) account.get("contacts");
                addContact(mail.getReceiver(), senderEmail);
                account.put("contacts", contacts);
                try {
                    Files.write(Paths.get(filename), account.toJSONString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    @Override
    public JSONArray getInbox(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        return mails;
    }

    @Override
    public JSONArray getSent(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("sent");
        return mails;
    }

    @Override
    public JSONArray getDraft(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("draft");
        return mails;
    }
    @Override
    public JSONArray getTrash(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("trash");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            String date = jsonMail.get("date").toString();
            LocalDateTime dateParsed = LocalDateTime.parse(date, df);
            if(dateParsed.getDayOfMonth() - LocalDateTime.now().getDayOfMonth() < -29){
                mails.remove(i);
            }
        }
        account.put("trash", mails);
        return mails;
    }
    @Override
    public JSONArray getImportant(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        JSONArray importantMails = new JSONArray();
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if ((boolean) jsonMail.get("important")){
                importantMails.add(jsonMail);
            }
        }
        return importantMails;
    }
    @Override
    public JSONArray getRead(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        JSONArray readMails = new JSONArray();
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if ((boolean) jsonMail.get("read")){
                readMails.add(jsonMail);
            }
        }
        return readMails;
    }
    @Override
    public JSONArray getunRead(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        JSONArray unreadMails = new JSONArray();
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (!(boolean) jsonMail.get("read")){
                unreadMails.add(jsonMail);
            }
        }
        return unreadMails;
    }

    @Override
    public void draftingMail(String userEmail, Mail mail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("draft");
        mails.add(mail);
        account.put("draft", mails);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletingMail(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        JSONArray trashMails = (JSONArray) account.get("trash");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id").equals(id)){
                trashMails.add(mails.get(i));
                mails.remove(i);
            }
        }
        account.put("inbox", mails);
        account.put("trash", trashMails);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void makingImportant(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id").equals(id)){
                jsonMail.put("important", true);
                mails.remove(i);
                mails.add(jsonMail);
                break;
            }
        }
        account.put("inbox", mails);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void makingUnImportant(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id").equals(id)){
                jsonMail.put("important", false);
                mails.remove(i);
                mails.add(jsonMail);
                break;
            }
        }
        account.put("inbox", mails);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void makingRead(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id").equals(id)){
                jsonMail.put("read", true);
                mails.remove(i);
                mails.add(jsonMail);
                break;
            }
        }
        account.put("inbox", mails);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONArray search(String userEmail, String searchKey) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        JSONArray foundedMails = new JSONArray();
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            String body = (String) jsonMail.get("body");
            String sender = (String) jsonMail.get("sender");
            String receiver = (String) jsonMail.get("receiver");
            String subject = (String) jsonMail.get("subject");
            if (body.contains(searchKey)){
                foundedMails.add(jsonMail);
                continue;
            }else if(subject.contains(searchKey)){
                foundedMails.add(jsonMail);
                continue;
            }else if(sender.contains(searchKey)){
                foundedMails.add(jsonMail);
                continue;
            }else if(receiver.contains(searchKey)){
                foundedMails.add(jsonMail);
                continue;
            }
        }
        return foundedMails;
    }
    public JSONArray sortInbox(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        mails.sort(new Comparator() {
            @Override
            public int compare(Object mail1, Object mail2) {
                JSONObject jsonMail1 = (JSONObject) mail1;
                JSONObject jsonMail2 = (JSONObject) mail2;
                String date1 = (String) jsonMail1.get("date");
                String date2 = (String) jsonMail2.get("date");
                LocalDateTime date1Parsed = LocalDateTime.parse(date1, df);
                LocalDateTime date2Parsed = LocalDateTime.parse(date2, df);
                return date2Parsed.compareTo(date1Parsed);
            }
        });
        return mails;
    }
    public JSONArray sortImportance(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        mails.sort(new Comparator() {
            @Override
            public int compare(Object mail1, Object mail2) {
                JSONObject jsonMail1 = (JSONObject) mail1;
                JSONObject jsonMail2 = (JSONObject) mail2;
                long importance1 = (long) jsonMail1.get("importance");
                long importance2 = (long) jsonMail2.get("importance");
                if(importance2 > importance1) return 1;
                else return -1;
            }
        });
        return mails;
    }

    @Override
    public JSONArray getcontacts(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        contacts = (JSONArray) account.get("contacts");
        return contacts;
    }

    @Override
    public boolean addContact(String userEmail, String contactEmail) throws IOException, ParseException {
        filename = "users.json";
        Object objc = new JSONParser().parse(new FileReader(this.filename));
        users = (JSONArray) objc;
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc2 = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc2;
        contacts = (JSONArray) account.get("contacts");
        boolean flage = false;
        for(int i = 0; i < users.size(); ++i){
            Object user = users.get(i);
            JSONObject jsonUser = (JSONObject) user;
            if(jsonUser.get("userEmail").equals(contactEmail)){
                jsonUser.remove("userPassword");
                contacts.add(jsonUser);
                flage = true;
                break;
            }
        }
        account.put("contacts", contacts);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flage;
    }

    @Override
    public void deleteContact(String userEmail, String contactEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        contacts = (JSONArray) account.get("contacts");
        for(int i = 0; i < contacts.size(); ++i){
            Object user = contacts.get(i);
            JSONObject jsonContact = (JSONObject) user;
            if(jsonContact.get("userEmail").equals(contactEmail)){
                contacts.remove(i);
                break;
            }
        }
        account.put("contacts", contacts);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean containContact(String userEmail, String contactEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        contacts = (JSONArray) account.get("contacts");
        for(int i = 0; i < contacts.size(); ++i){
            Object user = contacts.get(i);
            JSONObject jsonContact = (JSONObject) user;
            if(jsonContact.get("userEmail").equals(contactEmail)){
                return true;
            }
        }
        return false;
    }
    // ---------------------
    // attachments manipulation
    @Override
    public void uploadAttachments(List<MultipartFile> multipartFiles, String senderEmail, String receiverEmail, long id) throws IOException, ParseException {
        attachmentsManipulation.uploadAttachments(multipartFiles, senderEmail, receiverEmail, id);
    }

    @Override
    public ResponseEntity<Resource> downloadAttachment(String userEmail, long id) throws IOException {
        return  attachmentsManipulation.downloadAttachment(userEmail, id);
    }

}
