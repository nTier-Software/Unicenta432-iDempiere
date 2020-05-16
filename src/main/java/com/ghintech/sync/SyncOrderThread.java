//    Openbravo POS is a point of sales application designed for touch screens.
//    http://www.openbravo.com/product/pos
//    Copyright (c) 2007 openTrends Solucions i Sistemes, S.L
//    Modified by Openbravo SL on March 22, 2007
//    These modifications are copyright Openbravo SL
//    Author/s: A. Romero 
//    You may contact Openbravo SL at: http://www.openbravo.com
//
//		Contributor: Redhuan D. Oon - ActiveMQ XML string creation for MClient.sendmessage()
//		Please refer to notes at http://red1.org/adempiere/viewtopic.php?f=29&t=1356
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.
package com.ghintech.sync;

import com.ghintech.fiscalprint.DataLogicFiscal;
import com.openbravo.basic.BasicException;
import com.ghintech.sync.externalsales.OrderIdentifier;
import com.ghintech.sync.externalsales.OrderLine;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
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

/*import org.compiere.model.I_I_Order;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 *
 * @author sergio
 */
public class SyncOrderThread extends Thread {

    
    private final JRootApp app;
    
    private final DataLogicIntegration dlintegration;
    private final DataLogicFiscal dlfiscal;
    SyncOrder orders;
    protected DataLogicSystem dlsystem;
    private final Properties erpProperties;
    private final String hostname;
    
    public SyncOrderThread(JRootApp rootApp) {
        
        app = rootApp;            
        dlintegration = (DataLogicIntegration) app.getBean("com.ghintech.sync.DataLogicIntegration");
        dlfiscal = (DataLogicFiscal) app.getBean("com.ghintech.fiscalprint.DataLogicFiscal");
        orders = new SyncOrder(app);
        hostname = getHostName();
        dlsystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        erpProperties = dlsystem.getResourceAsProperties("erp.properties");
        
    }

    @Override
    public void run() {
        
        boolean sent = true; 
        Double stopLoop; 
        int c = 0;
        try{
                dlintegration.checkTickets();
            if(erpProperties.getProperty("SentAllOrders").equals("Y"))
                dlintegration.checkTicketsFiscalNumber();
            
        }catch(BasicException e){
            
        }
        while (true) {
            try {
                
                stopLoop = sent == true ? orders.getWsOrderTypeInterval() : 0.25;
                
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
        
        
        List<TicketInfo> ticketlistSync = dlintegration.getTicketsSync(hostname);
        if (ticketlistSync.size() > 0) {
            dlintegration.resetTicketsSync(hostname);
            //return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.sendingorders"));
        }
        //if there is not tickets in process update the list of tickets not sync
        dlintegration.setTicketsInProcess(hostname);

        List<TicketInfo> ticketlist = dlintegration.getTicketsSync(hostname);
        for (TicketInfo ticket : ticketlist) {
            ticket.setLines(dlintegration.getTicketLines(ticket.getId()));
            ticket.setPayments(dlintegration.getTicketPayments(ticket.getId()));
        }
        if (ticketlist.isEmpty()) {
            //dlintegration.execTicketUpdate();
            return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.zeroorders"));
        } else {
            int result = transformTickets(ticketlist,orders);
            if(result!=0)
                return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.syncordersok"), AppLocal.getIntString("message.syncordersinfo") + ticketlist.size());
            else
                return new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.syncorderserror"), AppLocal.getIntString("message.syncordersinfo") + ticketlist.size());
        }

    }

    private int transformTickets(List<TicketInfo> ticketlist,SyncOrder orders) {        
        String result;
        int imported = 0;
        int C_OrderType_ID=0;

        System.out.println("Cantidad de ticket para enviar: " + ticketlist.size());
        for (int i = 0; i < ticketlist.size(); i++) {
            if (null != this) {
                try {
                    TicketInfo ticket = ticketlist.get(i);
                    
                    OrderIdentifier orderid = new OrderIdentifier();
                    orderid.setDocumentNo(Integer.toString(ticket.getTicketId()));
                    
                    Calendar datenew = Calendar.getInstance();
                    datenew.setTime(ticket.getDate());
                    orderid.setDateNew(datenew);
                    
                    if (ticket.getCustomerId() != null) {
                        ticket.setCustomer(dlintegration.getTicketCustomer(ticket.getId()));
                    }
                    OrderLine[] orderLine = new OrderLine[ticket.getLines().size()];
                    for (int j = 0; j < ticket.getLines().size(); j++) {                      
                        TicketLineInfo line = ticket.getLines().get(j);
                        int I_Order_ID = dlintegration.findI_Order_ID(ticket.getId(),line.getTicketLine());
                        if(I_Order_ID != 0)
                            continue;
                        orderLine[j] = new OrderLine();
                        orderLine[j].setOrderLineId(String.valueOf(line.getTicketLine()));// or simply "j"
                        
                        DataRow data = new DataRow();
                        if(ticket.getTicketType()==0){
                            
                            C_OrderType_ID = orders.getC_DocType_ID();
                        }else{
                            
                            C_OrderType_ID = orders.getC_DocTypeRefund_ID();
                        }
                        data.addField("C_DocType_ID", C_OrderType_ID);
                        data.addField("AD_Client_ID", orders.getAD_Client_ID());
                        data.addField("AD_Org_ID", orders.getAD_Org_ID());
                        
                        data.addField("M_Warehouse_ID", orders.getM_Warehouse_ID());
                        
                        data.addField("DocumentNo", Integer.toString(ticket.getTicketId()));
                        data.addField("DateOrdered", new java.sql.Timestamp(datenew.getTime().getTime()).toString()); 
                        data.addField("SalesRep_ID", Integer.valueOf(ticket.getUser().getId()));
                        if (ticket.getCustomerId() != null) {
                            data.addField("BPartnerValue", ticket.getCustomer().getSearchkey());
                            
                            //data.addField("TaxID", ticket.getCustomer().getTaxid());

                            data.addField("Name", ticket.getCustomer().getName());
                            if(ticket.getCustomer().getAddress() != null && !ticket.getCustomer().getAddress().equals("")){
                                data.addField("Address1", ticket.getCustomer().getAddress());                            
                                data.addField("City", ticket.getCustomer().getCity());
                            }
                            else{
                                data.addField("Address1", orders.getCityName());                            
                                data.addField("City", orders.getCityName());                            
                            }                            
                        }                        
                        if (line.getProductID() == null) {
                            orderLine[j].setProductId("0");
                            orderLine[j].setProductValue("0");
                            data.addField("M_Product_ID", 0);
                            data.addField("ProductValue", 0);
                        } else {
                            if(!line.getProductID().contains("-")){
                                orderLine[j].setProductId(line.getProductID());                            
                                data.addField("M_Product_ID", line.getProductID());
                            }
                            ProductInfoExt productinfo = dlintegration.getProductInfo(line.getProductID());
                            data.addField("ProductValue", productinfo.getReference());
                        }
                        
                        orderLine[j].setUnits(line.getMultiply());
                        orderLine[j].setPrice(line.getPrice());
                        orderLine[j].setTaxId(line.getTaxInfo().getId());
                        
                        data.addField("QtyOrdered", Double.toString(Math.abs(line.getMultiply())));                        
                        data.addField("PriceActual", Double.toString(line.getPrice()));                       
                        data.addField("C_Tax_ID", line.getTaxInfo().getId());
                        data.addField("TaxAmt", Double.toString(Math.abs(line.getTax())));
                        

                        //String paymentType = "POS Order";
                        //Double creditnoteAmount = 0.0;
                        
                        /*if (ticket.getTotal() >= 0) {
                            List<PaymentInfo> payments = ticket.getPayments();
                            for (PaymentInfo payment : payments) {
                                if (payment.getName().equals("debt")) {
                                    paymentType = payment.getName();
                                    creditnoteAmount = payment.getTotal();
                                    break;
                                }
                            }
                        } else {
                            PaymentInfo payments = ticket.getPayments().get(0);
                            paymentType = payments.getName();
                            creditnoteAmount = payments.getTotal();
                        }*/
                        //data.addField("paymentType", paymentType);
                        //writer.writeCharacters(paymentType);
                        //writer.writeEndElement();
                        //data.addField("creditnoteAmount", creditnoteAmount);
                        /*writer.writeStartElement("creditnoteAmount");
                                writer.writeCharacters(String.valueOf(creditnoteAmount));
                                writer.writeEndElement();
                         */
                        //data.addField("paymentAmount", ticket.getTotal());
                        /*writer.writeStartElement("paymentAmount");
                                writer.writeCharacters(String.valueOf(ticket.getTotal()));
                                writer.writeEndElement();*/
                        //send fiscal information commented for standard
                        /*
                                writer.writeStartElement("fiscalprint_serial");
                                writer.writeCharacters(ticket.getFiscalprint_serial());
                                writer.writeEndElement();
                                writer.writeStartElement("fiscal_invoicenumber");
                                writer.writeCharacters(ticket.getFiscal_invoicenumber());
                                writer.writeEndElement();
                                writer.writeStartElement("fiscal_zreport");
                                writer.writeCharacters(ticket.getFiscal_zreport());
                                writer.writeEndElement();
                         */
                        //data.addField("OPOS_numberoflines", ticket.getLines().size());
                        
                        //data.addField("OPOS_line", line.getTicketLine());
                        
                        data.addField("C_Country_ID", orders.getAD_Country_ID());
                        
                        data.addField("C_Region_ID", orders.getAD_Region_ID());                        
 
                        
                        if(ticket.getCustomer().getPhone() != null)
                            data.addField("Phone", ticket.getCustomer().getPhone());
                        else
                            data.addField("Phone", "");
                        if(ticket.getCustomer().getEmail() != null)
                            data.addField("EMail", ticket.getCustomer().getEmail());
                        else
                            data.addField("EMail", "");
                        
                      /*  data.addField("OPOS_numberoflines", ticket.getLines().size());
                        
                        data.addField("OPOS_line", line.getTicketLine());
                        
                        data.addField("fiscalNumber", dlfiscal.findFiscalNumber(ticket.getId(), ticket.getTicketType()));
                        data.addField("FiscalPrintSerial", dlfiscal.findFiscalSerial(ticket.getId(), ticket.getTicketType()));                        
                       yn060520 */ 
                        int recordid = orders.SendOrders(data);
                        if(recordid!=0){
                            dlintegration.execTicketLineUpdate(ticket.getId(),line.getTicketLine(),recordid);
                        }
                    }
                    //REVISO SI faltan lineas
                    System.out.println(dlintegration.countExportedLines(ticket.getId()));
                    System.out.println(ticket.getLinesCount());
                    if(dlintegration.countExportedLines(ticket.getId()) == ticket.getLinesCount()){
                        System.out.println("*************Resultado*************");

                        result = orders.processOrder(ticket.getTicketId(),C_OrderType_ID);
                        System.out.println(result);                        
                        if(result!=null){
                            System.out.println("*************Orden Importada: "+ticket.getTicketId()+"*************");
                            dlintegration.execTicketUpdate(ticket.getId(),"1");
                            imported++;
                        }
                        else{
                            System.out.println("*************Falló al procesar orden: "+ticket.getTicketId()+"*************");
                            dlintegration.execTicketUpdate(ticket.getId(),"0");
                        }
                    }else{
                        System.out.println("*************Aun Faltan Lineas: "+ticket.getTicketId()+"*************");
                        dlintegration.execTicketUpdate(ticket.getId(),"0");                        
                    }
                    //PROCESAR ORDEN
                
                    
                } catch (BasicException ex) {
                    Logger.getLogger(SyncOrderThread.class.getName()).log(Level.SEVERE, null, ex);
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
