package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.android.questionnaire.MainActivity.LOG_TAG;
import static com.example.android.questionnaire.MainActivity.QUESTIONS;

public class ReviewListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        TextView emptyTextView = findViewById(R.id.empty_textview);

        ListView reviewListView = findViewById(R.id.review_listview);
        reviewListView.setEmptyView(emptyTextView);

        Intent intent = getIntent();
//        ArrayList<Integer> reviewChecklist = intent.getIntegerArrayListExtra("REVIEW_CHECKLIST");

        ArrayList<Question> questions = (ArrayList<Question>) intent.getSerializableExtra(QUESTIONS);

        ArrayList<Question> reviewQuestions = new ArrayList<>();

//        if(reviewChecklist != null){
        for (int index = 0; index < questions.size(); index++) {
//                if(index != -1) {

            Question question = questions.get(index);
            if (question.isMarkedForReview()) {
                Log.d(LOG_TAG, "Current Question Number has been marked?  " + question.isMarkedForReview());
                String[] options = question.getOptions();
                Options optionsType = question.getOptionsType();
                Question reviewQuestion = null;
                switch (optionsType) {
                    case RADIOBUTTON:
                        Log.d(LOG_TAG, "RB CASE");
                        if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                            int id = question.getUserSetAnswerId().get(0);
                            reviewQuestion = new Question(question.getQuestion(), options[id], index);
                        } else {
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }
                        break;
                    case CHECKBOX:
                        Log.d(LOG_TAG, "CB CASE");
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
                        Log.d(LOG_TAG, "ET CASE");
                        if (!TextUtils.isEmpty(question.getUserAnswer())) {
                            reviewQuestion = new Question(question.getQuestion(), question.getUserAnswer(), index);
                        } else {
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }

                        break;
                }
                reviewQuestions.add(reviewQuestion);
            }

//                } else {
//                    Log.d(LOG_TAG, "Index is -1");
//                }
        }
//        } else {
//            Log.d(LOG_TAG, "ReviewChecklist is null");
//        }

        ReviewQuizAdapter quizAdapter = new ReviewQuizAdapter(ReviewListActivity.this, reviewQuestions);
        reviewListView.setAdapter(quizAdapter);

        /*Button gotoQuestions = findViewById(R.id.goto_questions_button);
        if(reviewQuestions.size() == 0 ) {
            gotoQuestions.setVisibility(View.INVISIBLE);
        }
        gotoQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

    }
}
