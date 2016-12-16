package com.yang.chess.net;

public class ConnectConstants {

    public static final int SOCKET_NULL = 0;
    public static final int IP_NULL = 1;
    public static final int ON_JOIN = 2;
    public static final int ON_EXIT = 3;
    public static final int UDP_IP_ERROR = 4;
    public static final int UDP_DATA_ERROR = 5;
    public static final int MULTICAST_ERROR = 6;
    
    public static final int CONNECT_ASK = 11;
    public static final int CONNECT_AGREE = 12;
    public static final int CONNECT_REJECT = 13;
    public static final int CHAT_ONE = 14;

    static final int BROADCAST_JOIN = 0; //广播所有玩家加入到玩家集合列表。
    static final int BROADCAST_EXIT = 1;//广播所有玩家退出玩家集合。
    
    static final int UDP_JOIN = 0;
    
    
    public static final int CONNECT_ADD_CHESS = 0;
    public static final int GAME_CONNECTED = 1;
    public static final int ROLLBACK_ASK = 2;
    public static final int ROLLBACK_AGREE = 3;
    public static final int ROLLBACK_REJECT = 4;
	public static String PROXY_IP;
	public static int PROXY_PORT;

}
