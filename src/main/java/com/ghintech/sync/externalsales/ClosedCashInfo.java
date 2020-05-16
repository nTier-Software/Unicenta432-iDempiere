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

import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.SerializerRead;

/**
 *
 * @author adrianromero
 */
public class ClosedCashInfo implements SerializableRead, Externalizable {

    private static final long serialVersionUID = 3551922341181632195L;

    private static DateFormat m_dateformat = new SimpleDateFormat("hh:mm");

    private String m_sId;
    private String m_iClosedCashId;
    private int m_iHostSeguence;
    private Date m_dDateStart;
    private Date m_dDateEnd;
    private double difference;
    private String m_UserID;
    private Properties attributes;

    /** Creates new TicketModel */
    public ClosedCashInfo() {
        //m_sId = UUID.randomUUID().toString();
    }

    public ClosedCashInfo(String id) {
        m_sId = id;
        m_iClosedCashId = null;
        m_dDateStart = null;
        m_dDateEnd = null;
        difference = 0;
        m_UserID = null;
        attributes = null;
    }

    public ClosedCashInfo(String m_sId, String m_iClosedCashId, int m_iHostSeguence, Date m_dDateStart, Date m_dDateEnd, double difference, String m_UserID) {
        this.m_sId = m_sId;
        this.m_iClosedCashId = m_iClosedCashId;
        this.m_iHostSeguence = m_iHostSeguence;
        this.m_dDateStart = m_dDateStart;
        this.m_dDateEnd = m_dDateEnd;
        this.difference = difference;
        this.m_UserID = m_UserID;
    }

    public void readValues(DataRead dr) throws BasicException {
        m_sId = dr.getString(1);
        m_iClosedCashId = dr.getString(1);
        m_dDateStart = dr.getTimestamp(4);
    }

    public String getId() {
        return m_sId;
    }

    public String getClosedCashId() {
        return m_iClosedCashId;
    }

    public void setClosedCashId(String closedCashId) {
        m_iClosedCashId = closedCashId;
    }

    public int getHostSeguence() {
        return m_iHostSeguence;
    }

    public void setHostSeguence(int m_iHostSeguence) {
        this.m_iHostSeguence = m_iHostSeguence;
    }

    public java.util.Date getDateStart() {
        return m_dDateStart;
    }

    public void setDateStart(java.util.Date dDate) {
        m_dDateStart = dDate;
    }

    public Date getDateEnd() {
        return m_dDateEnd;
    }

    public void setDateEnd(Date m_dDateEnd) {
        this.m_dDateEnd = m_dDateEnd;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public String getUserID() {
        return m_UserID;
    }

    public void setUserID(String m_User) {
        this.m_UserID = m_User;
    }

    public String getName(Object info) {

        StringBuffer name = new StringBuffer();

        if (info == null) {
            if (m_iClosedCashId == null) {
                name.append("(" + m_dateformat.format(m_dDateStart) + " " + Long.toString(m_dDateStart.getTime() % 1000) + ")");
            } else {
                name.append(m_iClosedCashId);
            }
        } else {
            name.append(info.toString());
        }
        
        return name.toString();
    }

    public String getName() {
        return getName(null);
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }

    public Properties getProperties() {
        return attributes;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            public Object readValues(DataRead dr) throws BasicException {
                ClosedCashInfo c = new ClosedCashInfo(dr.getString(1));
                c.setClosedCashId(dr.getString(1));
                c.setHostSeguence(dr.getInt(2));
                c.setDateStart(dr.getTimestamp(3));
                c.setDateEnd(dr.getTimestamp(4));
                c.setDifference(dr.getDouble(5));
                c.setUserID(dr.getString(6));
          return c;
        }};
    }

}
