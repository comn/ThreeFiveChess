package cq.game.fivechess.game;

import java.util.Deque;
import java.util.LinkedList;

import android.os.Handler;
import android.os.Message;

/**
 *   三子棋规则：
 *   第一阶段：落子阶段黑白各9颗依次落子，成三可吃子,且不可再落子
 *   第二阶段：动子阶段黑白依次动子，成三可吃子，三不可吃，非三可吃
 *   第三阶段：子数最新为0的一方判负，投降方判负
	
 * @author Flsolate
 * @date 2016-9-15
 * @description  处理游戏逻辑
 */
public class To3Game {

    public static final int SCALE_MEDIUM = 15;
    
    // 自己
    Player me;
    // 对手
    Player challenger;
    
    private int mMode = 0;
  
    int mGameWidth = 0;
    int mGameHeight = 0;
    int[][] mGameMap = null;
    Deque<Coordinate> mActions ;
    public Deque<Coordinate> eatedActions;
    public Deque<Coordinate> clearedActions;
    
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    // 默认黑子先出
    private int mActive = 1;
    private boolean isFirstChess =false;
    private boolean isAddChess =true;
    
    private Handler mNotify;

	private boolean isTo3Back =false;
    
    public To3Game(Handler h, Player me, Player challenger){
        this(h, me, challenger, SCALE_MEDIUM, SCALE_MEDIUM);
    }
    
    public To3Game(Handler h, Player me, Player challenger, int width, int height){
        mNotify = h;
        this.me = me;
        this.challenger = challenger;
        mGameWidth = width;
        mGameHeight = height;
        mGameMap = new int[mGameWidth][mGameHeight];
        mActions = new LinkedList<Coordinate>();
        eatedActions = new LinkedList<Coordinate>();
        clearedActions =new LinkedList<Coordinate>();
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

	public boolean isAddChess() {
		return isAddChess;
	}

	public void setAddChess(boolean isAddChess) {
		this.isAddChess = isAddChess;
	}

	/**
     * 悔棋一子
     * @return 是否可以悔棋
     */
    public boolean rollback(){
        Coordinate c = mActions.pollLast();
      
		if (c != null){
			  //判断是否为成三状态，是：还原已吃的子
	        if (isThree(c.x, c.y, c.type)) {
	        		
//	        		Coordinate first = mActions.getFirst();
//	        		int firstCount = getChessCount(first.type);
//	        		int secondCount;
//	        		if (first.type==BLACK) {
//	        			secondCount = getChessCount(WHITE);
//					}else {
//						secondCount = getChessCount(BLACK);
//					}
	        		//未吃子，判断子数，先手时子数比后手多一或后手子数相等
//	        		if (((mActive ==first.type) && (firstCount> secondCount)) || ((mActive !=first.type) && (firstCount == secondCount))){
					if (c.type ==mActive) { //当前最后添加的一子颜色和当前手相同（未成三时不同,吃子之后不同），则为成三未吃子悔棋
	        			//则要多毁掉一子
	        			Coordinate r = mActions.pollLast();
	        			mGameMap[r.x][r.y] = 0;
	        			if (mActive==me.type) {
							me.rollbackChess();
						}else {
							challenger.rollbackChess();
						}
	        			sendGameRollBack("rollback eatChess");
					}else {//已吃子
						Coordinate last = eatedActions.pollLast();
//			        	if (last !=null) { 
						mGameMap[last.x][last.y] = last.type;
//					 }
				}
			}
			mGameMap[c.x][c.y] = 0;
			changeActive();
			isAddChess =true;
			return true;
		}
		
        return false;
    }
    
    /**
     * 移动阶段悔棋
     */
    public void moveRollBack(){
    	//成三手之后悔了当前子，不可再悔下一步
		if (isTo3Back) {
			isTo3Back =false;
			return;
		}
    	Coordinate clearlast = clearedActions.pollLast();
    	if (clearlast !=null) {
			//清除当前位置棋子
    		Coordinate c = mActions.pollLast();
    		if (isThree(c.x, c.y, c.type)) {//判断当前位置是否成三
				if (c.type ==mActive) {//未吃子，只悔成三手，并悔吃子状态
					//1、悔棋之后不可因之前成三吃子
					sendGameRollBack("rollback eatChess");
//					2、此处只让悔成三的一手，且不变手
					changeActive();
					isTo3Back =true;
				}else {
					Coordinate last = eatedActions.pollLast();
					mGameMap[last.x][last.y] = last.type;
				}
			}
    		
    		mGameMap[c.x][c.y] = 0;
    		
//    		原位置添加棋子
    		mGameMap[clearlast.x][clearlast.y] =clearlast.type;
    		changeActive();
    		isAddChess =true;
		}
    	
    }
    /**
     *  清除地图上的点
     * @param o
     * @return 
     */
    public boolean clearChess(Coordinate o) {
    	if (o==null) {
			throw new RuntimeException("o is null");
		}
//    	mActions.remove(o);这里要让对象以坐标点作为比较的标准
    	if (mGameMap[o.x][o.y] !=0) {
    		mGameMap[o.x][o.y] = 0;
    		return true;
		}
    	return false;
	}
    /**
     * 还原地图上的点
     */
    public boolean restoreChess(Coordinate o) {
    	if (o!=null) {
    			mGameMap[o.x][o.y] = o.type;
    			return true;
		}
    	return false;
	}
    /**
     * 吃掉
     */
    public boolean eatChess(Coordinate o) {
    	if (clearChess(o) && !isGameEnd(o.x, o.y, o.type)) {
			setAddChess(true);
    		//添加进已吃的子的集合
    		eatedActions.add(o);
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
     * 落子，在GameView中调用，用于玩家点击添加棋子。
     * @param x 横向下标
     * @param y 纵向下标
     * @return 当前位置是否可以下子，mGameMap[x][y] == 0则无子，1则为黑，2则为白。
     */
    public boolean addChess(int x, int y){
    	if (!isAddChess)return false;
    			
        if (mMode == GameConstants.MODE_FIGHT){
        	//地图上该点为空。则添子。
            if(mGameMap[x][y] == 0){
                if (mActive == BLACK){
                    mGameMap[x][y] = BLACK;
                    //判断是否成三
                    if (isThree(x, y, BLACK)) {
                    	sendGameStatus(BLACK);
					}else {
						changeActive();
					}
                } else {
                    mGameMap[x][y] = WHITE;
                    if (isThree(x, y, WHITE)) {
                    	sendGameStatus(WHITE);
					}else {
						changeActive();
					}
                }
                int type = mGameMap[x][y] ;
                if(!isGameEnd(x, y, me.type)){
                    sendAddChess(x, y,type);
                    mActions.add(new Coordinate(x, y,type));
                }
                return true;
            } 
        } else if(mMode == GameConstants.MODE_NET) {
            if(mActive == me.type && mGameMap[x][y] == 0){
                mGameMap[x][y] = me.type;
                mActive = challenger.type;
                if(!isGameEnd(x, y, me.type)){
                    mActions.add(new Coordinate(x, y,me.type));
                }
                sendAddChess(x, y,me.type);
                return true;
            }
        } else if(mMode == GameConstants.MODE_SINGLE){
            if(mActive == me.type && mGameMap[x][y] == 0){
                mGameMap[x][y] = me.type;
                //判断是否成三
                if (isThree(x, y, me.type)) {
                	sendGameStatus(me.type);
				}else {
					changeActive();
				}
            	sendAddChess(x, y,me.type);
            	mActions.add(new Coordinate(x, y,me.type));
                return true;
            }
        }
        
        else if(mMode == GameConstants.MODE_BLUETOOTH){
            if(mActive == me.type && mGameMap[x][y] == 0){
                mGameMap[x][y] = me.type;
                if (isThree(x, y, me.type)) {
                	sendGameStatus(me.type);
				}else {
					mActive = challenger.type;
				}
                sendBlueToothAddChess(x, y,me.type);
                mActions.add(new Coordinate(x, y,me.type));
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
    	if (!isAddChess)return ;
        if(mGameMap[x][y] == 0){
        	//地图中添加子
            mGameMap[x][y] = player.type; 
            if (isThree(x, y, player.type)) {
            	sendGameStatus(player.type);
			}else {
				mActive = me.type;
			}
            //添加坐标
            mActions.add(new Coordinate(x, y,player.type));
            boolean isEnd = isGameEnd(x, y, player.type);
            if(!isEnd){
                mNotify.sendEmptyMessage(GameConstants.CHALLENGER_ADD);
            }
        } 
    }
    
    /**
     * 落子
     * @param c 下子位置
     * @param player 游戏选手  这里为电脑或对手调用
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
        eatedActions.clear();
        clearedActions.clear();
        isAddChess =true; //设置可添加棋子
    }
    /**
     * 自定义先手
     */
    public void reset(int active){
        mGameMap = new int[mGameWidth][mGameHeight];
        mActive = active; 
        mActions.clear();
        eatedActions.clear();
        clearedActions.clear();
        isAddChess =true; //设置可添加棋子
    }
    
    /**
     * 不需要更新落子方，谁输谁先手
     */
    public void resetNet(){
        mGameMap = new int[mGameWidth][mGameHeight];
        mActions.clear();
        eatedActions.clear();
        clearedActions.clear();
        isAddChess =true; //设置可添加棋子
    }
    
    private void changeActive(){
        if(mActive == BLACK){
            mActive = WHITE;
        } else {
            mActive = BLACK;
        }
    }
    /**
     * 发送吃的对方子的位置信息
     * @param x
     * @param y
     * @param type
     */
    public void sendEatChess(int x, int y,int type){
        Message msg = new Message();
        msg.what = GameConstants.EAT_CHESS;
        msg.obj = new Coordinate(x, y, type);
        mNotify.sendMessage(msg);
    }
    
    private void sendBlueToothAddChess(int x, int y,int type){
        Message msg = new Message();
        msg.what = GameConstants.BLUETOOTH_ADD_CHESS;
        msg.obj = new Coordinate(x, y, type);
        mNotify.sendMessage(msg);
    }
    
    private void sendAddChess(int x, int y,int type){
        Message msg = new Message();
        msg.what = GameConstants.ADD_CHESS;
        msg.obj = new Coordinate(x, y, type);
        mNotify.sendMessage(msg);
    }
    /**
     * 换手更新主界面UI
     */
    public void sendChangeActive(){
    	changeActive();
        Message msg = Message.obtain();
        msg.what = GameConstants.CHANGE_ACTIVE;
        mNotify.sendMessage(msg);
    }
    
    // 判断是否成三
    public boolean isThree(int x, int y, int type){
       //1、外层四条三 
//    		横向
    	int horizontal = 0;
    	for (int i = 0; i < 3; i++) {
    		if (x==1+6*i) {
    			if (y==1 ||y==13) {
					//已成三
    				for (int j = 0; j < 3; j++) {
    					if (mGameMap[1+6*j][y] !=type) {
							break;
						}
    					horizontal++;
					}
				}
    		}
    	}
    	if (horizontal==3) {
    		return true;
		}
    	
//    	纵向
    	int vertical = 0;
    	for (int i = 0; i < 3; i++) {
    		if (y==1+6*i) {
    			if (x==1 ||x==13) {
    				//已成三
    				for (int j = 0; j < 3; j++) {
    					if (mGameMap[x][1+6*j] !=type) {
							break;
						}
    					vertical++;
					}
				}
    		}
    	}
     	if (vertical==3) {
    		return true;
		}
     	
       //2、中层四条三
     	int midvertical = 0;
    	for (int i = 0; i < 3; i++) {
    		if (y==3+4*i) {
    			if (x==3 ||x==11) {
    				for (int j = 0; j < 3; j++) {
    					if (mGameMap[x][3+4*j]  !=type) {
							break;
						}
    					midvertical++;
					}
				}
    		}
    	}
    	if (midvertical==3) {
    		return true;
		}
    	
    	int midhorizontal = 0;
    	for (int i = 0; i < 3; i++) {
    		if (x==3+4*i) {
    			if (y==3 ||y==11) {
    				for (int j = 0; j < 3; j++) {
    					if (mGameMap[3+4*j][y] !=type) {
							break;
						}
						midhorizontal++;
					}
				}
    		}
    	}
    	if (midhorizontal==3) {
    		return true;
		}
    	
       //3、内层四条三
    	int minhorizontal = 0;
    	int minvertical =0;
		for (int i = 0; i < 3; i++) {
    		if (y==5+2*i) {
    			if (x==5 ||x==9) {
    				for (int j = 0; j < 3; j++) {
    					if (mGameMap[x][5+2*j] !=type) {
							break;
						}
    					minvertical ++;
					}
				}
    		}
    	}
    	if (minvertical==3) {
    		return true;
		}
    	
     	for (int i = 0; i < 3; i++) {
    		if (x==5+2*i) {
    			if (y==5 ||y==9) {
    				for (int j = 0; j < 3; j++) {
    					if ( mGameMap[5+2*j][y] !=type) {
							break;
						}
						minhorizontal++;
					}
				}
    		}
    	}
       	if (minhorizontal==3) {
    		return true;
		}
       	
       //4、连接三层的四条三
       	//竖直方向
       	int topConnect=0;
       	int bottomConnect = 0;
       	for (int i = 0; i < 3; i++) {
			
       		if (x==7) {
       			if (y==1+2*i) {
       				for (int j = 0; j < 3; j++) {
       					if (mGameMap[x][1+2*j] !=type) {
       						break;
       					}
       					topConnect++;
       				}
    			}else if (y==13-2*i) {
    				for (int j = 0; j < 3;j++) {
    					if (mGameMap[x][13-2*j] !=type) {
    						break;
    					}
    					bottomConnect++;
    				}
				}
       		}
		}
       	if (topConnect==3 || bottomConnect==3) {
    		return true;
		}
       	//水平方向
       	int lefConnect=0;
       	int rightConnect = 0;
       	for (int i = 0; i < 3; i++) {
			
       		if (y==7) {
       			if (x==1+2*i) {
       				for (int j = 0; j < 3; j++) {
       					if (mGameMap[1+2*j][y] !=type) {
       						break;
       					}
       					lefConnect++;
       				}
    			}else if (x==13-2*i) {
    				for (int j = 0; j < 3;j++) {
    					if (mGameMap[13-2*j][y] !=type) {
    						break;
    					}
    					rightConnect++;
    				}
				}
       		}
		}
       	if (lefConnect==3 || rightConnect==3) {
    		return true;
		}
       	
        return false;
    }
    
    public int isTwo(Coordinate c,int type) {
    	    int  two=0;
//		对每个点分析，每个点有四种情况出现两子  如果该点同时又有两个两子（两条），则权值提升
    	//1、外层四条三 
//		横向
    		int bank=0;
			int horizontal = 0;
			int x =c.x;
			int y=c.y;
			for (int i = 0; i < 3; i++) {
				if (x==1+6*i) {
					if (y==1 ||y==13) {
						//已成三
						for (int j = 0; j < 3; j++) {
							if (mGameMap[1+6*j][y] !=type) {
								if (mGameMap[1+6*j][y] ==0) {
									bank++;
									continue;
								}
								break;
							}
							horizontal++;
						}
					}
				}
			}
			if (horizontal==2 && bank==1) {
				two++;
			}
			
		//	纵向
			bank=0;
			int vertical = 0;
			for (int i = 0; i < 3; i++) {
				if (y==1+6*i) {
					if (x==1 ||x==13) {
						//已成三
						for (int j = 0; j < 3; j++) {
							if (mGameMap[x][1+6*j] !=type) {
								if (mGameMap[x][1+6*j] ==0) {
									bank++;
									continue;
								}
								break;
							}
							vertical++;
						}
					}
				}
			}
		 	if (vertical==2 && bank==1) {
		 		two++;
			}
		 	
		   //2、中层四条三
		 	bank=0;
		 	int midvertical = 0;
			for (int i = 0; i < 3; i++) {
				if (y==3+4*i) {
					if (x==3 ||x==11) {
						for (int j = 0; j < 3; j++) {
							if (mGameMap[x][3+4*j]  !=type) {
								if (mGameMap[x][3+4*j] ==0) {
									bank++;
									continue;
								}
								break;
							}
							midvertical++;
						}
					}
				}
			}
			if (midvertical==2 && bank==1) {
				two++;
			}
			
			bank=0;
			int midhorizontal = 0;
			for (int i = 0; i < 3; i++) {
				if (x==3+4*i) {
					if (y==3 ||y==11) {
						for (int j = 0; j < 3; j++) {
							if (mGameMap[3+4*j][y] !=type) {
								if (mGameMap[3+4*j][y] ==0) {
									bank++;
									continue;
								}
								break;
							}
							midhorizontal++;
						}
					}
				}
			}
			if (midhorizontal==2 && bank==1) {
				two++;
			}
			
		   //3、内层四条三
			bank=0;
			int minvertical =0;
			for (int i = 0; i < 3; i++) {
				if (y==5+2*i) {
					if (x==5 ||x==9) {
						for (int j = 0; j < 3; j++) {
							if (mGameMap[x][5+2*j] !=type) {
								if (mGameMap[x][5+2*j] ==0) {
									bank++;
									continue;
								}
								break;
							}
							minvertical ++;
						}
					}
				}
			}
			if (minvertical==2 && bank==1) {
				two++;
			}
			
			bank=0;
			int minhorizontal = 0;
		 	for (int i = 0; i < 3; i++) {
				if (x==5+2*i) {
					if (y==5 ||y==9) {
						for (int j = 0; j < 3; j++) {
							if ( mGameMap[5+2*j][y] !=type) {
								if (mGameMap[5+2*j][y] ==0) {
									bank++;
									continue;
								}
								break;
							}
							minhorizontal++;
						}
					}
				}
			}
		   	if (minhorizontal==2 && bank==1) {
				two++;
			}
		   	
		   //4、连接三层的四条三
		   	//竖直方向
		   	bank=0;
		   	int topConnect=0;
		   	int bottomConnect = 0;
		   	for (int i = 0; i < 3; i++) {
				
		   		if (x==7) {
		   			if (y==1+2*i) {
		   				for (int j = 0; j < 3; j++) {
		   					if (mGameMap[x][1+2*j] !=type) {
		   						if (mGameMap[x][1+2*j] ==0) {
									bank++;
									continue;
								}
		   						break;
		   					}
		   					topConnect++;
		   				}
					}else if (y==13-2*i) {
						for (int j = 0; j < 3;j++) {
							if (mGameMap[x][13-2*j] !=type) {
								if (mGameMap[x][13-2*j] ==0) {
									bank++;
									continue;
								}
								break;
							}
							bottomConnect++;
						}
					}
		   		}
			}
		   	if (topConnect==2 || bottomConnect==2) {
		   		if (bank==1) {
		   			two++;
				}
			}
		   	//水平方向
		   	bank=0;
		   	int lefConnect=0;
		   	int rightConnect = 0;
		   	for (int i = 0; i < 3; i++) {
				
		   		if (y==7) {
		   			if (x==1+2*i) {
		   				for (int j = 0; j < 3; j++) {
		   					if (mGameMap[1+2*j][y] !=type) {
		   						if (mGameMap[1+2*j][y] ==0) {
									bank++;
									continue;
								}
		   						break;
		   					}
		   					lefConnect++;
		   				}
					}else if (x==13-2*i) {
						for (int j = 0; j < 3;j++) {
							if (mGameMap[13-2*j][y] !=type) {
								if (mGameMap[13-2*j][y] ==0) {
									bank++;
									continue;
								}
								break;
							}
							rightConnect++;
						}
					}
		   		}
			}
		   	if (lefConnect==2 || rightConnect==2) {
		   		if (bank==1) {
		   			two++;
				}
			}
		   	
		return two;
	}
    
   /**
    * 判断两个点是否相邻
    */
    public boolean isNearBy(Coordinate s, Coordinate e) {
    	//外层八个点
//    	1.start在角上，end在中心 问题：当start(13,1) 和 end(7,13)相邻
    	if (s.x==1 && s.y==1) {
			if (e.x==1&&e.y==7 || e.x==7&&e.y==1 ) {
				return true;
			}
		}
    	if (s.x==13 && s.y==1) {
			if (e.x==7&&e.y==1 || e.x==13&&e.y==7 ) {
				return true;
			}
		}
    	if (s.x==1 && s.y==13) {
			if (e.x==1&&e.y==7 || e.x==7&&e.y==13 ) {
				return true;
			}
		}
    	if (s.x==13 && s.y==13) {
			if (e.x==13&&e.y==7 || e.x==7&&e.y==13 ) {
				return true;
			}
		}
    	
//    	2.start在中心上，end在中层中心
    	if (s.x==7&&s.y==1) {
			if (e.x==7&&e.y==3) {
				return true;
			}
		}
    	if (s.x==13&&s.y==7) {
			if (e.x==11&&e.y==7) {
				return true;
			}
		}
    	if (s.x==7&&s.y==13) {
			if (e.x==7&&e.y==11) {
				return true;
			}
		}
    	if (s.x==1&&s.y==7) {
			if (e.x==3&&e.y==7) {
				return true;
			}
		}
    	
    	//中层八个点
//    	1.start在角上
    	if (s.x==3&&s.y==3) {
			if (e.x==7&&e.y==3 || e.x==3&&e.y==7) {
				return true;
			}
		}
    	if (s.x==11&&s.y==3) {
			if (e.x==7&&e.y==3 || e.x==11&&e.y==7) {
				return true;
			}
		}
    	if (s.x==3&&s.y==11) {
			if (e.x==7&&e.y==11 || e.x==3&&e.y==7) {
				return true;
			}
		}
    	if (s.x==11&&s.y==11) {
			if (e.x==7&&e.y==11 || e.x==11&&e.y==7) {
				return true;
			}
		}
//    	2.start在中心上
    	if (s.x==7&&s.y==3) {
			if (e.x==7&&e.y==5) {
				return true;
			}
		}
    	if (s.x==11&&s.y==7) {
			if (e.x==9&&e.y==7) {
				return true;
			}
		}
    	if (s.x==7&&s.y==11) {
			if (e.x==7&&e.y==9) {
				return true;
			}
		}
    	if (s.x==3&&s.y==7) {
			if(e.x==5 && e.y==7){
				return true;
			}
		}
    	
    	//下层八个点
//    	1.start在角上
    	if (s.x==5&&s.y==5) {
			if (e.x==7&&e.y==5 || e.x==5&&e.y==7) {
				return true;
			}
		}
    	if (s.x==9&&s.y==5) {
			if (e.x==7&&e.y==5 || e.x==9&&e.y==7) {
				return true;
			}
		}
    	if (s.x==5&&s.y==9) {
			if (e.x==5&&e.y==7 || e.x==7&&e.y==9) {
				return true;
			}
		}
    	if (s.x==9&&s.y==9) {
			if (e.x==7&&e.y==9 || e.x==9&&e.y==7) {
				return true;
			}
		}
    	
		return false;
	}
    
   /**
    *  开始结束的点位互换后也相邻
    * @param start
    * @param end
    * @return 
    */
    public boolean exChangePoint(Coordinate start, Coordinate end) {
		Coordinate coordinate =new Coordinate();
		coordinate =start;
		start =end;
		end =coordinate;
		
		return isNearBy(start,end);
	}
    
    private void sendGameRollBack(String initData) {
    	   Message msg = Message.obtain();
           msg.what = GameConstants.THREE_ROLLBACK;
           msg.obj = initData;
           mNotify.sendMessage(msg);
    }
    
    /**
     * 游戏进行中的状态
     * @param type
     */
    private void sendGameStatus(int player) {
 	   Message msg = Message.obtain();
        msg.what = GameConstants.THREE_CHESS;
        msg.arg1 = player;
        mNotify.sendMessage(msg);
    }
 
	public boolean isGameEnd(int x, int y, int type) {
		
		for (int i = 0; i < mGameWidth; i++) {
			for (int j = 0; j < mGameHeight; j++) {
				if (mGameMap[i][j] ==type) {
					return false;
				}
			}
		}
		if (type==BLACK) {
			sendGameResult(WHITE);
		}else {
			sendGameResult(BLACK);
		}
 		return true;
 		
 	}
    
    public void sendGameResult(int player){
        Message msg = Message.obtain();
        msg.what = GameConstants.GAME_OVER;
        msg.arg1 = player;
        mNotify.sendMessage(msg);
    }
}
