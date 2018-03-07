package com.example.android.questionnaire;


import java.io.Serializable;

public class Answer implements Serializable{

    private String question;
    private String answer;

    public Answer(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
