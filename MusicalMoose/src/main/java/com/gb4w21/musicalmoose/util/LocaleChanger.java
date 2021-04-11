package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import java.util.Locale;
import java.util.Map;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that allows us the change locale whenever the language graphic images
 * are clicked
 *
 * @author Daniel
 */
@Named
@RequestScoped
public class LocaleChanger {

    private final static Logger LOG = LoggerFactory.getLogger(LocaleChanger.class);

    /**
     * Retrieve the language code from the params and change the local
     * accordingly
     *
     * @return null to stay on the current page
     */
    public String changeLocale() {
        LOG.info("IN LOCALE CHANGER");
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String languageCode = params.get("languageCode");

        Locale aLocale;
        PreRenderViewBean preRenderViewBean = new PreRenderViewBean();

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
        //Set a cookie so the site remembers the selected languagew
        preRenderViewBean.writeLocaleCookie(languageCode);
        context.getViewRoot().setLocale(aLocale);
        LOG.info("LOCALE CHANGED");
        return null;
    }
}
