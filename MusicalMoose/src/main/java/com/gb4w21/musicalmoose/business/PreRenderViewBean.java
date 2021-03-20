/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author victo
 */
@Named
@RequestScoped
public class PreRenderViewBean {

//    @Inject
//    LoginBean loginBean;
    private final static Logger LOG = LoggerFactory.getLogger(PreRenderViewBean.class);

    /**
     * Look for a cookie
     */
    public void checkCookies() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Retrieve a specific cookie
        Object my_cookie = context.getExternalContext().getRequestCookieMap().get("GenreTracking");
        if (my_cookie != null) {
            LOG.info(((Cookie) my_cookie).getName());
            LOG.info(((Cookie) my_cookie).getValue());
        }
    }

    /**
     * Writing a cookie
     */
    public void writeCookie() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().addResponseCookie("GenreTracking", "RAP", null);
    }

    /**
     * Checks for an existing cookie which contains the last selected locale
     * from the last session and calls a helper method to set the locale.
     */
    public void checkLocaleCookie() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Retrieve a specific cookie
        Object localeCookie = context.getExternalContext().getRequestCookieMap().get("LocaleCookie");
        if (localeCookie != null) {
            LOG.info(((Cookie) localeCookie).getName());
            LOG.info(((Cookie) localeCookie).getValue());

            setLocale((Cookie) localeCookie);
        }
    }

    /**
     * Whenever a user switches language, create a new cookie with its value set
     * as the language code (en_CA, fr_CA)
     *
     * @param languageCode
     */
    public void writeLocaleCookie(String languageCode) {
        FacesContext context = FacesContext.getCurrentInstance();

        //First delete the original cookie
//        Cookie localeCookie = (Cookie) context.getExternalContext().getRequestCookieMap().get("LocaleCookie");
//        if (localeCookie != null) {
//            localeCookie.setMaxAge(0);
//        }

        Map<String, Object> properties = new HashMap();
        properties.put("maxAge", 60 * 60 * 24 * 365 * 10);

        //Create a new cookie with the new value and give it a very long time to live
        context.getExternalContext().addResponseCookie("LocaleCookie", languageCode, properties);

    }

    /**
     * Given a cookie with a value, set the locale with that value.
     *
     * @param cookie
     */
    private void setLocale(Cookie cookie) {
        FacesContext context = FacesContext.getCurrentInstance();
        Locale aLocale;

        String languageCode = cookie.getValue();

        switch (languageCode) {
            case "en_CA":
                aLocale = Locale.CANADA;
                break;
            case "fr_CA":
                aLocale = Locale.CANADA_FRENCH;
                break;
            default:
                aLocale = Locale.getDefault();
        }
        context.getViewRoot().setLocale(aLocale);
    }

}
