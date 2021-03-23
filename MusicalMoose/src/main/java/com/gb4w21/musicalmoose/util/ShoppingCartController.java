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
import javax.servlet.http.Cookie;
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
    
    public ShoppingCartController() {
        // do cookies
        // check if has the cookies, then store into this.shoppingCart if so
        
    }
    
    public void addShoppingCartAlbum(Album addedAlbum) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setId(addedAlbum.getAlbumid());
        shoppingCartItem.setTitle(addedAlbum.getAlbumtitle());
        shoppingCartItem.setArtist(addedAlbum.getArtist());
        shoppingCartItem.setPrice(addedAlbum.getCostprice());
        shoppingCartItem.setIsAlbum(true);
        shoppingCartItem.setImgNameSmall(addedAlbum.getAlbumimagefilenamesmall());
        
        shoppingCart.add(shoppingCartItem);
        writeCartCookie(addedAlbum.getAlbumid(), "cart_album");
                
        LOG.info("Shopping Cart Album Added: " + addedAlbum.getAlbumtitle());
    }
    
    private void writeCartAlbumCookie(int albumId) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object albumCartCookie = context.getExternalContext().getRequestCookieMap().get("cart_album");
        
        if (albumCartCookie == null) {
            context.getExternalContext().addResponseCookie("cart_album", Integer.toString(albumId), null);
        }
        else {
            String albumCartCookiesIds = ((Cookie)albumCartCookie).getValue() + "," + Integer.toString(albumId);
            context.getExternalContext().addResponseCookie("cart_album", albumCartCookiesIds, null);
        }
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
        writeCartCookie(addedTrack.getInventoryid(), "cart_track");
        
        LOG.info("Shopping Cart Track Added: " + addedTrack.getTracktitle());
    }
    
    private void writeCartTrackCookie(int trackId) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object trackCartCookie = context.getExternalContext().getRequestCookieMap().get("cart_track");
        
        if (trackCartCookie == null) {
            context.getExternalContext().addResponseCookie("cart_track", Integer.toString(trackId), null);
        }
        else {
            String trackCartCookiesIds = ((Cookie)trackCartCookie).getValue() + "," + Integer.toString(trackId);
            context.getExternalContext().addResponseCookie("cart_track", trackCartCookiesIds, null);
        }
    }
    
    private void writeCartCookie(int id, String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartCookie = context.getExternalContext().getRequestCookieMap().get(cookieName);
        
        if (cartCookie == null || ((Cookie)cartCookie).getValue().isEmpty()) {
            context.getExternalContext().addResponseCookie(cookieName, Integer.toString(id), null);
        }
        else {
            String cartCookiesIds = ((Cookie)cartCookie).getValue() + "," + Integer.toString(id);
            context.getExternalContext().addResponseCookie(cookieName, cartCookiesIds, null);
        }
    }
    
    public List<ShoppingCartItem> getShoppingCartList() {
        LOG.info("Find shopping cart list");
        return this.shoppingCart;
    }
    
    public String deleteCartItem(ShoppingCartItem deleteItem) {
        shoppingCart.remove(deleteItem);
        removeCookie(deleteItem.getId(), deleteItem.getIsAlbum());
        return null;
    }
    
    private void removeCookie(int id, boolean isAlbum) {
        if (isAlbum) {
//            removeAlbumCookie(id);
            removeCookie(id, "cart_album");
        }
        else {
//            removeTrackCookie(id);
            removeCookie(id, "cart_track");
        }
    }
    
    private void removeAlbumCookie(int albumId) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object albumCartCookie = context.getExternalContext().getRequestCookieMap().get("cart_album");
        
        if (albumCartCookie != null) {
            StringBuilder sb = new StringBuilder(((Cookie)albumCartCookie).getValue());
            int idIndex = sb.indexOf(Integer.toString(albumId));
            if (idIndex != 0) {
                sb.deleteCharAt(idIndex - 1);
            }
            sb.deleteCharAt(idIndex);
            String albumCartCookiesIds = sb.toString();
            
            context.getExternalContext().addResponseCookie("cart_album", albumCartCookiesIds, null);
        }
    }
    
    private void removeTrackCookie(int trackId) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object trackCartCookie = context.getExternalContext().getRequestCookieMap().get("cart_track");
        
        if (trackCartCookie != null) {
            StringBuilder sb = new StringBuilder(((Cookie)trackCartCookie).getValue());
            int idIndex = sb.indexOf(Integer.toString(trackId));
            if (idIndex != 0) {
                sb.deleteCharAt(idIndex - 1);
            }
            sb.deleteCharAt(idIndex);
            String trackCartCookiesIds = sb.toString();
            
            context.getExternalContext().addResponseCookie("cart_track", trackCartCookiesIds, null);
        }
    }
    
    private void removeCookie(int id, String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartCookie = context.getExternalContext().getRequestCookieMap().get(cookieName);
        
        if (cartCookie != null) {
//            StringBuilder sb = new StringBuilder(((Cookie)cartCookie).getValue());
//            int idIndex = sb.indexOf(Integer.toString(id));
            String cartCookiesIds = ((Cookie)cartCookie).getValue();
            String idString = Integer.toString(id);
            int idIndex = cartCookiesIds.indexOf(idString);
            
            
            if (idIndex == 0 && cartCookiesIds.length() > idString.length()) {
//                sb.deleteCharAt(idIndex + 1);
//                sb.deleteCharAt(idIndex);
                cartCookiesIds = cartCookiesIds.replace(idString + ",", "");
            }
            else if (idIndex == 0) {
//                sb.deleteCharAt(idIndex);
                cartCookiesIds = cartCookiesIds.replace(idString, "");
            }
            else {
//                sb.deleteCharAt(idIndex);
//                sb.deleteCharAt(idIndex - 1);
                cartCookiesIds = cartCookiesIds.replace("," + idString, "");
            }
            
//            if (sb.length() == 1) {
////                sb.deleteCharAt(idIndex);
//                
//            }
//            else if (idIndex == 0 && sb.length() > 1) {
////                sb.deleteCharAt(idIndex + 1);
////                sb.deleteCharAt(idIndex);
//            }
//            else {
////                sb.deleteCharAt(idIndex);
////                sb.deleteCharAt(idIndex - 1);
//            }
            
//            if (idIndex == 0 && sb.length() > 1) {
//                sb.deleteCharAt(idIndex + 1);
//                sb.deleteCharAt(idIndex);
//            }
//            else {
//                sb.deleteCharAt(idIndex);
//                
//                if (sb.length() > 0) {
//                    sb.deleteCharAt(idIndex - 1);
//                }
//            }
//            String cartCookiesIds = sb.toString();
            
            context.getExternalContext().addResponseCookie(cookieName, cartCookiesIds, null);
        }
    }
    
    public float calculateTotal() {
        float totalAmount = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            totalAmount += shoppingCart.get(i).getPrice();
        }
        return totalAmount;
    }
}
