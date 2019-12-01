/**
 * AUCSC 220
 * PiggiesTeam4
 *
 * Player.java
 *
 * The player class contains all the functions for the AI and Mutliplayer game. It contains
 * functions that assigns player's turn and gives the player the opportunity to change their
 * colors.
 *
 * ADD THE OPPORTUNITY TO ADD A FENCE IF THE BOARD IS EMPTY!! (for AI)
 *
 * Created by Arnold Gihozo
 * Started on: November 14, 2019
 * Finished on: November 23, 2019
 *
 * Changelog:
 *  - 2019/11/30: Keegan
 *      Changed the color field from int to int[2], so a lighter shade
 *      color can be stored as well. Lighter color used for indicated
 *      when player turn is over
 */
package com.example.piggiesteam4;

public class Player {

    private int color;
    private int colorLight;
    private boolean turn;
    private int score;


    /**
     * Constructor
     * @param playerColor-- the color of the player!
     */
    Player(int[] playerColor){
        this.turn = false;
        this.score = 0;
        this.color = playerColor[0];
        this.colorLight = playerColor[1];
    }// end of player

    /**
     *
     * The addScore method will take the current play score and updates the total value.
     * @param playerScore- current playScore
     */
    void addScore(int playerScore){
        score = score + playerScore;
    }// end of addScore

    /**
     * This method will clear the score of the player in the current game
     */
    public void clearScore (){
        score = 0;
    }// end of clearScore

    /**
     * This method would return whether it is the player's turn or not.
     * @return- returns whether it is the player's turn (true or false)
     */
    public boolean getTurn(){
       return turn;
    }// end of myTurn

    /**
     * This method will set the player's turn.
     */
    public void setTurn(){
        if (turn == false)
             turn = true;
        turn = false;

    }// end of setTurn

    /**
     * This function will get the player's score
     * @return-- it will return the player's score
     */
    public int getScore(){
        return score;

    }// end of getScore

    /**
     * This function will set the player's color
     * @param colorWanted-- it will take the colorwanted and updates the player's color
     */
    public void setColor(int colorWanted){
        color = colorWanted;
    }// end of setColor

    /**
     * This function will return the player's color
     * @return -- will return the current color of the player
     */
    public int getColor(){
        return color;
    }// end of getColor

    /**
     * Returns the player't complementatry lighter color
     *
     * By Keegan
     *
     * @return - returns the light color
     */
    public int getColorLight() {
        return colorLight;
    }//getColorLight

    /**
     * This method will end the current player's turn(set to false)
     * and set's the other player's turn to True
     * @param otherPlayer-- next player
     */
    public void endTurn(Player otherPlayer){
        this.turn = false;
        otherPlayer.setTurn();
    }// end of endTurn


}// end of player
