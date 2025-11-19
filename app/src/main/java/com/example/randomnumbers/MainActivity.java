package com.example.randomnumbers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private TextView[] slots = new TextView[6]; // The 6 top numbers
    private TextView largeNumberDisplay, winCounterText;
    private EditText nameInput;
    private Button startButton, newGameButton, scoreButton;

    // Game State
    private Set<Integer> foundNumbers = new HashSet<>(); // Stores numbers we have already matched
    private boolean isRolling = false;
    private Handler handler = new Handler();
    private Random random = new Random();
    private String playerName = "";

    // Static variables for ScoreActivity
    public static int totalWins = 0;
    public static int gamesPlayed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // WE USE THE LAYOUT YOU PROVIDED HERE (assumed to be named activity_main for logic)
        setContentView(R.layout.activity_main);

        // Bind Views
        slots[0] = findViewById(R.id.aID);
        slots[1] = findViewById(R.id.bID);
        slots[2] = findViewById(R.id.cID);
        slots[3] = findViewById(R.id.dID);
        slots[4] = findViewById(R.id.eID);
        slots[5] = findViewById(R.id.fID);

        largeNumberDisplay = findViewById(R.id.numID);
        winCounterText = findViewById(R.id.countWinID);
        nameInput = findViewById(R.id.name);
        startButton = findViewById(R.id.button);
        newGameButton = findViewById(R.id.newGameID);
        scoreButton = findViewById(R.id.ScoreID);

        // Initialize a new game board
        resetGame();

        // 1. START BUTTON (Rolls a number)
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameInput.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter Name First", Toast.LENGTH_SHORT).show();
                    return;
                }
                playerName = nameInput.getText().toString();

                if (foundNumbers.size() < 6) {
                    rollNumber();
                } else {
                    Toast.makeText(MainActivity.this, "Game Over! Click New Game.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2. NEW GAME BUTTON
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gamesPlayed++; // Count previous game as played
                resetGame();
            }
        });

        // 3. SCORE BUTTON
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("NAME", playerName);
                startActivity(intent);
            }
        });
    }

    // Setup 6 random target numbers
    private void resetGame() {
        foundNumbers.clear();
        winCounterText.setText("0 of 6");
        largeNumberDisplay.setText("?");

        // Generate 6 unique random numbers (1-30) for the top slots
        Set<Integer> targets = new HashSet<>();
        while (targets.size() < 6) {
            targets.add(random.nextInt(30) + 1);
        }

        int i = 0;
        for (Integer num : targets) {
            slots[i].setText(String.valueOf(num));
            slots[i].setTextColor(getResources().getColor(android.R.color.black)); // Reset color
            slots[i].setTag(num); // Store the number in the view tag for easy checking
            i++;
        }
    }

    // Simulates rolling animation
    private void rollNumber() {
        startButton.setEnabled(false);
        newGameButton.setEnabled(false);
        isRolling = true;

        // Run a fast loop to change numbers quickly
        final int[] rolls = {0};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentRoll = random.nextInt(30) + 1;
                largeNumberDisplay.setText(String.valueOf(currentRoll));

                if (rolls[0] < 20) { // Roll 20 times quickly
                    rolls[0]++;
                    handler.postDelayed(this, 50);
                } else {
                    // STOP ROLLING -> CHECK MATCH
                    checkMatch(currentRoll);
                    startButton.setEnabled(true);
                    newGameButton.setEnabled(true);
                }
            }
        };
        handler.post(runnable);
    }

    private void checkMatch(int rolledNumber) {
        boolean matchFound = false;

        for (TextView slot : slots) {
            int target = (int) slot.getTag();
            // If matches and hasn't been found yet
            if (target == rolledNumber && !foundNumbers.contains(target)) {
                foundNumbers.add(target);
                slot.setTextColor(getResources().getColor(android.R.color.holo_green_dark)); // Turn green
                matchFound = true;
                break;
            }
        }

        winCounterText.setText(foundNumbers.size() + " of 6");

        if (matchFound) {
            Toast.makeText(this, "MATCH FOUND!", Toast.LENGTH_SHORT).show();
        }

        if (foundNumbers.size() == 6) {
            totalWins++;
            largeNumberDisplay.setText("WIN!");
            Toast.makeText(this, "YOU WON THE GAME!", Toast.LENGTH_LONG).show();
        }
    }
}
