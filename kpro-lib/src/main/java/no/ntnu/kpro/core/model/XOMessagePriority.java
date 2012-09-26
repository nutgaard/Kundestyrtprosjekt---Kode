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
    DEFERRED("Deferred"),
    ROUTINE("Routine"),
    PRIORITY("Priority"),
    IMMEDIATE("Immediate"),
    FLASH("Flash"),
    OVERRIDE("Override");
    
    private String val;
    
    private XOMessagePriority(String value){
        this.val = value;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
}
