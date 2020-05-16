/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.payment;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializableRead;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author cesar3r2
 */
public class JPaymentGiftCardPanel implements SerializableRead, Externalizable {
    private Integer id;
    private String serial;
    private double amount;
    private Integer status;
    private Integer giftcardID;
    private java.util.Date Updated;
    
    public void readValues(DataRead dr) throws BasicException {
        id = dr.getInt(1);
        serial = dr.getString(2);
        amount = dr.getDouble(3);
        status = dr.getInt(4);
        //giftcardID = dr.getInt(5);
        //Updated = dr.getTimestamp(6);
    }

    public java.util.Date getUpdated() {
        return Updated;
    }

    public void setUpdated(java.util.Date updated) {
        this.Updated = updated;
    } 
    
    public JPaymentGiftCardPanel() {
        id = 0;
        serial = null;
        amount = 0;
        status = 0;
    }

    public JPaymentGiftCardPanel(Integer bInt, String string, Double aDouble, Integer aInt) {
        this.id = bInt;
        this.serial = string;
        this.amount = aDouble;
        this.status = aInt;
    }
    
    public Integer getId()
    {
    	return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getSerial()
    {
    	return serial;
    }

    public void setSerial(String serialNew)
    {
        serial = serialNew;
    }
    
    public double getAmount()
    {
    	return amount;
    }

    public void setAmount(double amountNew)
    {
        amount = amountNew;
    }
    
    public Integer getStatus()
    {
    	return status;
    }

    public void setStatus(Integer statusNew)
    {
        status = statusNew;
    }
    
    public Integer getGiftCardID()
    {
    	return giftcardID;
    }

    public void setGiftCardID(Integer giftcardID)
    {
        this.giftcardID = giftcardID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeChars(serial);
        out.writeDouble(amount);
        out.writeChar(status);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();
        serial = (String) in.readObject();
        amount = in.readInt();
        status = in.readInt();
    }
}
