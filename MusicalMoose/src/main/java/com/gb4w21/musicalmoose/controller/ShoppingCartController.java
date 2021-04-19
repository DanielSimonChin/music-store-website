package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.beans.MusicItem;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets the shopping cart functionality such as adding to cart, clearing, etc.
 *
 * @author Victor
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

    public List<MusicItem> getShoppingCartList() {
        LOG.info("Find shopping cart list " + shoppingCart.size());
        return this.shoppingCart;
    }

    public void setShoppingCart(List<MusicItem> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public String getPrevPage() {
        return this.prevPage;
    }

    public void setPrevPage(String prevPage) {
        this.prevPage = prevPage;
    }

    public BigDecimal getTotalCost() {
        return this.totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Adds the given id Album to shopping cart list
     *
     * @param id
     */
    public void findAlbumById(int id) {
        try {
            Album album = this.albumJpaController.findAlbumById(id);
            shoppingCart.add(convertAlbumToMusicItem(album));
        } catch (NonexistentEntityException e) {
            java.util.logging.Logger.getLogger(ShoppingCartController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Adds the given id Track to shopping cart list
     *
     * @param id
     */
    public void findMusicTrackById(int id) {
        try {
            MusicTrack musicTrack = this.musicTrackJpaController.findTrackById(id);
            shoppingCart.add(convertMusicTrackToMusicItem(musicTrack));
        } catch (NonexistentEntityException e) {
            java.util.logging.Logger.getLogger(ShoppingCartController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Converts an Album object to a MusicItem bean
     *
     * @param album
     * @return musicitem bean
     */
    public MusicItem convertAlbumToMusicItem(Album album) {
        MusicItem musicItem = new MusicItem();
        musicItem.setId(album.getAlbumid());
        musicItem.setTitle(album.getAlbumtitle());
        musicItem.setArtist(album.getArtist());
        if (album.getSaleprice() == 0) {
            musicItem.setPrice(album.getListprice());
        } else {
            musicItem.setPrice(album.getSaleprice());
        }
        musicItem.setIsAlbum(true);
        musicItem.setImgNameBig(album.getAlbumimagefilenamebig());
        musicItem.setGenre(albumJpaController.findGenreAlbumId(album.getAlbumid()));
        musicItem.setNumberOfTracks(album.getNumberoftracks());
        
        LOG.info("Converted music item: " + musicItem.getId());
        return musicItem;
    }

    /**
     * Converts an Music Track object to a MusicItem bean
     *
     * @param album
     * @return musicitem bean
     */
    public MusicItem convertMusicTrackToMusicItem(MusicTrack musicTrack) {
        MusicItem musicItem = new MusicItem();
        musicItem.setId(musicTrack.getInventoryid());
        musicItem.setTitle(musicTrack.getTracktitle());
        musicItem.setArtist(musicTrack.getArtist());
        if (musicTrack.getSaleprice() == 0) {
            musicItem.setPrice(musicTrack.getListprice());
        } else {
            musicItem.setPrice(musicTrack.getSaleprice());
        }
        musicItem.setIsAlbum(false);
        musicItem.setImgNameBig(musicTrack.getAlbumimagefilenamebig());
        musicItem.setGenre(musicTrack.getMusiccategory());
        musicItem.setSongLength(musicTrack.getPlaylength());
        LOG.info("Converted music item: " + musicItem.getId());
        
        return musicItem;
    }

    /**
     * Add given album to shopping cart
     *
     * @param addedAlbum
     */
    public void addShoppingCartAlbum(Album addedAlbum) {
        MusicItem musicItem = convertAlbumToMusicItem(addedAlbum);
        shoppingCart.add(musicItem);

        preRenderViewBean.writeCartCookie(addedAlbum.getAlbumid(), "cart_album");

        LOG.info("Shopping Cart Album Added: " + addedAlbum.getAlbumtitle());

        FacesContext.getCurrentInstance().addMessage(null, createMsg("success", "albumAdded"));
    }

    /**
     * Add given musicTrack to shopping cart
     *
     * @param addedAlbum
     */
    public void addShoppingCartTrack(MusicTrack addedTrack) {
        MusicItem musicItem = convertMusicTrackToMusicItem(addedTrack);
        shoppingCart.add(musicItem);

        preRenderViewBean.writeCartCookie(addedTrack.getInventoryid(), "cart_track");

        LOG.info("Shopping Cart Track Added: " + addedTrack.getTracktitle());
        LOG.info("Cart size: " + shoppingCart.size());
        
        FacesContext.getCurrentInstance().addMessage(null, createMsg("success", "trackAdded"));
    }

    /**
     * Creates a message to notify user whenever they add item to cart
     *
     * @param summary
     * @param detail
     * @return FacesMessage message
     */
    private FacesMessage createMsg(String summary, String detail) {
        FacesMessage facesMsgDets = com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", summary, null);
        FacesMessage facesMsgSummary = com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", detail, null);
        facesMsgDets.setDetail(facesMsgSummary.getSummary());
        return facesMsgDets;
    }

    /**
     * Adds message to FacesContext
     *
     * @param severity
     * @param summary
     * @param detail
     */
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }

    /**
     * Delete a given MusicItem from shopping cart list
     *
     * @param deleteItem
     * @return string of removed item
     */
    public String deleteCartItem(MusicItem deleteItem) {
        shoppingCart.remove(deleteItem);
        if (deleteItem.getIsAlbum()) {
            preRenderViewBean.removeCartCookie(deleteItem.getId(), "cart_album");
        } else {
            preRenderViewBean.removeCartCookie(deleteItem.getId(), "cart_track");
        }
        LOG.info("Shopping cart deleted");
        return null;
    }

    /**
     * Calculates the total without tax to present to user in shopping cart
     *
     * @return BigDecimal total
     */
    public BigDecimal calculateTotal() {
        float totalAmount = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            totalAmount += shoppingCart.get(i).getPrice();
        }
        totalCost = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
        LOG.info("Total cost: " + totalCost);
        return totalCost;
    }

    /**
     * Redirects user to shopping cart
     *
     * @return string nav to cart
     */
    public String toShoppingCart() {
        FacesContext context = FacesContext.getCurrentInstance();
        String tempPrevPage = context.getViewRoot().getViewId();
        tempPrevPage = tempPrevPage.substring(1, tempPrevPage.length() - 6);

        if (!tempPrevPage.equals("cart")) {
            this.prevPage = tempPrevPage;
        }
        LOG.info("To shopping cart page");
        return "shoppingcartpage";
    }

    /**
     * Redirects user to the page they were at before going to shopping cart
     *
     * @return string to nav to previous page 
     */
    public String backPage() {
        return prevPage;
    }

    /**
     * Converts Float to BigDecimal
     *
     * @param price
     * @return BigDecimal converted from Float
     */
    public BigDecimal floatToBigDec(Float price) {
        return new BigDecimal(Float.toString(price))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Checks if cart is empty
     *
     * @return true if empty, false otherwise
     */
    public boolean checkCartEmpty() {
        return this.shoppingCart.isEmpty();
    }

    /**
     * Clears the cart and cookies
     */
    public void clearCart() {
        shoppingCart.clear();
        preRenderViewBean.removeCookie("cart_album");
        preRenderViewBean.removeCookie("cart_track");
        LOG.info("Cart cleared");
    }
}
