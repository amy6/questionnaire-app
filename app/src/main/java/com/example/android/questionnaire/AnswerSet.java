package com.example.android.questionnaire;

import java.util.ArrayList;

/**
 * Created by mahima on 24/2/18.
 */

public class AnswerSet {

    private ArrayList<Answer> answers;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setUpAnswers() {
        answers = new ArrayList<>();

        Answer answer1 = new Answer("Favorite HP book?", "Prince");
        answers.add(answer1);

        Answer answer2 = new Answer("Favorite HP characters?", "Harry Ron Hermione");
        answers.add(answer2);

        Answer answer3 = new Answer("Favorite Stark kid?", "Arya");
        answers.add(answer3);
    }

    public void validateAnswers(ArrayList<Answer> userAnswers) {

        String userAnswer;
        String correctAnswer;

        for (int i = 0; i < userAnswers.size(); i++) {
            userAnswer = userAnswers.get(i).getAnswer().replaceAll("\\s+", "");
            correctAnswer = answers.get(i).getAnswer().replaceAll("\\s+", "");

            if (userAnswer.equalsIgnoreCase(correctAnswer))
                score++;

        }
    }

}
