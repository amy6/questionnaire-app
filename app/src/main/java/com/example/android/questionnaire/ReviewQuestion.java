package com.example.android.questionnaire;

public class ReviewQuestion {

    private String question;
    private String answer;

    public ReviewQuestion(String question, String answer) {
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
