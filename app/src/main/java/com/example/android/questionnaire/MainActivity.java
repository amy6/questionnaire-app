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
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String ANSWER_SET = "ANSWER_SET";
    public static final String USER_ANSWER = "USER_ANSWER_LIST";
    private static final String QUESTION_NUMBER = "QUESTION_NUMBER";
    private static final String CHOSEN_ANSWER = "CHOSEN_ANSWER";
    private static final String OPTIONS_TYPE = "OPTIONS_TYPE";
    private static final String EDITTEXT_ANSWER = "EDIT_TEXT_ANSWER";
    private static final String EDITTEXT_ANSWER_SET = "EDIT_TEXT_ANSWER_SET";

    TextView question_text_view;
    LinearLayout optionsLinearLayout;
    Button nextButton;

    private int qNumber;
    private ArrayList<Integer> checkedId;

    private boolean editTextAnswerSet;
    private String editTextAnswer;

    private Options optionsType;
    private View optionsView;

    private QuestionSet questionSet;
    private ArrayList<Question> questions;

    private AnswerSet answerSet;
    private ArrayList<String> userAnswers;

    private Toast toast;
    /**
     * Set up a listener for when the user chooses to go to the next Question
     * - start a new activity displaying quiz stats when all questions are done
     */
    private View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cancelToast();

            String answer = getUserAnswer(optionsView, optionsType.toString());

            if(answer == null || answer.length()==0) {
                displayErrorMessage();
                return;
            }
            userAnswers.add(answer);

            if (qNumber < questions.size()) {
                optionsLinearLayout.removeAllViews();
                displayQuestion(qNumber);
            }

            else {

                Intent intent = new Intent(MainActivity.this,
                        DisplayMessageActivity.class);
                intent.putExtra(USER_ANSWER, userAnswers);

                answerSet.validate(userAnswers);
                intent.putExtra(ANSWER_SET, answerSet);

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
        nextButton.setOnClickListener(nextButtonClickListener);

        checkedId = new ArrayList<>();

        questionSet = new QuestionSet();
        questions = questionSet.getQuestionSet();

        if(savedInstanceState == null) {
            answerSet = new AnswerSet();
            userAnswers = new ArrayList<>();

            answerSet.setUpAnswers();

        } else {
            restoreSavedInstanceState(savedInstanceState);
        }

        displayQuestion(qNumber);


    }

    /**
     * Restore state of the quiz on activity resume after stop/pause
     * @param inState provides access to the data prior to activity resume
     */

    private void restoreSavedInstanceState(Bundle inState) {
        qNumber = inState.getInt(QUESTION_NUMBER);
        qNumber--;
        userAnswers = inState.getStringArrayList(USER_ANSWER);

        answerSet = (AnswerSet) inState.getSerializable(ANSWER_SET);


        String type = inState.getString(OPTIONS_TYPE);
        Options opType = Options.valueOf(type);

        checkedId = inState.getIntegerArrayList(CHOSEN_ANSWER);

        editTextAnswerSet = inState.getBoolean(EDITTEXT_ANSWER_SET);
        if(editTextAnswerSet) editTextAnswer = inState.getString(EDITTEXT_ANSWER);

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

                if(selectedRadioButton == null) {
                    return null;
                }
                else
                    answer = selectedRadioButton.getText().toString();
                break;

            case CHECKBOX:
                LinearLayout parentLayout = (LinearLayout) view;
                int numOfCheckBox = parentLayout.getChildCount();
                for (int i = 0; i < numOfCheckBox; i++) {
                    CheckBox childCheckBox = (CheckBox) parentLayout.getChildAt(i);
                    if (childCheckBox.isChecked()) {
                        answer += childCheckBox.getText() + " ";
                    }
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
     * Display an error message to the user if no answer supplied
     */
    private void displayErrorMessage() {
        cancelToast();

        toast = Toast.makeText(this, R.string.no_answer_error, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void cancelToast() {
        if(toast != null)
            toast.cancel();
    }

    /**
     * Display the questions from the set along with it's options, each question can
     * have different number of options and different type of views for the inputs
     */
    private void displayQuestion(int questionNumber) {

        Question currentSet;
        String[] optionsCurrentSet;

        qNumber = questionNumber;

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

                    if(checkedId.size() > 0 && i==checkedId.get(0)) {
                        button.setChecked(true);
                    }

                }
                optionsView = radioGroup;
                optionsLinearLayout.addView(radioGroup);
                break;


            case CHECKBOX:
                for (int i = 0; i < numOfOptions; i++) {
                    CheckBox checkbox = new CheckBox(this);
                    checkbox.setText(options[i]);
                    optionsLinearLayout.addView(checkbox);

                    if(checkedId.size() > 0 && checkedId.indexOf(i) != -1) {
                        checkbox.setChecked(true);
                    }

                }
                optionsView = optionsLinearLayout;
                break;

            case EDITTEXT:
                EditText editText = new EditText(this);

                if(editTextAnswerSet) {
                    editText.setText(editTextAnswer);
                } else {
                    editText.setHint(R.string.editText_hint);
                }

                optionsLinearLayout.addView(editText);
                optionsView = editText;
                break;
        }
        this.optionsType = opType;

    }


    /**
     * Save the status of the quiz on activity stop/pause and restore the values
     * again when recreated
     * @param outState Bundle object used to save the state of the Activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(QUESTION_NUMBER, qNumber);
        outState.putStringArrayList(USER_ANSWER, userAnswers);
        outState.putSerializable(ANSWER_SET, answerSet);

        String type = optionsType.toString();
        Options opType = Options.valueOf(type);

        outState.putString(OPTIONS_TYPE, type);


        switch (opType) {

            case EDITTEXT:
                EditText text = (EditText) optionsLinearLayout.getChildAt(0);
                if(text.getText() != null) {
                    editTextAnswerSet = true;
                    outState.putString(EDITTEXT_ANSWER, String.valueOf(text.getText()));
                    outState.putBoolean(EDITTEXT_ANSWER_SET, editTextAnswerSet);
                }
                break;

            case CHECKBOX:
                int count = optionsLinearLayout.getChildCount();
                CheckBox[] checkBoxes = new CheckBox[count];
                for (int i=0 ; i<count; i++) {
                    checkBoxes[i] = (CheckBox) optionsLinearLayout.getChildAt(i);
                    if(checkBoxes[i].isChecked()) {
                        checkedId.add(i);
                    }
                }
                break;

            case RADIOBUTTON:
                RadioButton selectedRadioButton = findViewById(((RadioGroup) optionsLinearLayout.getChildAt(0))
                        .getCheckedRadioButtonId());
                if(selectedRadioButton != null) {
                    checkedId.add(selectedRadioButton.getId());
                }

                break;
        }

        outState.putIntegerArrayList(CHOSEN_ANSWER, checkedId);

    }
}
