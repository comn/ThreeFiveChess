package cq.game.fivechess.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.R.integer;
import android.util.Log;

/**
 * 
 * @author Flsolate
 * @date 2016-10-6
 * @description 成三的电脑算法    分析数据、按照人大脑中的思维来模拟过程
 */
public class To3ComputerAI {
	private static final String TAG = "To3ComputerAI";
	/*
	 * 分析：分为落子阶段和动子阶段
	 */
	//地图上可落子的所有点的集合，总共24个点位
	private List<Coordinate> gamePoints;
	private int[][][] black = null;
	private int[][][]  white =null;
	
	
	private int[][] plaValue ={ {2,6,173},
								{0,5,7}};
	private int[][] cpuValue ={ {0,3,166},
							    {0,0,5}};
	
	private To3Game game;

	public  To3ComputerAI(List<Coordinate> gamePoints) {
		this.gamePoints =gamePoints;
	    black = new int[To3Game.SCALE_MEDIUM][To3Game.SCALE_MEDIUM][3];
        white = new int[To3Game.SCALE_MEDIUM][To3Game.SCALE_MEDIUM][3];
	}
	
	public void updateValue(To3Game game){
		 this.game=game;
		 int[][] map = game.getChessMap();
	     updateValue(map);
	}
    /**
     * 总思维，就是分析除此空点之相邻两点的落子情况。判断该位置权值
     * @param map
     */
//	把判断成三和isNearBy()结合做或许更好一点.
	private void updateValue(int[][] map) {
		//初步分两个维度：纵向、横向
		//构想一个维度：奇招维度
		 int[] computerValue = {0,0};
	     int[] playerValue = {0,0};
//	     一、遍历棋盘上所有空点 
//	     					分析白棋权值
		for (Coordinate c : gamePoints) {
			if (map[c.x][c.y]==0) {
//			     1).外层
			     //1、该空点在角上
				int counter =0;
				//纵向
				if ((c.x==1&&c.y==1) || (c.x==13&&c.y==1)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][1+i*6] ==To3Game.BLACK) {
							computerValue[0] ++;
						}
						  if(map[c.x][1+i*6]== 0)
	                            break;
					   if(map[c.x][1+i*6] == Game.WHITE)//白子
                        {
                            counter ++;//跳往第二层
                            break;
                        }
					}
				}
				
				if ((c.x==1&&c.y==13) || (c.x==13&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][13-6*i] ==To3Game.BLACK) {
							computerValue[0] ++;
						}
					    if(map[c.x][13-6*i]== 0)
	                            break;
					   if(map[c.x][13-6*i] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==1 || c.x==13) {
					if (c.y==7) {
						if (map[c.x][1] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) { 
							// 此处两个点都为黑则只computerValue[0] ++一次
							computerValue[0] ++;
							if (map[c.x][1] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==1 ) {
						for (int i = 1; i < 3; i++) {
							if (map[c.x][1+2*i] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
							if(map[c.x][1+2*i]== 0)
								break;
							if(map[c.x][1+2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
					if (c.y==13) {
					for (int i = 1; i < 3; i++) {
							if (map[c.x][13-2*i] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
							if(map[c.x][13-2*i]== 0)
								break;
							if(map[c.x][13-2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}
			    white[c.x][c.y][0] = cpuValue[counter][computerValue[0]]; //赋予cpuValue中的权值
                computerValue[0] = 0;
                counter = 0;
				

				
//       	     2).中层
				//1、该空点在角上
				//纵向
				if ((c.x==3&&c.y==3) || (c.x==11&&c.y==3)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][3+i*4] ==To3Game.BLACK) {
							computerValue[0] ++;
						}
						  if(map[c.x][3+i*4]== 0)
	                            break;
					   if(map[c.x][3+i*4] == Game.WHITE)//白子
                        {
                            counter ++;//跳往第二层
                            break;
                        }
					}
				}
				
				if ((c.x==3&&c.y==11) || (c.x==11&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][11-4*i] ==To3Game.BLACK) {
							computerValue[0] ++;
						}
					    if(map[c.x][11-4*i]== 0)
	                            break;
					   if(map[c.x][11-4*i] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==3 || c.x==11) {
					if (c.y==7) {
						if (map[c.x][3] ==To3Game.BLACK || map[c.x][11] ==To3Game.BLACK) {
							computerValue[0] ++;
							if (map[c.x][3] ==To3Game.BLACK && map[c.x][11] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][3] ==To3Game.WHITE || map[c.x][11] ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 ) {
						if (map[c.x][1] ==To3Game.BLACK || map[c.x][5] ==To3Game.BLACK) {
							computerValue[0] ++;
							if (map[c.x][1] ==To3Game.BLACK && map[c.x][5] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.WHITE || map[c.x][5] ==To3Game.WHITE) {
							counter++;
						}
					}
					if (c.y==11) {
						if (map[c.x][9] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) {
							computerValue[0] ++;
							if (map[c.x][9] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][9] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) {
							counter++;
						}
					}
				}
			    white[c.x][c.y][0] = cpuValue[counter][computerValue[0]]; //赋予cpuValue中的权值
                computerValue[0] = 0;
                counter = 0;
				
				Log.d(TAG, "中层纵向：white[+"+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
//			    测试外层和中层AI
//			     3).内层
			     //1、该空点在角上
			     //2、该空点在边上
				//内层纵向
				if ((c.x==5&&c.y==5) || (c.x==9&&c.y==5)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][5+i*2] ==To3Game.BLACK) {
							computerValue[0] ++;
						}
						  if(map[c.x][5+i*2]== 0)
	                            break;
					   if(map[c.x][5+i*2] == Game.WHITE)//白子
                        {
                            counter ++;//跳往第二层
                            break;
                        }
					}
				}
				
				if ((c.x==5&&c.y==9) || (c.x==9&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][9-2*i] ==To3Game.BLACK) {
							computerValue[0] ++;
						}
					    if(map[c.x][9-2*i]== 0)
	                            break;
					   if(map[c.x][9-2*i] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==5 || c.x==9) {
					if (c.y==7) {
						if (map[c.x][5] ==To3Game.BLACK || map[c.x][9] ==To3Game.BLACK) { 
							// 此处两个点都为黑则只computerValue[0] ++一次
							computerValue[0] ++;
							if (map[c.x][5] ==To3Game.BLACK && map[c.x][9] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][5] ==To3Game.WHITE || map[c.x][9] ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==5 ) {
						for (int i = 1; i < 3; i++) {
							if (map[c.x][5-2*i] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
							if(map[c.x][5-2*i]== 0)
								break;
							if(map[c.x][5-2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
					if (c.y==9) {
					for (int i = 1; i < 3; i++) {
							if (map[c.x][9+2*i] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
							if(map[c.x][9+2*i]== 0)
								break;
							if(map[c.x][9+2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}
			    white[c.x][c.y][0] = cpuValue[counter][computerValue[0]]; //赋予cpuValue中的权值
                computerValue[0] = 0;
                counter = 0;
//				Log.d(TAG, "内层纵向：white[+"+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
				
//				外层 该空点在边上 的横向/////////////////////////////////////////////////////////////////////
				if (c.y==7) {
					if(c.x==1 ) {
						for (int i = 1; i < 3; i++) {
							if (map[1+2*i][c.y] ==To3Game.BLACK) {
								computerValue[1] ++;
							}
							if(map[1+2*i][c.y]== 0)
								break;
							if(map[1+2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}else if (c.x==13) {
						for (int i = 1; i < 3; i++) {
							if (map[13-2*i][c.y] ==To3Game.BLACK) {
								computerValue[1] ++;
							}
							if(map[13-2*i][c.y]== 0)
								break;
							if(map[13-2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==1 || c.y==13) {
						if (map[1][c.y] ==To3Game.BLACK || map[13][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[1][c.y] ==To3Game.BLACK && map[13][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[1][c.y]  ==To3Game.WHITE || map[13][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==1&&c.y==1) || (c.x==1&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[1+i*6][c.y] ==To3Game.BLACK) {
							computerValue[1] ++;
						}
					   if(map[1+i*6][c.y]== 0)
                            break;
					   if(map[1+i*6][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==13&&c.y==1) || (c.x==13&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[13-6*i][c.y] ==To3Game.BLACK) {
							computerValue[1] ++;
						}
					    if(map[13-6*i][c.y]== 0)
	                            break;
					   if(map[13-6*i][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][1] = cpuValue[counter][computerValue[1]]; //赋予cpuValue中的权值
                computerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......外层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
                
//            	中层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==3 ) {
						if (map[1][c.y] ==To3Game.BLACK || map[5][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[1][c.y] ==To3Game.BLACK && map[5][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[1][c.y] ==To3Game.WHITE || map[5][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}else if (c.x==11) {
						if (map[13][c.y] ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[13][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[13][c.y] ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 || c.y==11) {
						if (map[3][c.y] ==To3Game.BLACK || map[11][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[3][c.y] ==To3Game.BLACK && map[11][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[3][c.y]  ==To3Game.WHITE || map[11][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==3&&c.y==3) || (c.x==3&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[3+i*4][c.y] ==To3Game.BLACK) {
							computerValue[1] ++;
						}
					   if(map[3+i*4][c.y]== 0)
                            break;
					   if(map[3+i*4][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==11&&c.y==3) || (c.x==11&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[11-4*i][c.y] ==To3Game.BLACK) {
							computerValue[1] ++;
						}
					    if(map[11-4*i][c.y]== 0)
	                            break;
					   if(map[11-4*i][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][1] = cpuValue[counter][computerValue[1]]; //赋予cpuValue中的权值
                computerValue[1] = 0;
                counter = 0;
                Log.d(TAG, ".......中层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
                
//				内层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==5 ) {
						for (int i = 1; i < 3; i++) {
							if (map[5-2*i][c.y] ==To3Game.BLACK) {
								computerValue[1] ++;
							}
							if(map[5-2*i][c.y]== 0)
								break;
							if(map[5-2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}else if (c.x==9) {
						for (int i = 1; i < 3; i++) {
							if (map[9+2*i][c.y] ==To3Game.BLACK) {
								computerValue[1] ++;
							}
							if(map[9+2*i][c.y]== 0)
								break;
							if(map[9+2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==5 || c.y==9) {
						if (map[5][c.y] ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[5][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[5][c.y]  ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==5&&c.y==5) || (c.x==5&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[5+i*2][c.y] ==To3Game.BLACK) {
							computerValue[1] ++;
						}
					   if(map[5+i*2][c.y]== 0)
                            break;
					   if(map[5+i*2][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==9&&c.y==5) || (c.x==9&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[9-2*i][c.y] ==To3Game.BLACK) {
							computerValue[1] ++;
						}
					    if(map[9-2*i][c.y]== 0)
	                            break;
					   if(map[9-2*i][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][1] = cpuValue[counter][computerValue[1]]; //赋予cpuValue中的权值
                computerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......内层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
			}
		}
	     
//	     一、遍历棋盘上所有空点 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
//			分析黑棋权值
		for (Coordinate c : gamePoints) {
			if (map[c.x][c.y]==0) {
//			     1).外层
			     //1、该空点在角上
				int counter =0;
				//纵向
				if ((c.x==1&&c.y==1) || (c.x==13&&c.y==1)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][1+i*6] ==To3Game.BLACK) {
							playerValue[0] ++;
						}
						  if(map[c.x][1+i*6]== 0)
	                            break;
					   if(map[c.x][1+i*6] == Game.WHITE)//白子
                        {
                            counter ++;//跳往第二层
                            break;
                        }
					}
				}
				
				if ((c.x==1&&c.y==13) || (c.x==13&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][13-6*i] ==To3Game.BLACK) {
							playerValue[0] ++;
						}
					    if(map[c.x][13-6*i]== 0)
	                            break;
					   if(map[c.x][13-6*i] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==1 || c.x==13) {
					if (c.y==7) {
						if (map[c.x][1] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) { 
							// 此处两个点都为黑则只playerValue[0] ++一次
							playerValue[0] ++;
							if (map[c.x][1] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==1 ) {
						for (int i = 1; i < 3; i++) {
							if (map[c.x][1+2*i] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
							if(map[c.x][1+2*i]== 0)
								break;
							if(map[c.x][1+2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
					if (c.y==13) {
					for (int i = 1; i < 3; i++) {
							if (map[c.x][13-2*i] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
							if(map[c.x][13-2*i]== 0)
								break;
							if(map[c.x][13-2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}
			    black[c.x][c.y][0] = plaValue[counter][playerValue[0]]; //赋予cpuValue中的权值
			    playerValue[0] = 0;
                counter = 0;
				
//       	     2).中层
				//1、该空点在角上
				//纵向
				if ((c.x==3&&c.y==3) || (c.x==11&&c.y==3)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][3+i*4] ==To3Game.BLACK) {
							playerValue[0] ++;
						}
						  if(map[c.x][3+i*4]== 0)
	                            break;
					   if(map[c.x][3+i*4] == Game.WHITE)//白子
                        {
                            counter ++;//跳往第二层
                            break;
                        }
					}
				}
				
				if ((c.x==3&&c.y==11) || (c.x==11&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][11-4*i] ==To3Game.BLACK) {
							playerValue[0] ++;
						}
					    if(map[c.x][11-4*i]== 0)
	                            break;
					   if(map[c.x][11-4*i] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==3 || c.x==11) {
					if (c.y==7) {
						if (map[c.x][3] ==To3Game.BLACK || map[c.x][11] ==To3Game.BLACK) {
							playerValue[0] ++;
							if (map[c.x][3] ==To3Game.BLACK && map[c.x][11] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
						}else if (map[c.x][3] ==To3Game.WHITE || map[c.x][11] ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 ) {
						if (map[c.x][1] ==To3Game.BLACK || map[c.x][5] ==To3Game.BLACK) {
							playerValue[0] ++;
							if (map[c.x][1] ==To3Game.BLACK && map[c.x][5] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.WHITE || map[c.x][5] ==To3Game.WHITE) {
							counter++;
						}
					}
					if (c.y==11) {
						if (map[c.x][9] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) {
							playerValue[0] ++;
							if (map[c.x][9] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
						}else if (map[c.x][9] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) {
							counter++;
						}
					}
				}
			    black[c.x][c.y][0] = plaValue[counter][playerValue[0]]; //赋予cpuValue中的权值
                playerValue[0] = 0;
                counter = 0;
				
				Log.d(TAG, "中层纵向：white[+"+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
//			    测试外层和中层AI
//			     3).内层
			     //1、该空点在角上
			     //2、该空点在边上
				//内层纵向
				if ((c.x==5&&c.y==5) || (c.x==9&&c.y==5)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][5+i*2] ==To3Game.BLACK) {
							playerValue[0] ++;
						}
						  if(map[c.x][5+i*2]== 0)
	                            break;
					   if(map[c.x][5+i*2] == Game.WHITE)//白子
                        {
                            counter ++;//跳往第二层
                            break;
                        }
					}
				}
				
				if ((c.x==5&&c.y==9) || (c.x==9&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][9-2*i] ==To3Game.BLACK) {
							playerValue[0] ++;
						}
					    if(map[c.x][9-2*i]== 0)
	                            break;
					   if(map[c.x][9-2*i] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==5 || c.x==9) {
					if (c.y==7) {
						if (map[c.x][5] ==To3Game.BLACK || map[c.x][9] ==To3Game.BLACK) { 
							// 此处两个点都为黑则只playerValue[0] ++一次
							playerValue[0] ++;
							if (map[c.x][5] ==To3Game.BLACK && map[c.x][9] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
						}else if (map[c.x][5] ==To3Game.WHITE || map[c.x][9] ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==5 ) {
						for (int i = 1; i < 3; i++) {
							if (map[c.x][5-2*i] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
							if(map[c.x][5-2*i]== 0)
								break;
							if(map[c.x][5-2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
					if (c.y==9) {
					for (int i = 1; i < 3; i++) {
							if (map[c.x][9+2*i] ==To3Game.BLACK) {
								playerValue[0] ++;
							}
							if(map[c.x][9+2*i]== 0)
								break;
							if(map[c.x][9+2*i] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}
			    black[c.x][c.y][0] = plaValue[counter][playerValue[0]]; //赋予cpuValue中的权值
                playerValue[0] = 0;
                counter = 0;
//				Log.d(TAG, "内层纵向：white[+"+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
				
//				外层 该空点在边上 的横向/////////////////////////////////////////////////////////////////////
				if (c.y==7) {
					if(c.x==1 ) {
						for (int i = 1; i < 3; i++) {
							if (map[1+2*i][c.y] ==To3Game.BLACK) {
								playerValue[1] ++;
							}
							if(map[1+2*i][c.y]== 0)
								break;
							if(map[1+2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}else if (c.x==13) {
						for (int i = 1; i < 3; i++) {
							if (map[13-2*i][c.y] ==To3Game.BLACK) {
								playerValue[1] ++;
							}
							if(map[13-2*i][c.y]== 0)
								break;
							if(map[13-2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==1 || c.y==13) {
						if (map[1][c.y] ==To3Game.BLACK || map[13][c.y]  ==To3Game.BLACK) {
							playerValue[1] ++;
							if (map[1][c.y] ==To3Game.BLACK && map[13][c.y]  ==To3Game.BLACK) {
								playerValue[1] ++;
							}
						}else if (map[1][c.y]  ==To3Game.WHITE || map[13][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==1&&c.y==1) || (c.x==1&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[1+i*6][c.y] ==To3Game.BLACK) {
							playerValue[1] ++;
						}
					   if(map[1+i*6][c.y]== 0)
                            break;
					   if(map[1+i*6][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==13&&c.y==1) || (c.x==13&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[13-6*i][c.y] ==To3Game.BLACK) {
							playerValue[1] ++;
						}
					    if(map[13-6*i][c.y]== 0)
	                            break;
					   if(map[13-6*i][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][1] = plaValue[counter][playerValue[1]]; //赋予cpuValue中的权值
				playerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......外层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
                
//            	中层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==3 ) {
						if (map[1][c.y] ==To3Game.BLACK || map[5][c.y]  ==To3Game.BLACK) {
							playerValue[1] ++;
							if (map[1][c.y] ==To3Game.BLACK && map[5][c.y]  ==To3Game.BLACK) {
								playerValue[1] ++;
							}
						}else if (map[1][c.y] ==To3Game.WHITE || map[5][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}else if (c.x==11) {
						if (map[13][c.y] ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							playerValue[1] ++;
							if (map[13][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
								playerValue[1] ++;
							}
						}else if (map[13][c.y] ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 || c.y==11) {
						if (map[3][c.y] ==To3Game.BLACK || map[11][c.y]  ==To3Game.BLACK) {
							playerValue[1] ++;
							if (map[3][c.y] ==To3Game.BLACK && map[11][c.y]  ==To3Game.BLACK) {
								playerValue[1] ++;
							}
						}else if (map[3][c.y]  ==To3Game.WHITE || map[11][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==3&&c.y==3) || (c.x==3&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[3+i*4][c.y] ==To3Game.BLACK) {
							playerValue[1] ++;
						}
					   if(map[3+i*4][c.y]== 0)
                            break;
					   if(map[3+i*4][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==11&&c.y==3) || (c.x==11&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[11-4*i][c.y] ==To3Game.BLACK) {
							playerValue[1] ++;
						}
					    if(map[11-4*i][c.y]== 0)
	                            break;
					   if(map[11-4*i][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][1] = plaValue[counter][playerValue[1]]; //赋予cpuValue中的权值
				playerValue[1] = 0;
                counter = 0;
                Log.d(TAG, ".......中层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
//				内层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==5 ) {
						for (int i = 1; i < 3; i++) {
							if (map[5-2*i][c.y] ==To3Game.BLACK) {
								playerValue[1] ++;
							}
							if(map[5-2*i][c.y]== 0)
								break;
							if(map[5-2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}else if (c.x==9) {
						for (int i = 1; i < 3; i++) {
							if (map[9+2*i][c.y] ==To3Game.BLACK) {
								playerValue[1] ++;
							}
							if(map[9+2*i][c.y]== 0)
								break;
							if(map[9+2*i][c.y] == Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==5 || c.y==9) {
						if (map[5][c.y] ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							playerValue[1] ++;
							if (map[5][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
								playerValue[1] ++;
							}
						}else if (map[5][c.y]  ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==5&&c.y==5) || (c.x==5&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[5+i*2][c.y] ==To3Game.BLACK) {
							playerValue[1] ++;
						}
					   if(map[5+i*2][c.y]== 0)
                            break;
					   if(map[5+i*2][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==9&&c.y==5) || (c.x==9&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[9-2*i][c.y] ==To3Game.BLACK) {
							playerValue[1] ++;
						}
					    if(map[9-2*i][c.y]== 0)
	                            break;
					   if(map[9-2*i][c.y] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][1] = plaValue[counter][playerValue[1]]; //赋予cpuValue中的权值
				playerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......内层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
			}
		}
	}
	/**
	 * 
	 * @param map
	 * @return point
	 * @description 落子点坐标   问题分析： 我做的电脑AI目前太蠢了，还有很多改进的空间
	 */
	public Coordinate getPosition(int[][] map) {
		    int maxpSum = 0;
	        int maxcSum = 0;
	        
	        int maxpValue = -10;
	        int maxcValue = -10;
	        int blackRow = 0; 
	        int blackCollum = 0;
	        int whiteRow = 0; 
	        int whiteCollum = 0;

		for (Coordinate c : gamePoints) {
			if (map[c.x][c.y] == 0) {

				for (int k = 0; k < 2; k++) {
					if (black[c.x][c.y][k] > maxpValue) {
						blackRow = c.x;
						blackCollum = c.y;
						maxpValue = black[c.x][c.y][k];
						maxpSum = black[c.x][c.y][0] + black[c.x][c.y][1];
					}
					if (black[c.x][c.y][k] == maxpValue) {
						if (maxpSum < (black[c.x][c.y][0] + black[c.x][c.y][1])) {
							blackRow = c.x;
							blackCollum = c.y;
							maxpSum = black[c.x][c.y][0] + black[c.x][c.y][1];
						}
					}

					if (white[c.x][c.y][k] > maxpValue) {
						whiteRow = c.x;
						whiteCollum = c.y;
						maxpValue = white[c.x][c.y][k];
						maxpSum = white[c.x][c.y][0] + white[c.x][c.y][1];
					}
					if (white[c.x][c.y][k] == maxcValue) {
						if (maxcSum < (white[c.x][c.y][0] + white[c.x][c.y][1])) {
							whiteRow = c.x;
							whiteCollum = c.y;
							maxcSum = white[c.x][c.y][0] + white[c.x][c.y][1];
						}
					}

				}
			}
		}
		
		Coordinate coordinate =new Coordinate();
		  if(maxcValue > maxpValue){   
			  coordinate.x = whiteRow;
			  coordinate.y = whiteCollum; 
		  }else {
			  coordinate.x = blackRow; 
			  coordinate.y = blackCollum;
		  }
		return coordinate;
	}
	/**
	 * 成三吃子
	 */
	public Coordinate eatChess(int[][] map) {
		//1、有己方两子凉连续     吃堵住了己方三的对方子
//		2、无，则 吃堵住己方已经成三的子
//		3、无，则任意吃非对方成三的子
		
		return null;
	}
	
	/**
	 * 动子阶段的起始点
	 */
	public Coordinate moveStart(int[][] map){
		//1、分析棋盘的权值（白 /黑）判断进攻还是防守
		//  找到最大权值得点   
//				找到距离该点最近的路线进行子力移动
//		            
		return null;
	}
	
	/**
	 * 动子时的结束点
	 */
	public void moveEnd() {
		
	}
	
	
}
