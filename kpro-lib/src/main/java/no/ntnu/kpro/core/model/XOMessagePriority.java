/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author Aleksander
 */
public enum XOMessagePriority {
    DEFERRED("Deferred", 0),
    ROUTINE("Routine", 1),
    PRIORITY("Priority", 2),
    IMMEDIATE("Immediate", 3),
    FLASH("Flash", 4),
    OVERRIDE("Override", 5);
    
    private String val;
    private int numValue;
    
    private XOMessagePriority(String value, int numValue){
        this.val = value;
        this.numValue = numValue;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
    
    public String getNumValue(){
        return "" + this.numValue;
    }
}
