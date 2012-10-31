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
    DEFERRED("Deferred", 0, "a"),
    ROUTINE("Routine", 1, "b"),
    PRIORITY("Priority", 2, "c"),
    IMMEDIATE("Immediate", 3, "d"),
    FLASH("Flash", 4, "e"),
    OVERRIDE("Override", 5, "f");
    
    private String val;
    private int numValue;
    private String shortVal;
    
    private XOMessagePriority(String value, int numValue, String shortVal){
        this.val = value;
        this.numValue = numValue;
        this.shortVal = shortVal;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
    
    public String getNumValue(){
        return "" + this.numValue;
    }
    public int getNumeric() {
        return this.numValue;
    }
    public String getAlpha() {
        return this.shortVal;
    }
}
