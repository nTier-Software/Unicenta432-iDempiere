/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync.externalsales;

import com.openbravo.pos.ticket.ProductInfoExt;
import java.util.Date;

/**
 *
 * @author egil
 */
public class ProductInfoExt2 extends ProductInfoExt{
    private Date Updated;

    public Date getUpdated() {
        return Updated;
    }

    public void setUpdated(Date Updated) {
        this.Updated = Updated;
    }
    
    
}
