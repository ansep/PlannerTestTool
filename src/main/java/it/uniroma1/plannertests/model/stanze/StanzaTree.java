/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model.stanze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ansep
 */
public abstract class StanzaTree {
    protected int numStanze;
    protected Deque<Stanza> stanze;
    protected Random random;
    protected Stanza[] stanzeArray;

    public StanzaTree(int stanze) {
        this.numStanze = stanze;
        this.stanze = new ArrayDeque<>();
        this.random = new Random();
        this.stanzeArray = new Stanza[numStanze];
        initRooms();
        initTree();
    }

    public abstract void initRooms();

    public final void initTree() {
        List<Stanza> grey = new ArrayList<>();
        grey.add(stanze.pop());
        while (!(grey.isEmpty() || stanze.isEmpty())) {
            // System.out.println(this.stanze);
            Stanza s = grey.remove(0);
            // System.out.print(s.toString() + ": ");
            for (int i = 0; i < s.getStanzeAdiacenti().length; i++) {
                if (stanze.isEmpty())
                    break;
                Stanza m = stanze.pop();
                grey.add(m);
                s.addStanzaAdiacente(m);
                // System.out.println(m + ", ");
            }
            // System.out.println();
        }
    }

    public Stanza[] getStanze() {
        return this.stanzeArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Stanza s : stanzeArray) {
            sb.append(s.toString()).append(": ");
            for (Stanza ad : s.getStanzeAdiacenti()) {
                if (ad != null) {
                    sb.append(ad.toString()).append(", ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
