package com.ucllc.smcalculatorlock.DataClasses;

public class LockedFile {
    String sourcePath;
    String fileName;
    String hash;
    String date;

    public LockedFile(String sourcePath, String fileName, String hash, String date) {
        this.sourcePath = sourcePath;
        this.fileName = fileName;
        this.hash = hash;
        this.date = date;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
