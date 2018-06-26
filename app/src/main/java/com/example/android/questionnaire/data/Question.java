package com.example.android.questionnaire.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

    private String question;
    private Options optionsType;
    private String[] options;
    private String answer;
    private List<Integer> answerId;
    private String userAnswer;
    private List<Integer> userSetAnswerId;
    private boolean markedForReview;
    private int qNumber;

    public Question(String question, Options optionsType, String answer) {
        this.question = question;
        this.optionsType = optionsType;
        this.answer = answer;
    }

    public Question(String question, Options optionsType, String[] options, List<Integer> answerId) {
        this.question = question;
        this.optionsType = optionsType;
        this.options = options;
        this.answerId = answerId;
    }

    public Question(String question, String answer, int qNumber) {
        this.question = question;
        this.answer = answer;
        this.qNumber = qNumber;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public Options getOptionsType() {
        return optionsType;
    }

    public String getAnswer() {
        return answer;
    }

    public List<Integer> getAnswerId() {
        return answerId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public List<Integer> getUserSetAnswerId() {
        return userSetAnswerId;
    }

    public void setUserSetAnswerId(ArrayList<Integer> userSetAnswerId) {
        this.userSetAnswerId = userSetAnswerId;
    }

    public boolean isMarkedForReview() {
        return markedForReview;
    }

    public void setMarkedForReview(boolean markedForReview) {
        this.markedForReview = markedForReview;
    }

    public int getqNumber() {
        return qNumber;
    }

}
