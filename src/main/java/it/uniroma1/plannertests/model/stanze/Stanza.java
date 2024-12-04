/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model.stanze;

import it.uniroma1.plannertests.model.Attrazione;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ansep
 */
public abstract class Stanza {
    private int id;
    private List<Attrazione> attrazioni;
    protected List<Stanza> stanzeAdiacenti;

    public Stanza(int id) {
        this.id = id;
        this.attrazioni = new ArrayList<>();
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

    public void deleteAttrazioni() {
        this.attrazioni.clear();
    }

    public void addStanzaAdiacente(Stanza s) {
        if (this.stanzeAdiacenti == null) {
            this.stanzeAdiacenti = new ArrayList<>();
        }
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
