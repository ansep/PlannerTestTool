/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.command;

/**
 *
 * @author ansep
 */
public class SymbaNewCommand extends AbstractPlannerCommand{

    public SymbaNewCommand(String executablePath, String domainPath, String problemPath) {
        super(executablePath, domainPath, problemPath);
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
