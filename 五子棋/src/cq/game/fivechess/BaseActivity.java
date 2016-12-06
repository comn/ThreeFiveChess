package cq.game.fivechess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cq.game.fivechess.game.Game;
import cq.game.fivechess.game.GameConstants;
import cq.game.fivechess.game.Player;
import cq.game.fivechess.game.To3Game;
import cq.game.fivechess.game.To3GameView;

public abstract class BaseActivity extends Activity implements OnClickListener {

	To3GameView mGameView = null;
	private TextView mBlackWin;
	private TextView mWhiteWin;
	private TextView mBChesses;
	private TextView mWChesses;
	private TextView mBlackName;
	private TextView mWhiteName;

	private ImageView mBlackActive;
	private ImageView mWhiteActive;

	private TextView tv_loading;
	private LinearLayout progress_bar;

	// Control Button
	private Button restart;
	private Button rollback;
	private Button about;
	private Button setting;
	
	To3Game mGame;
	Player black;
	Player white;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_fight);
		initViews();
	}
	
	public void initGame(Handler mRefreshHandler,int mode) {
		black = new Player(Game.BLACK, GameConstants.TO3_CHESS_COUNT);
		white = new Player(Game.WHITE, GameConstants.TO3_CHESS_COUNT);
		mGame = new To3Game(mRefreshHandler , black, white);
		mGame.setMode(mode);
		mGameView.setGame(mGame);
		updateActive(mGame);
		updateScore(black, white);
		updatePlayerChess(black, white);
	}

	private void initViews() {
		mGameView = (To3GameView) findViewById(R.id.game_view);
		mBlackWin = (TextView) findViewById(R.id.black_win);
		mBlackActive = (ImageView) findViewById(R.id.black_active);
		mWhiteWin = (TextView) findViewById(R.id.white_win);
		mWhiteActive = (ImageView) findViewById(R.id.white_active);
		mBChesses = (TextView) findViewById(R.id.black_count);
		mWChesses = (TextView) findViewById(R.id.white_count);
		progress_bar = (LinearLayout) findViewById(R.id.ll_progress_bar);
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		mBlackName =(TextView) findViewById(R.id.black_name);
		mWhiteName =(TextView) findViewById(R.id.white_name);
		
		restart = (Button) findViewById(R.id.restart);
		rollback = (Button) findViewById(R.id.rollback);
		about = (Button) findViewById(R.id.about);
		setting = (Button) findViewById(R.id.setting);
		restart.setOnClickListener(this);
		rollback.setOnClickListener(this);
		about.setOnClickListener(this);
		setting.setOnClickListener(this);
	}
	
	public void updateActive(To3Game game) {
		if (game.getActive() == Game.BLACK) {
			mBlackActive.setVisibility(View.VISIBLE);
			mWhiteActive.setVisibility(View.INVISIBLE);
		} else {
			mBlackActive.setVisibility(View.INVISIBLE);
			mWhiteActive.setVisibility(View.VISIBLE);
		}
	}
	
	public void updateScore(Player black, Player white) {
		mBlackWin.setText(black.getWin());
		mWhiteWin.setText(white.getWin());
	}
	
	// 更新双方棋子数
	public void updatePlayerChess(Player black, Player white) {
		mBChesses.setText(String.valueOf(black.getmChesses()));
		mWChesses.setText(String.valueOf(white.getmChesses()));
	}
	
	public void showWinDialog(String message) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setCancelable(false);
		b.setMessage(message);
		b.setPositiveButton(R.string.Continue,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						restart();
					}
				});
		b.setNegativeButton(R.string.exit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		b.show();
	}
	
	private void restart() {
		mGame.reset();
		updateActive(mGame);
		updateScore(black, white);

		black.setmChesses(GameConstants.TO3_CHESS_COUNT);
		white.setmChesses(GameConstants.TO3_CHESS_COUNT);
		updatePlayerChess(black, white);
		mGameView.drawGame();
	}
	
	public void rollback() {
		
	}

	@Override
	public void onClick(View v) {
	}
	
	
}
