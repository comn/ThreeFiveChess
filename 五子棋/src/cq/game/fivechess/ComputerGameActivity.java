package cq.game.fivechess;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cq.game.fivechess.game.ComputerAI;
import cq.game.fivechess.game.Coordinate;
import cq.game.fivechess.game.Game;
import cq.game.fivechess.game.GameConstants;
import cq.game.fivechess.game.To3ComputerAI;
/**
 * 
 * @author Flsolate
 * @date 2016-12-5
 * @description  同电脑对战的模式
 */
public class ComputerGameActivity extends BaseActivity {

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
				break;
			case GameConstants.CHANGE_ACTIVE:// 换手
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

		// 此时玩家已下子，促使电脑下子
		private void computerHandler() {
			if (mGame.getActive() == white.getType()) {
				mComputerHandler.sendEmptyMessage(0);
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
			ai.updateValue(mGame);
			Coordinate c = null;
			// 判断是否为第一子。
			if (mGame.isFirstChess()) {
				c = new Coordinate(7, 3);// 3,7;7,11;11,7
//				此处为成三棋中电脑第一子的落点
			} else {
//				ComputerAI算法存在问题
				c = ai.getPosition(mGame.getChessMap());
			}
			mGame.addChess(c, white);
			mGameView.drawGame();
			// 控制电脑手时的玩家悔棋。
			if (isRollback) {
				rollback();
				isRollback = false;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// 停止持续处理消息。
		mComputerHandler.getLooper().quit();
		super.onDestroy();
	}

	public void updateGameInfo() {
		updatePlayerChess(black, white);  
		updateActive(mGame);    
	}
}
