/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.service.implementation.PersistenceService.PersistentWriteThroughStorage;

/**
 *
 * @author Nicklas
 */
public class DummyObject implements DummyInterface {
    private String name;
    private int age;
    private String workTitle;
    
    public DummyObject(String name, int age, String workTitle){
        this.name = name;
        this.age = age;
        this.workTitle = workTitle;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }
    
}
