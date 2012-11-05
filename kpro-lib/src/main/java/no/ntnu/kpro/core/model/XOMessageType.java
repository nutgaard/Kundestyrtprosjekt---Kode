/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author Nicklas
 */
public enum XOMessageType {
    EXERCISE("Exercise", 0),
    OPERATION("Operation", 1), 
    PROJECT("Project", 2), 
    DRILL("Drill", 3);
    
    private String val;
    private int numVal;
    
    private XOMessageType(String value, int numVal){
        this.val = value;
        this.numVal = numVal;
    }
    public int getNumval() {
        return this.numVal;
    }
    @Override
    public String toString(){
        return this.val;
    }
}
