/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.business;

import com.gb4w21.musicalmoose.controller.ShoppingCartController;
import com.gb4w21.musicalmoose.util.LocaleChanger;
import java.io.Serializable;
import java.util.HashMap;
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
 *
 * @author victor and daniel
 */
@Named
@SessionScoped
public class PreRenderViewBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(PreRenderViewBean.class);

    @Inject
    private ShoppingCartController shoppingCartController;

    @Inject
    private LocaleChanger localeChanger;

    private boolean checkedCookies = false;

    /**
     * Check for genre cookie
     */
    public void checkGenreTrackingCookie() {
        if (!checkedCookies) {
            this.addAlbumCookiesToShoppingCart();
            this.addTrackCookiesToShoppingCart();

            this.checkedCookies = true;
        }
        this.shoppingCartController.toShoppingCart();
    }

    /**
     * Writing a cookie
     */
    public void writeGenreTrackingCookie(String genre) {
        if (genre != null) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("maxAge", 60 * 60 * 24 * 365 * 10);

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().addResponseCookie("GenreTracking", genre, properties);
        }
    }

    public boolean hasGenreCookie() {
        FacesContext context = FacesContext.getCurrentInstance();
        Object genreCookie = context.getExternalContext().getRequestCookieMap().get("GenreTracking");

        if (genreCookie == null) {
            return false;
        } else {
            return true;
        }
    }

    public String findRecentGenreCookie() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Retrieve a GenreTracking cookie
        Object genreTrackingCookie = context.getExternalContext().getRequestCookieMap().get("GenreTracking");
        if (genreTrackingCookie == null || ((Cookie) genreTrackingCookie).getValue().isEmpty()) {
            return null;
        }
        return ((Cookie) genreTrackingCookie).getValue();
    }

    private void addAlbumCookiesToShoppingCart() {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartAlbumCookie = context.getExternalContext().getRequestCookieMap().get("cart_album");
        if (cartAlbumCookie != null && !((Cookie) cartAlbumCookie).getValue().isEmpty()) {
            String albumIdsString = ((Cookie) cartAlbumCookie).getValue();
            String[] albumIds = albumIdsString.split(",");

            for (int i = 0; i < albumIds.length; i++) {
                shoppingCartController.findAlbumById(Integer.parseInt(albumIds[i]));
            }
        }
    }

    private void addTrackCookiesToShoppingCart() {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartTrackCookie = context.getExternalContext().getRequestCookieMap().get("cart_track");
        if (cartTrackCookie != null && !((Cookie) cartTrackCookie).getValue().isEmpty()) {
            String trackIdsString = ((Cookie) cartTrackCookie).getValue();
            String[] trackIds = trackIdsString.split(",");

            for (int i = 0; i < trackIds.length; i++) {
                shoppingCartController.findMusicTrackById(Integer.parseInt(trackIds[i]));
            }
        }
    }

    public void writeCartCookie(int id, String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartCookie = context.getExternalContext().getRequestCookieMap().get(cookieName);

        Map<String, Object> properties = new HashMap<>();
        properties.put("maxAge", 60 * 60 * 24 * 365 * 10);

        if (cartCookie == null || ((Cookie) cartCookie).getValue().isEmpty()) {
            context.getExternalContext().addResponseCookie(cookieName, Integer.toString(id), properties);
        } else {
            String cartCookiesIds = ((Cookie) cartCookie).getValue() + "," + Integer.toString(id);
            context.getExternalContext().addResponseCookie(cookieName, cartCookiesIds, properties);
        }
    }

    public void removeCartCookie(int id, String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        Object cartCookie = context.getExternalContext().getRequestCookieMap().get(cookieName);

        if (cartCookie != null) {
            String cartCookiesIds = ((Cookie) cartCookie).getValue();
            String idString = Integer.toString(id);
            int idIndex = cartCookiesIds.indexOf(idString);

            if (idIndex == 0 && cartCookiesIds.length() > idString.length()) {
                cartCookiesIds = cartCookiesIds.replace(idString + ",", "");
            } else if (idIndex == 0) {
                cartCookiesIds = cartCookiesIds.replace(idString, "");
            } else {
                cartCookiesIds = cartCookiesIds.replace("," + idString, "");
            }
            context.getExternalContext().addResponseCookie(cookieName, cartCookiesIds, null);
        }
    }

    public void removeCookie(String cookieName) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().addResponseCookie(cookieName, "", null);
    }

    /**
     * Checks for an existing cookie which contains the last selected locale
     * from the last session.
     *
     * return the string representing the language code
     */
    public String checkLocaleCookie() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Retrieve a specific cookie
        Cookie localeCookie = (Cookie) context.getExternalContext().getRequestCookieMap().get("LocaleCookie");
        if (localeCookie != null) {
            LOG.info(((Cookie) localeCookie).getName());
            LOG.info(((Cookie) localeCookie).getValue());

            return localeCookie.getValue();

        }
        return null;
    }

    /**
     * Whenever a user switches language, create a new cookie with its value set
     * as the language code (en, fr)
     *
     * @param languageCode
     */
    public void writeLocaleCookie(String languageCode) {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, Object> properties = new HashMap();
        properties.put("maxAge", 60 * 60 * 24 * 365 * 10);

        //Create a new cookie with the new value and give it a very long time to live
        context.getExternalContext().addResponseCookie("LocaleCookie", languageCode, properties);
    }
}
