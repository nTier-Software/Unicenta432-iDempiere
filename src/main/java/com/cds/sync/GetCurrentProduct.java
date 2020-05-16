/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cds.sync;

import com.ghintech.sync.SyncCP;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.inventory.MovementReason;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums.WebServiceResponseStatus;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

/**
 *
 * @author cesar3r2
 */
public final class GetCurrentProduct extends SyncCP {
    private DataLogicSystem logicsystem = new DataLogicSystem();
    private String location="";
    public GetCurrentProduct(AppView app) {
        super(app);
        logicsystem.init(app.getSession());
        Properties p = logicsystem.getResourceAsProperties(getHostName() + "/properties");
        location=p.getProperty("location");
    }
    
    public DataRow GetCurrentProduct(DataRow data) throws BasicException {
        
            System.out.println("com.cds.sync.GetCurrentProduct.GetCurrentProduct()");
  
            //int Record_ID=0;
            DataRow dataResp = new DataRow();
             try {     
            //now i need to get the ID of the record
            QueryDataRequest checkproduct = new QueryDataRequest();
            checkproduct.setWebServiceType("CheckProduct");
            checkproduct.setLogin(getLogin());
            checkproduct.setLimit(10);    
            DataRow datacheckproduct = new DataRow();
            datacheckproduct.addField("M_Warehouse_ID", getM_Warehouse_ID());
            datacheckproduct.addField("M_Product_ID", data.getField("M_Product_ID").getValue());
            checkproduct.setDataRow(datacheckproduct);
            WebServiceConnection clienti = getClient();
            WindowTabDataResponse responsegc = clienti.sendRequest(checkproduct);
            
            if (responsegc.getStatus() == WebServiceResponseStatus.Error) {
                System.out.println(responsegc.getErrorMessage());
            } else {
                //Record_ID = responsegc.getDataSet().getRow(0).getFields().get(0).getIntValue();
                Double QtyOnHand = responsegc.getDataSet().getRow(0).getField("QtyOnHand").getDoubleValue();
                Double pricelist = responsegc.getDataSet().getRow(0).getField("PriceList").getDoubleValue();
                
                dataResp.addField("QtyOnHand", QtyOnHand);
                dataResp.addField("pricelist", pricelist);
                String prod_id = (String) data.getField("M_Product_ID").getValue();
                String Warehouse = Integer.toString(getM_Warehouse_ID());
                
                Double StockQty = dlintegration.findProductStock(location, prod_id, null);
                double diff = QtyOnHand - StockQty;
                System.out.println("QtyOnHand " + QtyOnHand);
                System.out.println("StockQty " + StockQty);
                System.out.println("diff " + diff);
                Date now = new Date();
                if(diff != 0.0){
                            Object[] diary = new Object[9];
                            diary[0] = UUID.randomUUID().toString();
                            diary[1] = now;
                            diary[2] = diff > 0.0 
                                        ? MovementReason.IN_MOVEMENT.getKey()
                                        : MovementReason.OUT_MOVEMENT.getKey();
                            //diary[3] = warehouse;
                            diary[3] = location;
                            String pid=prod_id;
                            diary[4] = pid;
                            diary[5] = null; ///TODO find out where to get AttributeInstanceID -- red1
                            diary[6] = diff;
                            diary[7] = pricelist;
                            diary[8] = dlsystem.getUser();
                            dlintegration.getStockDiaryInsert().exec(diary);
                        }
                
              }
         } catch (WebServiceException ex) {
            Logger.getLogger(GetCurrentProduct.class.getName()).log(Level.SEVERE, null, ex);
        }
         return dataResp;
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
