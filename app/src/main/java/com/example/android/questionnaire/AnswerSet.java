package com.example.android.questionnaire;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mahima on 24/2/18.
 */

public class AnswerSet implements Serializable{

    private ArrayList<Answer> answers;
    private int score;
    private int[] index;

    public int getScore() {
        return score;
    }

    public int[] getIndex() {
        return index;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
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

    public void validate(ArrayList<String> userAnswers) {
        index = new int[userAnswers.size()];

        String userAnswer;
        String correctAnswer;

        for (int i = 0; i < userAnswers.size(); i++) {
            userAnswer = userAnswers.get(i).replaceAll("\\s+", "");
            correctAnswer = answers.get(i).getAnswer().replaceAll("\\s+", "");

            if (userAnswer.equalsIgnoreCase(correctAnswer))
                score++;
            else
                index[i] = -1;

        }
    }

}
