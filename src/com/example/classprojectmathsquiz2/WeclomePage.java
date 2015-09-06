/* In Maths Quiz the player has one minute to answer arithmetic questions. He earns points based on the number of correct
 * answers and the difficulty level he choose. Occasionally more difficult three-operand questions are asked, which may
 * be passed at no penalty. The high score is saved. */

// This page tells the user how to play and asks him which difficulty level he would like

package com.example.classprojectmathsquiz2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class WeclomePage extends Activity {
// The three difficulty levels
	public static int level; 
	final String levels[] = new String[] { "Quite easy", "Very medium",
			"Too hard" }; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weclome_page);
// Play "Hello" to great user
		final MediaPlayer hello = MediaPlayer.create(this,
				com.example.classprojectmathsquiz2.R.raw.hello); 
		hello.start();
// Dialog window to let user choose a difficulty level. Once chosen, the game page activity is started.
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Please choose a level");
		builder.setItems(levels, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int choice) {
				level = choice + 1;
				startActivity(new Intent(WeclomePage.this, GamePage.class));
			}
		});
// This button opens the dialog in which players choose their level
		final Button startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				builder.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.weclome_page, menu);
		return true;
	}
// To release resources when the page is closed
	@Override
	protected void onStop() {
		super.onStop();
	}
}
