/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.writer;

import it.uniroma1.plannertests.model.Attrazione;
import it.uniroma1.plannertests.model.Museo;
import it.uniroma1.plannertests.model.stanze.Stanza;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author ansep
 */
public abstract class AbstractWriter {

    protected String basePath;
    private String folderPath;
    protected String domainPath;
    protected String problemPath;
    protected BufferedWriter domainWriter;
    protected BufferedWriter problemWriter;
    protected Museo museo;
    protected final int visite;

    public AbstractWriter(Museo museo, int maxVisitCounter, String pddlDirectory) throws IOException {
        this.museo = museo;
        this.visite = maxVisitCounter;
        basePath = pddlDirectory;
        folderPath = createFolder();
        domainPath = Paths.get(folderPath, "domain.pddl").toString();
        problemPath = Paths.get(folderPath, "problem.pddl").toString();
        writeMuseumProperties();
    }

    protected abstract String createFolder();

    protected String append() {
        StringBuilder sb = new StringBuilder();
        sb.append(museo.getNome()).append('_')
                .append(visite);
        return sb.toString();
    }

    public String getFolderPath() {
        return folderPath;
    }

    /**
     * 
     * @return domain file path
     */
    public abstract String writeDomain();

    /**
     * 
     * @return problem file path
     */
    public abstract String writeProblem();

    private void writeMuseumProperties() {
        BufferedWriter museumWriter;
        try {
            ArrayList<Attrazione> listAttrazioni = new ArrayList<>();
            museumWriter = new BufferedWriter(new FileWriter(folderPath + "/museum.properties"));
            StringBuilder sb = new StringBuilder();
            sb.append("collegamenti : " + museo.getCollegamenti()).append("\n");
            sb.append("attrazioni\n");
            Stanza[] stanze = museo.getStanze();
            for (Stanza stanza : stanze) {
                sb.append("stanza_").append(stanza.getId()).append(" : ");
                for (Attrazione attr : stanza.getAttrazioni()) {
                    sb.append("attr_").append(attr.getId()).append(",");
                    listAttrazioni.add(attr);
                }
                sb.append("\n");
            }
            sb.append("adiacenze\n");
            for (Stanza stanza : stanze) {
                sb.append("stanza_").append(stanza.getId()).append(" : ");
                for (Stanza adj : stanza.getStanzeAdiacenti()) {
                    if (adj != null) {
                        sb.append("stanza_").append(adj.getId()).append(",");
                    }
                }
                sb.append("\n");
            }
            sb.append("costi\n");
            for (Attrazione attr : listAttrazioni) {
                sb.append("attr_").append(attr.getId()).append(" : ");
                sb.append(attr.getRating()).append("\n");
            }
            museumWriter.write(sb.toString());
            museumWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}