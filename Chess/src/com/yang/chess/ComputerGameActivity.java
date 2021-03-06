package com.yang.chess;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.yang.chess.game.Coordinate;
import com.yang.chess.game.Game;
import com.yang.chess.game.GameConstants;
import com.yang.chess.game.To3ComputerAI;
import com.yang.chess.game.To3Game;
/**
 * 
 * @author Flsolate
 * @date 2016-12-5
 * @description  同电脑对战的模式
 */
public class ComputerGameActivity extends BaseActivity {

	public static final String TAG = ComputerGameActivity.class.getName();
	Handler mRefreshHandler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GameConstants.GAME_OVER:
				if (msg.arg1 == Game.BLACK) {
					showWinDialog("黑方胜");
					black.win();
				} else if (msg.arg1 == Game.WHITE) {
					showWinDialog("白方胜");
					white.win();
				}
				updateScore(black, white);
				break;
				
			case GameConstants.ADD_CHESS:
				Coordinate c =(Coordinate) msg.obj;
				if (c.type == Game.BLACK) {
					black.downChess();
					computerHandler();
				}
				updateGameInfo();
				break;
				
			case GameConstants.THREE_CHESS:
				if (msg.arg1 == Game.BLACK) {  
					Toast.makeText(ComputerGameActivity.this, "黑方成三吃子", 1).show();
					mGameView.eatChess(Game.WHITE);
				} 
				//电脑成三吃子
				 else if (msg.arg1 == Game.WHITE) {
						mComputerHandler.sendEmptyMessage(GameConstants.COMPUTER_EAT_CHESS);
				}
				break;
				
			case GameConstants.CHANGE_ACTIVE:
				updateActive(mGame);
				computerHandler();
				break;
				
			case GameConstants.CHALLENGER_ADD:
				white.downChess();
				//同步更新游戏进行中的信息
				updateGameInfo();
				break;
			default:
				break;
			}
		}

		// 此时玩家已下子，促使电脑反应处理
		private void computerHandler() {
			if (mGame.getActive() == white.getType()) {
				//动子阶段来了
				if (mGame.getActions().size()>=GameConstants.TOTAL_CHESS){
//				1、设置白子不可被玩家移动
					mComputerHandler.sendEmptyMessage(GameConstants.COMPUTER_CHESS_MOVE);
					return;
				}
				mComputerHandler.sendEmptyMessage(GameConstants.COMPUTER_DOWN_CHESS);
			}
		}
	};
	private ComputerHandler mComputerHandler;
	public To3ComputerAI ai;
	public boolean isRollback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGame(mRefreshHandler,GameConstants.MODE_SINGLE);
		initComputer();
		ai = new To3ComputerAI(mGameView.gamePoints);
	}

	private void initComputer() {
		HandlerThread thread = new HandlerThread("computerAi");
		thread.start();
		mComputerHandler = new ComputerHandler(thread.getLooper());
	}

	class ComputerHandler extends Handler {

		public ComputerHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			Coordinate c = null;
			switch (msg.what) {
			case GameConstants.COMPUTER_DOWN_CHESS:
				ai.updateValue(mGame);
				if (mGame.isFirstChess()) {
					c = new Coordinate(7, 3);
				} else {
					c = ai.getPosition(mGame.getChessMap());
				}
				mGame.addChess(c, white);
				mGameView.drawGame();
				break;
			case GameConstants.COMPUTER_EAT_CHESS:
				 c=ai.eatChess(mGame.getChessMap());
				boolean isComputerEat = mGame.eatChess(c);
				if (isComputerEat) {
					mGameView.drawGame();
					mGame.sendChangeActive();
				}
				break;
			case GameConstants.COMPUTER_CHESS_MOVE:
				Coordinate start =ai.moveStart(mGame);
				Coordinate end =ai.moveEnd(mGame.getChessMap());
				if (start==null && end==null) {
					mGame.sendGameResult(To3Game.BLACK);//黑方胜
					break;
				}
//				Toast.makeText(ComputerGameActivity.this, "白子从("+start.x+","+start.y+")移动到 ："
//						+"("+end.x+","+end.y+")", 0).show();
				Log.d(TAG,"白子从("+start.x+","+start.y+")移动到 ："
						+"("+end.x+","+end.y+")");
		
				mGameView.chessMove(start, end,false);
				if(!ai.isBlackHasWay()){
					mGame.sendGameResult(To3Game.WHITE);
				}
				break;
			default:
				break;
			}
			
			// 控制电脑悔棋
			if (isRollback) {
				rollback();
				isRollback = false;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		mComputerHandler.getLooper().quit();
		super.onDestroy();
	}

	public void updateGameInfo() {
		updatePlayerChess(black, white);  
		updateActive(mGame);    
	}
	
	@Override
	public void restart() {
		mGame.reset();
		updateActive(mGame);
		updateScore(black, white);

		black.setmChesses(GameConstants.TO3_CHESS_COUNT);
		white.setmChesses(GameConstants.TO3_CHESS_COUNT);
		updatePlayerChess(black, white);
		mGameView.drawGame();
	}
}
