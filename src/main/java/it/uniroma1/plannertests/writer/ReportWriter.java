/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author thomas
 */
public class ReportWriter {
    
    private String folderPath;
    private BufferedReader fdSolReader;
    private BufferedReader symbaSolReader;
    private BufferedWriter reportWriter;
    
    public ReportWriter(String folderPath) {
        this.folderPath = folderPath;
        readFdSolution();
        readSymbaSolution();
    }
    
    private void readFdSolution() {
        try {
            fdSolReader = new BufferedReader(new FileReader(folderPath + "/FD_FF.sol"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void readSymbaSolution() {
        try {
            symbaSolReader = new BufferedReader(new FileReader(folderPath + "/SYMBA2.sol"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String writeReport() {
        
        try {
            reportWriter = new BufferedWriter(new FileWriter(folderPath + "/report.csv"));
            StringBuilder sb = new StringBuilder();
            Path p = Paths.get(folderPath);
            String name = p.getFileName().toString();
            sb.append(name).append(',');
            String s;
            while((s = fdSolReader.readLine()) != null) {
                if(s.contains("Plan cost:"))
                    sb.append(s.split(" ")[2].trim()).append(',');
                if(s.contains("Duration:"))
                    sb.append(s.split(" ")[1].trim()).append(',');
            }
            fdSolReader.close();
            while((s = symbaSolReader.readLine()) != null) {
                if(s.contains("Plan cost:"))
                    sb.append(s.split(" ")[2].trim()).append(',');
                if(s.contains("Duration:"))
                    sb.append(s.split(" ")[1].trim()).append(',');
            }
            symbaSolReader.close();
            reportWriter.write(sb.toString());
            reportWriter.close();
            return folderPath + "/report.csv";
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
