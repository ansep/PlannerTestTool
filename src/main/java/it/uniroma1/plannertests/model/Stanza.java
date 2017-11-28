/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thomas
 */
public class Stanza {
    private int id;
    private List<Attrazione> attrazioni;
    private List<Stanza> stanzeAdiacenti;
    
    public Stanza(int id) {
        this.id = id;
        this.attrazioni = new ArrayList<>();
        this.stanzeAdiacenti = new ArrayList<>();
    }
    
    public int getId() {
        return this.id;
    }
    
    public void addAttrazione(Attrazione a) {
        this.attrazioni.add(a);
    }
    
    public List<Attrazione> getAttrazioni() {
        return this.attrazioni;
    }
    
    public void addStanzaAdiacente(Stanza s) {
        this.stanzeAdiacenti.add(s);
    }
    
    public List<Stanza> getStanzeAdiacenti() {
        return this.stanzeAdiacenti;
    }
    
    @Override
    public String toString() {
        return "stanza_" + this.id;
    }
}
