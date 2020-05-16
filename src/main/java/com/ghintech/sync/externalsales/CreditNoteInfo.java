//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.
package com.ghintech.sync.externalsales;

import com.openbravo.data.loader.IKeyed;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author adrianromero
 */
public class CreditNoteInfo implements Serializable, IKeyed  {
    private static final long serialVersionUID = -3449743568263820815L;

    private String name;
    private String m_sReceipt;
    private Double m_dTotal;
    private Double m_dAvail;
    private Date m_DateNew;
    private int m_nNumber;
    private String m_sBranch;

    public CreditNoteInfo() {
    }

    public CreditNoteInfo(int m_nNumber, String name, String m_sReceipt, Double m_dTotal, Double m_dAvail, Date m_DateNew, String m_sBranch) {
        this.name = name;
        this.m_sReceipt = m_sReceipt;
        this.m_dTotal = m_dTotal;
        this.m_dAvail = m_dAvail;
        this.m_DateNew = m_DateNew;
        this.m_nNumber = m_nNumber;
        this.m_sBranch = m_sBranch;
    }

    public Object getKey() {
        return name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceipt() {
        return m_sReceipt;
    }

    public void setReceipt(String m_sReceipt) {
        this.m_sReceipt = m_sReceipt;
    }

    public Double getTotal() {
        return m_dTotal;
    }

    public void setTotal(Double m_dTotal) {
        this.m_dTotal = m_dTotal;
    }

    public Double getAvail() {
        return m_dAvail;
    }

    public void setAvail(Double m_dAvail) {
        this.m_dAvail = m_dAvail;
    }

    public Date getDateNew() {
        return m_DateNew;
    }

    public void setDateNew(Date m_DateNew) {
        this.m_DateNew = m_DateNew;
    }

    public int getNumber() {
        return m_nNumber;
    }

    public void setNumber(int m_nNumber) {
        this.m_nNumber = m_nNumber;
    }

    public String getBranch() {
        return m_sBranch;
    }

    public void setBranch(String m_sBranch) {
        this.m_sBranch = m_sBranch;
    }

    
    @Override
    public String toString(){
        return name;
    }
}
