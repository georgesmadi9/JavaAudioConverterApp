package com.example;

public class AudioFile {

    private final String fName; // filename
    private final Integer cDate; // creation date in milliseconds

    // constructor
    public AudioFile(String filename, Integer creationDate) {
        fName = filename;
        cDate = creationDate;
    }

    // fName getter
    public String getfName() {
        return fName;
    }

    // cDate getter
    public Integer getcDate() {
        return cDate;
    }

    // String version of the audio file
    @Override
    public String toString() {
        return "com.example.AudioFile{" +
                "fName='" + fName + '\'' +
                ", cDate=" + cDate +
                '}';
    }
}
