package it.uniroma1.plannertests.writer;

import it.uniroma1.plannertests.model.Attrazione;
import it.uniroma1.plannertests.model.Museo;
import it.uniroma1.plannertests.model.stanze.Stanza;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ParametricPddlWriter extends AbstractWriter {

    public ParametricPddlWriter(Museo museo, int visite, String pddlDirectory) throws IOException {
        super(museo, visite, pddlDirectory);
        problemWriter = new BufferedWriter(new FileWriter(problemPath));
        domainWriter = new BufferedWriter(new FileWriter(domainPath));
    }

    @Override
    protected String createFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append('/').append(museo.getNome()).append('_')
                .append(visite).append("_parametric");
        File f = new File(basePath + sb.toString());
        f.mkdir();
        return f.getPath();
    }

    @Override
    public String writeDomain() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("(define (domain Museum)\n\t")
                    .append("(:requirements :typing :equality :fluents)\n\t")
                    .append("(:types topology_state visit_state - state attraction)\n\t")
                    .append("(:predicates\n\t\t")
                    .append("(cur_state ?s - state)\n\t\t")
                    .append("(visited ?a - attraction)\n\t\t")
                    .append("(at ?a - attraction ?s - topology_state)\n\t\t")
                    .append("(connected ?from - topology_state ?to - topology_state)\n\t)\n\t")
                    .append("(:functions\n\t\t")
                    .append("(total-cost)\n\t\t")
                    .append("(cost ?a - attraction)\n\t)\n\t");

            // Define parameterized visit action
            sb.append("(:action visit\n\t")
                    .append(":parameters (?a - attraction ?s - topology_state ?v1 - visit_state ?v2 - visit_state)\n\t")
                    .append(":precondition (and (cur_state ?s) (cur_state ?v1) (not (visited ?a)) (at ?a ?s))\n\t")
                    .append(":effect (and (cur_state ?v2) (not (cur_state ?v1)) (visited ?a) ")
                    .append("(increase (total-cost) (cost ?a)))\n\t)\n\t");

            // Define parameterized move action
            sb.append("(:action move\n\t")
                    .append(":parameters (?from - topology_state ?to - topology_state)\n\t")
                    .append(":precondition (and (cur_state ?from) (connected ?from ?to))\n\t")
                    .append(":effect (and (cur_state ?to) (not (cur_state ?from)) (increase (total-cost) 1))\n\t)\n\n");

            sb.append(")");
            domainWriter.write(sb.toString());
            domainWriter.close();
        } catch (IOException e) {
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
            // List all rooms
            for (Stanza s : museo.getStanze()) {
                String roomName = s.toString();
                sb.append(roomName).append(" ");
            }
            sb.append("- topology_state\n\t\t");
            // List all visit states
            for (int i = 0; i <= visite; i++)
                sb.append("v").append(i).append(" ");
            sb.append("- visit_state\n\t\t");
            // List all attractions
            for (Stanza s : museo.getStanze())
                for (Attrazione a : s.getAttrazioni())
                    sb.append(a.toString()).append(" ");
            sb.append("- attraction\n\t)\n\t");

            sb.append("(:init\n\t\t")
                    .append("(cur_state ").append(museo.getStanze()[0].toString()).append(")\n\t\t")
                    .append("(cur_state v0)\n\t\t")
                    .append("(= (total-cost) 0)\n\n");

            // Define 'at' predicate for attractions
            for (Stanza s : museo.getStanze()) {
                String roomName = s.toString();
                for (Attrazione a : s.getAttrazioni()) {
                    sb.append("\t\t(at ").append(a.toString()).append(" ").append(roomName).append(")\n");
                    sb.append("\t\t(= (cost ").append(a.toString()).append(") ").append(a.getRating()).append(")\n");
                }
            }

            // Define 'connected' predicate for rooms
            for (Stanza s : museo.getStanze()) {
                String roomName = s.toString();
                for (Stanza sa : s.getStanzeAdiacenti()) {
                    if (sa != null) {
                        String adjacentRoom = sa.toString();
                        sb.append("\t\t(connected ").append(roomName).append(" ").append(adjacentRoom).append(")\n");
                    }
                }
            }

            sb.append("\t)\n\n");
            sb.append("(:goal\n\t\t(and\n\t\t(cur_state v").append(visite).append(")\n\t\t)\n\t)\n");
            sb.append("(:metric minimize (total-cost))\n)");
            problemWriter.write(sb.toString());
            problemWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return problemPath;
    }
}
