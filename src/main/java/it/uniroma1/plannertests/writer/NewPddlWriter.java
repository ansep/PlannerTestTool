/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.writer;

import it.uniroma1.plannertests.model.Attrazione;
import it.uniroma1.plannertests.model.Museo;
import it.uniroma1.plannertests.model.stanze.Stanza;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thomas
 * Modifica con taglio del grafo
 */
public class NewPddlWriter extends AbstractWriter{
    
   private Map<Integer,List<Integer>> adiacenze;
   private Map<Integer, Integer> shortcut;
   
    public NewPddlWriter(Museo museo, int visite, String pddlDirectory) throws IOException {
        super(museo, visite, pddlDirectory);
        problemWriter = new BufferedWriter(new FileWriter(problemPath));
        domainWriter = new BufferedWriter(new FileWriter(domainPath));
    }
    
    @Override
    protected String createFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append('/').append(museo.getNome()).append('_')
            .append(visite).append("_new");
        File f = new File(basePath + sb.toString());
        f.mkdir();
        return f.getPath();
    }
    
    private void recursiveLink(int currentNode, int leafNode, int startNode, Map<Integer, List<Integer>> reverse,
            ArrayDeque<Integer> grey, ArrayDeque<Integer> black) {
        if(currentNode == startNode) {
            shortcut.put(leafNode, currentNode);
            return;
        }
        
        int outDegree = adiacenze.get(currentNode).size();
        //se il nodo ha piu di un figlio, allora e' un bivio
        if(outDegree > 1) {
            shortcut.put(leafNode, currentNode);
            if(!grey.contains(currentNode) && !black.contains(currentNode))
                grey.add(currentNode);
            return;
        }
        int parent = reverse.get(currentNode).get(0);
        recursiveLink(parent, leafNode, startNode, reverse, grey, black);
    }
    
    private void preprocess() {
        adiacenze = new HashMap<>();
        shortcut = new HashMap<>();
        List<Integer> l;
        Stanza[] roomArray = Arrays.copyOfRange(museo.getStanze(), 0 , museo.getOpenRooms());
        
        for(Stanza s : roomArray) {
            int sala = s.getId();
            l = new ArrayList<>();
            for(Stanza sa : s.getStanzeAdiacenti()) {
                if(sa != null && sa.getId() <= museo.getOpenRooms())
                    l.add(sa.getId());
            }
            adiacenze.put(sala, l);
        }
        Map<Integer,List<Integer>> listaPrev = new HashMap<>();
        
        for(int key : adiacenze.keySet()) {
            for(int value : adiacenze.get(key)) {
                l = listaPrev.get(value);
                if(l == null)
                    l = new ArrayList<>();
                l.add(key);
                listaPrev.put(value, l);
            }
        }
        
        //printList(adiacenze);
        //printList(listaPrev);
        
        ArrayDeque<Integer> grey = new ArrayDeque<>();
        ArrayDeque<Integer> black = new ArrayDeque<>();
        int startNode = roomArray[0].getId();
        //ricerca delle foglie
        for(int i : adiacenze.keySet()) {
            //se è nodo foglia, non ha adiacenze
            if(adiacenze.get(i).isEmpty())
                grey.add(i);
        }
        while(!grey.isEmpty()) {
            int nodo = grey.poll();
            int prev = -1;
            for(int x : listaPrev.get(nodo)) {
                prev = x;
                break;
            }
            recursiveLink(prev, nodo, startNode, listaPrev, grey, black);
            black.add(nodo);
        }
        //printList(adiacenze);
    }
    
    private static void printList(Map<Integer, List<Integer>> map) {
        for(int x : map.keySet()) {
            System.out.print(x + ":\t");
            for(int y : map.get(x)) {
                System.out.print(y + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    @Override
    protected final String append() {
        String path = super.append();
        path += "_new";
        return path;
    }

    @Override
    public String writeDomain(){
        preprocess();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("(define (domain Museum)\n\t")
                    .append("(:requirements :typing :equality)\n\t")
                    .append("(:types topology_state visit_state - state attraction)\n\t")
                    .append("(:predicates\n\t\t")
                    .append("(cur_state ?s - state)\n\t\t")
                    .append("(visited ?a - attraction)\n\t\t")
                    .append("(room_visited ?a - state)\n\t)\n\t")
                    .append("(:functions\n\t\t")
                    .append("(total-cost)\n\t)\n\t");
            domainWriter.write(sb.toString());
            sb.setLength(0);
            
            //Action visit
            Stanza[] roomArray = Arrays.copyOfRange(museo.getStanze(), 0 , museo.getOpenRooms());
            for(Stanza s : roomArray) {
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
                                .append("(increase (total-cost) ").append(a.getRating()).append("))\n\t)\n\t");
                    }
                }
            }
            domainWriter.write(sb.toString());
            sb.setLength(0);
            //Action move
            for(int sala : adiacenze.keySet()) {
                    for (int adiacente : adiacenze.get(sala)) {
                        String from = "stanza_" + sala;
                        String to = "stanza_" + adiacente;
                        sb.append("(:action move-").append(from).append('-').append(to).append("\n\t")
                            .append(":precondition (and (cur_state ").append(from).append(") ")
                            .append("(not(room_visited ").append(to).append(")))\n\t")
                            .append(":effect (and (cur_state ").append(to).append(") ")
                            .append("(not (cur_state ").append(from).append(")) ")
                            .append("(room_visited ").append(from).append(") ")
                            .append("(increase (total-cost) ").append("1").append("))\n\t)\n\n\t");
                    }
            }
            
            shortcut.forEach((sala, adiacente) -> {
                String from = "stanza_" + sala;
                String to = "stanza_" + adiacente;
                sb.append("(:action move-").append(from).append('-').append(to).append("\n\t")
                    .append(":precondition (cur_state ").append(from).append(") \n\t")
                    .append(":effect (and (cur_state ").append(to).append(") ")
                    .append("(not (cur_state ").append(from).append(")) ")
                        .append("(room_visited ").append(from).append(") ")
                    .append("(increase (total-cost) ").append("1").append("))\n\t)\n\n\t");
            });
            sb.append("\n)");
            domainWriter.write(sb.toString());
            domainWriter.close();
        } catch(IOException e) {
            e.printStackTrace();;
        }
        return domainPath;
    }

    @Override
    public String writeProblem() {
        StringBuilder sb = new StringBuilder();
        Stanza[] roomArray = Arrays.copyOfRange(museo.getStanze(), 0 , museo.getOpenRooms());
        try {
            sb.append("(define (problem Visit) (:domain Museum)\n\t")
                    .append("(:objects\n\t\t");
            for(Stanza s : roomArray) {
                String roomName = s.toString();
                sb.append(roomName).append(" ");
            }
            sb.append("- topology_state\n\t\t");
            for(int i = 0; i <= visite; i++)
                sb.append("v").append(i).append(" ");
            sb.append("- visit_state\n\t\t");
            for(Stanza s : roomArray)
                for(Attrazione a : s.getAttrazioni())
                    sb.append(a.toString()).append(" ");
            sb.append("- attraction\n\t)\n\t");
            
            sb.append("(:init\n\t\t")
                    .append("(cur_state ").append(museo.getStanze()[0].toString()).append(")\n\t\t")
                    .append("(cur_state v0)\n\n\t\t")
                    .append("(= (total-cost) 0)\n\t)\n\n\t");
            
            sb.append("(:goal\n\t\t(and\n\t\t")
                    //.append("(cur_state ").append(museo.getStanze()[0].toString()).append(")\n\t\t")
                    .append("(cur_state v").append(visite).append(")\n\t\t)");
            sb.append("\n\t)(:metric minimize (total-cost))\n)");
            
            problemWriter.write(sb.toString());
            problemWriter.close();
        } catch(IOException e) {
            e.printStackTrace();;
        }
        return problemPath;
    }
}
