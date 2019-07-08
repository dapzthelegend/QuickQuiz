package com.dapzthelegend.quickquiz;

public class Subject {
    private String subject;
    private int image;

    public String getSubject() {
        return subject;
    }

    public Subject(String subject, int image) {
        this.subject = subject;
        this.image = image;
    }

    public void setSubject(String subject) {
        this.subject = subject;

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
