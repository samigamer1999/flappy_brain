package com.some.thing;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class Player {

    private int x = 100 , y = 100 , width = 50 , height = 50 , velx = 1 ;
    private float vely = 0 , gravity = 0;
    private Color color = Color.YELLOW;
    private NeuralNetwork brain;
    private int score = 0;
    private float fitness = 0;
    private boolean illegal_move = false;

    public Player(){
     this.brain = new NeuralNetwork(6, 2, 2);
    }

    public Player(NeuralNetwork brain){
        this.brain = brain;
    }

    public void render(Graphics2D g){
        g.setColor(color);
        g.fillOval(x,y,width,height);
    }

    public void update(){
        score ++;
        y += (int) vely;
        if(y < 0){
            vely = 0;
            y = 0;
            illegal_move = true;
        }
        if( y + height > 600 ){
            vely = 0;
            illegal_move = true;
        }else{
            if (gravity < 1){
                gravity += 0.2;
            }else{
                vely += gravity;
                gravity = 0;
            }
        }
    }

    public void keyControls(KeyEvent e){
        int x = e.getKeyCode();
        if (x == KeyEvent.VK_SPACE){
            go_up();
        }

    }

    public void go_up() {
        vely = -5;
    }

    public int getX(){
        return x;
    }
    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return y;
    }
    public void setY(int y){
        this.y = y;
    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }

    public void setVely(int vely){
        this.vely = vely;
    }

    public void move(List<Pipe> pipes){
        float[] inputs = new float[6];
        int closest_pipe = closest_pipe(pipes);
        inputs[0] = this.getY();
        inputs[1] = pipes.get(closest_pipe).getY();
        inputs[2] = (pipes.get(closest_pipe).getY() + Pipe.opening);
        inputs[3] = pipes.get(closest_pipe).getX();
        inputs[4] = pipes.get(closest_pipe).getX() + Pipe.pipe_width;
        inputs[5] = this.vely * 75;
        float[] output =  brain.feedforward(inputs);
        if(output[1] > output[0]){
            this.go_up();
        }
    }
    private int closest_pipe(List<Pipe> pipes) {
        int left_side = this.getX();
        int result = 0;
        if (left_side > pipes.get(0).getX() + pipes.get(0).pipe_width) {
            result = 1;
        }
        return result;
    }

    public int getScore(){
        return score;
    }

    public void setFitness(float x){
        this.fitness = x;
    }

    public float getFitness(){
        return fitness;
    }

    public Player child(){
        return new Player(new NeuralNetwork(this.brain, this::mutate));
    }

    public float mutate(float x){

          if(new Random().nextFloat() < 0.1){
              return x + new Random().nextFloat() - 0.5f ;
          }else{
              return x;
          }
    }

    public boolean getIllegal_move(){
        return illegal_move;
    }

    public NeuralNetwork getBrain(){
        return brain;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void printInfo() {
        this.brain.weights_ih.printMatrix();
        this.brain.weights_ho.printMatrix();
        this.brain.bias_h.printMatrix();
        this.brain.bias_o.printMatrix();
    }
}
