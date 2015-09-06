// This page presents the user with a series of randomly generated arithmetic questions for one minute. Then the win page is opened.

package com.example.classprojectmathsquiz2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GamePage extends Activity {

	static int score = 0;
	String[] questionArray;
	MediaPlayer music;
	CountDownTimer tick;
	int numberOfQuestionsAsked = 0;
	static Random random;
	boolean isBonusQuestion = false;
	Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_page);
		// Declare random generator up here, rather than in methods. As methods
		// can be called very close
		// to each other and seeds are based on time, declaring it within them
		// can result in identical "random" numbers, as they
		// have been created from the same seed
		random = new Random();

		final Button submitButton = (Button) findViewById(R.id.submitButton);

		final TextView scoreView = (TextView) findViewById(R.id.currentScoreView);
		// The player's score is a product of the number of questions he has
		// answered correctly and the square of the difficulty he chose.
		scoreView.setText("Score: " + score * WeclomePage.level
				* WeclomePage.level);
		// The high score is displayed beside the player's current score
		final TextView highScoreView = (TextView) findViewById(R.id.highScoreView);
		highScoreView.setText("High score: " + getHighScore());
		final TextView questionText = (TextView) findViewById(R.id.questionText);
		// Chalkboard font for questions and answers
		Typeface typeface1 = Typeface.createFromAsset(getAssets(),
				"fonts/TenThousandReasons.ttf");
		// Digital font for countdown timer
		Typeface typeface2 = Typeface.createFromAsset(getAssets(),
				"fonts/digital.ttf");
		questionText.setTypeface(typeface1);
		final EditText answerField = (EditText) findViewById(R.id.answerField);
		answerField.setTypeface(typeface1);
		final TextView timerView = (TextView) findViewById(R.id.timerView);
		timerView.setTypeface(typeface2);
		// Flight of the Bumblebee
		music = MediaPlayer.create(this,
				com.example.classprojectmathsquiz2.R.raw.gamepagemusic);
		// Played when a question is answered incorrectly
		final MediaPlayer no = MediaPlayer.create(this,
				com.example.classprojectmathsquiz2.R.raw.no);
		// Played when a question is answered correctly
		final MediaPlayer yes = MediaPlayer.create(this,
				com.example.classprojectmathsquiz2.R.raw.yes);

		music.start();
		// This starts the activity with the keyboard visible
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		// Create the first question and display it to the user
		questionArray = setQuestion();
		questionText.setText(questionArray[0] + " " + questionArray[1] + " "
				+ questionArray[2] + " = ");

		passButtonListener();
		// Because toasts use a queue, if many are generated in a short space of
		// time, the can continue to appear long after they are
		// relevant. To fix this, create one toast here, and change its text and
		// make it visible when needed.
		final Context context = getApplicationContext();
		toast = Toast.makeText(context, " ", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 75);
		// The game lasts one minute, at the end of which the win page is
		// launched.
		tick = new CountDownTimer(60000, 1000) {
			public void onTick(long millisUntilFinished) {
				timerView.setText(Integer
						.toString((int) (millisUntilFinished / 1000)));
			}

			public void onFinish() {
				startActivity(new Intent(GamePage.this, WinPage.class));
			}
		}.start();
		// The sumbit button listener
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String sAnswerField = answerField.getText().toString();
				// make sure the answer field is not empty
				if (!sAnswerField.matches("")) {

					int guess = Integer.parseInt(answerField.getText()
							.toString());
					// if the player's answer is correct, play "yes"
					// the last element in the question array is the correct
					// answer
					if (guess == Integer
							.parseInt(questionArray[questionArray.length - 1])) {
						if (yes.isPlaying()) {
							yes.pause();
						}
						yes.start();
						// Bonus questions grant more points than normal ones
						if (isBonusQuestion) {
							score += 5;
						} else {
							score++;
						}
						// Display new score
						scoreView.setText("Score: " + score * WeclomePage.level
								* WeclomePage.level);

						toast.setText("Well done!");
						toast.show();
						// One in five questions are harder, bonus questions of
						// three operands.
						int rnd = random.nextInt(5);
						if (rnd == 0) {
							toast.setText("Bonus question - extra points!");
							toast.show();
							// The pass button is only visible for bonus
							// questions
							final Button passButton = (Button) findViewById(R.id.passButton);
							passButton.setVisibility(View.VISIBLE);
							// Generate and display a hard question
							questionArray = setLongQuestion();
							questionText.setText(questionArray[0] + " "
									+ questionArray[1] + " " + questionArray[2]
									+ " " + questionArray[3] + " "
									+ questionArray[4] + " = ");
							isBonusQuestion = true;

						}
						// generate and display a normal question
						else {
							final Button passButton = (Button) findViewById(R.id.passButton);
							passButton.setVisibility(View.GONE);
							questionArray = setQuestion();
							questionText.setText(questionArray[0] + " "
									+ questionArray[1] + " " + questionArray[2]
									+ " = ");
							isBonusQuestion = false;
						}
						// If the answer is incorrect, let the user no and ask
						// him to try again. Do not generate a new question.
					} else {
						if (no.isPlaying()) {
							no.pause();
						}
						no.start();
						toast.setText("Sorry, wrong answer - try again...");
						toast.show();
					}
				}
				answerField.setText("");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.game_page, menu);
		return true;
	}

	// Method for creating operands based on difficulty level
	public static int getRandom(int level) {

		int num;
		if (level == 1) {
			num = random.nextInt(8) + 1;
		} else if (level == 2) {
			num = random.nextInt(88) + 11;
		} else {
			num = random.nextInt(898) + 101;
		}
		return num;
	}

	// Method for creating a question, which is stored in an array: {first
	// operand, operator, second operand, answer}
	public static String[] setQuestion() {
		String[] operators = { "+", "-", "x", "/" };
		int answer;
		int num1;
		int num2;
		String operator;
		do {
			operator = operators[random.nextInt(4)];
			int first = getRandom(WeclomePage.level);
			int second = getRandom(WeclomePage.level);
			// This is one way to make sure subtraction questions can't give
			// negative answers. I do it another, probably better way,
			// in the setLongQuestion method below.
			num1 = Math.max(first, second);
			num2 = Math.min(first, second);

			if (operator == "+") {
				answer = num1 + num2;
			} else if (operator == "-") {
				answer = num1 - num2;
			} else if (operator == "x") {
				// Multiplications questions would be too hard if both operands
				// were of multiple digits
				num2 = random.nextInt(7) + 2;
				answer = num1 * num2;
			} else {
				// In division questions, the first operand is a multiple of the
				// second, so that there can't be a remainder
				num2 = random.nextInt(10) + 2;
				if (WeclomePage.level == 1)
					num1 = num2 * (random.nextInt(8) + 2);
				else if (WeclomePage.level == 2)
					num1 = num2 * (random.nextInt(88) + 11);
				else {
					num2 = random.nextInt(49) + 2;
					num1 = num2 * (random.nextInt(18) + 4);
				}
				answer = num1 / num2;
			}
			// I don't allow answers of zero, as they are too easy.
		} while (answer == 0);

		String[] question = { Integer.toString(num1), operator,
				Integer.toString(num2), Integer.toString(answer) };
		return question;
	}

	// Method to set hard, bonus question of three operands, store in an array:
	// {first operand, first operator, second operand, second operator, third
	// operand, answer}
	private static String[] setLongQuestion() {

		String[] operators = { "+", "-", "x", "/" };

		int answer;
		int num1;
		int num2;
		int num3;
		String operator1;
		String operator2;

		do {
			operator1 = operators[random.nextInt(operators.length - 1)];
			operator2 = operators[random.nextInt(operators.length - 1)];
			num1 = getRandom(WeclomePage.level);
			num2 = getRandom(WeclomePage.level);
			num3 = getRandom(WeclomePage.level);
// Not every combination of operator is described - some would be too difficult for the user.
			if (operator1 == "+" && operator2 == "-") {
				answer = num1 + num2 - num3;
			} else if (operator1 == "+" && operator2 == "+") {
				answer = num1 + num2 + num3;
			} else if (operator1 == "-" && operator2 == "-") {
				answer = num1 - num2 - num3;
			} else if (operator1 == "-" && operator2 == "+") {
				answer = num1 - num2 + num3;
			} else if (operator1 == "x" && operator2 == "+") {
				num2 = random.nextInt(7) + 2;
				answer = num1 * num2 + num3;
			} else if (operator1 == "+" && operator2 == "x") {
				num3 = random.nextInt(7) + 2;
				answer = num1 + num2 * num3;
			} else if (operator1 == "x" && operator2 == "-") {
				num2 = random.nextInt(7) + 2;
				answer = num1 * num2 - num3;
			} else {
				num3 = random.nextInt(7) + 2;
				answer = num1 - num2 * num3;
			}
			// This prevents answers of zero or less. Simpler than the Math.max
			// method used above
		} while (answer < 0);

		String[] question = { Integer.toString(num1), operator1,
				Integer.toString(num2), operator2, Integer.toString(num3),
				Integer.toString(answer) };
		return question;
	}

	// Method to release resources and stop music and timer when page is closed
	@Override
	protected void onStop() {
		super.onStop();
		if (music.isPlaying()) {
			music.reset();
			music.release();
			music = null;
		}
		tick.cancel();
	}

	// I tried calling this method from the Win Page but it didn't work, so I
	// wrote it out here too.
	private int getHighScore() {

		try {
			FileInputStream fisScore = openFileInput(WinPage.HIGHSCORE);
			BufferedReader scoreReader = new BufferedReader(
					new InputStreamReader(new DataInputStream(fisScore)));
			String highScore = scoreReader.readLine();

			while (highScore != null) {
				return Integer.parseInt(highScore);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		;
		return 0;
	}

	// Pass button to skip bonus questions.
	private void passButtonListener() {
		final Button passButton = (Button) findViewById(R.id.passButton);
		passButton.setVisibility(View.GONE);
		passButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isBonusQuestion = false;
				passButton.setVisibility(View.GONE);
				questionArray = setQuestion();
				TextView questionText = (TextView) findViewById(R.id.questionText);
				questionText.setText(questionArray[0] + " " + questionArray[1]
						+ " " + questionArray[2] + " = ");
			}
		});
	}
}