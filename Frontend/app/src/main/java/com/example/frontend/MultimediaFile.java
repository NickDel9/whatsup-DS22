package com.example.frontend;

import java.io.Serializable;

//C:\Users\User\Desktop

public class MultimediaFile implements Serializable{

    private String multimediaFileName;
    private String profileName;
    private String dateCreated;
    private String length;
    private String framerate;
    private String frameWidth;
    private String frameHeight;
    private byte[] multimediaFileChunk;

    public MultimediaFile(){}

    public MultimediaFile(String multimediaFileName , String profileName , String dateCreated ,
                          String length , String framerate , String frameWidth ,
                          String frameHeight , byte[] multimediaFileChunk) {
        this.multimediaFileName = multimediaFileName;
        this.profileName = profileName;
        this.dateCreated = dateCreated;
        this.length = length;
        this.framerate = framerate;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.multimediaFileChunk = multimediaFileChunk;
    }

    public MultimediaFile(byte[] multimediaFileChunk){
        this.multimediaFileChunk = multimediaFileChunk;
    }

    //setters
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setMultimediaFileName(String multimediaFileName) {
        this.multimediaFileName = multimediaFileName;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setFramerate(String framerate) {
        this.framerate = framerate;
    }

    public void setFrameHeight(String frameHeight) {
        this.frameHeight = frameHeight;
    }

    public void setFrameWidth(String frameWidth) {
        this.frameWidth = frameWidth;
    }

    public void setMultimediaFileChunk(byte[] multimediaFileChunk) {
        this.multimediaFileChunk = multimediaFileChunk;
    }

    //getters
    public String getMultimediaFileName() {
        return multimediaFileName;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getLength() {
        return length;
    }

    public String getFrameWidth() {
        return frameWidth;
    }

    public String getFramerate() {
        return framerate;
    }

    public String getFrameHeight() {
        return frameHeight;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public byte[] getMultimediaFileChunk() {
        return multimediaFileChunk;
    }

}