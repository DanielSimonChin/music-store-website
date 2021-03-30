package com.gb4w21.musicalmoose.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * This class supports custom converters and validators Its purpose if to return
 * the appropriate string from a specified bundle.
 *
 * From Core JavaServer Faces Third Edition by David Geary and Cay Horstmann
 *
 */
public class Messages {

    /**
     * Retrieve the message from the bundle. Locale aware
     *
     * @param bundleName The bundle file root name
     * @param resourceId The id value from the xhtml file
     * @param params Any params that must be inserted in message via {0}
     * @return The FacesMessage object contains both the summary and detail
     * messages
     */
    public static FacesMessage getMessage(String bundleName, String resourceId,
            Object[] params) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application app = context.getApplication();
        String appBundle = app.getMessageBundle();
        Locale locale = getLocale(context);
        ClassLoader loader = getClassLoader();
        String summary = getString(appBundle, bundleName, resourceId, locale,
                loader, params);
        if (summary == null) {
            summary = "???" + resourceId + "???";
        }
        String detail = getString(appBundle, bundleName,
                resourceId + "_detail", locale, loader, params);
        return new FacesMessage(summary, detail);
    }

    /**
     * Search for message in user defined bundle
     *
     * @param bundle
     * @param resourceId
     * @param params
     * @return
     */
    public static String getString(String bundle, String resourceId,
            Object[] params) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application app = context.getApplication();
        String appBundle = app.getMessageBundle();
        Locale locale = getLocale(context);
        ClassLoader loader = getClassLoader();
        return getString(appBundle, bundle, resourceId, locale, loader, params);
    }

    /**
     * Return the message from the user provided bundle or from the default JSF
     * bundle
     *
     * @param bundle1
     * @param bundle2
     * @param resourceId
     * @param locale
     * @param loader
     * @param params
     * @return
     */
    public static String getString(String bundle1, String bundle2,
            String resourceId, Locale locale, ClassLoader loader,
            Object[] params) {
        String resource = null;
        ResourceBundle bundle;

        if (bundle1 != null) {
            bundle = ResourceBundle.getBundle(bundle1, locale, loader);
            if (bundle != null) {
                try {
                    resource = bundle.getString(resourceId);
                } catch (MissingResourceException ex) {
                }
            }
        }

        if (resource == null) {
            bundle = ResourceBundle.getBundle(bundle2, locale, loader);
            if (bundle != null) {
                try {
                    resource = bundle.getString(resourceId);
                } catch (MissingResourceException ex) {
                }
            }
        }

        if (resource == null) {
            return null; // no match
        }
        if (params == null) {
            return resource;
        }

        MessageFormat formatter = new MessageFormat(resource, locale);
        return formatter.format(params);
    }

    /**
     * Retrieve the locale from the FacesContext
     *
     * @param context
     * @return
     */
    public static Locale getLocale(FacesContext context) {
        Locale locale = null;
        UIViewRoot viewRoot = context.getViewRoot();
        if (viewRoot != null) {
            locale = viewRoot.getLocale();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    /**
     * Returns a class loader from the current thread or from the system
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }
}
