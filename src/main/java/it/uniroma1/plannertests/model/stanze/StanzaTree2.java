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
public class StanzaTree2 extends StanzaTree{

    public StanzaTree2(int stanze) {
        super(stanze);
    }

    @Override
    public void initRooms() {
        for(int i = 0; i < numStanze; i++) {
            int c = random.nextInt(10);
            Stanza s;
            if(c < 5)
                s = new Stanza2(i+1);
            else
                s = new Stanza1(i+1);
            this.stanze.add(s);
            stanzeArray[i] = s;
        }
    }
    
}
