/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.writer;

import it.uniroma1.plannertests.model.Attrazione;
import it.uniroma1.plannertests.model.Museo;
import it.uniroma1.plannertests.model.Stanza;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class PddlGroundedWriter extends AbstractWriter {

    
    public PddlGroundedWriter(Museo museo, int visite, String pddlDirectory) throws IOException {
        super(museo, visite, pddlDirectory);
        problemWriter = new BufferedWriter(new FileWriter(problemPath));
        domainWriter = new BufferedWriter(new FileWriter(domainPath));
    }
    
    @Override
    protected String createFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append('/').append(museo.getNome()).append('_')
            .append(visite).append("_old");
        File f = new File(basePath + sb.toString());
        f.mkdir();
        return f.getPath();
    }
    
    @Override
    protected final String append() {
        String path = super.append();
        path += "_old";
        return path;
    }

    @Override
    public String writeDomain(){
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("(define (domain Museum)\n\t")
                    .append("(:requirements :typing :equality)\n\t")
                    .append("(:types topology_state visit_state - state attraction)\n\t")
                    .append("(:predicates\n\t\t")
                    .append("(cur_state ?s - state)\n\t\t")
                    .append("(visited ?a - attraction)\n\t)\n\t")
                    .append("(:functions\n\t\t")
                    .append("(total-cost)\n\t)\n\t");
            domainWriter.write(sb.toString());
            sb.setLength(0);
            
            //Action visit
            for(Stanza s : museo.getStanze()) {
                for(int v = 0; v < visite; v++) {
                    String roomName = s.toString();
                    for(Attrazione a : s.getAttrazioni()) {
                        String attrName = a.toString();
                        sb.append("(:action visit-v").append(v).append("-")
                                .append(attrName).append("\n\t")
                                //precondition
                                .append(":precondition ")
                                .append("(and (cur_state ").append(roomName)
                                .append(") (cur_state v").append(v).append(") ")
                                .append("(not (visited ").append(attrName).append(")))\n\t")
                                //effect
                                .append(":effect ")
                                .append("(and (cur_state v").append(v+1).append(") ")
                                .append("(not (cur_state v").append(v).append(")) ")
                                .append("(visited ").append(attrName).append(") ")
                                .append("(increase (total-cost) ").append(a.getRating()).append("))\n\t\t)\n\t");
                    }
                }
            }
            domainWriter.write(sb.toString());
            sb.setLength(0);
            //Action move
            for(Stanza s : museo.getStanze()) {
                String roomName = s.toString();
                for (Stanza sa : s.getStanzeAdiacenti()) {
                    String nextRoom = sa.toString();
                    sb.append("(:action move-").append(roomName).append('-').append(nextRoom).append("\n\t")
                            .append(":precondition (cur_state ").append(roomName).append(")\n\t")
                            .append(":effect (and (cur_state ").append(nextRoom).append(") ")
                            .append("(not (cur_state ").append(roomName).append(")) ")
                            .append("(increase (total-cost) 1").append("))\n\t)\n\n\t");
                    if(!nextRoom.equals("end")) {
                        sb.append("(:action move-").append(nextRoom).append('-').append(roomName).append("\n\t")
                                .append(":precondition (cur_state ").append(nextRoom).append(")\n\t")
                                .append(":effect (and (cur_state ").append(roomName).append(") ")
                                .append("(not (cur_state ").append(nextRoom).append(")) ")
                                .append("(increase (total-cost) 1").append("))\n\t)\n\n\t");
                    }
                }
            }
            sb.append("\n)");
            domainWriter.write(sb.toString());
            domainWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return domainPath;
    }

    @Override
    public String writeProblem() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("(define (problem Visit) (:domain Museum)\n\t")
                    .append("(:objects\n\t\t");
            for(Stanza s : museo.getStanze()) {
                String roomName = s.toString();
                sb.append(roomName).append(" ");
            }
            sb.append("- topology_state\n\t\t");
            for(int i = 0; i <= visite; i++)
                sb.append("v").append(i).append(" ");
            sb.append("- visit_state\n\t\t");
            for(Stanza s : museo.getStanze())
                for(Attrazione a : s.getAttrazioni())
                    sb.append(a.toString()).append(" ");
            sb.append("- attraction\n\t)\n\t");
            
            sb.append("(:init\n\t\t")
                    .append("(cur_state ").append(museo.getStanze()[0].toString()).append(")\n\t\t")
                    .append("(cur_state v0)\n\n\t\t")
                    .append("(= (total-cost) 0)\n\t)\n\n\t");
            
            sb.append("(:goal\n\t\t(and\n\t\t(cur_state ").append(museo.getStanze()[museo.getStanze().length-1].toString())
                    .append(")\n\t\t(cur_state v").append(visite).append(")\n\t\t)");
            sb.append("\n\t)(:metric minimize (total-cost))\n)");
            
            problemWriter.write(sb.toString());
            problemWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return problemPath;
    }
}