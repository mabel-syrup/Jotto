package com.surber.m.jotto;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    EditText guessWord;
    Button guessEnterButton;

    ListView theirGuesses;
    ListView myGuesses;

    ArrayList<Guess> myGuessesAL = new ArrayList<>();
    ArrayList<Guess> theirGuessesAL = new ArrayList<>();

    public AI ai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ai = AI.getInstance();

        final GuessListAdapter myGuessAdapter =
                new GuessListAdapter(MainActivity.this,R.layout.guess,myGuessesAL);
        final GuessListAdapter theirGuessAdapter =
                new GuessListAdapter(MainActivity.this,R.layout.guess,theirGuessesAL);

        ai.guessListAdapter = theirGuessAdapter;

        mySecretWordTV = (TextView) findViewById(R.id.my_secret_word);
        guessEnterButton = (Button) findViewById(R.id.guess_enter_button);
        theirGuesses = (ListView) findViewById(R.id.their_guesses);
        myGuesses = (ListView) findViewById(R.id.my_guesses);
        guessWord = (EditText) findViewById(R.id.my_guess);



        myGuesses.setAdapter(myGuessAdapter);
        theirGuesses.setAdapter(theirGuessAdapter);

        theirSecretWord = "koala";

        if(mySecretWord.isEmpty()){
            //No secret word!  Gotta make one.
            //For now, we'll just place "words" in.
            mySecretWord = "words";
            mySecretWordTV.setText(mySecretWord);
        }

        guessEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myGuess = guessWord.getText().toString();
                //All guesses and words must be 5-letter english non-proper-nouns.
                if(myGuess.length() != 5){
                    Toast.makeText(MainActivity.this,"You need exactly 5 letters.",Toast.LENGTH_SHORT).show();
                } else {
                    //Result is the number of matched letters between the guess and the secret word.
                    //G: GROOM
                    //W: FROGS
                    //Result: 3
                    int rslt = compareGuess(myGuess,theirSecretWord);
                    //Toast.makeText(MainActivity.this,"Temporary text informing you you got " + rslt + " matches.",Toast.LENGTH_SHORT).show();
                    myGuessesAL.add(new Guess(myGuess,rslt));
                    myGuessAdapter.add(new Guess(myGuess,rslt));
                    myGuessAdapter.notifyDataSetChanged();
                    if(myGuess.toLowerCase().equals(theirSecretWord.toLowerCase())){
                        Toast.makeText(MainActivity.this,"YOU WIN!",Toast.LENGTH_LONG).show();
                    }

                    //System.out.println("There's " + myGuessAdapter.getCount() + " items in the list.");
                    AITurn(myGuess,theirGuessAdapter);
                }
            }
        });
    }

    private void AITurn(String guessW, GuessListAdapter adapter){
        //REMOVE ARGUMENT!
        int rslt = compareGuess(guessW,mySecretWord);
        Guess guess = new Guess(guessW,rslt);
        ai.guesses.add(guess);
        theirGuessesAL.add(guess);
        adapter.add(guess);
        adapter.notifyDataSetChanged();
        if(guessW.toLowerCase().equals(mySecretWord.toLowerCase())){
            Toast.makeText(MainActivity.this,"COMPUTER WINS!",Toast.LENGTH_LONG).show();
        }
        ai.think();
    }


    private int compareGuess (String guess, String word){
        int count = 0;
        //Case doesn't matter.
        //GueSS
        //GOOSE
        //3
        guess = guess.toLowerCase();
        word = word.toLowerCase();
        for(char c : guess.toCharArray()){
            //The WORST workaround but it's what I got
            CharSequence cs = "" + c;
            if(word.contains(cs)){
                //Remove this instance from the word.
                //A guess of "UUUUU" in "UNDER" should be 1, not 5.
                word = word.replaceFirst(cs.toString(), "");
                count++;
            }
        }
        return count;
    }

    /*@Override
    protected void onResume() {

    }

    @Override
    protected void onPause() {

    }*/
}

class Guess{

    public String mText;
    public int mMatches;

    public Guess(String text, int matches){
        mText = text;
        mMatches = matches;
    }

}
