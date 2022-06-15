package com.example.frontend;

import java.io.File;

public class Message {
    public boolean left;
    public String message;
    public File file;
    public String type = "";

    public Message(boolean left, String message , String type) {
        super();
        this.left = left;
        this.message = message;
        this.type = type;

    }

}
