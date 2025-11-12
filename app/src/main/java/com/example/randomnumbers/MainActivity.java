package com.example.randomnumbers;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private TextView n1, n2, n3, n4, n5, n6, randomTextView, pointsTextView;
    private Button startStopButton, newGameButton;


    private final Random rand = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isSpinning = false;
    private Runnable spinningRunnable;


    private final ArrayList<TextView> numberFields = new ArrayList<>();

    private final Set<TextView> matchedFields = new HashSet<>();
    private int spinCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        n1 = findViewById(R.id.num1);
        n2 = findViewById(R.id.num2);
        n3 = findViewById(R.id.num3);
        n4 = findViewById(R.id.num4);
        n5 = findViewById(R.id.num5);
        n6 = findViewById(R.id.num6);
        randomTextView = findViewById(R.id.randNum);
        pointsTextView = findViewById(R.id.points);
        newGameButton = findViewById(R.id.newgame);
        startStopButton = findViewById(R.id.startstop);


        numberFields.add(n1);
        numberFields.add(n2);
        numberFields.add(n3);
        numberFields.add(n4);
        numberFields.add(n5);
        numberFields.add(n6);


        startStopButton.setOnClickListener(v -> {
            if (isSpinning) {
                stopSpinning();
            } else {
                startSpinning();
            }
        });

        newGameButton.setOnClickListener(v -> setupNewGame());


        setupNewGame();
    }


    private void setupNewGame() {

        Set<Integer> uniqueNumbers = new HashSet<>();
        while (uniqueNumbers.size() < 6) {
            uniqueNumbers.add(rand.nextInt(40) + 1);
        }


        Integer[] numbers = uniqueNumbers.toArray(new Integer[0]);


        for (int i = 0; i < numberFields.size(); i++) {
            TextView field = numberFields.get(i);
            field.setText(String.valueOf(numbers[i]));
            field.setBackgroundColor(Color.TRANSPARENT);
        }


        matchedFields.clear();
        spinCount = 0;
        randomTextView.setText("0");
        updatePointsText();
        // Ensure any previously running processes are stopped cleanly
        if(isSpinning) {
            // This will also reset the button color to green
            stopSpinning();
        } else {
            // Manually set button color if not spinning
            startStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green color
        }
        startStopButton.setEnabled(true);
        Toast.makeText(this, "New Game Started!", Toast.LENGTH_SHORT).show();
    }


    private void startSpinning() {
        if (matchedFields.size() == 6 || spinCount >= 6) {
            Toast.makeText(this, "Game Over! Press New Game to play again.", Toast.LENGTH_SHORT).show();
            return;
        }

        isSpinning = true;
        startStopButton.setText("STOP");
        startStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

        spinningRunnable = () -> {
            randomTextView.setText(String.valueOf(rand.nextInt(40) + 1));
            handler.postDelayed(spinningRunnable, 100); // Loop every 100ms
        };
        handler.post(spinningRunnable);
    }


    private void stopSpinning() {

        if(!isSpinning) return;

        isSpinning = false;
        handler.removeCallbacks(spinningRunnable);
        startStopButton.setText("START");
        // MODIFIED: Set button color to green when it becomes "START"
        startStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green color


        String spunNumber = randomTextView.getText().toString();
        boolean matchFoundThisTurn = false;

        for (TextView field : numberFields) {

            if (field.getText().toString().equals(spunNumber) && !matchedFields.contains(field)) {
                field.setBackgroundColor(Color.RED);
                matchedFields.add(field);
                updatePointsText();
                matchFoundThisTurn = true;
                break;
            }
        }

        if (matchFoundThisTurn) {
            Toast.makeText(this, "MATCH! You found " + spunNumber, Toast.LENGTH_SHORT).show();
        }

        spinCount++;

        // --- Check for Game Over Conditions ---
        if (matchedFields.size() == 6) {
            pointsTextView.setText("YOU WIN! Press NEW GAME to play again.");
            startStopButton.setEnabled(false);
        } else if (spinCount >= 6) { // MODIFIED: Check if max spins reached
            pointsTextView.setText("GAME OVER! You ran out of spins.");
            startStopButton.setEnabled(false); // Disable the button temporarily

            // Post a delayed task to start a new game automatically after 2 seconds
            handler.postDelayed(() -> {
                Toast.makeText(this, "Starting a new round...", Toast.LENGTH_SHORT).show();
                setupNewGame();
            }, 2000); // 2000 milliseconds = 2 seconds
        }
    }


    private void updatePointsText() {
        pointsTextView.setText(matchedFields.size() + " out of 6");
    }


}
