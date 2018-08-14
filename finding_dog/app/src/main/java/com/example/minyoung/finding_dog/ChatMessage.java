package com.example.minyoung.finding_dog;

import android.graphics.Bitmap;

public class ChatMessage {
    public boolean left;
    public String message;
    public Bitmap image;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
        this.image = null;
    }

    public ChatMessage(boolean left, Bitmap image) {
        super();
        this.left = left;
        this.message = null;
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public Bitmap getImage() {
        return image;
    }
}
