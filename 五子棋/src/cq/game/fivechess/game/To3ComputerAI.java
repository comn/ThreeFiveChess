package cq.game.fivechess.game;

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
 * @description 成三的电脑算法    分析数据、按照人大脑中的思维来模拟过程,所以人脑工程的研究能促进人工智能的发展
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
     * 分两步：白方落子则遍历棋盘每个无子点，判断该无子点周围子力分布情况
     * 反之则 黑方 假想其为电脑方
     * 
     * 白棋：根据实时棋盘上黑棋分布情况，计算出每个可落点位的每个维度的权值
     * 黑棋：————白棋——————————————————————————————————————————————————
     * @param map
     */
//	把判断成三和isNearBy()结合做或许更好一点.
	private void updateValue(int[][] map) {
		//初步分两个维度：纵向、横向、
		//构想一个维度：奇招维度
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
					   if(map[c.x][1+i*6] == To3Game.WHITE)//白子
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
					   if(map[c.x][13-6*i] == To3Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==1 || c.x==13) {
					if (c.y==7) {
					/*  if (map[c.x][1] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) { 
							// 此处两个点都为黑则只computerValue[0] ++一次
							computerValue[0] ++;
							if (map[c.x][1] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) {
							counter++;
						}*/
					  
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
							if(map[c.x][1+2*i] == To3Game.WHITE)//白子
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
							if(map[c.x][13-2*i] == To3Game.WHITE)//白子
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
//                Log.d(TAG, "外层纵向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
                
//                问题分析：这里面的 white[c.x][c.y][0]在中层和内层又重新赋值，层次没有做区分导致最后的0的问题，
//                层次对权值大小没有影响，只是作为一个标志，以示区分,不同层次的权值也要做大小比较，方便判断
//				可用0,1,2代表外中内纵向，3,4,5代表横向,并且可行 实现了该点在不同层次不同方向（横、纵）上的权值大小的直观反映
                
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
					   if(map[c.x][3+i*4] == To3Game.WHITE)//白子
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
					   if(map[c.x][11-4*i] == To3Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==3 || c.x==11) {
					if (c.y==7) {
					/*  if (map[c.x][3] ==To3Game.BLACK || map[c.x][11] ==To3Game.BLACK) {
							computerValue[0] ++;
							if (map[c.x][3] ==To3Game.BLACK && map[c.x][11] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][3] ==To3Game.WHITE || map[c.x][11] ==To3Game.WHITE) {
							counter++;
						}*/
					  
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
					/*  if (map[c.x][1] ==To3Game.BLACK || map[c.x][5] ==To3Game.BLACK) {
							computerValue[0] ++;
							if (map[c.x][1] ==To3Game.BLACK && map[c.x][5] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.WHITE || map[c.x][5] ==To3Game.WHITE) {
							counter++;
						}*/
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
					/* 	if (map[c.x][9] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) {
							computerValue[0] ++;
							if (map[c.x][9] ==To3Game.BLACK && map[c.x][13] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][9] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) {
							counter++;
						}*/
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
			    white[c.x][c.y][1] = cpuValue[counter][computerValue[0]]; //赋予cpuValue中的权值
                computerValue[0] = 0;
                counter = 0;
				
//				Log.d(TAG, "中层纵向：white[+"+c.x+"]["+c.y+"][0]"+white[c.x][c.y][0]);
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
					   if(map[c.x][5+i*2] == To3Game.WHITE)//白子
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
					   if(map[c.x][9-2*i] == To3Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==5 || c.x==9) {
					if (c.y==7) {
					/* 	if (map[c.x][5] ==To3Game.BLACK || map[c.x][9] ==To3Game.BLACK) { 
							// 此处两个点都为黑则只computerValue[0] ++一次
							computerValue[0] ++;
							if (map[c.x][5] ==To3Game.BLACK && map[c.x][9] ==To3Game.BLACK) {
								computerValue[0] ++;
							}
						}else if (map[c.x][5] ==To3Game.WHITE || map[c.x][9] ==To3Game.WHITE) {
							counter++;
						}*/
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
							if(map[c.x][5-2*i] == To3Game.WHITE)//白子
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
							if(map[c.x][9+2*i] == To3Game.WHITE)//白子
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
							if(map[1+2*i][c.y] == To3Game.WHITE)//白子
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
							if(map[13-2*i][c.y] == To3Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==1 || c.y==13) {
					/*	if (map[1][c.y] ==To3Game.BLACK || map[13][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[1][c.y] ==To3Game.BLACK && map[13][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[1][c.y]  ==To3Game.WHITE || map[13][c.y]  ==To3Game.WHITE) {
							counter++;
						}*/
						
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
					   if(map[1+i*6][c.y] == To3Game.WHITE)//白子
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
					   if(map[13-6*i][c.y] == To3Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][3] = cpuValue[counter][computerValue[1]]; //赋予cpuValue中的权值
                computerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......外层横向：white["+c.x+"]["+c.y+"][1]"+white[c.x][c.y][1]);
                
                
//            	中层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==3 ) {
					  /*	if (map[1][c.y] ==To3Game.BLACK || map[5][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[1][c.y] ==To3Game.BLACK && map[5][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[1][c.y] ==To3Game.WHITE || map[5][c.y]  ==To3Game.WHITE) {
							counter++;
						}*/
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
					/* 	if (map[13][c.y] ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[13][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[13][c.y] ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							counter++;
						}*/
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
					/*  if (map[3][c.y] ==To3Game.BLACK || map[11][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[3][c.y] ==To3Game.BLACK && map[11][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[3][c.y]  ==To3Game.WHITE || map[11][c.y]  ==To3Game.WHITE) {
							counter++;
						}*/
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
					   if(map[3+i*4][c.y] == To3Game.WHITE)//白子
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
					   if(map[11-4*i][c.y] == To3Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				white[c.x][c.y][4] = cpuValue[counter][computerValue[1]]; //赋予cpuValue中的权值
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
							if(map[5-2*i][c.y] == To3Game.WHITE)//白子
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
							if(map[9+2*i][c.y] == To3Game.WHITE)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==5 || c.y==9) {
					/*	if (map[5][c.y] ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							computerValue[1] ++;
							if (map[5][c.y] ==To3Game.BLACK && map[9][c.y]  ==To3Game.BLACK) {
								computerValue[1] ++;
							}
						}else if (map[5][c.y]  ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							counter++;
						}*/
						
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
					   if(map[5+i*2][c.y] == To3Game.WHITE)//白子
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
					   if(map[9-2*i][c.y] == To3Game.WHITE)//白子
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
					   if(map[c.x][1+i*6] == To3Game.BLACK)//白子
                        {
                            counter ++;//跳往第二层
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
					   if(map[c.x][13-6*i] == To3Game.BLACK)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==1 || c.x==13) {
					if (c.y==7) {
					/*  这种写法有BUG容易误判
					 * 	if (map[c.x][1] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) { 
							// 此处两个点都为黑则只playerValue[0] ++一次
							playerValue[0] ++;
							if (map[c.x][1] ==To3Game.WHITE && map[c.x][13] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) {
							counter++;
						}*/
						
//				每一个的这种地方都要进行针对的更改	 我就不细致去改了>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
							if(map[c.x][1+2*i] == To3Game.BLACK)//白子
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
							if(map[c.x][13-2*i] == To3Game.BLACK)//白子
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
						if (map[c.x][3+i*4] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
						  if(map[c.x][3+i*4]== 0)
	                            break;
					   if(map[c.x][3+i*4] == To3Game.BLACK)//白子
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
					   if(map[c.x][11-4*i] == To3Game.BLACK)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				
				//2、该空点在边上 的纵向
				if(c.x==3 || c.x==11) {
					if (c.y==7) {
					/*  if (map[c.x][3] ==To3Game.WHITE || map[c.x][11] ==To3Game.WHITE) {
							playerValue[0] ++;
							if (map[c.x][3] ==To3Game.WHITE && map[c.x][11] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
						}else if (map[c.x][3] ==To3Game.BLACK || map[c.x][11] ==To3Game.BLACK) {
							counter++;
						}*/
					  
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
					/*  if (map[c.x][1] ==To3Game.WHITE || map[c.x][5] ==To3Game.WHITE) {
							playerValue[0] ++;
							if (map[c.x][1] ==To3Game.WHITE && map[c.x][5] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
						}else if (map[c.x][1] ==To3Game.BLACK || map[c.x][5] ==To3Game.BLACK) {
							counter++;
						}*/
					  
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
					/*  if (map[c.x][9] ==To3Game.WHITE || map[c.x][13] ==To3Game.WHITE) {
							playerValue[0] ++;
							if (map[c.x][9] ==To3Game.WHITE && map[c.x][13] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
						}else if (map[c.x][9] ==To3Game.BLACK || map[c.x][13] ==To3Game.BLACK) {
							counter++;
						}*/
					  
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
			     //1、该空点在角上
			     //2、该空点在边上
				//内层纵向
				if ((c.x==5&&c.y==5) || (c.x==9&&c.y==5)) {
					for (int i = 1; i < 3; i++) {
						if (map[c.x][5+i*2] ==To3Game.WHITE) {
							playerValue[0] ++;
						}
						  if(map[c.x][5+i*2]== 0)
	                            break;
					   if(map[c.x][5+i*2] == To3Game.BLACK)//白子
                        {
                            counter ++;//跳往第二层
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
					/*	if (map[c.x][5] ==To3Game.WHITE || map[c.x][9] ==To3Game.WHITE) { 
							// 此处两个点都为黑则只playerValue[0] ++一次
							playerValue[0] ++;
							if (map[c.x][5] ==To3Game.WHITE && map[c.x][9] ==To3Game.WHITE) {
								playerValue[0] ++;
							}
						}else if (map[c.x][5] ==To3Game.BLACK || map[c.x][9] ==To3Game.BLACK) {
							counter++;
						}*/
						
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
			    black[c.x][c.y][2] = plaValue[counter][playerValue[0]]; //赋予cpuValue中的权值
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
							if(map[1+2*i][c.y] == To3Game.BLACK)//白子
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
							if(map[13-2*i][c.y] == To3Game.BLACK)//白子
							{
								counter ++;
								break;
							}
						}
					}
				}else if (c.x==7) {
					if (c.y==1 || c.y==13) {
				  /*	if (map[1][c.y] ==To3Game.WHITE || map[13][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							if (map[1][c.y] ==To3Game.WHITE && map[13][c.y]  ==To3Game.WHITE) {
								playerValue[1] ++;
							}
						}else if (map[1][c.y]  ==To3Game.BLACK || map[13][c.y]  ==To3Game.BLACK) {
							counter++;
						}*/
						
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
					   if(map[1+i*6][c.y] == To3Game.BLACK)//白子
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
					   if(map[13-6*i][c.y] == To3Game.BLACK)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][3] = plaValue[counter][playerValue[1]]; //赋予cpuValue中的权值
				playerValue[1] = 0;
                counter = 0;
//                Log.d(TAG, ".......外层横向：white["+c.x+"]["+c.y+"][0]"+white[c.x][c.y][1]);
                
                
//            	中层 该空点在边上 的横向
				if (c.y==7) {
					if(c.x==3 ) {
						/*if (map[1][c.y] ==To3Game.WHITE || map[5][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							if (map[1][c.y] ==To3Game.WHITE && map[5][c.y]  ==To3Game.WHITE) {
								playerValue[1] ++;
							}
						}else if (map[1][c.y] ==To3Game.BLACK || map[5][c.y]  ==To3Game.BLACK) {
							counter++;
						}*/
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
					/*	if (map[13][c.y] ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							if (map[13][c.y] ==To3Game.WHITE && map[9][c.y]  ==To3Game.WHITE) {
								playerValue[1] ++;
							}
						}else if (map[13][c.y] ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							counter++;
						}*/
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
					/*	if (map[3][c.y] ==To3Game.WHITE || map[11][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							if (map[3][c.y] ==To3Game.WHITE && map[11][c.y]  ==To3Game.WHITE) {
								playerValue[1] ++;
							}
						}else if (map[3][c.y]  ==To3Game.BLACK || map[11][c.y]  ==To3Game.BLACK) {
							counter++;
						}*/
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
					   if(map[3+i*4][c.y] == To3Game.BLACK)//白子
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
					   if(map[11-4*i][c.y] == To3Game.BLACK)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][4] = plaValue[counter][playerValue[1]]; //赋予cpuValue中的权值
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
					/*	if (map[5][c.y] ==To3Game.WHITE || map[9][c.y]  ==To3Game.WHITE) {
							playerValue[1] ++;
							if (map[5][c.y] ==To3Game.WHITE && map[9][c.y]  ==To3Game.WHITE) {
								playerValue[1] ++;
							}
						}else if (map[5][c.y]  ==To3Game.BLACK || map[9][c.y]  ==To3Game.BLACK) {
							counter++;
						}*/
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
					   if(map[9-2*i][c.y] == To3Game.BLACK)//白子
                        {
                            counter ++;
                            break;
                        }
					}
				}
				black[c.x][c.y][5] = plaValue[counter][playerValue[1]]; //赋予cpuValue中的权值
				playerValue[1] = 0;
                counter = 0;
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

//					为什么这里的值都是0? 是上面的updateValue权值分析方法没有做好?之所以出现这样的情况应该是外层之后到中层
//					内层又重新赋值了0了
//					Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>白棋权值：white["+c.x+"]["+c.y+"]["+k+"]"+white[c.x][c.y][k]);
					if (white[c.x][c.y][k] > maxcValue) { //这里面写成maxpValue导致的错误
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
//		判断对方有无两子连续  这里为优先级为最高，因为己方吃完就到对方手
//		有，吃其中一子
//		3、无，则任意吃非对方成三的子
		//1、有己方两子凉连续     吃堵住了己方三的对方子
//		2、无，则 吃堵住己方已经成三的子
		
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
		//  找到最大权值得点   
//				找到距离该点最近的路线进行子力移动
		 int blackRow = 0; 
	     int blackCollum = 0;
	     int whiteRow = 0; 
	     int whiteCollum = 0;
	     int maxpValue = -10;
	     int maxcValue = -10;
	     int maxpSum = 0;
	     int maxcSum = 0;
//		接下来就是电脑的移动问题了
		findWays(game, map);
//		白子走的是whiteWays中的线路
//		黑子走的是blackWays中的线路
//		要分析判断它们每一个路线的每一个维度的得分
		updateValue(game);//更新地图权值
//		这些end点里相对于白子、黑子方的判断，权值不同
//		白子走当前一步最好的点是，当前白子可走路线中，end点中权值最高的点 maxWhite
//		要在地图中除去该最好线路中开始点的子，即置为0，判断结束点权值大小， 因为是根据对方棋子分布判断所以这样没什么意义
//		否则       
		for (To3ChessWay way : whiteWays) {//		找白子最大权值点
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
			//白子的防守 判断该点周围是否有白子的路
			if (!isWaysContances(whiteRow,whiteCollum)){
				whiteChessMove(blackRow, blackCollum);
			}
		}else  {
			//包含，阻止黑子想要到往的路
			whiteChessMove(blackRow, blackCollum);
		}
//		电脑还是有点二
		
//		遍历所有的路线，找到权值最大的路线，点连成线，有利于最快到达权值最大的点的线路 权线值最大
//		1、先找到最大的权值点
		/*	updateValue(map);
			Coordinate maxPoint = getPosition(map);
			Coordinate maxWhite =new Coordinate(whiteRow, whiteCollum);
			Coordinate maxBlack =new Coordinate(blackRow, blackCollum);
//			拿白子最大权值点
//			拿黑子最大权值点
			
//		   2、找到能最快到达的线路            
//		如果对方到达最大权值的线路短于己方，则防守           
//			同等级的最大权值点出现时，以线路短作为进攻防守的依据（）
//			模拟几步白子黑子棋子移动后，棋局的整体变化
//				每移动一步就要updateValue(map);
//			1、先把目标放在当前能走的最好的一步路上；每走一步都有一个评分的，只有最终得分高的走法电脑才会选择
			if(maxcValue > maxpValue){   //防守 ，白行白列，白方的防守，相对应于白方则为防守
				
			}else {
			在这里面的思路有问题：故舍弃！
			 //进攻  黑行黑列，黑方的防守，相对应于白方则为进攻
				//maxWhite就是目标点，与之相关的有三条线路，怎么筛选？？
				for (To3ChessWay way : whiteWays) {
//						该权值最大的点周围有 己方子力 则way 能拿到值 (这里权值最大的点周围是有白子的，问题的
//					关键是：存不存在有权值最大点周围没有白子的情况)
					if (way.end.x==blackRow && way.end.y==blackCollum)
					{
						//循环每遍历一次，到这里面的线路就有至少两条
						if (game.clearChess(way.start)) {
							//假设该点移动一步，判断是否成三
							if (game.isThree(way.end.x, way.end.y, way.start.type)) {
								//记录该路线，即为所求
								this.way =way;
								break;
							}else { //不成三,则还原该点，还是记录way
								//到这里是模拟走了一步  且是走的目标是当前棋局的最大权值点
								game.restoreChess(way.start);
//								this.way =way;
							}
						}
					}
					
				}
			}
			
		if (way ==null) {
//			无路可走了，我输了
  			way =whiteWays.get(new Random().nextInt(whiteWays.size()));
		}*/
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
//						成三线路没有执行不知道为什么？
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
		//		还是博弈论 最终结果还是开发者智力的提现
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
				
			/*	方式一：麻烦，优点，可节省cup性能
			 * List<Coordinate> npList = getNearByPoint(c);
				for (Coordinate co : npList) {
					//在地图上为空的点
					if (map[co.x][co.y] ==0) {
//						存在，则c可作为起始点，co可作为结束点，它们可构成一条路线
//						这里面只能拿到最后遍历的路线，作为起止点
//						要做模拟几步棋子移动后的情况分析，才可了解哪一个路线为当前最优选择
						chessWays.add(new To3ChessWay(c, co));
					}
				}*/
				
				// 方式二：稍微消耗点cpu性能
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
