/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cds.sync;

import com.ghintech.sync.DataLogicIntegration;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.payment.JPaymentGiftCardPanel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.idempiere.webservice.client.base.DataRow;

/**
 *
 * @author cesar3r2
 */
public class SyncGiftCardThread extends Thread {
    private final JRootApp app;
    private final String hostname;
    private final DataLogicIntegration dlintegration;
   
    SyncGiftCard GiftCard;
    SyncGiftCardW GiftCardW;
    
    public SyncGiftCardThread(JRootApp rootApp) {
        
        app = rootApp;
        dlintegration = (DataLogicIntegration) app.getBean("com.ghintech.sync.DataLogicIntegration");
        GiftCard = new SyncGiftCard(app);
        GiftCardW = new SyncGiftCardW(app);
        hostname = getHostName();
    }
    
    @Override
    public void run() {
        boolean sent = true; 
        Double stopLoop; 
        int c = 0;

        while (true) {
            try {
                stopLoop = sent == true ? GiftCard.getWsGiftCardTypeInterval() : 0.25;

                if (c != 0) {
                    sleep(converter(stopLoop));
                }
                GiftCard.getGiftCardInfo();
                System.out.println(exportToERP().getMessageMsg());
                sent = true;
            } catch (InterruptedException | BasicException ex) {
                Logger.getLogger(SyncGiftCardThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            c++;
        }
    }
    
    public long converter(Double min) {
        long millis = (long) (min * 60 * 1000);
        return millis;
    }
    
    public MessageInf exportToERP() throws BasicException {
                
        List<JPaymentGiftCardPanel> listSync = dlintegration.getGiftCardSync(hostname);
        int i = 0;
        if (listSync.size() > 0) {
            dlintegration.resetGiftCardSync(hostname);
        }
        dlintegration.setGiftCardInProcess(hostname);
        
        List<JPaymentGiftCardPanel> giftcardlist = dlintegration.getGiftCardSync(hostname);
        
        if (giftcardlist.isEmpty()) {
            dlintegration.execGiftCardUpdate();
            return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.zerogiftcard"));
        } else {
            int result = transformGiftCard(giftcardlist, GiftCardW);
            if(result!=0)
                return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.syncgiftcardok"), AppLocal.getIntString("message.syncordersinfo") + giftcardlist.size());
            else
                return new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.syncgiftcarerror"), AppLocal.getIntString("message.syncordersinfo") + giftcardlist.size());
        }
    }
    
    private int transformGiftCard(List<JPaymentGiftCardPanel> giftcardlist, SyncGiftCardW GiftCardW) throws BasicException {
        int imported = 0;
        
        System.out.println("Cantidad de Gift Card para enviar: " + giftcardlist.size());
        for (int i = 0; i < giftcardlist.size(); i++) {
            if (null != this) {
                JPaymentGiftCardPanel giftcard = giftcardlist.get(i);
                DataRow data = new DataRow();
                data.addField("AD_Client_ID", GiftCardW.getAD_Client_ID());
                data.addField("AD_Org_ID", GiftCardW.getAD_Org_ID());
                data.addField("Serial", giftcard.getSerial());
                data.addField("Amt", giftcard.getAmount());
                data.addField("POS_ID", giftcard.getId());
                
                System.out.println("Enviando gift card serial número: "+giftcard.getSerial());
                
                DataRow recordGiftCard = GiftCardW.SendGiftCardW(data);
                Integer giftCardID = recordGiftCard.getField("giftcard_id").getIntValue();
                Date updated = recordGiftCard.getField("updated").getDateValue();
                
                System.out.println("El resultado fue: "+giftCardID);
                
                if(giftCardID!=0) {
                    dlintegration.execGiftCardUpdate(giftcard.getId(), "1");
                    JPaymentGiftCardPanel giftcardSync = new JPaymentGiftCardPanel();
                    giftcardSync.setId(giftcard.getId());
                    giftcardSync.setSerial(giftcard.getSerial());
                    giftcardSync.setAmount(giftcard.getAmount());
                    giftcardSync.setGiftCardID(giftCardID);
                    giftcardSync.setUpdated(updated);
                    dlintegration.syncGiftCard(giftcardSync);
                    System.out.println("*************Gift Card Importada: "+giftcard.getSerial()+"*************");
                    imported = 1;
                } else {
                    System.out.println("*************Falló al procesar la Gift Card: "+giftcard.getSerial()+"*************");
                }
            }
        }
        return imported;
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
}
