package com.example.android.questionnaire;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class QuizStatsAdapter extends RecyclerView.Adapter {

    private ArrayList<String> userAnswers;
    private AnswerSet answerSet;

    private class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView question;
        private TextView userAnswer;
        private TextView correctAnswer;

        CardViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question_text_view);
            userAnswer = itemView.findViewById(R.id.user_answer);
            correctAnswer = itemView.findViewById(R.id.correct_answer);
        }
    }

    QuizStatsAdapter(ArrayList<String> userAnswers, AnswerSet answerSet) {
        this.userAnswers = userAnswers;
        this.answerSet = answerSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_stats, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CardViewHolder cardViewHolder = (CardViewHolder) holder;

        ArrayList<Answer> correctAnswers = answerSet.getAnswers();
        Answer answer = correctAnswers.get(position);

        int[] answerIndex = answerSet.getIndex();
        String answerText = answer.getAnswer();

        cardViewHolder.question.setText(answer.getQuestion());

        cardViewHolder.userAnswer.setTextColor(Color.GREEN);
        cardViewHolder.correctAnswer.setText(answerText);

        if (answerIndex[position] == -1) {
            answerText = userAnswers.get(position);
            cardViewHolder.userAnswer.setTextColor(Color.RED);
        }
        cardViewHolder.userAnswer.setText(answerText);

    }

    @Override
    public int getItemCount() {
        return userAnswers.size();
    }


}
