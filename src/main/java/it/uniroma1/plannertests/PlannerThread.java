/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;

/**
 *
 * @author thomas
 */
public class PlannerThread extends Thread{
    
    private final String execString;
    private final String planner;
    private final String folderPath;
    private BufferedWriter solutionWriter;
    
    private boolean fd, symba;
    private Button report;
    private ProgressIndicator progress;
    
    public PlannerThread(String execString, String planner, String folderPath,
                        boolean fd, boolean symba, Button report, ProgressIndicator progress) {
        super();
        this.execString = execString;
        this.planner = planner;
        this.folderPath = folderPath;
        
        this.fd = fd;
        this.symba = symba;
        this.report = report;
        this.progress = progress;
    }

    @Override
    public void run() {
        progress.setVisible(true);
        Process p;
        BufferedReader input;
        BufferedReader error;
        try {
            p = Runtime.getRuntime().exec(execString);
            p.waitFor();
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String outFile = folderPath + '/' + planner + ".sol";
            solutionWriter = new BufferedWriter(new FileWriter(outFile));
            String e;
            while((e = input.readLine()) != null) {
                solutionWriter.append(e).append('\n');
                /*if(e.contains("move")) {
                    System.out.println(e);
                    solutionWriter.append(e);
                    solutionWriter.newLine();
                }
                if(e.contains("visit")) {
                    System.out.println(e);
                    solutionWriter.append(e);
                    solutionWriter.newLine();
                }
                if(e.contains("Plan length")) {
                    System.out.println(e);
                    solutionWriter.append(e);
                    solutionWriter.newLine();
                }
                if(e.contains("Plan cost")) {
                    System.out.println(e);
                    solutionWriter.append(e);
                    solutionWriter.newLine();
                }
                if(e.contains("Duration:")) {
                    solutionWriter.append(e);
                }*/
            }
            solutionWriter.close();
            while((e = error.readLine()) != null)
                System.err.println(e);
            if(fd && symba)
                report.setDisable(false);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        progress.setVisible(false);
    }
    
    
}
