/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.forms.AppView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.LoginRequest;
import org.idempiere.webservice.client.net.WebServiceConnection;



/**
 *
 * @author egil
 */
public class SyncCP {
    protected DataLogicIntegration dlintegration;
    protected DataLogicSystem dlsystem;
    private String UrlBase;
    private int AD_Client_ID;
    private int AD_Org_ID;
    
    private int AD_Role_ID;
    private String Language;
    private String UserName;
    private String UserPass;
    private int M_Warehouse_ID;
    private int AD_Country_ID;
    private int AD_Region_ID;
    private int AD_City_ID;
    private int C_DocType_ID;
    private int C_DocTypeRefund_ID;
    private int C_DocTypeMovement_ID;
    private int M_PriceList_Version_ID;
    private String wsProductType;
    private String wsCustomerType;
    private String wsOrderType;
    private String wsPeopleType;
    private String wsGiftCardWrite;
    private String wsGiftCardType;
    private double wsGiftCardTypeInterval;
    private String wsClosedcashType;
    private String wsMovementType;
    private String wsImportOrderType;
    private double wsImportOrderTypeInterval;
    private String wsImportOrderAction;
    private double wsOrderTypeInterval;
    private double wsPeopleTypeInterval;
    private double wsProductTypeInterval;
    private double wsCustomerTypeInterval;
    private double wsClosedcashTypeInterval;
    private double wsMovementTypeInterval;
    private String CityName;
    private final String MainLocationID;
    private String wsCompositeOrderType;
    private String wsCurrentOrder;
    private final Properties erpProperties;
    private String wsImportMovementType;
    private String wsCurrentGiftCard;
    private String wsCheckProduct;
    
    public SyncCP(AppView app) {
        dlintegration = (DataLogicIntegration) app.getBean("com.ghintech.sync.DataLogicIntegration");
        dlsystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        Properties propsdb = dlsystem.getResourceAsProperties(getHostName() + "/properties");
        MainLocationID = propsdb.getProperty("location");        
        erpProperties = dlsystem.getResourceAsProperties("erp.properties");
        setUrlBase(erpProperties.getProperty("UrlBase"));
        setAD_Client_ID(Integer.valueOf(erpProperties.getProperty("AD_Client_ID")));
        setAD_Org_ID(Integer.valueOf(erpProperties.getProperty("AD_Org_ID")));
        setC_DocType_ID(Integer.valueOf(erpProperties.getProperty("C_DocType_ID")));
        setC_DocTypeRefund_ID(Integer.valueOf(erpProperties.getProperty("C_DocTypeRefund_ID")));
        setC_DocTypeMovement_ID(Integer.valueOf(erpProperties.getProperty("C_DocTypeMovement_ID")));
        setCityName(erpProperties.getProperty("CityName"));
        setAD_Role_ID(Integer.valueOf(erpProperties.getProperty("AD_Role_ID")));
        setM_Warehouse_ID(Integer.valueOf(erpProperties.getProperty("M_Warehouse_ID")));
        setLanguage(erpProperties.getProperty("Language"));
        setUserName(erpProperties.getProperty("UserName"));
        setUserPass(erpProperties.getProperty("UserPass"));
        setM_PriceList_Version_ID(Integer.valueOf(erpProperties.getProperty("M_PriceList_Version_ID")));     
        setAD_Country_ID(Integer.valueOf(erpProperties.getProperty("AD_Country_ID")));
        setAD_Region_ID(Integer.valueOf(erpProperties.getProperty("AD_Region_ID")));
        setAD_City_ID(Integer.valueOf(erpProperties.getProperty("AD_City_ID")));        
        setWsProductType(erpProperties.getProperty("wsProductType"));
        setWsCustomerType(erpProperties.getProperty("wsCustomerType"));
        setWsPeopleType(erpProperties.getProperty("wsPeopleType"));
        setWsGiftCardWrite(erpProperties.getProperty("wsGiftCardWrite"));
        setWsGiftCardType(erpProperties.getProperty("wsGiftCardType"));
        setWsGiftCardTypeInterval(Double.valueOf(erpProperties.getProperty("wsGiftCardTypeInterval")));
        setWsOrderType(erpProperties.getProperty("wsOrderType"));        
        setWsClosedcashType(erpProperties.getProperty("wsClosedcashType"));
        setWsMovementType(erpProperties.getProperty("wsMovementType"));
        setwsImportOrderType(erpProperties.getProperty("wsImportOrderType"));
        setwsImportOrderTypeInterval(Double.valueOf(erpProperties.getProperty("wsImportOrderTypeInterval")));        
        setWsOrderTypeInterval(Double.valueOf(erpProperties.getProperty("wsOrderTypeInterval")));
        setWsProductTypeInterval(Double.valueOf(erpProperties.getProperty("wsProductTypeInterval")));
        setWsCustomerTypeInterval(Double.valueOf(erpProperties.getProperty("wsCustomerTypeInterval")));
        setWsPeopleTypeInterval(Double.valueOf(erpProperties.getProperty("wsPeopleTypeInterval")));
        setWsClosedcashTypeInterval(Double.valueOf(erpProperties.getProperty("wsClosedcashTypeInterval")));
        setWsMovementTypeInterval(Double.valueOf(erpProperties.getProperty("wsMovementTypeInterval")));                        
        //setwsImportOrderAction(erpProperties.getProperty("wsImportOrderAction"));
        setwsCompositeOrderType(erpProperties.getProperty("wsCompositeOrderType"));
        setwsCurrentOrder(erpProperties.getProperty("wsCurrentOrder"));
        setwsImportMovementType(erpProperties.getProperty("wsImportMovementType"));
        setwsCurrentGiftCard(erpProperties.getProperty("wsCurrentGiftCard"));
        setwsCheckProduct(erpProperties.getProperty("wsCheckProduct"));
    }

    public String getUrlBase() {
        return UrlBase;
    }

    public final void setUrlBase(String UrlBase) {
        this.UrlBase = UrlBase;
    }
    
    public int getAD_Client_ID() {
        return AD_Client_ID;
    }

    public final void setAD_Client_ID(int AD_Client_ID) {
        this.AD_Client_ID = AD_Client_ID;
    }

    public int getAD_Org_ID() {
        return AD_Org_ID;
    }

    public final void setAD_Org_ID(int AD_Org_ID) {
        this.AD_Org_ID = AD_Org_ID;
    }

    public int getAD_Role_ID() {
        return AD_Role_ID;
    }

    public final void setAD_Role_ID(int AD_Role_ID) {
        this.AD_Role_ID = AD_Role_ID;
    }

    public String getUserName() {
        return UserName;
    }

    public final void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserPass() {
        return UserPass;
    }

    public final void setUserPass(String UserPass) {
        this.UserPass = UserPass;
    }

    public int getM_Warehouse_ID() {
        return M_Warehouse_ID;
    }

    /**
     *
     * @param M_Warehouse_ID
     */
    public final void setM_Warehouse_ID(int M_Warehouse_ID) {
        this.M_Warehouse_ID = M_Warehouse_ID;
    }
    
    public LoginRequest getLogin() {
        LoginRequest login = new LoginRequest();
        login.setUser(getUserName());
        login.setPass(getUserPass());
        login.setLang(Enums.Language.valueOf(getLanguage()));
        login.setClientID(getAD_Client_ID());
        login.setRoleID(getAD_Role_ID());
        login.setOrgID(getAD_Org_ID());
        login.setWarehouseID(getM_Warehouse_ID());
        return login;
    }
    
    public WebServiceConnection getClient() {
        WebServiceConnection client = new WebServiceConnection();
        client.setAttempts(1);
        client.setTimeout(30000);
        client.setAttemptsTimeout(0);
        client.setUrl(getUrlBase());
        client.setAppName("Unicenta Integration");
        return client;
    }

    public String getWsProductType() {
        return wsProductType;
    }

    public final void setWsProductType(String wsProductType) {
        this.wsProductType = wsProductType;
    }

    public String getWsCustomerType() {
        return wsCustomerType;
    }

    public final void setWsCustomerType(String wsCustomerType) {
        this.wsCustomerType = wsCustomerType;
    }

    public String getWsOrderType() {
        return wsOrderType;
    }

    public final void setWsOrderType(String wsOrderType) {
        this.wsOrderType = wsOrderType;
    }

    public String getWsPeopleType() {
        return wsPeopleType;
    }

    public final void setWsPeopleType(String wsPeopleType) {
        this.wsPeopleType = wsPeopleType;
    }
    
    public String getWsGiftCardWrite() {
        return wsGiftCardWrite;
    }
    
    private void setWsGiftCardWrite(String wsGiftCardWrite) {
        this.wsGiftCardWrite = wsGiftCardWrite;
    }
    
    public String getWsGiftCardType() {
        return wsGiftCardType;
    }

    public final void setWsGiftCardType(String wsGiftCardType) {
        this.wsGiftCardType = wsGiftCardType;
    }
    
    public double getWsGiftCardTypeInterval() {
        return wsGiftCardTypeInterval;
    }

    public final void setWsGiftCardTypeInterval(double wsGiftCardTypeInterval) {
        this.wsGiftCardTypeInterval = wsGiftCardTypeInterval;
    }

    public String getWsClosedcashType() {
        return wsClosedcashType;
    }

    public final void setWsClosedcashType(String wsClosedcashType) {
        this.wsClosedcashType = wsClosedcashType;
    }

    public double getWsOrderTypeInterval() {
        return wsOrderTypeInterval;
    }

    public final void setWsOrderTypeInterval(double wsOrderTypeInterval) {
        this.wsOrderTypeInterval = wsOrderTypeInterval;
    }
    public double getWsPeopleTypeInterval() {
        return wsPeopleTypeInterval;
    }

    public final void setWsPeopleTypeInterval(double wsOrderTypeInterval) {
        this.wsPeopleTypeInterval = wsOrderTypeInterval;
    }
    public double getWsProductTypeInterval() {
        return wsProductTypeInterval;
    }

    public final void setWsProductTypeInterval(double wsOrderTypeInterval) {
        this.wsProductTypeInterval = wsOrderTypeInterval;
    }
    public double getWsCustomerTypeInterval() {
        return wsCustomerTypeInterval;
    }

    public final void setWsCustomerTypeInterval(double wsOrderTypeInterval) {
        this.wsCustomerTypeInterval = wsOrderTypeInterval;
    }

    public double getWsClosedcashTypeInterval() {
        return wsClosedcashTypeInterval;
    }

    public final void setWsClosedcashTypeInterval(double wsClosedcashTypeInterval) {
        this.wsClosedcashTypeInterval = wsClosedcashTypeInterval;
    }
    
    public int getAD_Country_ID() {
        return AD_Country_ID;
    }

    public final void setAD_Country_ID(int AD_Country_ID) {
        this.AD_Country_ID = AD_Country_ID;
    }

    public int getAD_Region_ID() {
        return AD_Region_ID;
    }

    public final void setAD_Region_ID(int AD_Region_ID) {
        this.AD_Region_ID = AD_Region_ID;
    }

    public int getAD_City_ID() {
        return AD_City_ID;
    }

    public final void setAD_City_ID(int AD_City_ID) {
        this.AD_City_ID = AD_City_ID;
    }

    public String getLanguage() {
        return Language;
    }

    public final void setLanguage(String Language) {
        this.Language = Language;
    }

    public int getC_DocType_ID() {
        return C_DocType_ID;
    }

    public final void setC_DocType_ID(int C_DocType_ID) {
        this.C_DocType_ID = C_DocType_ID;
    }

    public int getC_DocTypeRefund_ID() {
        return C_DocTypeRefund_ID;
    }

    public final void setC_DocTypeRefund_ID(int C_DocTypeRefund_ID) {
        this.C_DocTypeRefund_ID = C_DocTypeRefund_ID;
    }
    
    public int getM_PriceList_Version_ID() {
        return M_PriceList_Version_ID;
    }

    public final void setM_PriceList_Version_ID(int M_PriceList_Version_ID) {
        this.M_PriceList_Version_ID = M_PriceList_Version_ID;
    }

    public String getCityName() {
        return CityName;
    }

    public final void setCityName(String CityName) {
        this.CityName = CityName;
    }    
              
    public String getWsMovementType() {
        return wsMovementType;
    }

    public final void setWsMovementType(String WsMovementType) {
        this.wsMovementType = WsMovementType;
    }            
    
    public final void setWsMovementTypeInterval(double wsMovementTypeInterval) {
        this.wsMovementTypeInterval = wsMovementTypeInterval;
    } 
    
    public final double getWsMovementTypeInterval(){
        return wsMovementTypeInterval;
    }

    public int getC_DocTypeMovement_ID() {
        return C_DocTypeMovement_ID;
    }

    public final void setC_DocTypeMovement_ID(int C_DocTypeMovement_ID) {
        this.C_DocTypeMovement_ID = C_DocTypeMovement_ID;
    }    

    public String getMainLocationID() {
        return MainLocationID;
    }
    private String getHostName() {
        Properties m_propsconfig = new Properties();
        File file = new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + ".properties");
        try {
            InputStream in;
            in = new FileInputStream(file);
            m_propsconfig.load(in);
            in.close();
        } catch (IOException e) {

        }
        return m_propsconfig.getProperty("machine.hostname");
    }    
    public final void setwsImportOrderTypeInterval(double wsImportOrderTypeInterval) {
        this.wsImportOrderTypeInterval = wsImportOrderTypeInterval;
    } 
    
    public final double getwsImportOrderTypeInterval(){
        return wsImportOrderTypeInterval;
    }    
 
    public String getwsImportOrderType() {
        return wsImportOrderType;
    }

    public final void setwsImportOrderType(String wsImportOrderType) {
        this.wsImportOrderType = wsImportOrderType;
    }      

    public String getwsImportOrderAction() {
        return wsImportOrderAction;
    }

    public final void setwsImportOrderAction(String wsImportOrderAction) {
        this.wsImportOrderAction = wsImportOrderAction;
    }

    public String getwsCompositeOrderType() {
        return wsCompositeOrderType;
    }

    public final void setwsCompositeOrderType(String wsCompositeOrderType){
        this.wsCompositeOrderType=wsCompositeOrderType;
    }

    public String getwsCurrentOrder(){
        return wsCurrentOrder;
    }
    
    public final void setwsCurrentOrder(String wsCurrentOrder){
        this.wsCurrentOrder = wsCurrentOrder;
    }

    public String getwsImportMovementType() {
        return wsImportMovementType;
    }

    public final void setwsImportMovementType(String wsImportMovementType) {
        this.wsImportMovementType = wsImportMovementType;
    }
    
    public String getwsCurrentGiftCard(){
        return wsCurrentGiftCard;
    }
    
    public final void setwsCurrentGiftCard(String wsCurrentGiftCard){
        this.wsCurrentGiftCard = wsCurrentGiftCard;
    }
    
    public String getwsCheckProduct(){
        return wsCheckProduct;
    }
    
    public final void setwsCheckProduct(String wsCheckProduct){
        this.wsCheckProduct = wsCheckProduct;
    }
}
