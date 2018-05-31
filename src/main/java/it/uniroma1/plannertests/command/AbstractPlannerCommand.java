/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.command;

import it.uniroma1.plannertests.PlannerThread;

/**
 *
 * @author thomas
 */
public abstract class AbstractPlannerCommand{
    
    private String executablePath;
    private String domainPath;
    private String problemPath;

    public AbstractPlannerCommand(String executablePath, String domainPath, String problemPath) {
        this.executablePath = executablePath;
        this.domainPath = domainPath;
        this.problemPath = problemPath;
    }
    
    public void execute() {
        //PlannerThread thread = new PlannerThread(sb.toString(), planner, folderPath, fd, symba1, generaReportButton, progress);
        //thread.start();
    }
    
}
