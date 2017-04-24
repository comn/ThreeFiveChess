package com.yang.chess.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.R.integer;
import android.util.Log;

/**
 * 
 * @author Flsolate
 * @date 2016-10-6
 * @description 成三的电脑算法    分析数据
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
	
	//假想用户为防守方，电脑为进攻方
	private int[][] plaValue ={ {2,9,173},
								{0,3,8}};
	private int[][] cpuValue ={ {0,6,166},
							    {0,1,5}};
	
	private To3Game game;
	private Set<Coordinate> coordinates =new HashSet<Coordinate>();//不保证顺序的Set集合
	private List<To3ChessWay> whiteWays =new ArrayList<To3ChessWay>();
	private List<To3ChessWay> blackWays =new ArrayList<To3ChessWay>();
	private To3ChessWay way;

	public  To3ComputerAI(List<Coordinate> gamePoints) {
		this.gamePoints =gamePoints;
	    black = new int[To3Game.SCALE_MEDIUM][To3Game.SCALE_MEDIUM][6];
        white = new int[To3Game.SCALE_MEDIUM][To3Game.SCALE_MEDIUM][6];
	}
	
	public void updateValue(To3Game game){
		 this.game=game;
		 int[][] map = game.getChessMap();
	     updateValue(map);
	}
    /**
     * 总思维，就是分析除此空点之相邻两点的落子情况。判断该位置权值;
     * @param map
     */
	private void updateValue(int[][] map) {
		 int[] computerValue = {0,0};
	     int[] playerValue = {0,0};
//	     一、遍历棋盘上所有空点 
//	     					分析白棋权值
		for (Coordinate c : gamePoints) {
			if (map[c.x][c.y]==0) {
//				Log.d(TAG, "map["+c.x+"]["+c.y+"]");
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
					   if(map[c.x][1+i*6] == To3Game.WHITE)
                        {
                            counter ++;
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
					   if(map[c.x][13-6*i] == To3Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==1 || c.x==13) {
					if (c.y==7) {
					  
						if (map[c.x][1] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
							computerValue[0] ++;
							computerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.BLACK && map[c.x][13] ==0)||(map[c.x][1] ==0 && map[c.x][13] ==To3Game.BLACK)) {
							computerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.WHITE && map[c.x][13] ==To3Game.BLACK)||(map[c.x][1]  ==To3Game.BLACK && map[c.x][13] ==To3Game.WHITE)) {
							computerValue[0] ++;
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
							if(map[c.x][1+2*i] == To3Game.WHITE)
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
							if(map[c.x][13-2*i] == To3Game.WHITE)
							{
								counter ++;
								break;
							}
						}
					}
				}
			    white[c.x][c.y][0] = cpuValue[counter][computerValue[0]]; 
                computerValue[0] = 0;
                counter = 0;
//                Log.d(TAG, "外层纵向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
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
					   if(map[c.x][3+i*4] == To3Game.WHITE)
                        {
                            counter ++;
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
					   if(map[c.x][11-4*i] == To3Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==3 || c.x==11) {
					if (c.y==7) {
						if (map[c.x][3] ==To3Game.BLACK && map[c.x][11] ==To3Game.BLACK) {
							computerValue[0] ++;
							computerValue[0] ++;
						}
						if ((map[c.x][3] ==To3Game.BLACK && map[c.x][11] ==0)||(map[c.x][3] ==0 && map[c.x][11] ==To3Game.BLACK)) {
							computerValue[0] ++;
						}
						if ((map[c.x][3] ==To3Game.WHITE && map[c.x][11] ==To3Game.BLACK)||(map[c.x][3]  ==To3Game.BLACK && map[c.x][11] ==To3Game.WHITE)) {
							computerValue[0] ++;
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 ) {
				
						if (map[c.x][1] ==To3Game.BLACK && map[c.x][5] ==To3Game.BLACK) {
							computerValue[0] ++;
							computerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.BLACK && map[c.x][5] ==0)||(map[c.x][1] ==0 && map[c.x][5] ==To3Game.BLACK)) {
							computerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.WHITE && map[c.x][5] ==To3Game.BLACK)||(map[c.x][1]  ==To3Game.BLACK && map[c.x][5] ==To3Game.WHITE)) {
							computerValue[0] ++;
							counter++;
						}
					}
					if (c.y==11) {
				
						if (map[c.x][9] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
							computerValue[0] ++;
							computerValue[0] ++;
						}
						if ((map[c.x][9] ==To3Game.BLACK && map[c.x][13] ==0)||(map[c.x][9] ==0 && map[c.x][13] ==To3Game.BLACK)) {
							computerValue[0] ++;
						}
						if ((map[c.x][9] ==To3Game.WHITE && map[c.x][13] ==To3Game.BLACK)||(map[c.x][9]  ==To3Game.BLACK && map[c.x][13] ==To3Game.WHITE)) {
							computerValue[0] ++;
							counter++;
						}
					}
				}
			    white[c.x][c.y][1] = cpuValue[counter][computerValue[0]]; 
                computerValue[0] = 0;
                counter = 0;
				
//				Log.d(TAG, "中层纵向：white[+"+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
//			     3).内层
				//内层纵向
				if ((c.x==5&&c.y==5) || (c.x==9&&c.y==5)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][5+i*2] ==To3Game.BLACK) {
							computerValue[0] ++;
						}
						  if(map[c.x][5+i*2]== 0)
	                            break;
					   if(map[c.x][5+i*2] == To3Game.WHITE)
                        {
                            counter ++;
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
					   if(map[c.x][9-2*i] == To3Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==5 || c.x==9) {
					if (c.y==7) {
						if (map[c.x][5] ==To3Game.BLACK && map[c.x][9] ==To3Game.BLACK) {
							computerValue[0] ++;
							computerValue[0] ++;
						}
						if ((map[c.x][5] ==To3Game.BLACK && map[c.x][9] ==0)||(map[c.x][5] ==0 && map[c.x][9] ==To3Game.BLACK)) {
							computerValue[0] ++;
						}
						if ((map[c.x][5] ==To3Game.WHITE && map[c.x][9] ==To3Game.BLACK)||(map[c.x][5]  ==To3Game.BLACK && map[c.x][9] ==To3Game.WHITE)) {
							computerValue[0] ++;
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
							if(map[c.x][5-2*i] == To3Game.WHITE)
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
							if(map[c.x][9+2*i] == To3Game.WHITE)
							{
								counter ++;
								break;
							}
						}
					}
				}
			    white[c.x][c.y][2] = cpuValue[counter][computerValue[0]]; //赋予cpuValue中的权值
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
							if(map[1+2*i][c.y] == To3Game.WHITE)
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
							if(map[13-2*i][c.y] == To3Game.WHITE)
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==1 || c.y==13) {
						if (map[1][c.y] ==To3Game.BLACK && map[13][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							computerValue[1] ++;
						}
						if ((map[1][c.y] ==To3Game.BLACK && map[13][c.y] ==0)||(map[1][c.y] ==0 && map[13][c.y] ==To3Game.BLACK)) {
							computerValue[1] ++;
						}
						if ((map[1][c.y] ==To3Game.WHITE && map[13][c.y] ==To3Game.BLACK)||(map[1][c.y] ==To3Game.BLACK && map[13][c.y] ==To3Game.WHITE)) {
							computerValue[1] ++;
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
					   if(map[1+i*6][c.y] == To3Game.WHITE)
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
					   if(map[13-6*i][c.y] == To3Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][3] = cpuValue[counter][computerValue[1]]; 
                computerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......外层横向：white["+c.x+"]["+c.y+"][1]"+white[c.x][c.y][1]);
                
                
//            	中层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==3 ) {
					
						if (map[1][c.y] ==To3Game.BLACK && map[5][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							computerValue[1] ++;
						}
						if ((map[1][c.y] ==To3Game.BLACK && map[5][c.y] ==0)||(map[1][c.y] ==0 && map[5][c.y] ==To3Game.BLACK)) {
							computerValue[1] ++;
						}
						if ((map[1][c.y] ==To3Game.WHITE && map[5][c.y] ==To3Game.BLACK)||(map[1][c.y] ==To3Game.BLACK && map[5][c.y] ==To3Game.WHITE)) {
							computerValue[1] ++;
							counter++;
						}
					}else if (c.x==11) {
				
						if (map[13][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							computerValue[1] ++;
						}
						if ((map[13][c.y] ==To3Game.BLACK && map[9][c.y] ==0)||(map[13][c.y] ==0 && map[9][c.y] ==To3Game.BLACK)) {
							computerValue[1] ++;
						}
						if ((map[13][c.y] ==To3Game.WHITE && map[9][c.y] ==To3Game.BLACK)||(map[13][c.y] ==To3Game.BLACK && map[9][c.y] ==To3Game.WHITE)) {
							computerValue[1] ++;
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 || c.y==11) {
				
						if (map[3][c.y] ==To3Game.BLACK && map[11][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							computerValue[1] ++;
						}
						if ((map[3][c.y] ==To3Game.BLACK && map[11][c.y] ==0)||(map[3][c.y] ==0 && map[11][c.y] ==To3Game.BLACK)) {
							computerValue[1] ++;
						}
						if ((map[3][c.y] ==To3Game.WHITE && map[11][c.y] ==To3Game.BLACK)||(map[3][c.y] ==To3Game.BLACK && map[11][c.y] ==To3Game.WHITE)) {
							computerValue[1] ++;
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
					   if(map[3+i*4][c.y] == To3Game.WHITE)
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
					   if(map[11-4*i][c.y] == To3Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][4] = cpuValue[counter][computerValue[1]]; 
                computerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......中层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
                
//				内层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==5 ) {
						for (int i = 1; i < 3; i++) {
							if (map[5-2*i][c.y] ==To3Game.BLACK) {
								computerValue[1] ++;
							}
							if(map[5-2*i][c.y]== 0)
								break;
							if(map[5-2*i][c.y] == To3Game.WHITE)
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
							if(map[9+2*i][c.y] == To3Game.WHITE)
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==5 || c.y==9) {
						
						if (map[5][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							computerValue[1] ++;
						}
						if ((map[5][c.y] ==To3Game.BLACK && map[9][c.y] ==0)||(map[5][c.y] ==0 && map[9][c.y] ==To3Game.BLACK)) {
							computerValue[1] ++;
						}
						if ((map[5][c.y] ==To3Game.WHITE && map[9][c.y] ==To3Game.BLACK)||(map[5][c.y] ==To3Game.BLACK && map[9][c.y] ==To3Game.WHITE)) {
							computerValue[1] ++;
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
					   if(map[5+i*2][c.y] == To3Game.WHITE)
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
					   if(map[9-2*i][c.y] == To3Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][5] = cpuValue[counter][computerValue[1]]; //赋予cpuValue中的权值
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
						if (map[c.x][1+i*6] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
						  if(map[c.x][1+i*6]== 0)
	                            break;
					   if(map[c.x][1+i*6] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==1&&c.y==13) || (c.x==13&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][13-6*i] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
					    if(map[c.x][13-6*i]== 0)
	                            break;
					   if(map[c.x][13-6*i] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==1 || c.x==13) {
					if (c.y==7) {
						if (map[c.x][1] ==To3Game.WHITE && map[c.x][13] ==To3Game.WHITE) {
							playerValue[0] ++;
							playerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.WHITE && map[c.x][13] ==0)||(map[c.x][1] ==0 && map[c.x][13] ==To3Game.WHITE)) {
							playerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.WHITE && map[c.x][13] ==To3Game.BLACK)||(map[c.x][1] ==To3Game.BLACK && map[c.x][13] ==To3Game.WHITE)) {
							playerValue[0] ++;
							counter++;
						}
						
					}
				}else if (c.x==7) {
					if (c.y==1 ) {
						for (int i = 1; i < 3; i++) {
							if (map[c.x][1+2*i] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
							if(map[c.x][1+2*i]== 0)
								break;
							if(map[c.x][1+2*i] == To3Game.BLACK)
							{
								counter ++;
								break;
							}
						}
					}
					if (c.y==13) {
					for (int i = 1; i < 3; i++) {
							if (map[c.x][13-2*i] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
							if(map[c.x][13-2*i]== 0)
								break;
							if(map[c.x][13-2*i] == To3Game.BLACK)
							{
								counter ++;
								break;
							}
						}
					}
				}
			    black[c.x][c.y][0] = plaValue[counter][playerValue[0]];
			    playerValue[0] = 0;
                counter = 0;
				
//       	     2).中层
				//1、该空点在角上
				//纵向
				if ((c.x==3&&c.y==3) || (c.x==11&&c.y==3)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][3+i*4] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
						  if(map[c.x][3+i*4]== 0)
	                            break;
					   if(map[c.x][3+i*4] == To3Game.BLACK)
                        {
                            counter ++;//跳往第二层
                            break;
                        }
					}
				}
				
				if ((c.x==3&&c.y==11) || (c.x==11&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][11-4*i] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
					    if(map[c.x][11-4*i]== 0)
	                            break;
					   if(map[c.x][11-4*i] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==3 || c.x==11) {
					if (c.y==7) {
					
						if (map[c.x][3] ==To3Game.WHITE && map[c.x][11] ==To3Game.WHITE) {
							playerValue[0] ++;
							playerValue[0] ++;
						}
						if ((map[c.x][3] ==To3Game.WHITE && map[c.x][11] ==0)||(map[c.x][3] ==0 && map[c.x][11] ==To3Game.WHITE)) {
							playerValue[0] ++;
						}
						if ((map[c.x][3] ==To3Game.WHITE && map[c.x][11] ==To3Game.BLACK)||(map[c.x][3] ==To3Game.BLACK && map[c.x][11] ==To3Game.WHITE)) {
							playerValue[0] ++;
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 ) {
					  
						if (map[c.x][1] ==To3Game.WHITE && map[c.x][5] ==To3Game.WHITE) {
							playerValue[0] ++;
							playerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.WHITE && map[c.x][5] ==0)||(map[c.x][1] ==0 && map[c.x][5] ==To3Game.WHITE)) {
							playerValue[0] ++;
						}
						if ((map[c.x][1] ==To3Game.WHITE && map[c.x][5] ==To3Game.BLACK)||(map[c.x][1] ==To3Game.BLACK && map[c.x][5] ==To3Game.WHITE)) {
							playerValue[0] ++;
							counter++;
						}
					}
					if (c.y==11) {
						if (map[c.x][9] ==To3Game.WHITE && map[c.x][13] ==To3Game.WHITE) {
							playerValue[0] ++;
							playerValue[0] ++;
						}
						if ((map[c.x][9] ==To3Game.WHITE && map[c.x][13] ==0)||(map[c.x][9] ==0 && map[c.x][13] ==To3Game.WHITE)) {
							playerValue[0] ++;
						}
						if ((map[c.x][9] ==To3Game.WHITE && map[c.x][13] ==To3Game.BLACK)||(map[c.x][9] ==To3Game.BLACK && map[c.x][13] ==To3Game.WHITE)) {
							playerValue[0] ++;
							counter++;
						}
					}
				}
			    black[c.x][c.y][1] = plaValue[counter][playerValue[0]]; //赋予cpuValue中的权值
                playerValue[0] = 0;
                counter = 0;
				
//			    测试外层和中层AI
//			     3).内层
				//内层纵向
				if ((c.x==5&&c.y==5) || (c.x==9&&c.y==5)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][5+i*2] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
						  if(map[c.x][5+i*2]== 0)
	                            break;
					   if(map[c.x][5+i*2] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==5&&c.y==9) || (c.x==9&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][9-2*i] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
					    if(map[c.x][9-2*i]== 0)
	                            break;
					   if(map[c.x][9-2*i] == To3Game.BLACK)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==5 || c.x==9) {
					if (c.y==7) {
						
						if (map[c.x][5] ==To3Game.WHITE && map[c.x][9] ==To3Game.WHITE) {
							playerValue[0] ++;
							playerValue[0] ++;
						}
						if ((map[c.x][5] ==To3Game.WHITE && map[c.x][9] ==0)||(map[c.x][5] ==0 && map[c.x][9] ==To3Game.WHITE)) {
							playerValue[0] ++;
						}
						if ((map[c.x][5] ==To3Game.WHITE && map[c.x][9] ==To3Game.BLACK)||(map[c.x][5] ==To3Game.BLACK && map[c.x][9] ==To3Game.WHITE)) {
							playerValue[0] ++;
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==5 ) {
						for (int i = 1; i < 3; i++) {
							if (map[c.x][5-2*i] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
							if(map[c.x][5-2*i]== 0)
								break;
							if(map[c.x][5-2*i] == To3Game.BLACK)//白子
							{
								counter ++;
								break;
							}
						}
					}
					if (c.y==9) {
					for (int i = 1; i < 3; i++) {
							if (map[c.x][9+2*i] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
							if(map[c.x][9+2*i]== 0)
								break;
							if(map[c.x][9+2*i] == To3Game.BLACK)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}
			    black[c.x][c.y][2] = plaValue[counter][playerValue[0]]; 
                playerValue[0] = 0;
                counter = 0;
//				Log.d(TAG, "内层纵向：white[+"+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
				
//				外层 该空点在边上 的横向/////////////////////////////////////////////////////////////////////
				if (c.y==7) {
					if(c.x==1 ) {
						for (int i = 1; i < 3; i++) {
							if (map[1+2*i][c.y] ==To3Game.WHITE) {
								playerValue[1] ++;
							}
							if(map[1+2*i][c.y]== 0)
								break;
							if(map[1+2*i][c.y] == To3Game.BLACK)
							{
								counter ++;
								break;
							}
						}
					}else if (c.x==13) {
						for (int i = 1; i < 3; i++) {
							if (map[13-2*i][c.y] ==To3Game.WHITE) {
								playerValue[1] ++;
							}
							if(map[13-2*i][c.y]== 0)
								break;
							if(map[13-2*i][c.y] == To3Game.BLACK)
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==1 || c.y==13) {
						
						if (map[1][c.y] ==To3Game.WHITE && map[13][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							playerValue[1] ++;
						}
						if ((map[1][c.y]==To3Game.WHITE && map[13][c.y] ==0)||(map[1][c.y] ==0 && map[13][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
						}
						if ((map[1][c.y] ==To3Game.WHITE && map[13][c.y] ==To3Game.BLACK)||(map[1][c.y] ==To3Game.BLACK && map[13][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
							counter++;
						}
						
					}
				}
				
//				横向
				if ((c.x==1&&c.y==1) || (c.x==1&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[1+i*6][c.y] ==To3Game.WHITE) {
							playerValue[1] ++;
						}
					   if(map[1+i*6][c.y]== 0)
                            break;
					   if(map[1+i*6][c.y] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==13&&c.y==1) || (c.x==13&&c.y==13)) {
					for (int i = 1; i < 3; i++) {
						if (map[13-6*i][c.y] ==To3Game.WHITE) {
							playerValue[1] ++;
						}
					    if(map[13-6*i][c.y]== 0)
	                            break;
					   if(map[13-6*i][c.y] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][3] = plaValue[counter][playerValue[1]];
				playerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......外层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
                
//            	中层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==3 ) {
						if (map[1][c.y] ==To3Game.WHITE && map[5][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							playerValue[1] ++;
						}
						if ((map[1][c.y]==To3Game.WHITE && map[5][c.y] ==0)||(map[1][c.y] ==0 && map[5][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
						}
						if ((map[1][c.y] ==To3Game.WHITE && map[5][c.y] ==To3Game.BLACK)||(map[1][c.y] ==To3Game.BLACK && map[5][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
							counter++;
						}
					}else if (c.x==11) {
						if (map[13][c.y] ==To3Game.WHITE && map[9][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							playerValue[1] ++;
						}
						if ((map[13][c.y]==To3Game.WHITE && map[9][c.y] ==0)||(map[13][c.y] ==0 && map[9][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
						}
						if ((map[13][c.y] ==To3Game.WHITE && map[9][c.y] ==To3Game.BLACK)||(map[13][c.y] ==To3Game.BLACK && map[9][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
							counter++;
						}
					}
				}else if (c.x==7) {
					if (c.y==3 || c.y==11) {
						if (map[3][c.y] ==To3Game.WHITE && map[11][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							playerValue[1] ++;
						}
						if ((map[3][c.y]==To3Game.WHITE && map[11][c.y] ==0)||(map[3][c.y] ==0 && map[11][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
						}
						if ((map[3][c.y] ==To3Game.WHITE && map[11][c.y] ==To3Game.BLACK)||(map[3][c.y] ==To3Game.BLACK && map[11][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==3&&c.y==3) || (c.x==3&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[3+i*4][c.y] ==To3Game.WHITE) {
							playerValue[1] ++;
						}
					   if(map[3+i*4][c.y]== 0)
                            break;
					   if(map[3+i*4][c.y] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==11&&c.y==3) || (c.x==11&&c.y==11)) {
					for (int i = 1; i < 3; i++) {
						if (map[11-4*i][c.y] ==To3Game.WHITE) {
							playerValue[1] ++;
						}
					    if(map[11-4*i][c.y]== 0)
	                            break;
					   if(map[11-4*i][c.y] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][4] = plaValue[counter][playerValue[1]]; 
				playerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......中层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
//				内层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==5 ) {
						for (int i = 1; i < 3; i++) {
							if (map[5-2*i][c.y] ==To3Game.WHITE) {
								playerValue[1] ++;
							}
							if(map[5-2*i][c.y]== 0)
								break;
							if(map[5-2*i][c.y] == To3Game.BLACK)//白子
							{
								counter ++;
								break;
							}
						}
					}else if (c.x==9) {
						for (int i = 1; i < 3; i++) {
							if (map[9+2*i][c.y] ==To3Game.WHITE) {
								playerValue[1] ++;
							}
							if(map[9+2*i][c.y]== 0)
								break;
							if(map[9+2*i][c.y] == To3Game.BLACK)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==5 || c.y==9) {
						if (map[5][c.y] ==To3Game.WHITE && map[9][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							playerValue[1] ++;
						}
						if ((map[5][c.y]==To3Game.WHITE && map[9][c.y] ==0)||(map[5][c.y] ==0 && map[9][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
						}
						if ((map[5][c.y] ==To3Game.WHITE && map[9][c.y] ==To3Game.BLACK)||(map[5][c.y] ==To3Game.BLACK && map[9][c.y] ==To3Game.WHITE)) {
							playerValue[1] ++;
							counter++;
						}
					}
				}
				
//				横向
				if ((c.x==5&&c.y==5) || (c.x==5&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[5+i*2][c.y] ==To3Game.WHITE) {
							playerValue[1] ++;
						}
					   if(map[5+i*2][c.y]== 0)
                            break;
					   if(map[5+i*2][c.y] == To3Game.BLACK)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				if ((c.x==9&&c.y==5) || (c.x==9&&c.y==9)) {
					for (int i = 1; i < 3; i++) {
						if (map[9-2*i][c.y] ==To3Game.WHITE) {
							playerValue[1] ++;
						}
					    if(map[9-2*i][c.y]== 0)
	                            break;
					   if(map[9-2*i][c.y] == To3Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][5] = plaValue[counter][playerValue[1]]; 
				playerValue[1] = 0;
                counter = 0;
			}
		}
	}
	
	/**
	 * @param map
	 * @return point
	 * @description 落子点坐标   问题分析： 我做的电脑AI目前太蠢了，还有很多改进的空间，，
	 */
	public Coordinate getPosition(int[][] map) {
	        int blackRow = 0; 
	        int blackCollum = 0;
	        int whiteRow = 0; 
	        int whiteCollum = 0;
	        int maxpValue = -10;
	        int maxcValue = -10;
	        int maxpSum = 0;
	        int maxcSum = 0;
	        
	        coordinates.clear();
			coordinates.addAll(gamePoints);
			//无序的遍历棋盘，给一些变化，避免过于死板
		for (Coordinate c : coordinates) {
			if (map[c.x][c.y] == 0) {
				for (int k = 0; k < 6; k++) {
//					Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>黑棋权值：black["+c.x+"]["+c.y+"]["+k+"]"+black[c.x][c.y][k]);
					if (black[c.x][c.y][k] > maxpValue) {
						blackRow = c.x;
						blackCollum = c.y;
						maxpValue = black[c.x][c.y][k];
						maxpSum = black[c.x][c.y][0] + black[c.x][c.y][1]+ black[c.x][c.y][2]+ black[c.x][c.y][3]+ black[c.x][c.y][4]+ black[c.x][c.y][5];
					}
					if (black[c.x][c.y][k] == maxpValue) {
						if (maxpSum < (black[c.x][c.y][0] + black[c.x][c.y][1]+ black[c.x][c.y][2]+ black[c.x][c.y][3]+ black[c.x][c.y][4]+ black[c.x][c.y][5])) {
							blackRow = c.x;
							blackCollum = c.y;
							maxpSum = black[c.x][c.y][0] + black[c.x][c.y][1]+ black[c.x][c.y][2]+ black[c.x][c.y][3]+ black[c.x][c.y][4]+ black[c.x][c.y][5];
						}
					}

//					Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>白棋权值：white["+c.x+"]["+c.y+"]["+k+"]"+white[c.x][c.y][k]);
					if (white[c.x][c.y][k] > maxcValue) { 
						whiteRow = c.x;
						whiteCollum = c.y;
						maxcValue = white[c.x][c.y][k];
						maxcSum = white[c.x][c.y][0] + white[c.x][c.y][1]+ white[c.x][c.y][2]+ white[c.x][c.y][3]+ white[c.x][c.y][4]+ white[c.x][c.y][5];
					}
					if (white[c.x][c.y][k] == maxcValue) {
						if (maxcSum < (white[c.x][c.y][0] + white[c.x][c.y][1]+ white[c.x][c.y][2]+ white[c.x][c.y][3]+ white[c.x][c.y][4]+ white[c.x][c.y][5])) {
							whiteRow = c.x;
							whiteCollum = c.y;
							maxcSum = white[c.x][c.y][0] + white[c.x][c.y][1]+ white[c.x][c.y][2]+ white[c.x][c.y][3]+ white[c.x][c.y][4]+ white[c.x][c.y][5];
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
//		  Log.d(TAG, "White["+whiteRow+"]["+whiteCollum+"]" +"\n>>"+"Black["+blackRow+"]["+blackCollum+"]");
		return coordinate;
	}
	
	/**
	 * 电脑成三吃子
	 */
	public Coordinate eatChess(int[][] map) {
		
//		首先应该找出是否有成二的点
		Coordinate co =null;
		coordinates.clear();
		coordinates.addAll(gamePoints);
		for (Coordinate c : coordinates) {
			if (map[c.x][c.y]  == To3Game.BLACK && !(game.isThree(c.x, c.y, To3Game.BLACK))) {
				// 对方成二的情况优先吃其子
				Log.i(TAG, "two : "+game.isTwo(c,To3Game.BLACK));
				
				if (game.isTwo(c,To3Game.BLACK) ==GameConstants.TWO_TWO) {
//					有两条时，直接跳出
					co= new Coordinate(c.x, c.y, To3Game.BLACK);
					break;
				}else if (game.isTwo(c,To3Game.BLACK) ==GameConstants.A_TWO) {
//					有一条时记录该条，循环继续，co可替换之前的0条的情况
					co= new Coordinate(c.x, c.y, To3Game.BLACK);
					continue;
				}else {
					//该点没有条时，且也没有一条，这里co都替换不了之前的0和1条的
					if (co==null) {
						co= new Coordinate(c.x, c.y, To3Game.BLACK);
					}
				}
			}
		}
		return co;
	}
	
	/**
	 * 动子阶段的起始点                 哎，，，，，，，这制作的电脑实在是太傻了，有待研究！
	 * 电脑已经有一点智能了，不过电脑目前只能看到一步子，动子阶段的智能化目前还有待加强
	 */
	public Coordinate moveStart(To3Game game){
		this.game =game;
		int[][] map=game.getChessMap();
		//1、分析棋盘的权值（白 /黑）判断进攻还是防守
		
		 int blackRow = 0; 
	     int blackCollum = 0;
	     int whiteRow = 0; 
	     int whiteCollum = 0;
	     int maxpValue = -10;
	     int maxcValue = -10;
	     int maxpSum = 0;
	     int maxcSum = 0;
	     
		findWays(game, map);
		
		updateValue(game);
//		这些end点里相对于白子、黑子方的判断，权值不同
//		白子走当前一步最好的点是，当前白子可走路线中，end点中权值最高的点 maxWhite
//		要在地图中除去该最好线路中开始点的子，即置为0，判断结束点权值大小， 因为是根据对方棋子分布判断所以这样没什么意义
		for (To3ChessWay way : whiteWays) {
			Coordinate c =way.end;
			for (int k = 0; k < 6; k++) {
				if (white[c.x][c.y][k] > maxcValue) { 
					whiteRow = c.x;
					whiteCollum = c.y;
					maxcValue = white[c.x][c.y][k];
					maxcSum = white[c.x][c.y][0] + white[c.x][c.y][1]+ white[c.x][c.y][2]+ white[c.x][c.y][3]+ white[c.x][c.y][4]+ white[c.x][c.y][5];
				}
				if (white[c.x][c.y][k] == maxcValue) {
					if (maxcSum < (white[c.x][c.y][0] + white[c.x][c.y][1]+ white[c.x][c.y][2]+ white[c.x][c.y][3]+ white[c.x][c.y][4]+ white[c.x][c.y][5])) {
						whiteRow = c.x;
						whiteCollum = c.y;
						maxcSum = white[c.x][c.y][0] + white[c.x][c.y][1]+ white[c.x][c.y][2]+ white[c.x][c.y][3]+ white[c.x][c.y][4]+ white[c.x][c.y][5];
					}
				}
			}
		}
		
		for (To3ChessWay way : blackWays) {
			Coordinate c =way.end;
			for (int k = 0; k < 6; k++) { //记录该路线中end点的权值并筛选出最大值
				if (black[c.x][c.y][k] > maxpValue) {
					blackRow = c.x;
					blackCollum = c.y;
					maxpValue = black[c.x][c.y][k];
					maxpSum = black[c.x][c.y][0] + black[c.x][c.y][1]+ black[c.x][c.y][2]+ black[c.x][c.y][3]+ black[c.x][c.y][4]+ black[c.x][c.y][5];
				}
				if (black[c.x][c.y][k] == maxpValue) {
					if (maxpSum < (black[c.x][c.y][0] + black[c.x][c.y][1]+ black[c.x][c.y][2]+ black[c.x][c.y][3]+ black[c.x][c.y][4]+ black[c.x][c.y][5])) {
						blackRow = c.x;
						blackCollum = c.y;
						maxpSum = black[c.x][c.y][0] + black[c.x][c.y][1]+ black[c.x][c.y][2]+ black[c.x][c.y][3]+ black[c.x][c.y][4]+ black[c.x][c.y][5];
					}
				}
			}
		}
		
//		   如果当前黑子可走路线中，end点权值最高的点 maxBlack >maxWhite 并且 该end点在白子路线中
//			包含，则白子应选择end点为终点,不在，则
		  Log.d(TAG, "whiteWays"+whiteWays.size()+"maxcValue :"+maxcValue+"White["+whiteRow+"]["+whiteCollum+"]" +"\n>>"+"blackWays"+blackWays.size()+"maxpValue:"+maxpValue+"Black["+blackRow+"]["+blackCollum+"]");
		if (maxcValue >maxpValue) {
			if (!isWaysContances(whiteRow,whiteCollum)){
				whiteChessMove(blackRow, blackCollum);
			}
		}else  {
			whiteChessMove(blackRow, blackCollum);
		}
		return way.start;
	}

	private void whiteChessMove(int blackRow, int blackCollum) {
//		首先：在包含的情况中有三还要控制成三
		if (!isWaysContances(blackRow,blackCollum)) {//黑方想要落的点没有白子则，随机选择一条路线
			if (whiteWays.size()>0) {
				way=whiteWays.get(new Random().nextInt(whiteWays.size()));
			}else {
//					无路可走了，游戏结束，我输了
				way =new To3ChessWay();
			}
		}
	}
	/**
	 * 判定黑方是否有子可动
	 * @return
	 */
	public boolean isBlackHasWay() {
		if(blackWays.size()>0){
			return true;
		}else {
			return false;
		}
	}

	/*private void chooseWhiteWay(int whiteRow, int whiteCollum) {
		for (To3ChessWay w : whiteWays) {
			if (w.end.x==whiteRow && w.end.y==whiteCollum) {
				this.way= w;
			}
		}
	}*/

	private boolean isWaysContances(int blackRow, int blackCollum) {
		boolean flag= false;
		for (To3ChessWay way : whiteWays) {
			if (way.end.x==blackRow && way.end.y==blackCollum) {
//				有白子的成三情况下要优先成三
				if (game.clearChess(way.start)) {
					//假设该点移动一步，判断是否成三
					game.setChess(way.end, game.WHITE);
					if (game.isThree(way.end.x, way.end.y, way.start.type)) {
						//记录该路线，即为所求
						this.way =way;
						chessRestore(way);
						Log.d(TAG, "isToThree_____________"+"true");
						return true;
					}else { //不成三,则还原该点 没有成三的情况发生则 选择其中一条路
						//到这里是模拟走了一步  且是走的目标是当前棋局的最大权值点
						chessRestore(way);
						this.way=way;
						Log.d(TAG, "start("+way.start.x+"，"+way.start.y+"，"+way.start.type+")"+game.WHITE);
						flag =true;
					}
				}
			}
		}
		return flag;
	}
	/**
	 * 棋子还原
	 * @param way
	 */
	private void chessRestore(To3ChessWay way) {
		game.restoreChess(way.start);
		game.clearChess(way.end,game.WHITE);
	}

	private void findWays(To3Game game, int[][] map) {
//		1、根据对黑子力分布，更新白方权值，分析白方可移动的子力
//		为每一个可移动路线做评估，分析可移动的白子，每个白子周围空点位数，对应的就是路线数
//		目标：分析判断出最优路线（其结果是：有利于更快到达权值最大的点）
		whiteWays.clear();
		blackWays.clear();
		for (Coordinate c : gamePoints) {
			if (map[c.x][c.y] !=0) {
				c.type =map[c.x][c.y];
//				该白子周围空点位有：
//				获取该点附近的点
				for (Coordinate co : gamePoints) {
					if (map[co.x][co.y] ==0) {
						if(game.isNearBy(c,co) || game.exChangePoint(c,co)) {
//							Log.d(TAG, "start["+c.x+"]["+c.y+"]"+"--- end["+co.x+"]["+co.y+"]");
							if (c.type ==To3Game.WHITE) {
								whiteWays.add(new To3ChessWay(c, co));
							}else {
								blackWays.add(new To3ChessWay(c, co));
							}
						}
					}
				}
			}
			
		}
	}
	
	/**
	 * @param start 地图中的点
	 * @return  start相邻的点的集合
	 */
	private List<Coordinate> getNearByPoint(Coordinate s) {
		List<Coordinate> list=new ArrayList<Coordinate>();
		if (s.x==1 && s.y==1) {
			list.add(new Coordinate(1, 7));
			list.add(new Coordinate(7, 1));
		}
		
		return list;
	}

	/**
	 * 动子时的结束点
	 */
	public Coordinate moveEnd(int[][] map) {
		return  way.end;
	}

	
}
