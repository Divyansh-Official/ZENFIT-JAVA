package com.stream.zenfit.Modal;

public class ChatBotModal {

    public static String SENT_BY_USER = "user";
    public static String SENT_BY_BOT = "bot";

    String sender;
    String message;
    String timeStamp;

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

    public ChatBotModal(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
