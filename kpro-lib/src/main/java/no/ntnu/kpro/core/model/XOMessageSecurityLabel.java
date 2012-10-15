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
    CHOOSE_ONE("Choose Security Label", "I SHOULD NOT SHOW, BECAUSE I CANNOT BE CHOSEN", null),
    UGRADERT("Ugradert", "ug", "marking=\"UGRADERT\"; fgcolor=black; bgcolor=white; type=\":ess\"; label=\"MQ8CAQEGCmCEQgECARyEQgE=\""),
    BEGRENSET("Begrenset", "b", "marking=\"BEGRENSET\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ8CAQIGCmCEQgECARyEQgE=\""),
    KONFIDENSIELT("Kondfidensielt", "k", "marking=\"KONFIDENSIELT\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ8CAQMGCmCEQgECARyEQgE=\""),
    UNCLASSIFIED("Unclassified", "uc", "marking=\"UNCLASSIFIED\"; fgcolor=black; bgcolor=white; type=\":ess\"; label=\"MQ4CAQEGCWCEQgECARwAAQ==\""),
    RESTRICTED("Restricted", "r", "marking=\"RESTRICTED\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ4CAQIGCWCEQgECARwAAQ==\""), 
    CONFIDENTIAL("Confidential", "c", "marking=\"CONFIDENTIAL\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ4CAQMGCWCEQgECARwAAQ==\""),
    NATO_UNCLASSIFIED("Nato Unclassified", "nu", "marking=\"NATO UNCLASSIFIED\"; fgcolor=black; bgcolor=white; type=\":ess\"; label=\"MQ0CAQEGCCsaAKI2AAUB\""),
    NATO_RESTRICTED("Nato Restricted", "nr", "marking=\"NATO RESTRICTED\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ0CAQIGCCsaAKI2AAUB\""),
    NATO_CONFIDENTIAL("Nato Confidential", "nc", "marking=\"NATO CONFIDENTIAL\"; fgcolor=red; bgcolor=white; type=\":ess\"; label=\"MQ0CAQMGCCsaAKI2AAUB\"");
    
    private String val;
    private String shortVal;
    private String headerValue;
    
    private XOMessageSecurityLabel(String value, String shortValue, String headerValue){
        this.val = value;
        this.shortVal = shortValue;
        this.headerValue = headerValue;
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
