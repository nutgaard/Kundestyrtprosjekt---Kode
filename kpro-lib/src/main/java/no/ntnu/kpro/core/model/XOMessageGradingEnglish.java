/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model.Grading;

/**
 *
 * @author aleksandersjafjell
 */
public enum XOMessageGradingEnglish {
    UNCLASSIFIED("Unclassified"),
    RESTRICTED("Restricted"), 
    CONFIDENTIAL("Confidential");
    
    private String val;
    private XOMessageGradingEnglish(String value){
        this.val = value;
    }
    
    public String getValue(){
        return this.val;
    }
    
}
