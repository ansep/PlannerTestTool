/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniroma1.plannertests.model.stanze;

import java.util.ArrayList;

/**
 *
 * @author ansep
 */
public class Stanza2 extends Stanza{
    
    public Stanza2(int id) {
        super(id);
        stanzeAdiacenti = new ArrayList<>();
    }
    
}
