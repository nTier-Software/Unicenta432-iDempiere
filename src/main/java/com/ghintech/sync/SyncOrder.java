/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync;

import com.openbravo.pos.forms.JRootApp;
import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.ParamValues;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.request.RunProcessRequest;
import org.idempiere.webservice.client.response.CompositeResponse;
import org.idempiere.webservice.client.response.RunProcessResponse;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

/**
 *
 * @author egil
 */
public final class SyncOrder extends Sync {

    public SyncOrder(JRootApp app) {
        super(app);
    }

    public int SendOrders(DataRow data) {
        int Record_ID=0;
        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setWebServiceType(getwsCompositeOrderType());        
        CreateDataRequest ws = new CreateDataRequest();        
        ws.setWebServiceType(getWsOrderType());
        ws.setLogin(getLogin());       
        ws.setDataRow(data);
        compositeOperation.addOperation(ws);
        try {
            WebServiceConnection client = getClient();
            CompositeResponse response = client.sendRequest(compositeOperation);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
                return 0;
            }
            //now i need to get the ID of the record
           QueryDataRequest invoice = new QueryDataRequest();
           invoice.setWebServiceType(getwsCurrentOrder());
           invoice.setLogin(getLogin());
           invoice.setLimit(1);    
           DataRow datainvoice = new DataRow();
           datainvoice.addField("DocumentNo", data.getField("DocumentNo").getValue());
         /*  datainvoice.addField("OPOS_line", data.getField("OPOS_line").getValue()); yogan060520 */
           datainvoice.addField("AD_Org_ID", data.getField("AD_Org_ID").getValue());
           datainvoice.addField("C_DocType_ID", data.getField("C_DocType_ID").getValue());
           invoice.setDataRow(datainvoice);
           WebServiceConnection clienti = getClient();
           WindowTabDataResponse responsei = clienti.sendRequest(invoice);  
           if (responsei.getStatus() == Enums.WebServiceResponseStatus.Error) {
               System.out.println(responsei.getErrorMessage());
		return 0;
           } else {
                Record_ID = responsei.getDataSet().getRow(0).getFields().get(0).getIntValue();    
           }                        
        } catch (WebServiceException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return Record_ID;
    }
    
    public int SendNewOrder(DataRow data) {
        int Record_ID=0;
        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setWebServiceType(getwsCompositeOrderType());        
        CreateDataRequest ws = new CreateDataRequest();        
        ws.setWebServiceType(getWsOrderType());
       // ws.setLogin(getLogin());       
        ws.setDataRow(data);
        compositeOperation.addOperation(ws);
        try {
            WebServiceConnection client = getClient();
            CompositeResponse response = client.sendRequest(compositeOperation);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
                return 0;
            }
            //now i need to get the ID of the record
           QueryDataRequest invoice = new QueryDataRequest();
           invoice.setWebServiceType(getwsCurrentOrder());
           invoice.setLogin(getLogin());
           invoice.setLimit(1);    
           DataRow datainvoice = new DataRow();
           datainvoice.addField("DocumentNo", data.getField("DocumentNo").getValue());
         /*  datainvoice.addField("OPOS_line", data.getField("OPOS_line").getValue()); yogan060520 */
           datainvoice.addField("AD_Org_ID", data.getField("AD_Org_ID").getValue());
           datainvoice.addField("C_DocType_ID", data.getField("C_DocTypeTarget_ID").getValue());
           invoice.setDataRow(datainvoice);
           WebServiceConnection clienti = getClient();
           WindowTabDataResponse responsei = clienti.sendRequest(invoice);  
           if (responsei.getStatus() == Enums.WebServiceResponseStatus.Error) {
               System.out.println(responsei.getErrorMessage());
		return 0;
           } else {
                Record_ID = responsei.getDataSet().getRow(0).getFields().get(0).getIntValue();    
           }                        
        } catch (WebServiceException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return Record_ID;
    }

    public String processOrder(int ticketid){
        String Record_ID="";
        ParamValues data = new ParamValues();
        data.addField("AD_Client_ID",this.getAD_Client_ID());
        data.addField("AD_Org_ID",this.getAD_Org_ID());
        data.addField("DocumentNo", Integer.toString(ticketid));
        data.addField("DocAction", "CO");
        RunProcessRequest ws = new RunProcessRequest();
        
        ws.setWebServiceType(getwsImportOrderType());
        ws.setLogin(getLogin());
        
        ws.setParamValues(data);
        WebServiceConnection client = getClient();

        try {
            RunProcessResponse response;
            response = client.sendRequest(ws);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else{
                Record_ID = response.getSummary();
            
            }
        } catch (WebServiceException | NumberFormatException e) {
        }
        return Record_ID;
    }    
    public String processOrder(int ticketid,int C_DocType_ID){
        String Record_ID="";
        ParamValues data = new ParamValues();
        data.addField("AD_Client_ID",this.getAD_Client_ID());
        data.addField("AD_Org_ID",this.getAD_Org_ID());
        data.addField("DocumentNo", Integer.toString(ticketid));
        data.addField("DocAction", "CO");
        data.addField("C_DocType_ID", C_DocType_ID);
        RunProcessRequest ws = new RunProcessRequest();
        
        ws.setWebServiceType(getwsImportOrderType());
        ws.setLogin(getLogin());
        
        ws.setParamValues(data);
        WebServiceConnection client = getClient();

        try {
            RunProcessResponse response;
            response = client.sendRequest(ws);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else{
                Record_ID = response.getSummary();
            
            }
        } catch (WebServiceException | NumberFormatException e) {
        }
        return Record_ID;
    }
}
