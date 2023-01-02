package com.example.email.users;

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
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class AttachmentsManipulation {

    public void uploadAttachments(List<MultipartFile> multipartFiles, String senderEmail, String receiverEmail, long id) throws IOException, ParseException {
        String filename = "accounts/" + senderEmail + "/" + senderEmail + ".json";
        Object objc = new JSONParser().parse(new FileReader(filename));
        JSONObject account = (JSONObject) objc;
        JSONArray mails = (JSONArray) account.get("sent");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if ((long) jsonMail.get("id") == id){
                jsonMail.put("hasAttachment", true);
                mails.remove(i);
                mails.add(jsonMail);
                break;
            }
        }
        account.put("sent", mails);
        try {
            Files.write(Paths.get(filename), account.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //----------------------
        filename = "accounts/" + receiverEmail + "/" + receiverEmail + ".json";
        objc = new JSONParser().parse(new FileReader(filename));
        account = (JSONObject) objc;
        mails = (JSONArray) account.get("inbox");
        for(int i = 0; i < mails.size(); ++i){
            Object userMail = mails.get(i);
            JSONObject jsonMail = (JSONObject) userMail;
            if ((long) jsonMail.get("id") == id){
                jsonMail.put("hasAttachment", true);
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
        //-------------------------------
        String senderDirectory = "accounts/" + senderEmail + "/";
        String receiverDirectory = "accounts/" + receiverEmail + "/";

        List<String> filenames = new ArrayList<>();
        for(MultipartFile file : multipartFiles) {
            filename = StringUtils.cleanPath(file.getOriginalFilename());
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
