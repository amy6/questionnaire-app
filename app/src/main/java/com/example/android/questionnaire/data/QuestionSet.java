package com.example.android.questionnaire.data;

import android.content.Context;

import com.example.android.questionnaire.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.android.questionnaire.data.Options.CHECKBOX;
import static com.example.android.questionnaire.data.Options.RADIOBUTTON;

public class QuestionSet {

    public static ArrayList<Question> getAllQuestions(Context context) {
        ArrayList<Question> questions = new ArrayList<>();

        Question question1 = new Question(context.getString(R.string.q1), RADIOBUTTON, context.getResources().getStringArray(R.array.q1_options), Collections.singletonList(0));
        questions.add(question1);

        Question question2 = new Question(context.getString(R.string.q2), Options.CHECKBOX, context.getResources().getStringArray(R.array.q2_options), Arrays.asList(0, 1));
        questions.add(question2);

        Question question3 = new Question(context.getString(R.string.q3), Options.EDITTEXT, context.getString(R.string.q3_answer));
        questions.add(question3);

        Question question4 = new Question(context.getString(R.string.q4), RADIOBUTTON, context.getResources().getStringArray(R.array.q4_options), Collections.singletonList(3));
        questions.add(question4);

        Question question5 = new Question(context.getString(R.string.q5), CHECKBOX, context.getResources().getStringArray(R.array.q5_options), Arrays.asList(0, 1, 3));
        questions.add(question5);

        Question question6 = new Question(context.getString(R.string.q6), RADIOBUTTON, context.getResources().getStringArray(R.array.q6_options), Collections.singletonList(1));
        questions.add(question6);

        Question question7 = new Question(context.getString(R.string.q7), Options.EDITTEXT, context.getString(R.string.q7_answer));
        questions.add(question7);

        return questions;
    }
}
