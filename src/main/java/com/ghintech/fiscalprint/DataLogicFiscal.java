//    Openbravo POS is a point of sales application designed for touch screens.
//    http://www.openbravo.com/product/pos
//    Copyright (c) 2007 openTrends Solucions i Sistemes, S.L
//    Modified by Openbravo SL on March 22, 2007
//    These modifications are copyright Openbravo SL
//    Author/s: A. Romero
//    You may contact Openbravo SL at: http://www.openbravo.com
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

package com.ghintech.fiscalprint;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataParams;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.data.loader.SerializerReadInteger;
import com.openbravo.data.loader.SerializerReadString;
import com.openbravo.data.loader.SerializerWriteParams;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.payment.PaymentInfo;
import com.openbravo.pos.payment.PaymentInfoTicket;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

/**
 *
 * @author adrianromero
 * Created on 5 de marzo de 2007, 19:56
 * @contributor Sergio Oropeza - Double Click Sistemas - Venezuela - soropeza@dcs.net.ve, info@dcs.net.ve
 *
 */
public class DataLogicFiscal extends BeanFactoryDataSingle {
    
    protected Session s;
    private PreparedStatement pstmt;
    private String SQL;
    private ResultSet rs;
    private Statement stmt;
    private Connection conFiscal;
    private String p_vendor;
    /** Creates a new instance of DataLogicIntegration */
    public DataLogicFiscal() {
        
    }
    
    public void init(Session s) {
        this.s = s;
        try{
            
            conFiscal=s.getConnection();                      
        }
        catch (SQLException e){
            System.out.print("No session or connection");
        }
    }
     
    
    public final String findFiscalNumber(final String ticketid,final int tickettype) throws BasicException {
        PreparedSentence p = new PreparedSentence(s
                , "SELECT FISCALNUMBER FROM TICKETS WHERE ID=? AND TICKETTYPE=?"
                //, new SerializerWriteBasic(new Datas[] {
                //Datas.OBJECT, Datas.INT})
                , SerializerWriteParams.INSTANCE
                , SerializerReadString.INSTANCE);
        String fNumber = (String) p.find(new DataParams() {@Override
                                        public void writeValues() throws BasicException {
                                            setString(1, ticketid);
                                            setInt(2, tickettype);
                                        }});
        return fNumber;
    }

    public final String findFiscalSerial(final String ticketid,final int tickettype) throws BasicException {
        PreparedSentence p = new PreparedSentence(s
                , "SELECT FISCALSERIAL FROM TICKETS WHERE ID=? AND TICKETTYPE=?"
                //, new SerializerWriteBasic(new Datas[] {
                //Datas.OBJECT, Datas.INT})
                , SerializerWriteParams.INSTANCE
                , SerializerReadString.INSTANCE);
        String fSerial = (String) p.find(new DataParams() {@Override
                                        public void writeValues() throws BasicException {
                                            setString(1, ticketid);
                                            setInt(2, tickettype);
                                        }});
        return fSerial;
    }
    
    public final String findCuponNo(final int ticketid,final int tickettype) throws BasicException {
        PreparedSentence p = new PreparedSentence(s
                , "SELECT CUPONNO FROM TICKETS WHERE TICKETID=? AND TICKETTYPE=?"
                //, new SerializerWriteBasic(new Datas[] {
                //Datas.OBJECT, Datas.INT})
                , SerializerWriteParams.INSTANCE
                , SerializerReadString.INSTANCE);
        String fNumber = (String) p.find(new DataParams() {@Override
                                        public void writeValues() throws BasicException {
                                            setInt(1, ticketid);
                                            setInt(2, tickettype);
                                        }});
        return fNumber;
    }
    
    public final Boolean updateTicketFiscalCopyTheFactory(final TicketInfo ticket,final String fNumber) throws BasicException, FileNotFoundException, IOException, ParseException {
                SentenceExec ticketFiscalUpdate = new PreparedSentence(s
                , "UPDATE TICKETS SET fiscalnumber=? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE);
                        ticketFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                            setString(1, fNumber);
                            setString(2, ticket.getId());
                       }});
        
        return true;
    }
    
    public final Boolean updateTicketFiscalTheFactory(final TicketInfo ticket,File fileStatus) throws BasicException, FileNotFoundException, IOException, ParseException {
        FileReader reader = new FileReader(fileStatus);
        BufferedReader br = new BufferedReader(reader); 
        String status; 
        if((status = br.readLine()) == null) {
            return false;
        }
        DataLogicSystem logicSystem = new DataLogicSystem();
        logicSystem.init(this.s);
        Properties propsdb = logicSystem.getResourceAsProperties(getHostName() + "/properties");
        String serial = propsdb.getProperty("serialNumber");
        String aux = status.substring(21,29);
        if (ticket.getTicketType()==1){
            aux = status.substring(34,42);
        }
        final String fiscalnumber = aux;
        final String fiscalserial = serial;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        final Date date= formatter.parse(status.substring(122,124)+"/"+status.substring(124,126)+"/"+status.substring(126,128)
                +" "+status.substring(116,118)+":"+status.substring(118,120)+":"+status.substring(120,122));
        
        SentenceExec ticketFiscalUpdate = new PreparedSentence(s
                , "UPDATE TICKETS SET fiscalnumber=?, fiscalserial=? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE);
                        ticketFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                            setString(1, fiscalnumber);
                            setString(2, fiscalserial);
                            setString(3, ticket.getId());
                       }});
        
        /*SentenceExec receiptFiscalUpdate = new PreparedSentence(s
                , "UPDATE RECEIPTS SET DATENEW=? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE);
                        receiptFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                            setTimestamp(1, new Timestamp(date.getTime()));
                            setString(2, ticket.getId());
                       }});*/
        return true;
    }

        public final Boolean updateTicketFiscalHasar2(final TicketInfo ticket,File fileStatus) throws BasicException, FileNotFoundException, IOException, ParseException {
            DataLogicSystem logicSystem = new DataLogicSystem();
            logicSystem.init(this.s);
            FileReader reader = new FileReader(fileStatus);
            BufferedReader br = new BufferedReader(reader); 
            String status = "",line; 
            int type = ticket.getTicketType();
            Hasar objHasar =new Hasar();
            
            while ((line = br.readLine()) != null) {
                status = line;
            }
            String[] statusArray;            
            String aux = "";
            if(status!=null){
                statusArray = status.split(String.valueOf(objHasar.FS));    
                if(ticket.getTicketType()==0)        
                    aux = statusArray[7];
                else
                    aux = statusArray[9];  
            }
            System.out.println(aux);
            Properties propsdb = logicSystem.getResourceAsProperties(getHostName() + "/properties");
            String serial = propsdb.getProperty("serialNumber");

        if(checkFiscalNumber(Integer.parseInt(aux), serial,type)>0)
            return false;
        
        if(aux != null && !"".equals(aux) &&  serial != null && !"".equals(serial)){
            final String fiscalnumber = String.format("%08d", Integer.parseInt(aux));
            final String fiscalserial = serial;
            SentenceExec ticketFiscalUpdate = new PreparedSentence(s
                    , "UPDATE TICKETS SET fiscalnumber=? , fiscalserial = ? WHERE ID = ?"
                    , SerializerWriteParams.INSTANCE);
                            ticketFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, fiscalnumber);
                                setString(2, fiscalserial);
                                setString(3, ticket.getId());
                           }});
        }
        return true;

        }
    
    public final Boolean updateTicketFiscalHasar(final TicketInfo ticket,File fileStatus) throws BasicException, FileNotFoundException, IOException, ParseException {
        DataLogicSystem logicSystem = new DataLogicSystem();
        logicSystem.init(this.s);
        FileReader reader = new FileReader(fileStatus);
        BufferedReader br = new BufferedReader(reader); 
        String status; 
        String[] statusArray;
        int type = ticket.getTicketType();
        if((status = br.readLine()) == null) {
            return false;
        }
        statusArray = status.split("\\|");
        //La posicion 3 contiene el nro de factura, posicion 10 nro de nota de credito
        String aux = "";
        Properties propsdb = logicSystem.getResourceAsProperties(getHostName() + "/properties");
        String serial = propsdb.getProperty("serialNumber");
        if(ticket.getTicketType()==0)        
            aux = statusArray[3];
        else
            aux = statusArray[10];      
        
        if(checkFiscalNumber(Integer.parseInt(aux), serial,type)>0)
            return false;
        
        if(aux != null && !"".equals(aux) &&  serial != null && !"".equals(serial)){
            final String fiscalnumber = String.format("%08d", Integer.parseInt(aux));
            final String fiscalserial = serial;
            SentenceExec ticketFiscalUpdate = new PreparedSentence(s
                    , "UPDATE TICKETS SET fiscalnumber=? , fiscalserial = ? WHERE ID = ?"
                    , SerializerWriteParams.INSTANCE);
                            ticketFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, fiscalnumber);
                                setString(2, fiscalserial);
                                setString(3, ticket.getId());
                           }});
        }
        return true;
    }
        
    public final Boolean updateTicketFiscalBematech(final TicketInfo ticket,File fileStatus) throws BasicException, FileNotFoundException, IOException, ParseException {
        FileReader reader = new FileReader(fileStatus);
        BufferedReader br = new BufferedReader(reader); 
        int currentline = 1;
        String[] lineFiscalNo = null; 
        String[] lineCupon = null; 
        String line;
        while((line = br.readLine()) != null) {
            if(currentline==9){
               lineFiscalNo = line.split(" ");               
            }
            if(currentline==19){
               lineCupon = line.split(" ");               
            }            
            currentline++;
        }        
        String aux1 = lineFiscalNo[2];
        String aux2 = lineCupon[2];
        
        if(aux1 != null && aux2 != null && aux1 != "" && aux2 != ""){
            final String fiscalnumber = String.format("%08d", Integer.parseInt(aux1));
            final String cupon = String.format("%06d", Integer.parseInt(aux2));
            SentenceExec ticketFiscalUpdate = new PreparedSentence(s
                    , "UPDATE TICKETS SET fiscalnumber=?, cuponno=? WHERE ID = ?"
                    , SerializerWriteParams.INSTANCE);
                            ticketFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, fiscalnumber);
                                setString(2, cupon);
                                setString(3, ticket.getId());
                           }});
        }
        return true;
    }
    
    //Migrate functions 
    public TicketInfo getLastTicketOfMachineFiscal() throws BasicException{
        final Properties m_propsconfig =  new Properties();    
                File file =  new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + ".properties");
                try {
                    InputStream in = new FileInputStream(file);
                    if (in != null) {
                        m_propsconfig.load(in);
                        in.close();
                    }
                } catch (IOException e){
   
                }
        TicketInfo ticket = (TicketInfo) new PreparedSentence(s
                , " SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, T.CUSTOMER, "
                        + " C.SEARCHKEY, C.NAME, C.TAXID, T.FISCALPRINT_SERIAL, T.FISCAL_INVOICENUMBER, T. FISCAL_ZREPORT "
                        + " FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                        + " LEFT OUTER JOIN CUSTOMERS C ON T.CUSTOMER = C.ID "
                        + " WHERE T.MACHINENAME = ? AND TRIM(T.FISCAL_INVOICENUMBER) is null "
                , SerializerWriteParams.INSTANCE
                , new SerializerReadClass(TicketInfo.class))
                .find(new DataParams() { public void writeValues() throws BasicException {
                   setString(1, m_propsconfig.getProperty("machine.hostname"));
                }});
        
        return ticket;
    }
   
    public final Boolean updateTicketFiscal(final TicketInfo ticket) throws BasicException, FileNotFoundException, IOException, ParseException {
        
        
        
        DataLogicSystem logicSystem = new DataLogicSystem();
        logicSystem.init(this.s);
        Properties propsdb = logicSystem.getResourceAsProperties(getHostName() + "/properties");
        String serial = propsdb.getProperty("serialNumber");
        String aux = String.valueOf(ticket.getTicketId());
        
        final String fiscalnumber = aux;
        final String fiscalserial = serial;
        
        SentenceExec ticketFiscalUpdate = new PreparedSentence(s
                , "UPDATE TICKETS SET fiscalnumber=?, fiscalserial=? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE);
                        ticketFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                            setString(1, fiscalnumber);
                            setString(2, fiscalserial);
                            setString(3, ticket.getId());
                       }});
        
        /*SentenceExec receiptFiscalUpdate = new PreparedSentence(s
                , "UPDATE RECEIPTS SET DATENEW=? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE);
                        receiptFiscalUpdate.exec(new DataParams() { public void writeValues() throws BasicException {
                            setTimestamp(1, new Timestamp(date.getTime()));
                            setString(2, ticket.getId());
                       }});*/
        return true;
    }
    public final TicketInfo loadTicketFiscal(final int tickettype, final int ticketid) throws BasicException {
        TicketInfo ticket = (TicketInfo) new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, T.CUSTOMER, "
                        + " C.SEARCHKEY, C.NAME, C.TAXID, T.FISCALPRINT_SERIAL, T.FISCAL_INVOICENUMBER, T. FISCAL_ZREPORT "
                        + " FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                        + " LEFT OUTER JOIN CUSTOMERS C ON T.CUSTOMER = C.ID "
                        + " WHERE T.TICKETTYPE = ? AND T.TICKETID = ? "
                , SerializerWriteParams.INSTANCE
                , new SerializerReadClass(TicketInfo.class))
                .find(new DataParams() { public void writeValues() throws BasicException {
                    setInt(1, tickettype);
                    setInt(2, ticketid);
                }});
        if (ticket != null) {
            DataLogicSales logicSales= new DataLogicSales();
            logicSales.init(this.s);
            String customerid = ticket.getCustomerId();
            ticket.setCustomer(customerid == null
                    ? null
                    : logicSales.loadCustomerExt(customerid));

            ticket.setLines(new PreparedSentence(s
                , "SELECT L.TICKET, L.LINE, L.PRODUCT, L.ATTRIBUTESETINSTANCE_ID, L.UNITS, L.PRICE, T.ID, T.NAME, T.CATEGORY, T.VALIDFROM, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES " +
                  "FROM TICKETLINES L, TAXES T WHERE L.TAXID = T.ID AND L.TICKET = ? ORDER BY L.LINE"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(TicketLineInfo.class)).list(ticket.getId()));
            ticket.setPayments(new PreparedSentence(s
                , "SELECT PAYMENT, TOTAL, TRANSID FROM PAYMENTS WHERE RECEIPT = ?"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentInfoTicket.class)).list(ticket.getId()));
        }
        return ticket;
    }
    
    public String getTableDetails (String ticketID){
       try{
            SQL = "SELECT NAME FROM PLACES WHERE TICKETID='"+ ticketID + "'";   
            stmt = (Statement) conFiscal.createStatement();  
            rs = stmt.executeQuery(SQL);
            if (rs.next()){
                String name =rs.getString("NAME");
                return(name);
            }    
        }catch(Exception e){
        
        }
        return "";
   }

    public String salesByCategory(String startDate,DataLogicSystem dlSystem) {
        Properties propsdb = dlSystem.getResourceAsProperties(getHostName() + "/properties");
        //Properties prop = dlSystem.getResourceAsProperties("fiscalprint.properties");        
        String vendor = propsdb.getProperty("vendor"); 
        p_vendor =vendor;
        String sales = "";
        if (vendor.compareTo("thefactory") == 0) {
            String prefix="800";
            sales=prefix+formatMsgToLeft("Tipo Venta")+formatMsgToRight("Cantidad")+formatMsgToRight("Total")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT CATEGORIES.NAME, " +
                        "SUM(TICKETLINES.UNITS) AS QTY, " +
                        "SUM(TICKETLINES.PRICE * TICKETLINES.UNITS) AS CATTOTAL " +
                        "FROM (TICKETS INNER JOIN RECEIPTS ON TICKETS.ID = RECEIPTS.ID) INNER JOIN ((CATEGORIES INNER JOIN PRODUCTS ON CATEGORIES.ID = PRODUCTS.CATEGORY) INNER JOIN (TAXES INNER JOIN TICKETLINES ON TAXES.ID = TICKETLINES.TAXID) ON PRODUCTS.ID = TICKETLINES.PRODUCT) ON TICKETS.ID = TICKETLINES.TICKET " +
                        "WHERE RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' AND PRODUCTS.ID!='000' " +
                        "GROUP BY categories.ID, categories.NAME " +
                        "ORDER BY CATEGORIES.NAME ";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=prefix+formatMsgToLeft(rs.getString("NAME"))+formatMsgToRight(String.valueOf(rs.getDouble("QTY")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("CATTOTAL")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("CATTOTAL");
                }    
                sales+=prefix+formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }          
        }        
        if (vendor.compareTo("bematech") == 0) {
            sales=formatMsgToLeft("Tipo Venta")+formatMsgToRight("Cantidad")+formatMsgToRight("Total")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT CATEGORIES.NAME, " +
                        "SUM(TICKETLINES.UNITS) AS QTY, " +
                        "SUM(TICKETLINES.PRICE * TICKETLINES.UNITS) AS CATTOTAL " +
                        "FROM (TICKETS INNER JOIN RECEIPTS ON TICKETS.ID = RECEIPTS.ID) INNER JOIN ((CATEGORIES INNER JOIN PRODUCTS ON CATEGORIES.ID = PRODUCTS.CATEGORY) INNER JOIN (TAXES INNER JOIN TICKETLINES ON TAXES.ID = TICKETLINES.TAXID) ON PRODUCTS.ID = TICKETLINES.PRODUCT) ON TICKETS.ID = TICKETLINES.TICKET " +
                        "WHERE RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' AND PRODUCTS.ID!='000' " +
                        "GROUP BY categories.ID, categories.NAME " +
                        "ORDER BY CATEGORIES.NAME ";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=formatMsgToLeft(rs.getString("NAME"))+formatMsgToRight(String.valueOf(rs.getDouble("QTY")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("CATTOTAL")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("CATTOTAL");
                }    
                sales+=formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";


            }catch(SQLException e){
                return e.getMessage();
            }            
        }           
        if (vendor.compareTo("hasar") == 0) {
            
        }           
        return sales;
    }
   
    public String salesByTaxes(String startDate,DataLogicSystem dlSystem) {        
        Properties propsdb = dlSystem.getResourceAsProperties(getHostName() + "/properties");
        String vendor = propsdb.getProperty("vendor"); 
        p_vendor =vendor;
        String sales = "";
        if (vendor.compareTo("thefactory") == 0) {
            String prefix="800";
            sales=prefix+formatMsgToLeft("Impuesto")+formatMsgToRight("Base")+formatMsgToRight("Monto")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT TAXCATEGORIES.NAME AS TAXNAME, SUM(TAXLINES.BASE) TOTALBASE,SUM(TAXLINES.AMOUNT) AS TOTALTAXES " +
                        "FROM RECEIPTS, TAXLINES, TAXES, TAXCATEGORIES  " +
                        "WHERE RECEIPTS.ID = TAXLINES.RECEIPT AND TAXLINES.TAXID = TAXES.ID AND TAXES.CATEGORY = TAXCATEGORIES.ID " +
                        "AND RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' " +
                        "GROUP BY TAXCATEGORIES.ID,  TAXCATEGORIES.NAME ";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=prefix+formatMsgToLeft(rs.getString("TAXNAME"))+formatMsgToRight(String.valueOf(rs.getDouble("TOTALBASE")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("TOTALTAXES")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("TOTALTAXES");
                }    
                sales+=prefix+formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";


            }catch(SQLException e){
                return e.getMessage();
            }            
        }        
        if (vendor.compareTo("bematech") == 0) {
            sales=formatMsgToLeft("Impuesto")+formatMsgToRight("Base")+formatMsgToRight("Monto")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT TAXCATEGORIES.NAME AS TAXNAME, SUM(TAXLINES.BASE) TOTALBASE,SUM(TAXLINES.AMOUNT) AS TOTALTAXES " +
                        "FROM RECEIPTS, TAXLINES, TAXES, TAXCATEGORIES  " +
                        "WHERE RECEIPTS.ID = TAXLINES.RECEIPT AND TAXLINES.TAXID = TAXES.ID AND TAXES.CATEGORY = TAXCATEGORIES.ID " +
                        "AND RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' " +
                        "GROUP BY TAXCATEGORIES.ID,  TAXCATEGORIES.NAME ";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=formatMsgToLeft(rs.getString("TAXNAME"))+formatMsgToRight(String.valueOf(rs.getDouble("TOTALBASE")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("TOTALTAXES")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("TOTALTAXES");
                }    
                sales+=formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";


            }catch(SQLException e){
                return e.getMessage();
            }            
        }
        if (vendor.compareTo("hasar") == 0) {
            
        }        
        return sales;
    
    }
    
    public String salesByProduct(String startDate,DataLogicSystem dlSystem) {
        Properties propsdb = dlSystem.getResourceAsProperties(getHostName() + "/properties");
        //Properties prop = dlSystem.getResourceAsProperties("fiscalprint.properties");
        String vendor = propsdb.getProperty("vendor");
        p_vendor =vendor;
        String sales = "";
        if (vendor.compareTo("thefactory") == 0) {
            String prefix="800";
            sales=prefix+formatMsgToLeft("Producto")+formatMsgToRight("Unidades")+formatMsgToRight("Monto")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT " +
                        "PRODUCTS.NAME, " +
                        "SUM(TICKETLINES.UNITS) AS UNITS, " +
                        "SUM(TICKETLINES.UNITS * TICKETLINES.PRICE) AS TOTAL " +
                        "FROM RECEIPTS, TICKETS, TICKETLINES, PRODUCTS " +
                        "WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND TICKETLINES.PRODUCT = PRODUCTS.ID " +
                        "AND RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' AND PRODUCTS.ID!='000' " +
                        "GROUP BY PRODUCTS.REFERENCE, PRODUCTS.NAME " +
                        "ORDER BY PRODUCTS.NAME";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=prefix+formatMsgToLeft(rs.getString("NAME"))+formatMsgToRight(String.valueOf(rs.getDouble("UNITS")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("TOTAL")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("TOTAL");
                }    
                sales+=prefix+formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }
        }
        if (vendor.compareTo("bematech") == 0) {
            sales=formatMsgToLeft("Producto")+formatMsgToRight("Unidades")+formatMsgToRight("Monto")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT " +
                        "PRODUCTS.NAME, " +
                        "SUM(TICKETLINES.UNITS) AS UNITS, " +
                        "SUM(TICKETLINES.UNITS * TICKETLINES.PRICE) AS TOTAL " +
                        "FROM RECEIPTS, TICKETS, TICKETLINES, PRODUCTS " +
                        "WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND TICKETLINES.PRODUCT = PRODUCTS.ID " +
                        "AND RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' AND PRODUCTS.ID!='000' " +
                        "GROUP BY PRODUCTS.REFERENCE, PRODUCTS.NAME " +
                        "ORDER BY PRODUCTS.NAME";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=formatMsgToLeft(rs.getString("NAME"))+formatMsgToRight(String.valueOf(rs.getDouble("UNITS")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("TOTAL")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("TOTAL");
                }    
                sales+=formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }            
        }
        if (vendor.compareTo("hasar") == 0) {
            
        }
        return sales;
        
    }
    
    public String salesByHour(String startDate,DataLogicSystem dlSystem) {
        //Properties prop = dlSystem.getResourceAsProperties("fiscalprint.properties");
        Properties propsdb = dlSystem.getResourceAsProperties(getHostName() + "/properties");
        String vendor = propsdb.getProperty("vendor"); 
        p_vendor =vendor;
        String sales = "";
        if (vendor.compareTo("thefactory") == 0) {
            String prefix="800";
            sales=prefix+formatMsgToLeft("Hora")+formatMsgToRight("Transacciones")+formatMsgToRight("Valor")+"\n";
            double amt=0.0;
            try{
                SQL = "select date_part('hour',r.datenew) salehour,count(r.id) transact, sum(tl.units*tl.price) amt from receipts r inner join ticketlines tl on r.id=tl.ticket " +
                        "where datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' AND tl.product!='000' " +
                        "group by salehour " +
                        "order by salehour";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=prefix+formatMsgToLeft(rs.getString("salehour"))+formatMsgToRight(String.valueOf(rs.getInt("transact")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("amt")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("amt");
                }    
                sales+=prefix+formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }            
        }
        if (vendor.compareTo("bematech") == 0) {
            sales=formatMsgToLeft("Hora")+formatMsgToRight("Transacciones")+formatMsgToRight("Valor")+"\n";
            double amt=0.0;
            try{
                SQL = "select date_part('hour',r.datenew) salehour,count(r.id) transact, sum(tl.units*tl.price) amt from receipts r inner join ticketlines tl on r.id=tl.ticket " +
                        "where datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' AND tl.product!='000' " +
                        "group by salehour " +
                        "order by salehour";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=formatMsgToLeft(rs.getString("salehour"))+formatMsgToRight(String.valueOf(rs.getInt("transact")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("amt")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("amt");
                }    
                sales+=formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }            
        }
        if (vendor.compareTo("hasar") == 0) {
            
        }
        return sales;
    }
    
    public String salesByPaymentType(String startDate,DataLogicSystem dlSystem) {
        //Properties prop = dlSystem.getResourceAsProperties("fiscalprint.properties");
        Properties propsdb = dlSystem.getResourceAsProperties(getHostName() + "/properties");
        String vendor = propsdb.getProperty("vendor"); 
        p_vendor =vendor;
        String sales = "";
        if (vendor.compareTo("thefactory") == 0) {
            String prefix="800";
            sales=prefix+formatMsgToLeft("Forma Pago")+formatMsgToRight("Transacciones")+formatMsgToRight("Monto")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT " +
                        "PAYMENTS.PAYMENT, " +
                        "COUNT(PAYMENTS.PAYMENT) AS TRANSACTIONS, " +
                        "SUM(TICKETLINES.TOTAL) AS TOTAL " +
                        "FROM RECEIPTS, TICKETS, PAYMENTS,"
                        + "(SELECT TICKET,SUM(TICKETLINES.UNITS * TICKETLINES.PRICE) AS TOTAL  FROM TICKETLINES GROUP BY TICKET) as TICKETLINES " +
                        "WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND PAYMENTS.RECEIPT=RECEIPTS.ID " +
                        "AND RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' " +
                        "GROUP BY PAYMENTS.PAYMENT " +
                        "ORDER BY PAYMENTS.PAYMENT";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=formatMsgToLeft(rs.getString("PAYMENT"))+formatMsgToRight(String.valueOf(rs.getDouble("TRANSACTIONS")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("TOTAL")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("TOTAL");
                }    
                sales+=prefix+formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }
        }
        if (vendor.compareTo("bematech") == 0) {
            sales=formatMsgToLeft("Forma Pago")+formatMsgToRight("Transacciones")+formatMsgToRight("Monto")+"\n";
            double amt=0.0;
            try{
                SQL = "SELECT " +
                        "PAYMENTS.PAYMENT, " +
                        "COUNT(PAYMENTS.PAYMENT) AS TRANSACTIONS, " +
                        "SUM(TICKETLINES.TOTAL) AS TOTAL " +
                        "FROM RECEIPTS, TICKETS, PAYMENTS,"
                        + "(SELECT TICKET,SUM(TICKETLINES.UNITS * TICKETLINES.PRICE) AS TOTAL  FROM TICKETLINES GROUP BY TICKET) as TICKETLINES " +
                        "WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND PAYMENTS.RECEIPT=RECEIPTS.ID " +
                        "AND RECEIPTS.datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' " +
                        "GROUP BY PAYMENTS.PAYMENT " +
                        "ORDER BY PAYMENTS.PAYMENT";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){
                    sales+=formatMsgToLeft(rs.getString("PAYMENT"))+formatMsgToRight(String.valueOf(rs.getDouble("TRANSACTIONS")))+formatMsgToRight(String.valueOf(BigDecimal.valueOf(rs.getDouble("TOTAL")).setScale(2, RoundingMode.CEILING)))+"\n";
                    amt+=rs.getDouble("TOTAL");
                }    
                sales+=formatMsgToLeft("Total:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }            
        }
        if (vendor.compareTo("hasar") == 0) {
            
        }
        return sales;
        
    }
          
    public String totalTips(String startDate,DataLogicSystem dlSystem) {
        //Properties prop = dlSystem.getResourceAsProperties("fiscalprint.properties");
        Properties propsdb = dlSystem.getResourceAsProperties(getHostName() + "/properties");
        String vendor = propsdb.getProperty("vendor"); 
        p_vendor =vendor;
        String sales = "";
        if (vendor.compareTo("thefactory") == 0) {
            String prefix="800";
            sales="";
            double amt=0.0;
            try{
                SQL = "select tl.product, sum(tl.units*tl.price) amt from receipts r inner join ticketlines tl on r.id=tl.ticket " +
                        "where datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' and product = '000' " +
                        "group by tl.product ";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){

                    amt+=rs.getDouble("amt");
                }    
                sales+=prefix+formatMsgToLeft("Propinas:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }            
        } 
        if (vendor.compareTo("bematech") == 0) {
            sales="";
            double amt=0.0;
            try{
                SQL = "select tl.product, sum(tl.units*tl.price) amt from receipts r inner join ticketlines tl on r.id=tl.ticket " +
                        "where datenew between date '"+startDate+"' + time '6:00' and date '"+startDate+"'+1 + time '6:00' and product = '000' " +
                        "group by tl.product ";
                stmt = (Statement) conFiscal.createStatement();  
                rs = stmt.executeQuery(SQL);

                while (rs.next()){

                    amt+=rs.getDouble("amt");
                }    
                sales+=formatMsgToLeft("Propinas:")+formatMsgToLeft(" ")+formatMsgToRight(String.valueOf(BigDecimal.valueOf(amt).setScale(2, RoundingMode.CEILING)))+"\n";
            }catch(SQLException e){
                return e.getMessage();
            }            
        } 
        if (vendor.compareTo("hasar") == 0) {
            
        }         
        return sales;
    }
    
    public String formatMsgToRight(String msg){
        String desc = msg;
        int len = desc.length();
        if(p_vendor.compareTo("thefactory") == 0) {  
        if(len <=10){
            for (int i =0;i<10-len;i++){
                desc = " " + desc;
            }
        }
        return desc.substring(0,10);            
        }
        if(p_vendor.compareTo("bematech") == 0) {
            if(len <=15){
                for (int i =0;i<15-len;i++){
                    desc = " " + desc;
                }
            }
            return desc.substring(0,15);             
        } 
        if(p_vendor.compareTo("hasar") == 0) {  
            if(len <=10){
               for (int i =0;i<10-len;i++){
                   desc = " " + desc;
               }
           }
           return desc.substring(0,10);            
        }
        return "";
    }
   
    public String formatMsgToLeft(String msg){
        String desc = msg;
        int len = desc.length();
        if(p_vendor.compareTo("thefactory") == 0) {                        
            if(len <=10){
                for (int i = 0;i<10-len;i++){
                    desc = desc + " ";
                }
            }
            return desc.substring(0,10);            
        }else if(p_vendor.compareTo("bematech") == 0) {
            if(len <=15){
                for (int i = 0;i<15-len;i++){
                    desc = desc + " ";
                }
            }
            return desc.substring(0,15);             
        }
        else if(p_vendor.compareTo("hasar") == 0) {
            if(len <=10){
                for (int i = 0;i<10-len;i++){
                    desc = desc + " ";
                }
            }
            return desc.substring(0,10);             
        }
        return "";
    }
    
    public final ProductInfoExt getProductInfo(String id) throws BasicException {
	return (ProductInfoExt) new PreparedSentence(s
		, "SELECT "
                + "ID, "
                + "REFERENCE, "
                + "CODE, "
                + "CODETYPE, "                        
                + "NAME, " 
                + "PRICEBUY, "
                + "PRICESELL, "
                + "CATEGORY, "
                + "TAXCAT, "
                + "ATTRIBUTESET_ID, "
                + "STOCKCOST, "
                + "STOCKVOLUME, "
                + "IMAGE, "
                + "ISCOM, "
                + "ISSCALE, "
                + "ISCONSTANT, "
                + "PRINTKB, "
                + "SENDSTATUS, "                        
                + "ISSERVICE, "
                + "ATTRIBUTES, "
                + "DISPLAY, "
                + "ISVPRICE, "
                + "ISVERPATRIB, "
                + "TEXTTIP, "
                + "WARRANTY, "
                + "COALESCE(STOCKCURRENT.UNITS,0.0), "   
                        + "printto, "  
                        + "supplier, "  
                        + "uom, "  
                        + "memodate "                       
                + "FROM STOCKCURRENT LEFT JOIN PRODUCTS ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) "
                + "WHERE ID = ? "
                + "GROUP BY ID, REFERENCE, NAME,STOCKCURRENT.UNITS;"
		, SerializerWriteString.INSTANCE
		, ProductInfoExt.getSerializerRead()).find(id);
    }

public boolean hasfiscalinfo(final TicketInfo ticket) throws BasicException{
        PreparedSentence p = new PreparedSentence(s
                , "SELECT CASE WHEN FISCALNUMBER IS NOT NULL THEN 'TRUE' ELSE 'FALSE ' END AS ISFISCAL FROM TICKETS WHERE TICKETID=? AND TICKETTYPE=?"
                //, new SerializerWriteBasic(new Datas[] {
                //Datas.OBJECT, Datas.INT})
                , SerializerWriteParams.INSTANCE
                , SerializerReadString.INSTANCE);
        String isfiscal;
        isfiscal = (String) p.find(new DataParams() {@Override
                                        public void writeValues() throws BasicException {
                                            setInt(1, ticket.getTicketId());
                                            setInt(2, ticket.getTicketType());
                                        }});
        return "TRUE".equals(isfiscal);    
}

    public String getHostName() {
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

    public final int checkFiscalNumber(final int no,final String serial, final int type) throws BasicException {
        PreparedSentence p = new PreparedSentence(s
                , "SELECT count(*) FROM TICKETS WHERE fiscalnumber::numeric=?::numeric AND fiscalserial=? and tickettype = ?"
                //, new SerializerWriteBasic(new Datas[] {
                //Datas.OBJECT, Datas.INT})
                , SerializerWriteParams.INSTANCE
                , SerializerReadInteger.INSTANCE);
        System.out.println("EL PARAMETRO: "+no);
        
        int fNumber = (int) p.find(new DataParams() {@Override  
                                        public void writeValues() throws BasicException {
                                            setInt(1, no);
                                            setString(2, serial);
                                            setInt(3, type);
                                        }});
        System.out.println("EL RESULTADO: "+fNumber);
        return fNumber;
    }

    public void printDocument(TicketInfo ticket) throws Exception {
		//if(p_C_Invoice_ID != 0){
			//MInvoice m_invoice = new MInvoice(getCtx(), p_C_Invoice_ID, get_TrxName());
			//if(m_invoice.getDocStatus().equals(DocAction.STATUS_Completed)){
				//if(!m_invoice.get_ValueAsBoolean("XX_PrintFiscalDocument")){
					//MUser user = new MUser(getCtx(), p_AD_User_ID, get_TrxName());
					//int m_XX_FiscalPrinterConfig_ID = user.get_ValueAsInt("XX_FiscalPrinterConfig_ID");
					//if(m_XX_FiscalPrinterConfig_ID != 0){
						//MDocType m_DocType = new MDocType(getCtx(), m_invoice.getC_DocType_ID(), get_TrxName());
						//	Fiscal Document
						//int m_XX_DocFiscalPrinterNo_ID = m_DocType.get_ValueAsInt("XX_DocFiscalPrinterNo_ID");
						//if(m_XX_DocFiscalPrinterNo_ID != 0){
							//	Load Fiscal No
						//	MXXDocFiscalPrinterNo docNoFiscal = new MXXDocFiscalPrinterNo(getCtx(), m_XX_DocFiscalPrinterNo_ID, get_TrxName());
							
						//	MXXFiscalPrinterConfig m_fpConfig = new MXXFiscalPrinterConfig(getCtx(), m_XX_FiscalPrinterConfig_ID, get_TrxName());
        DataLogicSystem logicSystem = new DataLogicSystem();
        logicSystem.init(this.s);
        DataLogicFiscal logicfiscal = new DataLogicFiscal();
        logicfiscal.init(this.s);
        Properties propsdb = logicSystem.getResourceAsProperties(logicfiscal.getHostName() + "/properties");
        //String portNumber = propsdb.getProperty("portNumber");
        String portNumber = propsdb.getProperty("portNumber");
        int speed = 9600;
        //String spoolerRoute = propsdb.getProperty("spoolerRoute");
        String spoolerFolder = propsdb.getProperty("spoolerFolder");
        String spoolerName = "wspooler.exe";
        String serialNumber = propsdb.getProperty("serialNumber");
        String logRoute = propsdb.getProperty("logRoute");
        String PathTmp = propsdb.getProperty("PathTmp");


        HandlerSpooler hsp = new HandlerSpooler(portNumber,speed,spoolerFolder.concat(spoolerName),logRoute,PathTmp,"", //null for windows sh for linux
                                                serialNumber,28);

        //MBPartner cp = new MBPartner(getCtx(), m_invoice.getC_BPartner_ID(), get_TrxName());

        //	Document Affected
        //int m_DA_ID = m_invoice.get_ValueAsInt("XX_DocAffected");

         String nroVoucher = null;
         String codPrinter = null;
         String dateVoucher = null;
         String timeVoucher = null;

        /*if(m_DA_ID != 0){
                MInvoice m_XX_DocAffected = new MInvoice(getCtx(), m_DA_ID, get_TrxName());
                nroVoucher = m_XX_DocAffected.get_ValueAsString("XX_FiscalNo");
                codPrinter = m_XX_DocAffected.get_ValueAsString("PrinterId");
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMdd");
                SimpleDateFormat sdfTime = new SimpleDateFormat("hhmmss");
                Timestamp dV = m_XX_DocAffected.getDateInvoiced();//_ValueAsString("PrinterId");
                dateVoucher = sdfDate.format(dV);
                timeVoucher = sdfTime.format(dV);
                if(nroVoucher == null || nroVoucher.equals(""))
                        throw new Exception(Msg.translate(getCtx(), "SGNotDocAff"));	//	No existe Codigo del Documento Afectado
                if(codPrinter == null || codPrinter.equals(""))
                        throw new Exception(Msg.translate(getCtx(), "SGNotPrinterIDDocAff"));	//	No existe Codigo de Impresora en el Documento Afectado
        }*/
        //	Next Sequence
        //MXXDocFiscalPrinterNo pdn = new MXXDocFiscalPrinterNo(getCtx(), m_XX_DocFiscalPrinterNo_ID, get_TrxName());

        //String salesOrder = m_invoice.getC_Order().getDocumentNo();
        //String poReference = m_invoice.getC_Order().getPOReference();
        StringBuilder soRefNo = new StringBuilder();
        String auxname="CONTADO";
        String auxaddress="Panama";
        String auxtaxid="";
        //if(salesOrder == null)
        //	salesOrder = " ";

        soRefNo.append("*" + "Ticket: " + ":").append(ticket.getTicketId()).append("*");
        soRefNo.append(" ");

        //if(poReference == null)
                //poReference = " ";

        //soRefNo.append("*" + Msg.translate(getCtx(), "FP_PORef") + ":" + poReference + "*");	

        int top = (soRefNo.toString().length() -1 >= 0? soRefNo.toString().length() -1: 0);

        if(top > 45)
                top = 45;
        //Print TicketNo
        hsp.printTextHeader(soRefNo.toString().substring(0, top));

        //hsp.printTextHeader("*" + Msg.translate(getCtx(), "FP_RefAdempiere") + ":" + (m_invoice.getDocumentNo() != null? m_invoice.getDocumentNo(): "") + "*");
        if(ticket.printCustomer()!=null){
            if(ticket.printCustomer().trim().compareTo("")!=0){
              auxname=ticket.printCustomer().replace("&amp;", "&").replace("&quot;", "\"").replace("&apos;", "\'");
            }
            if(ticket.getCustomer().getTaxid() != null){
                if(ticket.getCustomer().getTaxid().trim().compareTo("")!=0){
                    auxtaxid=ticket.getCustomer().getTaxid().trim();
                }
            }                     
        }
        //Print Customer Name
        if(ticket.getTicketType()==0){
            hsp.printHeader(auxname, auxtaxid, nroVoucher, codPrinter, dateVoucher, timeVoucher, "A");
        }
        else if(ticket.getTicketType()==1){
            hsp.printHeader(auxname, auxtaxid, nroVoucher, codPrinter, dateVoucher, timeVoucher, "D");
        }
        //	get Partner Location
        //I_C_BPartner_Location address = m_invoice.getC_BPartner_Location();
        //	get Location
        //I_C_Location loc = address.getC_Location();
        //	Concat Location
        if(ticket.getCustomer().getAddress()!=null){
            if(ticket.getCustomer().getAddress().trim().compareTo("")!=0){
                auxaddress=ticket.getCustomer().getAddress().trim();
            }
        }                    
        StringBuilder nameLoc = new StringBuilder();
        nameLoc.append(auxaddress);
        /*if(address.getName() != null){
                nameLoc.append(address.getName());
                nameLoc.append(", ");	
        }

        if(loc.getRegionName() != null){
                nameLoc.append(loc.getRegionName());
                nameLoc.append(", ");	
        }

        if(loc.getCity() != null){
                nameLoc.append(loc.getCity());
                nameLoc.append(", ");
        }


        StringBuffer nameLoc1 = new StringBuffer();
        if(loc.getAddress1() != null){
                nameLoc1.append(loc.getAddress1());
                nameLoc1.append(", ");	
        }

        if(loc.getAddress3() != null){
                nameLoc1.append(loc.getAddress2());
                nameLoc1.append(", ");
        }

        StringBuffer nameLoc2 = new StringBuffer();
        if(loc.getAddress3() != null){
                nameLoc2.append(loc.getAddress3());
                nameLoc2.append(", ");
        }

        if(loc.getAddress4() != null){
                nameLoc2.append(loc.getAddress4());
        }*/

        /*StringBuffer soPtSr = new StringBuffer();
        String paymentTerm = m_invoice.getC_PaymentTerm().getName();
        String salesRep = m_invoice.getSalesRep().getName();

        if(paymentTerm != null){
            soPtSr.append("*" + Msg.translate(getCtx(), "FP_PT") + ":" + paymentTerm + "*");
            soPtSr.append(" ");	
            }

        if(salesRep != null){
                soPtSr.append("*" + Msg.translate(getCtx(), "FP_SR") + ":" + salesRep + "*");
        }

        top = (soPtSr.toString().length() -1 >= 0? soPtSr.toString().length() -1: 0);

        if(top > 41)
                top = 41;
        hsp.printMessage(soPtSr.toString().substring(0, top) + " ");
        */
        top = (nameLoc.toString().length() -1 >= 0? nameLoc.toString().length() -1: 0);

        if(top > 41)
            top = 41;
        //Print Location 
        hsp.printMessage("Direccion" + ":" + nameLoc.toString().substring(0, top));

        //top = (nameLoc1.toString().length() -1 >= 0? nameLoc1.toString().length() -1: 0);

        /*if(top > 41)
            top = 41;
            hsp.printMessage(nameLoc1.toString().substring(0, top) + " ");
            top = (nameLoc2.toString().length() -1 >= 0? nameLoc2.toString().length() -1: 0);
            if(top > 41)
                top = 41;
                hsp.printMessage(nameLoc2.toString().substring(0, top) + " ");
            */
        //MInvoiceLine [] lInvoices = m_invoice.getLines();
        //for (MInvoiceLine m_InvoiceLine : lInvoices) {
        int length=ticket.getLinesCount();
        String aux=null;
        NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
        f.setMinimumFractionDigits(1);
        if (f instanceof DecimalFormat) {
            ((DecimalFormat) f).setDecimalSeparatorAlwaysShown(true);
        }

        ProductInfoExt product;    
        Double totalLines=0.0;                    
        for (int i = 0; i < length; i++) {
            int pstrlength=40;
            TicketLineInfo line = ticket.getLine(i);                         
            String nameItem = null;
            String valueItem = null;

            if(line.getProductID()!=null){
                product = logicfiscal.getProductInfo(line.getProductID());
                nameItem=product.getName();
                valueItem=product.getReference();
                if(nameItem.length()<=pstrlength){
                    pstrlength=nameItem.length();			
                }
                nameItem=nameItem.substring(0,pstrlength);                        
            }            

            /*if(m_InvoiceLine.getM_Product_ID() != 0){
                                MProduct product = new MProduct(getCtx(), m_InvoiceLine.getM_Product_ID(), get_TrxName());
                                nameItem = product.getName();
                                valueItem = product.getValue();	
                        } else if(m_InvoiceLine.getC_Charge_ID() != 0){
                                MCharge charge = new MCharge(getCtx(), m_InvoiceLine.getC_Charge_ID(), get_TrxName());
                                nameItem = charge.getName();
                                valueItem = charge.getName().substring(0, 3);
                        } else
                                continue;
                                //throw new Exception(Msg.translate(getCtx(), "SGNotItem"));	//	No existe Item
            */

                        //if(m_InvoiceLine.getQtyInvoiced().doubleValue() != 0
                                      //  && m_InvoiceLine.getPriceEntered().doubleValue() != 0){
                                //MTax tax = new MTax(getCtx(), m_InvoiceLine.getC_Tax_ID(), get_TrxName());
            hsp.printLine(nameItem, 
                (f.format(Math.abs(line.getMultiply()))), 
                (f.format(line.getPrice())), 
                (f.format(line.getTaxRate()*100)), 
                null, 
                valueItem);
            }        
        //String desc = new String();

        //	Print Description
        //if(m_invoice.getDescription() != null
        //                 && m_invoice.getDescription().length() > 0){
        //        desc = m_invoice.getDescription();
        //}

        //top = (desc.length() -1 >= 0? desc.length() -1: 0);
        //if(top > 45)
        //        top = 45;
        //hsp.printTextTrailer(desc.substring(0, top).trim() + " ");
        
        //FORMAS DE PAGO
        Iterator i = ticket.getPayments().iterator();
        Double montoefectivo=0.0;
        Double totalPayment=0.0;   
        BigDecimal bd =null; 
        while (i.hasNext()){
            PaymentInfo paymentline = (PaymentInfo)i.next();
            aux=f.format(paymentline.getTotal());  
            if (paymentline.getName().compareTo("cash")==0){
                if(paymentline.getChange()<=10000000.0){
                    hsp.printPaymentForm("Efectivo", aux, 1);    
                }                
            }else if (paymentline.getName().compareTo("magcard")==0){
                    hsp.printPaymentForm("Tarjeta", aux, 2);    
                }
            }        
        //FIN FORMAS DE PAGO
        hsp.printCommand("E");
        hsp.get_g_Status();
        //	Printer ID
                            /*m_invoice.set_ValueOfColumn("PrinterId", m_fpConfig.getPrinterId());

                            //	Next Sequence
                            m_invoice.set_ValueOfColumn("XX_FiscalNo", pdn.nextSequence());
                            m_invoice.set_ValueOfColumn("XX_PrintFiscalDocument", "Y");
                            m_invoice.saveEx();
                            pdn.saveEx();
                            message = "OK";*/
/*						} else
							throw new Exception(Msg.translate(getCtx(), "SGNotConfDocType"));	//	Secuencia Fiscal No Configurada en el Tipo de Documento
					} else
						throw new Exception(Msg.translate(getCtx(), "SGNotConfFiscalPrinter"));	//	Impresora Fiscal No Configurada
				} else
					throw new Exception(Msg.translate(getCtx(), "SGSentDoc"));	//	El Documento ya fue enviado a la Impresora Fiscal
			} else
				throw new Exception(Msg.translate(getCtx(), "SGNotCompletedDoc"));	//	Documento no Completado
		} else
			throw new Exception(Msg.translate(getCtx(), "SGDocNotFound"));	//	Documento no Encontrado*/
	}
	    
}
