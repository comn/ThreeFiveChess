package com.yang.chess;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yang.chess.bluetooth.BluetoothChatService;
import com.yang.chess.bluetooth.BluetoothConstants;
import com.yang.chess.game.Coordinate;
import com.yang.chess.game.Game;
import com.yang.chess.game.GameConstants;

public class BluetoothGameActivity extends BaseActivity {
	private static final String TAG = "BluetoothGameActivity";
	
	protected static final String DEVICE_NAME = "device_name";
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothChatService mChatService;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 2;
	protected boolean isServer=true;

	private Handler mRefreshHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
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
			case GameConstants.BLUETOOTH_ADD_CHESS:
				Coordinate co = (Coordinate) msg.obj;
				me.downChess();
				updatePlayerChess(me, challenger);
				updateActive(mGame);
				Toast.makeText(BluetoothGameActivity.this, "我已下子了，该你了！", 0).show();
				// 发送给联机玩家
				sendChess(co.x, co.y, BluetoothConstants.CHALLENGER_ADD_CHESS);
				break;
			case GameConstants.THREE_CHESS: 
				if (msg.arg1 == Game.BLACK) {  
					Toast.makeText(BluetoothGameActivity.this, "黑方成三吃子", 1).show();
					mGameView.eatChess(Game.WHITE);
				} else if (msg.arg1 == Game.WHITE) {
					Toast.makeText(BluetoothGameActivity.this, "白方成三吃子", 1).show();
					mGameView.eatChess(Game.BLACK);
				}
				break;
			case GameConstants.CHANGE_ACTIVE:// 换手
				updateActive(mGame);
				break;
			// 将自己成三吃的对方子点位传给对方
			case GameConstants.EAT_CHESS:
				Coordinate eat = (Coordinate) msg.obj;
				sendChess(eat.x, eat.y, BluetoothConstants.CHALLENGER_EAT_CHESS);
				break;
			case GameConstants.CHESS_MOVE_START:
				Coordinate s = (Coordinate) msg.obj;
				sendChess(s.x, s.y, BluetoothConstants.CHALLENGER_CHESS_MOVE);
				break;
			default:
				break;
			}
			
		}

	};
	/**
	 * 处理对方发来的消息,相当于电脑的处理
	 */
	private Handler mRequestHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothConstants.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(this,"已联机");
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(this,"正在联机..");
					break;
				case BluetoothChatService.STATE_LISTEN: //开始状态
				case BluetoothChatService.STATE_NONE: //停止状态
					setStatus(this,"未联机");
					break;
				}
				break;
			case BluetoothConstants.MESSAGE_IMFOR:
				// save the connected device's name
				String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				isServer = msg.getData().getBoolean("isServer");
				
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case BluetoothConstants.CHALLENGER_ADD_CHESS://对方发来的添加棋子的信号
				byte[] receive = (byte[]) msg.obj;
				mGame.addChess(receive[2], receive[3], challenger);
				mGameView.drawGame();
	            updateActive(mGame);
//				这里面要做区分了，是不是动子阶段的子力移动的添加
				if (me.getmChesses()==0 && challenger.getmChesses()==0) {
					//记录动子阶段对方移动的结束点  直接跳出循环不必执行落子阶段的代码
					break;
				}
				challenger.downChess();
				updatePlayerChess(me, challenger);
                break;
			case BluetoothConstants.CHALLENGER_EAT_CHESS:
				byte[] e = (byte[]) msg.obj;
				Coordinate coordinate = new Coordinate(e[2], e[3],challenger.getType());
				boolean clearChess = mGame.eatChess(coordinate);
				if (clearChess) {
					mGameView.drawGame();
					//换手
					mGame.sendChangeActive();
				}
				break;
			case BluetoothConstants.CHALLENGER_CHESS_MOVE://处理对方的棋子移动
				//这里面拿到的是移动的开始点即要清除的点，结束点在CHALLENGER_ADD_CHESS中包含
				byte[] s = (byte[]) msg.obj;
				Coordinate start = new Coordinate(s[2], s[3], challenger.type);
				if(mGame.clearChess(start)){
					mGame.clearedActions.add(new Coordinate(start.x, start.y, start.type));
				}
				break;
				
				
			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		初始化蓝牙设备，进行消息的接发
		initBluetooth();
	}

	/**
	 * 初始化蓝牙
	 */
	private void initBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "蓝牙不可用", 0).show();
			finish();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {// 没打开，则开启
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, REQUEST_ENABLE_BT);
		} else {
			initBluetoothService();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
	
	private void initBluetoothService() {
		mChatService = new BluetoothChatService(this, mRequestHandler);
		if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
			mChatService.start();
		}
		initGame(mRefreshHandler,GameConstants.MODE_BLUETOOTH,isServer);
		enterDeviceListActivity();
	}
	
	/**
	 * 进入设备列表页
	 */
	private void enterDeviceListActivity() {
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
	}
	
	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
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
		if(mChatService==null)return;
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

	@Override
	public void restart() {
	}
	
}
