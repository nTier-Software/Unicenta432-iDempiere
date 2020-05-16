/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.sync;

import com.openbravo.basic.BasicException;
import com.ghintech.sync.externalsales.Category;
import com.ghintech.sync.externalsales.CategoryInfo2;
import com.ghintech.sync.externalsales.Product;
import com.ghintech.sync.externalsales.ProductInfoExt2;
import com.ghintech.sync.externalsales.ProductPlus;
import com.ghintech.sync.externalsales.Tax;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.inventory.MovementReason;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.ticket.CategoryInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.idempiere.webservice.client.base.Enums.WebServiceResponseStatus;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;




/**
 *
 * @author egil
 */
public final class SyncProducts extends Sync{

    int OffSet;
    int Limit;
    QueryDataRequest ws;
    public SyncProducts(JRootApp app) {
        super(app);
        OffSet=1;
        Limit=getwsLimit();
        ws = new QueryDataRequest();
        ws.setWebServiceType(getWsProductType());
        ws.setLogin(getLogin());
        ws.setLimit(Limit);
        Date updated=null;
        try {
            updated = dlintegration.findLastUpdated("PRODUCTS");
            if (updated == null){
                updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1900-01-01 00:00:00");
            }
        } catch (ParseException ex) {
            Logger.getLogger(SyncProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        Calendar up = Calendar.getInstance();
        up.setTime(updated);
        up.add(Calendar.HOUR, -2);
        //String filter = " Updated > \'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(up.getTime())+"\' AND M_Warehouse_ID="+getM_Warehouse_ID();
        //ws.setFilter(filter);
        //ws.setFilter(" M_Warehouse_ID="+getM_Warehouse_ID());
        
        
        
    }
    public void getProductInfo() throws MalformedURLException, IOException{
        WebServiceConnection client = getClient();
        try {           
            do{                
                WindowTabDataResponse response = client.sendRequest(ws);
                if (response.getStatus() == WebServiceResponseStatus.Error) {
                    System.out.println(response.getErrorMessage());
                    return;
                } else {
                    System.out.println("Total rows: " + response.getTotalRows());
                    System.out.println("Num rows: " + response.getNumRows());
                    System.out.println("Start row: " + response.getStartRow());
                    if(response.getNumRows()>0){
                        OffSet+=Limit;
                    }else{
                        OffSet=0;
                    }
                    ProductPlus[] product = new ProductPlus[response.getNumRows()];
                    for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {
                        //System.out.println("Row: " + (i + 1));
                        product[i] = new ProductPlus();
                        Category cate = new Category();
                        Tax newtax = new Tax();
                        for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {
                            Field field = response.getDataSet().getRow(i).getFields().get(j);
                            //int index =response.getDataSet().getRow(i).getFields().indexOf("POSLocatorName");
                            System.out.print(" " + field.getColumn() + " = " + field.getValue() + "  ");
                            String fieldValue = String.valueOf(field.getValue());
                            switch (field.getColumn()) {
                                case "POSLocatorName":
                                    product[i].setLocation_name(fieldValue);
                                    break;
                                case "M_Locator_ID":
                                //field = response.getDataSet().getRow(i).getFields().get(1);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setLocation_id(fieldValue);
                                    break;
                                case "ProductValue":
                                //field = response.getDataSet().getRow(i).getFields().get(2);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setReference(fieldValue);
                                    break;     
                                case "ProductName":
                                //field = response.getDataSet().getRow(i).getFields().get(3);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setName(fieldValue);
                                    break;
                                case "QtyOnHand":
                                //field = response.getDataSet().getRow(i).getFields().get(4);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setQtyonhand(Double.parseDouble(fieldValue));
                                    break;
                                //field = response.getDataSet().getRow(i).getFields().get(5);
                                //fieldValue = String.valueOf(field.getValue());                                    
                                case "M_Product_Category_ID":
                                    cate.setId(fieldValue);
                                    if (cate.getName() != null)
                                        product[i].setCategory(cate);
                                    break;
                                case "CategoryName":
                                //field = response.getDataSet().getRow(i).getFields().get(6);
                                //fieldValue = String.valueOf(field.getValue());
                                    cate.setName(fieldValue);
                                    if (cate.getName() != null)
                                        product[i].setCategory(cate);
                                    break;
                                    
                                case "M_Product_ID":
                                //field = response.getDataSet().getRow(i).getFields().get(7);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setId(fieldValue);
                                    break;
                                case "C_Tax_ID":
                                //field = response.getDataSet().getRow(i).getFields().get(8);
                                //fieldValue = String.valueOf(field.getValue());
                                    newtax.setId(fieldValue);
                                    if (newtax.getName() != null)
                                        product[i].setTax(newtax);
                                    break;
                                case "TaxRate":
                                //field = response.getDataSet().getRow(i).getFields().get(9);
                                //fieldValue = String.valueOf(field.getValue());
                                    newtax.setPercentage(Double.parseDouble(fieldValue));
                                    if (newtax.getId() != null || newtax.getName() != null)
                                        product[i].setTax(newtax);
                                    break;
                                case "TaxName":
                                //field = response.getDataSet().getRow(i).getFields().get(10);
                                //fieldValue = String.valueOf(field.getValue());
                                    newtax.setName(fieldValue);
                                    if (newtax.getId() != null)
                                        product[i].setTax(newtax);
                                    break;
                                case "UPC":
                                //field = response.getDataSet().getRow(i).getFields().get(11);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setEan(fieldValue);
                                    break;
                                case "PriceList":
                                //field = response.getDataSet().getRow(i).getFields().get(12);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setListPrice(Double.parseDouble(fieldValue));
                                    break;
                                case "PriceLimit":
                                //field = response.getDataSet().getRow(i).getFields().get(13);
                                //fieldValue = String.valueOf(field.getValue());
                                    product[i].setPurchasePrice(Double.parseDouble(fieldValue));
                                    break;
                                case "ImageURL":
                                //field = response.getDataSet().getRow(i).getFields().get(14);
                                //fieldValue = String.valueOf(field.getValue());
                                    if(fieldValue!=null && fieldValue.compareTo("")!=0){
                                        product[i].setImageUrl(fieldValue);
                                        URL imageURL = new URL(fieldValue);
                                        BufferedImage img = ImageIO.read(imageURL);
                                        product[i].setImage(img);
                                    }
                                    break;
                                case "Updated":
                                //field = response.getDataSet().getRow(i).getFields().get(15);
                                //fieldValue = String.valueOf(field.getValue());
                                    try {
                                        product[i].setUpdated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue));

                                    } catch (ParseException ex) {
                                        Logger.getLogger(SyncProducts.class.getName()).log(Level.SEVERE, null, ex);
                                    }                            
                                    break; 
                                case "M_Product_Category_Parent_ID":
                                //field = response.getDataSet().getRow(i).getFields().get(16);
                                //fieldValue = String.valueOf(field.getValue());
                                    if(fieldValue!=null && fieldValue.compareTo("")!=0){
                                    product[i].getCategory().setParentID(fieldValue);
                                    }
                                    break;
                                case "ParentCategoryName":
                                //field = response.getDataSet().getRow(i).getFields().get(17);
                                //fieldValue = String.valueOf(field.getValue());
                                    if(fieldValue!=null && fieldValue.compareTo("")!=0){
                                    product[i].getCategory().setParentName(fieldValue);
                                    }
                                    break;                                    
                                
                                //field = response.getDataSet().getRow(i).getFields().get(18);
                                //fieldValue = String.valueOf(field.getValue());
                                
                                
                                
                                case "UOMType":
                                    if(fieldValue != null && !"".equals(fieldValue))
                                        if("WE".equals(fieldValue))
                                            product[i].setisScale("Y");
                                    break;                                    
                                case "ProductType":
                                    if(fieldValue.compareTo("S")==0)
                                        product[i].setIsService(true);
                                    
                                    break;
                                default:
                                    break;
                                    
                            }
                        }
                    }
                    ImportProducts(product);                    
                }
                ws.setOffset(OffSet);
            }while (OffSet!=0);   
        } catch (WebServiceException | NumberFormatException e) {
        }
    }
    
    public boolean ImportProducts(ProductPlus[] products){
        try {            
            if (products == null){
                    throw new BasicException(AppLocal.getIntString("message.returnnull"));
            }
            if (products.length > 0){
                //dlintegration.syncProductsBefore();                
                Date now = new Date();
                //System.out.println("Cantidad de productos" + products.length);
                for (Product product : products) {
                    System.out.println("Registering Product: "+product.getName()+" updated: "+product.getUpdated());           
                    // Synchonization of taxcategories
                    TaxCategoryInfo tc = new TaxCategoryInfo(product.getTax().getId(), product.getTax().getName());
                    dlintegration.syncTaxCategory(tc);
                    TaxCategoryInfo tcaux = dlintegration.getTaxCategoryInfoByName(tc.getName());
                    // Synchonization of taxes
                    TaxInfo t = new TaxInfo(
                                product.getTax().getId(),
                                product.getTax().getName(),
                                tcaux.getID(),
                                //new Date(Long.MIN_VALUE),
                                null,
                                null,
                                product.getTax().getPercentage() / 100,
                                false,
                                0);
                    dlintegration.syncTax(t);   
                    // Synchonization of categories
                    CategoryInfo2 c = new CategoryInfo2(product.getCategory().getId(),product.getCategory().getName(), null,null,null);
                    if(product.getCategory().getParentID() != null){
                        c.setParentID(product.getCategory().getParentID());
                        c.setParentName(product.getCategory().getParentName());
                        dlintegration.syncParentCategory(c);
                    }
                    dlintegration.syncCategory(c);
                    CategoryInfo caux = dlintegration.getCategoryInfoByName(c.getName());
                    // Synchonization of products
                    ProductInfoExt2 p = new ProductInfoExt2();
                    p.setID(product.getId());
                    p.setReference(product.getReference());
                    p.setCode(product.getEan() == null || product.getEan().equals("") ? product.getReference(): product.getEan());
                    // String auxname=product.getName().replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    // auxname=auxname.replaceAll("\\+", "%2B");
                    //System.out.println(product.getName());
                    p.setName(product.getName());
                    p.setCom(false);
                    p.setScale(false);
                    p.setPriceBuy(product.getPurchasePrice());
                    p.setPriceSell(product.getListPrice());
                    p.setCategoryID(caux.getID());
                    p.setTaxCategoryID(tcaux.getID());
                    //p.setImage(ImageUtils.readImage(product.getImageUrl()));
                    p.setImage(product.getImage());
                    p.setScale("Y".equals(product.getisScale()));
                    // build html display like <html><font size=-2>MIRACLE NOIR<br> MASK</font>
                    p.setDisplay("<html><font size=-2>"+product.getName().substring(0, product.getName().length()>15?15:product.getName().length()) 
                                    + "<br>" +((product.getName().length()>15)? product.getName().substring(15, product.getName().length()>30?30:product.getName().length()):"")
                                    + "</font>");
                    p.setUpdated(product.getUpdated());
                    p.setService(product.isIsService());
                    dlintegration.syncProduct(p);
                    // Synchronization of stock          
                    if (product instanceof ProductPlus) {
                        ProductPlus productplus = (ProductPlus) product;
                        ProductInfoExt productaux=dlintegration.getProductInfoByReference(productplus.getReference());
                        //  Synchonization of locations
                        dlintegration.syncLocations(productplus.getLocation_id(),productplus.getLocation_name());
                        double diff = productplus.getQtyonhand() - dlintegration.findProductStock(productplus.getLocation_id(), p.getID(), null);
                        if(diff != 0.0){
                            Object[] diary = new Object[9];
                            diary[0] = UUID.randomUUID().toString();
                            diary[1] = now;
                            diary[2] = diff > 0.0 
                                        ? MovementReason.IN_MOVEMENT.getKey()
                                        : MovementReason.OUT_MOVEMENT.getKey();
                            //diary[3] = warehouse;
                            diary[3] = productplus.getLocation_id();
                            String pid=p.getID();
                            if (productaux.getID()!=null){
                                pid=productaux.getID();
                            }
                            diary[4] = pid;
                            diary[5] = null; ///TODO find out where to get AttributeInstanceID -- red1
                            diary[6] = diff;
                            diary[7] = p.getPriceBuy();
                            diary[8] = dlsystem.getUser();
                            dlintegration.getStockDiaryInsert().exec(diary);
                        }
                    }
                }
                dlintegration.syncProductsAfter();
                return true;
            }
        } catch (BasicException ex) {
            Logger.getLogger(SyncProducts.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return false;
    }


}

