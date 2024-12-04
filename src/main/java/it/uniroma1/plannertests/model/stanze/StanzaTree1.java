/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model.stanze;

/**
 *
 * @author ansep
 */
public class StanzaTree1 extends StanzaTree {

    public StanzaTree1(int stanze) {
        super(stanze);
    }

    public StanzaTree1(Stanza[] stanze) {
        super(stanze.length);
        this.stanzeArray = stanze;
    }

    @Override
    public void initRooms() {
        for (int i = 0; i < numStanze; i++) {
            Stanza s = new Stanza1(i + 1);
            this.stanze.add(s);
            stanzeArray[i] = s;
        }
    }
}
