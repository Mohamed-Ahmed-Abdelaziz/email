package com.example.email.users;


//import org.apache.tomcat.util.json.JSONParser;
//import org.json.JSONArray;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsersService {
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
            // checking Emails so that it can be added to contacts
//            if(!containContact(senderEmail, mail.getReceiver())){
//                contacts = (JSONArray) account.get("contacts");
//                addContact(senderEmail, mail.getReceiver());
//                account.put("contacts", contacts);
//            }
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
            // checking Emails so that it can be added to contacts
//            if(!containContact(mail.getReceiver(), senderEmail)){
//                contacts = (JSONArray) account.get("contacts");
//                addContact(mail.getReceiver(), senderEmail);
//                account.put("contacts", contacts);
//            }
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

    public JSONArray getDraft(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("draft");
        return mails;
    }
    public JSONArray getTrash(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("Trash");
        return mails;
    }
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

    public void deletingMail(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        JSONArray trashMails = (JSONArray) account.get("trash");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id") == id){
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

    public void makingImportant(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id") == id){
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

    public void makingUnImportant(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id") == id){
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

    public void makingRead(String userEmail, Long id) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if (jsonMail.get("id") == id){
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

    public JSONArray getcontacts(String userEmail) throws IOException, ParseException {
        filename = "accounts/" + userEmail + "/" + userEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        contacts = (JSONArray) account.get("contacts");
        return contacts;
    }

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
    public void uploadAttachments(List<MultipartFile> multipartFiles, String senderEmail, String receiverEmail, long id) throws IOException {
        String senderDirectory = "accounts/" + senderEmail + "/";
        String receiverDirectory = "accounts/" + receiverEmail + "/";
        List<String> filenames = new ArrayList<>();
        for(MultipartFile file : multipartFiles) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            filename = Long.toString(id) + "_" + filename;

            Path fileStorage = Paths.get(senderDirectory, filename).toAbsolutePath().normalize();
            Files.copy(file.getInputStream(), fileStorage, StandardCopyOption.REPLACE_EXISTING);

            fileStorage = Paths.get(receiverDirectory, filename).toAbsolutePath().normalize();
            Files.copy(file.getInputStream(), fileStorage, StandardCopyOption.REPLACE_EXISTING);
            filenames.add(filename);
        }
    }

    public ResponseEntity<Resource> downloadAttachment(String userEmail, long id) throws IOException {
        String attachmentId;
        String attachmentsDirectory = "accounts/" + userEmail + "/";
        File directory = new File(attachmentsDirectory);
        String[] flist = directory.list();
        if (flist == null) {
            Resource  resource = null;
            return ResponseEntity.ok().body(resource);
        }
        else {
            // Linear search in the array
            for (int i = 0; i < flist.length; i++) {
                String filename = flist[i];
                int index = filename.indexOf("_");
                int lastIndex = filename.length();
                if(filename.contains("_")){
                    attachmentId = filename.substring(0, index);
                    if(id == Long.parseLong(attachmentId)){
                        Path filePath = Paths.get(attachmentsDirectory).toAbsolutePath().normalize().resolve(filename);
                        Resource resource = new UrlResource(filePath.toUri());
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.add("File-Name", filename.substring(index + 1, lastIndex));
                        httpHeaders.add(httpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=" + filename.substring(index + 1, lastIndex));
                        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                                .headers(httpHeaders).body(resource);
                    }
                }
            }
        }
        Resource  resource = null;
        return ResponseEntity.ok().body(resource);
    }

}
