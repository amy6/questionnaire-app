package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.android.questionnaire.Options.CHECKBOX;
import static com.example.android.questionnaire.Options.RADIOBUTTON;

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
    private Button nextButton;
    private Button prevButton;
    private TextView timer;

    private int qNumber;
    private int totalQuestions;
    private ArrayList<Question> questions;

    private Options optionsType;
    private View optionsView;

    private ArrayList<Integer> checkedId;
    private String editTextAnswer;
    private boolean editTextAnswerSet;
    private boolean answered;

    private Toast toast;
    private CountDownTimer counter;
    private long timeRemaining;
    private TextView reviewTextView;
    /**
     * Set up a listener for when the user chooses to go to the next Question
     * - start a new activity displaying quiz stats when all questions are done
     */
    private View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (checkedId != null) checkedId = null;
            if (editTextAnswer != null) {
                editTextAnswer = null;
                editTextAnswerSet = false;
            }

            saveUserAnswer(optionsView, optionsType.toString());
            if (!answered) {
                displayErrorMessage();
                return;
            }

            qNumber++;
            if (qNumber > 0) {
                prevButton.setEnabled(true);
            }
            if (qNumber < questions.size()) {
                displayQuestion();
            } else {
                if (counter != null) {
                    counter.cancel();
                }
                displayResults();
            }
        }
    };
    private View.OnClickListener prevButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            saveCurrentAnswer();

            if (qNumber > 0) {
                qNumber -= 1;
                optionsLinearLayout.removeAllViews();
                if (checkedId != null) checkedId = null;
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

    private void displayResults() {
        Intent intent = new Intent(MainActivity.this,
                DisplayMessageActivity.class);
        intent.putExtra(QUESTIONS, questions);
        startActivity(intent);
        finish();
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.question_text);
        numOfQuestionsTextView = findViewById(R.id.questions_remaining);
        optionsLinearLayout = findViewById(R.id.linearLayout_Options);
        progressBar = findViewById(R.id.determinantProgressBar);

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(nextButtonClickListener);

        prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(prevButtonClickListener);


        ImageButton reviewButton = findViewById(R.id.review_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter != null) {
                    counter.cancel();
                }
                saveCurrentAnswer();
                Intent intent = new Intent(MainActivity.this, ReviewListActivity.class);
                intent.putExtra(QUESTIONS, questions);
                startActivity(intent);
            }
        });

        checkedId = new ArrayList<>();

        questions = getAllQuestions();
        totalQuestions = questions.size();

        reviewTextView = findViewById(R.id.review_check);
        reviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkerForReview(v);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(totalQuestions);
        timer = findViewById(R.id.timer_textView);

        displayQuestion();

        counter = new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf((int) (millisUntilFinished / 1000)).concat(":00"));
                timeRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                cancelToast();
                toast = Toast.makeText(MainActivity.this, "Time is up!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 258);
                toast.show();
                displayResults();
            }
        };
//        counter.start();
    }

    private ArrayList<Question> getAllQuestions() {
        questions = new ArrayList<>();

        Question question1 = new Question("Which famous person does Phoebe believe is her grandfather?", RADIOBUTTON, new String[]{"Albert Einstein", "Isaac Newton", "Winston Churchill", "Beethoven"}, Collections.singletonList(0));
        questions.add(question1);

        Question question2 = new Question("Who among the following belong to the Targaryen family?", Options.CHECKBOX, new String[]{"Aemon", "Rhaegar", "Ned", "Robb"}, Arrays.asList(0, 1));
        questions.add(question2);

        Question question3 = new Question("What is Sheldon's middle name?", Options.EDITTEXT, "Lee");
        questions.add(question3);

        Question question4 = new Question("What is Pied Piper?", RADIOBUTTON, new String[]{"A book", "A scary story", "A song", "A company", "A bank"}, Collections.singletonList(3));
        questions.add(question4);

        Question question5 = new Question("Which of the following are the names of fictional characters from Dan Brown novels?", CHECKBOX, new String[]{"Sophie Neveu", "Vittoria Vetra", "Nick Adams", "Robert Langdon"}, Arrays.asList(0, 1, 3));
        questions.add(question5);

        Question question6 = new Question("What color is \"The Incredible Hulk\"?", RADIOBUTTON, new String[]{"purple", "green", "blue", "grey"}, Collections.singletonList(1));
        questions.add(question6);

        Question question7 = new Question("How many seasons are there in the TV series Breaking Bad?", Options.EDITTEXT, "5");
        questions.add(question7);

        return questions;
    }


    private void setMarkerForReview(View v) {
        if (!questions.get(qNumber).isMarkedForReview()) {
            questions.get(qNumber).setMarkedForReview(true);
            ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box, 0, 0, 0);
        } else {
            questions.get(qNumber).setMarkedForReview(false);
            ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_box_outline_blank, 0, 0, 0);
        }
    }

    private void saveUserAnswer(View optionsView, String optionsType) {

        Question currentQuestion = questions.get(qNumber);
        ArrayList<Integer> userSelectedAnswers = new ArrayList<>();

        String answer;

        switch (Options.valueOf(optionsType)) {
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
     * Restore state of the quiz on activity resume after stop/pause
     *
     * @param savedInstanceState provides access to the data prior to activity resume
     */
    @SuppressWarnings("unchecked")
    private void restoreSavedInstanceState(Bundle savedInstanceState) {

        qNumber = savedInstanceState.getInt(QUESTION_NUMBER);

        editTextAnswerSet = savedInstanceState.getBoolean(EDITTEXT_ANSWER_SET);
        if (editTextAnswerSet) editTextAnswer = savedInstanceState.getString(EDITTEXT_ANSWER);

        checkedId = savedInstanceState.getIntegerArrayList(CHOSEN_ANSWER);

        questions = (ArrayList<Question>) savedInstanceState.getSerializable(QUESTIONS);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreSavedInstanceState(savedInstanceState);

        displayQuestion();
    }

    /**
     * Display an error message to the user if no answer supplied
     */
    private void displayErrorMessage() {
        cancelToast();

        toast = Toast.makeText(this, R.string.no_answer_error, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 258);
        toast.show();
    }

    private void cancelToast() {
        if (toast != null)
            toast.cancel();
    }

    /**
     * Display the questions from the set along with it's options, each question can
     * have different number of options and different type of views for the inputs
     */
    private void displayQuestion() {

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

        Question currentSet;
        String[] optionsCurrentSet;

        optionsLinearLayout.removeAllViews();
        currentSet = questions.get(qNumber);
        questionTextView.setText(currentSet.getQuestion());
        optionsCurrentSet = currentSet.getOptions();
        Options type = currentSet.getOptionsType();

        if (qNumber == questions.size() - 1) {
            nextButton.setText(R.string.submit);
        } else {
            nextButton.setText(R.string.nextQuestion);
        }

        displayOptions(optionsCurrentSet, type);

    }

    /**
     * Display the options for the given question
     *
     * @param options     list of options for the question
     * @param optionsType type of view for the options
     */

    private void displayOptions(String[] options, Options optionsType) {

        Question question = questions.get(qNumber);


        int numOfOptions = 0;
        if (optionsType.equals(RADIOBUTTON) || optionsType.equals(Options.CHECKBOX))
            numOfOptions = options.length;
        String type = optionsType.toString();
        Options opType = Options.valueOf(type);

        switch (opType) {

            case RADIOBUTTON:
                RadioGroup radioGroup = new RadioGroup(this);
                for (int i = 0; i < numOfOptions; i++) {
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
                for (int i = 0; i < numOfOptions; i++) {
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
        this.optionsType = opType;
    }


    /**
     * Save the status of the quiz on activity stop/pause and restore the values
     * again when recreated
     *
     * @param outState Bundle object used to save the state of the Activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveSetAnswers(outState);
    }

    private void saveSetAnswers(Bundle outState) {

        fetchSavedAnswers();

        outState.putInt(QUESTION_NUMBER, qNumber);
        outState.putString(EDITTEXT_ANSWER, editTextAnswer);
        outState.putBoolean(EDITTEXT_ANSWER_SET, editTextAnswerSet);
        outState.putIntegerArrayList(CHOSEN_ANSWER, checkedId);
        outState.putSerializable(QUESTIONS, questions);
    }

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            qNumber = intent.getIntExtra(QUESTION_NUMBER, 0);
            displayQuestion();
        }
    }
}
