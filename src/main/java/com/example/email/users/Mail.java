package com.example.email.users;

public class Mail {
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private Long id;
    private boolean important;
    private boolean read;

    public Mail(String sender, String receiver, String subject, String body, Long id, boolean important, boolean read) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.id = id;
        this.important = important;
        this.read = read;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "{" +
                "\"sender\": " + "\"" + sender + "\"" +
                ", \"receiver\": " + "\"" + receiver + "\"" +
                ", \"subject\": " + "\"" + subject + "\"" +
                ", \"body\": " +"\"" + body + "\"" +
                ", \"id\": " + id +
                ", \"important\": " + important +
                ", \"read\": " + read +
                '}';
    }
}
