package com.yang.chess.game;

import java.util.Deque;
import java.util.LinkedList;

import android.os.Handler;
import android.os.Message;

/**
 * @author Flsolate
 * @date 2016-9-15
 * @description  处理游戏逻辑
 */
public class Game {

    public static final int SCALE_SMALL = 11;
    public static final int SCALE_MEDIUM = 15;
    public static final int SCALE_LARGE = 19;
    
    // 自己
    Player me;
    // 对手
    Player challenger;
    
    private int mMode = 0;
  
    int mGameWidth = 0;
    int mGameHeight = 0;
    int[][] mGameMap = null;
    Deque<Coordinate> mActions ;
    
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    // 默认黑子先
    private int mActive = 1;
    private boolean isFirstChess =false;
    
    private Handler mNotify;
    
    public Game(Handler h, Player me, Player challenger){
        this(h, me, challenger, SCALE_MEDIUM, SCALE_MEDIUM);
    }
    
    public Game(Handler h, Player me, Player challenger, int width, int height){
        mNotify = h;
        this.me = me;
        this.challenger = challenger;
        mGameWidth = width;
        mGameHeight = height;
        mGameMap = new int[mGameWidth][mGameHeight];
        mActions = new LinkedList<Coordinate>();
    }
    
    public void setMode(int mode){
        this.mMode = mode;
    }
    
    public int getMode(){
        return mMode;
    }

	public boolean isFirstChess() {
		if (mActions.size()<1) {
			isFirstChess =true;
		}else {
			isFirstChess =false;
		}
		return isFirstChess;
	}

	/**
     * 悔棋一子
     * @return 是否可以悔棋
     */
    public boolean rollback(){
        Coordinate c = mActions.pollLast();
        if (c != null){
            mGameMap[c.x][c.y] = 0;
            changeActive();
            return true;
        }
        return false;
    }
    
    public boolean clearChess(Coordinate o) {
    	if (o==null) {
			throw new RuntimeException("o is null");
		}
//    	mActions.remove(o);这里要让对象以坐标点作为比较的标准
    	if (mGameMap[o.x][o.y] !=0) {
    		mGameMap[o.x][o.y] = 0;
    		changeActive();
    		return true;
		}
    	return false;
	}
    
    /**
     * 游戏宽度
     * @return 棋盘的列数
     */
    public int getWidth(){
        return mGameWidth;
    }
    
    /**
     * 游戏高度
     * @return 棋盘横数
     */
    public int getHeight(){
        return mGameHeight;
    }
    
    /**
     * 落子
     * @param x 横向下标
     * @param y 纵向下标
     * @return 当前位置是否可以下子，mGameMap[x][y] == 0则无子，1则为黑，2则为白。
     */
    public boolean addChess(int x, int y){
    	
        if (mMode == GameConstants.MODE_FIGHT){
            if(mGameMap[x][y] == 0){
                if (mActive == BLACK){
                    mGameMap[x][y] = BLACK;
                } else {
                    mGameMap[x][y] = WHITE;
                }
                if(!isGameEnd(x, y, me.type)){
                    changeActive();
                    sendAddChess(x, y);
                    mActions.add(new Coordinate(x, y));
                }
                return true;
            } 
        } else if(mMode == GameConstants.MODE_NET) {
            if(mActive == me.type && mGameMap[x][y] == 0){
                mGameMap[x][y] = me.type;
                mActive = challenger.type;
                if(!isGameEnd(x, y, me.type)){
                    mActions.add(new Coordinate(x, y));
                }
                sendAddChess(x, y);
                return true;
            }
        } else if(mMode == GameConstants.MODE_SINGLE){
            if(mActive == me.type && mGameMap[x][y] == 0){
                mGameMap[x][y] = me.type;
                mActive = challenger.type;
                if(!isGameEnd(x, y, me.type)){
                    sendAddChess(x, y);
                    mActions.add(new Coordinate(x, y));
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * 落子
     * @param x 横向下标
     * @param y 纵向下标
     * @param player 游戏选手
     */
    public void addChess(int x, int y, Player player){
        if(mGameMap[x][y] == 0){
        	//地图中添加子
            mGameMap[x][y] = player.type; 
            //添加坐标
            mActions.add(new Coordinate(x, y));
            boolean isEnd = isGameEnd(x, y, player.type);
            mActive = me.type;
            if(!isEnd){
                mNotify.sendEmptyMessage(GameConstants.ACTIVE_CHANGE);
            }
        } 
    }
    
    /**
     * 落子
     * @param c 下子位置
     * @param player 游戏选手
     */
    public void addChess(Coordinate c, Player player){
        addChess(c.x, c.y, player);
    }
    
    public static int getFighter(int type){
        if (type == BLACK){
            return WHITE;
        } else {
            return BLACK;
        }
    }
    
    /**
     * 返回当前落子方
     * @return mActive
     */
    public int getActive(){
        return mActive;
    }
    
    /**
     * 获取棋盘
     * @return 棋盘数据
     */
    public int[][] getChessMap(){
        return mGameMap;
    }
    /**
     * 获取当前棋子数
     * @return
     */
    public int getChessCount(int type){
    	int count=0;
    	if (mGameMap !=null) {
			for (int i = 0; i < mGameWidth; i++) {
				for (int j = 0; j < mGameHeight; j++) {
					if (type==BLACK && mGameMap[i][j]==BLACK) {
						count++;
					}else if (type==WHITE && mGameMap[i][j]==WHITE) {
						count++;
					}
				}
			}
		}
    	return count;
    }
    
    /**
     * 获取棋盘历史，在GameView中调用
     * @return mActions
     */
    public Deque<Coordinate> getActions(){
        return mActions;
    }
    
    /**
     * 重置游戏,默认黑先手。
     */
    public void reset(){
        mGameMap = new int[mGameWidth][mGameHeight];
        mActive = BLACK; 
        mActions.clear();
    }
    /**
     * 自定义先手
     */
    public void reset(int active){
        mGameMap = new int[mGameWidth][mGameHeight];
        mActive = active; 
        mActions.clear();
    }
    
    /**
     * 不需要更新落子方，谁输谁先手
     */
    public void resetNet(){
        mGameMap = new int[mGameWidth][mGameHeight];
        mActions.clear();
    }
    
    private void changeActive(){
        if(mActive == BLACK){
            mActive = WHITE;
        } else {
            mActive = BLACK;
        }
    }
    
    private void sendAddChess(int x, int y){
        Message msg = new Message();
        msg.what = GameConstants.ADD_CHESS;
        msg.arg1 = x;
        msg.arg2 = y;
        mNotify.sendMessage(msg);
    }
    
    // 判断是否五子连珠
    private boolean isGameEnd(int x, int y, int type){
        int leftX = x-4 > 0? x-4 : 0;
        int rightX = x+4 < mGameWidth-1 ? x+4: mGameWidth-1;
        int topY = y-4 > 0? y-4 : 0;
        int bottomY = y + 4< mGameHeight-1 ? y+4: mGameHeight-1;

        int horizontal = 1;
        // 横向向左
        for (int i = x - 1; i >= leftX ; --i){
            if (mGameMap[i][y] != type){
                break;
            } 
            ++horizontal;
        }
        // 横向向右
        for (int i = x + 1; i <= rightX ; ++i){
            if (mGameMap[i][y] != type){
                break;
            } 
            ++horizontal;
        }
        if (horizontal>=5) {
            sendGameResult(type);
            return true;
        }
        
        int vertical = 1;
        // 纵向向上
        for (int j = y - 1; j >= topY ; --j){
            if (mGameMap[x][j] != type){
                break;
            } 
            ++vertical;
        }
        // 纵向向下
        for (int j = y + 1; j <= bottomY ; ++j){
            if (mGameMap[x][j] != type){
                break;
            } 
            ++vertical;
        }
        if (vertical >= 5) {
            sendGameResult(type);
            return true;
        }
        
        int leftOblique = 1;
        // 左斜向上
        for (int i = x + 1,j = y - 1; i <= rightX && j >= topY ; ++i, --j){
            if (mGameMap[i][j] != type){
                break;
            } 
            ++leftOblique;
        }
        // 左斜向下
        for (int i = x - 1,j = y + 1; i >= leftX && j <= bottomY ; --i, ++j){
            if (mGameMap[i][j] != type){
                break;
            } 
            ++leftOblique;
        }
        if (leftOblique >= 5) {
            sendGameResult(type);
            return true;
        }
        
        int rightOblique = 1;
        // 右斜向上
        for (int i = x - 1,j = y - 1; i >= leftX && j >= topY ; --i, --j){
            if (mGameMap[i][j] != type){
                break;
            } 
            ++rightOblique;
        }
        // 右斜向下
        for (int i = x + 1,j = y + 1; i <= rightX && j <= bottomY ; ++i, ++j){
            if (mGameMap[i][j] != type){
                break;
            } 
            ++rightOblique;
        }
        if (rightOblique >= 5) {
            sendGameResult(type);
            return true;
        }
        
        return false;
    }
    
    private void sendGameResult(int player){
        Message msg = Message.obtain();
        msg.what = GameConstants.GAME_OVER;
        msg.arg1 = player;
        mNotify.sendMessage(msg);
    }
}
