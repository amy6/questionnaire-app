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

            //save current set answer before displaying the next question
            saveUserAnswer();

            //if the current question is unanswered, alert the user as all questions are mandatory
            if (!answered) {
                alertQuestionUnanswered();
                return;
            }

            /*
            increment the question number and display the next question
            or display the results if it's the last question
             */
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

            //save current set answer before displaying the previous question
            saveUserAnswer();

            /*
            decrement the question number and display the previous question
            if this is the first question, display a toast message stating there are no previous questions
             */
            if (qNumber > 0) {
                qNumber--;
                displayQuestion();
            } else {
                alertNoPrevQuestions();
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
                saveUserAnswer();
                displayReviewQuestions();
            }
        });

        //get all the questions and determine the total number of questions
        questions = QuestionSet.getAllQuestions();
        totalQuestions = questions.size();

        //set progress bar max value
        progressBar.setMax(totalQuestions);

        displayQuestion();
    }

    /**
     * Display the questions from the set along with it's options, each question can
     * have different number of options and different type of views for the inputs
     */
    private void displayQuestion() {

        //remove previous question and it's corresponding options
        optionsLinearLayout.removeAllViews();

        //update the state of 'mark for review' TextView appropriately
        if (questions.get(qNumber).isMarkedForReview()) {
            reviewTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
        } else {
            reviewTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_outline_blank, 0, 0, 0);
        }

        //display the current question number and total number of questions
        String text = (qNumber + 1) + "/" + totalQuestions;
        numOfQuestionsTextView.setText(text);

        //update the progress bar status
        progressBar.setProgress(qNumber);

        if (answered)
            answered = false;

        Question currentSet = questions.get(qNumber);
        questionTextView.setText(currentSet.getQuestion());

        //set the button for last question to be 'Submit', rather than 'Next'
        if (qNumber == questions.size() - 1) {
            nextButton.setText(R.string.submit);
        } else {
            nextButton.setText(R.string.nextQuestion);
        }

        displayOptions();

    }

    /**
     * display options for each question - type could be Radiobuttons, checkboxes or edittext
     * restore answers from question object that were saved on activity reload (orientation change, minimize app)
     * or on clicking next, previous or review buttons
     */
    private void displayOptions() {

        //get the current question and it's options
        Question question = questions.get(qNumber);
        String[] options = question.getOptions();
        Options currentOptionsType = question.getOptionsType();

        switch (currentOptionsType) {

            case RADIOBUTTON:
                //For the case of radiobuttons, create a RadioGroup and add each option as a RadioButton
                //to the group - set an ID for each RadioButton to be referred later
                RadioGroup radioGroup = new RadioGroup(this);
                for (int i = 0; i < options.length; i++) {
                    RadioButton button = new RadioButton(this);
                    button.setText(options[i]);
                    button.setId(i);
                    radioGroup.addView(button);

                }
                optionsLinearLayout.addView(radioGroup);

                //restore saved answers
                if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(question.getUserSetAnswerId().get(0));
                    radioButton.setChecked(true);
                }

                optionsView = radioGroup;
                break;


            case CHECKBOX:
                //For the case of check boxes, create a new CheckBox for each option
                for (String option : options) {
                    CheckBox checkbox = new CheckBox(this);
                    checkbox.setText(option);
                    optionsLinearLayout.addView(checkbox);
                }

                //restore saved answers
                if (question.getUserSetAnswerId() != null && question.getUserSetAnswerId().size() > 0) {
                    for (int index : question.getUserSetAnswerId()) {
                        ((CheckBox) optionsLinearLayout.getChildAt(index)).setChecked(true);
                    }
                }
                optionsView = optionsLinearLayout;
                break;

            case EDITTEXT:
                //For the case of edit text, display an EditText for the user to enter the answer
                EditText editText = new EditText(this);
                //set the InputType to display digits only keyboard if applicable
                if (TextUtils.isDigitsOnly(questions.get(qNumber).getAnswer())) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                //restore saved answers, set hint text if answer is empty/remains unanswered
                if (!TextUtils.isEmpty(question.getUserAnswer())) {
                    editText.setText(question.getUserAnswer());
                    editText.setSelection(question.getUserAnswer().length());
                } else {
                    editText.setHint(R.string.editText_hint);
                }

                optionsLinearLayout.addView(editText);

                optionsView = editText;
                break;
        }
        optionsType = currentOptionsType;
    }

    /**
     * save the user answer on click of either the next, previous or review button
     * the method is also called by saveInstance state (orientation change or minimizing the app)
     */
    private void saveUserAnswer() {

        if (qNumber < questions.size()) {
            Question currentQuestion = questions.get(qNumber);
            ArrayList<Integer> userSelectedAnswers = new ArrayList<>();

            String answer;

            switch (optionsType) {
                case RADIOBUTTON:
                    //save the selected RadioButton IDs
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
                    //save checkbox IDs that have been checked by the user
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
                    //save the EditText answer
                    EditText answerText = (EditText) optionsView;
                    answer = answerText.getText().toString();
                    if (!TextUtils.isEmpty(answer)) {
                        currentQuestion.setUserAnswer(answer);
                        answered = true;
                    } else {
                        currentQuestion.setUserAnswer(null);
                    }
                    break;
            }
        }
    }

    /**
     * called when `mark for review` option is checked ot unchecked by the user
     *
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
     * Save the status of the quiz on activity stop/pause and restore the values
     * again when recreated
     * @param outState Bundle object used to save the state of the Activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveUserAnswer();

        outState.putInt(QUESTION_NUMBER, qNumber);
        outState.putSerializable(QUESTIONS, questions);
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
        questions = (ArrayList<Question>) savedInstanceState.getSerializable(QUESTIONS);

        displayQuestion();
    }

    /**
     * display an alert toast message to the user
     * if the next button is clicked without answering the current question
     */
    private void alertQuestionUnanswered() {
        cancelToast();

        toast = Toast.makeText(this, R.string.no_answer_error, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 258);
        toast.show();
    }

    /**
     * when the previous button is clicked while in the first questions
     * display a toast message stating there are no previous questions
     */
    private void alertNoPrevQuestions() {
        cancelToast();

        toast = Toast.makeText(MainActivity.this, "This is the first question", Toast.LENGTH_SHORT);
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
     * Navigate to new activity when user presses review list button
     * - pass the questions object in the intent to be used by the ReviewAnswersActivity
     */
    private void displayReviewQuestions() {
        Intent intent = new Intent(MainActivity.this, ReviewAnswersActivity.class);
        intent.putExtra(QUESTIONS, questions);
        startActivity(intent);
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
            displayQuestion();
        }
    }
}
