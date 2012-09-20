/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author aleksandersjafjell
 */
public enum XOMessageGradingNorwegian {
    UGRADERT("Ugradert"),
    BEGRENSET("Begrenset"),
    KONFIDENSIELT("Kondfidensielt");
    
    private String val;
    
    private XOMessageGradingNorwegian(String value){
        this.val = value;
    }
    
    @Override
    public String toString(){
        return this.val;
    }
        
}
