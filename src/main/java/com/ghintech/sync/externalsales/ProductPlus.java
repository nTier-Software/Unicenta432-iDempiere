/**
 * ProductPlus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.ghintech.sync.externalsales;

import java.util.Date;

/*
 * @contributor Sergio Oropeza - Double Click Sistemas - Venezuela - soropeza@dcs.net.ve, info@dcs.net.ve
 */
public class ProductPlus extends com.ghintech.sync.externalsales.Product implements java.io.Serializable {

    private static final long serialVersionUID = 9203746223092L;
    private double qtyonhand;
    private String reference;
    private String location_id;
    private String location_name;        
    private boolean isService;
    
    public ProductPlus() {
    }

    public ProductPlus(
            com.ghintech.sync.externalsales.Category category,
            java.lang.String description,
            java.lang.String ean,
            java.lang.String id,
            java.lang.String imageUrl,
            double listPrice,
            java.lang.String name,
            java.lang.String number,
            double purchasePrice,
            com.ghintech.sync.externalsales.Tax tax,
            double qtyonhand,          
            java.lang.String reference,
            java.lang.String location_id,
            java.lang.String location_name,
            java.lang.String isScale) {
        super(
                category,
                description,
                ean,
                id,
                imageUrl,
                listPrice,
                name,
                number,
                purchasePrice,
                tax,
                isScale);
        this.qtyonhand = qtyonhand;
        this.reference = reference;
        this.location_id = location_id;
        this.location_name = location_name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    /**
     * Gets the qtyonhand value for this ProductPlus.
     * 
     * @return qtyonhand
     */
    public double getQtyonhand() {
        return qtyonhand;
    }

    /**
     * Sets the qtyonhand value for this ProductPlus.
     * 
     * @param qtyonhand
     */
    public void setQtyonhand(double qtyonhand) {
        this.qtyonhand = qtyonhand;
    }

    /**
     * Gets the reference value for this ProductPlus.
     * 
     * @return reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference value for this ProductPlus.
     * 
     * @param qtyonhand
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    

    
    
    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProductPlus)) {
            return false;
        }
        ProductPlus other = (ProductPlus) obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) &&
                this.qtyonhand == other.getQtyonhand();
        __equalsCalc = null;
        return _equals;
    }
    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        _hashCode += new Double(getQtyonhand()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(ProductPlus.class, true);
    

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("/services/ExternalSales", "ProductPlus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qtyonhand");
        elemField.setXmlName(new javax.xml.namespace.QName("", "qtyonhand"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(
                _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(
                _javaType, _xmlType, typeDesc);
    }

    public boolean isIsService() {
        return isService;
    }

    public void setIsService(boolean isService) {
        this.isService = isService;
    }
    
    
}