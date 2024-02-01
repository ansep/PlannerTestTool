/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model;

import java.util.Random;

/**
 *
 * @author ansep
 */
public class Attrazione {
    private int id;
    private int rating;
    private int[] ratings = new int[]{10,50,100};
    private Random random;

    public Attrazione(int id) {
        this.id = id;
        random = new Random();
        this.rating = ratings[random.nextInt(3)];
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getRating() {
        return this.rating;
    }
    
    @Override
    public String toString() {
        return "attr_" + this.id;
    }
}
