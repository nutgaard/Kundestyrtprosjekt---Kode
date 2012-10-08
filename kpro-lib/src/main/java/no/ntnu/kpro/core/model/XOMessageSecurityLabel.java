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
    CHOOSE_ONE("Choose Security Label", "I SHOULD NOT SHOW, BECAUSE I CANNOT BE CHOSEN"),
    UGRADERT("Ugradert", "ug"),
    BEGRENSET("Begrenset", "b"),
    KONFIDENSIELT("Kondfidensielt", "k"),
    UNCLASSIFIED("Unclassified", "uc"),
    RESTRICTED("Restricted", "r"), 
    CONFIDENTIAL("Confidential", "c"),
    NATO_UNCLASSIFIED("Nato Unclassified", "nu"),
    NATO_RESTRICTED("Nato Restricted", "nr"),
    NATO_CONFIDENTIAL("Nato Confidential", "nc");
    
    private String val;
    private String shortVal;
    
    private XOMessageSecurityLabel(String value, String shortValue){
        this.val = value;
        this.shortVal = shortValue;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
    
    public String getShortValue(){
        return this.shortVal;
    }
}
