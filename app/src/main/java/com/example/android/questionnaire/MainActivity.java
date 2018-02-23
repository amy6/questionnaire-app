package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "MESSAGE";
    TextView question_text_view;
    LinearLayout optionsLinearLayout;
    Button nextButton;
    private int qNumber;
    private ArrayList<Question> questions;
    /**
     * Set up a listener for when the user chooses to go to the next Question
     */
    private View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.v("Mainactivity", "QNumber: " + qNumber);
            if (qNumber < questions.size()) {
                optionsLinearLayout.removeAllViews();
                displayQuestion();
            }
            /**
             * Start a new activity and display a user message with the results once all questions have been
             * displayed
             */
            else {
                Intent intent = new Intent(MainActivity.this,
                        DisplayMessageActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Thank you for completing the quiz!");
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question_text_view = findViewById(R.id.question_text);
        optionsLinearLayout = findViewById(R.id.linearLayout_Options);
        nextButton = findViewById(R.id.next_button);

        setUpQuestions();

        displayQuestion();

        nextButton.setOnClickListener(nextButtonClickListener);

    }

    /**
     * Set the list of questions and it's options to be displayed to the user
     */
    private void setUpQuestions() {
        questions = new ArrayList<>();

        String[] hpBooks = new String[]{"Stone", "Chamber", "Azkaban", "Goblet", "Phoenix", "Prince", "Hallows"};
        Question question1 = new Question("Favorite HP book?", hpBooks, Options.RADIOBUTTON);
        questions.add(question1);

        String[] hpCharacters = new String[]{"Harry", "Ron", "Hermione", "Dobby", "Dumbledore", "Snape"};
        Question question2 = new Question("Favorite HP characters?", hpCharacters, Options.CHECKBOX);
        questions.add(question2);

        Question question3 = new Question("Favorite Stark kid?", Options.EDITTEXT);
        questions.add(question3);
    }

    /**
     * Display the questions from the set along with it's options, each question can
     * have different number of options and different type of views for the inputs
     */
    private void displayQuestion() {

        Question currentSet;
        String[] optionsCurrentSet;

        if (qNumber < questions.size()) {
            currentSet = questions.get(qNumber++);
            question_text_view.setText(currentSet.getQuestion());
            optionsCurrentSet = currentSet.getOptions();
            Options type = currentSet.getOptionsType();

            Log.v("MainActivity", "Calling displayQuestion for QuestionNo. " + qNumber
                    + "\n\n OptionsType for QNo. " + qNumber + " is " + type.toString());

            displayOptions(optionsCurrentSet, type);

        }

    }

    /**
     * Display the options for the given question
     *
     * @param options     list of options for the question
     * @param optionsType type of view for the options
     */

    private void displayOptions(String[] options, Options optionsType) {
        Log.v("MainActivity", "displayOptions for QNo. " + qNumber);
        int numOfOptions = 0;
        if (optionsType.equals(Options.RADIOBUTTON) || optionsType.equals(Options.CHECKBOX))
            numOfOptions = options.length;
        String type = optionsType.toString();
        Options opType = Options.valueOf(type);
        Log.v("MainActivity", Options.valueOf(type).toString());

        switch (opType) {

            case RADIOBUTTON:
                RadioGroup radioGroup = new RadioGroup(this);
                for (int i = 0; i < numOfOptions; i++) {
                    RadioButton button = new RadioButton(this);
                    button.setText(options[i]);
                    radioGroup.addView(button);
                }
                optionsLinearLayout.addView(radioGroup);
                break;


            case CHECKBOX:
                for (int i = 0; i < numOfOptions; i++) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(options[i]);
                    optionsLinearLayout.addView(checkBox);
                }
                break;

            case EDITTEXT:
                EditText editText = new EditText(this);
                editText.setHint("Enter your answer");
                optionsLinearLayout.addView(editText);
                break;
        }

    }
}
