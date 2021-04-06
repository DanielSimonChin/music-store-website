package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.beans.ShoppingCartItem;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.BanneradJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author victoAlberta
 */
@Named
@SessionScoped
public class ShoppingCartController implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(ShoppingCartController.class);
    
    @Inject
    private AlbumJpaController albumJpaController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;
    @Inject
    private PreRenderViewBean preRenderViewBean;
    
    private List<ShoppingCartItem> shoppingCart;
    private String prevPage;
    
    public ShoppingCartController() {
        this.shoppingCart = new ArrayList<ShoppingCartItem>();
    }
    
    public void findAlbumById(int id) {
        try {
            Album album = this.albumJpaController.findAlbumById(id);
            shoppingCart.add(convertAlbumToShoppingCartItem(album));
        }
        catch (NonexistentEntityException e) {
            LOG.error("Album not found in database with id: " + id);
        }
    }
    
    public void findMusicTrackById(int id) {
        try {
            MusicTrack musicTrack = this.musicTrackJpaController.findTrackById(id);
            shoppingCart.add(convertMusicTrackToShoppingCartItem(musicTrack));
        }
        catch (NonexistentEntityException e) {
            LOG.error("MusicTrack not found in database with id: " + id);
        }
    }
    
    private ShoppingCartItem convertAlbumToShoppingCartItem(Album album) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setId(album.getAlbumid());
        shoppingCartItem.setTitle(album.getAlbumtitle());
        shoppingCartItem.setArtist(album.getArtist());
        shoppingCartItem.setPrice(album.getCostprice());
        shoppingCartItem.setIsAlbum(true);
        shoppingCartItem.setImgNameSmall(album.getAlbumimagefilenamesmall());
        
        return shoppingCartItem;
    }
    
    private ShoppingCartItem convertMusicTrackToShoppingCartItem(MusicTrack musicTrack) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setId(musicTrack.getInventoryid());
        shoppingCartItem.setTitle(musicTrack.getTracktitle());
        shoppingCartItem.setArtist(musicTrack.getArtist());
        shoppingCartItem.setPrice(musicTrack.getCostprice());
        shoppingCartItem.setIsAlbum(false);
        shoppingCartItem.setImgNameSmall(musicTrack.getAlbumimagefilenamesmall());
        
        return shoppingCartItem;
    }
    
    public void addShoppingCartAlbum(Album addedAlbum) {
        ShoppingCartItem shoppingCartItem = convertAlbumToShoppingCartItem(addedAlbum);
        shoppingCart.add(shoppingCartItem);
        
        preRenderViewBean.writeCartCookie(addedAlbum.getAlbumid(), "cart_album");
                
        LOG.info("Shopping Cart Album Added: " + addedAlbum.getAlbumtitle());
    }
    
    public void addShoppingCartTrack(MusicTrack addedTrack) {
        ShoppingCartItem shoppingCartItem = convertMusicTrackToShoppingCartItem(addedTrack);
        shoppingCart.add(shoppingCartItem);
        
        preRenderViewBean.writeCartCookie(addedTrack.getInventoryid(), "cart_track");
        
        LOG.info("Shopping Cart Track Added: " + addedTrack.getTracktitle());
    }
    
    public List<ShoppingCartItem> getShoppingCartList() {
        LOG.info("Find shopping cart list");
        return this.shoppingCart;
    }
    
    public String deleteCartItem(ShoppingCartItem deleteItem) {
        shoppingCart.remove(deleteItem);
        if (deleteItem.getIsAlbum()) {
            preRenderViewBean.removeCartCookie(deleteItem.getId(), "cart_album");
        }
        else {
            preRenderViewBean.removeCartCookie(deleteItem.getId(), "cart_track");
        }
        return null;
    }
    
    public BigDecimal calculateTotal() {
        float totalAmount = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            totalAmount += shoppingCart.get(i).getPrice();
        }
        return new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
    }
    
    public String toShoppingCart() {
        FacesContext context = FacesContext.getCurrentInstance();
        String tempPrevPage = context.getViewRoot().getViewId();
        tempPrevPage = tempPrevPage.substring(1, tempPrevPage.length() - 6);
        
        if (!tempPrevPage.equals("cart")) {
            this.prevPage = tempPrevPage;
        }
        return "shoppingcartpage";
    }
    
    public String backPage() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().addResponseCookie("BACKPAGE", prevPage, null);
        return prevPage;
    }
    
    public boolean checkCartEmpty() {
        return this.shoppingCart.isEmpty();
    }
    
    public void clearCart() {
        shoppingCart.clear();
        preRenderViewBean.removeCookie("cart_album");
        preRenderViewBean.removeCookie("cart_track");
    }
}
