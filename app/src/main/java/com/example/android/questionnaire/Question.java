package com.example.android.questionnaire;

public class Question {

    private String question;
    private String[] options;
    private Options optionsType;

    public Question(String question, Options optionsType) {
        this.question = question;
        this.optionsType = optionsType;
    }

    public Question(String question, String[] options, Options optionsType) {
        this.question = question;
        this.options = options;
        this.optionsType = optionsType;
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
}
