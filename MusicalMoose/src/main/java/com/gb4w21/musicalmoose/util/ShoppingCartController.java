package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.beans.ShoppingCartItem;
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
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    
    private List<ShoppingCartItem> shoppingCart;;
    private boolean checkedCookies;
    private String prevPage;
    
    
    public ShoppingCartController() {
        this.shoppingCart = new ArrayList<ShoppingCartItem>();
        this.checkedCookies = false;
    }
    
    public void checkCookies() {
        if (!checkedCookies) {
            addAlbumCookiesToShoppingCart();
            addTrackCookiesToShoppingCart();
            
            checkedCookies = true;
        }
        this.toShoppingCart();
    }
    
    private void addAlbumCookiesToShoppingCart() {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartAlbumCookie = context.getExternalContext().getRequestCookieMap().get("cart_album");
        if (cartAlbumCookie != null && !((Cookie)cartAlbumCookie).getValue().isEmpty()) {
            String albumIdsString = ((Cookie)cartAlbumCookie).getValue();
            String[] albumIds = albumIdsString.split(",");
            
            for (int i = 0; i < albumIds.length; i++) {
                this.findAlbumById(Integer.parseInt(albumIds[i]));
            }
        }
    }
    
    private void addTrackCookiesToShoppingCart() {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartTrackCookie = context.getExternalContext().getRequestCookieMap().get("cart_track");
        if (cartTrackCookie != null && !((Cookie)cartTrackCookie).getValue().isEmpty()) {
            String trackIdsString = ((Cookie)cartTrackCookie).getValue();
            String[] trackIds = trackIdsString.split(",");
            
            for (int i = 0; i < trackIds.length; i++) {
                findMusicTrackById(Integer.parseInt(trackIds[i]));
            }
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
    
    private void findAlbumById(int id) {
        try {
            Album album = this.albumJpaController.findAlbumById(id);
            shoppingCart.add(convertAlbumToShoppingCartItem(album));
        }
        catch (NonexistentEntityException e) {
            LOG.error("Album not found in database with id: " + id);
        }
    }
    private void findMusicTrackById(int id) {
        try {
            MusicTrack musicTrack = this.musicTrackJpaController.findTrackById(id);
            shoppingCart.add(convertMusicTrackToShoppingCartItem(musicTrack));
        }
        catch (NonexistentEntityException e) {
            LOG.error("MusicTrack not found in database with id: " + id);
        }
    }
    
    public void addShoppingCartAlbum(Album addedAlbum) {
        ShoppingCartItem shoppingCartItem = convertAlbumToShoppingCartItem(addedAlbum);
        shoppingCart.add(shoppingCartItem);
        
        writeCartCookie(addedAlbum.getAlbumid(), "cart_album");
                
        LOG.info("Shopping Cart Album Added: " + addedAlbum.getAlbumtitle());
    }
    
    public void addShoppingCartTrack(MusicTrack addedTrack) {
        ShoppingCartItem shoppingCartItem = convertMusicTrackToShoppingCartItem(addedTrack);
        shoppingCart.add(shoppingCartItem);
        
        writeCartCookie(addedTrack.getInventoryid(), "cart_track");
        
        LOG.info("Shopping Cart Track Added: " + addedTrack.getTracktitle());
    }
    
    private void writeCartCookie(int id, String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartCookie = context.getExternalContext().getRequestCookieMap().get(cookieName);
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("maxAge", 31536000);
        
        if (cartCookie == null || ((Cookie)cartCookie).getValue().isEmpty()) {
            context.getExternalContext().addResponseCookie(cookieName, Integer.toString(id), properties);
        }
        else {
            String cartCookiesIds = ((Cookie)cartCookie).getValue() + "," + Integer.toString(id);
            context.getExternalContext().addResponseCookie(cookieName, cartCookiesIds, properties);
        }
    }
    
    public List<ShoppingCartItem> getShoppingCartList() {
        LOG.info("Find shopping cart list");
        return this.shoppingCart;
    }
    
    public String deleteCartItem(ShoppingCartItem deleteItem) {
        shoppingCart.remove(deleteItem);
        if (deleteItem.getIsAlbum()) {
            removeCartCookie(deleteItem.getId(), "cart_album");
        }
        else {
            removeCartCookie(deleteItem.getId(), "cart_track");
        }
        return null;
    }
    
    private void removeCartCookie(int id, String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartCookie = context.getExternalContext().getRequestCookieMap().get(cookieName);
        
        if (cartCookie != null) {
            String cartCookiesIds = ((Cookie)cartCookie).getValue();
            String idString = Integer.toString(id);
            int idIndex = cartCookiesIds.indexOf(idString);
            
            if (idIndex == 0 && cartCookiesIds.length() > idString.length()) {
                cartCookiesIds = cartCookiesIds.replace(idString + ",", "");
            }
            else if (idIndex == 0) {
                cartCookiesIds = cartCookiesIds.replace(idString, "");
            }
            else {
                cartCookiesIds = cartCookiesIds.replace("," + idString, "");
            }
            context.getExternalContext().addResponseCookie(cookieName, cartCookiesIds, null);
        }
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
        removeCookie("cart_album");
        removeCookie("cart_track");
    }
    
    private void removeCookie(String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().addResponseCookie(cookieName, "", null);
    }
}
