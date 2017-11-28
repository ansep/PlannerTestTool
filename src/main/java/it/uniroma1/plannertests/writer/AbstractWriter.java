/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.writer;

import it.uniroma1.plannertests.model.Museo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author thomas
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
        domainPath = folderPath + "/domain.pddl";
        problemPath = folderPath + "/problem.pddl";
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
    
    
}
