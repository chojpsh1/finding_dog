package com.example.minyoung.finding_dog;

public class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
