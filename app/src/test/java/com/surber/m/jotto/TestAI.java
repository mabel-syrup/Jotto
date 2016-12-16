package com.surber.m.jotto;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestAI {

    //Right-click on this file and select Run to run the unit tests; you don't need to start your application. 
    
    AI testAI;

    @Before
    public void setUp() {
        System.out.println("setup runs before each test");
        testAI = AI.getInstance();
    }

    @Test
    public void test_dontKnowMuch() throws Exception {

        //Create an AI in the setup method, will need an AI object for each test.
        //setup runs before each test.

        //mock alphabet, mock known arraylists

        //Four scenarios
        // alphabet has more than 15 letters; known has more than 2
        // alphabet has more than 15 letters; known has less than 2
        // etc....

        //Scenario 1: alphabet has more than 15; mockKnown has more than 2  - we know a lot
        ArrayList<Character> mockAlphabet = new ArrayList<>();
        for (char ch = 65; ch < 65+17 ; ch++) {
            mockAlphabet.add(ch);
        }

        testAI.alphabet = mockAlphabet;

        ArrayList<Character> mockKnown = new ArrayList<>();
        mockKnown.add('a');
        mockKnown.add('b');
        mockKnown.add('c');

        testAI.known = mockKnown;

        assertFalse(testAI.dontKnowMuch());

        //todo the other cases, edge cases.

    }

    // TODO write more methods, one to test each part of the AI functionality.
    // First, break your AI code into smaller, more focused methods.

}
