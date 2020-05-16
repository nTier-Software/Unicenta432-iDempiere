/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync.movement;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.pos.inventory.MaterialProdInfo;
import com.openbravo.pos.ticket.UserInfo;

/**
 *
 * @author alara
 */
public class MaterialProdInfoExt extends MaterialProdInfo{
    private String m_sID;
    private int m_sReason;
    private java.util.Date m_dDate;
    private String m_sLocation; 
    private String m_sProduct; 
    private double m_dAmount;
    private double m_dPriceBuy;
    private UserInfo m_User; 
    
    
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        m_sReason = dr.getInt(2);
        m_dDate = dr.getTimestamp(3);
        m_sLocation = dr.getString(4);
        m_sProduct = dr.getString(5);
        m_dAmount = (dr.getDouble(6) == null) ? 1.0 : dr.getDouble(6);
        m_dPriceBuy = dr.getDouble(7);
        m_User = new UserInfo(dr.getString(8), dr.getString(9));        
    }
    /**
     *
     * @return
     */
    public java.util.Date getDate() {
        return m_dDate;
    }

    /**
     *
     * @param dDate
     */
    public void setDate(java.util.Date dDate) {
        m_dDate = dDate;
    }
    
    public java.lang.String getProductId() {
        return m_sProduct;
    }

    /**
     * Sets the orderLineId value for this OrderLine.
     * 
     * @param m_sProduct
     */
    public void setProductId(java.lang.String m_sProduct) {
        this.m_sProduct = m_sProduct;
    }    
    
    @Override
    public double getAmount() {
        return m_dAmount;
    }  
    
    public UserInfo getUser() {
        return m_User;
    }

    public java.lang.String getLocatorId() {
        return m_sLocation;
    }      


    /**
     *
     * @return
     */
    @Override
    public String getID() {
        return m_sID;
    }    
    
}
