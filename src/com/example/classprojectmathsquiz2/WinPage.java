// This page tell the user how well he did. If his score beats the high score he is asked to enter his name for the record.

package com.example.classprojectmathsquiz2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class WinPage extends Activity {

	int score = GamePage.score * WeclomePage.level * WeclomePage.level;
	public final static String HIGHSCORE = "highScores.txt"; 
	public final static String HIGHSCORENAME = "highScoreName.txt"; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_win_page);

		final MediaPlayer cheering = MediaPlayer.create(this,
				com.example.classprojectmathsquiz2.R.raw.cheering);
		cheering.start(); 

		TextView currentScoreView = (TextView) findViewById(R.id.currentScore);
		final TextView highScoreView = (TextView) findViewById(R.id.highScore);

		currentScoreView.setText("Well done, you answered "
				+ Integer.toString(GamePage.score)
				+ ((GamePage.score == 1) ? " question" : " questions")
				+ " correctly!"
				+ " Multiplied by level bonus (level squared) = "
				+ score
				+ ((score == 1) ? " point" : " points!"));

		if (score > getHighScore()) {
// Dialog to get user's name if his score is the new high score
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("New high score!");
			alert.setMessage("What is your name, O Great One?");

			final EditText input = new EditText(this); 
			alert.setView(input);

			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String name = input.getText().toString();
// To save this new high score and the user's name
							try {
								FileOutputStream fosScore = openFileOutput(
										HIGHSCORE, MODE_PRIVATE);
								fosScore.write((Integer.toString(score)
										.getBytes()));
								fosScore.close();
								FileOutputStream fosName = openFileOutput(
										HIGHSCORENAME, MODE_PRIVATE);
								fosName.write(name.getBytes());
								fosName.close();
								highScoreView.setText("High Score: "
										+ getHighScore() + " ("
										+ getHighScoreName() + ")");

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			alert.show();
		}
		highScoreView.setText("High Score: " + getHighScore() + " ("
				+ getHighScoreName() + ")"); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.win_page, menu);
		return true;
	}
// To retrieve the previous high score
	public int getHighScore() { 
		try {
			FileInputStream fisScore = openFileInput(HIGHSCORE);
			BufferedReader scoreReader = new BufferedReader(
					new InputStreamReader(new DataInputStream(fisScore)));
			String highScore = scoreReader.readLine();
			while (highScore != null) {
				fisScore.close();
				return Integer.parseInt(highScore);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		;
		return 0;
	}
// To retrieve the previous high scorer's name
	private String getHighScoreName() { 
		try {
			FileInputStream fisName = openFileInput(HIGHSCORENAME);
			BufferedReader nameReader = new BufferedReader(
					new InputStreamReader(new DataInputStream(fisName)));
			String name = nameReader.readLine();

			while (name != null) {
				fisName.close();
				return name;
			}
		} catch (Exception e) {
			e.printStackTrace();
		};
		return "";
	}
// Method called via xml to return to the start page
	public void tryAgain(View view) {
		Intent intent = new Intent(this, WeclomePage.class);
		GamePage.score = 0;
		startActivity(intent); 
	}

	@Override
	protected void onStop() {
		super.onStop(); 
	}
}