/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync;

import com.openbravo.basic.BasicException;
import com.ghintech.sync.people.User;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.util.Hashcypher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums.WebServiceResponseStatus;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;




/**
 *
 * @author egil
 */
public final class SyncPeople extends Sync{

    private int OffSet;
    private int Limit;
    private QueryDataRequest ws;
    public SyncPeople(JRootApp app) {
        super(app);
        OffSet=1;
        Limit=getwsLimit();
        ws = new QueryDataRequest();
        ws.setWebServiceType(getWsPeopleType());
        ws.setLogin(getLogin());
        ws.setLimit(Limit);
        Date updated=null;
        try {
            updated = dlintegration.findLastUpdated("PEOPLE");
            if (updated == null){
                updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1900-01-01 00:00:00");
            }
        } catch (ParseException ex) {
            Logger.getLogger(SyncPeople.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //ws.setFilter("(AD_Org_ID=0 OR AD_Org_ID="+getAD_Org_ID()+") AND Updated>\'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updated)+"\'");
     
    }
    public void getPeopleInfo(){
        WebServiceConnection client = getClient();
 
        try {
            
            do{
                
                WindowTabDataResponse response = client.sendRequest(ws);
                if (response.getStatus() == WebServiceResponseStatus.Error) {
                    System.out.println(response.getErrorMessage());
                    return;
                } else {
                    System.out.println("Total rows: " + response.getTotalRows());
                    System.out.println("Num rows: " + response.getNumRows());
                    System.out.println("Start row: " + response.getStartRow());
                    if(response.getNumRows()>0)
                        OffSet+=Limit;
                    else
                        OffSet=0;
                    
                    User[] user = new User[response.getNumRows()];
                    for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {
                        System.out.println("Row: " + (i + 1));
                        user[i] = new User();
                        

                        for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {
                            Field field = response.getDataSet().getRow(i).getFields().get(j);
                            System.out.print(" " + field.getColumn() + " = " + field.getValue() + "  ");
                            String fieldValue = String.valueOf(field.getValue());
                            switch (field.getColumn()) {
                                case "Name":
                                    user[i].setName(fieldValue);
                                    break;
                                case "AD_User_ID":
                                    user[i].setId(fieldValue);
                                    break;
                                case "IsActive":
                                    if (fieldValue.compareTo("Y")==0)
                                        user[i].setVisible(Boolean.TRUE);
                                    else
                                        user[i].setVisible(Boolean.FALSE);
                                    break;
                                case "Updated":
                            
                                    try {
                                        user[i].setUpdated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue));

                                    } catch (ParseException ex) {
                                        Logger.getLogger(SyncPeople.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                            
                                    break;
                                case "Password":
                                    String sNewPassword = Hashcypher.hashString(fieldValue);
                                    System.err.println(sNewPassword);
                                    user[i].setPassword(sNewPassword);
                                default:
                                    break;
                            }
                        }

                    }
                    ImportPeople(user);
                    
                }
                ws.setOffset(OffSet);
            }while (OffSet!=0);   
        } catch (WebServiceException | NumberFormatException e) {
        }
    }
    public boolean ImportPeople(User[] users){
        try {
            
        
             if (users == null){
                throw new BasicException(AppLocal.getIntString("message.returnnull"));
             } 
             
              if (users.length > 0 ) {
                    
                    for (User user : users) {    
                        System.out.println("Registrando Usuario: "+user.getName());
                        dlintegration.syncPeople(user);
                    }
                    return true;
                }
                
        } catch (BasicException ex) {
            Logger.getLogger(SyncPeople.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    
    }
   
        
}
