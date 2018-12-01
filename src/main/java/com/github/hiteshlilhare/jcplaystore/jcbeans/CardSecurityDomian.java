/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.jcbeans;

/**
 *
 * @author Hitesh
 */
public class CardSecurityDomian {
    private boolean fresh;
    private String aid;
    private String lifeCycleState;
    private String privileges;
    private String privilegesString;

    public boolean isFresh() {
        return fresh;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getLifeCycleState() {
        return lifeCycleState;
    }

    public void setLifeCycleState(String lifeCycleState) {
        this.lifeCycleState = lifeCycleState;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    public String getPrivilegesString() {
        return privilegesString;
    }

    public void setPrivilegesString(String privilegesString) {
        this.privilegesString = privilegesString;
    }
    
}
