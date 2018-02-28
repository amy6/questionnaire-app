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

    public static final String DISPLAY_MESSAGE = "MESSAGE";
    public static final String SCORE = "SCORE";

    TextView question_text_view;
    LinearLayout optionsLinearLayout;
    Button nextButton;

    private int qNumber;
    private int score;

    private Options optionsType;
    private View optionsView;

    private QuestionSet questionSet;
    private ArrayList<Question> questions;

    private AnswerSet answerSet;
    private ArrayList<Answer> answers;
    /**
     * Set up a listener for when the user chooses to go to the next Question
     */
    private View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.v("Mainactivity", "QNumber: " + qNumber);
            String answer = getUserAnswer(optionsView, optionsType.toString());
            answers.add(new Answer(questions.get(qNumber - 1).getQuestion(), answer));
            if (qNumber < questions.size()) {
                optionsLinearLayout.removeAllViews();
                displayQuestion();
            }
            /**
             * Start a new activity and display a user message with the results once all questions have been
             * displayed
             */
            else {
                answerSet.validateAnswers(answers);
                score = answerSet.getScore();
                Intent intent = new Intent(MainActivity.this,
                        DisplayMessageActivity.class);
                intent.putExtra(DISPLAY_MESSAGE, "Thank you for completing the quiz!");
                intent.putExtra(SCORE, score);
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

        questionSet = new QuestionSet();
        questions = questionSet.getQuestionSet();

        answerSet = new AnswerSet();
        answers = new ArrayList<>();

        answerSet.setUpAnswers();

        displayQuestion();

        nextButton.setOnClickListener(nextButtonClickListener);

    }

    /**
     * Fetch the answer selected by the user
     * @param view specifies the options view for the current question
     * @param type specifies the options type for the current question
     */

    private String getUserAnswer(View view, String type) {
        String answer = "";
        switch (Options.valueOf(type)) {
            case RADIOBUTTON:
                RadioButton selectedRadioButton = findViewById(((RadioGroup) view).getCheckedRadioButtonId());
                answer = selectedRadioButton.getText().toString();
                break;

            case CHECKBOX:
                LinearLayout parentLayout = (LinearLayout) view;
                int numOfCheckBox = parentLayout.getChildCount();
                for (int i = 0; i < numOfCheckBox; i++) {
                    CheckBox childCheckBox = (CheckBox) parentLayout.getChildAt(i);
                    if (childCheckBox.isChecked())
                        answer += childCheckBox.getText();
                }

                break;

            case EDITTEXT:
                EditText answerText = (EditText) view;
                answer = answerText.getText().toString();
                break;
        }
        return answer;

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
        int numOfOptions = 0;
        if (optionsType.equals(Options.RADIOBUTTON) || optionsType.equals(Options.CHECKBOX))
            numOfOptions = options.length;
        String type = optionsType.toString();
        Options opType = Options.valueOf(type);

        switch (opType) {

            case RADIOBUTTON:
                RadioGroup radioGroup = new RadioGroup(this);
                for (int i = 0; i < numOfOptions; i++) {
                    RadioButton button = new RadioButton(this);
                    button.setText(options[i]);
                    radioGroup.addView(button);
                }
                optionsView = radioGroup;
                optionsLinearLayout.addView(radioGroup);
                break;


            case CHECKBOX:
                for (int i = 0; i < numOfOptions; i++) {
                    CheckBox checkbox = new CheckBox(this);
                    checkbox.setText(options[i]);
                    optionsLinearLayout.addView(checkbox);
                }
                optionsView = optionsLinearLayout;
                break;

            case EDITTEXT:
                EditText editText = new EditText(this);
                editText.setHint(R.string.editText_hint);
                optionsLinearLayout.addView(editText);
                optionsView = editText;
                break;
        }
        this.optionsType = opType;

    }
}
