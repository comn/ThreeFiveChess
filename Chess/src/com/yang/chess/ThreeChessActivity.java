package com.yang.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ThreeChessActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_three_chess);
	}
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.fight_computer:
			startActivity(new Intent(this,ComputerGameActivity.class));
			break;
		case R.id.fight_myself:
			startActivity(new Intent(this,FightGameActivity.class));
			break;
		case R.id.fight_bluetooth:
			startActivity(new Intent(this,BluetoothGameActivity.class));
			break;

		default:
			break;
		}
	}
}
