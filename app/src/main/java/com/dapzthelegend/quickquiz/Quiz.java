package com.dapzthelegend.quickquiz;

public class Quiz {
    private String Question, A,B,C,D,E;

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getE() {
        return E;
    }

    public void setE(String e) {
        E = e;
    }

    public boolean contains(String key){
        boolean status = false;
        String q = Question + " " + A + " " + B+ " " + C +" " + D+ " " +E;

        if(q.contains(key)){
            status = true;

        }
        return status;
    }
}
