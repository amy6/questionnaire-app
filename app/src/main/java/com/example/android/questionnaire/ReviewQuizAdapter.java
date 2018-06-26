package com.example.android.questionnaire;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import static com.example.android.questionnaire.MainActivity.QUESTION_NUMBER;

public class ReviewQuizAdapter extends ArrayAdapter<Question> {


    ReviewQuizAdapter(@NonNull Context context, List<Question> questions) {
        super(context, 0, questions);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_review_list_item, parent, false);
        }

        final Question question = getItem(position);
        TextView questionTextView = convertView.findViewById(R.id.review_question_textview);
        TextView answerTextView = convertView.findViewById(R.id.review_answer_textview);
        Button goToButton = convertView.findViewById(R.id.goto_button);

        if (question != null) {
            questionTextView.setText(question.getQuestion());
            answerTextView.setText(question.getAnswer());
            goToButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(QUESTION_NUMBER, question.getqNumber());
                    getContext().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
