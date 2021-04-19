/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.LoginController;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.validator.ValidatorException;
/**
 * Responsible for registering and validating a client creates them and stores them in the database logs them in automatically
 * @author Alessandro Dare
 * @version 1.0
 */
@Named
@SessionScoped
public class RegistrationController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);
    @Inject
    private ClientJpaController clientJpaController;
    @Inject
    private LoginController loginController;
    @PersistenceContext
    private EntityManager entityManager;
    private Client registrationBean;
    private String fristPassword;
    private String confrimPassword;
    private boolean userRegistered;
    private String lastPageRegister;
   
    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$", Pattern.CASE_INSENSITIVE);
    /**
     * Default controller
     */
    public RegistrationController() {

    }

    public String getLastPageRegister() {
        return lastPageRegister;
    }

    public String getFristPassword() {
        return fristPassword;
    }

    public void setFristPassword(String fristPassword) {
        this.fristPassword = fristPassword;
    }

    public String getConfrimPassword() {
        return confrimPassword;
    }

    public boolean isUserRegistered() {
        return userRegistered;
    }

    public void setConfrimPassword(String confrimPassword) {
        this.confrimPassword = confrimPassword;
    }

    public Client getRegistrationBean() {
        return registrationBean;
    }

    public void setRegistrationBean(Client registrationBean) {
        this.registrationBean = registrationBean;
    }
    /**
     * takes user name information from the registration information form the registration 
     * page and uses to to create a user and logs the in automatically
     * @author Alessandro Dare
     * @return previous visted page
     * @throws RollbackFailureException 
     */
    public String addNewUser() throws RollbackFailureException {
        registrationBean.setPassword(fristPassword);
        registrationBean.setClientactive(true);
        registrationBean.setIsmanager(false);
        clientJpaController.create(registrationBean);
        loginController.getLoginBean().setLoggedIn(true);
        loginController.getLoginBean().setId(clientJpaController.findUser(registrationBean.getUsername(), registrationBean.getPassword()).getClientid());
        loginController.getLoginBean().setPassword(registrationBean.getPassword());
        loginController.getLoginBean().setUsername(registrationBean.getUsername());
        loginController.getLoginBean().setEmailAddress(registrationBean.getEmail());
        return lastPageRegister;
    }
    /**
     * takes the user to the registration page and clears all previous values
     * @author Alessandro Dare
     * @return the register page
     */
    public String registerUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        lastPageRegister = context.getViewRoot().getViewId();
        lastPageRegister = lastPageRegister.substring(1, lastPageRegister.length() - 6);
        if (lastPageRegister.equals("register") || lastPageRegister.equals("login")) {
            lastPageRegister = loginController.getLoginLastPage();
        }
        registrationBean = new Client();
        fristPassword = "";
        confrimPassword = "";
        userRegistered = false;
        return "register";
    }

 
    /**
     * validates to make sure the confirm password matches the original password
     * @author Alessandro Dare
     * @param context
     * @param component
     * @param value 
     */
    public void validatePasswordError(FacesContext context, UIComponent component,
            Object value) {
        String confrimPassword = value.toString();
        if (!fristPassword.equals(confrimPassword)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "notMatchPasswordError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }
    /**
     * validates the second address to make sure it's in the correct format and isn't copying address 1
     * @author Alessandro Dare
     * @param context FacesContext
     * @param component UIComponent
     * @param value  Object
     */
    public void validateAddress(FacesContext context, UIComponent component,
            Object value) {
        String address1 = registrationBean.getAddress1();
        String address2 = value.toString();
        if (address2 != null && (!address2.equals(""))) {
            if (address1.equals(address2)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "addressError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
        }
    }
    /**
     * validates to make sure cell phone follows correct format and isn't related to the home page
     * @author Alessandro Dare
     * @param context FacesContext
     * @param component UIComponent
     * @param value  Object
     */

    public void validateCellPhoneNumber(FacesContext context, UIComponent component,
            Object value) {
        String cellPhoneNumber = value.toString();
        String homePhoneNumber = "" + registrationBean.getHometelephone();
        Matcher matcher = VALID_PHONE_NUMBER_PATTERN.matcher(cellPhoneNumber);
        if (cellPhoneNumber!=null &&(!cellPhoneNumber.isEmpty())) {
            if (!matcher.find()) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "phoneNumberFormatError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            if (cellPhoneNumber != null && (!cellPhoneNumber.equals("")) && homePhoneNumber.equals(cellPhoneNumber)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "phoneNumberIdenticalError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
        }
    }
}
