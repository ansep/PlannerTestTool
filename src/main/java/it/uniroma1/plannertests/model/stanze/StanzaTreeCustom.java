package it.uniroma1.plannertests.model.stanze;

import java.util.List;

public class StanzaTreeCustom extends StanzaTree {

    public StanzaTreeCustom(List<Stanza> stanzeList) {
        super(); // Call to the superclass constructor
        this.numStanze = stanzeList.size();
        this.stanzeArray = stanzeList.toArray(new Stanza[0]);
    }

    @Override
    public void initRooms() {
        // Non necessario
    }
}
