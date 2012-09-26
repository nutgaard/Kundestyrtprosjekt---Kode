/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author Aleksander
 * Note. There also exists separate enums for each of the grading classes.
 * Unsure of which to keep as of now.
 */
public enum XOMessageSecurityLabel {
    UGRADERT("Ugradert"),
    BEGRENSET("Begrenset"),
    KONFIDENSIELT("Kondfidensielt"),
    UNCLASSIFIED("Unclassified"),
    RESTRICTED("Restricted"), 
    CONFIDENTIAL("Confidential"),
    NATO_UNCLASSIFIED("Nato Classified"),
    NATO_RESTRICTED("Nato Restricted"),
    NATO_CONFIDENTIAL("Nato Confidential");
    
    private String val;
    
    private XOMessageSecurityLabel(String value){
        this.val = value;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
}
