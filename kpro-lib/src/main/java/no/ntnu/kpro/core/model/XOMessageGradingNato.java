/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author aleksandersjafjell
 */
public enum XOMessageGradingNato {
    NATO_UNCLASSIFIED("Nato Classified"),
    NATO_RESTRICTED("Nato Restricted"),
    NATO_CONFIDENTIAL("Nato Confidential");
    
    private String val;
    private XOMessageGradingNato(String value){
        this.val = value;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
}
