package com.example.android.questionnaire.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.questionnaire.R;
import com.example.android.questionnaire.data.Options;
import com.example.android.questionnaire.data.Question;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<Question> questions;
    //a boolean array indicating which questions have been correctly answered by the user
    //used to set the text color when displaying the answer
    private boolean[] correctAnswers;

    public ResultsAdapter(Context context, ArrayList<Question> questions, boolean[] validateAnswers) {
        this.context = context;
        this.questions = questions;
        this.correctAnswers = validateAnswers;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_results_list_item, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        //get the current question and it's options
        Question question = questions.get(position);
        String[] options = question.getOptions();
        Options optionsType = question.getOptionsType();

        //variables for displaying the answers
        StringBuilder correctAnswer = new StringBuilder();
        StringBuilder userAnswer = new StringBuilder();

        switch (optionsType) {
            case RADIOBUTTON:
                //get the correct answer
                correctAnswer.append(options[question.getAnswerId().get(0)]);
                if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                    if (correctAnswers[position]) {
                        //if user answer is correct, then directly assign the correct answer from question object
                        userAnswer = correctAnswer;
                    } else {
                        //get the user answer
                        userAnswer.append(options[question.getUserSetAnswerId().get(0)]);
                    }
                }
                break;
            case CHECKBOX:
                for (int i = 0; i < question.getAnswerId().size(); i++) {
                    if (i > 0) correctAnswer.append(", ");
                    //get the correct answers
                    correctAnswer.append(options[question.getAnswerId().get(i)]);
                }
                if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                    if (correctAnswers[position]) {
                        //if user answer is correct, then directly assign the correct answer from question object
                        userAnswer = correctAnswer;
                    } else {
                        for (int j = 0; j < question.getUserSetAnswerId().size(); j++) {
                            if (j > 0) userAnswer.append(", ");
                            //get the user answers
                            userAnswer.append(options[question.getUserSetAnswerId().get(j)]);
                        }
                    }
                }
                break;
            case EDITTEXT:
                //get the correct answer
                correctAnswer.append(question.getAnswer());
                if (!TextUtils.isEmpty(question.getUserAnswer())) {
                    if (correctAnswers[position]) {
                        //if user answer is correct, then directly assign the correct answer from question object
                        userAnswer = correctAnswer;
                    } else {
                        //get the user answer
                        //set the fist letter capital in the user answer
                        userAnswer.append(question.getUserAnswer().substring(0, 1).toUpperCase()).append(question.getUserAnswer().substring(1));
                    }
                }
                break;
        }

        //set the text views accordingly
        holder.questionTextView.setText(question.getQuestion());
        holder.correctAnswerTextView.setText(correctAnswer);
        holder.userAnswerTextView.setText(TextUtils.isEmpty(userAnswer) ? "Unanswered" : userAnswer);
        //set the text color for correct and incorrect answers
        if (correctAnswers[position]) {
            holder.userAnswerTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.userAnswerTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView questionTextView;
        private TextView userAnswerTextView;
        private TextView correctAnswerTextView;

        CardViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question_text_view);
            userAnswerTextView = itemView.findViewById(R.id.user_answer);
            correctAnswerTextView = itemView.findViewById(R.id.correct_answer);
        }
    }


}
