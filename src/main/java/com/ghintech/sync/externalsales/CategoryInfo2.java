/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync.externalsales;

import com.openbravo.pos.ticket.CategoryInfo;
import java.awt.image.BufferedImage;

/**
 *
 * @author alara
 */
public class CategoryInfo2 extends CategoryInfo{

    private String ParentID;
    private String ParentName;

    public CategoryInfo2(String id, String name, BufferedImage image, String texttip, Boolean catshowname) {
        super(id, name, image, texttip, catshowname);
    }
    /**
     *
     * @param ParentID
     */
    public void setParentID(String ParentID) {
        this.ParentID = ParentID;
    }

    /**
     *
     * @return
     */
    public String getParentID() {
        return ParentID;
    }

    /**
     *
     * @param ParentName
     */
    public void setParentName(String ParentName) {
        this.ParentName = ParentName;
    }

    /**
     *
     * @return
     */
    public String getParentName() {
        return ParentName;
    }
}
