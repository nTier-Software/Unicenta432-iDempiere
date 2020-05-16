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

import com.openbravo.pos.forms.JRootApp;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;



public class SyncCustomersThread extends Thread {

    
    private final JRootApp app;
    
    private final DataLogicIntegration dlintegration;
   
    SyncCustomers customers;

    
    public SyncCustomersThread(JRootApp rootApp) {
        
        app = rootApp;            
        dlintegration = (DataLogicIntegration) app.getBean("com.ghintech.sync.DataLogicIntegration");
        customers =  new SyncCustomers(app);
    }

    @Override
    public void run() {
        // Envía las ordenes a la cola de manera automática
        boolean sent = true; // Define si el mensaje fué enviado a la cola
        Double stopLoop; // Tiempo que se detendrá el cilclo 
        int c = 0;

        while (true) {
            try {
                /* si el mensaje fue enviado las ordenes de enviaran al tiempo definido por el usuario;
                  si no se intentará a enviar dentro de un minuto */
                stopLoop = sent == true ? customers.getWsCustomerTypeInterval() : 0.25;
                //si es la primera vez que entra no se detiene el ciclo
                if (c != 0) {
                    sleep(converter(stopLoop));
                }
                customers.getCustomerInfo();
                sent = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(SyncCustomersThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            c++;
        }

    }

    //convierte de minuto a milisegundos
    public long converter(Double min) {
        long millis = (long) (min * 60 * 1000);
        return millis;
    }
}
