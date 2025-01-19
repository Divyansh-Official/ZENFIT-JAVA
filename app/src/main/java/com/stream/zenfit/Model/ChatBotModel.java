package com.stream.zenfit.Model;

public class ChatBotModel {

    public static String SENT_BY_USER = "user";
    public static String SENT_BY_BOT = "bot";

    String sender;
    String message;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatBotModel(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
