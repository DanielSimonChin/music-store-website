/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.business;

import java.util.ArrayList;
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

}
