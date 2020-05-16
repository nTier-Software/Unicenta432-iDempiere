//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
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

import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryCache;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSystem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Eduardo Gil
 */
public class PrintXReportSummary extends BeanFactoryCache {
    
    private DataLogicFiscal dlFiscal;
    
    @Override
    public Object constructBean(AppView app) throws BeanFactoryException {
        
        DataLogicSystem dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlFiscal = (DataLogicFiscal) app.getBean("com.ghintech.fiscalprint.DataLogicFiscal");
        String startDate=JOptionPane.showInputDialog("Introduzca una fecha en el formato dd/MM/aaaa",new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTimeInMillis()));
        DateFormat helper = new SimpleDateFormat("dd/MM/yyyy");
        Date datetmp = null;
        Properties propsdb = dlSystem.getResourceAsProperties(dlFiscal.getHostName() + "/properties");
        //Properties prop = dlSystem.getResourceAsProperties("fiscalprint.properties");
        String vendor = propsdb.getProperty("vendor");        
        try {
            datetmp = helper.parse(startDate);
        } catch (ParseException ex) {
            Logger.getLogger(PrintXReportSummary.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Valor de fecha incorrecto", "POS", JOptionPane.PLAIN_MESSAGE);
            
        }

        PrintReport bean = null;
        if (vendor.compareTo("thefactory") == 0) {
            datetmp = getDateLastZReport(dlSystem,dlFiscal.getHostName());

            if(datetmp==null) return null;            
            bean = new PrintReport("I0X\n"+createSummary(new SimpleDateFormat("yyyy-MM-dd").format(datetmp), dlSystem), dlSystem, dlFiscal.getHostName());
        }
        if (vendor.compareTo("bematech") == 0) {
            bean = new PrintReport("Summary",createSummaryBematech(new SimpleDateFormat("yyyy-MM-dd").format(datetmp), dlSystem),dlSystem, dlFiscal.getHostName());
        }
        if (vendor.compareTo("hasar") == 0) {
            JOptionPane.showMessageDialog(null, "Este reporte no es compatible con Hasar", "POS", JOptionPane.PLAIN_MESSAGE);

        }
        return bean;
    }

    public String createSummary(String startDate,DataLogicSystem dlSystem) {
        
        String rep ;
        rep="800\n"+dlFiscal.salesByProduct(startDate,dlSystem);
        rep+=dlFiscal.salesByCategory(startDate,dlSystem);
        rep+=dlFiscal.salesByTaxes(startDate,dlSystem);
        rep+=dlFiscal.salesByHour(startDate,dlSystem);
        rep+=dlFiscal.totalTips(startDate,dlSystem);
        rep+="810\n";
        
        return rep;
    }
    
        public String[] createSummaryBematech(String startDate,DataLogicSystem dlSystem) {
        
        String rep ;
        rep="\n"+dlFiscal.salesByProduct(startDate,dlSystem);
        rep+=dlFiscal.salesByCategory(startDate,dlSystem);
        rep+=dlFiscal.salesByTaxes(startDate,dlSystem);
        rep+=dlFiscal.salesByHour(startDate,dlSystem);
        rep+=dlFiscal.salesByPaymentType(startDate,dlSystem);
        rep+=dlFiscal.totalTips(startDate,dlSystem);
        rep+="\n";
        int maxchar = 600;
        String[] textarray = splitStringEvery(rep,maxchar);
        return textarray;
    }
        
    public Date getDateLastZReport(DataLogicSystem dlsystem,String hostname) {
        //Properties prop = dlsystem.getResourceAsProperties("fiscalprint.properties");
        Properties propsdb = dlsystem.getResourceAsProperties(hostname + "/properties");
        String spoolerFolder = null;
        
        String vendor = propsdb.getProperty("vendor");
        String library = null;
        if (propsdb.getProperty("vendor").compareTo("thefactory") == 0) {
            
            spoolerFolder = propsdb.getProperty("spoolerFolder");
            
        } else if (propsdb.getProperty("vendor").compareTo("bematech") == 0) {
            library = propsdb.getProperty("library");
        }
        if (vendor.compareTo("thefactory") == 0) {
            try {
                String filePath = spoolerFolder + "IntTFHKA.exe UploadReportCmd(U0Z)";
                Process p = Runtime.getRuntime().exec(filePath);
                p.waitFor();
                boolean checkprinter = false;
                File fileStatus = new File(spoolerFolder + "Status_Error.txt");
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
                    JOptionPane.showMessageDialog(null, "No se puede imprimir el reporte", "POS", JOptionPane.PLAIN_MESSAGE);
                }
                File fileReport = new File(spoolerFolder + "Report.txt");
                if (fileStatus.exists()) {
                    String report;
                    FileReader reader = new FileReader(fileStatus);
                    BufferedReader br = new BufferedReader(reader);
                    if ((report = br.readLine()) != null) {
                        DateFormat helper = new SimpleDateFormat("yyMMdd");
                        Date datetmp = null;
                        try {
                            datetmp = helper.parse(report.substring(4,10));
                        } catch (ParseException ex) {
                            Logger.getLogger(PrintXReportSummary.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Valor de fecha incorrecto", "POS", JOptionPane.PLAIN_MESSAGE);

                        }
                        return datetmp;
                    }
                    reader.close();
                }
                
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(PrintXReportSummary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (vendor.compareTo("bematech") == 0){
            
        }
        return null;
    }

public String[] splitStringEvery(String s, int interval) {
    int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
    String[] result = new String[arrayLength];

    int j = 0;
    int lastIndex = result.length - 1;
    for (int i = 0; i < lastIndex; i++) {
        result[i] = s.substring(j, j + interval);
        j += interval;
    } //Add the last bit
    result[lastIndex] = s.substring(j);

    return result;
}    
   
}
