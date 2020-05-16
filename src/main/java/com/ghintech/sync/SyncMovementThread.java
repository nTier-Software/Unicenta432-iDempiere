/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync;
import com.ghintech.sync.externalsales.MovementIdentifier;
import com.ghintech.sync.movement.MaterialProdInfoExt;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.inventory.MaterialProdInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.idempiere.webservice.client.base.DataRow;

/**
 *
 * @author alara
 */
public class SyncMovementThread extends Thread{
    private JRootApp app;    
    private final DataLogicIntegration dlintegration;
    SyncMovement movements;
    private final String hostname;
    
    public SyncMovementThread(JRootApp rootApp) {
        
        app = rootApp;            
        dlintegration = (DataLogicIntegration) app.getBean("com.ghintech.sync.DataLogicIntegration");
        movements = new SyncMovement(app);
        hostname = getHostName();
    }    

    @Override
    public void run() {
        
        boolean sent = true; 
        Double stopLoop; 
        int c = 0;

        while (true) {
            try {
                
                stopLoop = sent == true ? movements.getWsMovementTypeInterval() : 0.25;
                
                if (c != 0) {
                    sleep(converter(stopLoop));
                }
                System.out.println(exportToERP().getMessageMsg());
                
                sent = true;
            } catch (InterruptedException | BasicException ex) {
                Logger.getLogger(SyncOrderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            c++;
        }

    }    
    
    public long converter(Double min) {
        long millis = (long) (min * 60 * 1000);
        return millis;
    }   
    
  public MessageInf exportToERP() throws BasicException {       
              
        //Verifico que no se esten importando movimientos
        List<MaterialProdInfo> movementlistSync = dlintegration.getMovementsSync(hostname);
        int i = 0;
        if (movementlistSync.size() > 0) {
            dlintegration.resetMovementsSync(hostname);
            //return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.sendingmovements"));
        }
        //if there is not Movements in process update the list of tickets not sync
        dlintegration.setMovementsInProcess(hostname);
        
        List<MaterialProdInfoExt> movementlist = dlintegration.getMovementsSync(hostname);
        if (movementlist.isEmpty()) {
            dlintegration.execMovementUpdate();
            return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.zeromovements"));
        } else {            
            //List<DataRow> datalist = transformMovements(movementlist,movements);
            int result = transformMovements(movementlist,movements);
            //for(DataRow data : datalist){
            if(result!=0)
                /*if(movements.SendMovements(data)!=0){
                    dlintegration.execMovementUpdate((String)data.getField("Description").getValue(),"1");
                }else{
                    dlintegration.execMovementUpdate((String)data.getField("Description").getValue(),"0");
                }
            }*/
                return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.syncmovementsok"), AppLocal.getIntString("message.syncmovementsinfo") + movementlist.size());
            else
                return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.syncmovementserror"), AppLocal.getIntString("message.syncmovementsinfo") + movementlist.size());
        }

    }
 
    //private List<DataRow> transformMovements(List<MaterialProdInfoExt> movementlist,SyncMovement movements) {
  private int transformMovements(List<MaterialProdInfoExt> movementlist,SyncMovement movements) {
        
        //List<DataRow> datalist = new ArrayList();
        String result;
        int imported = 0;
        System.out.println("Cantidad de Movimientos para enviar: " + movementlist.size());
        try {
            for (int i = 0; i < movementlist.size(); i++) {
                if (null != this) {

                      MaterialProdInfoExt movement = movementlist.get(i);
                      MovementIdentifier movementid = new MovementIdentifier();
                      //movementid.setDocumentNo(Integer.toString(ticket.getTicketId()));
                      Calendar datenew = Calendar.getInstance();
                      datenew.setTime(movement.getDate());
                      movementid.setDateNew(datenew);

                      DataRow data = new DataRow();
                      data.addField("AD_Client_ID", movements.getAD_Client_ID());
                      data.addField("AD_Org_ID", movements.getAD_Org_ID());
                      data.addField("MovementDate", movement.getDate());
                      data.addField("C_DocType_ID", movements.getC_DocTypeMovement_ID());
                      data.addField("AD_User_ID", movement.getUser().getId());
                      data.addField("M_Product_ID", movement.getProductId());
                      data.addField("MovementQty", movement.getAmount());
                      data.addField("M_Locator_ID", movements.getMainLocationID());//
                      data.addField("M_LocatorTo_ID", movement.getLocatorId());//
                      //data.addField("Description", movement.getID());//
                      //datalist.add(data);

                      int recordid = movements.SendMovements(data);
                      if(recordid!=0){
                          dlintegration.execStockDiaryUpdate((String)movement.getID(),recordid);
                      }
               }
            }
        result = movements.processMovement();
        if(result!=null){
            //System.out.println("*************Movimiento  Importado: "+movement.getID()+"*************");
            dlintegration.execMovementUpdateValid();
            imported++;
        }
        /*else{
            //System.out.println("*************Falló al procesar movimiento: "+movement.getID()+"*************");
            dlintegration.execMovementUpdateFailed();
        }    */
        } catch (BasicException ex) {
               Logger.getLogger(SyncOrderThread.class.getName()).log(Level.SEVERE, null, ex);
               }        

        //return datalist;
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
