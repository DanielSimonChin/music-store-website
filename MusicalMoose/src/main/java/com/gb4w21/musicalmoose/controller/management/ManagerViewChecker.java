/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller.management;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.LoginController;
import com.gb4w21.musicalmoose.controller.RegistrationController;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.primefaces.component.calendar.Calendar;
import javax.faces.event.ComponentSystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.faces.application.ConfigurableNavigationHandler;

/**
 * Checks to make sure that manger's can't get to the client side by using the
 * url and vise versa
 *
 * @author Alessandro
 * @version 1.0
 */
@Named
@SessionScoped
public class ManagerViewChecker implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerViewChecker.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    RegistrationController registrationController;
    @Inject
    LoginController loginController;
    @Inject
    ClientJpaController clientJpaController;

    /**
     * Default constructor
     */
    public ManagerViewChecker() {

    }

    /**
     * checks to make sure that only a manager can go to manager pages by
     * accessing the url
     *
     * @author Alessandro Dare
     * @param event ComponentSystemEvent
     */
    public void checkManager(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();

        LOG.info("user " + loginController.getLoginBean().getUsername() + " is trying to go to a management page");
        Client client = clientJpaController.findUser(loginController.getLoginBean().getUsername(), loginController.getLoginBean().getPassword());
        if (client == null || (!client.getIsmanager()) || (!client.getClientactive())) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("authenticationerror");
        }
    }

    /**
     * checks to make sure that a manager can't get to the client side y
     * accessing the url
     *
     * @author Alessandro Dare
     * @param event ComponentSystemEvent
     */
    public void checkClient(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        loginController.getLoginBean();
        Client client = clientJpaController.findUser(loginController.getLoginBean().getUsername(), loginController.getLoginBean().getPassword());
        LOG.info("user " + loginController.getLoginBean().getUsername() + " is trying to go to a client page");
        if (client != null && client.getIsmanager() && client.getClientactive()) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("adminauthenticationerror");
        }
    }
}
