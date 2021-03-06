package com.example.piggiesteam4;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import java.util.Random;

import java.util.concurrent.TimeUnit;

public class GridFragment extends Fragment
        implements View.OnTouchListener, View.OnClickListener {

    //Variable Declarations
    private MainActivity main;
    private boolean isMultiplayer;
    Game fragmentGame;
    private OnFragmentInteractionListener mListener;
    private Button p1ScoreButton;
    private Button p2ScoreButton;
    private gameStateListener listener;
    private View fragmentView;
    private int gridSize;
    private boolean buttonsEnabled = true;
    private MediaPlayer[] fenceSounds;
    private MediaPlayer[] pigSounds;

    /**
     * This was placed by default, best not to remove it
     * - Keegan
     */
    public GridFragment() {
        // Required empty public constructor
    }//GridFragment

    /**
     * On creation of the fragment, find out if it is for a multiplayer game or not
     * so it can point to the correct game inside MainActivity
     *
     * By Keegan
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isMultiplayer = getArguments().getBoolean("multiplayer");

        main = (MainActivity) getActivity(); //get the main activity to share game variables

        Log.d("onCreate grid fragment", "isMultiplayer = " + isMultiplayer);

        if (isMultiplayer) {
            fragmentGame = main.multiPlayer;
        }//if

        else {
            fragmentGame = main.singlePlayer;
        }//if

        p1ScoreButton = main.p1Score;
        p2ScoreButton = main.p2Score;

        updateScoreView();

        gridSize = fragmentGame.getGrid().getX();
        initializeSoundEffects();

//        Log.d("inFragment", "Is current game same as fragmentGame " + (fragmentGame == main.currentGame));
//        Log.d("inFragment", "Is this fragmentGame multiplayer " + isMultiplayer);
//        Log.d("inFragment", "Is this game same as singlePlayer main " + (fragmentGame == main.singlePlayer));
//        Log.d("inFragment", "Scores of the current game are "
//                + main.currentGame.getPlayer1().getScore() + " " + main.currentGame.getPlayer2().getScore());
//        Log.d("inFragment", "Scores of the fragment game are "
//                + fragmentGame.getPlayer1().getScore() + " " + fragmentGame.getPlayer2().getScore());

    }//onCreate

    /**
     * Set up the buttons on creation of the fragment
     *
     * I also set an onTouchListener for every button: what this does, is when a unplaced
     * fence/button is pressed and held, a little preview of the fence appears first,
     * designed to give a little bit of feedback to the player
     *
     * By Keegan
     *
     * @param inflater - inflater to set up the layout
     * @param container - where the fragment will be contained
     * @param savedInstanceState - the saved instance if recreating the fragment
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String viewID = "fragment_grid_" + fragmentGame.getGrid().getX() +
                fragmentGame.getGrid().getY();

        int resID = getResources().getIdentifier(viewID, "layout", main.getPackageName());

        View v = inflater.inflate(resID, container, false);
        fragmentView = v;
        //loadGame();
        showSaved();
        setReset();

        setFenceListeners(v);

        // Inflate the layout for this fragment
        return v;

    }//onCreateView

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     * This was put in here by default, I don't wanna touch it, just to be safe
     * -Keegan
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }//OnFragmentInteractionListener

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * This was put in here by default, I don't wanna touch it, just to be safe
     * -Keegan
     *
     * @return A new instance of fragment GridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridFragment newInstance() {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }//newInstance

    /**
     * This was placed by default, best not to touch it
     * - Keegan
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d("onAttach", "onAttach called");

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }//onAttach

    /**
     * This was placed by default, best not to touch it
     * - Keegan
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }//onDetach

    /**
     * When a button is pressed (ie fence placement attempted), a check is performed to
     * see if a fence can be placed at the specified location (if it hasn't been placed already)
     *
     * By Alvin
     *
     * @param v - the button clicked
     */
    @Override
    public void onClick(View v) {

        if (!buttonsEnabled) {
            return;
        }

        final char VERTICAL_ORIENTATION = 'v';
        final char HORIZONTAL_ORIENTATION = 'h';
        final int ORIENTATION_INDEX = 8;
        final int ROW_INDEX = 15;
        final int COL_INDEX = 16;

        String idName = main.getResources().getResourceEntryName(v.getId());
        Log.d("onClick", "Current player instance is " + fragmentGame.getCurrentPlayer());
        int row = Integer.parseInt((idName.charAt(ROW_INDEX) + "").trim());
        int col = Integer.parseInt((idName.charAt(COL_INDEX) + "").trim());
        char orientation = idName.charAt(ORIENTATION_INDEX);

        if (orientation == HORIZONTAL_ORIENTATION) {
            setHorizontalFence(v, row, col);
        }//if
        else if (orientation == VERTICAL_ORIENTATION) {
            setVerticalFence(v, row, col);
        }//elseif
        else {
            throw new AssertionError("Unexpected value, fence does not announce orientation");
        }//else

        listener.endGame(fragmentGame, this);

    }//onClick

    /**
     *
     */
    public interface gameStateListener {
        void endGame(Game game, GridFragment frag);
//        void saveGame(GridFragment frag);
//        boolean retrieveGame(GridFragment frag);
    }//gameStateListener

    /**
     * Sets the listener.
     *
     * @param listener the listener.
     */
    public void setListener(gameStateListener listener) {
        this.listener = listener;
    }//setListener

    /**
     * Saves game on pause.
     */
    @Override
    public void onPause() {
        super.onPause();
        //listener.saveGame(this);
    }//onPause

    /**
     * Shows the fences previously selected in a saved game.
     *
     * By Alvin
     */
    public void showSaved() {

        Grid.Fence[][] xCoords = fragmentGame.getGrid().getxCoords();
        Grid.Fence[][] yCoords = fragmentGame.getGrid().getyCoords();
        Grid.Fence[][] pens = fragmentGame.getGrid().getPens();

        for (int row = 0; row < gridSize; row++) {

            for (int col = 0; col < gridSize; col++) {

                if (col < gridSize - 1) {

                    Grid.Fence currentXFence = xCoords[row][col];

                    if (currentXFence.exists()) {

                        Button fence = (Button) fragmentView.findViewById(getResources()
                                .getIdentifier("grid_" + gridSize + gridSize + "_hfence_" +
                                                row + col, "id",
                                        this.getActivity().getPackageName()));

                        fence.getBackground()
                                .setColorFilter(currentXFence.getColor(), PorterDuff.Mode.MULTIPLY);

                        fence.setAlpha((float) 1.0);

                    }//if

                }//if

                if (row < gridSize - 1) {

                    Grid.Fence currentYFence = yCoords[row][col];

                    if (currentYFence.exists()) {

                        Button fence = (Button) fragmentView.findViewById(getResources()
                                .getIdentifier("grid_" + gridSize + gridSize + "_vfence_" +
                                                row + col, "id",
                                        this.getActivity().getPackageName()));

                        fence.getBackground()
                                .setColorFilter(currentYFence.getColor(), PorterDuff.Mode.MULTIPLY);

                        fence.setAlpha((float) 1.0);
                    }//if

                }//if

                if ((row < gridSize - 1) && (col < gridSize - 1)) {

                    Grid.Fence currentPen = pens[row][col];

                    if (currentPen.exists()) {

                        ImageView pig = (ImageView) fragmentView.findViewById(getResources()
                                .getIdentifier("grid_" + gridSize + gridSize + "_pig_" +
                                                row + col, "id",
                                        this.getActivity().getPackageName()));

                        pig.setColorFilter(currentPen.getColor(), PorterDuff.Mode.MULTIPLY);
                        pig.setVisibility(View.VISIBLE);

                    }//if

                }//if

            }//for

        }//for

        main.setScoreButtonColor();
    }//showSaved

    /**
     * Resets the fence UI.
     */
    public void resetFences() {

        for (int row = 0; row < gridSize; row++) {

            for (int col = 0; col < gridSize; col++) {

                if (col < (gridSize - 1)) {

                    Button fence = (Button) fragmentView.findViewById(getResources()
                            .getIdentifier("grid_" + gridSize + gridSize + "_hfence_" +
                                            row + col, "id",
                                    this.getActivity().getPackageName()));

                    fence.getBackground()
                            .setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

                    fence.setAlpha((float) 0.0);
                }//if

                if (row < gridSize - 1) {

                    Button fence = (Button) fragmentView.findViewById(getResources()
                            .getIdentifier("grid_" + gridSize + gridSize + "_vfence_" +
                                            row + col, "id",
                                    this.getActivity().getPackageName()));

                    fence.getBackground()
                            .setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

                    fence.setAlpha((float) 0.0);

                }//if

            }//for

        }//for

    }//resetFences

    /**
     * Ends the current players turn, and set's the other players turn to true.
     * Also updates the color of the player score button to indicate who the new
     * current player is
     * <p>
     * By Keegan
     *
     * @param currentPlayer - the player who's turn it currently is
     */
    public void toggleTurn(Player currentPlayer) {

        int currentTurn;

        if (currentPlayer.getWhichplayer() == 1) {
            currentTurn = 2;
        }//if

        else if (currentPlayer.getWhichplayer() == 2) {
            currentTurn = 1;
        }// else if

        else {
            throw new AssertionError("current player has no player turn value???");
        }//else

        if (currentPlayer == fragmentGame.getPlayer1()) {

            p2ScoreButton.getBackground().setColorFilter(fragmentGame.getPlayer2().getColor(),
                    PorterDuff.Mode.MULTIPLY);

            p1ScoreButton.getBackground().setColorFilter(fragmentGame.getPlayer1().getColorLight(),
                    PorterDuff.Mode.MULTIPLY);

        }//if

        else {

            p1ScoreButton.getBackground().setColorFilter(fragmentGame.getPlayer1().getColor(),
                    PorterDuff.Mode.MULTIPLY);

            p2ScoreButton.getBackground().setColorFilter(fragmentGame.getPlayer2().getColorLight(),
                    PorterDuff.Mode.MULTIPLY);

        }//else

        fragmentGame.toggleCurrentPlayer();

        setMainCurrentPlayer(fragmentGame.getCurrentPlayer());

        //This lets the AI have its turn.
        if (fragmentGame.getCurrentPlayer().isCPU()){
            buttonsEnabled = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep200ms();
                    boolean penFound;
                    do{
                        sleep200ms();
                        penFound = aiTurn();
                        if (fragmentGame.isGameOver()){
                            break;
                        }
                    } while (penFound);

                    buttonsEnabled = true;
                }
            }).start();
        }

        if (fragmentGame.isGameOver()){
            listener.endGame(fragmentGame, this);
        }

    }//toggleTurn

    /**
     * Waits for 200ms.
     */
    public void sleep200ms(){
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        }
        catch (Exception e){

        }
    }

    /**
     * Sets player 1 to be the current player.
     * <p>
     * By Alvin
     */
    public void setP1Current() {

        if(fragmentGame.getCurrentPlayer() != fragmentGame.getPlayer1()){
//            toggleTurn(fragmentGame.getCurrentPlayer());

            fragmentGame.toggleCurrentPlayer();
            setMainCurrentPlayer(fragmentGame.getCurrentPlayer());
        }//if

    }//setP1Current

    protected MainActivity.resetListener resetListener = new MainActivity.resetListener() {
        /**
         * Resets the game.
         */
        @Override
        public void reset() {
            fragmentGame.endGame();
            resetFences();
            setP1Current();
            resetPens();
        }
    };

    /**
     * Sets the resetListener in the main activity.
     */
    public void setReset() {
        ((MainActivity) getActivity()).setResetListener(resetListener);
    }//setReset

    /**
     * Sets the currentGame in the main activity to the currently active game.
     *
     * @param isMultiplayer
     */
    public void setMainCurrentGame(boolean isMultiplayer) {

        if (isMultiplayer) {
            main.currentGame = main.multiPlayer;
        }//if

        else {
            main.currentGame = main.singlePlayer;
        }//else

    }//setMainCurrentGame

    /**
     * @param currentPlayer
     */
    public void setMainCurrentPlayer(Player currentPlayer) {
        main.currentPlayer = currentPlayer;
    }//setMainCurrentPlayer

    /**
     *
     */
    public void resetPens() {

        int sizeX = fragmentGame.getGrid().getX();
        int sizeY = fragmentGame.getGrid().getY();

        for (int i = 0; i < sizeX - 1; i++) {

            for (int j = 0; j < sizeY - 1; j++) {

                String penID = "grid_" + sizeX + sizeY + "_pig_" + i + j;
                int resID = getResources().getIdentifier(penID, "id", main.getPackageName());

                ImageView pig = ((ImageView) fragmentView.findViewById(resID));
                resetPigVisibility(pig);
                fragmentGame.getGrid().setPen(i, j, false);

            }//for

        }//for

    }//resetPens

    /**
     * Called when a pen has been completed. Makes the parameter pig (in the pen) visible,
     * ands give the pig a color filter based on who created the pen
     *
     * By Keegan
     *
     * @param v - the image view to toggle visibility of
     */
    public void togglePigVisibility(View v) {

        ImageView pig = (ImageView) v;

        if (pig.getVisibility() == View.VISIBLE) {
            resetPigVisibility(pig);
        }//if

        else {

            pig.setColorFilter(fragmentGame.getCurrentPlayer().getColorLight(),
                    PorterDuff.Mode.MULTIPLY);
            pig.setVisibility(View.VISIBLE);

            final int ROW_INDEX = 12;
            final int COL_INDEX = 13;
            String pigId = main.getResources().getResourceEntryName(v.getId());
            int row = Integer.parseInt((pigId.charAt(ROW_INDEX) + "").trim());
            int col = Integer.parseInt((pigId.charAt(COL_INDEX) + "").trim());

            fragmentGame.getGrid().getPens()[row][col].setColor(fragmentGame.getCurrentPlayer().getColorLight());
            fragmentGame.getGrid().getPens()[row][col].setExistence(true);

            playPigSound();

        }//else

    }//togglePigVisibility

    /**
     * Sets the provided image view to be invisible
     *
     * By Keegan
     *
     * @param pig - the image view (of a pig) to make invisible
     */
    public void resetPigVisibility(ImageView pig) {

        pig.setVisibility(View.INVISIBLE);
        pig.clearColorFilter();

    }//resetPigVisibility

    /**
     * Added for fun
     * If a "fence" hasn't been place yet, hovering over it with your finger
     * will make it slightly visible. works by checking how visible the selected fence
     * currently is: if alpha = 1, then already placed so don't bother
     * <
     * By Keegan
     *
     * @param v     - the view being touched
     * @param event - the event (ACTION_DOWN = finger push down, ACTION_UP = finger lift)
     * @return false for some reason
     */
    public boolean onTouch(View v, MotionEvent event) {

        //button pressed down
        if (event.getAction() == MotionEvent.ACTION_DOWN &&
                v.findViewById(v.getId()).getAlpha() != 1) {
            v.findViewById(v.getId()).setAlpha((float) 0.15);
        }//if

        //button released
        if (event.getAction() == MotionEvent.ACTION_UP &&
                v.findViewById(v.getId()).getAlpha() != 1) {
            v.findViewById(v.getId()).setAlpha((float) (0.0));
        }//if

        return false;

    }//onTouch

    /**
     * Called when a horizontal button is touched. Takes the button's coordinates on grid.
     * If fence doesn't exist at corresponding spot in the grid, it's placed and the button
     * is updated to reflect this
     *
     * A switch case would be more ideal here, but a switch can't be used for non-constant
     * variables (each grid has a variable amount of rows, so at compile time the number of
     * rows can't be determined as this is designed to work with all grid sizes)
     *
     * By Keegan
     *
     * @param v   - the button pressed
     * @param row - button's respective row coordinate into xCoords of the grid
     * @param col - button's respective col coordinate into xCoords of the grid
     */
    void setHorizontalFence(View v, int row, int col) {

        //if fence is placed on top row
        if (row == 0) {
            setTopRowFence(v, row, col);
        }//if

        //if fence is placed on the bottom of grid
        else if (row == fragmentGame.getGrid().getY() - 1) {
            setBottomRowFence(v, row, col);
        }//else if

        else {
            setMidRowFence(v, row, col);
        }//else

        updateScoreView();

    }//setHorizontalFence

    /**
     * Sets a fence on the top row
     *
     * By Keegan
     *
     * @param v - the fence to be placed
     * @param row
     * @param col
     */
    void setTopRowFence(View v, int row, int col) {

        int currentColor = fragmentGame.getCurrentPlayer().getColor();
        Player currentPlayer = fragmentGame.getCurrentPlayer();

        //if fence is placed on top row
        if (fragmentGame.getGrid().setFenceX(row, col, currentColor)) {

            playFenceSound();

            //set color
            v.findViewById(v.getId()).getBackground()
                    .setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);

            v.findViewById(v.getId()).setAlpha((float) 1.0);

            //if fence placement didn't make pen
            if (!fragmentGame.getGrid().checkPenBelow(row, col, currentPlayer)) {

                //other players turn
                toggleTurn(currentPlayer);

            }//if

            //if it did, make pig visible
            else {
                togglePigVisibility(getUpdatedPenView(row, col));
            }//else

        }//if

    }//setTopRowFence

    /**
     * Sets a fence on the bottom row
     *
     * By Keegan
     *
     * @param v
     * @param row
     * @param col
     */
    void setBottomRowFence(View v, int row, int col) {

        int currentColor = fragmentGame.getCurrentPlayer().getColor();
        Player currentPlayer = fragmentGame.getCurrentPlayer();

        //if fence is placed on top row
        if (fragmentGame.getGrid().setFenceX(row, col, currentColor)) {

            playFenceSound();

            //set color
            v.findViewById(v.getId()).getBackground()
                    .setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);

            v.findViewById(v.getId()).setAlpha((float) 1.0);

            //if fence placement didn't make pen
            if (!fragmentGame.getGrid().checkPenAbove(row, col, currentPlayer)) {

                //other players turn
                toggleTurn(currentPlayer);

            }//if

            //if it did, make pig visible
            else {
                togglePigVisibility(getUpdatedPenView(row - 1, col));
            }//else

        }//if

    }//setBottomRowFence

    /**
     * Sets a horizontal fence that isn't on the top or bottom row
     *
     * By Keegan
     *
     * @param v
     * @param row
     * @param col
     */
    void setMidRowFence(View v, int row, int col) {

        boolean createdPen = false;
        int currentColor = fragmentGame.getCurrentPlayer().getColor();
        Player currentPlayer = fragmentGame.getCurrentPlayer();

        //if fence is placed
        if (fragmentGame.getGrid().setFenceX(row, col, currentColor)) {

            playFenceSound();

            //set color
            v.findViewById(v.getId()).getBackground()
                    .setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);

            v.findViewById(v.getId()).setAlpha((float) 1.0);

            //check for pen to the left
            if (fragmentGame.getGrid().checkPenAbove(row, col, currentPlayer)) {
                togglePigVisibility(getUpdatedPenView(row - 1, col));
                createdPen = true;
            }//if

            //check for pen to the right
            if (fragmentGame.getGrid().checkPenBelow(row, col, currentPlayer)) {
                togglePigVisibility(getUpdatedPenView(row, col));
                createdPen = true;
            }//if

            //other player turn if no pen made
            if (!createdPen) {
                toggleTurn(currentPlayer);
            }//if

        }//if

    }//setMidFence

    /**
     * Sets a vertical fence, calling the necessary function
     * based on its location
     *
     * By Keegan
     *
     * @param v
     * @param row
     * @param col
     */
    void setVerticalFence(View v, int row, int col) {

        //if fence is placed on top row
        if (col == 0) {
            setLeftColFence(v, row, col);
        }//if

        //if fence is placed on the bottom of grid
        else if (col == fragmentGame.getGrid().getX() - 1) {
            setRightColFence(v, row, col);
        }//else if

        else {
            setMidColFence(v, row, col);
        }//else

        updateScoreView();

    }//setVerticalFence

    /**
     * @param v
     * @param row
     * @param col
     */
    void setLeftColFence(View v, int row, int col) {

        int currentColor = fragmentGame.getCurrentPlayer().getColor();
        Player currentPlayer = fragmentGame.getCurrentPlayer();

        //if fence is placed on top row
        if (fragmentGame.getGrid().setFenceY(row, col, currentColor)) {

            playFenceSound();

            //set color
            v.findViewById(v.getId()).getBackground()
                    .setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);

            v.findViewById(v.getId()).setAlpha((float) 1.0);

            //if fence placement didn't make pen
            if (!fragmentGame.getGrid().checkPenRight(row, col, currentPlayer)) {

                //other players turn
                toggleTurn(currentPlayer);

            }//if

            //if it did, make pig visible
            else {
                togglePigVisibility(getUpdatedPenView(row, col));
            }//else

        }//if

    }//setTopRowFence

    /**
     * @param v
     * @param row
     * @param col
     */
    void setRightColFence(View v, int row, int col) {

        int currentColor = fragmentGame.getCurrentPlayer().getColor();
        Player currentPlayer = fragmentGame.getCurrentPlayer();

        //if fence is placed on top row
        if (fragmentGame.getGrid().setFenceY(row, col, currentColor)) {

            playFenceSound();

            //set color
            v.findViewById(v.getId()).getBackground()
                    .setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);

            v.findViewById(v.getId()).setAlpha((float) 1.0);

            //if fence placement didn't make pen
            if (!fragmentGame.getGrid().checkPenLeft(row, col, currentPlayer)) {

                //other players turn
                toggleTurn(currentPlayer);

            }//if

            //if it did, make pig visible
            else {
                togglePigVisibility(getUpdatedPenView(row, col - 1));
            }//else

        }//if

    }//setBottomRowFence

    /**
     * Sets a vertical fence that isn't on the far left or right
     *
     * By Keegan
     *
     * @param v - the fence placed
     * @param row
     * @param col
     */
    void setMidColFence(View v, int row, int col) {

        boolean createdPen = false;
        int currentColor = fragmentGame.getCurrentPlayer().getColor();
        Player currentPlayer = fragmentGame.getCurrentPlayer();

        //if fence is placed
        if (fragmentGame.getGrid().setFenceY(row, col, currentColor)) {

            playFenceSound();

            //set color
            v.findViewById(v.getId()).getBackground()
                    .setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);

            v.findViewById(v.getId()).setAlpha((float) 1.0);

            //check for pen to the left
            if (fragmentGame.getGrid().checkPenLeft(row, col, currentPlayer)) {
                togglePigVisibility(getUpdatedPenView(row, col - 1));
                createdPen = true;
            }//if

            //check for pen to the right
            if (fragmentGame.getGrid().checkPenRight(row, col, currentPlayer)) {
                togglePigVisibility(getUpdatedPenView(row, col));
                createdPen = true;
            }//if

            //other player turn if no pen made
            if (!createdPen) {
                toggleTurn(currentPlayer);
            }//if

        }//if

    }//setMidFence

    /**
     * Called when a pen is completed. Gets the corresponding pen's pig image, so it
     * can be passed to togglePigVisibility to make it visible
     *
     * By Alvin
     *
     * @param row - row index for pig
     * @param col - col index for pig
     * @return the view of the pig that has been penned
     */
    public View getUpdatedPenView(int row, int col) {

        String buttonId = "grid_" + gridSize + gridSize + "_pig_" + row + col;
        return this.getActivity().findViewById(getResources()
                .getIdentifier(buttonId, "id", main.getPackageName()));

    }//getUpdatedPenView

    /**
     * Called after a fence is placed. Keeps the visible player scores up to date
     *
     * By Keegan
     */
    public void updateScoreView() {

        //update the scoreboard in MainActivity
        p1ScoreButton.setText(Integer.toString(fragmentGame.getPlayer1().getScore()));
        p2ScoreButton.setText(Integer.toString(fragmentGame.getPlayer2().getScore()));

    }//updateScoreView

    /**
     * Set the listeners for interactions on the fences
     *
     * By Keegan
     *
     * @param v
     */
    void setFenceListeners(View v) {

        int sizeX = fragmentGame.getGrid().getX();
        int sizeY = fragmentGame.getGrid().getY();

        //set listeners for the horizontal fences
        for (int i = 0; i < sizeX; i++) {

            for (int j = 0; j < sizeY - 1; j++) {

                String buttonID = "grid_" + sizeX + sizeY + "_hfence_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", main.getPackageName());

                Button button = ((Button) v.findViewById(resID));
                button.setOnClickListener(this);
                button.setOnTouchListener(this);

            }//for

        }//for

        //set listeners for the vertical fences
        for (int i = 0; i < sizeX - 1; i++) {

            for (int j = 0; j < sizeY; j++) {

                String buttonID = "grid_" + sizeX + sizeY + "_vfence_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", main.getPackageName());

                Button button = ((Button) v.findViewById(resID));
                button.setOnClickListener(this);
                button.setOnTouchListener(this);

            }//for

        }//for

    }//setFenceListeners

    /**
     * Carries out the AI's turn.
     * @return whether a pen is found.
     */
    public boolean aiTurn(){
        boolean foundPen = fragmentGame.getCurrentPlayer().placeFenceCPU(fragmentGame);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                showSaved();
                updateScoreView();
                if (fragmentGame.isGameOver()){
                    listener.endGame(fragmentGame, GridFragment.this);
                }//if
            }//run
        });//Runnable
        return foundPen;
    }//aiTurn

    /**
     * Sets fragmentGame, used in loading.
     * @param fragmentGame the game.
     */
    public void setFragmentGame(Game fragmentGame) {
        this.fragmentGame = fragmentGame;
    }//setFragmentGame

    /**
     * Sets main, used in loading.
     * @param main the activity.
     */
    public void setMain(MainActivity main) {
        this.main = main;
    }

    /**
     * Initializes the sound effects
     *
     * By Keegan
     */
    void initializeSoundEffects(){

        pigSounds = new MediaPlayer[3];
        fenceSounds = new MediaPlayer[5];

        //pig sounds
        for (int i = 0 ; i < 3 ; i++){

            int resID = getResources().getIdentifier("pig" + (i + 1), "raw", main.getPackageName());
            pigSounds[i] = MediaPlayer.create(main, resID);

        }//for

        //fence sounds
        for (int i = 0 ; i < 5 ; i++){

            int resID = getResources().getIdentifier("wood" + (i + 1), "raw", main.getPackageName());
            fenceSounds[i] = MediaPlayer.create(main, resID);

        }//for

    }//initializeSoundEffects

    /**
     * Plays a random pig sound
     *
     * By Keegan
     */
    void playPigSound(){

        Settings.retrieve(main);

        int randNum;
        Random rand = new Random();

        if (Settings.getSfxOn()){

            randNum = rand.nextInt(pigSounds.length - 1);

            pigSounds[randNum].start();

        }//if

    }//playPigSound

    /**
     * Plays a random fence placement sound
     *
     * By Keegan
     */
    void playFenceSound(){

        Settings.retrieve(main);

        int randNum;
        Random rand = new Random();

        if (Settings.getSfxOn()){

            randNum = rand.nextInt(fenceSounds.length - 1);

            fenceSounds[randNum].start();

        }//if

    }//playPigSound

    /**
     * Sets the score buttons, used in loading.
     * @param p1ScoreButton player one's score button.
     * @param p2ScoreButton player two's score button.
     */
    public void setScoreButtons(Button p1ScoreButton, Button p2ScoreButton) {
        this.p1ScoreButton = p1ScoreButton;
        this.p2ScoreButton = p2ScoreButton;
    }
}//GridFragment
