//    Openbravo POS is a point of sales application designed for touch screens.
//    http://www.openbravo.com/product/pos
//    Copyright (c) 2007 openTrends Solucions i Sistemes, S.L
//    Modified by Openbravo SL on March 22, 2007
//    These modifications are copyright Openbravo SL
//    Author/s: A. Romero 
//    You may contact Openbravo SL at: http://www.openbravo.com
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

import com.ghintech.fiscalprint.bematech.BemaFI;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.ProcessAction;
import com.sun.jna.Native;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class PrintReport implements ProcessAction {
    private final String reportType;
    private final String invoiceFolder;
    private final String spoolerFolder;
    private final String fileName;
    private final String vendor;
    private final String library;
    private final String portNumber ;
    private final String confirmxz ;
    private final String[] content ;
    private final String logRoute;
    private final String PathTmp;
    public PrintReport(String reportType, DataLogicSystem dlsystem, String hostname) {
        this.reportType = reportType;
        this.content = null;
        Properties prop = dlsystem.getResourceAsProperties("fiscalprint.properties");
        Properties propsdb = dlsystem.getResourceAsProperties(hostname + "/properties");
        String invoiceFolderT = null;
        String spoolerFolderT = null;
        String fileNameT = null;
        String vendorT = propsdb.getProperty("vendor");
        String libraryT = null;
        String portNumberT= propsdb.getProperty("portNumber");
        String confirmxz = prop.getProperty("confirmxz");
        String logRoute = propsdb.getProperty("logRoute");
        String PathTmp = propsdb.getProperty("PathTmp");                    
        if (propsdb.getProperty("vendor").compareTo("thefactory") == 0) {
            invoiceFolderT = propsdb.getProperty("invoiceFolder");
            spoolerFolderT = propsdb.getProperty("spoolerFolder");
            fileNameT = propsdb.getProperty("fileName");
        } else if (propsdb.getProperty("vendor").compareTo("bematech") == 0) {
            libraryT = propsdb.getProperty("library");
        } else if (propsdb.getProperty("vendor").compareTo("hasar") == 0) {
            invoiceFolderT = propsdb.getProperty("invoiceFolder");
            spoolerFolderT = propsdb.getProperty("spoolerFolder");
            fileNameT = propsdb.getProperty("fileName");
            portNumberT = propsdb.getProperty("portNumber");
        }
        this.invoiceFolder = invoiceFolderT;
        this.spoolerFolder = spoolerFolderT;
        this.fileName = fileNameT;
        this.vendor = vendorT;
        this.library = libraryT;
        this.portNumber = portNumberT;
        this.confirmxz = confirmxz;
        this.logRoute = logRoute;
        this.PathTmp = PathTmp;
    }

   public PrintReport(String reportType, String[] text, DataLogicSystem dlsystem, String hostname) {
        this.reportType = reportType;
        this.content = text;
        Properties propsdb = dlsystem.getResourceAsProperties(hostname + "/properties");
        Properties prop = dlsystem.getResourceAsProperties("fiscalprint.properties");
        String invoiceFolderT = null;
        String spoolerFolderT = null;
        String fileNameT = null;
        String vendorT = propsdb.getProperty("vendor");
        String libraryT = null;
        String portNumberT= propsdb.getProperty("portNumber");
        String confirmxz = prop.getProperty("confirmxz");
        String logRoute = propsdb.getProperty("logRoute");
        String PathTmp = propsdb.getProperty("PathTmp");                            
        if (propsdb.getProperty("vendor").compareTo("thefactory") == 0) {
            invoiceFolderT = propsdb.getProperty("invoiceFolder");
            spoolerFolderT = propsdb.getProperty("spoolerFolder");
            fileNameT = propsdb.getProperty("fileName");
        } else if (propsdb.getProperty("vendor").compareTo("bematech") == 0) {
            libraryT = propsdb.getProperty("library");
        } else if (propsdb.getProperty("vendor").compareTo("hasar") == 0) {
            invoiceFolderT = propsdb.getProperty("invoiceFolder");
            spoolerFolderT = propsdb.getProperty("spoolerFolder");
            fileNameT = propsdb.getProperty("fileName");
            portNumberT = propsdb.getProperty("portNumber");
        }
        this.invoiceFolder = invoiceFolderT;
        this.spoolerFolder = spoolerFolderT;
        this.fileName = fileNameT;
        this.vendor = vendorT;
        this.library = libraryT;
        this.portNumber = portNumberT;
        this.confirmxz = confirmxz;
        this.logRoute = logRoute;
        this.PathTmp = PathTmp;        
    }
    
    @Override
    public MessageInf execute() throws BasicException {
        Hasar objHasar =new Hasar();
        boolean printreport = true;
        try {
            if(confirmxz.equals("Y")){
                int res = JOptionPane.showConfirmDialog(null, AppLocal.getIntString("message.printfiscalreport"), AppLocal.getIntString("message.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.NO_OPTION) {
                    return null;
                }
            }
            if(printreport){
                if (this.vendor.compareTo("thefactory") == 0) {
                    File file = new File(this.invoiceFolder + this.fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(this.reportType);
                    writer.flush();
                    writer.close();
                    String filePath = this.spoolerFolder + "IntTFHKA.exe SendFileCmd(" + this.invoiceFolder + this.fileName + ")";
                    Process p = Runtime.getRuntime().exec(filePath);
                    p.waitFor();
                    boolean checkprinter = false;
                    File fileStatus = new File(this.spoolerFolder + "Status_Error.txt");
                    if (fileStatus.exists()) {
                        String status;
                        FileReader reader = new FileReader(fileStatus);
                        BufferedReader br = new BufferedReader(reader);
                        while ((status = br.readLine()) != null) {
                            String[] statusError = status.split("\\t");
                            if (statusError[2].compareTo("0") != 0) continue;
                            checkprinter = true;
                        }
                        reader.close();
                    }
                    if (!checkprinter) {
                        JOptionPane.showMessageDialog(null, "No se pudo imprimir el ticket correctamente", "POS", JOptionPane.PLAIN_MESSAGE);
                        return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotprint"));
                    }
                } else if (this.vendor.compareTo("bematech") == 0) {
                    BemaFI print = (BemaFI)Native.loadLibrary((String)this.library, (Class)BemaFI.class);
                    int iRetorno = 1;
                    if(this.reportType.compareTo("I0X")== 0){
                       iRetorno = print.Bematech_FI_LecturaX();
                    }
                    else if(this.reportType.compareTo("I0Z")== 0){
                       iRetorno = print.Bematech_FI_ReduccionZ("", "");
                    }
                    else if(this.reportType.compareTo("Summary")== 0){
                        for (String text: content){      
                            iRetorno = print.Bematech_FI_InformeGerencial(text);                                    
                        }
                        iRetorno = print.Bematech_FI_CierraInformeGerencial();
                    }
                    //int iRetorno = this.reportType.compareTo("I0X") == 0 ? print.Bematech_FI_LecturaX() : print.Bematech_FI_ReduccionZ("", "");
                    if (iRetorno != 1) {
                        JOptionPane.showMessageDialog(null, "No se pudo imprimir el ticket correctamente", "POS", JOptionPane.PLAIN_MESSAGE);
                        return new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotprint"));
                    }
                } else if (this.vendor.compareTo("hasar") == 0) {
                    //NEW CODE
                    int speed = 9600;
                    String spoolerName = "wspooler.exe";

                    HandlerSpooler hsp = new HandlerSpooler(portNumber,speed,spoolerFolder.concat(spoolerName),logRoute,PathTmp,"", //null for windows sh for linux
                                        null,28);
                    try {
                        if(this.reportType.compareTo("I0X") == 0){
                            hsp.printXReport();
                        } 
                        else if(this.reportType.compareTo("I0Z") == 0){
                            hsp.printZReport();
                        }
                    }catch (Exception ex) {
                        Logger.getLogger(PrintReport.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    /*
                    File file = new File(this.invoiceFolder + this.fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    if(this.reportType.compareTo("I0X") == 0){
                        writer.append('9').append(objHasar.FS).append('X');
                    }else if(this.reportType.compareTo("I0Z") == 0){
                        writer.append('9').append(objHasar.FS).append('Z');
                    }
                    writer.flush();
                    writer.close();
                    String filePath = this.spoolerFolder + "wspooler.exe -"+this.portNumber+" -z -s " + invoiceFolder ;
                    Process p = Runtime.getRuntime().exec(filePath);
                    System.err.println(filePath);
                    System.err.println(p);
                    System.err.println(p.getErrorStream());
                    // Delete Folder Files
                     FileReader fileans = new FileReader(this.invoiceFolder + "factura.ans");
                     String status;
                        BufferedReader br = new BufferedReader(fileans);
                        while ((status = br.readLine()) != null) {
                            System.err.println(status);
                            String[] statusError = status.split("\\t");
                            System.err.println(statusError);
                        }
                    //objHasar.emptyInvoiceFolder(invoiceFolder);                        
                        p.destroy();     
                        */
                }
            }
        }
        catch (IOException | InterruptedException ex) {
            Logger.getLogger(PrintReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new MessageInf(MessageInf.SGN_SUCCESS, AppLocal.getIntString("message.printok"));
    }
}
