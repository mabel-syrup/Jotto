package com.surber.m.jotto;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    String mySecretWord = "";
    String theirSecretWord;

    TextView mySecretWordTV;
    TextView theirSecretWordTV;
    EditText guessWord;
    Button guessEnterButton;
    Button resetButton;

    ListView theirGuesses;
    ListView myGuesses;

    Boolean finished = false;

    ArrayList<Guess> myGuessesAL = new ArrayList<>();
    ArrayList<Guess> theirGuessesAL = new ArrayList<>();

    public AI ai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ai = AI.getInstance();
        ai.res = getResources();
        ai.getWords();

        final GuessListAdapter myGuessAdapter =
                new GuessListAdapter(MainActivity.this,R.layout.guess,myGuessesAL);
        final GuessListAdapter theirGuessAdapter =
                new GuessListAdapter(MainActivity.this,R.layout.guess,theirGuessesAL);

        mySecretWordTV = (TextView) findViewById(R.id.my_secret_word);
        theirSecretWordTV = (TextView) findViewById(R.id.their_secret_word);
        guessEnterButton = (Button) findViewById(R.id.guess_enter_button);
        resetButton = (Button) findViewById(R.id.note_button);
        theirGuesses = (ListView) findViewById(R.id.their_guesses);
        myGuesses = (ListView) findViewById(R.id.my_guesses);
        guessWord = (EditText) findViewById(R.id.my_guess);

        ai.guessButton = guessEnterButton;




        myGuesses.setAdapter(myGuessAdapter);
        theirGuesses.setAdapter(theirGuessAdapter);



        theirSecretWord = ai.knownWords.get(AI.RANDOM.nextInt(ai.knownWords.size()));
        System.out.println("Their Secret Word: " + theirSecretWord);

        guessEnterButton.setText(R.string.word_enter_button_text);

        guessEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myGuess = guessWord.getText().toString().toLowerCase();
                if(finished) {
                    mySecretWord = "";
                    theirSecretWord = "";
                    ai.reset();
                    mySecretWordTV.setText("_____");
                    theirSecretWordTV.setText("_____");
                    myGuessesAL.clear();
                    theirGuessesAL.clear();
                    myGuessAdapter.clear();
                    myGuessAdapter.notifyDataSetChanged();
                    theirGuessAdapter.clear();
                    theirGuessAdapter.notifyDataSetChanged();
                    theirGuesses.setBackgroundColor(Color.argb(255,255,255,255));
                    myGuesses.setBackgroundColor(Color.argb(255,255,255,255));
                    guessEnterButton.setText(R.string.word_enter_button_text);
                    finished = false;
                    theirSecretWord = ai.knownWords.get(AI.RANDOM.nextInt(ai.knownWords.size()));
                    return;
                }
                if(myGuess.length() != 5 || !ai.knownWords.contains(myGuess)) {
                    Toast.makeText(MainActivity.this, "Your word was not valid.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //All guesses and words must be 5-letter english non-proper-nouns.
                if(mySecretWord.isEmpty()){
                    mySecretWord = myGuess;
                    mySecretWordTV.setText(mySecretWord);
                    guessEnterButton.setText(R.string.guess_enter_button_text);
                } else {
                    //Result is the number of matched letters between the guess and the secret word.
                    int rslt = compareGuess(myGuess,theirSecretWord);
                    Guess myGuessObj = new Guess(myGuess,rslt);
                    myGuessesAL.add(myGuessObj);
                    myGuessAdapter.add(myGuessObj);
                    myGuessAdapter.notifyDataSetChanged();
                    myGuesses.setSelection(myGuesses.getCount() - 1);
                    if(myGuess.toLowerCase().equals(theirSecretWord.toLowerCase())){
                        Toast.makeText(MainActivity.this,"YOU WIN!",Toast.LENGTH_LONG).show();
                        finished = true;
                        guessEnterButton.setText("New");
                        theirGuesses.setBackgroundColor(Color.argb(255,224,224,224));
                        theirSecretWordTV.setText(myGuess);
                    }
                    guessWord.setText("");
                    AITurn(theirGuessAdapter);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finished = true;
                theirSecretWordTV.setText(theirSecretWord);
                guessEnterButton.setText("New");
            }
        });
    }

    private void AITurn(GuessListAdapter adapter){
        String aiGuess = ai.guess();
        int rslt = compareGuess(aiGuess,mySecretWord);
        Guess guess = new Guess(aiGuess,rslt);
        ai.guesses.add(new Guess(aiGuess,rslt));
        theirGuessesAL.add(guess);
        adapter.add(guess);
        adapter.notifyDataSetChanged();
        theirGuesses.setSelection(theirGuesses.getCount() - 1);
        if(aiGuess.toLowerCase().equals(mySecretWord.toLowerCase())){
            Toast.makeText(MainActivity.this,"COMPUTER WINS!",Toast.LENGTH_LONG).show();
            theirSecretWordTV.setText(theirSecretWord);
            myGuesses.setBackgroundColor(Color.argb(255,224,224,224));
            finished = true;
            guessEnterButton.setText("New");
        }
        Boolean changed = true;
        while(changed){
            changed = ai.think();
        }
    }


    private int compareGuess (String guess, String word){
        int count = 0;
        //Case doesn't matter.
        guess = guess.toLowerCase();
        word = word.toLowerCase();
        for(char c : guess.toCharArray()){
            //The WORST workaround but it's what I got
            CharSequence cs = "" + c;
            if(word.contains(cs)){
                word = word.replaceFirst(cs.toString(), "");
                count++;
            }
        }
        return count;
    }
}

class Guess{

    public String mText;
    public int mMatches;

    public Guess(String text, int matches){
        mText = text;
        mMatches = matches;
    }

}
