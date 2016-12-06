package cq.game.fivechess.game;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import cq.game.fivechess.R;

/**
 * 负责游戏的显示，游戏的逻辑判断在Game.java中
 * @author cuiqing
 */
public class To3GameView extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "To3GameView";
    private static final boolean DEBUG = true;
    public static boolean isTo3 =false;
    
    // 定义SurfaceHolder对象
    SurfaceHolder mHolder = null;
    
    // 棋子画笔
    private Paint chessPaint = new Paint();;
    // 棋盘画笔
    private Paint boardPaint = new Paint();
    private int boardColor = 0;
    private float boardWidth = 0.0f;
    private float anchorWidth = 0.0f;
    
    // 清屏画笔
    Paint clear = new Paint();
    
    public List<Coordinate> gamePoints =new ArrayList<Coordinate>();
    public int[][] mChessArray =null;

    Bitmap mBlack = null;
    Bitmap mBlackNew = null;
    Bitmap mWhite = null;
    Bitmap mWhiteNew = null;
    
    int mChessboardWidth = 0; //棋盘宽度
    int mChessboardHeight = 0;//棋盘高度
    int mChessSize = 0;//棋子大小

    Context mContext = null;

    private To3Game mGame;
    
    private Coordinate focus;
    private Coordinate start;
    private Coordinate end;
    private boolean isDrawFocus;
    private Bitmap bFocus;
    
	private double speed=1;
	private int width;
	private int height;
	private EatChessCallBack eatChessCallBack;
	protected boolean isCanTouchUp =true;
    
    public To3GameView(Context context) {
        this(context, null);
    }

    public To3GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        boardColor = Color.BLACK;
        boardWidth = getResources().getDimensionPixelSize(R.dimen.boardWidth);
        anchorWidth = getResources().getDimensionPixelSize(R.dimen.anchorWidth);
        focus = new Coordinate();
        start = new Coordinate();
        end = new Coordinate();
        init();
    }
    
    private void init(){
        mHolder = this.getHolder();
        mHolder.addCallback(this);
        // 设置透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
//        true to set the antialias bit in the flags, false to clear it 抗锯齿
        chessPaint.setAntiAlias(true);
        boardPaint.setStrokeWidth(boardWidth);
        boardPaint.setColor(boardColor);
        clear.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        setFocusable(true);
        //添加可下子的点位
        addGamePoints();
        isTo3=true;
    }
    
    private void addGamePoints() {
    	//左右长线竖直方向六点
    	for (int i = 0; i < 3; i++) {
    		gamePoints.add(new Coordinate(1,1+6*i));
    		gamePoints.add(new Coordinate(13,1+6*i));
		}
    	for (int i = 0; i < 3; i++) {
    		gamePoints.add(new Coordinate(3,3+4*i));
    		gamePoints.add(new Coordinate(11,3+4*i));
		}
    	for (int i = 0; i < 3; i++) {
    		gamePoints.add(new Coordinate(5,5+2*i));
    		gamePoints.add(new Coordinate(9,5+2*i));
		}
    	//中间竖直方向六点
    	for (int i = 0; i < 3; i++) {
    		gamePoints.add(new Coordinate(7,1+2*i));
    		gamePoints.add(new Coordinate(7,13-2*i));
		}
    	
	}
    /**
     * 控制棋子移动
     */
    public void chessMove(Coordinate start,Coordinate end) {
    	int type = mGame.getChessMap()[start.x][start.y];
    	if (mGame.getActive()!=type) {//非己手则不可移动
			return;
		}
    	
//		chessMoveAnima(type);
    	boolean clearChess = mGame.clearChess(start);
    	if (clearChess) {
    		mGame.clearedActions.add(new Coordinate(start.x, start.y, type));
			mGame.addChess(end.x, end.y);
		}
		drawGame();
	}
   
    /**
     *   棋子移动的动画
     * @param start
     * @param end
     */
	private void chessMoveAnima(Coordinate start,Coordinate end) {
		int type = mGame.getChessMap()[start.x][start.y];
		int x=end.x-start.x;
		int y=end.y-start.y;
		int s=(int) Math.sqrt((x*x+y*y));
		double dx=x*speed/s;
		double dy=y*speed/s;
		
		int i = 0;
		while (i<s) {
			
			start.x+=dx;
			start.y+=dy;
			
			Canvas canvas = mHolder.lockCanvas();
			canvas.drawRect(0,0,width,height, clear);
              if (type == Game.BLACK){
                  canvas.drawBitmap(mBlack, start.x*mChessSize, start.y*mChessSize, chessPaint);
              } else if (type == Game.WHITE){
                  canvas.drawBitmap(mWhite, start.x*mChessSize, start.y*mChessSize, chessPaint);
              }
              mHolder.unlockCanvasAndPost(canvas);
              i++;
		}
	}

	/**
     * 设置游戏
     * @param game
     */
    public void setGame(To3Game game){
        mGame = game;
        requestLayout();
    }
    /**
     * 测量和重新调整宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置高度与宽度
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        Log.d(TAG, "width="+width);
        if(mGame != null){
            if (width % mGame.getWidth() == 0){//宽度能被棋盘宽度整除
            	
            } else {
                width = width / mGame.getWidth() * mGame.getWidth();
            }
            
            float scale = ((float)mGame.getHeight()) / mGame.getWidth();
            height = (int) (width*scale);
            setMeasuredDimension(width, height);//确定GameView最终宽高
            Log.d(TAG, "width="+width+"  height="+height);//645,645
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //16,661,16,661
        if (DEBUG) Log.d(TAG, "left="+left+"  top="+top+" right="+right+" bottom="+bottom);
        if (mGame != null) {
            mChessboardWidth = mGame.getWidth();
            mChessboardHeight = mGame.getHeight();
            mChessSize = (right - left) / mChessboardWidth;//棋子大小43
            Log.d(TAG, "mChessSize=" + mChessSize + " mChessboardWidth="
                    + mChessboardWidth + " mChessboardHeight"
                    + mChessboardHeight);
        }
    }

    /**
     * 绘制当前显示进程中的游戏界面
     */
    public void drawGame(){
        Canvas canvas = mHolder.lockCanvas();
        if (mHolder == null || canvas == null) {
            Log.d(TAG, "mholde="+mHolder+"  canvas="+canvas);
            return;
        }
        // 清屏  ：是否可以不用清屏，用双缓冲技术实现
        canvas.drawPaint(clear);
        drawChessBoard(canvas);
        drawChess(canvas);
        drawFocus(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }
    
    /**
     * 增加一个棋子
     * @param x 横坐标
     * @param y 纵坐标
     */
    public void addChess(int x, int y){
        if (mGame == null){
            Log.d(TAG, "game can not be null");
            return;
        }
      //成龙棋总棋子数小于18
    	if (mGame.getActions().size()>=GameConstants.TOTAL_CHESS)
    		return;
    	
        //可添加棋子的点
        for (Coordinate gamePoint : gamePoints) {
        	if (gamePoint.x==x && gamePoint.y==y) {
        			mGame.addChess(x, y);
        			drawGame();
			}
		}
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            focus.x = (int) (x/mChessSize);//强转之后丢失半个棋子的精度，正好为GameMap点位
            focus.y = (int) (y/mChessSize);
            Log.d(TAG, "x/mChessSize :"+x/mChessSize);//1.534...为一个半棋子长度
            isCanTouchUp=true;
            
            if (mGame == null){
            	Log.d(TAG, "game can not be null");
            	break;
            }
            int type = mGame.getChessMap()[focus.x][focus.y];
            //动子阶段
            if (mGame.me.getmChesses()==0 && mGame.challenger.getmChesses()==0) {
            	if (type!=0) {
            		start.x =focus.x;
            		start.y=focus.y;
            		start.type =type;
            	}else {
            		end.x=focus.x;
            		end.y=focus.y;
            		end.type =type;
            		//判断结束点是否在开始点的旁边一个位置 是：可移动 否：不可移动
            		if(isNearBy(start,end) || exChangePoint(start,end)){
            			chessMove(start, end);
            		}
            	}
			}
           
            if (eatChessCallBack !=null) {
				eatChessCallBack.eatChess(focus.x,focus.y,type);
			}
            
            //按下后该点具有焦点
		    isDrawFocus = true;
		    //抬起有效果
            drawGame();
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
        	if (!isCanTouchUp)break;
        	
            isDrawFocus = false;
            int newx = (int) (x / mChessSize);
            int newy = (int) (y / mChessSize);
            if (canAdd(newx, newy, focus)) {
                addChess(focus.x, focus.y);
            } else {
                drawGame();
            }
            break;
        default:
            break;
        }
        return true;
    }

    private boolean isNearBy(Coordinate start, Coordinate end) {
    	//外层八个点
//    	1.start在角上，end在中心
    	if (start.x==1 || start.x==13) {
			if (start.y==1 || start.y==13) {
				if (end.x==7) {
					if (end.y==1 || end.y==13) {
						return true;
					}
				}else if(end.x==1 || end.x==13) {
					if (end.y==7) {
						return true;
					}
				}
			}
		}
//    	2.start在中心上，end在中层中心
    	if (start.x==7) {
			if (start.y==1 || start.y==13) {
				if (end.x==7) {
					if (end.y==3 || end.y==11) {
						return true;
					}
				}
			}
		}else if(start.x==1 || start.x ==13) {
			if (start.y==7) {
				if (end.y==7) {
					if (end.x==3 ||end.x==11) {
						return true;
					}
				}
			}
		}
    	//中层八个点
//    	1.start在角上
    	if (start.x==3 || start.x==11) {
			if (start.y==3 || start.y==11) {
				if (end.x==7) {
					if (end.y==3 || end.y==11) {
						return true;
					}
				}else if(end.x==3 || end.x==11) {
					if (end.y==7) {
						return true;
					}
				}
			}
		}
//    	2.start在中心上
    	if (start.x==7) {
			if (start.y==3 || start.y==11) {
				if (end.x==7) {
					if (end.y==5 || end.y==9) {
						return true;
					}
				}
			}
		}else if(start.x==3 || start.x ==11) {
			if (start.y==7) {
				if (end.y==7) {
					if (end.x==5 ||end.x==9) {
						return true;
					}
				}
			}
		}
    	
    	//下层八个点
//    	1.start在角上
    	if (start.x==5 || start.x==9) {
			if (start.y==5 || start.y==9) {
				if (end.x==7) {
					if (end.y==5 || end.y==9) {
						return true;
					}
				}else if(end.x==5 || end.x==9) {
					if (end.y==7) {
						return true;
					}
				}
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
	private boolean exChangePoint(Coordinate start, Coordinate end) {
		Coordinate coordinate =new Coordinate();
		coordinate =start;
		start =end;
		end =coordinate;
		
		return isNearBy(start,end);
	}

	/**
     * 判断是否取消此次下子
     * @param x x位置
     * @param y y位置
     * @return
     */
    private boolean canAdd(float x, float y, Coordinate focus){
        return x < focus.x+3 && x > focus.x -3 
                && y < focus.y + 3 && y > focus.y - 3;
    }
    
    /**
     * 创建棋子
     * @param width VIEW的宽度
     * @param height VIEW的高度
     * @param type 类型——白子或黑子
     * @return Bitmap
     */
    private Bitmap createChess(int width, int height, int type){
        int tileSize = width/15;
        Bitmap bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable d = null;
        if (type == 0){
            d = getResources().getDrawable(R.drawable.black);
        } else if (type == 1) {
            d = getResources().getDrawable(R.drawable.white);
        } else if (type == 2){
            d = getResources().getDrawable(R.drawable.black_new);
        } else if (type == 3){
            d = getResources().getDrawable(R.drawable.white_new);
        } else if (type == 4){
            d = getResources().getDrawable(R.drawable.focus);
        }
        d.setBounds(0, 0, tileSize, tileSize);
        d.draw(canvas);
        return bitmap;
    }
    
    // 画棋盘背景
    private void drawChessBoard(){
        Canvas canvas = mHolder.lockCanvas();//拿到画布
        if (mHolder == null || canvas == null) {
            return;
        }
        drawChessBoard(canvas);
        mHolder.unlockCanvasAndPost(canvas);//解锁更新
    }
    
    // 画棋盘背景
    private void drawChessBoard(Canvas canvas){
        // 绘制锚点
        int startlongX = mChessSize/2+mChessSize;
        int startlongY = mChessSize/2+mChessSize;
        int endlongX = startlongX + (mChessSize * (mChessboardWidth - 3));
        int endlongY = startlongY + (mChessSize * (mChessboardHeight- 3));
        // draw 竖直线,左侧
        for (int i = 0; i < 3; i++) {
        	canvas.drawLine(startlongX+2*i*mChessSize, startlongY+2*i*mChessSize, startlongX+2*i*mChessSize, endlongY-2*i*mChessSize, boardPaint);
		}
        // draw 竖直线,右侧
        for (int i = 0; i < 3; i++) {
        	canvas.drawLine(endlongX-2*i*mChessSize, startlongY+2*i*mChessSize, endlongX-2*i*mChessSize, endlongY-2*i*mChessSize, boardPaint);
		}
        
        // draw 水平线，上侧
        for (int i = 0; i < 3; ++i){
            canvas.drawLine(startlongX+2*i*mChessSize, startlongY+2*i*mChessSize, endlongX-2*i*mChessSize, startlongY+2*i*mChessSize, boardPaint);
        }
        // draw 水平线，下侧
        for (int i = 0; i < 3; ++i){
            canvas.drawLine(startlongX+2*i*mChessSize, endlongY-2*i*mChessSize, endlongX-2*i*mChessSize, endlongY-2*i*mChessSize, boardPaint);
        }
       
        // 中心点
        int circleX = startlongX+mChessSize*(mChessboardWidth/2)-mChessSize;
        int circleY = startlongY+mChessSize*(mChessboardHeight/2)-mChessSize;
        //绘制锚点
        canvas.drawCircle(0, 0, anchorWidth, boardPaint);
        
        //画四条连接线
        canvas.drawLine(startlongX, circleY, startlongX+4*mChessSize, circleY, boardPaint);
        canvas.drawLine(endlongX, circleY, endlongX-4*mChessSize, circleY, boardPaint);
        
        canvas.drawLine(circleX, startlongY, circleX, startlongY+4*mChessSize, boardPaint);
        canvas.drawLine(circleX, endlongY, circleX, endlongY-4*mChessSize, boardPaint);
        
        //画四个十字点
        canvas.drawCircle(7*mChessSize+mChessSize/2, 3*mChessSize+mChessSize/2, anchorWidth, boardPaint);
        canvas.drawCircle(3*mChessSize+mChessSize/2, 7*mChessSize+mChessSize/2, anchorWidth, boardPaint);
        canvas.drawCircle(7*mChessSize+mChessSize/2, 11*mChessSize+mChessSize/2, anchorWidth, boardPaint);
        canvas.drawCircle(11*mChessSize+mChessSize/2, 7*mChessSize+mChessSize/2, anchorWidth, boardPaint);
    }
    
    // 画棋子
    private void drawChess(Canvas canvas){
        int[][] chessMap = mGame.getChessMap();
        //遍历棋盘，画所有已落的棋子。
        for (int x = 0; x < chessMap.length; ++x){
            for (int y = 0; y < chessMap[0].length; ++y){//x=0的一维数组长度
                int type = chessMap[x][y];
                if (type == Game.BLACK){
                    canvas.drawBitmap(mBlack, x*mChessSize, y*mChessSize, chessPaint);
                } else if (type == Game.WHITE){
                    canvas.drawBitmap(mWhite, x*mChessSize, y*mChessSize, chessPaint);
                }
            }
        }
        // 画最新下的一个棋子，带有中心焦点的棋子。
        if (mGame.getActions() != null && mGame.getActions().size() > 0){
            Coordinate last = mGame.getActions().getLast();
            int lastType = chessMap[last.x][last.y];
            if (lastType == Game.BLACK){
                canvas.drawBitmap(mBlackNew, last.x*mChessSize, last.y*mChessSize, chessPaint);
            } else if (lastType == Game.WHITE){
                canvas.drawBitmap(mWhiteNew, last.x*mChessSize, last.y*mChessSize, chessPaint);
            }
        }
    }
    
    /**
     * 画棋子落子时的当前框
     * @param canvas
     */
    private void drawFocus(Canvas canvas){
        if (isDrawFocus){
            canvas.drawBitmap(bFocus, focus.x*mChessSize, focus.y*mChessSize, chessPaint);
        }
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 初始化棋盘
        drawChessBoard();
    }
    
    /**
     * This method is always called at least once, after surfaceCreated.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mBlack != null){
//        	so it should only be called if you are sure there are no further uses for the bitmap
            mBlack.recycle();
        }
        if (mWhite != null){
        	//当前白子Bitmap没有被销毁则循环利用
            mWhite.recycle();
        }
        mBlack = createChess(width, height, 0);
        mWhite = createChess(width, height, 1);
        mBlackNew = createChess(width, height, 2);
        mWhiteNew = createChess(width, height, 3);
        bFocus = createChess(width, height, 4);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//    	holder.removeCallback(this);
    }
    
    interface EatChessCallBack {
    	public void eatChess(int x,int y,int type);
    }
    
    public void setEatChessCallBack(EatChessCallBack eatChessCallBack){
    	this.eatChessCallBack =eatChessCallBack;
    }
    /**
     * 吃子
     * @param white
     */
	public void eatChess(final int player) {
		//此時可不再落子，直到吃子后，成三者执手吃完之后才能换手
		mGame.setAddChess(false);
		eatChessCallBack =new EatChessCallBack() {
			
			@Override
			public void eatChess(int x, int y, int type) {
				if (player ==type) {
					if (mGame.isThree(x, y, type)) {
						Toast.makeText(mContext, "龙不可吃！", 0).show();
						return;
					}
					boolean clearChess = mGame.clearChess(new Coordinate(x, y));
					if (clearChess && !mGame.isGameEnd(x, y, player)) {//吃子成功后不可再吃
						eatChessCallBack=null;
						//可添加子
						mGame.setAddChess(true);
						//设置抬起不能添加子
						isCanTouchUp  =false;
						//换手
						mGame.sendChangeActive();
						//添加进已吃的子的集合
						mGame.eatedActions.add(new Coordinate(x, y, type));
						//发送吃子点位
						mGame.sendEatChess(x, player, type);
					}
				}
			}
		};
	}
	

}
