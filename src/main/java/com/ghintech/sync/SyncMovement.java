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
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.RunProcessRequest;
import org.idempiere.webservice.client.response.RunProcessResponse;
import org.idempiere.webservice.client.response.StandardResponse;

/**
 *
 * @author Angel Lara
 */
public class SyncMovement extends Sync{

    public SyncMovement(JRootApp app) {
        super(app);
    }

    public int SendMovements(DataRow data) {
        int Record_ID=0;
        
        CreateDataRequest ws = new CreateDataRequest();
        
        ws.setWebServiceType(getWsMovementType());
        ws.setLogin(getLogin());
        
        ws.setDataRow(data);
        WebServiceConnection client = getClient();

        try {

            StandardResponse response = client.sendRequest(ws);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else {
                Record_ID=response.getRecordID();
            }

        } catch (WebServiceException | NumberFormatException e) {
        }
        return Record_ID;
    }    

public String processMovement(){
        String Record_ID="";
        ParamValues data = new ParamValues();
        data.addField("AD_Client_ID",this.getAD_Client_ID());
        data.addField("AD_Org_ID",this.getAD_Org_ID());
        //data.addField("DocumentNo", Integer.toString(movementid));
        data.addField("DocAction", "CO");
//        data.addField("DocAction", "PR");
        RunProcessRequest ws = new RunProcessRequest();        
        ws.setWebServiceType(getwsImportMovementType());
        ws.setLogin(getLogin());
        
        ws.setParamValues(data);
        WebServiceConnection client = getClient();

        try {
            RunProcessResponse response;
            response = client.sendRequest(ws);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else if (response.getSummary().compareTo("#0/0")==0){
                Record_ID = null;
            }else                
                Record_ID=response.getSummary();
        } catch (WebServiceException | NumberFormatException e) {
        }
        return Record_ID;
    }    
}
