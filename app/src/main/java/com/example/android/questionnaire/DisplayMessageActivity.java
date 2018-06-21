package com.example.android.questionnaire;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.android.questionnaire.MainActivity.LOG_TAG;
import static com.example.android.questionnaire.MainActivity.QUESTIONS;

public class DisplayMessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizStatsAdapter quizStatsAdapter;
    private ArrayList<String> userAnswerList;
    private AnswerSet answerSet;
    private ArrayList<Question> questions;
    private boolean[] validateAnswers;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        questions = (ArrayList<Question>) getIntent().getSerializableExtra(QUESTIONS);
        validateAnswers = validateAnswers();

        for (boolean b : validateAnswers) {
            Log.d(LOG_TAG, String.valueOf(b));
            if (b) score++;
        }

        TextView textView = findViewById(R.id.answer_text_view);
        textView.append(String.valueOf(score));

        /*HashMap<String, String> answers = new HashMap<>();

        for(int i=0; i<questions.size(); i++) {
            Question question = questions.get(i);
            if(question.getOptionsType().equals(EDITTEXT)) {
                answers.put(question.getAnswer(), question.getUserAnswer());
            }
            else if (question.getOptionsType().equals(CHECKBOX) || question.getOptionsType().equals(RADIOBUTTON)) {
                String[] options = question.getOptions();
                StringBuilder correctAnswer = new StringBuilder();
                StringBuilder userAnswer = new StringBuilder();
                for(int index : question.getUserSetAnswerId()) {
                    correctAnswer.append(options[question.getAnswerId().get(index)]);
                    userAnswer.append(options[question.getUserSetAnswerId().get(index)]);
                }
                answers.put(correctAnswer.toString(), userAnswer.toString());
            }
        }*/


        quizStatsAdapter = new QuizStatsAdapter(DisplayMessageActivity.this, questions, validateAnswers);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(quizStatsAdapter);


        /*Intent intent = getIntent();

        userAnswerList = intent.getStringArrayListExtra(MainActivity.USER_ANSWER);
        answerSet = (AnswerSet) intent.getSerializableExtra(MainActivity.ANSWER_SET);
        int score = answerSet.getScore();

        TextView answer = findViewById(R.id.answer_text_view);
        String message = getString(R.string.score_text) + score;
        answer.setText(message);

        quizStatsAdapter = new QuizStatsAdapter(userAnswerList, answerSet);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(quizStatsAdapter);*/

    }

    private boolean[] validateAnswers() {
        validateAnswers = new boolean[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Options opType = question.getOptionsType();
            switch (opType) {
                case CHECKBOX:
                    int count = question.getAnswerId().size();
                    Log.d(LOG_TAG, "Number of options to be selected: " + count);
                    if (count == question.getUserSetAnswerId().size()) {
                        Log.d(LOG_TAG, "Number of options selected are indeed equal: " + count);
                        for (int index : question.getAnswerId()) {
//                            Log.d(LOG_TAG, question.getAnswerId().get(index) + "\t" + question.getUserSetAnswerId().get(index));
                            if (question.getUserSetAnswerId().contains(index)) {
                                count--;
                            }
                        }
                    }
                    if (count == 0) {
                        validateAnswers[i] = true;
                    }
                    break;
                case EDITTEXT:
                    if (question.getAnswer().equalsIgnoreCase(question.getUserAnswer())) {
                        validateAnswers[i] = true;
                    }
                    break;
                case RADIOBUTTON:
                    if (question.getAnswerId().get(0).equals(question.getUserSetAnswerId().get(0))) {
                        validateAnswers[i] = true;
                    }
                    break;
            }
        }
        return validateAnswers;
    }
}
