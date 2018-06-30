package com.example.android.questionnaire;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.questionnaire.data.Options;
import com.example.android.questionnaire.data.Question;
import com.example.android.questionnaire.utils.ResultsAdapter;

import java.util.ArrayList;

import static com.example.android.questionnaire.MainActivity.QUESTIONS;

public class ResultsActivity extends AppCompatActivity {

    private ArrayList<Question> questions;
    private boolean[] validateAnswers;
    private int score;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list);

        //display back arrow on ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //get questions array list from intent passed from MainActivity
        questions = (ArrayList<Question>) getIntent().getSerializableExtra(QUESTIONS);
        validateAnswers = validateAnswers();

        //set the score based on the results after validating the answers
        for (boolean b : validateAnswers) {
            if (b) score++;
        }

        //display the score
        String message = getString(R.string.score_text)
                .concat(String.valueOf(score))
                .concat(getString(R.string.score_text_append))
                .concat(String.valueOf(questions.size()));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        ResultsAdapter resultsAdapter = new ResultsAdapter(this, questions, validateAnswers);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resultsAdapter);

    }

    /**
     * validates the user answers against the correct answer
     *
     * @return boolean array indicating the correct answers for all the questions
     */
    private boolean[] validateAnswers() {
        validateAnswers = new boolean[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Options opType = question.getOptionsType();

            switch (opType) {
                case CHECKBOX:
                    if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                        //get total number of correct answers to be checked
                        int count = question.getAnswerId().size();
                        //verify if the user has checked the same number of options
                        if (count == question.getUserSetAnswerId().size()) {
                            for (int index : question.getAnswerId()) {
                                if (question.getUserSetAnswerId().contains(index)) {
                                    count--;
                                }
                            }
                        }
                        //set the answer as correct only if the user has selected all checkbox options required
                        if (count == 0) {
                            validateAnswers[i] = true;
                        }
                    }
                    break;
                case EDITTEXT:
                    if (!TextUtils.isEmpty(question.getUserAnswer())) {
                        //compare the user answer with the correct answer
                        if (question.getAnswer().equalsIgnoreCase(question.getUserAnswer())) {
                            validateAnswers[i] = true;
                        }
                    }
                    break;
                case RADIOBUTTON:
                    if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {                        //compare the user answer with the correct answer
                        //compare the user answer with the correct answer
                        if (question.getAnswerId().get(0).equals(question.getUserSetAnswerId().get(0))) {
                            validateAnswers[i] = true;
                        }
                    }
                    break;
            }
        }
        return validateAnswers;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
