package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.beans.MusicItem;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
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
    
    private List<MusicItem> shoppingCart;
    private String prevPage;
    private BigDecimal totalCost;
    
    public ShoppingCartController() {
        this.shoppingCart = new ArrayList<MusicItem>();
    }
    
    public BigDecimal getTotalCost() {
        return this.totalCost;
    }
    
    public void findAlbumById(int id) {
        try {
            Album album = this.albumJpaController.findAlbumById(id);
            shoppingCart.add(convertAlbumToMusicItem(album));
        }
        catch (NonexistentEntityException e) {
            java.util.logging.Logger.getLogger(ShoppingCartController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void findMusicTrackById(int id) {
        try {
            MusicTrack musicTrack = this.musicTrackJpaController.findTrackById(id);
            shoppingCart.add(convertMusicTrackToMusicItem(musicTrack));
        }
        catch (NonexistentEntityException e) {
            java.util.logging.Logger.getLogger(ShoppingCartController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public MusicItem convertAlbumToMusicItem(Album album) {
        MusicItem musicItem = new MusicItem();
        musicItem.setId(album.getAlbumid());
        musicItem.setTitle(album.getAlbumtitle());
        musicItem.setArtist(album.getArtist());
        if (album.getSaleprice() == 0) {
            musicItem.setPrice(album.getListprice());
        }
        else {
            musicItem.setPrice(album.getSaleprice());
        }
        musicItem.setIsAlbum(true);
        musicItem.setImgNameBig(album.getAlbumimagefilenamebig());
        musicItem.setGenre(albumJpaController.findGenreAlbumId(album.getAlbumid()));
        musicItem.setNumberOfTracks(album.getNumberoftracks());
        
        return musicItem;
    }
    
    public MusicItem convertMusicTrackToMusicItem(MusicTrack musicTrack) {
        MusicItem musicItem = new MusicItem();
        musicItem.setId(musicTrack.getInventoryid());
        musicItem.setTitle(musicTrack.getTracktitle());
        musicItem.setArtist(musicTrack.getArtist());
        if (musicTrack.getSaleprice() == 0) {
            musicItem.setPrice(musicTrack.getListprice());
        }
        else {
            musicItem.setPrice(musicTrack.getSaleprice());
        }
        musicItem.setIsAlbum(false);
        musicItem.setImgNameBig(musicTrack.getAlbumimagefilenamebig());
        musicItem.setGenre(musicTrack.getMusiccategory());
        musicItem.setSongLength(musicTrack.getPlaylength());
        
        return musicItem;
    }
    
    public void addShoppingCartAlbum(Album addedAlbum) {
        MusicItem musicItem = convertAlbumToMusicItem(addedAlbum);
        shoppingCart.add(musicItem);
        
        preRenderViewBean.writeCartCookie(addedAlbum.getAlbumid(), "cart_album");
                
        LOG.info("Shopping Cart Album Added: " + addedAlbum.getAlbumtitle());
    }
    
    public void addShoppingCartTrack(MusicTrack addedTrack) {
        MusicItem musicItem = convertMusicTrackToMusicItem(addedTrack);
        shoppingCart.add(musicItem);
        
        preRenderViewBean.writeCartCookie(addedTrack.getInventoryid(), "cart_track");
        
        LOG.info("Shopping Cart Track Added: " + addedTrack.getTracktitle());
    }
    
    public List<MusicItem> getShoppingCartList() {
        LOG.info("Find shopping cart list");
        return this.shoppingCart;
    }
    
    public String deleteCartItem(MusicItem deleteItem) {
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
        totalCost = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
        return totalCost;
//        return new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
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
