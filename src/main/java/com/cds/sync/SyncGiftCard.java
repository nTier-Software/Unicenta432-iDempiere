/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cds.sync;

import com.ghintech.sync.Sync;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.payment.JPaymentGiftCardPanel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

/**
 *
 * @author cesar3r2
 */
public final class SyncGiftCard extends Sync {
    private int OffSet;
    private int Limit;
    private QueryDataRequest ws;
    private String hostname;
    
    public SyncGiftCard(JRootApp app) {
        super(app);
        OffSet=1;
        Limit=1000;
        ws = new QueryDataRequest();
        ws.setWebServiceType(getWsGiftCardType());
        ws.setLogin(getLogin());
        ws.setLimit(Limit);
        Date updated=null;
        try {
            updated = dlintegration.findLastUpdated("GIFTCARD");
            
            if (updated == null){
                updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1900-01-01 00:00:00");
            }
        } catch (ParseException ex) {
            Logger.getLogger(SyncGiftCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        //ws.setFilter("Updated>\'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updated)+"\'");
    }
    
    public void getGiftCardInfo() {
        WebServiceConnection client = getClient();
 
        try {
            do {   
                WindowTabDataResponse response = client.sendRequest(ws);
                
                if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
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
                    
                    JPaymentGiftCardPanel[] giftcard = new JPaymentGiftCardPanel[response.getNumRows()];
                    for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {
                        System.out.println("Row: " + (i + 1));
                        giftcard[i] = new JPaymentGiftCardPanel();
                        
                        for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {
                            Field field = response.getDataSet().getRow(i).getFields().get(j);
                            System.out.print(" " + field.getColumn() + " = " + field.getValue() + "  ");
                            String fieldValue = String.valueOf(field.getValue());
                            switch (field.getColumn()) {
                                case "Serial":
                                    giftcard[i].setSerial(fieldValue);
                                    break;
                                case "Amt":
                                    giftcard[i].setAmount(Double.parseDouble(fieldValue));
                                    break;
                                case "GiftCard_ID":
                                    giftcard[i].setGiftCardID(Integer.parseInt(fieldValue));
                                    break;
                                case "Updated":
                                    try {
                                        giftcard[i].setUpdated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue));
                                    } catch (ParseException ex) {
                                        Logger.getLogger(SyncGiftCard.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    ImportGiftCard(giftcard);
                }
                ws.setOffset(OffSet);
            } while (OffSet!=0);   
        } catch (WebServiceException | NumberFormatException e) {
        }
    }
    
    public boolean ImportGiftCard(JPaymentGiftCardPanel[] giftcards){
        try {
            if (giftcards == null) {
                throw new BasicException(AppLocal.getIntString("message.returnnull"));
            } 
            if (giftcards.length > 0 ) {
                for (JPaymentGiftCardPanel giftcard : giftcards) {
                    System.out.println("Registrando GiftCard: "+giftcard.getSerial());
                    dlintegration.syncGiftCard(giftcard);
                }
                return true;
            }
        } catch (BasicException ex) {
            Logger.getLogger(SyncGiftCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }    
}
