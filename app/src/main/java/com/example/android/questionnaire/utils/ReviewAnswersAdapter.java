package com.example.android.questionnaire.utils;

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

import com.example.android.questionnaire.MainActivity;
import com.example.android.questionnaire.R;
import com.example.android.questionnaire.data.Question;

import java.util.List;

import static com.example.android.questionnaire.MainActivity.QUESTION_NUMBER;

public class ReviewAnswersAdapter extends ArrayAdapter<Question> {


    public ReviewAnswersAdapter(@NonNull Context context, List<Question> questions) {
        super(context, 0, questions);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //use a previous view if available, inflate a new one otherwise
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_review_list_item, parent, false);
        }

        final Question question = getItem(position);
        //get references to views in an individual list item
        TextView questionTextView = convertView.findViewById(R.id.review_question_textview);
        TextView answerTextView = convertView.findViewById(R.id.review_answer_textview);
        Button goToButton = convertView.findViewById(R.id.goto_button);

        if (question != null) {
            questionTextView.setText(question.getQuestion());
            answerTextView.setText(question.getAnswer());
            goToButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //goToButton takes the user to the specific question present in the review list
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    //ensure that the activity is not reloaded, so as to preserve the state of the questions object
                    //below set flags make sure any activities in the stack are cleared rather than restarting the target activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(QUESTION_NUMBER, question.getqNumber());
                    getContext().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
