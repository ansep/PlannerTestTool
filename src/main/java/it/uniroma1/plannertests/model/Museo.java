/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model;

import it.uniroma1.plannertests.model.stanze.Stanza;
import it.uniroma1.plannertests.model.stanze.StanzaTree;
import it.uniroma1.plannertests.model.stanze.StanzaTree1;
import it.uniroma1.plannertests.model.stanze.StanzaTree2;
import it.uniroma1.plannertests.model.stanze.StanzaTree3;
import java.util.Random;

/**
 *
 * @author ansep
 */
public class Museo {

    private int rooms;
    private int openRooms;
    private static Museo instance;
    private int attractions;
    private int collegamenti;

    private StanzaTree tree;

    private Museo(int collegamenti, int rooms, int attractions) {
        this.collegamenti = collegamenti;
        this.rooms = rooms;
        this.openRooms = rooms;
        this.attractions = attractions;
        switch (collegamenti) {
            case 1:
                tree = new StanzaTree1(rooms);
                break;
            case 2:
                tree = new StanzaTree2(rooms);
                break;
            case 3:
                tree = new StanzaTree3(rooms);
                break;
            default:
                tree = null;
        }
        // System.out.println(tree.toString());
        initRooms();
    }

    public static synchronized Museo getInstance(int collegamenti, int stanze, int attrazioni) {
        if (instance == null || instance.getCollegamenti() != collegamenti) {
            System.out.println("Nuovo museo generato");
            instance = new Museo(collegamenti, stanze, attrazioni);
            return instance;
        }
        if (instance.getOpenRooms() != stanze) {
            for (Stanza s : instance.getStanze()) {
                s.deleteAttrazioni();
                // System.out.println(s.getAttrazioni());
            }
            Random r = new Random();
            for (int attrId = 1; attrId <= attrazioni; attrId++) {
                Attrazione a = new Attrazione(attrId);
                int stanza = r.nextInt(stanze);
                instance.getStanze()[stanza].addAttrazione(a);
            }
            instance.setOpenRooms(stanze);
        }
        if (instance.getNumeroAttrazioni() != attrazioni) {
            System.out.println("Numero attrazioni variato");
            int nuoveAttrazioni = attrazioni - instance.getNumeroAttrazioni();
            Random r = new Random();
            if (nuoveAttrazioni >= 0) {
                // Aggiungere
                Attrazione a;
                System.out.println("Numero attrazioni prima: " + instance.getNumeroAttrazioni());
                for (int i = instance.getNumeroAttrazioni() + 1; i <= attrazioni; i++) {
                    a = new Attrazione(i);
                    int stanza = r.nextInt(instance.getOpenRooms());
                    instance.getStanze()[stanza].addAttrazione(a);
                }
            }
            instance.setNumeroAttrazini(attrazioni);
            System.out.println("Numero attrazioni dopo: " + instance.getNumeroAttrazioni());
        }
        return instance;
    }

    private void initRooms() {
        Random r = new Random();
        for (int attrId = 1; attrId <= attractions; attrId++) {
            Attrazione a = new Attrazione(attrId);
            int stanza = r.nextInt(openRooms);
            tree.getStanze()[stanza].addAttrazione(a);
        }
    }

    public int getCollegamenti() {
        return this.collegamenti;
    }

    public it.uniroma1.plannertests.model.stanze.Stanza[] getStanze() {
        return tree.getStanze();
    }

    public int getNumeroAttrazioni() {
        return this.attractions;
    }

    public void setNumeroAttrazini(int attrazioni) {
        this.attractions = attrazioni;
    }

    public String getNome() {
        return this.collegamenti + "_" + this.openRooms + "_" + this.attractions;
    }

    public void setOpenRooms(int rooms) {
        this.openRooms = rooms;
    }

    public int getOpenRooms() {
        return this.openRooms;
    }
}