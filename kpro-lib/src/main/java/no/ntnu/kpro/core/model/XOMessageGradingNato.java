/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model.Grading;

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
    
    public String getValue(){
        return this.val;
    }
}
