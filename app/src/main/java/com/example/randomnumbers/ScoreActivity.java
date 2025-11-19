package com.example.randomnumbers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    private TextView nameView, winsView, playedView;
    private Button backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score); // Matches the XML below

        nameView = findViewById(R.id.textViewName);
        winsView = findViewById(R.id.textViewWins);
        playedView = findViewById(R.id.textViewPlayed);
        backButton = findViewById(R.id.buttonBack);

        // Set Name
        if (getIntent() != null && getIntent().hasExtra("NAME")) {
            String name = getIntent().getStringExtra("NAME");
            nameView.setText(name.isEmpty() ? "Player" : name);
        }

        // Set Scores
        winsView.setText("Total Full Wins: " + MainActivity.totalWins);
        playedView.setText("Games Reset: " + MainActivity.gamesPlayed);

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
