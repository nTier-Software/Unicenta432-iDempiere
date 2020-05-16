/**
 * User.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package com.ghintech.sync.people;

import com.ghintech.sync.customers.BusinessPartner;

public class User extends BusinessPartner implements java.io.Serializable {

    private static final long serialVersionUID = 5260054797000073180L;

    public User() {
    }

    public User(
            java.lang.String clientId,
            java.lang.Boolean complete,
            com.ghintech.sync.customers.Contact[] contacts,
            java.lang.Boolean customer,
            java.lang.String description,
            java.lang.String id,
            com.ghintech.sync.customers.Location[] locations,
            java.lang.String name,
            java.lang.String searchKey,
            java.lang.Boolean vendor) {
        super(
                clientId,
                complete,
                contacts,
                customer,
                description,
                id,
                locations,
                name,
                searchKey,
                vendor);
    }
    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
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
        _equals = super.equals(obj);
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
        __hashCodeCalc = false;
        return _hashCode;
    }    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(User.class, true);
    

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("/services/WebService", "Customer"));
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
}
