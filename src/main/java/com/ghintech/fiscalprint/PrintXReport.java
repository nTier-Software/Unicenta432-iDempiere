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

import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryCache;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSystem;

/**
 * @author Eduardo Gil
 */
public class PrintXReport extends BeanFactoryCache {
    
    @Override
    public Object constructBean(AppView app) throws BeanFactoryException {
        DataLogicSystem dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        DataLogicFiscal dlFiscal = (DataLogicFiscal) app.getBean("com.ghintech.fiscalprint.DataLogicFiscal");
        PrintReport bean = new PrintReport("I0X",dlSystem,dlFiscal.getHostName());
        return bean;
    }
}
