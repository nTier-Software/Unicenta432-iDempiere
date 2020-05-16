/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync.externalsales;

/**
 *
 * @author alara
 */
public class MovementIdentifier implements java.io.Serializable{
    private static final long serialVersionUID = 9341677250499L;
    private java.util.Calendar dateNew;

    public MovementIdentifier() {
    }

    public MovementIdentifier(
            java.util.Calendar dateNew,
            java.lang.String documentNo) {
        this.dateNew = dateNew;       
    }    

    /**
     * Gets the dateNew value for this MovementIdentifier.
     * 
     * @return dateNew
     */
    public java.util.Calendar getDateNew() {
        return dateNew;
    }

    /**
     * Sets the dateNew value for this MovementIdentifier.
     * 
     * @param dateNew
     */
    public void setDateNew(java.util.Calendar dateNew) {
        this.dateNew = dateNew;
    }   
    private java.lang.Object __equalsCalc = null;
}
