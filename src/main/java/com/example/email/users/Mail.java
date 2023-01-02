package com.example.email.users;

public class Mail {
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private Long id;
    private boolean important;
    private boolean read;
    private int importance;
    private String date;
    private boolean hasAttachment;

    public Mail(String sender, String receiver, String subject, String body, Long id, boolean important, boolean read, int importance, boolean hasAttachment) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.id = id;
        this.important = important;
        this.read = read;
        this.importance = importance;
        this.hasAttachment = hasAttachment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }
    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
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
                ", \"importance\": " + importance +
                ", \"date\": " +"\"" + date + "\"" +
                ", \"hasAttachment\": " + hasAttachment +
                '}';
    }
}
