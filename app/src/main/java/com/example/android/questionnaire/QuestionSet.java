package com.example.android.questionnaire;


import java.util.ArrayList;

public class QuestionSet {

    //com.example.android.questionnaire.QuestionSet -> Represents all questions and options (Array of QuestionAndOptions objects)

    //AnswerSetService - submitAnswer(Array<QuestionAndAnswer> array)

    ArrayList<Question> questions;

    /**
     * Set the list of questions and it's options to be displayed to the user
     */
    public ArrayList<Question> getQuestionSet() {
        questions = new ArrayList<>();

        String[] hpBooks = new String[]{"Stone", "Chamber", "Azkaban", "Goblet", "Phoenix", "Prince", "Hallows"};
        Question question1 = new Question("Favorite HP book?", hpBooks, Options.RADIOBUTTON);
        questions.add(question1);

        String[] hpCharacters = new String[]{"Harry", "Ron", "Hermione", "Dobby", "Dumbledore", "Snape"};
        Question question2 = new Question("Favorite HP characters?", hpCharacters, Options.CHECKBOX);
        questions.add(question2);

        Question question3 = new Question("Favorite Stark kid?", Options.EDITTEXT);
        questions.add(question3);

        return questions;
    }

}
