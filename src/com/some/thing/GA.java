package com.some.thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA {

    public GA(){

    }

    public List<Player> nextGen(List<Player> saved_birds){
        calculateFitness(saved_birds);
        return generate(saved_birds);
    }

    private List<Player> generate(List<Player> saved_birds) {
       List<Player> new_birds = new ArrayList<>();
       for(int i = 0  ; i < 175; i++){
           // pick bird based on fitness
           // the selection will eventually be mutated
           new_birds.add(selection(saved_birds));
       }
       for(int i = 0 ; i < 75 ; i++){
           new_birds.add(new Player());
       }
       return new_birds;
    }

    private Player selection(List<Player> saved_birds) {
        // Start at 0
        int index = 0;

        // Pick a random number between 0 and 1
        float r = new Random().nextFloat();
        do{
            r -= saved_birds.get(index).getFitness();
            index ++;
        }while(r > 0);

        index--;

        return saved_birds.get(index).child();
    }

    public void calculateFitness(List<Player> saved_birds){
        float sum = 0;
        for(Player bird : saved_birds){
            sum += bird.getScore();
        }

        for(Player bird : saved_birds){
                bird.setFitness((float)bird.getScore()/sum);
        }
    }
}
