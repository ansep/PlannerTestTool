package it.uniroma1.plannertests.model.stanze;

import it.uniroma1.plannertests.model.Attrazione;
import java.util.ArrayList;
import java.util.List;

public class Stanza {
    private int id;
    private List<Attrazione> attrazioni;
    private List<Stanza> stanzeAdiacenti;
    private int maxAdiacenti;

    public Stanza(int id, int maxAdiacenti) {
        this.id = id;
        this.maxAdiacenti = maxAdiacenti;
        this.attrazioni = new ArrayList<>();
        this.stanzeAdiacenti = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public int getMaxAdiacenti() {
        return this.maxAdiacenti;
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
        if (this.stanzeAdiacenti.size() < this.maxAdiacenti) {
            if (!this.stanzeAdiacenti.contains(s)) {
                this.stanzeAdiacenti.add(s);
                s.addStanzaAdiacenteInterno(this); // Aggiunge bidirezionalmente
            }
        }
    }

    private void addStanzaAdiacenteInterno(Stanza s) {
        if (this.stanzeAdiacenti.size() < this.maxAdiacenti) {
            if (!this.stanzeAdiacenti.contains(s)) {
                this.stanzeAdiacenti.add(s);
            }
        }
    }

    public List<Stanza> getStanzeAdiacenti() {
        return this.stanzeAdiacenti;
    }

    @Override
    public String toString() {
        return "stanza_" + this.id;
    }
}
