package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.questionnaire.data.Options;
import com.example.android.questionnaire.data.Question;
import com.example.android.questionnaire.utils.ReviewAnswersAdapter;

import java.util.ArrayList;

import static com.example.android.questionnaire.MainActivity.QUESTIONS;

public class ReviewAnswersActivity extends AppCompatActivity {

    private ArrayList<Question> questions;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        //get reference to ListView, set empty view for the ListView
        TextView emptyTextView = findViewById(R.id.empty_textview);
        ListView reviewListView = findViewById(R.id.review_listview);
        reviewListView.setEmptyView(emptyTextView);

        //get questions object passed via intent from MainActivity
        Intent intent = getIntent();
        questions = (ArrayList<Question>) intent.getSerializableExtra(QUESTIONS);

        //ArrayList for the list of questions marked for review
        ArrayList<Question> reviewQuestions = getReviewQuestions();

        ReviewAnswersAdapter quizAdapter = new ReviewAnswersAdapter(ReviewAnswersActivity.this, reviewQuestions);
        reviewListView.setAdapter(quizAdapter);
    }

    private ArrayList<Question> getReviewQuestions() {
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
                            //get the current user checked RadioButton id
                            int id = question.getUserSetAnswerId().get(0);
                            //add the question to the review list, set the question number index to be retrieved later
                            reviewQuestion = new Question(question.getQuestion(), options[id], index);
                        } else {
                            //set user answer as 'Unanswered' for questions currently being displayed
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }
                        break;
                    case CHECKBOX:
                        StringBuilder answer = new StringBuilder();
                        if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                            for (int i = 0; i < question.getUserSetAnswerId().size(); i++) {
                                if (i > 0) {
                                    answer.append(", ");
                                }
                                //get the answers from the user selected check boxes,
                                // append multiple answers if any as a single string
                                answer.append(options[question.getUserSetAnswerId().get(i)]);
                            }
                            //add the question to the review list, set the question number index to be retrieved later
                            reviewQuestion = new Question(question.getQuestion(), answer.toString(), index);
                        } else {
                            //set user answer as 'Unanswered' for questions currently being displayed
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }

                        break;
                    case EDITTEXT:
                        if (!TextUtils.isEmpty(question.getUserAnswer())) {
                            //add the question to the review list, set the question number index to be retrieved later
                            //set the fist letter capital in the user answer
                            reviewQuestion = new Question(question.getQuestion(), question.getUserAnswer()
                                    .substring(0, 1).toUpperCase()
                                    .concat(question.getUserAnswer().substring(1)), index);
                        } else {
                            //set user answer as 'Unanswered' for questions currently being displayed
                            reviewQuestion = new Question(question.getQuestion(), "Unanswered", index);
                        }

                        break;
                }
                reviewQuestions.add(reviewQuestion);
            }
        }
        return reviewQuestions;
    }
}
