/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.command;

import it.uniroma1.plannertests.PlannerThread;

/**
 *
 * @author ansep
 */
public class FFCommand extends AbstractPlannerCommand {

    public FFCommand(String executablePath, String domainPath, String problemPath) {
        super(executablePath, domainPath, problemPath);
    }

    @Override
    public void execute() {
    }

}
