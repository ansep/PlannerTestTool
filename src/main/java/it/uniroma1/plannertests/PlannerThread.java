/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests;

import it.uniroma1.plannertests.controller.MainController;
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
    
    private final Button report;
    private final ProgressIndicator progress;
    
    public PlannerThread(String execString, String planner, String folderPath,
                         Button report, ProgressIndicator progress) {
        super();
        this.execString = execString;
        this.planner = planner;
        this.folderPath = folderPath;
        
        this.report = report;
        this.progress = progress;
    }

    @Override
    public void run() {
        progress.setVisible(true);
        System.out.println(MainController.planners);
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
            }
            solutionWriter.close();
            while((e = error.readLine()) != null)
                System.err.println(e);
            switch(planner) {
                case "FD_FF": MainController.planners |= 0b100; break;
                case "SYMBA_new" : MainController.planners |= 0b001; break;
            }
            if(MainController.planners == 0b101) {
                report.setDisable(false);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        progress.setVisible(false);
    }
}
