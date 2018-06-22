package com.example.android.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

    private static final String QUESTION_NUMBER = "QUESTION_NUMBER";
    private static final String CHOSEN_ANSWER = "CHOSEN_ANSWER";
    private static final String EDITTEXT_ANSWER = "EDIT_TEXT_ANSWER";
    private static final String EDITTEXT_ANSWER_SET = "EDIT_TEXT_ANSWER_SET";
    public static final String QUESTIONS = "QUESTIONS";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    private TextView questionTextView;
    private TextView numOfQuestionsTextView;
    private LinearLayout optionsLinearLayout;
    private ProgressBar progressBar;

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


    /**
     * Set up a listener for when the user chooses to go to the next Question
     * - start a new activity displaying quiz stats when all questions are done
     */
    private View.OnClickListener nextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Log.d(LOG_TAG, "Next Button has been clicked");

            if(checkedId != null) checkedId = null;
            if (editTextAnswer != null) {
                editTextAnswer = null;
                editTextAnswerSet = false;
            }

            saveUserAnswer(optionsView, optionsType.toString());
            if (!answered) {
                displayErrorMessage();
                return;
            }

            Log.d(LOG_TAG, "Incrementing the QNo by 1");
            qNumber++;
            if (qNumber < questions.size()) {
                displayQuestion(qNumber);
            }

            else {

                Intent intent = new Intent(MainActivity.this,
                        DisplayMessageActivity.class);
                intent.putExtra(QUESTIONS, questions);
                startActivity(intent);
                finish();
            }
        }
    };
    private View.OnClickListener prevButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(LOG_TAG, "Prev Button has been clicked");

            int currentQNo = qNumber;
            Question currentQuestion = questions.get(currentQNo);
            String[] currentOptions = currentQuestion.getOptions();
            ArrayList<Integer> currentAnswer = new ArrayList<>();
            String currentEditTextAnswer = null;

            Log.d(LOG_TAG, "Saving answer for current Question with number " + currentQNo);

            switch (optionsType) {
                case EDITTEXT:
                    Log.d(LOG_TAG, "----------EditText Type current questions------------");
                    EditText editText = (EditText) optionsLinearLayout.getChildAt(0);
                    if (editText.getText() != null) {
                        currentEditTextAnswer = editText.getText().toString();
                    }
                    Log.d(LOG_TAG, "EditText Case ------ Saving answer: " + currentEditTextAnswer);
                    currentQuestion.setUserAnswer(currentEditTextAnswer);
                    break;
                case CHECKBOX:
                    Log.d(LOG_TAG, "------------Checkbox Type current questions-----------");
                    for (int i = 0; i < currentOptions.length; i++) {
                        CheckBox checkBox = (CheckBox) optionsLinearLayout.getChildAt(i);
                        if (checkBox.isChecked()) {
                            currentAnswer.add(i);
                            Log.d(LOG_TAG, "Checkbox case ---- Saving ID : " + i);
                        }
                    }
                    currentQuestion.setUserSetAnswerId(currentAnswer);
                    break;
                case RADIOBUTTON:
                    Log.d(LOG_TAG, "-----------RadioButton Type current questions------------");
                    RadioGroup radioGroup = (RadioGroup) optionsLinearLayout.getChildAt(0);
                    for (int j = 0; j < radioGroup.getChildCount(); j++) {
                        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(j);
                        if (radioButton.isChecked()) {
                            currentAnswer.add(j);
                            Log.d(LOG_TAG, "Radiobutton case ---- Saving ID : " + j);
                        }
                    }
                    currentQuestion.setUserSetAnswerId(currentAnswer);
                    break;
            }

            if (qNumber > 0) {
                qNumber -= 1;
                optionsLinearLayout.removeAllViews();
                if (checkedId != null) checkedId = null;
                displayQuestion(qNumber);
                Question prevQuestion = questions.get(qNumber);
                Options optionsType = prevQuestion.getOptionsType();

//                cancelToast();
//                toast = Toast.makeText(MainActivity.this, "Question is : " + prevQuestion.getQuestion(), Toast.LENGTH_SHORT);
//                toast.show();
                Log.d(LOG_TAG, "Displaying question for previous clicked QNo : " + qNumber + " is " + prevQuestion.getQuestion());

                switch (optionsType) {
                    case RADIOBUTTON:
                        Log.d(LOG_TAG, "-----------RadioButton Type prev questions------------");
                        if (prevQuestion.getUserSetAnswerId() != null) {
                            int rbSelectedId = prevQuestion.getUserSetAnswerId().get(0);
                            RadioGroup radioGroup = (RadioGroup) optionsLinearLayout.getChildAt(0);
                            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(rbSelectedId);
                            radioButton.setChecked(true);
                        }
                        break;
                    case CHECKBOX:
                        Log.d(LOG_TAG, "------------Checkbox Type prev - now questions-----------");
                        if (prevQuestion.getUserSetAnswerId() != null) {
                            ArrayList<Integer> cbSelectedId = (ArrayList<Integer>) prevQuestion.getUserSetAnswerId();
                            for (int index : cbSelectedId) {
                                CheckBox checkBox = (CheckBox) optionsLinearLayout.getChildAt(index);
                                checkBox.setChecked(true);
                            }
                        }
                        break;
                    case EDITTEXT:
                        Log.d(LOG_TAG, "----------EditText Type prev questions------------");
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
                toast.show();
            }

        }
    };

    private void saveUserAnswer(View optionsView, String optionsType) {

        Question currentQuestion = questions.get(qNumber);
        ArrayList<Integer> userSelectedAnswers = new ArrayList<>();

        String answer;

        Log.d(LOG_TAG, "Saving the answers after Next button was clicked. Options type is : " + optionsType);

        switch (Options.valueOf(optionsType)) {
            case RADIOBUTTON:

                int selectedId = ((RadioGroup) optionsView).getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedId);

                if (selectedRadioButton == null) {
                    return;
                } else {
                    userSelectedAnswers.add(selectedId);
//                    Toast.makeText(MainActivity.this, "RadioButton Adding ID : " + selectedId + " for QNo: " + qNumber, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, "Size of arraylist: " + userSelectedAnswers.size(), Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(MainActivity.this, "CheckBox Adding ID : " + i + " for QNo: " + qNumber, Toast.LENGTH_SHORT).show();
                        answered = true;
                    }
                }
//                Toast.makeText(MainActivity.this, "Size of arraylist: " + userSelectedAnswers.size(), Toast.LENGTH_SHORT).show();
                currentQuestion.setUserSetAnswerId(userSelectedAnswers);
                break;

            case EDITTEXT:
                EditText answerText = (EditText) optionsView;
                answer = answerText.getText().toString();
//                Toast.makeText(MainActivity.this, "EditText Saving answer : " + answer, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(answer)) {
                    currentQuestion.setUserAnswer(answer);
                    answered = true;
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("MainActivity", "OnCreate called");

        questionTextView = findViewById(R.id.question_text);
        numOfQuestionsTextView = findViewById(R.id.questions_remaining);
        optionsLinearLayout = findViewById(R.id.linearLayout_Options);
        progressBar = findViewById(R.id.determinantProgressBar);

        Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(nextButtonClickListener);

        Button prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(prevButtonClickListener);

        checkedId = new ArrayList<>();

        questions = getQuestionsList();
        totalQuestions = questions.size();

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(totalQuestions);

        displayQuestion(qNumber);


    }

    private ArrayList<Question> getQuestionsList() {
        ArrayList<Question> questions = new ArrayList<>();
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

    /**
     * Restore state of the quiz on activity resume after stop/pause
     * @param savedInstanceState provides access to the data prior to activity resume
     */

    private void restoreSavedInstanceState(Bundle savedInstanceState) {

        qNumber = savedInstanceState.getInt(QUESTION_NUMBER);
//        qNumber--;

        editTextAnswerSet = savedInstanceState.getBoolean(EDITTEXT_ANSWER_SET);
        if (editTextAnswerSet) editTextAnswer = savedInstanceState.getString(EDITTEXT_ANSWER);

        checkedId = savedInstanceState.getIntegerArrayList(CHOSEN_ANSWER);

        questions = (ArrayList<Question>) savedInstanceState.getSerializable(QUESTIONS);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("MainActivity", "Restore instance state is called");

        restoreSavedInstanceState(savedInstanceState);

        displayQuestion(qNumber);
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

        String text = (qNumber + 1) + "/" + totalQuestions;
        numOfQuestionsTextView.setText(text);

        progressBar.setProgress(qNumber);

        if (answered)
            answered = false;

        Question currentSet;
        String[] optionsCurrentSet;

        optionsLinearLayout.removeAllViews();
//        qNumber = questionNumber;

        Log.d(LOG_TAG, "In displayQuestion method ---> Current Question Number is: " + qNumber);

//        if (qNumber < questions.size()) {
            currentSet = questions.get(qNumber);
            questionTextView.setText(currentSet.getQuestion());
            optionsCurrentSet = currentSet.getOptions();
            Options type = currentSet.getOptionsType();

            displayOptions(optionsCurrentSet, type);
//        }

    }

    /**
     * Display the options for the given question
     *
     * @param options     list of options for the question
     * @param optionsType type of view for the options
     */

    private void displayOptions(String[] options, Options optionsType) {

        Log.d(LOG_TAG, "Inside Display Options NOW. Question Number is : " + qNumber);
        Question question = questions.get(qNumber);


        int numOfOptions = 0;
        if (optionsType.equals(RADIOBUTTON) || optionsType.equals(Options.CHECKBOX))
            numOfOptions = options.length;
        String type = optionsType.toString();
        Options opType = Options.valueOf(type);

        switch (opType) {

            case RADIOBUTTON:
                Log.d(LOG_TAG, "Displaying RadioButtons Options");
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
                Log.d(LOG_TAG, "Displaying Checkboxes Options");
                for (int i = 0; i < numOfOptions; i++) {
                    CheckBox checkbox = new CheckBox(this);
                    checkbox.setText(options[i]);
                    optionsLinearLayout.addView(checkbox);

                    if(checkedId != null) {
                        if(checkedId.size() > 0 && checkedId.indexOf(i) != -1) {
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
                Log.d(LOG_TAG, "Displaying EditText Options");
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
     * @param outState Bundle object used to save the state of the Activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v("MainActivity", "Inside SaveInstanceState");
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
                if(text.getText() != null) {
                    editTextAnswer = String.valueOf(text.getText());
                    editTextAnswerSet = true;
                    Log.v("MainActivity", "Text entered in EditText for QNo:" + qNumber + " is " + String.valueOf(text.getText()));
                }
                break;

            case CHECKBOX:
                int count = optionsLinearLayout.getChildCount();
                CheckBox[] checkBoxes = new CheckBox[count];
                for (int i=0 ; i<count; i++) {
                    checkBoxes[i] = (CheckBox) optionsLinearLayout.getChildAt(i);
                    if(checkBoxes[i].isChecked()) {
                        Log.v("MainActivity", "Checkbox selected for QNo: " + qNumber + " is " + checkBoxes[i].getText());
                        checkedId.add(i);
                    }
                }
                break;

            case RADIOBUTTON:
                RadioButton selectedRadioButton = findViewById(((RadioGroup) optionsLinearLayout.getChildAt(0))
                        .getCheckedRadioButtonId());
                if(selectedRadioButton != null) {
                    Log.v("MainActivity", "RadioButton selected for QNo: " + qNumber + " is " + String.valueOf((selectedRadioButton.getId())));
                    checkedId.add((selectedRadioButton.getId()));
                }

                break;
        }
    }
}
