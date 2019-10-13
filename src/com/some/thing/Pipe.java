package com.some.thing;

import java.awt.*;

public class Pipe {

    private int y = 0, x = 500;
    public static int opening = 175;
    public static int pipe_width = 80;

    public Pipe(int y){
      this.y = y;
    }

    public void render(Graphics2D g){
        g.setColor(Color.GREEN);
        g.fillRect(x,0 , pipe_width , y);
        g.fillRect(x, y+opening ,pipe_width , 600-y-opening);
    }

    public void update(){
        x += -2;
    }
    public int getY(){
        return y;
    }
    public int getOpening(){
        return opening;
    }

    public int getX(){
        return x;
    }

}
