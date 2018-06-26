package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.questionnaire.data.Options;
import com.example.android.questionnaire.data.Question;
import com.example.android.questionnaire.data.QuestionSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String QUESTION_NUMBER = "QUESTION_NUMBER";
    public static final String QUESTIONS = "QUESTIONS";
    private static final String CHOSEN_ANSWER = "CHOSEN_ANSWER";
    private static final String EDITTEXT_ANSWER = "EDIT_TEXT_ANSWER";
    private static final String EDITTEXT_ANSWER_SET = "EDIT_TEXT_ANSWER_SET";

    private TextView questionTextView;
    private TextView numOfQuestionsTextView;
    private LinearLayout optionsLinearLayout;
    private ProgressBar progressBar;
    private TextView reviewTextView;
    private Button nextButton;
    private Button prevButton;

    private int qNumber;
    private int totalQuestions;
    private ArrayList<Question> questions;
    private ArrayList<Integer> checkedId;
    private String editTextAnswer;
    private boolean editTextAnswerSet;
    private boolean answered;

    private Options optionsType;
    private View optionsView;
    private Toast toast;

    /**
     * Set up a listener for the next button to display next Question
     * start a new activity displaying quiz results when all questions are done
     */
    private View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            clearPreviousSetAnswers();

            saveUserAnswer();
            if (!answered) {
                alertUser();
                return;
            }

            qNumber++;
            if (qNumber < questions.size()) {
                displayQuestion();
            } else {
                displayResults();
            }
        }
    };

    /**
     * Set up a listener for the previous button to display previous Question
     * display a toast message when button is clicked while in the first question
     */
    private View.OnClickListener prevButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            saveCurrentAnswer();

            if (qNumber > 0) {
                qNumber -= 1;
                clearPreviousSetAnswers();
                displayQuestion();
                Question prevQuestion = questions.get(qNumber);
                Options optionsType = prevQuestion.getOptionsType();

                switch (optionsType) {
                    case RADIOBUTTON:
                        if (prevQuestion.getUserSetAnswerId() != null) {
                            int rbSelectedId = prevQuestion.getUserSetAnswerId().get(0);
                            RadioGroup radioGroup = (RadioGroup) optionsLinearLayout.getChildAt(0);
                            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(rbSelectedId);
                            radioButton.setChecked(true);
                        }
                        break;
                    case CHECKBOX:
                        if (prevQuestion.getUserSetAnswerId() != null) {
                            ArrayList<Integer> cbSelectedId = (ArrayList<Integer>) prevQuestion.getUserSetAnswerId();
                            for (int index : cbSelectedId) {
                                CheckBox checkBox = (CheckBox) optionsLinearLayout.getChildAt(index);
                                checkBox.setChecked(true);
                            }
                        }
                        break;
                    case EDITTEXT:
                        if (prevQuestion.getUserAnswer() != null) {
                            String editTextAnswer = prevQuestion.getUserAnswer();
                            EditText editText = (EditText) (optionsLinearLayout.getChildAt(0));
                            editText.setText(editTextAnswer);
                        }
                        break;
                }
            } else {
                cancelToast();
                toast = Toast.makeText(MainActivity.this, "This is the first question", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 258);
                toast.show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //obtain references to all the views in the main activity
        questionTextView = findViewById(R.id.question_text);
        numOfQuestionsTextView = findViewById(R.id.questions_remaining);
        optionsLinearLayout = findViewById(R.id.linearLayout_Options);
        progressBar = findViewById(R.id.determinantProgressBar);

        //register the click events for the previous and next buttons
        prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(prevButtonClickListener);
        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(nextButtonClickListener);

        //set up the listener for 'mark for review' option
        reviewTextView = findViewById(R.id.review_check);
        reviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkerForReview(v);
            }
        });

        //implement the review button click functionality to display the questions marked for review
        ImageButton reviewButton = findViewById(R.id.review_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentAnswer();
                displayReviewQuestions();
            }
        });

        questions = QuestionSet.getAllQuestions();
        totalQuestions = questions.size();

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(totalQuestions);

        displayQuestion();
    }

    /**
     * called when `mark for review` option is checked ot unchecked by the user
     * @param v `marker for review` textview reference
     */
    private void setMarkerForReview(View v) {
        if (!questions.get(qNumber).isMarkedForReview()) {
            questions.get(qNumber).setMarkedForReview(true);
            ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
        } else {
            questions.get(qNumber).setMarkedForReview(false);
            ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_outline_blank, 0, 0, 0);
        }
    }

    /**
     * save any currently entered answers by the user when the previous button or the review button
     * is clicked so that it can be redisplayed when the user gets back to the question
     */
    private void saveCurrentAnswer() {

        int currentQNo = qNumber;
        Question currentQuestion = questions.get(currentQNo);
        String[] currentOptions = currentQuestion.getOptions();
        ArrayList<Integer> currentAnswer = new ArrayList<>();
        String currentEditTextAnswer = null;

        switch (optionsType) {
            case EDITTEXT:
                EditText editText = (EditText) optionsLinearLayout.getChildAt(0);
                if (editText.getText() != null) {
                    currentEditTextAnswer = editText.getText().toString();
                }
                currentQuestion.setUserAnswer(currentEditTextAnswer);
                break;
            case CHECKBOX:
                for (int i = 0; i < currentOptions.length; i++) {
                    CheckBox checkBox = (CheckBox) optionsLinearLayout.getChildAt(i);
                    if (checkBox.isChecked()) {
                        currentAnswer.add(i);
                    }
                }
                currentQuestion.setUserSetAnswerId(currentAnswer);
                break;
            case RADIOBUTTON:
                RadioGroup radioGroup = (RadioGroup) optionsLinearLayout.getChildAt(0);
                for (int j = 0; j < radioGroup.getChildCount(); j++) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(j);
                    if (radioButton.isChecked()) {
                        currentAnswer.add(j);
                    }
                }
                currentQuestion.setUserSetAnswerId(currentAnswer);
                break;
        }
    }

    /**
     * Navigate to new activity when user presses review list button
     * - pass the questions object in the intent to be used by the ReviewAnswersActivity
     */
    private void displayReviewQuestions() {
        Intent intent = new Intent(MainActivity.this, ReviewAnswersActivity.class);
        intent.putExtra(QUESTIONS, questions);
        startActivity(intent);
    }

    /**
     * save the user answer on click of next button
     */
    private void saveUserAnswer() {

        Question currentQuestion = questions.get(qNumber);
        ArrayList<Integer> userSelectedAnswers = new ArrayList<>();

        String answer;

        switch (optionsType) {
            case RADIOBUTTON:

                int selectedId = ((RadioGroup) optionsView).getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedId);

                if (selectedRadioButton == null) {
                    return;
                } else {
                    userSelectedAnswers.add(selectedId);
                    currentQuestion.setUserSetAnswerId(userSelectedAnswers);
                    answered = true;
                }
                break;

            case CHECKBOX:
                LinearLayout parentLayout = (LinearLayout) optionsView;
                int numOfCheckBox = parentLayout.getChildCount();
                for (int i = 0; i < numOfCheckBox; i++) {
                    CheckBox childCheckBox = (CheckBox) parentLayout.getChildAt(i);
                    if (childCheckBox.isChecked()) {
                        userSelectedAnswers.add(i);
                        answered = true;
                    }
                }
                currentQuestion.setUserSetAnswerId(userSelectedAnswers);
                break;

            case EDITTEXT:
                EditText answerText = (EditText) optionsView;
                answer = answerText.getText().toString();
                if (!TextUtils.isEmpty(answer)) {
                    currentQuestion.setUserAnswer(answer);
                    answered = true;
                }
                break;
        }
    }


    /**
     * restore state of the quiz on activity resumes after stop/pause
     * @param savedInstanceState provides access to the data prior to activity resume
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        qNumber = savedInstanceState.getInt(QUESTION_NUMBER);

        editTextAnswerSet = savedInstanceState.getBoolean(EDITTEXT_ANSWER_SET);
        if (editTextAnswerSet) editTextAnswer = savedInstanceState.getString(EDITTEXT_ANSWER);

        checkedId = savedInstanceState.getIntegerArrayList(CHOSEN_ANSWER);

        questions = (ArrayList<Question>) savedInstanceState.getSerializable(QUESTIONS);

        displayQuestion();
    }

    /**
     * display an alert toast message to the user if next button is clicked without answering the current question
     */
    private void alertUser() {
        cancelToast();

        toast = Toast.makeText(this, R.string.no_answer_error, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 258);
        toast.show();
    }

    /**
     * method to handle canceling of any previously displayed toast before displaying new one
     */
    private void cancelToast() {
        if (toast != null)
            toast.cancel();
    }

    /**
     * Display the questions from the set along with it's options, each question can
     * have different number of options and different type of views for the inputs
     */
    private void displayQuestion() {

        optionsLinearLayout.removeAllViews();

        if (questions.get(qNumber).isMarkedForReview()) {
            reviewTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
        } else {
            reviewTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_outline_blank, 0, 0, 0);
        }

        String text = (qNumber + 1) + "/" + totalQuestions;
        numOfQuestionsTextView.setText(text);

        progressBar.setProgress(qNumber);

        if (answered)
            answered = false;

        Question currentSet = questions.get(qNumber);
        questionTextView.setText(currentSet.getQuestion());

        if (qNumber == questions.size() - 1) {
            nextButton.setText(R.string.submit);
        } else {
            nextButton.setText(R.string.nextQuestion);
        }

        displayOptions();

    }

    /**
     * display options for each question - type could be Radiobuttons, checkboxes or edittext
     * fetch saved options from checkedId array when restoring the set answers on activity reload (orientation change)
     * fetch saved options from question object when a user enters the main activity from the review answers activity
     */
    private void displayOptions() {

        Question question = questions.get(qNumber);
        String[] options = question.getOptions();
        Options currentOptionsType = question.getOptionsType();

        switch (currentOptionsType) {

            case RADIOBUTTON:
                RadioGroup radioGroup = new RadioGroup(this);
                for (int i = 0; i < options.length; i++) {
                    RadioButton button = new RadioButton(this);
                    button.setText(options[i]);
                    button.setId(i);
                    radioGroup.addView(button);

                    if (checkedId != null) {
                        if (checkedId.size() > 0 && i == checkedId.get(0)) {
                            button.setChecked(true);
                        }
                    }

                }
                optionsLinearLayout.addView(radioGroup);

                if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(question.getUserSetAnswerId().get(0));
                    radioButton.setChecked(true);
                }

                optionsView = radioGroup;
                break;


            case CHECKBOX:
                for (int i = 0; i < options.length; i++) {
                    CheckBox checkbox = new CheckBox(this);
                    checkbox.setText(options[i]);
                    optionsLinearLayout.addView(checkbox);

                    if (checkedId != null) {
                        if (checkedId.size() > 0 && checkedId.indexOf(i) != -1) {
                            checkbox.setChecked(true);
                        }
                    }

                }
                if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                    for (int index : question.getUserSetAnswerId()) {
                        ((CheckBox) optionsLinearLayout.getChildAt(index)).setChecked(true);
                    }
                }
                optionsView = optionsLinearLayout;
                break;

            case EDITTEXT:
                EditText editText = new EditText(this);
                if (TextUtils.isDigitsOnly(questions.get(qNumber).getAnswer())) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                if (editTextAnswerSet && editTextAnswer != null) {
                    editText.setText(editTextAnswer);
                    editText.setSelection(editTextAnswer.length());
                } else {
                    editText.setHint(R.string.editText_hint);
                }

                optionsLinearLayout.addView(editText);

                if (!TextUtils.isEmpty(question.getUserAnswer())) {
                    editText.setText(question.getUserAnswer());
                }

                optionsView = editText;
                break;
        }
        optionsType = currentOptionsType;
    }

    /**
     * Save the status of the quiz on activity stop/pause and restore the values
     * again when recreated
     * @param outState Bundle object used to save the state of the Activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fetchSavedAnswers();

        outState.putInt(QUESTION_NUMBER, qNumber);
        outState.putString(EDITTEXT_ANSWER, editTextAnswer);
        outState.putBoolean(EDITTEXT_ANSWER_SET, editTextAnswerSet);
        outState.putIntegerArrayList(CHOSEN_ANSWER, checkedId);
        outState.putSerializable(QUESTIONS, questions);
    }


    /**
     * get the saved answers for the questions to be passed to the bundle
     * saved answers will be restored when the activity is reloaded (ex: orientation change)
     */
    private void fetchSavedAnswers() {

        checkedId = new ArrayList<>();

        switch (optionsType) {

            case EDITTEXT:
                EditText text = (EditText) optionsLinearLayout.getChildAt(0);
                if (text.getText() != null) {
                    editTextAnswer = String.valueOf(text.getText());
                    editTextAnswerSet = true;
                }
                break;

            case CHECKBOX:
                int count = optionsLinearLayout.getChildCount();
                CheckBox[] checkBoxes = new CheckBox[count];
                for (int i = 0; i < count; i++) {
                    checkBoxes[i] = (CheckBox) optionsLinearLayout.getChildAt(i);
                    if (checkBoxes[i].isChecked()) {
                        checkedId.add(i);
                    }
                }
                break;

            case RADIOBUTTON:
                RadioButton selectedRadioButton = findViewById(((RadioGroup) optionsLinearLayout.getChildAt(0))
                        .getCheckedRadioButtonId());
                if (selectedRadioButton != null) {
                    checkedId.add((selectedRadioButton.getId()));
                }

                break;
        }
    }

    /**
     * Navigate to new activity when user presses submit button
     * pass the questions object in the intent to be used by the ResultsActivity
     */
    private void displayResults() {
        Intent intent = new Intent(MainActivity.this,
                ResultsActivity.class);
        intent.putExtra(QUESTIONS, questions);
        startActivity(intent);
        finish();
    }

    /**
     * new intent will be received when the user clicks on a `Go to question` button from the Review Answers activity
     * @param intent intent object received from ReviewAnswersActivity - contains the question number to be displayed
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            qNumber = intent.getIntExtra(QUESTION_NUMBER, 0);

            clearPreviousSetAnswers();
            displayQuestion();
        }
    }

    /**
     * clear any previously set radiobuttons/checkbox answers or edittext answers
     */
    private void clearPreviousSetAnswers() {
        if (checkedId != null) {
            checkedId = null;
        }
        if (editTextAnswer != null) {
            editTextAnswer = null;
            editTextAnswerSet = false;
        }
    }
}
