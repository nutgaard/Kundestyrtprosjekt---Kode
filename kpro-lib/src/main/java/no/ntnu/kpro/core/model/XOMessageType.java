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
    EXERCISE("Exercise"),
    OPERATION("Operation"), 
    PROJECT("Project"), 
    DRILL("Drill");
    
    private String val;
    
    private XOMessageType(String value){
        this.val = value;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
}
