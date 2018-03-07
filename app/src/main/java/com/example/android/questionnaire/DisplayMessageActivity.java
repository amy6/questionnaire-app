package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayMessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizStatsAdapter quizStatsAdapter;
    private ArrayList<String> userAnswerList;
    private AnswerSet answerSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();

        userAnswerList = intent.getStringArrayListExtra(MainActivity.USER_ANSWER);
        answerSet = (AnswerSet) intent.getSerializableExtra(MainActivity.ANSWER_SET);
        int score = answerSet.getScore();

        TextView answer = findViewById(R.id.answer_text_view);
        String message = getString(R.string.score_text) + score;
        answer.setText(message);

        quizStatsAdapter = new QuizStatsAdapter(userAnswerList, answerSet);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(quizStatsAdapter);

    }
}
