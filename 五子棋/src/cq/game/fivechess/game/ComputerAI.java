package cq.game.fivechess.game;

import android.util.Log;

/**
 * 
 * @author Flsolate
 * @date 2016-9-16
 * @description  封装的算法，可理解为电脑的脑袋。用假设分析法比较好理解。
 */
public class ComputerAI {
	private static final String TAG = "ComputerAI";

    public static final int HOR = 1;
    public static final int VER = 2;
    public static final int HOR_VER = 3;
    public static final int VER_HOR = 4;
    
//    private static final int FIVE = 100; // 活五
//    private static final int L_FOUR = 90; // 活四
//    private static final int D_FOUR = 100; // 死四
    
    private int mWidth = 0;
    private int mHeight = 0;
    
    // Black chess priority value array
    int[][][] black = null;
    // white chess priority value array
    int[][][] white = null;
    
    // the value of position which has different performance
    // 五子棋中的各个点的权值，x递增连续出现四个对方子时权值就达到最大了最后两列可去掉，
//    y递减出现己方一子时，最大到第2行最后一行可去掉。
    int[][] plaValue = {{2,6,173,212,250,250,250},
    					{0,5,7,200,230,231,231},
                        {0,0,0,0,230,230,230,0}};
    //玩家权值每每大于电脑，说明同种棋势下电脑偏向进攻
    int[][] cpuValue = {{0,3,166,186,229,229,229}, 
    					{0,0,5,167,220,220,220},
                        {0,0,0,0,220,220,220,0}};
    
    public ComputerAI(int width, int height) {
        mWidth = width;
        mHeight = height;
      //从 纵向、反斜线、横向、正斜线、综合5个维度来考量。
        black = new int[width][height][5];
        white = new int[width][height][5];
//        for(int i = 0 ; i < width ; i++){
//            for(int j = 0; j < height; j++){
//                black[i][j] = new Chess();
//                white[i][j] = new Chess();
//            }
//        }
    }
    
    /**
     * 更新棋盘权值
     * @param game
     * 此处应该是他后添加
     */
    public void updateValue(Game game){
        int[][] map = game.getChessMap();
        updateValue(map);
    }
    
    /**
     * 更新棋盘权值
     * 猜测：计算电脑的权值侧重，以图利于电脑方的权重。
     * 
     *  编程思想：
     *  这是一个互相博弈的过程，博弈论算法。和玩家想要达成的最大利益相违背。
     *  总之就是：玩家棋势偏进攻，就防守。玩家棋势偏防守，就进攻。
     */
    public void updateValue(int[][] map)
    {
        //四个值分别代表 纵向、反斜线、横向、正斜线
        int[] computerValue = {0,0,0,0};
        int[] playerValue = {0,0,0,0};
        for(int i = 0; i < mWidth; i ++)
        {
            for(int j = 0; j < mHeight; j ++)
            {
            	/**
            	 * 无子位置。
            	 */
                if(map[i][j] == 0)
                {
                    int counter = 0;
                    // 对不同的情况给与不同的权值,分析每个点子力情况来给该点一个权值
                    // 纵向  
                    /**
                     * 以纵向为例，取天元为黑子，天元下为白子，j=9来计算纵向权值,cpuValue[1][1]=0
                     * 分析该点的上下子力分布情况。最终纵向权值为5
                     */
//                    该点下半部分点位
                    for(int k = j + 1; k < mHeight; k ++)
                    {
                        if(map[i][k] == Game.BLACK)//黑子,没有跳出循环
                            computerValue[0] ++;
                        if(map[i][k] == 0) //一旦出现无子则跳出
                            break;
                        if(map[i][k] == Game.WHITE)//白子
                        {
                            counter ++;
                            break;
                        }
                        if(k == mHeight-1) //在最后一个位置有子且是黑子时被调用。边界相当于自动堵了一颗白子
                            counter ++;
                    }
                    //该点上半部分点位
                    for(int k = j - 1; k >= 0; k --)
                    {
                        if(map[i][k] == Game.BLACK)
                            computerValue[0] ++;
                        if(map[i][k] == 0)
                            break;
                        if(map[i][k] == Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
                        if(k == 0)
                            counter ++;
                    }
                    //无子位置在第一行和最后一行（最后一个位置无子时），counter++ ，那么黑子在该相对应的边界上时counter不可能++；
//                    而在j+1即第二或倒数第二行为白子时counter++，出现这种情况则counter++了两次。
//                    而在j+1即第二或倒数第二行为黑子时counter++ 一次。
                    if(j == 0 || j == mHeight-1)
                        counter ++;//counter++权值范围降低一级，注：这里实际作用影响不大。
                    
                    //counter自加两次后则 white[i][j][0] =0，白子权值降到最低，不需要防御反之
//                    玩家需要防御，那么电脑在落子的时候就要取玩家想要落子的权值最大的点来进攻
//                    这是一个互相博弈的过程，博弈论算法。和玩家想要达成的最大利益相违背。
//                    总之就是：玩家棋势偏进攻，就防守。玩家棋势偏防守，就进攻。
                    white[i][j][0] = cpuValue[counter][computerValue[0]]; //赋予cpuValue中的权值
                    computerValue[0] = 0;
                    counter = 0;
                    //1、纵向两黑一白连续，权值5。
//                    2、纵向两黑连续，权值166。
//                    3、纵向三黑连续，权值186。
//                    4、纵向四黑一白连续，权值220。
//                    5、纵向两黑中间隔两空，权值3。
//                    6、纵向四黑连续，权值229。
//                    总结：根据黑子在纵向上出现的情况给各个点位打分。分高越危险，白子则堵之。
//                    Log.d(TAG, "white["+i+"]["+j+"][0] :"+white[i][j][0] );
                    // 反斜线
                    for(int k = i + 1, l = j + 1; l < mHeight; k ++, l ++)
                    {   
                        if(k >= mHeight)
                        {
                            break;
                        }
                        if(map[k][l] == Game.BLACK)
                            computerValue[1] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
                        if(k == mWidth-1 || l == mHeight-1)
                            counter ++;
                    }
                
                    for(int k = i - 1, l = j - 1; l >= 0; k --, l --)
                    {
                        if(k < 0)
                        {
                            break;
                        }
                        if(map[k][l] == Game.BLACK)
                            computerValue[1] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
                        if(k == 0 || l == 0)
                            counter ++;
                        
                    }
                    if(i == 0 || i == mWidth-1 || j == 0 || j == mHeight-1)
                        counter ++;
                    
                    white[i][j][1] = cpuValue[counter][computerValue[1]];
                    computerValue[1] = 0;
                    counter = 0;
                    
                    // 横向
                    for(int k = i + 1; k < mWidth; k ++)
                    {
                    
                        if(map[k][j] == Game.BLACK)
                            computerValue[2] ++;
                        if(map[k][j] == 0)
                            break;
                        if(map[k][j] == Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
                        if(k == mWidth-1)
                            counter ++;
                    }
                    
                    
                    for(int k = i - 1; k >= 0; k --)
                    {
                        
                        if(map[k][j] == Game.BLACK)
                            computerValue[2] ++;
                        if(map[k][j] == 0)
                            break;
                        if(map[k][j] == Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
                        if(k == 0)
                            counter ++;
                    }
                    
                    if(i == 0 || i == mWidth-1)
                        counter ++;
                    white[i][j][2] = cpuValue[counter][computerValue[2]];
                    computerValue[2] = 0;
                    counter = 0;
                    
                    // 正斜线
                    for(int k = i - 1, l = j + 1; l < mWidth; k --, l ++)
                    {
                        
                        if(k < 0)
                        {
                            break;
                        }
                        if(map[k][l] == Game.BLACK)
                            computerValue[3] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
                        if(k ==0 || l == mHeight-1)
                            counter ++;
                        
                    }
                    
                    
                    for(int k = i + 1, l = j - 1; l >= 0; k ++, l --)
                    {
                        
                        if(k >= mWidth)
                        {
                            break;
                        }
                        if(map[k][l] == Game.BLACK)
                            computerValue[3] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.WHITE)
                        {
                            counter ++;
                            break;
                        }
                        if(k == mWidth-1 || l == 0)
                            counter ++;
                        
                    }
                    if(i == 0 || i == mWidth-1 || j == 0 || j == mHeight-1)
                        counter ++;
                    white[i][j][3] = cpuValue[counter][computerValue[3]];
                    computerValue[3] = 0;
                    counter = 0;
                    
                    // 遍历空子中每一个点四个方向上的权值。......................................自己更改了
                    for(int k = 0; k < 4; k ++)
                    {   
                    	//四个方向上黑子两子连续的情况。
                        if(white[i][j][k] == 166)
                            counter ++;
                    }
                    //两个方向以上有两黑连续，提升权值。
                    if(counter >= 2 && white[i][j][4] < 174)
                        white[i][j][4] = 174;
                    counter = 0;
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        for(int l = 0; l < 4; l ++)
                        {
                        	//黑子有两子连续也有三子连续，提升权值。
                            if(white[i][j][k] == 166 && white[i][j][l] == 167
                                && white[i][j][4] < 176)
                                white[i][j][4] = 176;
                        }
                    }
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        for(int l = 0; l < 4; l ++)
                        {
                            if(white[i][j][k] == 166 && white[i][j][l] == 186
                                && white[i][j][4] < 177)
                                white[i][j][4] = 177;
                        }
                    }
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        if(white[i][j][k] == 167)
                            counter ++;
                    }
                    if(counter >= 2 && white[i][j][4] < 178)
                        white[i][j][4] = 178;
                    counter = 0;
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        for(int l = 0; l < 4; l ++)
                        {
                            if(white[i][j][k] == 167 && white[i][j][l] == 186
                                && white[i][j][4] < 179)
                                white[i][j][4] = 179;
                        }
                    }
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        if(white[i][j][k] == 186)
                            counter ++;
                    }
                    if(counter >= 2 && white[i][j][4] < 180)
                    	white[i][j][4] = 180;
                    counter = 0;
                    
//                  难点:分析该点空子且周围8个点中有为空子时，的权值情况。存入white[i][j][4]中。
                    if(j >= 1)
                    {
                        if(map[i][j-1] == 0)
                        {   //该点纵向上方一空点有两黑以上连续
                            if(white[i][j-1][0] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
//                                	该点又有两黑以上连续。则提升权值。
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i][j-1][0] == 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                            //该点纵向上方一空点纵向两子连续
                         /*   if(white[i][j-1][0] == 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {   //该点四个方向有两子连续的情况。降低权值。有三有二的情况呢？？//已更改
                                //                     或者：  if(white[i][j][k] > 173)break;
                                    if(white[i][j][k] == 173)
                                    {
                                    	//权值已经被提升了的。
                                        if(white[i][j][4] == 201)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                        }
                        
                    }
                    
                    if(j >= 1 && i >= 1)
                    {
                        if(map[i-1][j-1] == 0)
                        { //该点左上角一空点有两黑以上连续
                            if(white[i-1][j-1][1] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i-1][j-1][1] == 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        /*    if(white[i-1][j-1][1] == 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] == 166)
                                    {
                                        if(white[i][j][4] == 176)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                        }
                    }
                    
                    if(i >= 1)
                    {
                        if(map[i-1][j] == 0)
                        {
                            if(white[i-1][j][2] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i-1][j][2] == 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        /*    if(white[i-1][j][2] == 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] == 166)
                                    {
                                        if(white[i][j][4] == 176)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                        }
                    }
                    
                    if(i > 0 && j < mHeight-1)
                    {
                        if(map[i-1][j+1] == 0)
                        {
                            if(white[i-1][j+1][3] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i-1][j+1][3] == 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                          /*  if(white[i-1][j+1][3] == 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] == 166)
                                    {
                                        if(white[i][j][4] == 176)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                        }
                    }
                    
                    if(j < mHeight-1)
                    {
                        if(map[i][j+1] == 0)
                        {
                            if(white[i][j+1][0] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i][j+1][0] == 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        /*    if(white[i][j+1][0] == 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] == 166)
                                    {
                                        if(white[i][j][4] == 176)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                            
                        }
                    }
                    
                    if(i < mWidth-1 && j < mHeight-1)
                    {
                        if(map[i+1][j+1] == 0)
                        {
                            if(white[i+1][j+1][1] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i+1][j+1][1] == 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                         /*   if(white[i+1][j+1][1] == 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] == 166)
                                    {
                                        if(white[i][j][4] == 176)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                        }
                    }
                    
                    if(i < mWidth-1)
                    {
                        if(map[i+1][j] == 0)
                        {
                            if(white[i+1][j][2] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i+1][j][2] == 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                          /*  if(white[i+1][j][2] == 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] == 166)
                                    {
                                        if(white[i][j][4] == 176)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                        }
                    }
                    
                    if(i < mWidth-1 && j > 0)
                    {
                        if(map[i+1][j-1] == 0)
                        {
                            if(white[i+1][j-1][3] >= 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] >= 166)
                                    {
                                    	if (white[i+1][j-1][3]== 166 && white[i][j][k] == 166) continue;
                                        if(white[i][j][4] < 176)
                                        {
                                            white[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        /*    if(white[i+1][j-1][3] == 166)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(white[i][j][k] == 166)
                                    {
                                        if(white[i][j][4] == 176)
                                        {
                                            white[i][j][4] = 175;
                                        }
                                    }
                                }
                            }*/
                            
                        }
                    }
                    
                    Log.d(TAG, "white["+i+"]["+j+"][4] :"+white[i][j][4] );
                    
                }
                
            }
        }
        //分析黑子的权值
        for(int i = 0; i < mWidth; i ++)
        {
            for(int j = 0; j < mHeight; j ++)
            {
                if(map[i][j] == 0)
                {
                    int counter = 0;
                    for(int k = j + 1; k < mHeight; k ++)
                    {
                            
                        if(map[i][k] == Game.WHITE)
                            playerValue[0] ++;
                        if(map[i][k] == 0)
                            break;
                        if(map[i][k] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == mHeight-1)
                            counter ++;
                    }
                    
                    
                    for(int k = j - 1; k >= 0; k --)
                    {
                            
                        if(map[i][k] == Game.WHITE)
                            playerValue[0] ++;
                        if(map[i][k] == 0)
                            break;
                        if(map[i][k] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == 0)
                            counter ++;
                    }
                    if(j == 0 || j == mHeight-1)
                        counter ++;
                    black[i][j][0] = plaValue[counter][playerValue[0]];
                    playerValue[0] = 0;
                    counter = 0;
                    
                    for(int k = i + 1, l = j + 1; l < mHeight; k ++, l ++)
                    {   
                        if(k >= mWidth)
                        {
                            break;
                        }
                        if(map[k][l] == Game.WHITE)
                            playerValue[1] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == mWidth-1 || l == mHeight-1)
                            counter ++;
                        
                    }
                    
                
                    for(int k = i - 1, l = j - 1; l >= 0; k --, l --)
                    {
                            
                        if(k < 0)
                        {
                            break;
                        }
                        if(map[k][l] == Game.WHITE)
                            playerValue[1] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == 0 || l == 0)
                            counter ++;
                        
                    }
                    if(i == 0 || i == mWidth-1 || j == 0 || j == mHeight-1)
                        counter ++;
                    black[i][j][1] = plaValue[counter][playerValue[1]];
                    playerValue[1] = 0;
                    counter = 0;
                    
                    for(int k = i + 1; k < mWidth; k ++)
                    {
                    
                        if(map[k][j] == Game.WHITE)
                            playerValue[2] ++;
                        if(map[k][j] == 0)
                            break;
                        if(map[k][j] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == mWidth-1)
                            counter ++;
                    }
                    
                    
                    for(int k = i - 1; k >= 0; k --)
                    {
                        
                        if(map[k][j] == Game.WHITE)
                            playerValue[2] ++;
                        if(map[k][j] == 0)
                            break;
                        if(map[k][j] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == 0)
                            counter ++;
                    }
                    if(i == 0 || i == mWidth-1)
                        counter ++;
                    black[i][j][2] = plaValue[counter][playerValue[2]];
                    playerValue[2] = 0;
                    counter = 0;
                    
                    for(int k = i - 1, l = j + 1; l < mHeight; k --, l ++)
                    {
                        
                        if(k < 0)
                        {
                            break;
                        }
                        if(map[k][l] == Game.WHITE)
                            playerValue[3] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == 0 || l == mHeight-1)
                            counter ++;
                        
                    }
                    
                    
                    for(int k = i + 1, l = j - 1; l >= 0; k ++, l --)
                    {
                        
                        if(k >= mWidth)
                        {
                            break;
                        }
                        if(map[k][l] == Game.WHITE)
                            playerValue[3] ++;
                        if(map[k][l] == 0)
                            break;
                        if(map[k][l] == Game.BLACK)
                        {
                            counter ++;
                            break;
                        }
                        if(k == mWidth-1 || l ==0)
                            counter ++;
                        
                    }
                    if(i == 0 || i == mWidth-1 || j == 0 || j == mHeight-1)
                        counter ++;
                    black[i][j][3] = plaValue[counter][playerValue[3]];
                    playerValue[3] = 0;
                    counter = 0;
                    
                  //两个方向上分析权值。
                    for(int k = 0; k < 4; k ++)
                    {
                        if(black[i][j][k] == 173)
                            counter ++;
                    }
                    if(counter >= 2 && black[i][j][4] < 175)
                    {
                        black[i][j][4] = 175;
                        
                    }
                    counter = 0;
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        for(int l = 0; l < 4; l ++)
                        {
                            if(black[i][j][k] == 173 && black[i][j][l] == 200
                                && black[i][j][4] < 176)
                                black[i][j][4] = 176;
                        }
                    }
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        for(int l = 0; l < 4; l ++)
                        {
                            if(black[i][j][k] == 173 && black[i][j][l] == 212
                                && black[i][j][4] < 177)
                                black[i][j][4] = 177;
                        }
                    }
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        if(black[i][j][k] == 200)
                            counter ++;
                    }
                    if(counter >= 2 && black[i][j][4] < 178)
                        black[i][j][4] = 178;
                    counter = 0;
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        for(int l = 0; l < 4; l ++)
                        {
                            if(black[i][j][k] == 200 && black[i][j][l] == 212
                                && black[i][j][4] < 179)
                                black[i][j][4] = 179;
                        }
                    }
                    
                    for(int k = 0; k < 4; k ++)
                    {
                        if(black[i][j][k] == 212)
                            counter ++;
                    }
                    if(counter >= 2 && black[i][j][4] < 180)
                        black[i][j][4] = 180;
                    counter = 0;
                    
                    if(j >= 1)
                    {
                        if(map[i][j-1] == 0)
                        {
                            if(black[i][j-1][0] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (white[i][j-1][0] == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                        
                    }
                    
                    if(j >= 1 && i >= 1)
                    {
                        if(map[i-1][j-1] == 0)
                        {
                            if(black[i-1][j-1][1] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (white[i-1][j-1][1] == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(i >= 1)
                    {
                        if(map[i-1][j] == 0)
                        {
                            if(black[i-1][j][2] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (black[i-1][j][2] == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(i > 0 && j < mHeight-1)
                    {
                        if(map[i-1][j+1] == 0)
                        {
                            if(black[i-1][j+1][3] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (black[i-1][j+1][3]  == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(j < mHeight-1)
                    {
                        if(map[i][j+1] == 0)
                        {
                            if(black[i][j+1][0] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (black[i][j+1][0] == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(i < mWidth-1 && j < mHeight-1)
                    {
                        if(map[i+1][j+1] == 0)
                        {
                            if(black[i+1][j+1][1] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (black[i+1][j+1][1] == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(i < mWidth-1)
                    {
                        if(map[i+1][j] == 0)
                        {
                            if(black[i+1][j][2] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (black[i+1][j][2]  == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if(i < mWidth-1 && j > 0)
                    {
                        if(map[i+1][j-1] == 0)
                        {
                            if(black[i+1][j-1][3] >= 173)
                            {
                                for(int k = 0; k < 4; k ++)
                                {
                                    if(black[i][j][k] >= 173)
                                    {
                                    	if (black[i+1][j-1][3] == 173 && white[i][j][k] == 173) continue;
                                        if(black[i][j][4] < 176)
                                        {
                                            black[i][j][4] = 176;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
        }
    }
    
    public Coordinate getPosition(int[][] map)
    {
        int maxpSum = 0;
        int maxcSum = 0;
        
        int maxpValue = -10;
        int maxcValue = -10;
        int blackRow = 0; 
        int blackCollum = 0;
        int whiteRow = 0; 
        int whiteCollum = 0;
        for(int i = 0; i < mWidth; i ++)
        {
            for(int j = 0; j < mHeight; j ++)
            {
                if(map[i][j] == 0)
                {
                    for(int k = 0; k < 4; k ++)//遍历四个方向，横、竖、正斜线、反斜线、和一综合
                    {
                        if(black[i][j][k] > maxpValue)
                        {
                            blackRow = i;
                            blackCollum = j;
                            maxpValue = black[i][j][k];
                            maxpSum = black[i][j][0] + black[i][j][1]
                                + black[i][j][2] + black[i][j][3] ;
                        }
                        
                        // if the value if equal, check the sum of the value
//                        出现两个点位以上的最大权值相等的情况，则取四个方向上四条线权值和最大的点。
                        if(black[i][j][k] == maxpValue)
                        {
                                if(maxpSum < (black[i][j][0] + black[i][j][1]
                                    + black[i][j][2] + black[i][j][3]))
                                    {
                                        blackRow = i;
                                        blackCollum = j;
                                        maxpSum = black[i][j][0] + black[i][j][1]
                                            + black[i][j][2] + black[i][j][3];        
                                    }   
                        }
                        
                        if(white[i][j][k] > maxcValue)
                        {
                            whiteRow = i;
                            whiteCollum = j;
                            maxcValue = white[i][j][k];
                            maxcSum = white[i][j][0] + white[i][j][1]
                                            + white[i][j][2] + white[i][j][3];  // black[i][j][0] 改white[i][j][0] 
                                
                        }   
                        
                        if(white[i][j][k] == maxcValue)
                        {
                                if(maxcSum < (white[i][j][0] + white[i][j][1]
                                    + white[i][j][2] + white[i][j][3]))
                                    {
                                        whiteRow = i;
                                        whiteCollum = j;
                                        maxcSum = white[i][j][0] + white[i][j][1]
                                            + white[i][j][2] + white[i][j][3];    // black[i][j][0] 改white[i][j][0] 
                                    }   
                        }
                        
                    }
                }
                
            }
        }
        Coordinate c = new Coordinate();
        //电脑最大权值大于玩家最大权值，则电脑防御，看来，权值越大的点，越利于防御。
        if(maxcValue > maxpValue){   
            c.x = whiteRow;
            c.y = whiteCollum; 
//            Log.d(TAG, " maxcValue:"+maxcValue+" whiteRow:"+whiteRow+" whiteCollum:"+whiteCollum);
        }
        else//反之，则攻击？？？，下玩家想要下（防守）的权值最高的点位。
        {
            c.x = blackRow; 
            c.y = blackCollum;
//            Log.d(TAG, " blackRow:"+blackRow+" blackCollum:"+blackCollum);
        }
//        Log.d("cuiqing", "x="+c.x+" y="+c.y);
//        for(int i = 0; i < mWidth; i ++)
//        {
//            for(int j = 0; j < mHeight; j ++)
//            {
//                Log.d("cuiqing", "black="+Arrays.toString(black[i][j]));
//                Log.d("cuiqing", "white="+Arrays.toString(white[i][j]));
//            
//            }
//        }
        return c;
    }
}
