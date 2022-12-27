package com.example.email.users;


import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@CrossOrigin
public class UsersController {
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("signup")
    public void signUp(@RequestBody User user) throws IOException, ParseException {
        usersService.signUp(user);
    }
    @PostMapping("login")
    public boolean logIn(@RequestBody User user) throws IOException, ParseException {
        return usersService.logIn(user);
    }
    @PostMapping("sendingmail/{senderEmail}")
    public boolean sendingMail(@PathVariable String senderEmail, @RequestBody Mail mail) throws IOException, ParseException {
        return usersService.sendingMail(senderEmail, mail);
    }
    @GetMapping("inbox/{userEmail}")
    public JSONArray getInbox(@PathVariable String userEmail) throws IOException, ParseException {
        return usersService.getInbox(userEmail);
    }
    @GetMapping("sent/{userEmail}")
    public JSONArray getSent(@PathVariable String userEmail) throws IOException, ParseException {
        return usersService.getSent(userEmail);
    }
//    @GetMapping
//    public boolean test(){
//        usersService.signUp();
//        return true;
//    }
}
