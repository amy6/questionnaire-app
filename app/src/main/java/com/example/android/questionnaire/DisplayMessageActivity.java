package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.DISPLAY_MESSAGE);
        int score = intent.getIntExtra(MainActivity.SCORE, 0);

        TextView answer = findViewById(R.id.answer_text_view);
        message += "\n Your score is: " + score;
        answer.setText(message);
    }
}
