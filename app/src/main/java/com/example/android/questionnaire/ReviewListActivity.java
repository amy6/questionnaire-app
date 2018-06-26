package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.android.questionnaire.MainActivity.QUESTIONS;

public class ReviewListActivity extends AppCompatActivity {

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        TextView emptyTextView = findViewById(R.id.empty_textview);

        ListView reviewListView = findViewById(R.id.review_listview);
        reviewListView.setEmptyView(emptyTextView);

        Intent intent = getIntent();

        ArrayList<Question> questions = (ArrayList<Question>) intent.getSerializableExtra(QUESTIONS);

        ArrayList<Question> reviewQuestions = new ArrayList<>();

        for (int index = 0; index < questions.size(); index++) {

            Question question = questions.get(index);
            if (question.isMarkedForReview()) {
                String[] options = question.getOptions();
                Options optionsType = question.getOptionsType();
                Question reviewQuestion = null;
                switch (optionsType) {
                    case RADIOBUTTON:
                        if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                            int id = question.getUserSetAnswerId().get(0);
                            reviewQuestion = new Question(question.getQuestion(), options[id], index);
                        } else {
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }
                        break;
                    case CHECKBOX:
                        StringBuilder answer = new StringBuilder();
                        if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                            for (int i : question.getUserSetAnswerId()) {
                                if (i > 0) {
                                    answer.append(" ");
                                }
                                answer.append(options[i]);
                            }
                            reviewQuestion = new Question(question.getQuestion(), answer.toString(), index);
                        } else {
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }

                        break;
                    case EDITTEXT:
                        if (!TextUtils.isEmpty(question.getUserAnswer())) {
                            reviewQuestion = new Question(question.getQuestion(), question.getUserAnswer(), index);
                        } else {
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }

                        break;
                }
                reviewQuestions.add(reviewQuestion);
            }
        }

        ReviewQuizAdapter quizAdapter = new ReviewQuizAdapter(ReviewListActivity.this, reviewQuestions);
        reviewListView.setAdapter(quizAdapter);
    }
}
