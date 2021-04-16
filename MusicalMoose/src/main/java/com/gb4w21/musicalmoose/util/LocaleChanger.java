package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that allows us the change locale whenever the user selects a language
 *
 * @author Daniel
 */
@Named
@SessionScoped
public class LocaleChanger implements Serializable {

    @Inject
    PreRenderViewBean renderBean;

    private final static Logger LOG = LoggerFactory.getLogger(LocaleChanger.class);

    private String localeCode;

    private static Map<String, Object> languages;

    //The two locales that are available to clients
    static {
        languages = new LinkedHashMap<String, Object>();
        languages.put("English", Locale.ENGLISH); //label, value
        languages.put("Français", Locale.FRENCH);
    }

    /**
     * We want the cookie's language code to be used before the page renders
     */
    @PostConstruct
    public void init() {
        localeCode = renderBean.checkLocaleCookie();
    }

    /**
     * @return the Map of countries
     */
    public Map<String, Object> getLanguages() {
        return languages;
    }

    /**
     * @return the localeCode variable
     */
    public String getLocaleCode() {
        return localeCode;
    }

    /**
     * Set the localCode
     *
     * @param localeCode
     */
    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    /**
     * When the value of the selectOneMenu is changed, set the locale to that
     * value.
     *
     * @param e
     */
    public void localCodeChanged(ValueChangeEvent e) {
        //Get the value of the selectedItem
        String newLocaleValue = e.getNewValue().toString();

        for (Map.Entry<String, Object> entry : languages.entrySet()) {

            if (entry.getValue().toString().equals(newLocaleValue)) {

                FacesContext.getCurrentInstance()
                        .getViewRoot().setLocale((Locale) entry.getValue());

                //Write a new cookie with the selected value.
                renderBean.writeLocaleCookie(newLocaleValue);
            }
        }
    }
}