/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model;

import java.util.Random;

/**
 *
 * @author thomas
 */
public class Museo {
    
    private Stanza[] rooms;
    private static Museo instance;
    private int attractions;
    private int collegamenti;
    
    private Museo(int collegamenti, int rooms, int attractions) {
        this.collegamenti = collegamenti;
        this.rooms = new Stanza[rooms];
        this.attractions = attractions;
        initRooms();
    }
    
    public static synchronized Museo getInstance(int collegamenti, int stanze, int attrazioni) {
        if(instance == null || instance.getCollegamenti() != collegamenti)
            return new Museo(collegamenti, stanze, attrazioni);
        //Genera una nuova istanza Museo se il numero di stanze varia
        if(instance.getStanze().length != stanze)
            return new Museo(collegamenti, stanze, attrazioni);
        if(instance.getNumeroAttrazioni() != attrazioni) {
            int nuoveAttrazioni = attrazioni - instance.getNumeroAttrazioni();
            if(nuoveAttrazioni >= 0) {
                Attrazione a;
                Random r = new Random();
                for(int i = instance.getNumeroAttrazioni() + 1; i <= attrazioni; i++) {
                    a = new Attrazione(i);
                    int stanza = r.nextInt(instance.getStanze().length);
                    instance.getStanze()[stanza].addAttrazione(a);
                }
            }
        }
        return instance;
    }
    
    private void initRooms() {
        for(int i = 0; i < rooms.length; i++) {
            this.rooms[i] = new Stanza(i+1);
        }
        Random r = new Random();
        for(int attrId = 1; attrId <= attractions; attrId++) {
            Attrazione a = new Attrazione(attrId);
            int stanza = r.nextInt(rooms.length);
            rooms[stanza].addAttrazione(a);
        }
        
        for(int i = 0; i < this.rooms.length - 1; i++) {
            this.rooms[i].addStanzaAdiacente(this.rooms[i+1]);
        }
    }
    
    public int getCollegamenti() {
        return this.collegamenti;
    }

    public Stanza[] getStanze() {
        return rooms;
    }
    
    public int getNumeroAttrazioni() {
        return this.attractions;
    }
    
    public String getNome() {
        return this.rooms.length + "_" + this.attractions;
    }
}