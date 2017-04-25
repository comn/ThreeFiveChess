package com.yang.chess.game;

/**
 * 坐标类
 * @author cuiqing
 */
public class Coordinate {
    public int x;
    public int y;
    public int type;

    public Coordinate(){
        
    }
    
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Coordinate(int x, int y,int type) {
        this.x = x;
        this.y = y;
        this.type =type;
    }
    
    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }

}
