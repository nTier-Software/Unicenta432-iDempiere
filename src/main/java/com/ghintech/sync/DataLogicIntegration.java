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

package com.ghintech.sync;

import java.util.List;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataParams;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.PreparedSentence;
import com.openbravo.data.loader.SerializerRead;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.data.loader.SerializerWriteParams;
import com.openbravo.data.loader.SerializerWriteString;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.loader.Transaction;
import com.ghintech.sync.customers.CustomerInfoExt;
import com.ghintech.sync.externalsales.CategoryInfo2;
import com.ghintech.sync.people.User;
import com.openbravo.pos.forms.BeanFactoryDataSingle;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.payment.PaymentInfoTicket;
import com.openbravo.pos.ticket.CategoryInfo;
import com.ghintech.sync.externalsales.ClosedCashInfo;
import com.ghintech.sync.externalsales.CreditNoteInfo;
import com.ghintech.sync.externalsales.ProductInfoExt2;
import com.ghintech.sync.movement.MaterialProdInfoExt;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.data.loader.SentenceExecTransaction;
import com.openbravo.data.loader.SentenceFind;
import com.openbravo.data.loader.SerializerReadBasic;
import com.openbravo.data.loader.SerializerReadDouble;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.SerializerWriteBasicExt;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.AppViewConnection;
import com.openbravo.pos.inventory.ProductsBundleInfo;
import com.openbravo.pos.payment.JPaymentGiftCardPanel;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 * Created on 5 de marzo de 2007, 19:56
 * @contributor Sergio Oropeza - Double Click Sistemas - Venezuela - soropeza@dcs.net.ve, info@dcs.net.ve
 *
 */
public class DataLogicIntegration extends BeanFactoryDataSingle {
    
    protected Session s;
    private PreparedStatement pstmt;
    private String SQL;
    private ResultSet rs;
    private Statement stmt;
    private Connection conInt;
    protected Datas[] stockAdjustDatas;
    protected Datas[] stockdiaryDatas;
    /** Creates a new instance of DataLogicIntegration */
    public DataLogicIntegration() {
        stockAdjustDatas = new Datas[] {
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE};
        stockdiaryDatas = new Datas[] {
            Datas.STRING, Datas.TIMESTAMP, Datas.INT, Datas.STRING, 
            Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, 
            Datas.STRING, Datas.STRING, Datas.STRING};
    }
    
    public void init(AppView app) {
        try {
            this.s = AppViewConnection.createSession(app.getProperties());
            try{
            
                conInt=s.getConnection();
            }
            catch (SQLException e){
                System.out.print("No session or connection");
            }
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicIntegration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public void syncCustomersBefore() throws BasicException {
        new StaticSentence(s, "UPDATE CUSTOMERS SET VISIBLE = " + s.DB.FALSE()).exec();
    }

    public void syncCustomer(final CustomerInfoExt customer) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Customer in a transaction

                // Try to update
	        // Add the field TaxID to sync...
                if (new PreparedSentence(s,
                            "UPDATE CUSTOMERS SET TAXID = ?, NAME = ?, ADDRESS = ?, VISIBLE = ?, MAXDEBT=?, UPDATED=? WHERE SEARCHKEY = ? OR ID=? ",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, customer.getTaxid());
                                setString(2, customer.getName());
                                setString(3, customer.getAddress());
                                setBoolean(4, customer.isVisible());
                                setDouble(5, customer.getMaxdebt());
                                //setDouble(6, customer.getCurdebt());
                                setTimestamp(6, customer.getUpdated());
                                setString(7, customer.getSearchkey());
                                setString(8, customer.getId());
                                
                            }}) == 0) {

                    // If not updated, try to insert
                    new PreparedSentence(s,
                            "INSERT INTO CUSTOMERS(ID, TAXID, SEARCHKEY, NAME, NOTES, VISIBLE, MAXDEBT, UPDATED) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, customer.getId());
                                setString(2, customer.getTaxid());
                                setString(3, customer.getSearchkey());
                                setString(4, customer.getName());
                                setString(5, customer.getAddress());
                                setBoolean(6, customer.isVisible());
                                setDouble(7, customer.getMaxdebt());
                                setTimestamp(8, customer.getUpdated());
                                //setDouble(9, customer.getCurdebt());
                            }});
                }

                return null;
            }
        };
        t.execute();
    }
    /*public void syncCustomer(final CustomerInfoExt customer) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Customer in a transaction

                // Try to update
	        // Add the field TaxID to sync...
                if (new PreparedSentence(s,
                            "UPDATE CUSTOMERS SET TAXID = ?, NAME = ?, ADDRESS = ?, VISIBLE = ?, MAXDEBT=?, CURDEBT=?, UPDATED=? WHERE SEARCHKEY = ? OR ID=? ",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, customer.getTaxid());
                                setString(2, customer.getName());
                                setString(3, customer.getAddress());
                                setBoolean(4, customer.isVisible());
                                setDouble(5, customer.getMaxdebt());
                                setDouble(6, customer.getCurdebt());
                                setTimestamp(7, customer.getUpdated());
                                setString(8, customer.getSearchkey());
                                setString(9, customer.getId());
                                
                            }}) == 0) {

                    // If not updated, try to insert
                    new PreparedSentence(s,
                            "INSERT INTO CUSTOMERS(ID, TAXID, SEARCHKEY, NAME, NOTES, VISIBLE, MAXDEBT, UPDATED, CURDEBT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, customer.getId());
                                setString(2, customer.getTaxid());
                                setString(3, customer.getSearchkey());
                                setString(4, customer.getName());
                                setString(5, customer.getAddress());
                                setBoolean(6, customer.isVisible());
                                setDouble(7, customer.getMaxdebt());
                                setTimestamp(8, customer.getUpdated());
                                setDouble(9, customer.getCurdebt());
                            }});
                }

                return null;
            }
        };
        t.execute();
    }*/
        
    public void syncPeople(final User user) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Customer in a transaction

                // Try to update
	        // Add the field TaxID to sync...
                if (new PreparedSentence(s,
                            "UPDATE PEOPLE SET NAME = ?, VISIBLE = ?, UPDATED = ? , APPPASSWORD = ? WHERE ID = ? OR NAME = ?",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, user.getName());
                                setBoolean(2, user.getVisible());
                                setTimestamp(3, user.getUpdated());
                                setString(4, user.getPassword());
                                setString(5, user.getId());
                                setString(6, user.getName());
                                
                            }}) == 0) {

                    // If not updated, try to insert
                    new PreparedSentence(s,
                            "INSERT INTO PEOPLE(ID, NAME, ROLE, VISIBLE, UPDATED, APPPASSWORD) VALUES (?, ?, 103, ?, ?, ?)",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, user.getId());
                                setString(2, user.getName());
                                setBoolean(3, user.getVisible());
                                setTimestamp(4, user.getUpdated());
                                setString(5, user.getPassword());
                            }});
                }

                return null;
            }
        };
        t.execute();
    }
    public void syncCreditmemo(final String BPValue,final Double OpenAmount) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Customer in a transaction

                // Try to update
	        // Add the field TaxID to sync...
                if (new PreparedSentence(s,
                            "UPDATE CUSTOMERS SET curdebt=0, maxdebt = ? WHERE trim(taxid) = trim(?)",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setDouble(1, OpenAmount);
                                setString(2, BPValue);
                                
                            }}) == 0) {

                    // If not updated, try to insert
                   /* new PreparedSentence(s,
                            "INSERT INTO PEOPLE(ID, NAME, ROLE, VISIBLE) VALUES (?, ?, 2, " + s.DB.TRUE() + ")",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, user.getId());
                                setString(2, user.getName());
                            }});*/
                }

                return null;
            }
        };
        t.execute();
    }
    public void syncResendOrders(final int ticketid) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Customer in a transaction

                // Try to update
	        // Add the field TaxID to sync...
                if (new PreparedSentence(s,
                            "UPDATE tickets SET Status=0 WHERE ticketid=? ",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setInt(1, ticketid);
                            }}) == 0) {

                }

                return null;
            }
        };
        t.execute();
    }
    
    public void syncProductsBefore() throws BasicException {
        new StaticSentence(s, "DELETE FROM PRODUCTS_CAT").exec();
    }
    public void syncProductsAfter() throws BasicException {
        new StaticSentence(s, "INSERT INTO PRODUCTS_CAT (SELECT ID,NULL FROM PRODUCTS WHERE ID NOT IN (SELECT PRODUCT FROM PRODUCTS_CAT))").exec();
    }
    
    
    public void syncTaxCategory(final TaxCategoryInfo taxcat) throws BasicException {
        
        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Tax in a transaction
                
                // Try to update                
                if (new PreparedSentence(s, 
                            "UPDATE TAXCATEGORIES SET NAME = ?  WHERE ID = ? OR TRIM(regexp_replace(NAME,'\\t','')) = ?",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, taxcat.getName());
                                setString(2, taxcat.getID());  
                                setString(3, taxcat.getName());
                            }}) == 0) {
                       
                    // If not updated, try to insert
                    new PreparedSentence(s, 
                            "INSERT INTO TAXCATEGORIES(ID, NAME) VALUES (?, ?)", 
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, taxcat.getID());
                                setString(2, taxcat.getName());
                            }});
                }
                
                return null;
            }
        };
        t.execute();                   
    }
    public void syncLocations(final String location_id,final String location_name) throws BasicException {
        
        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Tax in a transaction
                
                // Try to update                
                if (new PreparedSentence(s, 
                            "UPDATE LOCATIONS SET NAME = ?  WHERE ID = ?",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, location_name);
                                setString(2, location_id);                                    
                            }}) == 0) {
                       
                    // If not updated, try to insert
                    new PreparedSentence(s, 
                            "INSERT INTO LOCATIONS(ID, NAME) VALUES (?, ?)", 
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, location_id);
                                setString(2, location_name);
                            }});
                }
                
                return null;
            }
        };
        t.execute();                   
    }
    public void syncTax(final TaxInfo tax) throws BasicException {
        
        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Tax in a transaction
                
                // Try to update                
                if (new PreparedSentence(s, 
                            "UPDATE TAXES SET NAME = ?, CATEGORY = ?, CUSTCATEGORY = ?, PARENTID = ?, RATE = ?, RATECASCADE = ? WHERE ID = ? OR TRIM(regexp_replace(NAME,'\\t','')) = ?",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, tax.getName());
                                setString(2, tax.getTaxCategoryID());
                                setString(3, tax.getTaxCustCategoryID());
                                setString(4, tax.getParentID());
                                setDouble(5, tax.getRate());
                                setBoolean(6, tax.isCascade());
                                setString(7, tax.getId()); 
                                setString(8, tax.getName());
                            }}) == 0) {
                       
                    // If not updated, try to insert
                    new PreparedSentence(s, 
                            "INSERT INTO TAXES(ID, NAME, CATEGORY, CUSTCATEGORY, PARENTID, RATE, RATECASCADE) VALUES (?, ?, ?, ?, ?, ?, ?)", 
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, tax.getId());
                                setString(2, tax.getName());
                                setString(3, tax.getTaxCategoryID());
                                setString(4, tax.getTaxCustCategoryID());
                                setString(5, tax.getParentID());                                
                                setDouble(6, tax.getRate());
                                setBoolean(7, tax.isCascade());
                            }});
                }
                
                return null;
            }
        };
        t.execute();                   
    }
    
    public void syncCategory(final CategoryInfo2 cat) throws BasicException {
        
        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Category in a transaction
                
                // Try to update
                if (new PreparedSentence(s, 
                            "UPDATE CATEGORIES SET NAME = ?, PARENTID = ? WHERE ID = ?", 
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                 setString(1, cat.getName());
                                 setString(2, cat.getParentID());
                                 setString(3, cat.getID());
                            }}) == 0) {
                       
                    // If not updated, try to insert
                    new PreparedSentence(s, 
                        //"INSERT INTO CATEGORIES(ID, NAME, IMAGE) VALUES (?, ?, ?)",
                          //"INSERT INTO CATEGORIES(ID, NAME) VALUES (?, ?)",
                            "INSERT INTO CATEGORIES(ID, NAME,PARENTID) VALUES (?, ?,?)",
                        SerializerWriteParams.INSTANCE
                        ).exec(new DataParams() { public void writeValues() throws BasicException {
                            setString(1, cat.getID());
                            setString(2, cat.getName());
                            setString(3, cat.getParentID());
                            //setBytes(3, ImageUtils.writeImage(cat.getImage()));
                        }});
                }
                return null;        
            }
        };
        t.execute();        
    }    
    
    public void syncParentCategory(final CategoryInfo2 cat) throws BasicException{
        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Parent Category in a transaction
                
                // Try to update
                if (new PreparedSentence(s, 
                            "UPDATE CATEGORIES SET NAME = ? WHERE ID = ?", 
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                 setString(1, cat.getParentName());
                                 setString(2, cat.getParentID());
                            }}) == 0) {
                       
                    // If not updated, try to insert
                    new PreparedSentence(s, 
                            "INSERT INTO CATEGORIES(ID, NAME) VALUES (?, ?)",
                        SerializerWriteParams.INSTANCE
                        ).exec(new DataParams() { public void writeValues() throws BasicException {
                            setString(1, cat.getParentID());
                            setString(2, cat.getParentName());
                        }});
                }
                return null;        
            }
        };
        t.execute();        
    }    
    
    public void syncProduct(final ProductInfoExt2 prod) throws BasicException {
        System.out.println("en syncproduct"+prod.getReference());
        Transaction t;
        t = new Transaction(s) {
    public Object transact() throws BasicException {
    // Sync the Product in a transaction
    
    // Try to update
    if (new PreparedSentence(s, 
                "UPDATE PRODUCTS SET REFERENCE = ?, CODE = ?, NAME = ?, PRICEBUY = ?, PRICESELL = ?, CATEGORY = ?, TAXCAT = ?, IMAGE = ?, STOCKVOLUME = ?, DISPLAY = ? , UPDATED = ? , ISSCALE = ?, isservice = ? WHERE ID = ? OR TRIM(regexp_replace(REFERENCE,'\\t','')) = ?",
                //"UPDATE PRODUCTS SET REFERENCE = ?, CODE = ?, NAME = ?, PRICEBUY = ?, PRICESELL = ?, CATEGORY = ?, TAXCAT = ?, STOCKVOLUME = ?, DISPLAY = ?, UPDATED = ? , ISSCALE = ? WHERE ID = ? OR TRIM(regexp_replace(REFERENCE,'\\t','')) = ?",
                SerializerWriteParams.INSTANCE
                ).exec(new DataParams() { public void writeValues() throws BasicException {
                    setString(1, prod.getReference());
                    setString(2, prod.getCode());
                    setString(3, prod.getName());
                    setDouble(4, prod.getPriceBuy());
                    setDouble(5, prod.getPriceSell());
                    setString(6, prod.getCategoryID());
                    setString(7, prod.getTaxCategoryID());
                    setBytes(8, ImageUtils.writeImage(prod.getImage()));
                    setDouble(9, prod.getStockVolume());
                    setString(10, prod.getDisplay());
                    setTimestamp(11,prod.getUpdated());
                    setBoolean(12, prod.isScale());
                    setBoolean(13,prod.isService());
                    //where
                    setString(14, prod.getID());
                    setString(15, prod.getReference());
                    
                }}) == 0) 
    {
        // If not updated, try to insert
        new PreparedSentence(s, 
                "INSERT INTO PRODUCTS (ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, CATEGORY, TAXCAT, IMAGE, STOCKCOST, STOCKVOLUME, DISPLAY, UPDATED,isservice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)",
                //"INSERT INTO PRODUCTS (ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, CATEGORY, TAXCAT, STOCKCOST, STOCKVOLUME, DISPLAY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                SerializerWriteParams.INSTANCE
                ).exec(new DataParams() { public void writeValues() throws BasicException {
                    setString(1, prod.getID());
                    setString(2, prod.getReference());
                    setString(3, prod.getCode());
                    setString(4, prod.getName());
                    setBoolean(5, prod.isCom());
                    setBoolean(6, prod.isScale());
                    setDouble(7, prod.getPriceBuy());
                    setDouble(8, prod.getPriceSell());
                    setString(9, prod.getCategoryID());
                    setString(10, prod.getTaxCategoryID());
                    setBytes(11, ImageUtils.writeImage(prod.getImage()));
                    setDouble(12, 0.0);
                    setDouble(13, prod.getStockVolume());
                    setString(14, prod.getDisplay());
                    setTimestamp(15,prod.getUpdated());
                    setBoolean(16, prod.isService());
                }});
           }
        return null;        
    }
};
        t.execute();     
    }
// red1 - TicketInfo check for DataRead.getDataField().length > 9  (R.ATTRIBUTES, inserted, c.taxid jumps over)
    public List getTickets() throws BasicException {
        return new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, C.ID, "
                        + "C.SEARCHKEY, C.NAME, C.TAXID "
                        + "FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID "
                        + "JOIN (SELECT COUNT(LINE) LINESLEFT,TICKET FROM TICKETLINES WHERE I_ORDER_ID IS NULL GROUP BY TICKET) TL ON T.ID = TL.TICKET "
                        + "LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                        + "LEFT OUTER JOIN CUSTOMERS C ON T.CUSTOMER = C.ID "
                        + "WHERE (T.TICKETTYPE = 0 OR T.TICKETTYPE = 1) AND T.STATUS = 2 AND TL.LINESLEFT > 0 "
                        + "ORDER BY T.TICKETID ASC"
                , null
                , new SerializerReadClass(TicketInfo.class)).list();
    }
    public List getTicketsFiscal() throws BasicException {
        return new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, C.ID, "
                        + "C.SEARCHKEY, C.NAME, C.TAXID, T.FISCALPRINT_SERIAL, T.FISCAL_INVOICENUMBER, T. FISCAL_ZREPORT "
                        + "FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID "
                        + "LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                        + "LEFT OUTER JOIN CUSTOMERS C ON T.CUSTOMER = C.ID WHERE (T.TICKETTYPE = 0 OR T.TICKETTYPE = 1) AND T.STATUS = 2 "
                        + "ORDER BY T.TICKETID ASC"
                , null
                , new SerializerReadClass(TicketInfo.class)).list();
    }
    /**Check if there is a process sending tickets by cheking database status in tickets. 
     * 0 not sync, 1 sync, 2 sync in progress
     * @return List of tickets in process of sync
     * @throws BasicException 
     */
    public List getTicketsSync() throws BasicException {
        return new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, C.ID, "
                        + "C.SEARCHKEY, C.NAME, C.TAXID  "
                        + "FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID "
                        + "JOIN (SELECT COUNT(LINE) LINESLEFT,TICKET FROM TICKETLINES WHERE I_ORDER_ID IS NULL GROUP BY TICKET) TL ON T.ID = TL.TICKET "
                        + "LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                        + "LEFT OUTER JOIN CUSTOMERS C ON T.CUSTOMER = C.ID "
                        + "WHERE (T.TICKETTYPE = 0 OR T.TICKETTYPE = 1) AND T.STATUS = 2 AND TL.LINESLEFT > 0"
                , null
                , new SerializerReadClass(TicketInfo.class)).list();
    }
    
    public List getTicketsSync(String hostname) throws BasicException {
        //new StaticSentence(s, "update tickets set status=0 where id in (select ticket from ticketlines where i_order_id is null) ").exec();
        return new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, C.ID,T.STATUS,C.SEARCHKEY, C.NAME, C.TAXID "
                        + "FROM TICKETLINES TL "
                        + "INNER JOIN RECEIPTS R ON TL.TICKET = R.ID "
                        + "INNER JOIN TICKETS T ON R.ID = T.ID "
                       
                        + "LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                        + "LEFT OUTER JOIN CUSTOMERS C ON T.CUSTOMER = C.ID "
                        
                        + "WHERE TL.I_ORDER_ID IS NULL AND (T.TICKETTYPE = 0 OR T.TICKETTYPE = 1) AND T.STATUS = 2  AND (hostsync = '"+hostname+"' OR hostsync IS NULL) AND fiscalnumber IS NOT NULL "
                        + "GROUP BY T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, C.ID, C.SEARCHKEY, C.NAME, C.TAXID "      
                        + "ORDER BY T.TICKETID ASC"
                , null
                , new SerializerReadClass(TicketInfo.class)).list();
    }
        
    public void resetTicketsSync(String hostname) throws BasicException{
        
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 0, hostsync=null WHERE STATUS = 2 AND (hostsync = '"+hostname+"' OR hostsync IS NULL) AND fiscalnumber IS NOT NULL ").exec();
        
    }
    
    
    public List getTicketsSyncFiscal() throws BasicException {
        return new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, C.ID, T.STATUS, "
                        + "C.SEARCHKEY, C.NAME, C.TAXID, T.FISCALPRINT_SERIAL, T.FISCAL_INVOICENUMBER, T. FISCAL_ZREPORT "
                        + "FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID "
                        + "JOIN (SELECT COUNT(LINE) LINESLEFT,TICKET FROM TICKETLINES WHERE I_ORDER_ID IS NULL GROUP BY TICKET) TL ON T.ID = TL.TICKET "
                        + "LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                        + "LEFT OUTER JOIN CUSTOMERS C ON T.CUSTOMER = C.ID WHERE (T.TICKETTYPE = 0 OR T.TICKETTYPE = 1) AND T.STATUS = 2 AND TL.LINESLEFT > 0"
                , null
                , new SerializerReadClass(TicketInfo.class)).list();
    }
    public List getTicketLines(final String ticket) throws BasicException {
        return new PreparedSentence(s
                //, "SELECT L.TICKET, L.LINE, L.PRODUCT, L.ATTRIBUTESETINSTANCE_ID, L.UNITS, L.PRICE, T.ID, T.NAME, T.CATEGORY, T.VALIDFROM, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES " +
                , "SELECT L.TICKET, L.LINE, L.PRODUCT, L.ATTRIBUTESETINSTANCE_ID, L.UNITS, L.PRICE, T.ID, T.NAME, T.CATEGORY, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES " +
                "FROM TICKETLINES L, TAXES T WHERE L.TAXID = T.ID AND L.TICKET = ? ORDER BY L.LINE"
//  red1       , "SELECT L.TICKET, L.LINE, L.PRODUCT, L.UNITS, L.PRICE, T.ID, T.NAME, T.CATEGORY, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES " +
//  red1         "FROM TICKETLINES L, TAXES T WHERE L.TAXID = T.ID AND L.TICKET = ?"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(TicketLineInfo.class)).list(ticket);
    }
    public List getTicketPayments(final String ticket) throws BasicException {
        return new PreparedSentence(s
                , "SELECT TOTAL, PAYMENT FROM PAYMENTS WHERE RECEIPT = ?"
                , SerializerWriteString.INSTANCE
                , new SerializerRead() {
                    public Object readValues(DataRead dr) throws BasicException {
                        return new PaymentInfoTicket(
                                dr.getDouble(1),
                                dr.getString(2));
                    }                
                }).list(ticket);
    }    

    public CustomerInfoExt getTicketCustomer(final String ticket) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s
                , "SELECT C.ID, C.TAXID, C.SEARCHKEY, C.NAME, C.CARD, C.TAXCATEGORY, C.NOTES, C.MAXDEBT, C.VISIBLE, C.CURDATE, C.CURDEBT" +
                  ", C.FIRSTNAME, C.LASTNAME, C.EMAIL, C.PHONE, C.PHONE2, C.FAX" +
                  ", C.ADDRESS, C.ADDRESS2, C.POSTAL, C.CITY, C.REGION, C.COUNTRY" +
                " FROM CUSTOMERS C INNER JOIN TICKETS T ON C.ID = T.CUSTOMER WHERE T.ID = ?"
                , SerializerWriteString.INSTANCE
                , CustomerInfoExt.getSerializerRead()
                ).find(ticket);
    }    

    public TaxCategoryInfo getTaxCategoryInfoByName(final String name) throws BasicException {
        return (TaxCategoryInfo) new PreparedSentence(s
                , "SELECT ID, NAME FROM TAXCATEGORIES WHERE NAME = ?"
                , SerializerWriteString.INSTANCE
                , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                     return new TaxCategoryInfo(
                        dr.getString(1), 
                        dr.getString(2));
            }}).find(name);
    }
    public final CategoryInfo getCategoryInfoByName(final String name) throws BasicException {
        return (CategoryInfo) new PreparedSentence(s
        , "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME "
                + "FROM CATEGORIES "
                + "WHERE NAME = ? "
                + "ORDER BY NAME"
        , SerializerWriteString.INSTANCE
        , CategoryInfo.getSerializerRead()).find(name);
    }
    public void checkTickets() throws BasicException {
        
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 0 WHERE id in (select ticket from ticketlines where i_order_id is null) and status !=2").exec();
        
    }
    public void checkTicketsFiscalNumber() throws BasicException {
        new StaticSentence(s,"update tickets set fiscalnumber=ticketid where fiscalnumber is null and id not in "
                + "(select ticket from ticketlines where product in (select id from products where isvprice=true))").exec();
        
        
    }
    public void execTicketUpdate() throws BasicException {
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 1 WHERE STATUS = 2").exec();
    }
    public void execTicketUpdate(String ticketid, String STATUS) throws BasicException {
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = " + STATUS + " WHERE STATUS = 2 AND ID='"+ticketid+"'").exec();
    }
    public void execTicketUpdateError() throws BasicException {
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 0 WHERE STATUS = 2").exec();
    }
    public void execClosedCashUpdate() throws BasicException {
        new StaticSentence(s, "UPDATE CLOSEDCASH SET STATUS = 1 WHERE STATUS = 2 AND DATEEND IS NOT NULL ").exec();
    }
    public void execClosedCashUpdateError() throws BasicException {
        new StaticSentence(s, "UPDATE CLOSEDCASH SET STATUS = 0 WHERE STATUS = 2 AND DATEEND IS NOT NULL").exec();
    }
    public void execGiftCardUpdate() throws BasicException {
        new StaticSentence(s, "UPDATE GIFTCARD SET STATUS = 1 WHERE STATUS = 2 ").exec();
    }
    public void setTicketsInProcess() throws BasicException {
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 2 WHERE STATUS = 0  ").exec();
    }
    public void setTicketsInProcess(final String hostname) throws BasicException {
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 2, hostsync = '"+hostname+"' WHERE STATUS = 0 AND fiscalnumber IS NOT NULL").exec();
    }
    public void setTicketsInProcessFiscal() throws BasicException {
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 2 WHERE STATUS = 0 AND FISCAL_INVOICENUMBER IS NOT NULL ").exec();
    }
    public void resendTickets() throws BasicException {
        new StaticSentence(s, "UPDATE TICKETS SET STATUS = 0 WHERE ID IN (SELECT ID FROM RECEIPTS WHERE DATENEW BETWEEN date_trunc('day', now()) AND now()) AND FISCAL_INVOICENUMBER IS NOT NULL ").exec();
    }
    public void resendTickets(final Timestamp OrdersSyncDateFrom,final Timestamp OrdersSyncDateTo) throws BasicException {
        new PreparedSentence(s, "UPDATE TICKETS SET STATUS = 0 WHERE ID IN (SELECT ID FROM RECEIPTS WHERE DATENEW BETWEEN ? AND ?) AND FISCAL_INVOICENUMBER IS NOT NULL ", SerializerWriteParams.INSTANCE)
                .exec(new DataParams() { public void writeValues() throws BasicException {
                    setTimestamp(1, OrdersSyncDateFrom);
                    setTimestamp(2, OrdersSyncDateTo);
                    
                }});
        //new StaticSentence(s, ).exec();
    }
    public void setClosedCashInProcess() throws BasicException {
        new StaticSentence(s, "UPDATE CLOSEDCASH SET STATUS = 2 WHERE STATUS = 0 AND DATEEND IS NOT NULL").exec();
    }

    public List getClosedCash() throws BasicException {
    return new PreparedSentence(s
            , "SELECT MONEY, HOSTSEQUENCE, DATESTART, DATEEND, DIFFERENCE, PERSON FROM CLOSEDCASH WHERE STATUS = 2 AND DATEEND IS NOT NULL"
            , null
            , new SerializerRead() {
                public Object readValues(DataRead dr) throws BasicException {
                    return new ClosedCashInfo(
                            dr.getString(1),
                            dr.getString(1),
                            dr.getInt(2),
                            dr.getTimestamp(3),
                            dr.getTimestamp(4),
                            dr.getDouble(5),
                            dr.getString(6));
                }                
            }).list();
    }
    public List getClosedCashSync() throws BasicException {
    return new PreparedSentence(s
            , "SELECT MONEY, HOSTSEQUENCE, DATESTART, DATEEND, DIFFERENCE, PERSON FROM CLOSEDCASH WHERE STATUS = 2 AND DATEEND IS NOT NULL"
            , null
            , new SerializerRead() {
                public Object readValues(DataRead dr) throws BasicException {
                    return new ClosedCashInfo(
                            dr.getString(1),
                            dr.getString(1),
                            dr.getInt(2),
                            dr.getTimestamp(3),
                            dr.getTimestamp(4),
                            dr.getDouble(5),
                            dr.getString(6));
                }                
            }).list();
    }
    
        public List getCreditNote(String receiptId) throws BasicException {
        return new PreparedSentence(s
                ,   "SELECT A.NUMBER, A.ID, A.RECEIPT, A.TOTAL, A.AVALIABLE, A.DATENEW, A.BRANCH " +
                    "FROM CREDITNOTE A " +
                    "WHERE A.RECEIPT = ? "
            , SerializerWriteString.INSTANCE
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                     return new CreditNoteInfo(
                        dr.getInt(1), 
                        dr.getString(2),
                        dr.getString(3),
                        dr.getDouble(4), 
                        dr.getDouble(5),
                        dr.getTimestamp(6),
                        dr.getString(7));
            }}).list(receiptId);
        
    }
        public final ProductInfoExt getProductInfo(String id) throws BasicException {
                        System.out.println("product info ");

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
                + "FROM PRODUCTS LEFT JOIN STOCKCURRENT ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) "
                + "WHERE ID = ? "
                + "GROUP BY ID, REFERENCE, NAME,STOCKCURRENT.UNITS;"
		, SerializerWriteString.INSTANCE
		, ProductInfoExt.getSerializerRead()).find(id);
    }
        public final ProductInfoExt getProductInfoByReference(String sReference) throws BasicException {
            //System.out.println("product info by reference");
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
                + "FROM PRODUCTS LEFT JOIN STOCKCURRENT ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) "
                + "WHERE REFERENCE = ? "
                + "GROUP BY ID, REFERENCE, NAME,STOCKCURRENT.UNITS;"
		, SerializerWriteString.INSTANCE
		, ProductInfoExt.getSerializerRead()).find(sReference);
    }
        
        public final Date findLastUpdated(String table){            
            SentenceFind m_updated;
            SerializerRead updateread;
            updateread =new SerializerRead() {
                @Override
                public Date readValues(DataRead dr) throws BasicException {
                    return (                       
                            dr.getTimestamp(1)
                           );                
                }};            
 	m_updated = new PreparedSentence(s
		, "select max(updated)::varchar from "+table
		, null
		, updateread
                );            
        Date lastUpdate=null;
        try {
            lastUpdate =(Date)m_updated.find();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicIntegration.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Ultima actualizacion de :"+table+" " + lastUpdate);
        /*try{
            SQL = "select max(updated) from "+table;
            conInt=s.getConnection();
            stmt = (Statement) conInt.createStatement();  
            rs = stmt.executeQuery(SQL);
            
            while (rs.next()){
                lastUpdate = rs.getTimestamp(1);
            }   
            conInt.close();
            s.close();
        }catch(SQLException e){
            
        }*/
        return lastUpdate;
    }              

    public final Integer CheckCashReceipts(String Id) throws BasicException {
        if (Id == null) {
            return null; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM RECEIPTS WHERE money = ?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.INT})).find(Id);
            return record == null ? 0 : (Integer)record[0];
        }
    }    
    
    public final void updateCashStartDate(final String money) throws BasicException {
        if (money == null) {
            return; 
        } else {
                new PreparedSentence(s,
                            "UPDATE CLOSEDCASH SET DATESTART = ? WHERE MONEY = ? ",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                                setString(2, money);
                            }});            
        }        
    }
    
    public List getMovementsSync() throws BasicException {
        return new PreparedSentence(s
                , "SELECT S.ID, S.REASON, S.DATENEW, S.LOCATION, S.PRODUCT, S.UNITS, S.PRICE, P.ID, P.NAME "
                        + "FROM STOCKDIARY S "
                        + "LEFT OUTER JOIN PEOPLE P ON S.APPUSER = P.ID "
                        + "WHERE S.REASON = 4 AND S.STATUS = 2 AND S.APPUSER != '' AND I_Movement_ID IS NULL "
                , null
                , new SerializerReadClass(MaterialProdInfoExt.class)).list();
    }    
    
    public List getMovementsSync(String hostname) throws BasicException {
        return new PreparedSentence(s
                , "SELECT S.ID, S.REASON, S.DATENEW, S.LOCATION, S.PRODUCT, S.UNITS, S.PRICE, P.ID, P.NAME "
                        + "FROM STOCKDIARY S "
                        + "LEFT OUTER JOIN PEOPLE P ON S.APPUSER = P.ID "
                        + "WHERE S.REASON = 4 AND S.STATUS = 2 AND S.APPUSER != '' AND I_Movement_ID IS NULL "
                        + "AND (hostsync = '"+hostname+"' OR hostsync IS NULL)"
                , null
                , new SerializerReadClass(MaterialProdInfoExt.class)).list();
    }       
    
    public void resetMovementsSync(String hostname) throws BasicException{
        new StaticSentence(s, "UPDATE STOCKDIARY SET STATUS = 0, hostsync=null WHERE STATUS = 2 AND REASON = 4 AND APPUSER != '' AND (hostsync = '"+hostname+"' OR hostsync IS NULL) ").exec();
    }    
    
    public void setMovementsInProcess() throws BasicException {
        new StaticSentence(s, "UPDATE STOCKDIARY SET STATUS = 2 WHERE STATUS = 0 AND REASON = 4 AND APPUSER != ''").exec();
    }    

    public void setMovementsInProcess(String hostname) throws BasicException {
        new StaticSentence(s, "UPDATE STOCKDIARY SET STATUS = 2,hostsync = '"+hostname+"' WHERE STATUS = 0 AND REASON = 4 AND APPUSER != ''").exec();
    } 
    
    public List getMovements() throws BasicException {
        return new PreparedSentence(s
                , "SELECT S.ID, S.REASON, S.DATENEW, S.LOCATION, S.PRODUCT, S.UNITS, S.PRICE, P.ID, P.NAME "
                        + "FROM STOCKDIARY S "
                        + "LEFT OUTER JOIN PEOPLE P ON S.APPUSER = P.ID "
                        + "WHERE S.REASON = 4 AND S.STATUS = 2 AND S.APPUSER != '' AND I_Movement_ID IS NULL "
                , null
                , new SerializerReadClass(MaterialProdInfoExt.class)).list();
    }
    
    public void execMovementUpdate() throws BasicException {
        new StaticSentence(s, "UPDATE STOCKDIARY SET STATUS = 1 WHERE REASON = 4 AND STATUS = 2 AND APPUSER != ''").exec();
    }
    public void execMovementUpdate(String movementid, String STATUS) throws BasicException {
        new StaticSentence(s, "UPDATE STOCKDIARY SET STATUS = " + STATUS + " WHERE STATUS = 2 AND ID='"+movementid+"'").exec();
    }    
    public void execMovementUpdateValid() throws BasicException {
        new StaticSentence(s, "UPDATE STOCKDIARY SET STATUS = 1 WHERE REASON = 4 AND STATUS = 2 AND APPUSER != '' AND I_Movement_ID is not NULL ").exec();
    }
    public void execMovementUpdateFailed() throws BasicException {
        new StaticSentence(s, "UPDATE STOCKDIARY SET STATUS = 0 WHERE REASON = 4 AND STATUS = 2 AND APPUSER != '' AND I_Movement_ID is not NULL ").exec();
    }    
    public void execTicketLineUpdate(String ticket, int line, int recordid) throws BasicException {
        new StaticSentence(s, "UPDATE TICKETLINES SET I_Order_ID = " + recordid + " WHERE TICKET = '"+ ticket+"' AND line = "+line).exec();
    }    
    public void execStockDiaryUpdate(String id,int recordid) throws BasicException {
        new StaticSentence(s, "UPDATE STOCKDIARY SET I_Movement_ID = " + recordid + " WHERE ID = '"+id+"'").exec();
    }
    
    public int countExportedLines (String ticketid) throws BasicException{        
        if (ticketid == null) {
            return 0; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM TICKETLINES WHERE TICKET = ? AND I_Order_ID IS NOT NULL"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.INT})).find(ticketid);
            return record == null ? 0 : (Integer)record[0];
        }
    } 
    
    public int findI_Order_ID (String ticketid, int line) throws BasicException{        
        if (ticketid == null) {
            return 0; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT COALESCE(I_Order_ID,0) FROM TICKETLINES WHERE TICKET = '"+ticketid+"' AND line = "+line
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.INT})).find(ticketid);
            return record == null ? 0 : (Integer)record[0];
        }        
    }
    
    public int checkGiftCard (JPaymentGiftCardPanel giftcard) throws BasicException{        
        if (giftcard.getSerial().isEmpty() || giftcard.getSerial() == null || giftcard.getSerial().equals("")) {
            return 0; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM GIFTCARD WHERE SERIAL = '"+giftcard.getSerial()+"' AND GIFTCARD_ID = "+giftcard.getGiftCardID()
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.INT})).find(giftcard.getSerial());
            return record == null ? 0 : (Integer)record[0];
        }        
    }
    
    public int checkHostGiftCard (JPaymentGiftCardPanel giftcard, String hostname) throws BasicException{        
        if (giftcard.getSerial().isEmpty() || giftcard.getSerial() == null || giftcard.getSerial().equals("")) {
            return 0; 
        } else {
            Object[]record = (Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM GIFTCARD WHERE AMOUNT <0 AND HOSTSYNC = '"+hostname+"' AND SERIAL = '"+giftcard.getSerial()+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.INT})).find(giftcard.getSerial());
            return record == null ? 0 : (Integer)record[0];
        }        
    }
    
    public void syncGiftCard(final JPaymentGiftCardPanel giftcard) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {
                // Sync the Giftcard in a transaction
                if (new PreparedSentence(s,
                            "UPDATE GIFTCARD SET GIFTCARD_ID = ?, UPDATED = ? WHERE SERIAL = ? AND AMOUNT = ? ",
                            //"UPDATE GIFTCARD SET AMOUNT = ?, UPDATED = ? WHERE GIFTCARD_ID = ?",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                /*setDouble(1, giftcard.getAmount());
                                setTimestamp(2, giftcard.getUpdated());
                                setInt(3, giftcard.getGiftCardID());*/
                                setInt(1, giftcard.getGiftCardID());
                                setTimestamp(2, giftcard.getUpdated());
                                setString(3, giftcard.getSerial());
                                setDouble(4, giftcard.getAmount());
                            }}) == 0) {
                    new PreparedSentence(s,
                            "INSERT INTO GIFTCARD(SERIAL, AMOUNT, STATUS, GIFTCARD_ID, UPDATED) VALUES (?, ?, ?, ?, ?)",
                            SerializerWriteParams.INSTANCE
                            ).exec(new DataParams() { public void writeValues() throws BasicException {
                                setString(1, giftcard.getSerial());
                                setDouble(2, giftcard.getAmount());
                                setInt(3, 1);
                                setInt(4, giftcard.getGiftCardID());
                                setTimestamp(5, giftcard.getUpdated());
                            }});
                }
                return null;
            }
        };
        t.execute();
    }
    
    public List getGiftCardSync(String hostname) throws BasicException {
    return new PreparedSentence(s
            , "SELECT ID, SERIAL, AMOUNT, STATUS FROM GIFTCARD WHERE (hostsync = '"+hostname+"' OR hostsync IS NULL) AND STATUS = 2"
            , null
            , new SerializerRead() {
                @Override
                public Object readValues(DataRead dr) throws BasicException {
                    return new JPaymentGiftCardPanel(
                            dr.getInt(1),
                            dr.getString(2),
                            dr.getDouble(3),
                            dr.getInt(4));
                }                
            }).list();
    }
    
    public List getGiftCard() throws BasicException {
    return new PreparedSentence(s
            , "SELECT ID, SERIAL, AMOUNT, STATUS FROM GIFTCARD WHERE STATUS=0"
            , null
            , new SerializerRead() {
                @Override
                public Object readValues(DataRead dr) throws BasicException {
                    return new JPaymentGiftCardPanel(
                            dr.getInt(1),
                            dr.getString(2),
                            dr.getDouble(3),
                            dr.getInt(4));
                }                
            }).list();
    }
    
    public void resetGiftCardSync(String hostname) throws BasicException{
        new StaticSentence(s, "UPDATE GIFTCARD SET STATUS = 0, hostsync=null WHERE STATUS = 2 AND (hostsync = '"+hostname+"' OR hostsync IS NULL) ").exec();
    }
    
    public void setGiftCardInProcess(final String hostname) throws BasicException {
        new StaticSentence(s, "UPDATE GIFTCARD SET STATUS = 2, hostsync = '"+hostname+"' WHERE STATUS = 0 ").exec();
    }
    
    public void execGiftCardUpdate(Integer giftcardid, String STATUS) throws BasicException {
        new StaticSentence(s, "UPDATE GIFTCARD SET STATUS = " + STATUS + " WHERE STATUS = 2 AND id="+giftcardid).exec();
    }
    public final double findProductStock(String warehouse, String id, String attsetinstid) throws BasicException {

        PreparedSentence p = attsetinstid == null
                ? new PreparedSentence(s, "SELECT UNITS FROM stockcurrent "
                        + "WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL"
                    , new SerializerWriteBasic(Datas.STRING, Datas.STRING)
                    , SerializerReadDouble.INSTANCE)
                : new PreparedSentence(s, "SELECT UNITS FROM stockcurrent "
                        + "WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?"
                    , new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING)
                    , SerializerReadDouble.INSTANCE);

        Double d = (Double) p.find(warehouse, id, attsetinstid);
        return d == null ? 0.0 : d;
    }
    public final List<ProductsBundleInfo> getProductsBundle(String productId) throws BasicException {
        return new PreparedSentence(s
            , "SELECT "
                + "ID, "
                + "PRODUCT, "
                + "PRODUCT_BUNDLE, "
                + "QUANTITY "
                + "FROM products_bundle WHERE PRODUCT = ?"
            , SerializerWriteString.INSTANCE
            , ProductsBundleInfo.getSerializerRead()).list(productId);
    }
    private void adjustStock(Object params) throws BasicException {

        List<ProductsBundleInfo> bundle = getProductsBundle((String) ((Object[])params)[0]);

        if (bundle.size() > 0) {
//            int as=0;

            for (ProductsBundleInfo component : bundle) {
                Object[] adjustParams = new Object[4];
                adjustParams[0] = component.getProductBundleId();
                adjustParams[1] = ((Object[])params)[1];                
                adjustParams[2] = ((Object[])params)[2];
                adjustParams[3] = ((Double)((Object[])params)[3]) * component.getQuantity();
                adjustStock(adjustParams);
            }
//            return as;
        } else {

            int updateresult = ((Object[]) params)[2] == null
                ? new PreparedSentence(s
                    , "UPDATE stockcurrent SET UNITS = (UNITS + ?) "
                           + "WHERE LOCATION = ? AND PRODUCT = ? "
                           + "AND ATTRIBUTESETINSTANCE_ID IS NULL"
                    , new SerializerWriteBasicExt(stockAdjustDatas
                           , new int[] {3, 1, 0}))
                        .exec(params)
                : new PreparedSentence(s
                    , "UPDATE stockcurrent SET UNITS = (UNITS + ?) "
                           + "WHERE LOCATION = ? AND PRODUCT = ? "
                           + "AND ATTRIBUTESETINSTANCE_ID = ?"
                    , new SerializerWriteBasicExt(stockAdjustDatas
                           , new int[] {3, 1, 0, 2}))
                        .exec(params);

            if (updateresult == 0) {

                new PreparedSentence(s
                    , "INSERT INTO stockcurrent (LOCATION, PRODUCT, "
                            + "ATTRIBUTESETINSTANCE_ID, UNITS) "
                            + "VALUES (?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(stockAdjustDatas
                            , new int[] {1, 0, 2, 3}))
                        .exec(params);
            }
//            return 1;
        }
    }
    /**
     * ProductBundle version
     * @return
     */
    public final SentenceExec getStockDiaryInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            /**
             * @param params[0] String     STOCKDIARY.ID
             * @param params[1] Date       Timestamp
             * @param params[2] Integer    Reason
             * @param params[3] String     Location
             * @param params[4] String     Product ID
             * @param params[5] String     Attribute instance ID
             * @param params[6] Double     Units
             * @param params[7] Double     Price
             * @param params[8] String     Application User
             */            
            public int execInTransaction(Object params) throws BasicException {

                Object[] adjustParams = new Object[4];
                Object[] paramsArray = (Object[]) params;
                adjustParams[0] = paramsArray[4];                               //product ->Location
                adjustParams[1] = paramsArray[3];                               //location -> Product
                adjustParams[2] = paramsArray[5];                               //attributesetinstance
                adjustParams[3] = paramsArray[6];                               //units
                adjustStock(adjustParams);
                
                return new PreparedSentence(s
                    , "INSERT INTO stockdiary (ID, DATENEW, REASON, LOCATION, "
                            + "PRODUCT, ATTRIBUTESETINSTANCE_ID, "
                            + "UNITS, PRICE, AppUser) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(stockdiaryDatas
                            , new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}))
                        .exec(params);
            }
        };
    }    

    @Override
    public void init(Session s) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
