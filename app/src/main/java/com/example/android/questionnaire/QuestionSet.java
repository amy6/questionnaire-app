package com.example.android.questionnaire;


import java.util.ArrayList;

public class QuestionSet {

    //com.example.android.questionnaire.QuestionSet -> Represents all questions and options (Array of QuestionAndOptions objects)

    //AnswerSetService - submitAnswer(Array<QuestionAndAnswer> array)

    ArrayList<Question> questions;

    /**
     * Set the list of questions and it's options to be displayed to the user
     */
    public ArrayList<Question> getQuestionSet() {
        questions = new ArrayList<>();

        /*Question question1 = new Question("Which famous person does Phoebe believe is her grandfather?", Options.RADIOBUTTON, new String[]{"Albert Einstein", "Isaac Newton", "Winston Churchill", "Beethoven"}, new int[]{0});
        questions.add(question1);

        Question question2 = new Question("Who among the following belong to the Targaryen family?", Options.CHECKBOX, new String[]{"Aemon", "Rhaegar", "Ned", "Robb"}, new int[]{0,1});
        questions.add(question2);

        Question question3 = new Question("What is Sheldon's middle name?", Options.EDITTEXT, "Leonard");
        questions.add(question3);*/

       /* String[] hpBooks = new String[]{"Stone", "Chamber", "Azkaban", "Goblet", "Phoenix", "Prince", "Hallows"};
        Question question11 = new Question("Favorite HP book?", hpBooks, Options.RADIOBUTTON);
        questions.add(question11);

        String[] hpCharacters = new String[]{"Harry", "Ron", "Hermione", "Dobby", "Dumbledore", "Snape"};
        Question question12 = new Question("Favorite HP characters?", hpCharacters, Options.CHECKBOX);
        questions.add(question12);

        Question question13 = new Question("Favorite Stark kid?", Options.EDITTEXT);
        questions.add(question13);

        String[] avengerCharacters = new String[]{"Harry", "Ron", "Hermione", "Dobby", "Dumbledore", "Snape"};
        Question question14 = new Question("Favorite Avenger?", avengerCharacters, Options.RADIOBUTTON);
        questions.add(question14);

        String[] gotCharacters = new String[]{"Harry", "Ron", "Hermione", "Dobby", "Dumbledore", "Snape"};
        Question question15 = new Question("Favorite GOT characters?", gotCharacters, Options.CHECKBOX);
        questions.add(question15);

        String[] abndCharacters = new String[]{"Harry", "Ron", "Hermione", "Dobby", "Dumbledore", "Snape"};
        Question question16 = new Question("Favorite ABND scholar?", abndCharacters, Options.RADIOBUTTON);
        questions.add(question16);*/

        return questions;
    }

}
