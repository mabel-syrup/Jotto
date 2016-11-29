package com.surber.m.jotto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String mySecretWord;
    String theirSecretWord;

    TextView mySecretWordTV;
    Button guessEnterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySecretWordTV = (TextView) findViewById(R.id.my_secret_word);
        guessEnterButton = (Button) findViewById(R.id.guess_enter_button);

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
                //Send guess!
            }
        });
    }


    private int compareGuess (String guess, String word){

        /*for(char c : guess.toCharArray()){
            try{
                int index = word.indexOf(c);
            }
        }*/
    }

}
