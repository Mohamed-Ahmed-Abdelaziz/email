package com.example.email.users;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUsersService {
    boolean signUp(User user) throws IOException, ParseException;

    boolean logIn(User user) throws IOException, ParseException;

    boolean sendingMail(String senderEmail, Mail mail) throws IOException, ParseException;

    JSONArray getInbox(String userEmail) throws IOException, ParseException;

    JSONArray getSent(String userEmail) throws IOException, ParseException;

    JSONArray getDraft(String userEmail) throws IOException, ParseException;

    JSONArray getTrash(String userEmail) throws IOException, ParseException;

    JSONArray getImportant(String userEmail) throws IOException, ParseException;

    JSONArray getRead(String userEmail) throws IOException, ParseException;

    JSONArray getunRead(String userEmail) throws IOException, ParseException;

    void draftingMail(String userEmail, Mail mail) throws IOException, ParseException;

    void deletingMail(String userEmail, Long id) throws IOException, ParseException;

    void makingImportant(String userEmail, Long id) throws IOException, ParseException;

    void makingUnImportant(String userEmail, Long id) throws IOException, ParseException;

    void makingRead(String userEmail, Long id) throws IOException, ParseException;

    JSONArray search(String userEmail, String searchKey) throws IOException, ParseException;

    JSONArray getcontacts(String userEmail) throws IOException, ParseException;

    boolean addContact(String userEmail, String contactEmail) throws IOException, ParseException;

    void deleteContact(String userEmail, String contactEmail) throws IOException, ParseException;

    boolean containContact(String userEmail, String contactEmail) throws IOException, ParseException;

    // ---------------------
    // attachments manipulation
    void uploadAttachments(List<MultipartFile> multipartFiles, String senderEmail, String receiverEmail, long id) throws IOException, ParseException;

    ResponseEntity<Resource> downloadAttachment(String userEmail, long id) throws IOException;
}
