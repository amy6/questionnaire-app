package com.example.android.questionnaire;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.android.questionnaire.MainActivity.QUESTIONS;

public class DisplayMessageActivity extends AppCompatActivity {

    private ArrayList<Question> questions;
    private boolean[] validateAnswers;
    private int score;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        questions = (ArrayList<Question>) getIntent().getSerializableExtra(QUESTIONS);
        validateAnswers = validateAnswers();

        for (boolean b : validateAnswers) {
            if (b) score++;
        }

        TextView textView = findViewById(R.id.answer_text_view);
        textView.append(String.valueOf(score));

        QuizStatsAdapter quizStatsAdapter = new QuizStatsAdapter(questions, validateAnswers);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(quizStatsAdapter);

    }

    private boolean[] validateAnswers() {
        validateAnswers = new boolean[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Options opType = question.getOptionsType();
            switch (opType) {
                case CHECKBOX:
                    if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                        int count = question.getAnswerId().size();
                        if (count == question.getUserSetAnswerId().size()) {
                            for (int index : question.getAnswerId()) {
                                if (question.getUserSetAnswerId().contains(index)) {
                                    count--;
                                }
                            }
                        }
                        if (count == 0) {
                            validateAnswers[i] = true;
                        }
                    }
                    break;
                case EDITTEXT:
                    if (!TextUtils.isEmpty(question.getUserAnswer())) {
                        if (question.getAnswer().equalsIgnoreCase(question.getUserAnswer())) {
                            validateAnswers[i] = true;
                        }
                    }
                    break;
                case RADIOBUTTON:
                    if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                        if (question.getAnswerId().get(0).equals(question.getUserSetAnswerId().get(0))) {
                            validateAnswers[i] = true;
                        }
                    }
                    break;
            }
        }
        return validateAnswers;
    }
}
