package com.surber.m.jotto;

import android.content.res.Resources;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

/**
 * Created by wo1624bu on 12/6/16.
 */
public class AI {

    public ArrayList<Character> alphabet = new ArrayList<>();
    public ArrayList<Guess> guesses = new ArrayList<>();
    public ArrayList<Character> known = new ArrayList<>();

    public ArrayList<Character> guessedChars = new ArrayList<>();

    static final Random RANDOM = new Random();

    public Resources res;

    public ArrayList<String> knownWords = new ArrayList<>();
    public ArrayList<String> possibleWords = new ArrayList<>();

    public GuessListAdapter guessListAdapter;

    private static AI ourInstance = new AI();

    public static AI getInstance() {
        return ourInstance;
    }

    public Button guessButton;

    private AI() {
        for(char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()){
            alphabet.add(c);
        }

    }

    public void reset(){
        alphabet.clear();
        for(char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()){
            alphabet.add(c);
        }
        possibleWords.clear();
        possibleWords.addAll(knownWords);
        System.out.println("There's " + possibleWords.size() + " possible words after reset.");
        guessedChars.clear();
        known.clear();
        guesses.clear();
        System.out.println("possibleWords reset: " + (possibleWords.size() > 10) + ", " + possibleWords.toString());
        System.out.println("Alphabet reset: " + (alphabet.size() == 26) + ", " + alphabet.toString());
        System.out.println("guessedChars reset: " + (guessedChars.size() == 0) + ", " + guessedChars.toString());
        System.out.println("Known Letters reset: " + (known.size() == 0) + ", " + known.toString());
        System.out.println("Guessed words reset: " + (guesses.size() == 0) + ", " + guesses.toString());
        System.out.println("Ai was reset.");
    }

    public void getWords(){
        try {
            InputStream inputStream = res.openRawResource(R.raw.words);
            //File file = new File("words.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                knownWords.add(line);
                possibleWords.add(line);
                System.out.println(line);
            }
            br.close() ;
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String diverisfy(){
        ArrayList<String> guessedWords = new ArrayList<>();
        for(Guess g : guesses){
            guessedWords.add(g.mText);
        }
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> rotatedKnown = new ArrayList<>();
        int randPart = RANDOM.nextInt(knownWords.size() - 1);
        rotatedKnown.addAll(knownWords.subList(randPart,knownWords.size() - 1));
        rotatedKnown.addAll(knownWords.subList(0,randPart - 1));
        for(String word : rotatedKnown){
            if(guessedWords.contains(word)) continue;
            //Looking for words that use all the known letters, and as few guessed letters as we can.
            //This is going to run out quickly, but we'll just break out if it does.
            int niceChars = 0;
            for(char c : word.toCharArray()){
                if(!guessedChars.contains(Character.valueOf(c)) || known.contains(Character.valueOf(c))){
                    niceChars++;
                }
            }
            if(niceChars >= 4){
                words.add(word);
            }
            if(words.size() > 20) break;
        }

        return words.get(RANDOM.nextInt(words.size()));
    }

    private void compareMinor(){
        //System.out.println("Comparing minor differences.");
        for(Guess g : guesses){
            //System.out.println("Comparing " + g.mText);
            for(Guess gb: guesses){
                if(g.equals(gb)) continue;
                if(g.mMatches - gb.mMatches == 1 || g.mMatches - gb.mMatches == -1){
                    //System.out.println("Very similar to " + gb.mText + "!");
                    ArrayList<Character> sameLetters = new ArrayList<>();
                    for(char c : g.mText.toCharArray()){
                        if(gb.mText.contains(""+c)) sameLetters.add(Character.valueOf(c));
                    }
                    if(sameLetters.size() == 4){
                        Guess better;
                        if(g.mMatches - gb.mMatches == 1) better = g;
                        else better = gb;
                        for(char c : better.mText.toCharArray()){
                            if(!sameLetters.contains(Character.valueOf(c)) && !known.contains(Character.valueOf(c))) known.add(Character.valueOf(c));
                        }
                    }
                } //else System.out.println("Not similar enough to " + gb.mText);
            }
        }
    }

    private boolean removeDeadLetters(Guess guess){
        boolean changed = false;
        for(char c : guess.mText.toCharArray()){
            if(!alphabet.contains(c)){
                //Letter is dead, remove it.
                guess.mText = guess.mText.replaceFirst(""+c,"");
                changed = true;
                System.out.println("CHANGE - Removing dead letters.");
            }
        }
        return changed;
    }

    private boolean killZeroJotWord(Guess guess){
        boolean changed = false;
        if(guess.mMatches == 0){
            for(char c : guess.mText.toCharArray()){
                //Every letter confirmed dead.
                if(alphabet.contains(c)){
                    alphabet.remove((Character)c);
                    changed = true;
                    System.out.println("CHANGE - Killing letters from 0-jot word.");
                }
            }
        }
        return changed;
    }

    private boolean eliminateByAccounted(Guess guess){
        Boolean changed = false;
        //Number of letters in this word that match known letters.
        int matching_known = 0;
        for(char c : guess.mText.toCharArray()){
            if(known.contains(c)){
                matching_known++;
            }
        }

        //Number of matches is EXACTLY number of matches we know should be there.
        if(guess.mMatches <= matching_known){
            String temp = guess.mText;
            for(char c : guess.mText.toCharArray()){
                if(known.contains(Character.valueOf(c))){
                    temp = temp.replaceFirst(""+c,"");
                }
            }
            for(char c : temp.toCharArray()){
                //These letters are what was left.  They have to be dead.
                if(alphabet.contains(Character.valueOf(c))){
                    alphabet.remove(Character.valueOf(c));
                    changed = true;
                    System.out.println("CHANGE - Removing dead letters from known-letter elimination.");
                }
            }
        }
        return changed;
    }

    private boolean confirmFullKnown(Guess guess){
        Boolean changed = false;
        if(guess.mMatches == guess.mText.length()){
            for(char c : guess.mText.toCharArray()){
                //Every single character here has to be in the word.
                if(!known.contains(Character.valueOf(c))) {
                    known.add(Character.valueOf(c));
                    changed = true;
                    System.out.println("CHANGE - Marking letters in words with jots equal to living letter length as known.");
                }
            }
        }
        return changed;
    }

    public boolean think(){
        compareMinor();


        Boolean changed = false;
        for(Guess guess : guesses){
            if(guess.mText.length() == 0){
                //completely manually eliminated word.
                continue;
            }
            if(removeDeadLetters(guess)) changed = true;
            if(killZeroJotWord(guess)) changed = true;
            if(eliminateByAccounted(guess)) changed = true;
            if(confirmFullKnown(guess)) changed = true;

            if (possibleWords.contains(guess.mText)) {
                possibleWords.remove(guess.mText);
                changed = true;
                System.out.println("CHANGE - Removing guessed word.");
            }

        }

        //Five jots means those are the five letters.  No exceptions.
        if(known.size() == 5){
            alphabet.clear();
            alphabet.addAll(known);
        }

        try {
            ArrayList<String> deadWords = new ArrayList<>();
            for (String word : possibleWords) {
                boolean dead = false;
                for (char c : word.toCharArray()) {
                    if (!alphabet.contains(Character.valueOf(c))) {
                        //If the word has a letter in it we know is dead, the secret word is definitely not that.
                        dead = true;
                    }
                }
                for(char c : known){
                    if(!word.contains(""+c)){
                        //If the word doesn't have one of the letters we know is in the secret word, it can't be the secret word.
                        dead = true;
                    }
                }
                if(dead) deadWords.add(word);
            }
            //Remove all the killed words from the list of possible words.
            for(String word : deadWords){
                possibleWords.remove(word);
                changed = true;
                System.out.println("CHANGE - Removing dead words.");
            }

            System.out.println("Done processing.");
        } catch (ConcurrentModificationException cme){
            cme.printStackTrace();
        }

        System.out.println("Remaining letters: " + alphabet.toString());
        System.out.println("Known letters: " + known.toString());
        System.out.println("Number of living words: " + String.valueOf(possibleWords.size()));
        //Tells the AI if it knows anything now that it didn't before.
        // If so, we're going to run it again until it DOESN'T learn anything new.
        return changed;
    }



    public String guess(){

        if(alphabet.size() > 15 && known.size() < 2){
            //We know very little right now, so we'll just guess some very different words to get the ball rolling.
            return diverisfy();
        }else {
            System.out.println("Guessing with purpose.");
            //Return a word that fits the bill.  By selecting these, the minor differences can tell us which letters are and are not in the word.
            return possibleWords.get(RANDOM.nextInt(possibleWords.size()));
        }
    }


}
