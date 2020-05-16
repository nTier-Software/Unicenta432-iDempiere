/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cds.sync;

import com.ghintech.sync.Sync;
import com.openbravo.pos.forms.JRootApp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Enums.WebServiceResponseStatus;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.CompositeResponse;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

/**
 *
 * @author cesar3r2
 */
public final class SyncGiftCardW extends Sync {
    public SyncGiftCardW(JRootApp app) {
        super(app);
    }
    
    public DataRow SendGiftCardW(DataRow data) {
        //int Record_ID=0;
        DataRow dataResp = new DataRow();
        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setWebServiceType("writeGiftCard");
        
        CreateDataRequest ws = new CreateDataRequest();        
        ws.setWebServiceType(getWsGiftCardWrite());
        ws.setLogin(getLogin());
        ws.setDataRow(data);
        
        compositeOperation.addOperation(ws);
        
        try {
            WebServiceConnection client = getClient();
            CompositeResponse response = client.sendRequest(compositeOperation);
            
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            }
            //now i need to get the ID of the record
            QueryDataRequest giftcard = new QueryDataRequest();
            giftcard.setWebServiceType(getwsCurrentGiftCard());
            giftcard.setLogin(getLogin());
            giftcard.setLimit(1);    
            DataRow datagiftcard = new DataRow();
            datagiftcard.addField("AD_Org_ID", data.getField("AD_Org_ID").getValue());
            datagiftcard.addField("POS_ID", data.getField("POS_ID").getValue());
            giftcard.setDataRow(datagiftcard);
            WebServiceConnection clienti = getClient();
            WindowTabDataResponse responsegc = clienti.sendRequest(giftcard);
            
            if (responsegc.getStatus() == WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else {
                //Record_ID = responsegc.getDataSet().getRow(0).getFields().get(0).getIntValue();
                Integer giftcard_id = responsegc.getDataSet().getRow(0).getField("GiftCard_ID").getIntValue();
                Date updated_tmp = responsegc.getDataSet().getRow(0).getField("Updated").getDateValue();
                String updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updated_tmp);
                
                dataResp.addField("giftcard_id", giftcard_id);
                dataResp.addField("updated", updated);
                //Record_ID = response.getResponsesCount();
            }
        } catch (WebServiceException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return dataResp;
    }
    
    /*public String processGiftCardW() {
        String Record_ID="";
        ParamValues data = new ParamValues();
        
        data.addField("AD_Client_ID", this.getAD_City_ID());
        data.addField("AD_Org_ID", this.getAD_Org_ID());
        data.addField("DocAction", "CO");
        
        RunProcessRequest ws = new RunProcessRequest();
        
        ws.setWebServiceType(getWsGiftCardType());
        ws.setLogin(getLogin());
        
        ws.setParamValues(data);
        WebServiceConnection client = getClient();

        try {
            RunProcessResponse response = client.sendRequest(ws);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else if (response.getSummary().compareTo("#0/0")==0){
                Record_ID = null;
            }else                
                Record_ID=response.getSummary();
        } catch (WebServiceException | NumberFormatException e) {
        }
        return Record_ID;
    }*/
}
