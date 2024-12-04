/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model;

import it.uniroma1.plannertests.model.stanze.Stanza;
import it.uniroma1.plannertests.model.stanze.StanzaCustom;
import it.uniroma1.plannertests.model.stanze.StanzaTree;
import it.uniroma1.plannertests.model.stanze.StanzaTree1;
import it.uniroma1.plannertests.model.stanze.StanzaTree2;
import it.uniroma1.plannertests.model.stanze.StanzaTree3;
import it.uniroma1.plannertests.model.stanze.StanzaTreeCustom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    public Museo(String propertiesFilePath) throws IOException {
        parsePropertiesFile(propertiesFilePath);
    }

    public static synchronized Museo getInstance(int collegamenti, int stanze, int attrazioni) {
        if (instance == null || instance.getCollegamenti() != collegamenti) {
            System.out.println("Generazione di un nuovo museo");
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
            instance.setNumeroAttrazioni(attrazioni);
            System.out.println("Numero attrazioni dopo: " + instance.getNumeroAttrazioni());
        }
        return instance;
    }

    public static synchronized Museo getInstanceFromFile(String propertiesFilePath) throws IOException {
        if (instance == null) {
            instance = new Museo(propertiesFilePath);
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
                    Stanza stanza = stanzeMap.computeIfAbsent(stanzaId, k -> new StanzaCustom(k));
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
                    Stanza stanza = stanzeMap.computeIfAbsent(stanzaId, k -> new StanzaCustom(k));
                    String[] adiacenti = adiacenzePart.split(",");
                    for (String adj : adiacenti) {
                        adj = adj.trim();
                        if (!adj.isEmpty()) {
                            int adjId = extractId(adj, "stanza_");
                            Stanza stanzaAdiacente = stanzeMap.computeIfAbsent(adjId, k -> new StanzaCustom(adjId));
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
        this.tree = new StanzaTreeCustom(new ArrayList<>(stanzeMap.values()));
        this.rooms = stanzeMap.size();
        this.openRooms = this.rooms;
        this.attractions = attrazioniCosti.size();
    }

    // Metodo helper per estrarre l'ID numerico
    private int extractId(String text, String prefix) {
        if (text.startsWith(prefix)) {
            return Integer.parseInt(text.substring(prefix.length()));
        } else {
            throw new IllegalArgumentException("Formato non valido: " + text);
        }
    }

}