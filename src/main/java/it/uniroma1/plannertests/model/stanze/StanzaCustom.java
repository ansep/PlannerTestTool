package it.uniroma1.plannertests.model.stanze;

import java.util.ArrayList;
import java.util.List;

public class StanzaCustom extends Stanza {
    private List<Stanza> stanzeAdiacenti;

    public StanzaCustom(int id) {
        super(id);
        this.stanzeAdiacenti = new ArrayList<>();
    }

    @Override
    public void addStanzaAdiacente(Stanza s) {
        if (!this.stanzeAdiacenti.contains(s)) {
            this.stanzeAdiacenti.add(s);
        }
    }

    @Override
    public List<Stanza> getStanzeAdiacenti() {
        return this.stanzeAdiacenti;
    }
}
