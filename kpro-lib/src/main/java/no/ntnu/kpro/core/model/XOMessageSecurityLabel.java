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
    CHOOSE_ONE("Choose Security Label", "I SHOULD NOT SHOW, BECAUSE I CANNOT BE CHOSEN", null, -1),
    UGRADERT("UGRADERT", "ug", "marking=\"UGRADERT\"; fgcolor=black; bgcolor=white; type=\":ess\"; label=\"MQ8CAQEGCmCEQgECARyEQgE=\"", 1),
    BEGRENSET("BEGRENSET", "b", "marking=\"BEGRENSET\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ8CAQIGCmCEQgECARyEQgE=\"", 2),
    KONFIDENSIELT("KONFIDENSIELT", "k", "marking=\"KONFIDENSIELT\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ8CAQMGCmCEQgECARyEQgE=\"", 3),
    UNCLASSIFIED("UNCLASSIFIED", "uc", "marking=\"UNCLASSIFIED\"; fgcolor=black; bgcolor=white; type=\":ess\"; label=\"MQ4CAQEGCWCEQgECARwAAQ==\"", 1),
    RESTRICTED("RESTRICTED", "r", "marking=\"RESTRICTED\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ4CAQIGCWCEQgECARwAAQ==\"", 2), 
    CONFIDENTIAL("CONFIDENTIAL", "c", "marking=\"CONFIDENTIAL\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ4CAQMGCWCEQgECARwAAQ==\"", 3),
    NATO_UNCLASSIFIED("NATO_UNCLASSIFIED", "nu", "marking=\"NATO UNCLASSIFIED\"; fgcolor=black; bgcolor=white; type=\":ess\"; label=\"MQ0CAQEGCCsaAKI2AAUB\"", 1),
    NATO_RESTRICTED("NATO_RESTRICTED", "nr", "marking=\"NATO RESTRICTED\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ0CAQIGCCsaAKI2AAUB\"", 2),
    NATO_CONFIDENTIAL("NATO_CONFIDENTIAL", "nc", "marking=\"NATO CONFIDENTIAL\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ0CAQMGCCsaAKI2AAUB\"", 3);
    
    private String val;
    private String shortVal;
    private String headerValue;
    private int sortVal;
    
    private XOMessageSecurityLabel(String value, String shortValue, String headerValue, int sortVal){
        this.val = value;
        this.shortVal = shortValue;
        this.headerValue = headerValue;
        this.sortVal = sortVal;
    }
    public int getSortVal() {
        return this.sortVal;
    }
    @Override
    public String toString(){
        return this.val;
    }

    public String getShortValue(){
        return this.shortVal;
    }
    
    public String getHeaderValue() {
        return this.headerValue;
    }
}
