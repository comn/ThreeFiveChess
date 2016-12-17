package com.yang.chess.bluetooth;

public class BluetoothConstants {

	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 6;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
	public static final int CHALLENGER_ADD_CHESS = 7;
	public static final int MESSAGE_IMFOR = 4;
	public static final int CHALLENGER_EAT_CHESS = 8;
	public static final int CHALLENGER_CHESS_MOVE = 9;
}
