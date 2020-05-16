//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2017 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.sales;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.catalog.CatalogSelector;
import com.openbravo.pos.catalog.JCatalog;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.ticket.ProductInfoExt;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author JG uniCenta
 */
public class JPanelTicketCreditNote extends JPanelTicket {

    private CatalogSelector m_cat;
    double multiply = -1.0;
   
    /** Creates a new instance of JPanelTicketSales */
    public JPanelTicketCreditNote() {        
    }
    
    /**
     *
     * @param app
     */
    @Override
    public void init(AppView app) {
        super.init(app);
        m_ticketlines.addListSelectionListener(new CatalogSelectionListener());
        }
    
    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return "Nota de Crédito";
    }
    
    /**
     *
     * @return
     */
    @Override
    protected Component getSouthComponent() {
        m_cat = new JCatalog(dlSales,
                "true".equals(m_jbtnconfig.getProperty("pricevisible")),
                "true".equals(m_jbtnconfig.getProperty("taxesincluded")),
                Integer.parseInt(m_jbtnconfig.getProperty("img-width", "64")),                
                Integer.parseInt(m_jbtnconfig.getProperty("img-height", "54")));
        //   Integer.parseInt(m_jbtnconfig.getProperty("img-width", "32")),
               //   Integer.parseInt(m_jbtnconfig.getProperty("img-height", "32")));
        m_cat.addActionListener(new CatalogListener());
        m_cat.getComponent().setPreferredSize(new Dimension(
                0,
                Integer.parseInt(m_jbtnconfig.getProperty("cat-height", "245"))));
        return m_cat.getComponent();
    }

    /**
     *
     */
    @Override
    protected void resetSouthComponent() {
       m_cat.showCatalogPanel(null);
    }
    
    @Override
    protected void resetTicketType()
    {
        m_oTicket.setTicketType(1);
        ticketlab = true;
        repaint();
        System.err.println("CREDITNOTETICKET" + m_oTicket.getTicketType());
    }
    
    /**
     *
     * @return
     */
    @Override
    protected JTicketsBag getJTicketsBag() {
        return JTicketsBag.createTicketsBag("CreditNote", m_App, this);
    }
    
    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {      
        super.activate();
         m_oTicket.setTicketType(1);
         m_cat.loadCatalog();
    }      
    
    public void reLoadCatalog() {
        try {
            m_oTicket.setTicketType(1);
            m_cat.loadCatalog();
        } catch (BasicException ex) {
        }

    }

    private class CatalogListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            m_oTicket.setTicketType(1);
            buttonTransition((ProductInfoExt) e.getSource());
        }  
    }
    
    private class CatalogSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {      
            m_oTicket.setTicketType(1);
            if (!e.getValueIsAdjusting()) {
                int i = m_ticketlines.getSelectedIndex();
                if (i >= 0) {
                   while (i >= 0 && m_oTicket.getLine(i).isProductCom()) {
                        i--;
                    }

                    // Show the accurate catalog panel...
                    if (i >= 0) {
                        m_cat.showCatalogPanel(m_oTicket.getLine(i).getProductID());
                    } else {
                        m_cat.showCatalogPanel(null);
                    }
                }
            }
        }  
    }
       
}