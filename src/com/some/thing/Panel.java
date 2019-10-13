package com.some.thing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Panel extends JPanel implements KeyListener, Runnable{

    private boolean running = true;
    private Thread th ;
    //private Player pl;
    private GA ga= new GA();
    private List<Pipe> pipes = new ArrayList<Pipe>();
    private List<Player> birds = new ArrayList<Player>();
    private List<Player> saved_birds = new ArrayList<Player>();
    private int generations = 0, pipes_passed = 0, largest_score = 0, saved_score = 0;
    private int slider = 1;
    private Player best_bird;
    private boolean best_bird_toggle = false;

    public Panel(){

        this.setPreferredSize(new Dimension(400,640));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);
        for (int i = 0; i < 250   ; i++){
            birds.add(new Player());
        }
        best_bird = birds.get(0);
        pipes.add(new Pipe(new Random().nextInt(350)+50));
        th = new Thread(this);
        th.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0,600,400,5);
        if(!best_bird_toggle) {
            //translating before rendering
            if (!birds.isEmpty()) {
                g2.translate(-(birds.get(0).getX() + birds.get(0).getWidth() / 2) + 100, 0);
            } else {
                // Clearing pipes , whatever
                pipes.clear();
                pipes.add(new Pipe(new Random().nextInt(350) + 50));
                //New generation
                generations++;
                pipes_passed = 0;
                birds.addAll(ga.nextGen(saved_birds));
                saved_birds.clear();
            }
            //rendering birds
            for (int i = 0; i < birds.size(); i++) {
                birds.get(i).render(g2);
            }
            //updating best bird
            long[] sc = bestScore(birds);
            if (largest_score < sc[0]) {
                largest_score = birds.get((int) sc[1]).getScore();
                best_bird = birds.get((int) sc[1]);
            }

            //Update screen info  if !best_bird_toggle
            g2.setColor(Color.WHITE);
            g2.drawString("Training Mode", 30 , 620);
            g2.drawString("Generation: "+generations+"  Population: "+birds.size()+"  Population score: "+pipes_passed+
                    "  Max fitness: "+largest_score, 30, 630);
        }else{
            //translating to best_bird
            g2.translate(-(best_bird.getX() + best_bird.getWidth() / 2) + 100, 0);
            //rendering best_bird
            best_bird.render(g2);
            //Update screen info  if best_bird_toggle
            g2.drawString("Best_Bird Mode", 30 , 620);
            g2.drawString("Score: "+pipes_passed, 30, 630);
        }

        //rendering pipes
        for (int i = 0; i < pipes.size(); i++) {
            pipes.get(i).render(g2);
        }
    }

    private long[] bestScore(List<Player> birds) {
        long[] sc = new long[]{0,0};
        for(int i = 0; i < birds.size(); i++){
            if(sc[0] < birds.get(i).getScore()){
                sc[0] = birds.get(i).getScore();
                sc[1] = i;
            }
        }
        return sc;
    }

    public void update(){
        if(!best_bird_toggle) {
            //updating birds positions , deleting losing ones
            for (int i = 0; i < birds.size(); i++) {
                birds.get(i).update();
                if (playerCollision(birds.get(i))) {
                    saved_birds.add(birds.get(i));
                    birds.remove(birds.get(i));
                }
            }
            //deciding whether to jump or not for each bird
            for (Player bird : birds) {
                bird.move(pipes);
            }
        }else{
            //updating best_bird's position
            best_bird.update();
            //Collision detection
            if(playerCollision(best_bird)){
                resetBest_bird();
            }

            //Move !!
            best_bird.move(pipes);
        }

        //updating pipes positions
        for (int i = 0; i < pipes.size(); i++) {
            pipes.get(i).update();
        }
        //adding new pipe when needed
        if (pipes.get(pipes.size() - 1).getX() < 190) {
            pipes.add(new Pipe(new Random().nextInt(350) + 50));
        }
        //removing overflowing pipe
        if (pipes.get(0).getX() < -60) {
            pipes.remove(0);
            pipes_passed++;
        }
    }

    private void resetBest_bird() {
        pipes_passed = 0;
        pipes.clear();
        pipes.add(new Pipe(new Random().nextInt(350) + 50));
    }


    public boolean playerCollision(Player bird){
            if(bird.getIllegal_move()){
               return true;
            }
            boolean istouching = false;
            int up_side = bird.getY();
            int down_side = bird.getY()+bird.getHeight();
            int left_side = bird.getX();
            int right_side = bird.getX()+bird.getWidth();
            for(int i = 0 ; i < pipes.size() ; i++){
                if(left_side > pipes.get(i).getX() + pipes.get(i).pipe_width){
                    continue;
                }
                if(left_side > pipes.get(i).getX() && (up_side < pipes.get(i).getY() || down_side > pipes.get(i).getY() + pipes.get(i).getOpening())){
                    istouching = true;
                    break;
                }
                if((up_side < pipes.get(i).getY() || down_side > pipes.get(i).getY() + pipes.get(i).getOpening()) && right_side > pipes.get(i).getX() && right_side < pipes.get(i).getX()+pipes.get(i).pipe_width  ){
                    istouching = true;
                    break;
                }
            }
            return istouching;
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP ){
            if(slider < 500)slider++;
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN && slider > 1 ){
            slider--;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE ){
            System.out.println("Largest_score : "+largest_score);
        }
        if(e.getKeyCode() == KeyEvent.VK_P){
            best_bird.printInfo();
        }
        if(e.getKeyCode() == KeyEvent.VK_B){
            if(!best_bird_toggle)saved_score = pipes_passed;
            slider = 1;
            best_bird_toggle = true;
            resetBest_bird();
        }
        if(e.getKeyCode() == KeyEvent.VK_G && best_bird_toggle){
            best_bird_toggle = false;
            pipes_passed = saved_score;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() {
       while (running){
           try{
               //badly built but functional system
               Thread.sleep(10);
           }catch(Exception e){
               e.printStackTrace();
           }
           repaint();
           for(int i = 0; i < slider ; i++){
               update();
           }
       }
    }
}
