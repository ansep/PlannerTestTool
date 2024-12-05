package it.uniroma1.plannertests.model;

import it.uniroma1.plannertests.model.stanze.Stanza;
import it.uniroma1.plannertests.model.stanze.StanzaTree;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Museo {

    private int rooms;
    private int openRooms;
    private static Museo instance;
    private int attractions;
    private int collegamenti;

    private StanzaTree tree;

    // Costruttore privato per il pattern Singleton
    private Museo() {
    }

    // Costruttore per generazione casuale
    private Museo(int collegamenti, int rooms, int attractions) {
        this.collegamenti = collegamenti;
        this.rooms = rooms;
        this.openRooms = rooms;
        this.attractions = attractions;
        this.tree = new StanzaTree(rooms, collegamenti);
        initRooms();
    }

    // Metodo Singleton per generazione casuale
    public static synchronized Museo getInstance(int collegamenti, int stanze, int attrazioni) {
        if (instance == null || instance.getCollegamenti() != collegamenti) {
            System.out.println("Generazione di un nuovo museo");
            instance = new Museo(collegamenti, stanze, attrazioni);
            return instance;
        }
        if (instance.getOpenRooms() != stanze) {
            for (Stanza s : instance.getStanze()) {
                s.deleteAttrazioni();
            }
            Random r = new Random();
            for (int attrId = 1; attrId <= attrazioni; attrId++) {
                Attrazione a = new Attrazione(attrId);
                int stanzaIndex = r.nextInt(stanze);
                instance.getStanze()[stanzaIndex].addAttrazione(a);
            }
            instance.setOpenRooms(stanze);
        }
        if (instance.getNumeroAttrazioni() != attrazioni) {
            System.out.println("Numero attrazioni variato");
            int nuoveAttrazioni = attrazioni - instance.getNumeroAttrazioni();
            Random r = new Random();
            if (nuoveAttrazioni >= 0) {
                // Aggiungere
                for (int i = instance.getNumeroAttrazioni() + 1; i <= attrazioni; i++) {
                    Attrazione a = new Attrazione(i);
                    int stanzaIndex = r.nextInt(instance.getOpenRooms());
                    instance.getStanze()[stanzaIndex].addAttrazione(a);
                }
            }
            instance.setNumeroAttrazioni(attrazioni);
            System.out.println("Numero attrazioni dopo: " + instance.getNumeroAttrazioni());
        }
        return instance;
    }

    // Metodo Singleton per caricamento da file
    public static synchronized Museo getInstanceFromFile(String propertiesFilePath) throws IOException {
        if (instance == null) {
            instance = new Museo();
            instance.parsePropertiesFile(propertiesFilePath);
        }
        return instance;
    }

    // Parsing del file properties per caricare il museo
    private void parsePropertiesFile(String propertiesFilePath) throws IOException {
        Map<Integer, Stanza> stanzeMap = new HashMap<>();
        Map<Integer, Integer> attrazioniCosti = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(propertiesFilePath));
        String line;
        boolean parsingAttrazioni = false;
        boolean parsingAdiacenze = false;
        boolean parsingCosti = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("collegamenti")) {
                String[] parts = line.split(":");
                this.collegamenti = Integer.parseInt(parts[1].trim());
                continue;
            }
            if (line.equalsIgnoreCase("attrazioni")) {
                parsingAttrazioni = true;
                parsingAdiacenze = false;
                parsingCosti = false;
                continue;
            } else if (line.equalsIgnoreCase("adiacenze")) {
                parsingAttrazioni = false;
                parsingAdiacenze = true;
                parsingCosti = false;
                continue;
            } else if (line.equalsIgnoreCase("costi")) {
                parsingAttrazioni = false;
                parsingAdiacenze = false;
                parsingCosti = true;
                continue;
            } else if (line.isEmpty()) {
                continue;
            }

            if (parsingAttrazioni) {
                // Parsing delle attrazioni per stanza
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    int stanzaId = extractId(parts[0].trim(), "stanza_");
                    String attrazioniPart = parts[1].trim();
                    Stanza stanza = stanzeMap.computeIfAbsent(stanzaId, k -> new Stanza(k, collegamenti));
                    String[] attrazioni = attrazioniPart.split(",");
                    for (String attr : attrazioni) {
                        attr = attr.trim();
                        if (!attr.isEmpty()) {
                            int attrId = extractId(attr, "attr_");
                            Attrazione a = new Attrazione(attrId);
                            stanza.addAttrazione(a);
                        }
                    }
                }
            } else if (parsingAdiacenze) {
                // Parsing delle adiacenze tra stanze
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    int stanzaId = extractId(parts[0].trim(), "stanza_");
                    String adiacenzePart = parts[1].trim();
                    Stanza stanza = stanzeMap.computeIfAbsent(stanzaId, k -> new Stanza(k, collegamenti));
                    String[] adiacenti = adiacenzePart.split(",");
                    for (String adj : adiacenti) {
                        adj = adj.trim();
                        if (!adj.isEmpty()) {
                            int adjId = extractId(adj, "stanza_");
                            Stanza stanzaAdiacente = stanzeMap.computeIfAbsent(adjId, k -> new Stanza(k, collegamenti));
                            stanza.addStanzaAdiacente(stanzaAdiacente);
                        }
                    }
                }
            } else if (parsingCosti) {
                // Parsing dei costi delle attrazioni
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    int attrId = extractId(parts[0].trim(), "attr_");
                    int costo = Integer.parseInt(parts[1].trim());
                    attrazioniCosti.put(attrId, costo);
                }
            }
        }
        reader.close();

        // Assegnazione dei costi alle attrazioni
        for (Stanza stanza : stanzeMap.values()) {
            for (Attrazione attrazione : stanza.getAttrazioni()) {
                Integer costo = attrazioniCosti.get(attrazione.getId());
                if (costo != null) {
                    attrazione.setRating(costo);
                }
            }
        }

        // Inizializzazione del tree con le stanze create
        this.tree = new StanzaTree(new ArrayList<>(stanzeMap.values()));
        this.rooms = stanzeMap.size();
        this.openRooms = this.rooms;
        this.attractions = attrazioniCosti.size();
    }

    // Metodo helper per estrarre l'ID numerico da stringhe come "stanza_1" o "attr_3"
    private int extractId(String text, String prefix) {
        if (text.startsWith(prefix)) {
            return Integer.parseInt(text.substring(prefix.length()));
        } else {
            throw new IllegalArgumentException("Formato non valido: " + text);
        }
    }

    // Assegna le attrazioni alle stanze in modo casuale
    private void initRooms() {
        Random r = new Random();
        for (int attrId = 1; attrId <= attractions; attrId++) {
            Attrazione a = new Attrazione(attrId);
            int stanzaIndex = r.nextInt(openRooms);
            tree.getStanze()[stanzaIndex].addAttrazione(a);
        }
    }

    // Getters e Setters
    public int getCollegamenti() {
        return this.collegamenti;
    }

    public Stanza[] getStanze() {
        return tree.getStanze();
    }

    public int getNumeroAttrazioni() {
        return this.attractions;
    }

    public void setNumeroAttrazioni(int attrazioni) {
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
