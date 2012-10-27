/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.model;

/**
 *
 * @author Administrator
 */
public class Contact {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    public String name;
    private String email;
    private int id;

    public Contact() {
    }

    public Contact(String name, String email, int id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public Contact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
       this.id = id;
    }
}

