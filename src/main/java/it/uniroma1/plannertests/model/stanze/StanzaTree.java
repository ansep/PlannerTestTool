package it.uniroma1.plannertests.model.stanze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class StanzaTree {

    protected int numStanze;
    protected Stanza[] stanzeArray;
    protected Random random;

    // Costruttore per la generazione casuale
    public StanzaTree(int numStanze, int maxAdiacentiPerStanza) {
        this.numStanze = numStanze;
        this.stanzeArray = new Stanza[numStanze];
        this.random = new Random();
        initRooms(maxAdiacentiPerStanza);
        initTree();
    }

    // Costruttore per stanze predefinite (caricate da file)
    public StanzaTree(List<Stanza> stanzeList) {
        this.numStanze = stanzeList.size();
        this.stanzeArray = stanzeList.toArray(new Stanza[0]);
        this.random = new Random();
    }

    // Inizializza le stanze per la generazione casuale
    private void initRooms(int maxAdiacentiPerStanza) {
        for (int i = 0; i < numStanze; i++) {
            Stanza s = new Stanza(i + 1, maxAdiacentiPerStanza);
            stanzeArray[i] = s;
        }
    }

    // Costruisce la struttura delle stanze per la generazione casuale
    private void initTree() {
        Deque<Stanza> stanzeDaCollegare = new ArrayDeque<>();
        List<Stanza> stanzeCollegate = new ArrayList<>();

        // Aggiungi tutte le stanze alla coda
        for (Stanza s : stanzeArray) {
            stanzeDaCollegare.add(s);
        }

        // Inizia con la prima stanza
        Stanza primaStanza = stanzeDaCollegare.pop();
        stanzeCollegate.add(primaStanza);

        while (!stanzeDaCollegare.isEmpty()) {
            // Se non ci sono stanze collegate che possono avere nuovi collegamenti, termina
            if (stanzeCollegate.isEmpty()) {
                System.err.println(
                        "Impossibile collegare tutte le stanze. Verifica il numero di stanze e i collegamenti massimi.");
                break;
            }

            // Seleziona casualmente una stanza già collegata che può avere nuovi
            // collegamenti
            Stanza stanzaCorrente = stanzeCollegate.get(random.nextInt(stanzeCollegate.size()));

            if (stanzaCorrente.getStanzeAdiacenti().size() < stanzaCorrente.getMaxAdiacenti()) {
                // Collega una nuova stanza
                Stanza stanzaDaCollegare = stanzeDaCollegare.pop();
                stanzaCorrente.addStanzaAdiacente(stanzaDaCollegare);
                stanzeCollegate.add(stanzaDaCollegare);
            } else {
                // Rimuove la stanza dalla lista se ha raggiunto il massimo di adiacenze
                stanzeCollegate.remove(stanzaCorrente);
            }
        }
    }

    // Ottiene l'array di stanze
    public Stanza[] getStanze() {
        return this.stanzeArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Stanza s : stanzeArray) {
            sb.append(s.toString()).append(": ");
            for (Stanza ad : s.getStanzeAdiacenti()) {
                sb.append(ad.toString()).append(", ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
