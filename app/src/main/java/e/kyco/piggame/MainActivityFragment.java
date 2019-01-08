package e.kyco.piggame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;


import android.os.Handler;


public class MainActivityFragment extends Fragment  {

    private static final String ERROR_TAG = "PigGame Activity";

    private int maxScore = 100;
    private int selfCurrentScore; //personal score
    private int compCurrentScore; //computer score
    private int currentTrough; //trough

    private Random randomToRollDice;

    private boolean playerTurn = true;
    private boolean userStartingGame = true;


    private String imageDiceOneStr = "die1";
    private String imageDiceTwoStr = "die1";

    private String computerForResult = "comp";
    private int totalCurrentScoreSelf;
    private int totalCurrentScoreComp;

    private ImageView diceOne;
    private ImageView diceTwo;

    private ImageView diceOneIV;
    private ImageView diceTwoIV;

   // private TextView results;
    private TextView selfScoreTextView;
    private TextView compScoreTextView;
    private TextView currentTroughTextView;
    private TextView resultsOfTurn;
    private Button rollButton;
    private Button holdButton;

    private SharedPreferences prefSet;
    private SharedPreferences.Editor prefEdit;

    private int totalWinsAllTime;
    private int totalLossAllTime;


/*
    private static final String PREF_NAME = "prefs";
    private static final String KEY_TROUGH = "trough";
    private static final String KEY_COMP_SCORE = "compScore";
    private static final String KEY_USER_SCORE = "userScore";
    private static final String TOTAL_WINS = "totalWins";
    private static final String TOTAL_LOSSES = "totalLosses";*/

    Handler handler = new Handler();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);




        //intializing stuff
        randomToRollDice = new Random();

        //self score, computer score, and result text views
        selfScoreTextView = (TextView) view.findViewById(R.id.displaySelfScoreTV);
        compScoreTextView = (TextView) view.findViewById(R.id.displayCompScoreTV);
        resultsOfTurn = (TextView) view.findViewById(R.id.resultsOfTurnTV);

        //trough text view
        currentTroughTextView = (TextView) view.findViewById(R.id.displayTroughTV);

        //dice image views
        diceOne = (ImageView) view.findViewById(R.id.dieOneIV);
        diceTwo = (ImageView) view.findViewById(R.id.dieTwoIV);

        //buttons
        rollButton = (Button) view.findViewById(R.id.rollAgainButton);
        holdButton = (Button) view.findViewById(R.id.holdButton);


        //button listeners
        rollButton.setOnClickListener(rollButtonListener);
        holdButton.setOnClickListener(holdButtonListener);




        return view;
    }



    public void setMaximumScorePref(SharedPreferences sharedPreferences) {
        String userMaxScore = sharedPreferences.getString(MainActivity.MAX_SCORE, null);
        int userMaxScoreInt = Integer.parseInt(userMaxScore);
        maxScore = userMaxScoreInt;

    }




    //enabling buttons only when it is the players turn so they cant
    //do anything with them while computer is playing
    private void allowButtonUse(){
        rollButton.setEnabled(playerTurn);
        holdButton.setEnabled(playerTurn);
    }


    //set current score of player and display
    private void setPlayerScore(final int totalPlayerScore){
        selfCurrentScore = totalPlayerScore;
        selfScoreTextView.setText(String.valueOf(selfCurrentScore));
    }

    //set current score of computer and display
    private void setComputerScore(final int totalComputerScore){
        compCurrentScore = totalComputerScore;
        compScoreTextView.setText(String.valueOf(compCurrentScore));
    }

    //set and display current trough
    private void setCurrentTrough(final int troughOfTurn){
        currentTrough = troughOfTurn;
        currentTroughTextView.setText(String.valueOf(troughOfTurn));
    }

    //display dice 1 image
    private void setDiceImages(final int newDiceOneImage, final int newDiceTwoImage){


        String dieImages = "dieImages";


        String diceImageOneString = Integer.toString(newDiceOneImage);
        String diceImageTwoString = Integer.toString(newDiceTwoImage);

        AssetManager assets = getActivity().getAssets();


        try (InputStream stream = assets.open(dieImages + "/" + diceImageOneString + ".png")) {
            Drawable dieNumberOne = Drawable.createFromStream(stream, diceImageOneString);
            diceOne.setImageDrawable(dieNumberOne);
        } catch (IOException exception) {
            Log.e(ERROR_TAG, "Error loading: " + diceImageOneString, exception);
        }


        try (InputStream stream = assets.open(dieImages + "/" + diceImageTwoString + ".png")) {
            Drawable dieNumberTwo = Drawable.createFromStream(stream, diceImageTwoString);
            diceTwo.setImageDrawable(dieNumberTwo);
        } catch (IOException exception) {
            Log.e(ERROR_TAG, "Error loading: " + diceImageTwoString, exception);
        }




    }


    public void roll(){
        int roll1 = randomToRollDice.nextInt(6)+ 1;
        int roll2 = randomToRollDice.nextInt(6)+ 1;


        setDiceImages(roll1, roll2);

        //if snake eyes, set 0
        if(roll1 == 1 && roll2 == 1){
            setCurrentTrough(0);

            if(playerTurn){
                setPlayerScore(0);
            }
            else{
                setComputerScore(0);
            }

            changeTurn();
            resultsOfTurn.setText("Rolled two 1's,lose all points and end turn.");

        }
        //if one 1 set 0 for trough and move to others turn
        else if(roll1 == 1 || roll2 == 1){
            setCurrentTrough(0);

         //   currentTroughTextView.setText(currentTrough);
            changeTurn();
            resultsOfTurn.setText("Rolled a 1,turn points cleared and end turn.");
        }
        else{
            setCurrentTrough(currentTrough + roll1 + roll2);


            resultsOfTurn.setText("Added " + currentTrough + " points this turn.");

        }

    }



    private void hold() {

        if(playerTurn){
            setPlayerScore(selfCurrentScore + currentTrough);

        }
        else{
            setComputerScore(compCurrentScore + currentTrough);
        }

        if (selfCurrentScore >= maxScore) {

            totalWinsAllTime++;
            endGame();
        }
        else if(compCurrentScore >= maxScore) {
            totalLossAllTime++;
            endGame();
        }
        setCurrentTrough(0);
        changeTurn();

    }

    private void changeTurn(){
        playerTurn = !playerTurn;
        allowButtonUse();
        if (!playerTurn)
            computerTurn();
    }



    public void updateMaxScore(SharedPreferences sharedPreferences) {
        String scoreOptions = sharedPreferences.getString(MainActivity.MAX_SCORE,null);
        maxScore = Integer.parseInt(scoreOptions);

    }



    private void computerTurn() {

        //delay computer speed by 2seconds (2k millisec)
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        while(!playerTurn) {
                            double holdValue = (.5 * maxScore);
                            //if not at max score (bc obv wanna hold then to win game)
                            // computer will roll if player is
                            //at 75% of max score or more, or if they are less than 50% of maxScore
                            if (!(compCurrentScore + currentTrough >= maxScore) &&
                                    (selfCurrentScore >= (.75 * maxScore) || currentTrough < holdValue))


                                roll();
                        else{
                            hold();
                            }
                    }
                }

    },2000);
    }
    private void endGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Game ended. Total Win/Loss to date: " + totalWinsAllTime + "/" + totalLossAllTime);


        builder.setPositiveButton(R.string.gameEndedNewOne, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {


                    setComputerScore(0);
                    setPlayerScore(0);
                    setCurrentTrough(0);
                    resultsOfTurn.setText("New Game Starting!");

            }

        });
        //without setCancelable if user clicked away from dialog it would not reset scores bc
        //not positive click
        builder.setCancelable(false);
        builder.create();

        builder.show();

    }


    private View.OnClickListener rollButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            //delay button by a tiny bit
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            roll();
                        }


                    }, 250);
        }
    };
    private View.OnClickListener holdButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            //Whenever this is hit it will always be computers turn next
            hold();


        }
    };



    }

