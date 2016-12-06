package cq.game.fivechess;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cq.game.fivechess.bluetooth.BluetoothChatService;
import cq.game.fivechess.bluetooth.BluetoothConstants;
import cq.game.fivechess.game.Coordinate;
import cq.game.fivechess.game.Game;
import cq.game.fivechess.game.GameConstants;
import cq.game.fivechess.game.Player;
import cq.game.fivechess.game.To3ComputerAI;
import cq.game.fivechess.game.To3Game;
import cq.game.fivechess.game.To3GameView;

public class FightGameActivity extends Activity implements OnClickListener {

	private static final String TAG = "FightGameActivity";

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 2;

	protected static final String DEVICE_NAME = "device_name";

	To3GameView mGameView = null;

	To3Game mGame;
	Player black;
	Player white;

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

	private Handler mRefreshHandler = new Handler() {

		public void handleMessage(Message msg) {
			Log.d(TAG, "refresh action=" + msg.what);
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
				//棋盘中添加棋子后在这里更新UI变化
			case GameConstants.ADD_CHESS:
				Coordinate c =(Coordinate) msg.obj;
				if (c.type == Game.BLACK) {
					black.downChess();
				} else {
					white.downChess();
				}
				updatePlayerChess(black, white);  
				updateActive(mGame);    
				
				//AI分析
				to3ComputerAI.updateValue(mGame);
				break;
			case GameConstants.THREE_CHESS: 
				if (msg.arg1 == Game.BLACK) {  
					Toast.makeText(FightGameActivity.this, "黑方成三吃子", 1).show();
					mGameView.eatChess(Game.WHITE);
				} else if (msg.arg1 == Game.WHITE) {
					Toast.makeText(FightGameActivity.this, "白方成三吃子", 1).show();
					mGameView.eatChess(Game.BLACK);
				}
				break;
			case GameConstants.CHANGE_ACTIVE:// 换手
				Log.d(TAG, mGame.getActive() + "");
				updateActive(mGame);
				break;
			case GameConstants.THREE_ROLLBACK:
				String string = (String) msg.obj;
				if (string.equals("rollback eatChess")) {
					mGameView.setEatChessCallBack(null);
				}
				break;
				
				//蓝牙模块
			case GameConstants.BLUETOOTH_ADD_CHESS:
				Coordinate co =(Coordinate) msg.obj;
				me.downChess();
				updatePlayerChess(me, challenger);  
				updateActive(mGame);    
				//发送给联机玩家
				sendChess(co.x, co.y,BluetoothConstants.CHALLENGER_ADD_CHESS);
				break;
//             将自己成三吃的对方子点位传给对方
			case GameConstants.EAT_CHESS:
				Coordinate eat =(Coordinate) msg.obj;
				sendChess(eat.x, eat.y,BluetoothConstants.CHALLENGER_EAT_CHESS);
				break;
			default:
				break;
			}
		};
	};

	private Handler mRequestHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothConstants.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus("已联机");
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus("正在联机..");
					break;
				case BluetoothChatService.STATE_LISTEN: //开始状态
				case BluetoothChatService.STATE_NONE: //停止状态
					setStatus("未联机");
					break;
				}
				break;
			case BluetoothConstants.CHALLENGER_ADD_CHESS:
				byte[] receive = (byte[]) msg.obj;
                mGame.addChess(receive[2], receive[3], challenger);
                mGameView.drawGame();
                challenger.downChess();
                updatePlayerChess(me, challenger);
                updateActive(mGame);
                break;
			case BluetoothConstants.MESSAGE_IMFOR:
				// save the connected device's name
				String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				isServer = msg.getData().getBoolean("isServer");
				
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case BluetoothConstants.CHALLENGER_EAT_CHESS:
				byte[] e = (byte[]) msg.obj;
				Coordinate coordinate = new Coordinate(e[2], e[3],challenger.getType());
				boolean clearChess = mGame.clearChess(coordinate);
				if (clearChess) {
					//换手
					mGame.sendChangeActive();
					//添加进已吃的子的集合
					mGame.eatedActions.add(coordinate);
				}
//				,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
				break;
			case BluetoothConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(BluetoothConstants.TOAST),
						Toast.LENGTH_SHORT).show();
				break;
				
                
			case BluetoothConstants.MESSAGE_WRITE:// 已发送成功的历史消息
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				break;
			case BluetoothConstants.MESSAGE_READ:// 对方成功发来的消息
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				break;

			default:
				break;
			}

		};
	};

	private To3ComputerAI to3ComputerAI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_fight);
		initViews();
		initGame();
		to3ComputerAI = new To3ComputerAI(mGameView.gamePoints);
	}

	protected void setStatus(String string) {
		progress_bar.setVisibility(View.VISIBLE);
		tv_loading.setText(string);
		mRefreshHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				progress_bar.setVisibility(View.GONE);
			}
		}, 6000);
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

	private void initGame() {
		black = new Player(Game.BLACK, GameConstants.TO3_CHESS_COUNT);
		white = new Player(Game.WHITE, GameConstants.TO3_CHESS_COUNT);
		mGame = new To3Game(mRefreshHandler, black, white);
		mGame.setMode(GameConstants.MODE_FIGHT);
		mGameView.setGame(mGame);
		updateActive(mGame);
		updateScore(black, white);
		updatePlayerChess(black, white);
	}

	private void updateActive(To3Game game) {
		if (game.getActive() == Game.BLACK) {
			mBlackActive.setVisibility(View.VISIBLE);
			mWhiteActive.setVisibility(View.INVISIBLE);
		} else {
			mBlackActive.setVisibility(View.INVISIBLE);
			mWhiteActive.setVisibility(View.VISIBLE);
		}
	}

	private void updateScore(Player black, Player white) {
		mBlackWin.setText(black.getWin());
		mWhiteWin.setText(white.getWin());
	}

	// 更新双方棋子数
	private void updatePlayerChess(Player black, Player white) {
		mBChesses.setText(String.valueOf(black.getmChesses()));
		mWChesses.setText(String.valueOf(white.getmChesses()));
	}

	private void showWinDialog(String message) {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.restart:
			restart();
			break;
		case R.id.rollback:
			if (black.getmChesses() == 0 && white.getmChesses() == 0) {
				// 动子阶段的悔棋
				// 1、一般悔棋
				// 2、 吃子悔棋
				mGame.moveRollBack();
				mGame.moveRollBack();
			} else {
				// 落子阶段的悔棋
				// 1、己方成三己方悔棋
				// 吃对方子 之前：己方悔棋 双方各退回一子——只能换一次手
				// 之后：己方悔棋 双方各回退一子 ——被吃的子还原——退回到 重新吃子的状态
				// 2、对方成三，己方悔棋
				// 己方悔棋 被吃的子还原 ——双方各回退一子

				// 3、落子悔棋，双方各悔一子，

				mGame.rollback();
				mGame.rollback();
				black.rollbackChess();
				white.rollbackChess();
				updatePlayerChess(black, white);
			}
			updateActive(mGame);
			mGameView.drawGame();
			break;
		case R.id.about:
			initBluetooth();

			break;
		case R.id.setting:

			break;
		default:
			break;
		}

	}

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothChatService mChatService;

	private Player me;
	private Player challenger;
	
	protected boolean isServer;

	/**
	 * 初始化蓝牙
	 */
	private void initBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "蓝牙不可用", 0).show();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {// 没打开，则开启
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, REQUEST_ENABLE_BT);
		} else {
			initBluetoothService();

		}
	}
	
	private void initGame(Boolean isServer){
		 if (isServer){
	            me = new Player(Game.BLACK,GameConstants.TO3_CHESS_COUNT);
	            challenger = new Player(Game.WHITE,GameConstants.TO3_CHESS_COUNT);
	            mBlackName.setText(R.string.myself);
	            mWhiteName.setText(R.string.challenger);
	        } else {
	            me = new Player(Game.WHITE,GameConstants.TO3_CHESS_COUNT);
	            challenger = new Player(Game.BLACK,GameConstants.TO3_CHESS_COUNT);
	            mWhiteName.setText(R.string.myself);
	            mBlackName.setText(R.string.challenger);
	        }
	        mGame = new To3Game(mRefreshHandler, me, challenger);
	        mGame.setMode(GameConstants.MODE_BLUETOOTH);
	        mGameView.setGame(mGame);
	        updateActive(mGame);
	        updatePlayerChess(me, challenger);
	}

	private void initBluetoothService() {
		mChatService = new BluetoothChatService(this, mRequestHandler);
		if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
			mChatService.start();
		}
		initGame(isServer);
		
		enterDeviceListActivity();
	}

	/**
	 * 进入设备列表页
	 */
	private void enterDeviceListActivity() {
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				initBluetoothService();
			} else {
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "蓝牙无法开启", Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_CONNECT_DEVICE_SECURE:
			// 点击列表设备时，执行以下方法
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		default:
			break;
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	/**
	 * 发消息
	 * @param message
	 */
	private void sendChess(int x, int y,int des) {
		// Check that we're actually connected before trying anything
		if (mChatService !=null && (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)) {
			Toast.makeText(this, "未联机", Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		// Get the message bytes and tell the BluetoothChatService to write
		byte[] send = new byte[4];
		send[0]=4;
		send[1]=(byte) des;
		send[2] = (byte) x;
		send[3] = (byte) y;
		mChatService.write(send);
	}

}
