/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.beans.ShoppingCartItem;
import com.gb4w21.musicalmoose.controller.BanneradJpaController;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author victo
 */
@Named
@SessionScoped
public class ShoppingCartController implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(ShoppingCartController.class);
    
    private List<ShoppingCartItem> shoppingCart = new ArrayList<ShoppingCartItem>();
    
    public void addShoppingCartAlbum(Album addedAlbum) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setId(addedAlbum.getAlbumid());
        shoppingCartItem.setTitle(addedAlbum.getAlbumtitle());
        shoppingCartItem.setArtist(addedAlbum.getArtist());
        shoppingCartItem.setPrice(addedAlbum.getCostprice());
        shoppingCartItem.setIsAlbum(true);
        shoppingCartItem.setImgNameSmall(addedAlbum.getAlbumimagefilenamesmall());
        
        shoppingCart.add(shoppingCartItem);
        LOG.info("Shopping Cart Album Added: " + addedAlbum.getAlbumtitle());
    }
    
    public void addShoppingCartTrack(MusicTrack addedTrack) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setId(addedTrack.getInventoryid());
        shoppingCartItem.setTitle(addedTrack.getTracktitle());
        shoppingCartItem.setArtist(addedTrack.getArtist());
        shoppingCartItem.setPrice(addedTrack.getCostprice());
        shoppingCartItem.setIsAlbum(false);
        shoppingCartItem.setImgNameSmall(addedTrack.getAlbumimagefilenamesmall());
        
        shoppingCart.add(shoppingCartItem);
        LOG.info("Shopping Cart Track Added: " + addedTrack.getTracktitle());
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().addResponseCookie("Test1", shoppingCart.get(0).getTitle(), null);
    }
    
    public List<ShoppingCartItem> getShoppingCartList() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().addResponseCookie("Test2", shoppingCart.get(0).getTitle(), null);
        LOG.info("Find shopping cart list");
        return this.shoppingCart;
    }
    
    public String deleteCartItem(ShoppingCartItem deleteItem) {
        shoppingCart.remove(deleteItem);
        return null;
    }
    
    public float calculateTotal() {
        float totalAmount = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            totalAmount += shoppingCart.get(i).getPrice();
        }
        return totalAmount;
    }
}
