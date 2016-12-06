package com.surber.m.jotto;

import java.util.ArrayList;

/**
 * Created by wo1624bu on 12/6/16.
 */
public class AI {

    public ArrayList<Character> alphabet = new ArrayList<>();
    public ArrayList<Guess> guesses = new ArrayList<>();
    public ArrayList<Character> known = new ArrayList<>();

    public ArrayList<String> knownWords = new ArrayList<>();

    public GuessListAdapter guessListAdapter;

    private static AI ourInstance = new AI();

    public static AI getInstance() {
        return ourInstance;
    }

    private AI() {
        for(char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()){
            alphabet.add(c);
        }

    }

    public void think(){

        //If we know fewer than 3 letters OR have more than 9 letters alive,
        //Attempt to find a word containing as many living letters as possible (letters in alphabet) AND as few already guessed letters as possible.
        //When a word is 0 letters, eliminate all those letters from guessed words and alphabet
        //When the remaining letters in a word equals the number of matches, consider those letters known.
        //WORDS
        //RD___ 2
        //WS___ 2
        //WRDS
        //When we know at least 3 letters AND have fewer than 9 living letters,
        //Make a list of all possible words, and try those.
        //When a word returns the same number as known numbers in it, eliminate the unknown ones as dead.
        //Win.

        //If we know fewer than 3 letters OR have more than 9 letters alive,

        for(Guess guess : guesses){
            if(guess.mText.length() == 0){
                //completely manually eliminated word.
                continue;
            }
            if(guess.mMatches == 0){
                for(char c : guess.mText.toCharArray()){
                    //Every letter confirmed dead.
                    if(alphabet.contains(c)){
                        alphabet.remove((Character)c);
                    }
                }
            }

            //QUEUE
            //QUEST 3 QUE 3

            //QUEST
            //QUEUE 3 QUE 5

            //Number of letters in THIS WORD that match known letters.
            int matching_known = 0;
            for(char c : guess.mText.toCharArray()){
                if(known.contains(c)){
                    matching_known = 0;
                }
            }
            //Number of matches is EXACTLY number of matches we know about.
            if(guess.mMatches == matching_known){
                String temp = guess.mText;
                for(char c : guess.mText.toCharArray()){
                    if(known.contains(c)){
                        temp = temp.replaceFirst(""+c,"");
                    }
                }
                for(char c : temp.toCharArray()){
                    //These letters are what was left.  They have to be dead.
                    if(alphabet.contains(c)){
                        alphabet.remove(c);
                    }
                }
            }
            for(char c : guess.mText.toCharArray()){
                if(!alphabet.contains(c)){
                    //Letter is dead, remove it.
                    guess.mText.replaceFirst(""+c,"");
                }
            }
            System.out.println("Remaining letters: " + alphabet.toString());
        }

        /*if(known.size() < 3 || alphabet.size() > 9){



            for(String word : knownWords){

            }

        }*/


    }

    public String getWordsWith(CharSequence cs){

        return null;
    }

    public String getWordsWithout(CharSequence cs){

        return null;
    }


}
