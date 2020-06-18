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
import com.openbravo.pos.payment.PaymentInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums.DocAction;
import org.idempiere.webservice.client.base.Enums.WebServiceResponseStatus;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.base.ParamValues;
import org.idempiere.webservice.client.base.WebServiceRequest;
import org.idempiere.webservice.client.base.WebServiceResponse;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.exceptions.XMLWriteException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.CreateUpdateDataRequest;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.request.RunProcessRequest;
import org.idempiere.webservice.client.request.SetDocActionRequest;
import org.idempiere.webservice.client.response.CompositeResponse;
import org.idempiere.webservice.client.response.RunProcessResponse;
import org.idempiere.webservice.client.response.StandardResponse;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

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

    final String UCTenderType_CASH = "cash";
    final String UCTenderType_CREDITCARD = "magcard";
    final String UCTenderType_EFT = "bank";
    final String UCTenderType_CREDIT = "debt";

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
        /* try {
            dlintegration.checkTickets();
            if (erpProperties.getProperty("SentAllOrders").equals("Y")) {
                dlintegration.checkTicketsFiscalNumber();
            }

        } catch (BasicException e) {

        }*/
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

        //List<TicketInfo> ticketlistSync = dlintegration.getTicketsSync(hostname);
        //if (ticketlistSync.size() > 0) {
        //    dlintegration.resetTicketsSync(hostname);
        //return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.sendingorders"));
        //}
        //if there is no tickets in process update the list of tickets not sync
        //dlintegration.setTicketsInProcess(hostname);
        List<TicketInfo> ticketlist = dlintegration.getTicketsToSync(hostname);

        if (ticketlist.isEmpty()) {
            //dlintegration.execTicketUpdate();
            return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.zeroorders"));
        } else {
            for (TicketInfo ticket : ticketlist) {
                ticket.setLines(dlintegration.getTicketLines(ticket.getId()));
                ticket.setPayments(dlintegration.getTicketPayments(ticket.getId()));
            }
            if (createWsOrder(ticketlist, orders)) {
                return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.syncordersok"), AppLocal.getIntString("message.syncordersinfo") + ticketlist.size());
            } else {
                return new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.syncorderserror"), AppLocal.getIntString("message.syncordersinfo") + ticketlist.size());
            }
        }
    }

    private boolean createWsOrder(List<TicketInfo> ticketlist, SyncOrder orders) {

        boolean isOrdersSentOk = true;

        System.out.println("\n" + new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.qtyorders_sync")).getMessageMsg()
                + ticketlist.size() + "\n");

        Iterator iterator = ticketlist.iterator();
        while (iterator.hasNext()) {
            TicketInfo ticket = (TicketInfo) iterator.next();
            // Create Composite WS
            CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
            compositeOperation.setWebServiceType(orders.getwsCompositeOrderType());

            // Set Login
            compositeOperation.setLogin(orders.getLogin());

            buildOrder(compositeOperation, ticket);

            createWsOrderlines(compositeOperation, ticket);

            List<PaymentInfo> payments = ticket.getPayments();
            if (!payments.get(0).getName().equals(UCTenderType_CREDIT)) {
                createWsMixedPayment(compositeOperation, ticket);
            }

            completeOrder(compositeOperation);

            CompositeResponse response = sendRequest(compositeOperation);

            if (response.getStatus() == WebServiceResponseStatus.Error) {
                isOrdersSentOk = false;
            }

            updateTicketSyncStatus(response, ticket);
        }
        return isOrdersSentOk;
    }

    private void updateTicketSyncStatus(CompositeResponse response, TicketInfo ticket) {
        try {
            if (response.getStatus() == WebServiceResponseStatus.Successful) {
                System.out.println("\n" + "*************Order Imported: "
                        + ticket.getTicketId() + "*************" + "\n");
                dlintegration.execTicketUpdate(ticket.getId(), "1");

            } else {
                System.out.println("\n" + "*************Order Not Imported: "
                        + ticket.getTicketId() + "*************" + "\n");
                dlintegration.execTicketUpdate(ticket.getId(), "0");
            }
        } catch (BasicException ex) {
            Logger.getLogger(SyncOrderThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CompositeResponse sendRequest(CompositeOperationRequest compositeOperation) {
        // CREATE CLIENT
        WebServiceConnection client = orders.getClient();
        CompositeResponse response = null;
        try {
            // SEND REQUEST
            response = client.sendRequest(compositeOperation);

            // GET RESPONSE
            if (response.getStatus() == WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else {
                for (int i = 0; i < response.getResponsesCount(); i++) {
                    if (response.getResponse(i).getStatus() == WebServiceResponseStatus.Error) {
                        System.out.println(response.getResponse(i).getErrorMessage());
                    } else {
                        System.out.println(response.getResponse(i).getWebServiceResponseModel());
                    }
                }
            }

            client.writeRequest(System.out);

            System.out.println();
            client.writeResponse(System.out);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public int sendWsQueryRequest(QueryDataRequest wsRequest) {
        // CREATE CLIENT
        WebServiceConnection client = orders.getClient();
        WindowTabDataResponse response = null;
        try {
            // SEND REQUEST
            response = client.sendRequest(wsRequest);

            // GET RESPONSE
            if (response.getStatus() == WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            }

            client.writeRequest(System.out);

            System.out.println();
            client.writeResponse(System.out);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (!((response.getNumRows() == 0) || (response.getStatus() == WebServiceResponseStatus.Error)))
                ? (response.getDataSet().getRow(0).getFields().get(0).getIntValue()) : 0;
    }

    private void createWsOrderlines(CompositeOperationRequest compositeOperation, TicketInfo ticket) {
        for (TicketLineInfo line : ticket.getLines()) {
            // Create WS for Order Line
            CreateDataRequest createOrderLine = new CreateDataRequest();
            createOrderLine.setWebServiceType("nTierCreateOrderLine");
            DataRow dataOrderLine = new DataRow();
            dataOrderLine.addField("AD_Client_ID", "@C_Order.AD_Client_ID");
            dataOrderLine.addField("AD_Org_ID", "@C_Order.AD_Org_ID");
            dataOrderLine.addField("C_Order_ID", "@C_Order.C_Order_ID");

            ProductInfoExt productinfo = null;
            try {
                productinfo = dlintegration.getProductInfo(line.getProductID());
            } catch (BasicException ex) {
                Logger.getLogger(SyncOrderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Lookup product via name
            Field field = new Field("M_Product_ID");
            field.setLval(productinfo.getName());
            dataOrderLine.addField(field);

            dataOrderLine.addField("QtyOrdered", Double.toString(Math.abs(line.getMultiply())));
            dataOrderLine.addField("QtyEntered", Double.toString(Math.abs(line.getMultiply())));
            dataOrderLine.addField("Line", String.valueOf((Math.abs(line.getTicketLine()) + 1) * 10));
            dataOrderLine.addField("PriceEntered", Double.toString(round(line.getPrice(), 2)));
            dataOrderLine.addField("PriceActual", Double.toString(round(line.getPrice(), 2)));
            dataOrderLine.addField("C_Tax_ID", line.getTaxInfo().getId());

            createOrderLine.setDataRow(dataOrderLine);
            compositeOperation.addOperation(createOrderLine);
        }
    }

    private void buildOrder(CompositeOperationRequest compositeOperation, TicketInfo ticket) {
        // Create WS for Order
        CreateDataRequest createOrder = new CreateDataRequest();
        createOrder.setWebServiceType(orders.getWsOrderType());
        DataRow data = new DataRow();
        Calendar datenew = Calendar.getInstance();
        datenew.setTime(ticket.getDate());

        if (ticket.getCustomerId() != null) {
            try {
                ticket.setCustomer(dlintegration.getTicketCustomer(ticket.getId()));
            } catch (BasicException ex) {
                Logger.getLogger(SyncOrderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("\n No Customer ID attached against Ticket\n");
            System.exit(0);
        }

        int RecordId = queryBpRecordId(ticket);
        if (RecordId != 0) {
            data.addField("C_BPartner_ID", RecordId);
        } else {
            createBP(compositeOperation, ticket);
            data.addField("C_BPartner_ID", "@C_BPartner.C_BPartner_ID");
        }

        if (ticket.getTicketType() == 0) {

            data.addField("C_DocTypeTarget_ID", orders.getC_DocType_ID()); //regular order
        } else {

            data.addField("C_DocTypeTarget_ID", orders.getC_DocTypeRefund_ID()); //return
        }
        data.addField("AD_Client_ID", orders.getAD_Client_ID());
        data.addField("AD_Org_ID", orders.getAD_Org_ID());

        data.addField("M_Warehouse_ID", orders.getM_Warehouse_ID());

        data.addField("DocumentNo", Integer.toString(ticket.getTicketId()));
        data.addField("DateOrdered", new java.sql.Timestamp(datenew.getTime().getTime()).toString());
        data.addField("SalesRep_ID", Integer.valueOf(ticket.getUser().getId()));

        List<PaymentInfo> payments = ticket.getPayments();
        data.addField("PaymentRule", (payments.get(0).getName().equals(UCTenderType_CREDIT)) ? "P" : "M");
        data.addField("C_BPartner_Location_ID", "@C_BPartner_Location.C_BPartner_Location_ID");

        createOrder.setDataRow(data);
        compositeOperation.addOperation(createOrder);
    }

    private void createBP(CompositeOperationRequest compositeOperation, TicketInfo ticket) {
        CreateUpdateDataRequest createBP = new CreateUpdateDataRequest();
        createBP.setWebServiceType("nTierCreateBP");
        DataRow data = new DataRow();

        data.addField("Name", ticket.getCustomer().getName());
        data.addField("TaxID", "12345");
        data.addField("IsCustomer", "Y");
        data.addField("IsVendor", "N");

        createBP.setDataRow(data);
        compositeOperation.addOperation(createBP);

        createLocation(compositeOperation, ticket);
    }

    private void createBPLocation(CompositeOperationRequest compositeOperation, TicketInfo ticket) {
        CreateUpdateDataRequest createBPLocation = new CreateUpdateDataRequest();
        createBPLocation.setWebServiceType("nTierCreateBPLocation");
        DataRow data = new DataRow();

        data.addField("C_BPartner_ID", "@C_BPartner.C_BPartner_ID");
        data.addField("C_Location_ID", "@C_Location.C_Location_ID");
        data.addField("IsShipTo", "True");
        data.addField("IsBillTo", "True");

        createBPLocation.setDataRow(data);
        compositeOperation.addOperation(createBPLocation);

        createUser(compositeOperation, ticket);
    }

    private void createLocation(CompositeOperationRequest compositeOperation, TicketInfo ticket) {
        CreateUpdateDataRequest createLocation = new CreateUpdateDataRequest();
        createLocation.setWebServiceType("nTierCreateLocation");
        DataRow data = new DataRow();

        Field field = new Field("C_Country_ID");
        field.setLval(ticket.getCustomer().getCountry());
        data.addField(field);
        data.addField("Address1", ticket.getCustomer().getAddress());
        data.addField("Address2", ticket.getCustomer().getAddress2());
        data.addField("Postal", ticket.getCustomer().getPostal());
        data.addField("City", ticket.getCustomer().getCity());

        createLocation.setDataRow(data);
        compositeOperation.addOperation(createLocation);

        createBPLocation(compositeOperation, ticket);

    }

    private void createUser(CompositeOperationRequest compositeOperation, TicketInfo ticket) {
        CreateUpdateDataRequest createUser = new CreateUpdateDataRequest();
        createUser.setWebServiceType("nTierCreateUser");
        DataRow data = new DataRow();

        data.addField("Name", ticket.getCustomer().getFirstname());
        data.addField("C_BPartner_ID", "@C_BPartner.C_BPartner_ID");
        data.addField("C_BPartner_Location_ID", "@C_BPartner_Location.C_BPartner_Location_ID");
        data.addField("EMail", ticket.getCustomer().getEmail());
        data.addField("Phone", ticket.getCustomer().getPhone());

        createUser.setDataRow(data);
        compositeOperation.addOperation(createUser);

    }

    private void createWsMixedPayment(CompositeOperationRequest compositeOperation, TicketInfo ticket) {
        // Create WS for Order Line
        CreateDataRequest createMixedPayment = new CreateDataRequest();
        createMixedPayment.setWebServiceType("nTierCreatePosPayment");
        DataRow data = new DataRow();
        data.addField("AD_Client_ID", "@C_Order.AD_Client_ID");
        data.addField("AD_Org_ID", "@C_Order.AD_Org_ID");
        data.addField("C_Order_ID", "@C_Order.C_Order_ID");
        setPaymentTenderType(data, ticket);
        data.addField("PayAmt", Double.toString(round(ticket.getTotal(), 2)));

        createMixedPayment.setDataRow(data);
        compositeOperation.addOperation(createMixedPayment);

    }

    enum IDPosTenderType {
        CASH("1000001"),
        CREDITCARD("1000000"),
        EFT("1000002");

        private final String value;

        public final String getValue() {
            return value;
        }

        private IDPosTenderType(String value) {
            this.value = value;
        }
    }

    enum IDTenderType {
        CASH("X"),
        CREDITCARD("C"),
        EFT("A");

        private final String value;

        public final String getValue() {
            return value;
        }

        private IDTenderType(String value) {
            this.value = value;
        }
    }

    private void setPaymentTenderType(DataRow data, TicketInfo ticket) {

        List<PaymentInfo> payments = ticket.getPayments();

        switch (payments.get(0).getName()) {
            case UCTenderType_CASH:
                data.addField("C_POSTenderType_ID", IDPosTenderType.CASH.getValue());
                data.addField("TenderType", IDTenderType.CASH.getValue());
                break;
            case UCTenderType_CREDITCARD:
                data.addField("C_POSTenderType_ID", IDPosTenderType.CREDITCARD.getValue());
                data.addField("TenderType", IDTenderType.CREDITCARD.getValue());
                break;
            case UCTenderType_EFT:
                data.addField("C_POSTenderType_ID", IDPosTenderType.EFT.getValue());
                data.addField("TenderType", IDTenderType.EFT.getValue());
                break;
            default:
                System.out.println("\n" + "************ UC tender type not recognised*************"
                        + payments.get(0).getName());
                data.addField("C_POSTenderType_ID", IDPosTenderType.CASH.getValue());
                data.addField("TenderType", IDTenderType.CASH.getValue());
        }
    }

    private void completeOrder(CompositeOperationRequest compositeOperation) {
        SetDocActionRequest createDocAction = new SetDocActionRequest();
        createDocAction.setWebServiceType("nTierCompleteOrder");
        createDocAction.setTableName("C_Order");
        createDocAction.setRecordID(0);
        createDocAction.setRecordIDVariable("@C_Order.C_Order_ID");
        createDocAction.setDocAction(DocAction.Complete);

        compositeOperation.addOperation(createDocAction);
    }

    private int queryOrderRecordId(TicketInfo ticket) {
        QueryDataRequest query = new QueryDataRequest();
        query.setWebServiceType("nTierQueryOrderRecordId");
        query.setLogin(orders.getLogin());
        DataRow dataRow = new DataRow();
        dataRow.addField("AD_Client_ID", orders.getAD_Client_ID());
        dataRow.addField("AD_Org_ID", orders.getAD_Org_ID());
        dataRow.addField("DocumentNo", Integer.toString(ticket.getTicketId()));
        dataRow.addField("GrandTotal", Double.toString(round(ticket.getTotal(), 2)));
        query.setDataRow(dataRow);
        return (sendWsQueryRequest(query));
    }

    private int queryBpRecordId(TicketInfo ticket) {
        QueryDataRequest query = new QueryDataRequest();
        query.setWebServiceType("nTierQueryBP");
        query.setLogin(orders.getLogin());
        DataRow dataRow = new DataRow();
        dataRow.addField("AD_Client_ID", orders.getAD_Client_ID());
        dataRow.addField("Name", "%" + ticket.getCustomer().getName() + "%");
        query.setDataRow(dataRow);
        return (sendWsQueryRequest(query));
    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private int transformTickets(List<TicketInfo> ticketlist, SyncOrder orders) {
        String result;
        int imported = 0;
        int C_OrderType_ID = 0;

        //System.out.println("Cantidad de ticket para enviar: " + ticketlist.size());
        System.out.println("\n" + new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.qtyorders_sync")).getMessageMsg()
                + ticketlist.size() + "\n");
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
                        int I_Order_ID = dlintegration.findI_Order_ID(ticket.getId(), line.getTicketLine());
                        if (I_Order_ID != 0) {
                            continue;
                        }
                        orderLine[j] = new OrderLine();
                        orderLine[j].setOrderLineId(String.valueOf(line.getTicketLine()));// or simply "j"

                        DataRow data = new DataRow();
                        if (ticket.getTicketType() == 0) {

                            C_OrderType_ID = orders.getC_DocType_ID(); //regular order
                        } else {

                            C_OrderType_ID = orders.getC_DocTypeRefund_ID(); //return
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
                            if (ticket.getCustomer().getAddress() != null && !ticket.getCustomer().getAddress().equals("")) {
                                data.addField("Address1", ticket.getCustomer().getAddress());
                                data.addField("City", ticket.getCustomer().getCity());
                            } else {
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
                            if (!line.getProductID().contains("-")) {
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

                        if (ticket.getCustomer().getPhone() != null) {
                            data.addField("Phone", ticket.getCustomer().getPhone());
                        } else {
                            data.addField("Phone", "");
                        }
                        if (ticket.getCustomer().getEmail() != null) {
                            data.addField("EMail", ticket.getCustomer().getEmail());
                        } else {
                            data.addField("EMail", "");
                        }

                        /*  data.addField("OPOS_numberoflines", ticket.getLines().size());
                        
                        data.addField("OPOS_line", line.getTicketLine());
                        
                        data.addField("fiscalNumber", dlfiscal.findFiscalNumber(ticket.getId(), ticket.getTicketType()));
                        data.addField("FiscalPrintSerial", dlfiscal.findFiscalSerial(ticket.getId(), ticket.getTicketType()));                        
                       yogan060520 */
                        int recordid = orders.SendOrders(data);
                        if (recordid != 0) {
                            dlintegration.execTicketLineUpdate(ticket.getId(), line.getTicketLine(), recordid);
                        }
                    }
                    //REVISO SI faltan lineas
                    System.out.println(dlintegration.countExportedLines(ticket.getId()));
                    System.out.println(ticket.getLinesCount());
                    if (dlintegration.countExportedLines(ticket.getId()) == ticket.getLinesCount()) {
                        System.out.println("*************Resultado*************");

                        result = orders.processOrder(ticket.getTicketId(), C_OrderType_ID);
                        System.out.println(result);
                        if (result != null) {
                            System.out.println("*************Orden Importada: " + ticket.getTicketId() + "*************");
                            dlintegration.execTicketUpdate(ticket.getId(), "1");
                            imported++;
                        } else {
                            System.out.println("*************Falló al procesar orden: " + ticket.getTicketId() + "*************");
                            dlintegration.execTicketUpdate(ticket.getId(), "0");
                        }
                    } else {
                        System.out.println("*************Aun Faltan Lineas: " + ticket.getTicketId() + "*************");
                        dlintegration.execTicketUpdate(ticket.getId(), "0");

                    }
                    //PROCESAR ORDEN

                } catch (BasicException ex) {
                    Logger.getLogger(SyncOrderThread.class
                            .getName()).log(Level.SEVERE, null, ex);
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
