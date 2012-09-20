/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author Nicklas
 */
public enum XOMessageGrading {
    NONE, // TODO: All messages should have a classification
    UGRADERT, BEGRENSET, KONFIDENSIELT, 
    UNCLASSIFIED, RESTRICTED, CONFIDENTIAL, 
    NATO_UNCLASSIFIED, NATO_RESTRICTED, NATO_CONFIDENTIAL;
}
