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
                    .append("(:requirements :typing :equality)\n\t")
                    .append("(:types topology_state visit_state - state attraction)\n\t")
                    .append("(:predicates\n\t\t")
                    .append("(cur_state ?s - state)\n\t\t")
                    .append("(visited ?a - attraction)\n\t\t")
                    .append("(cost_10 ?a - attraction)\n\t\t")
                    .append("(cost_50 ?a - attraction)\n\t\t")
                    .append("(cost_100 ?a - attraction)\n\t\t")
                    .append("(at ?a - attraction ?s - topology_state)\n\t\t")
                    .append("(connected ?from - topology_state ?to - topology_state)\n\t\t")
                    .append("(next_visit_state ?v1 - visit_state ?v2 - visit_state)\n\t)\n\t")
                    .append("(:functions\n\t\t")
                    .append("(total-cost)\n\t)\n\t");

            // // Define parameterized visit action
            // sb.append("(:action visit\n\t\t")
            // .append(":parameters (?a - attraction ?s - topology_state ?v1 - visit_state
            // ?v2 - visit_state)\n\t\t")
            // .append(":precondition (and (cur_state ?s) (cur_state ?v1) (not (visited ?a))
            // (at ?a ?s))\n\t\t")
            // .append(":effect (and (cur_state ?v2) (not (cur_state ?v1)) (visited
            // ?a)\n\t\t\t")
            // .append("(when (cost_10 ?a) (increase (total-cost) 10))\n\t\t\t")
            // .append("(when (cost_50 ?a) (increase (total-cost) 50))\n\t\t\t")
            // .append("(when (cost_100 ?a) (increase (total-cost) 100))\n\t\t")
            // .append(")\n\t)\n\n\t");

            // Define cost-dependent visit actions
            sb.append("(:action visit-cost-10\n\t\t")
                    .append(":parameters (?a - attraction ?s - topology_state ?v1 - visit_state ?v2 - visit_state)\n\t\t")
                    .append(":precondition (and (cur_state ?s) (cur_state ?v1) (not (visited ?a)) (at ?a ?s) (cost_10 ?a) (next_visit_state ?v1 ?v2))\n\t\t")
                    .append(":effect (and (cur_state ?v2) (not (cur_state ?v1)) (visited ?a) (increase (total-cost) 10))\n\t)\n\n\t");

            sb.append("(:action visit-cost-50\n\t\t")
                    .append(":parameters (?a - attraction ?s - topology_state ?v1 - visit_state ?v2 - visit_state)\n\t\t")
                    .append(":precondition (and (cur_state ?s) (cur_state ?v1) (not (visited ?a)) (at ?a ?s) (cost_50 ?a) (next_visit_state ?v1 ?v2))\n\t\t")
                    .append(":effect (and (cur_state ?v2) (not (cur_state ?v1)) (visited ?a) (increase (total-cost) 50))\n\t)\n\n\t");

            sb.append("(:action visit-cost-100\n\t\t")
                    .append(":parameters (?a - attraction ?s - topology_state ?v1 - visit_state ?v2 - visit_state)\n\t\t")
                    .append(":precondition (and (cur_state ?s) (cur_state ?v1) (not (visited ?a)) (at ?a ?s) (cost_100 ?a) (next_visit_state ?v1 ?v2))\n\t\t")
                    .append(":effect (and (cur_state ?v2) (not (cur_state ?v1)) (visited ?a) (increase (total-cost) 100))\n\t)\n\n\t");

            // Define parameterized move action
            sb.append("(:action move\n\t\t")
                    .append(":parameters (?from - topology_state ?to - topology_state)\n\t\t")
                    .append(":precondition (and (cur_state ?from) (connected ?from ?to))\n\t\t")
                    .append(":effect (and (cur_state ?to) (not (cur_state ?from)) (increase (total-cost) 1))\n\t)\n");

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
                    switch (a.getRating()) {
                        case 10:
                            sb.append("\t\t(cost_10 ").append(a.toString()).append(")\n");
                            break;
                        case 50:
                            sb.append("\t\t(cost_50 ").append(a.toString()).append(")\n");
                            break;
                        case 100:
                            sb.append("\t\t(cost_100 ").append(a.toString()).append(")\n");
                            break;
                    }
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

            // Define 'next_visit_state' predicate
            for (int i = 0; i < visite; i++) {
                sb.append("\t\t(next_visit_state v").append(i).append(" v").append(i + 1).append(")\n");
            }

            sb.append("\t)\n\n\t");
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
