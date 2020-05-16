/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync;

import com.openbravo.basic.BasicException;
import com.ghintech.sync.customers.CustomerInfoExt;
import com.ghintech.sync.customers.Customer;
import com.ghintech.sync.customers.Location;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.JRootApp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public final class SyncCustomers extends Sync{

    int OffSet;
    int Limit;
    QueryDataRequest ws;
    public SyncCustomers(JRootApp app) {
        super(app);
        OffSet=1;
        Limit=getwsLimit();
        ws = new QueryDataRequest();
        ws.setWebServiceType(getWsCustomerType());
        ws.setLogin(getLogin());
        ws.setLimit(Limit);
        Date updated=null;
        try {
            updated = dlintegration.findLastUpdated("CUSTOMERS");
            if (updated == null){
                updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1900-01-01 00:00:00");
            }
        } catch (ParseException ex) {
            Logger.getLogger(SyncCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        ws.setFilter(" Updated > \'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updated) +"\' ");
        
 
        
    }
    public void getCustomerInfo(){
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
                    Customer[] customer = new Customer[response.getNumRows()];
                    
                    for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {
                        System.out.println("Row: " + (i + 1));
                        customer[i] = new Customer();
                        

                        for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {
                            Field field = response.getDataSet().getRow(i).getFields().get(j);
                            System.out.print(" " + field.getColumn() + " = " + field.getValue() + "  ");
                            String fieldValue = String.valueOf(field.getValue());
                            switch (field.getColumn()) {
                                case "CustomerName":
                                    customer[i].setName(fieldValue);
                                    break;
                                case "Value":
                                    customer[i].setSearchKey(fieldValue);
                                    break;
                                case "TaxID":
                                    customer[i].setTaxId(fieldValue);
                                    break;
                                case "C_BPartner_ID":
                                    customer[i].setId(fieldValue);
                                    break;
                                case "Description":
                                    customer[i].setDescription(fieldValue);
                                    break;
                                case "Address1":
                                    Location[] loc= new Location[1];
                                    loc[0]= new Location();
                                    loc[0].setAddress1(fieldValue);
                                    customer[i].setLocations(loc);
                                    break;
                                case "IsActive":
                                    if (fieldValue.compareTo("Y")==0)
                                        customer[i].setVisible(Boolean.TRUE);
                                    else
                                        customer[i].setVisible(Boolean.FALSE);
                                    break;
                                case "TotalOpenBalance":
                                    //customer[i].setMaxdebt(Double.parseDouble(fieldValue));
                                    //customer[i].setCurdebt(0.0);
                                    break;
                                case "Updated":
                            
                                    try {
                                        customer[i].setUpdated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue));

                                    } catch (ParseException ex) {
                                        Logger.getLogger(SyncCustomers.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                            
                                    break;
                                default:
                                    break;
                            }
                        }

                    }
                    ImportCustomers(customer);
                    
                }
                ws.setOffset(OffSet);
            }while (OffSet!=0);   
        } catch (WebServiceException | NumberFormatException e) {
        }
    }
     public boolean ImportCustomers(Customer[] customers){
        try {
            
        
             if (customers == null){
                throw new BasicException(AppLocal.getIntString("message.returnnull"));
             } 
             
              if (customers.length > 0 ) {
                    
                    //dlintegration. syncCustomersBefore();
                                    //Add RIF to field Taxid
                    for (Customer customer : customers) {    
                        System.out.println("Registrando Cliente: "+customer.getName());
                        CustomerInfoExt cinfo = new CustomerInfoExt(customer.getId());
                        cinfo.setTaxid(customer.getTaxId());
                        cinfo.setSearchkey(customer.getSearchKey());
                        cinfo.setName(customer.getName());          
                        cinfo.setNotes(customer.getDescription());
                        Location loc[]=new Location[1];
                        loc=customer.getLocations();
                        cinfo.setAddress(loc[0].getAddress1());
                        cinfo.setVisible(customer.getVisible());
                        cinfo.setMaxdebt(customer.getMaxdebt());
                        cinfo.setCurdebt(customer.getCurdebt());
                        cinfo.setUpdated(customer.getUpdated());
                        
                        // TODO: Finish the integration of all fields.
                        dlintegration.syncCustomer(cinfo);
                    }
                    return true;
                }
                
        } catch (BasicException ex) {
            Logger.getLogger(SyncCustomers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    
    }   

    private String getWsCustomerTypeLastUpdate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
