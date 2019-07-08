package com.dapzthelegend.quickquiz;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Words {
    private String key, link, definition;
    private List<Quiz> Questions;


    public List<Quiz> getQuestions() {
        return Questions;
    }

    public void setQuestions(List<Quiz> questions) {
        Questions = questions;
    }

    public String getKey() {
        return key;
    }

    public Words(){}

    public Words(String key, String link, String definition, List<Quiz> questions) {
        this.Questions = questions;
        this.key = key;
        this.link = link;
        this.definition = definition;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
